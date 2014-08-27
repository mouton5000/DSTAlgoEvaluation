package graphTheory.utils.probabilities;

import graphTheory.utils.Math2;

/**
 * Uniform law returning an integer between two defined integers.
 * 
 * Loi uniforme discrète entre deux entiers. Renvoie un entier au hasard entre
 * la borne inférieure et supérieures, toutes deux inclues.
 * 
 * @author mouton
 * 
 */
public class DUniformLaw extends DiscreteProbabilityLaw {
	
	/**
	 * The lower bound of the returned integers (included)
	 */
	private int sub;
	
	/**
	 * The upper bound of the returned integers (included)
	 */
	private int sup;

	/**
	 * Create a discrete uniform law returning any integer between sb and sp (included) with uniform
	 * probability.
	 * @param sb
	 * @param sp
	 */
	public DUniformLaw(int sb, int sp) {
		sub = sb;
		sup = sp;
	}

	@Override
	public int simulate() {
		return Math2.randomInt(sub, sup + 1);
	}
}

// TODO Relire
// TODO Refactor
// TODO Commenter
