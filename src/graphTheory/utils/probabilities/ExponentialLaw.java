package graphTheory.utils.probabilities;

import graphTheory.utils.Math2;

/**
 * This class simulate an exponential probability law
 * @author mouton
 *
 */
public class ExponentialLaw extends ProbabilityLaw {
	
	/**
	 * The lambda parameter of the exponential law
	 */
	private double lambda;

	/**
	 * Create an exponential probability law with parameter lambda
	 * @param lambda
	 */
	private ExponentialLaw(double lambda) {
		this.lambda = lambda;
	}

	@Override
	public double simulate() {
		return Math2.exp(lambda);
	}
}

// TODO Relire
// TODO Refactor
// TODO Commenter
