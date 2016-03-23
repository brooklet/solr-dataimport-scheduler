package org.apache.solr.handler.dataimport.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		// get our timer from the context
		Timer timer = (Timer) servletContext.getAttribute("timer");
		Timer fullImportTimer = (Timer) servletContext.getAttribute("fullImportTimer");

		// cancel all active tasks in the timers queue
		if (timer != null)
			timer.cancel();
		if (fullImportTimer != null)
			fullImportTimer.cancel();

		// remove the timer from the context
		servletContext.removeAttribute("timer");
		servletContext.removeAttribute("fullImportTimer");

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		try {
			// 增量更新任务计划
			// create the timer and timer task objects
			Timer timer = new Timer();
			DeltaImportHTTPPostScheduler task = new DeltaImportHTTPPostScheduler(servletContext.getServletContextName(),
					timer);

			// get our interval from HTTPPostScheduler
			int interval = task.getIntervalInt();

			// get a calendar to set the start time (first run)
			Calendar calendar = Calendar.getInstance();

			int startDelaySeconds = task.getStartDelaySecondsInt();

			logger.warn(
					"Delta Import Schedule enabled. startDelaySeconds: " + startDelaySeconds + " interval:" + interval);

			// set the first run to now + interval (to avoid fireing while the
			// app/server is starting)
			// 最快不低于1秒
			calendar.add(Calendar.SECOND, (startDelaySeconds < 1 ? 1 : startDelaySeconds));
			Date startTime = calendar.getTime();

			// schedule the task
			// timer.scheduleAtFixedRate(task, startTime, 1000 * 60 * interval);
			// 单位改成秒, 张良 2014-02-20 16:51:44
			timer.scheduleAtFixedRate(task, startTime, 1000 * interval);

			// save the timer in context
			servletContext.setAttribute("timer", timer);

			// 重做索引任务计划
			Timer fullImportTimer = new Timer();
			FullImportHTTPPostScheduler fullImportTask = new FullImportHTTPPostScheduler(
					servletContext.getServletContextName(), fullImportTimer);

			int reBuildIndexInterval = fullImportTask.getReBuildIndexIntervalInt();
			if (reBuildIndexInterval <= 0) {
				logger.warn("Full Import Schedule disabled");
				return;
			}

			Calendar fullImportCalendar = Calendar.getInstance();
			Date beginDate = fullImportTask.getReBuildIndexBeginTime();
			fullImportCalendar.setTime(beginDate);
			fullImportCalendar.add(Calendar.SECOND, (reBuildIndexInterval < 10 ? 10 : reBuildIndexInterval));
			Date fullImportStartTime = fullImportCalendar.getTime();

			// schedule the task
			fullImportTimer.scheduleAtFixedRate(fullImportTask, fullImportStartTime, 1000 * reBuildIndexInterval);

			// save the timer in context
			servletContext.setAttribute("fullImportTimer", fullImportTimer);

			logger.warn("Full Import Schedule enabled. fullImportStartTime: " + fullImportStartTime
					+ " reBuildIndexInterval: " + reBuildIndexInterval);

		} catch (Exception e) {
			if (e.getMessage().endsWith("disabled")) {
				logger.warn("Schedule disabled");
			} else {
				logger.error("Problem initializing the scheduled task: ", e);
			}
		}

	}

}