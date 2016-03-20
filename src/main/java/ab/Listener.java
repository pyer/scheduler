package ab;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;

import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
	
public class Listener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private static Scheduler scheduler = null;

    //This method is invoked when the Web Application
    //is ready to service requests
    public void contextInitialized(ServletContextEvent event)
    {
    	LOGGER.info("BEGIN");
    	try {
       		//Creating scheduler factory and scheduler
       		SchedulerFactory factory = new StdSchedulerFactory();
       		scheduler = factory.getScheduler();
       		scheduler.start();
		} catch (SchedulerException e) {
//			e.printStackTrace();
    		LOGGER.error("SchedulerException",e);
		}
   		// Load properties
   	    Properties p = loadProperties("listener.properties");
        // Launch jobs
    	initializeJobs(p);
    }
    
    public void contextDestroyed (ServletContextEvent sce) {
    	LOGGER.info("END");
    	try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
//			e.printStackTrace();
    		LOGGER.error("SchedulerException",e);
		}
    }

    private void initializeJobs(Properties p)
    {
   	    // assign the property names in a enumeration
   	    Enumeration<?> en = p.propertyNames();
   	  	while (en.hasMoreElements()){
   	 		String prop = en.nextElement().toString();
   	 		LOGGER.debug(prop);
   	 		String[] parts = prop.split("\\.");
   	 		String name = parts[0];
   	 		if( "status".equals(parts[1]) ) {
   	 			// property exists
   	 			String key = p.getProperty(prop,"off");
   	 			if("on".equals(key)) {
   	 				// define the job and tie it to the job class
   	 				String jobClass = p.getProperty(name+".job");
   	 				try {
   	   	 				LOGGER.info(name+" launches "+jobClass);
   	 					//Class<? extends Job> k = (Class<? extends Job>) Class.forName(jobClass);
   	   	 				@SuppressWarnings("unchecked")
   	 					JobDetail job = newJob((Class<? extends Job>) Class.forName(jobClass))
   	 							.withIdentity(jobClass) 
   	 							.build();

   	 					// search a cron expression
   	 					String value = p.getProperty(name+".expression","none");
   	 					if("none".equals(value)) {
   	 						// no expression, so it's a trigger
   	 						value = p.getProperty(name+".interval","1");
   	 						pullTrigger(name, job, value, p.getProperty(name+".unit","second"));
   	 					} else {
   	 						// it's a cron
   	 						cronTrigger(name, job, value);
   	 					}
   	 				} catch (ClassNotFoundException e) {
   	   	 				LOGGER.error("Unkonwn "+jobClass+" for "+parts[0],e);
  	 				}
   	 			} else {
   	 				LOGGER.info(parts[0]+" is disabled.");
   	 			}
   	 		}
   	 	}
    }
 
    private Properties loadProperties(String name) {
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream input = classLoader.getResourceAsStream(name);
    	Properties p = new Properties();
    	try {
    		p.load(input);
		} catch (IOException e) {
//			e.printStackTrace();
			LOGGER.error("Loading file '"+name+"' ", e);
		}
    	return p;
    }

    private void pullTrigger(String triggerName, JobDetail job, String value, String unit) {
    	LOGGER.info("Trigger "+triggerName+" is enabled every "+value+ " "+unit);
   		int interval = Integer.valueOf(value);
    	if(unit.isEmpty()) {
        	LOGGER.error("Trigger "+triggerName+" unit is undefined.");
    	} else {
    		try {
    			SimpleScheduleBuilder ssb = simpleSchedule();
    			if(unit.startsWith("second")) {
    				ssb = ssb.withIntervalInSeconds(interval);
    			} else {
    				if(unit.startsWith("minute")) {
    					ssb = ssb.withIntervalInMinutes(interval);
    				} else {
    					ssb = ssb.withIntervalInHours(interval);
    				}
    			}
    			SimpleTrigger trigger = newTrigger()
					.withIdentity(triggerName)
					.withSchedule(ssb.repeatForever())
					.build();
    			// Tell quartz to schedule the job using our trigger 
    			scheduler.scheduleJob(job, trigger);
    		} catch( SchedulerException e){
    			LOGGER.error("SchedulerException",e);
    		}
   		}
    }

    private void cronTrigger(String triggerName, JobDetail job, String expression) {
    	String msg = "Cron "+triggerName+" expression is ";
    	if(expression.isEmpty()) {
        	LOGGER.error(msg+"empty.");
    	} else {
    		try {
    			CronTrigger trigger = newTrigger()
    					.withIdentity(triggerName)
    				    .withSchedule(cronSchedule(expression))
    				    .build();
    			// Tell quartz to schedule the job using our trigger 
    			scheduler.scheduleJob(job, trigger);
   	        	LOGGER.info(msg+expression);    			
    		} catch( SchedulerException e){
    			LOGGER.error("SchedulerException",e);
    		}
    	}
    }
}
