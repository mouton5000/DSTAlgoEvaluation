package graphTheory.algorithms;

import graphTheory.instances.Instance;

/**
 * An algorithm is an object we build with inputs, then we run it, and then we
 * check the outputs.
 * 
 * @author Watel Dimitri
 */
public abstract class Algorithm<T extends Instance> {

	/**
	 * Time in ms when the algorithm starts to run over an instance
	 */
	protected long begginingTime;

	/**
	 * Last running time in ms of this algorithm
	 */
	private Long time;

	protected T instance;

	/**
	 * Set the instance this algorithm is running or will run over
	 * 
	 * @return
	 */
	public void setInstance(T instance) {
		this.instance = instance;
	}
	
	/**
	 * Return the instance this algorithm is running or will run over
	 * 
	 * @return
	 */
	protected T getInstance(){
		return instance;
	}

	/**
	 * Apply necessary operations to define default output when the input
	 * instance has no solution
	 * 
	 * @return
	 */
	protected abstract void setNoSolution();

	/**
	 * Run the algorithm over an instance
	 */
	public void compute() {
		Instance instance = this.getInstance();
		if (instance == null || (checkFeasibility && !instance.hasSolution())) {
			setNoSolution(); // If the instance has no solution...
			return;
		}
		time = null;
		setTimeBegin(); // Save the current timestamp
		computeWithoutTime();
		setTime(); // Check the current timestamp to compute the running time of the algorithm
	}

	/**
	 * Run the algorithm over the instance (without checking the instance has a
	 * solution, and without checking the running time)
	 */
	protected abstract void computeWithoutTime();

	/**
	 * If false, this algorithm does not check if the current instance has a
	 * solution. The default value is true.
	 */
	private boolean checkFeasibility;

	/**
	 * If false, this algorithm does not check if the current instance has a
	 * solution. The default value is true.
	 */
	public void setCheckFeasibility(boolean cf) {
		checkFeasibility = cf;
	}

	/**
	 * create a new algorithm, with no defined instance.
	 */
	protected Algorithm() {
		checkFeasibility = true;
	}

	/**
	 * Save the current timestamp
	 */
	private void setTimeBegin() {
		begginingTime = System.currentTimeMillis();
	}

	/**
	 * Check the current timestamp to compute the running time of the algorithm
	 * 
	 * @return the time from the last call of {@link #setTimeBegin()}..
	 */
	protected long getCurrentTime() {
		return System.currentTimeMillis() - begginingTime;
	}

	private void setTime() {
		time = getCurrentTime();
	}

	/**
	 * @return the last running time of this algorithm. 0 if it was never run.
	 */
	public Long getTime() {
		return time;
	}
}
