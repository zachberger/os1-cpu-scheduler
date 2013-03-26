


import edu.rit.os1.cpu.CpuScheduler;
import edu.rit.os1.cpu.FifoReadyQueue;
import edu.rit.os1.cpu.PriorityReadyQueue;
import edu.rit.os1.cpu.ReadyQueue;

public class MLFSimulationMain {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		Params p = new Params(1,1,0);
		new MLFSimulationMain(p);
		
		p = new Params(5,1,0);
		new MLFSimulationMain(p);
		
		p = new Params(10,1,0);
		new MLFSimulationMain(p);

		p = new Params(1,1,1);
		new MLFSimulationMain(p);

		p = new Params(5,1,1);
		new MLFSimulationMain(p);

		p = new Params(10,1,1);
		new MLFSimulationMain(p);
		
		p = new Params(10,1,2);
		new MLFSimulationMain(p);
		
		p = new Params(5,1,2);
		new MLFSimulationMain(p);

		p = new Params(10,1,2);
		new MLFSimulationMain(p);	
		
		p = new Params(1,0,0);
		new MLFSimulationMain(p);	
		
		p = new Params(0,1,0);
		new MLFSimulationMain(p);	
		
		p = new Params(0,0,1);
		new MLFSimulationMain(p);	
	}
	
	public MLFSimulationMain(Params p ){
		
		System.out.println( "\n\n" + p );
		
		ReadyQueue rq = new FifoReadyQueue();
		Result rrResults = runSimulation( rq, new Params(p) );
		
		rq = new PriorityReadyQueue();
		Result hpfResults = runSimulation( rq, new Params(p) );
		
		rq = new MultiLevelFeedbackReadyQueue();
		Result mlfResults = runSimulation( rq, new Params(p) );
		
		System.out.println("\t\t\t\tRR\t\tHPF\t\tMLF");
		
		System.out.print("CPU utilization");
		System.out.printf("\t\t\t%.2f", rrResults.utilization);
		System.out.printf("\t\t%.2f", hpfResults.utilization);
		System.out.printf("\t\t%.2f\n", mlfResults.utilization);
		
		System.out.print("Type A mean response time");
		System.out.printf("\t%.2f", rrResults.aMean);
		System.out.printf("\t\t%.2f", hpfResults.aMean);
		System.out.printf("\t\t%.2f\n", mlfResults.aMean);
		
		System.out.print("Type B mean turnaround time");
		System.out.printf("\t%.2f", rrResults.bTAT);
		System.out.printf("\t%.2f", hpfResults.bTAT);
		System.out.printf("\t%.2f\n", mlfResults.bTAT);
		
		System.out.print("Type C mean response time ");
		System.out.printf("\t%.2f", rrResults.cResponse);
		System.out.printf("\t\t%.2f", hpfResults.cResponse);
		System.out.printf("\t\t%.2f\n", mlfResults.cResponse);

	}
	
	private Result runSimulation( ReadyQueue rq, Params p ){

		CpuScheduler cpu = new CpuScheduler(rq);
		int counter = p.numTypeA;
		while( counter-- > 0 ){
			cpu.addJob(new TypeAJob());
		}
		
		counter = p.numTypeB;
		while( counter-- > 0 ){
			cpu.addJob(new TypeBJob());
		}
		
		counter = p.numTypeC;
		while( counter-- > 0 ){
			cpu.addJob(new TypeCJob());
		}

		cpu.simulate(false);
		Result r = new Result();
		r.utilization = cpu.getCpuUtilization();
		
		counter = p.numTypeA - 1;
		while( counter >= 0 ){
			r.aMean += cpu.getResponseTime(counter--);
		}
		r.aMean /= p.numTypeA;
		
		counter = p.numTypeA;
		while( counter < p.numTypeA + p.numTypeB ){
			r.bTAT += cpu.getTurnaroundTime( counter++ );
		}
		r.bTAT /= p.numTypeB;

		counter = p.numTypeA + p.numTypeB;
		while( counter < p.numTypeA + p.numTypeB + p.numTypeC ){
			r.cResponse += cpu.getResponseTime( counter++ );
		}
		r.cResponse /= p.numTypeC;
		return r;
	}
	
	public static class Params{
		public int numTypeA;
		public int numTypeB;
		public int numTypeC;
		
		public Params(){}
		
		public Params( int numTypeA, int numTypeB, int numTypeC  ){
			this.numTypeA = numTypeA;
			this.numTypeB = numTypeB;
			this.numTypeC = numTypeC;
		}
		
		public Params( Params toCopy ){
			this.numTypeA = toCopy.numTypeA;
			this.numTypeB = toCopy.numTypeB;
			this.numTypeC = toCopy.numTypeC;
		}
		
		public String toString(){
			return numTypeA + ", " + numTypeB + ", " + numTypeC;
		}
		
	}
	
	public class Result{
		public double utilization;
		public double aMean;
		public double bTAT;
		public double cResponse;
	}

}
