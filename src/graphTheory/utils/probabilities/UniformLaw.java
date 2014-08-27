package graphTheory.utils.probabilities;

import graphTheory.utils.Math2;

/**
 * Uniform law returning an double between two defined doubles
 * with uniform probabilities.
 * @author mouton
 *
 */
public class UniformLaw extends ProbabilityLaw {
	
	/**
	 * The lower bound of the returned doubles (included)
	 */
	private double sub;
	
	/**
	 * The upper bound of the returned doubles (included)
	 */
	private double sup;

	/**
	 * Create a uniform law returning any double between sb and sp (included) with uniform
	 * probability.
	 * @param sb
	 * @param sp
	 */
	public UniformLaw(double sb, double sp) {
		sub = sb;
		sup = sp;
	}

	@Override
	public double simulate() {
		return Math2.uniform(sub, sup);
	}
}

// TODO Relire
// TODO Refactor
// TODO Commenter
