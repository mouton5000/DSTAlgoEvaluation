package graphTheory.utils.probabilities;

import graphTheory.utils.Math2;

/**
 * This class simulate a normal probability law
 * @author mouton
 *
 */
public class NormalLaw extends ProbabilityLaw {
	
	/**
	 * The mu parameter of the normal law corresponding to the mean
	 */
	private double mu;
	
	/**
	 * The sigma^2 parameter of the normal law
	 */
	private double sigma2;


	/**
	 * Create an normal probability law with parameters mu and sigma^2
	 *
	 * @param mu
	 * @param sigma2
	 */
	private NormalLaw(double mu, double sigma2) {
		this.mu = mu;
		this.sigma2 = sigma2;
	}

	@Override
	public double simulate() {
		return Math2.norm(mu, sigma2);
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
