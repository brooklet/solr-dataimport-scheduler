### Solr Data Import Hander Scheduler 说明: ###

#### Solr官方提供了很强大的Data Import Request Handler，同时提供了一个简单的 Scheduler，Url：http://wiki.apache.org/solr/DataImportHandler ####
#### 示例中的 Scheduler 只支持增量更新，不支持定期重做索引，因此我做了一个简单的封装，增加了重做索引的定时器. ####
#### (原定时器作者是:Marko Bonaci, 在此表示感谢!)The original Scheduling source by Marko Bonaci, Thank him! ####

---


---

#### 使用说明 ####
  * 将 apache-solr-dataimportscheduler-1.0.jar 和solr自带的 apache-solr-dataimporthandler-**.jar, apache-solr-dataimporthandler-extras-**.jar 放到solr.war的lib目录下面 *** 修改solr.war中WEB-INF/web.xml, 在servlet节点前面增加:**
```
       <listener>
  	<listener-class>
  		org.apache.solr.handler.dataimport.scheduler.ApplicationListener
  	</listener-class>
       </listener>
```
  * 将apache-solr-dataimportscheduler-**.jar 中 dataimport.properties 取出并根据实际情况修改,然后放到 solr.home/conf (不是solr.home/core/conf) 目录下面**
  * 重启tomcat或者jboss 即可 


---


---

#### dataimport.properties 配置项说明 ####
```
#################################################
#                                               #
#       dataimport scheduler properties         #
#                                               #
#################################################

#  to sync or not to sync
#  1 - active; anything else - inactive
syncEnabled=1

#  which cores to schedule
#  in a multi-core environment you can decide which cores you want syncronized
#  leave empty or comment it out if using single-core deployment
syncCores=core1,core2

#  solr server name or IP address
#  [defaults to localhost if empty]
server=localhost

#  solr server port
#  [defaults to 80 if empty]
port=8080

#  application name/context
#  [defaults to current ServletContextListener's context (app) name]
webapp=solr

#  URL params [mandatory]
#  remainder of URL
params=/dataimport?command=delta-import&clean=false&commit=true

#  schedule interval
#  number of minutes between two runs
#  [defaults to 30 if empty]
interval=1

#  重做索引的时间间隔，单位分钟，默认7200，即5天; 
#  为空,为0,或者注释掉:表示永不重做索引
reBuildIndexInterval=7200

#  重做索引的参数
reBuildIndexParams=/dataimport?command=full-import&clean=true&commit=true

#  重做索引时间间隔的计时开始时间，第一次真正执行的时间=reBuildIndexBeginTime+reBuildIndexInterval*60*1000；
#  两种格式：2012-04-11 03:10:00 或者  03:10:00，后一种会自动补全日期部分为服务启动时的日期
reBuildIndexBeginTime=03:10:00


```