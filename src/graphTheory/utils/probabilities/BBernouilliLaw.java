package graphTheory.utils.probabilities;

/**
 * A Bernouilli Law which has a probability of p to return true
 * 
 *  
 * @author mouton
 */
public class BBernouilliLaw extends BooleanProbabilityLaw {

	/**
	 * Probability to return true
	 */
	private double p;

	/**
	 * Create a boolean bernouilli probability law of parameter p
	 * @param p
	 */
	public BBernouilliLaw(double p) {
		setP(p);
	}
	
	public void setP(double p){
		if (p < 0)
			this.p = 0;
		else if (p > 1)
			this.p = 1;
		else
			this.p = p;
	}

	@Override
	public boolean simulate() {
		double x = Math.random();
		return x < p;
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
