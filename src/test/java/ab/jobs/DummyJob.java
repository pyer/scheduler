package ab.jobs;



import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DummyJob implements Job{

	public static int count=0;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
    	count++;
    }
    
}
