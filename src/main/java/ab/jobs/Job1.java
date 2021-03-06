package ab.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Job1 implements Job{

    private static final Logger LOGGER = LoggerFactory.getLogger(Job1.class);

    private int count = 0;

    public int getCount() {
      return count;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
      LOGGER.info("Job1 ...");
      count++;
    }
}
