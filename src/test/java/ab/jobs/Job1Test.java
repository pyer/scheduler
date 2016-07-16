package ab.jobs;

import org.quartz.JobExecutionException;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class Job1Test {

    @Test
    public void testExecute() throws JobExecutionException {
      Job1 job = new Job1();
      job.execute(null);
      assertEquals(job.getCount(), 1);
	  }
}
