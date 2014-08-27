package graphTheory.utils.probabilities;

/**
 * A probability law which always returns the same value
 * 
 * @author mouton
 */
public class BConstantLaw extends BooleanProbabilityLaw {

	/**
	 * Value always returned by the law.
	 */
	private boolean constant;

	/**
	 * Create a probability law which always returns c
	 * @param c
	 */
	public BConstantLaw(boolean c) {
		constant = c;
	}

	@Override
	public boolean simulate() {
		return constant;
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
