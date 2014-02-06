package graphTheory.generators;

import graphTheory.instances.GraphInstance;

/**
 * 
 * This abstract class generate an instance with the method {@link #generate()}.
 * 
 * @author Watel Dimitri
 * 
 * @param <T>
 *            type of the generated instance
 */
public abstract class InstanceGenerator<T extends GraphInstance> {

	/**
	 * Generate an instance of type T
	 */
	public abstract T generate();
}
