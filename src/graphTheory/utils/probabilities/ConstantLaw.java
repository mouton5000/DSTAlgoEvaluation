package graphTheory.utils.probabilities;

/**
 * Probability law which always returns the same double.
 * @author mouton
 *
 */
public class ConstantLaw extends ProbabilityLaw {

	/**
	 * The double always returned by this probability law
	 */
	double constant;

	/**
	 * Create a probability law which always returns c
	 * @param c
	 */
	public ConstantLaw(double c) {
		constant = c;
	}

	@Override
	public double simulate() {
		return constant;
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
