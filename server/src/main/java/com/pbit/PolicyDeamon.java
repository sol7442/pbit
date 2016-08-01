package com.pbit;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.policy.PolicyServer;

public class PolicyDeamon {

	public static final String INSTANCE_NAME = "PS_INS";
	public static final String LOG4J_PATH    = "PS_LOG";
	
	private static Logger syslogger = null;
	public static void main(String[] args) {
		setLoggerConfigure(args[0]);
		
		loadServerConfigure();
		printStartHeder();
		PolicyServer server = new PolicyServer();
		try {
			server.open(5000);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadServerConfigure() {
		
	}
	private static void setLoggerConfigure(String log_config_path){
		if(log_config_path == null){
			log_config_path = System.getProperty("user.dir");
			log_config_path += "\\config\\log4j.xml";
		}
		System.out.println(log_config_path);
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY,log_config_path);
		syslogger = LoggerFactory.getLogger("system");
	}
	private static void printStartHeder() {
		System.out.println("======================================================");
		System.out.println("STAR_TIME : " + new Date());
		System.out.println("INS_NAME  : " + System.getProperty(INSTANCE_NAME));
		System.out.println("LOG_PATH  : " + System.getProperty(LOG4J_PATH));
		System.out.println("======================================================");
		
		if(syslogger !=null){
			syslogger.info("======================================================");
			syslogger.info("STAR_TIME : " + new Date());
			syslogger.info("INS_NAME  : " + System.getProperty(INSTANCE_NAME));
			syslogger.info("LOG_PATH  : " + System.getProperty(LOG4J_PATH));
			syslogger.info("======================================================");
		}
	}
}
