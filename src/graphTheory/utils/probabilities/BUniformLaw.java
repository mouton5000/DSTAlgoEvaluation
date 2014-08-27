package graphTheory.utils.probabilities;

import graphTheory.utils.Math2;

/**
 * Probability law which returns true of false with uniform probability
 * @author mouton
 *
 */
public class BUniformLaw extends BooleanProbabilityLaw {

	@Override
	public boolean simulate() {
		return Math2.randomBoolean();
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
