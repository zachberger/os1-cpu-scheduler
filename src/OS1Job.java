import edu.rit.os1.cpu.Job;

/**
 * Abstracts out common elements of Jobs used in this simulation
 * @author zach
 */
public abstract class OS1Job extends Job {

	/**
	 * All OS1 Jobs run for this length
	 */
	protected static final long TOTAL_RUNTIME = 10000L;
	
	/**
	 * Time elapsed in steps yielded 
	 */
	protected long timeRun = 0L;

	/**
	 * @see - See superclass for parameters
	 */
	protected OS1Job(long theStartTime, int thePriority) {
		super(theStartTime, thePriority);
	}

}