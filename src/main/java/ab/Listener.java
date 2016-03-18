package ab;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.CronTrigger;
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
   		
   		// define the job and tie it to our Job1 class 
   		JobDetail job1 = newJob(Job1.class) 
        		.withIdentity("job1") 
        		.build();

   		// Pull triggers
   		pullTrigger("trigger1", job1, p);
   		pullTrigger("trigger2", job1, p);
   		pullTrigger("trigger3", job1, p);
 
   		JobDetail job2 = newJob(Job2.class) 
        		.withIdentity("job2") 
        		.build();

   		cronTrigger("cron1", job2, p);
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

    private void pullTrigger(String triggerName, JobDetail job, Properties prop) {
    	String status = prop.getProperty(triggerName + ".status","off");
    	String msg = "Trigger "+triggerName;
    	if(status.equals("on")) {
        	msg += " is enabled every ";
    		int seconds = Integer.valueOf(prop.getProperty(triggerName + ".intervalInSeconds","0"));
    		int minutes = Integer.valueOf(prop.getProperty(triggerName + ".intervalInMinutes","0"));
    		int hours   = Integer.valueOf(prop.getProperty(triggerName + ".intervalInHours","24"));
    		try {
    			SimpleScheduleBuilder ssb = simpleSchedule();
    			if(seconds>0) {
    	        	msg += seconds+" seconds";
    				ssb = ssb.withIntervalInSeconds(seconds);
    			} else {
    				if(minutes>0) {
        	        	msg += minutes+" minutes";
    					ssb = ssb.withIntervalInMinutes(minutes);
    				} else {
        	        	msg += hours+" hours";
    					ssb = ssb.withIntervalInHours(hours);
    				}
    			}
    			SimpleTrigger trigger = newTrigger()
					.withIdentity(triggerName)
					.withSchedule(ssb.repeatForever())
					.build();
    			// Tell quartz to schedule the job using our trigger 
    			scheduler.scheduleJob(job, trigger);
   	        	LOGGER.info(msg+".");    			
    		} catch( SchedulerException e){
    			LOGGER.error("SchedulerException",e);
    		}
    	} else {
        	LOGGER.info(msg+" is disabled.");
    	}
    }

    private void cronTrigger(String triggerName, JobDetail job, Properties prop) {
    	String status     = prop.getProperty(triggerName + ".status","off");
    	String expression = prop.getProperty(triggerName + ".expression","");

    	String msg = "Cron "+triggerName;
    	if(status.equals("on") && !expression.isEmpty()) {
        	msg += " is enabled: "+expression;
    		try {
    			CronTrigger trigger = newTrigger()
    					.withIdentity(triggerName)
    				    .withSchedule(cronSchedule(expression))
    				    .build();
    			// Tell quartz to schedule the job using our trigger 
    			scheduler.scheduleJob(job, trigger);
   	        	LOGGER.info(msg);    			
    		} catch( SchedulerException e){
    			LOGGER.error("SchedulerException",e);
    		}
    	} else {
        	LOGGER.info(msg+" is disabled.");
    	}
    }
}
