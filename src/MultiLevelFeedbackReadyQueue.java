import java.util.HashMap;
import java.util.Map;

import edu.rit.os1.cpu.FifoReadyQueue;
import edu.rit.os1.cpu.Job;
import edu.rit.os1.cpu.JobState;
import edu.rit.os1.cpu.ReadyQueue;

public class MultiLevelFeedbackReadyQueue extends ReadyQueue {

	/**
	 * Ready queue 0 must use RR scheduling with a time quantum of 8 seconds. 
	 */
	private final ReadyQueue level0 = new FifoReadyQueue( 8 );
	
	/**
	 * Ready queue 1 must use RR scheduling with a time quantum of 24 seconds. 
	 */
	private final ReadyQueue level1 = new FifoReadyQueue( 24 );
	
	/**
	 * Ready queue 2 must use FCFS scheduling. 
	 */
	private final ReadyQueue level2 = new FifoReadyQueue();
	
	/**
	 * The last Job returned by subclassGet(). This should
	 * be the Job that is currently executing on the CPU.
	 */
	private Job lastJob = null;

	/**
	 * Jobs that this queue has given to the CPU via subclassGet()
	 * of and have not terminated are mapped to the ReadyQueue that 
	 * they most recently originated from. 
	 */
	private Map<Job, ReadyQueue> outstandingJobs = new HashMap<Job,ReadyQueue>();

	/**
	 * The reason for the pending preemption. Will be NO_PREEMPTION when
	 * there is no pending preemption.
	 */
	private PreemptionCause preemptionCause = PreemptionCause.NO_PREEMPTION;
	
	
	/**
	 * The MLF ready queue class's constructor must have no arguments. 
	 */
	public MultiLevelFeedbackReadyQueue(){}
	
	/**
	 * Checks each subqueue to determine if this queue is empty
	 * @return true iff each subqueue is empty
	 */
	@Override
	protected boolean subclassIsEmpty() {
		return  level0.isEmpty() &
				level1.isEmpty() &
				level2.isEmpty();
	}

	/**
	 * Checks level 0 for jobs, followed by level 1, and followed by level 2
	 * Additionally marks which queue the job came from in the outstandingJobs map
	 */
	@Override
	protected Job subclassGet() {
		if( !level0.isEmpty() ){
			lastJob = level0.get();
			outstandingJobs.put(lastJob, level0);
		}else if( !level1.isEmpty() ){
			lastJob = level1.get();
			outstandingJobs.put(lastJob, level1);
		}else{
			lastJob = level2.get();
			outstandingJobs.put(lastJob, level2);
		}
		return lastJob;
	}

	/* (non-Javadoc)
	 * @see edu.rit.os1.cpu.ReadyQueue#subclassGetTimeQuantum()
	 */
	@Override
	protected long subclassGetTimeQuantum() {
		return outstandingJobs.get(lastJob).getTimeQuantum();
	}

	/**
	 * Puts the job into the proper queue based on rules 
	 * documented at http://www.cs.rit.edu/~ark/440/p3/p3.shtml
	 */
	@Override
	protected void subclassPut(Job theJob, JobState theOldState) {
		
		//A hack because this API is poorly designed...
		if( theOldState.equals(JobState.RUNNING) && 
			preemptionCause.equals(PreemptionCause.NO_PREEMPTION )){
			preemptionCause = PreemptionCause.TIME_QUANTUM_EXPIRED;
		}
		
		if( theOldState.equals(JobState.NOT_STARTED) ||
				theOldState.equals(JobState.BLOCKED) ){
			level0.put(theJob, theOldState);
			return;
		}
		
		if( preemptionCause.equals( PreemptionCause.NEW_JOB_READY ) ){
			preemptionCause = PreemptionCause.NO_PREEMPTION;
			outstandingJobs.get(theJob).put(theJob, theOldState);
			return;
		}else if( preemptionCause.equals( PreemptionCause.TIME_QUANTUM_EXPIRED ) ){
			
			ReadyQueue jobsPreviousQueue = outstandingJobs.get(theJob);
			if( jobsPreviousQueue.equals( level0 ) ){
				level1.put(theJob, theOldState);
			}else{
				level2.put(theJob, theOldState);
			}
		}

	}

	/**
	 * Determines if the current running job should be preempted
	 * based on rules at: http://www.cs.rit.edu/~ark/440/p3/p3.shtml
	 */
	@Override
	protected boolean subclassPreempt(Job theJob, long theCpuTime) {
		if( outstandingJobs.get(lastJob).equals(level2) ){
			if( !level0.isEmpty() || !level1.isEmpty() ){
				preemptionCause = PreemptionCause.NEW_JOB_READY;
				return true;
			}else if( level2.preempt(theJob, theCpuTime) ){
				preemptionCause = PreemptionCause.TIME_QUANTUM_EXPIRED;	
				return true;
			}else{
				preemptionCause = PreemptionCause.NO_PREEMPTION;
				return false;
			}
		}else if( outstandingJobs.get(lastJob).equals(level1) ){
			if( !level0.isEmpty() ){
				preemptionCause = PreemptionCause.NEW_JOB_READY;
				return true;
			}else if( level1.preempt(theJob, theCpuTime) ){
				preemptionCause = PreemptionCause.TIME_QUANTUM_EXPIRED;	
				return true;
			}else{
				preemptionCause = PreemptionCause.NO_PREEMPTION;
				return false;
			}
		}else{
			if( level0.preempt(theJob, theCpuTime) ){
				preemptionCause = PreemptionCause.TIME_QUANTUM_EXPIRED;	
				return true;
			}else{
				preemptionCause = PreemptionCause.NO_PREEMPTION;
				return false;
			}
		}
	}
	
	/**
	 * Not Used by this ReadyQueue
	 */
	@Override
	protected void subclassCpuBurstFinished(Job theJob, long theCpuBurstTime) {}

	/**
	 * Not Used by this ReadyQueue
	 */
	@Override
	protected void subclassPriorityChanged(Job theJob) {}

	/**
	 * Removes the job from the list of outstanding jobs
	 * @param theJob - the job to remove from outsandingJobs
	 */
	@Override
	protected void subclassTerminated(Job theJob) {
		outstandingJobs.remove(theJob);
	}

	/**
	 * Enumerates the causes of preemption
	 * 
	 * @author Zach Berger - zsb1244@rit.edu
	 *
	 */
	enum PreemptionCause{
		
		/**
		 * A job was added to the ReadyQueue in a higher priority subqueue
		 */
		NEW_JOB_READY,
		
		/**
		 * The time quantum of the currently running job has expired and the
		 * job should be preempted. 
		 */
		TIME_QUANTUM_EXPIRED, 
		
		/**
		 * There is no pending preemption 
		 */
		NO_PREEMPTION
		
	}
	
}
