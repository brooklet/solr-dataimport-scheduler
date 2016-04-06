package org.apache.solr.handler.dataimport.scheduler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTimerTask extends TimerTask {
	protected String syncEnabled = "0";
	protected String[] syncCores;
	protected String server;
	protected String port;
	protected String webapp;
	protected String params;
	protected String interval;
	protected String cores;
	protected String startDelaySeconds;
	protected SolrDataImportProperties p;
	protected boolean singleCore;

	protected String reBuildIndexParams;
	protected String reBuildIndexBeginTime;
	protected String reBuildIndexInterval;

	protected static final Logger logger = LoggerFactory.getLogger(BaseTimerTask.class);

	public BaseTimerTask(String webAppName, Timer t) throws Exception {
		// load properties from global dataimport.properties
		p = new SolrDataImportProperties();
		reloadParams();
		fixParams(webAppName);

		if (syncEnabled == null || !syncEnabled.equals("1"))
			throw new Exception("Schedule disabled");

		if (syncCores == null || (syncCores.length == 1 && syncCores[0].isEmpty())) {
			singleCore = true;
			logger.info("<index update process> Single core identified in dataimport.properties");
		} else {
			singleCore = false;
			logger.info("<index update process> Multiple cores identified in dataimport.properties. Sync active for: "
					+ cores);
		}
	}

	protected void reloadParams() {
		p.loadProperties(true);
		syncEnabled = p.getProperty(SolrDataImportProperties.SYNC_ENABLED);

		cores = p.getProperty(SolrDataImportProperties.SYNC_CORES);
		server = p.getProperty(SolrDataImportProperties.SERVER);
		port = p.getProperty(SolrDataImportProperties.PORT);
		webapp = p.getProperty(SolrDataImportProperties.WEBAPP);
		params = p.getProperty(SolrDataImportProperties.PARAMS);
		interval = p.getProperty(SolrDataImportProperties.INTERVAL);
		startDelaySeconds = p.getProperty(SolrDataImportProperties.STARTDELAYSECONDS);
		syncCores = cores != null ? cores.split(",") : null;

		reBuildIndexParams = p.getProperty(SolrDataImportProperties.REBUILDINDEXPARAMS);
		reBuildIndexBeginTime = p.getProperty(SolrDataImportProperties.REBUILDINDEXBEGINTIME);
		reBuildIndexInterval = p.getProperty(SolrDataImportProperties.REBUILDINDEXINTERVAL);

	}

	protected void fixParams(String webAppName) {
		if (server == null || server.isEmpty())
			server = "localhost";
		if (port == null || port.isEmpty())
			port = "8080";
		if (webapp == null || webapp.isEmpty())
			webapp = webAppName;
		if (interval == null || interval.isEmpty() || getIntervalInt() <= 0)
			interval = "30";
		if (reBuildIndexBeginTime == null || reBuildIndexBeginTime.isEmpty())
			reBuildIndexBeginTime = "00:00:00";
		if (reBuildIndexInterval == null || reBuildIndexInterval.isEmpty() || getReBuildIndexIntervalInt() <= 0)
			reBuildIndexInterval = "0";
	}

	protected void prepUrlSendHttpPost(String params) {
		String coreUrl = "http://" + server + ":" + port + "/" + webapp + params;
		sendHttpPost(coreUrl, null);
	}

	protected void prepUrlSendHttpPost(String coreName, String params) {
		String coreUrl = "http://" + server + ":" + port + "/" + webapp + "/" + coreName + params;
		sendHttpPost(coreUrl, coreName);
	}

	protected void sendHttpPost(String completeUrl, String coreName) {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss SSS");
		Date startTime = new Date();

		// prepare the core var
		String core = coreName == null ? "" : "[" + coreName + "] ";

		logger.info(core + "<index update process> Process started at .............. " + df.format(startTime));

		try {

			URL url = new URL(completeUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("type", "submit");
			conn.setDoOutput(true);

			// Send HTTP POST
			conn.connect();

			logger.info(core + "<index update process> Full URL\t\t\t\t" + conn.getURL());
			logger.info(core + "<index update process> Response message\t\t\t" + conn.getResponseMessage());
			logger.info(core + "<index update process> Response code\t\t\t" + conn.getResponseCode());

			// listen for change in properties file if an error occurs
			if (conn.getResponseCode() != 200) {
				reloadParams();
			}

			conn.disconnect();
			logger.info(core + "<index update process> Disconnected from server\t\t" + server);
			Date endTime = new Date();
			logger.info(core + "<index update process> Process ended at ................ " + df.format(endTime));
		} catch (MalformedURLException mue) {
			logger.error("Failed to assemble URL for HTTP POST", mue);
		} catch (IOException ioe) {
			logger.error("Failed to connect to the specified URL while trying to send HTTP POST", ioe);
		} catch (Exception e) {
			logger.error("Failed to send HTTP POST", e);
		}
	}

	public int getIntervalInt() {
		try {
			return Integer.parseInt(interval);
		} catch (NumberFormatException e) {
			logger.warn("Unable to convert 'interval' to number. Using default value (30) instead", e);
			return 30; // return default in case of error
		}
	}

	public int getStartDelaySecondsInt() {
		try {
			return Integer.parseInt(startDelaySeconds);
		} catch (NumberFormatException e) {
			logger.warn("Unable to convert 'startDelaySeconds' to number. Using default value (60) instead", e);
			return 60; // return default in case of error
		}
	}

	public int getReBuildIndexIntervalInt() {
		try {
			return Integer.parseInt(reBuildIndexInterval);
		} catch (NumberFormatException e) {
			logger.info("Unable to convert 'reBuildIndexInterval' to number. do't rebuild index.", e);
			return 0; // return default in case of error
		}
	}

	public Date getReBuildIndexBeginTime() {
		Date beginDate = null;
		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = sdfDate.format(new Date());
			beginDate = sdfDate.parse(dateStr);
			if (reBuildIndexBeginTime == null || reBuildIndexBeginTime.isEmpty()) {
				return beginDate;
			}
			if (reBuildIndexBeginTime.matches("\\d{2}:\\d{2}:\\d{2}")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				beginDate = sdf.parse(dateStr + " " + reBuildIndexBeginTime);
			} else if (reBuildIndexBeginTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				beginDate = sdf.parse(reBuildIndexBeginTime);
			}
			return beginDate;
		} catch (ParseException e) {
			logger.warn("Unable to convert 'reBuildIndexBeginTime' to date. use now time.", e);
			return beginDate;
		}
	}

}
