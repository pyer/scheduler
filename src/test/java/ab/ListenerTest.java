package ab;

import java.util.Properties;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.quartz.impl.StdSchedulerFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class ListenerTest {

    @Test
    public void testDisabledJob() {
    	Properties p = new Properties();
    	p.put("job.status", "off");
    	new Listener().initializeJobs(null, p);
        assertTrue(true);
	  }

    @Test
    public void testEnabledJob() {
    	Properties p = new Properties();
    	p.put("job.status", "on");
    	new Listener().initializeJobs(null, p);
        assertTrue(true);
	  }
    
    @Test
    public void testTrigger1sOff() throws SchedulerException {
    	Scheduler sched = new StdSchedulerFactory().getScheduler();
    	Properties p = new Properties();
    	p.put("job.status", "off");
    	p.put("job.interval", "1");
    	p.put("job.unit", "seconds");
    	p.put("job.job", "ab.jobs.DummyJob");
     	try {
    		sched.start();
    		ab.jobs.DummyJob.count=0;
    		new Listener().initializeJobs(sched, p);
			Thread.sleep(1200);
			assertEquals(ab.jobs.DummyJob.count,0);
		} catch (InterruptedException e) {
			assertTrue(false);
		}
    	sched.shutdown(true);
	  }

    @Test
    public void testTrigger1sOn() throws SchedulerException {
    	Scheduler sched = new StdSchedulerFactory().getScheduler();
		Properties p = new Properties();
    	p.put("job.status", "on");
    	p.put("job.interval", "1");
    	p.put("job.unit", "seconds");
    	p.put("job.job", "ab.jobs.DummyJob");
    	try {
    		sched.start();
    		ab.jobs.DummyJob.count=0;
    		new Listener().initializeJobs(sched, p);
			assertEquals(ab.jobs.DummyJob.count,0);
			Thread.sleep(2100);
			assertEquals(ab.jobs.DummyJob.count,3);
		} catch (InterruptedException e) {
			assertTrue(false);
		}
    	sched.shutdown(true);
	  }

    @Test
    public void testBadCron() throws SchedulerException {
    	Scheduler sched = new StdSchedulerFactory().getScheduler();
		Properties p = new Properties();
    	p.put("job.status", "on");
    	// Every second expr: * mn hh * * ?
    	p.put("job.expression", "*");
    	p.put("job.job", "ab.jobs.DummyJob");
    	try {
    		sched.start();
    		ab.jobs.DummyJob.count=0;
    		new Listener().initializeJobs(sched, p);
			assertEquals(ab.jobs.DummyJob.count,0);
			Thread.sleep(1200);
			assertEquals(ab.jobs.DummyJob.count,0);
		} catch (InterruptedException e) {
			assertTrue(false);
		} finally {
			sched.shutdown(true);
		}
	}

    @Test
    public void testGoodCron() throws SchedulerException {
    	Scheduler sched = new StdSchedulerFactory().getScheduler();
		Properties p = new Properties();
    	p.put("job.status", "on");
    	// Every second expr: * mn hh * * ?
    	p.put("job.expression", "* * * ? * *");
    	p.put("job.job", "ab.jobs.DummyJob");
    	try {
    		sched.start();
    		ab.jobs.DummyJob.count=0;
    		new Listener().initializeJobs(sched, p);
			assertEquals(ab.jobs.DummyJob.count,0);
			Thread.sleep(1100);
			assertEquals(ab.jobs.DummyJob.count,2);
		} catch (InterruptedException e) {
			assertTrue(false);
		} finally {
			sched.shutdown(true);
		}
	  }
    
}