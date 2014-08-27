package graphTheory.utils.probabilities;

/**
 * This class simulate a discrete bernouilli law which returns
 * 1 with a probability of p
 * 
 * Loi de bernouilli discrète de paramètre p. Renvoie 1 avec une
 *         probabilité p et 0 avec une probablité 1-p.
 * 
 * @author mouton 
 */
public class DBernouilliLaw extends DiscreteProbabilityLaw {

	/**
	 * Probability to return 1
	 */
	private double p;

	/**
	 * Create a discrete bernouilli probability law of parameter p
	 * @param p
	 */
	public DBernouilliLaw(double p) {
		if (p < 0)
			this.p = 0;
		else if (p > 1)
			this.p = 1;
		else
			this.p = p;

	}

	@Override
	public int simulate() {
		double x = Math.random();
		if (x < p)
			return 1;
		else
			return 0;
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
