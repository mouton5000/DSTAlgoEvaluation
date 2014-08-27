package graphTheory.utils.probabilities;

/**
 * A probability law which always returns the same value
 * 
 * @author mouton
 */
public class DConstantLaw extends DiscreteProbabilityLaw {

	/**
	 * Value always returned by the law.
	 */
	private int constant;

	/**
	 * Create a probability law which always returns c
	 * @param c
	 */
	public DConstantLaw(int c) {
		constant = c;
	}

	@Override
	public int simulate() {
		return constant;
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
