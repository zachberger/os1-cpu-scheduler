

import edu.rit.os1.cpu.JobStep;

/**
 * A class representing a computation job for use 
 * within a CPU scheduling simulation.
 * 
 * A type b job, if run all alone, executes for 10,000 seconds.
 * In a type B job, each job step is 50 second of computation time 
 * followed by 50 seconds of blocking time.
 * 
 * @author zsb1244
 */
public class TypeBJob extends OS1Job {

	/**
	 * Constructs this job with a starting time of 0L and a priority of 3
	 */
	public TypeBJob() {
		super(0L, 3);
	}

	/**
	 * The pattern of steps returned is described in the class comment.
	 */
	@Override
	protected JobStep subclassNext() {
		if( timeRun < TOTAL_RUNTIME ){
			timeRun += 100L;
			return new JobStep(50L, 50L);
		}
		return null;
	}

}
