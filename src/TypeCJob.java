

import edu.rit.os1.cpu.JobStep;

/**
 * A class representing a computation job for use 
 * within a CPU scheduling simulation.
 * 
 * A type C job, if run all alone, executes for 10,000 seconds.
 * In a type C job, for the first nine job steps, each job step is 
 * 1 second of computation time followed by 99 seconds of blocking time; 
 * for the next five job steps, each job step is 19 seconds of 
 * computation time followed by 1 second of blocking time; and this 
 * pattern repeats until the end of the job. 
 * 
 * @author zsb1244
 */
public class TypeCJob extends OS1Job {
	
	/**
	 * Number of steps returned so far via subclassNext
	 */
	private int stepsCompleted = 0;
	
	/**
	 * Constructs this job with a starting time of 0L and a priority of 2
	 */
	public TypeCJob() {
		super(0L, 2);
	}

	/**
	 * The pattern of steps returned is described in the class comment.
	 */
	@Override
	protected JobStep subclassNext() {
		if( timeRun < TOTAL_RUNTIME ){
			if(  stepsCompleted++ % 14 < 9 ){
				timeRun += 100L;
				return new JobStep(1L, 99L);
			}else{
				timeRun += 20L;
				return new JobStep(19L, 1L);
			}
		}
		return null;
	}

}
