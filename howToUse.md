### solr-dataimport-scheduler使用说明 ###
  * 将apache-solr-dataimportscheduler-1.0.jar, apache-solr-dataimporthandler-**.jar, apache-solr-dataimporthandler-extras-**.jar 放到solr.war的lib目录下面 *** 修改solr.war中WEB-INF/web.xml, 在servlet节点前面增加:**
```
       <listener>
  	<listener-class>
  		org.apache.solr.handler.dataimport.scheduler.ApplicationListener
  	</listener-class>
       </listener>
```
  * 将apache-solr-dataimportscheduler-**.jar 中 dataimport.properties 取出并根据实际情况修改,然后放到 solr.home/conf (不是solr.home/core/conf) 目录下面**
  * 重启tomcat或者jboss 即可 