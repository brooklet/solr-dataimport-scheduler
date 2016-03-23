package org.apache.solr.handler.dataimport.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.solr.core.SolrResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDataImportProperties {
	private Properties properties;

	public static final String SYNC_ENABLED = "syncEnabled";
	public static final String SYNC_CORES = "syncCores";
	public static final String SERVER = "server";
	public static final String PORT = "port";
	public static final String WEBAPP = "webapp";
	public static final String PARAMS = "params";
	public static final String INTERVAL = "interval";
	public static final String STARTDELAYSECONDS ="startDelaySeconds";

	public static final String REBUILDINDEXPARAMS = "reBuildIndexParams";
	public static final String REBUILDINDEXBEGINTIME = "reBuildIndexBeginTime";
	public static final String REBUILDINDEXINTERVAL = "reBuildIndexInterval";

	private static final Logger logger = LoggerFactory.getLogger(SolrDataImportProperties.class);

	public SolrDataImportProperties() {
		// loadProperties(true);
	}

	public void loadProperties(boolean force) {
		boolean res = false;
		try {
			SolrResourceLoader loader = new SolrResourceLoader();
			// logger.info("Instance dir = " + loader.getInstanceDir());
			logger.info("configDir dir = " + (loader.getConfigDir() == null ? "" : loader.getConfigDir()));
			logger.info("getDataDir dir = " + (loader.getDataDir() == null ? "" : loader.getDataDir()));
			logger.info("getInstancePath dir = " + (loader.getInstancePath() == null ? "" : loader.getInstancePath()));

			String instancePath = loader.getInstancePath().toString();
			logger.info("configDir dir = " + instancePath);

			instancePath = SolrResourceLoader.normalizeDir(instancePath);
			String dataImportPropertiesPath = instancePath + "dataimport.properties";
			logger.info("dataImportPropertiesPath = " + dataImportPropertiesPath);

			if ((new File(dataImportPropertiesPath)).exists()) {
				res = true;
			}
			if (force || properties == null) {
				properties = new Properties();
				if ((new File(dataImportPropertiesPath)).exists()) {
					FileInputStream fis = new FileInputStream(dataImportPropertiesPath);
					properties.load(fis);
					fis.close();
				}
			}
		} catch (FileNotFoundException fnfe) {
			logger.error("Error locating DataImportScheduler dataimport.properties file", fnfe);
		} catch (IOException ioe) {
			logger.error("Error reading DataImportScheduler dataimport.properties file", ioe);
		} catch (Exception e) {
			logger.error("Error loading DataImportScheduler properties", e);
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}