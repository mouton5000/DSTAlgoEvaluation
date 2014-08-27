package graphTheory.utils.probabilities;

/**
 * 
 * Probability law which returns true or false.
 * 
 * Sous ensemble des générateur d'entiers réduis à une génération de booléens (1
 * et 0).
 * 
 * @author mouton
 * 
 */
public abstract class BooleanProbabilityLaw {
	
	/**
	 * Simulate this probability law and return a boolean value
	 * depending on the law.
	 * @return
	 */
	public abstract boolean simulate();
}

// TODO Relire
// TODO Refactor
// TODO Commenter
