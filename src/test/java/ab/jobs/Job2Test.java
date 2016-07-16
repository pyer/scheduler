package ab.jobs;

import org.quartz.JobExecutionException;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class Job2Test {

    @Test
    public void testExecute() throws JobExecutionException {
      Job2 job = new Job2();
      job.execute(null);
      assertEquals(job.getCount(), 1);
	  }
}
