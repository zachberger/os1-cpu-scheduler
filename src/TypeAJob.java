

import edu.rit.os1.cpu.JobStep;

/**
 * 
 * A class representing a computation job for use 
 * within a CPU scheduling simulation.
 * 
 * A type A job, if run all alone, executes for 10,000 seconds.
 * In a type A job, each job step is 1 second of computation time 
 * followed by 99 seconds of blocking time.
 * 
 * @author zsb1244
 * 
 */
public class TypeAJob extends OS1Job {

	/**
	 * Constructs this job with a starting time of 0L and a priority of 1
	 */
	public TypeAJob() {
		super(0L, 1);
	}
	
	/**
	 * The pattern of steps returned is described in the class comment.
	 */
	@Override
	protected JobStep subclassNext() {
		if( timeRun < TOTAL_RUNTIME ){
			timeRun += 100L;
			return new JobStep(1L, 99L);
		}
		return null;
	}

}
