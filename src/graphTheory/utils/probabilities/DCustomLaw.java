package graphTheory.utils.probabilities;

import graphTheory.utils.Math2;

import java.util.HashMap;

/**
 * 
 * Create a custom discrete probability law : 
 * for each possible integer returned, a probability is decided (such that the sum
 * of all probabilities is 1)
 * when simulated, the law returns each element with the decided probability.
 * 
 * Each probability is defined by a number, his force. Higher is the
 * force, compared to all other forces, higher is the probability. 
 * It is equal to the ratio of the force divided by the sum of all forces.
 * 
 * Crée une loi de probabilité personalisée.
 * Elle permet de définir la liste des éléments que l'on peut
 * renvoyer et la force de ceux ci.
 * Renvoie ensuite les éléments avec une probabilité qui 
 * dépend de leur force.
 * 
 * Par exemple, si la loi l est définie comme : 
 * 1 avec une force x1 = 3, 4 avec une force x4 = 1, et 156 avec une force x156 = 1.
 * Alors :
 * -	1 a 3 fois plus de chance de sortir que 4 et 156.
 * -	l.simulate() va calculer la somme des forces S = x1 + x4 + x156, tirer au hasard
 * uniformément un nombre u entre 1 et S, et renvoyer 1 si u <= x1, 4 si u <= x1 + x4
 * et 156 sinon.
 *  
 *  
 *  Renvoie 0 par défaut.
 * @author mouton
 *
 */
public class DCustomLaw extends DiscreteProbabilityLaw{

	
	/**
	 * Association between elements and their forces
	 */
	private HashMap<Integer,Integer> h;
	
	/**
	 * Sum of all the forces
	 */
	private int sum;

	/**
	 * Create a custom discrete law with no elements returnable.
	 */
	public DCustomLaw() {
		h = new HashMap<Integer, Integer>();
		sum = 0;
	}

	/**
	 * Add that element to the list of possibly returned elements.
	 * The force define the probability to be returned. Higher is the
	 * forces, compared to all other forces, higher is the probability.
	 * 
	 * It is equal to the ratio of the force divided by the sum of all forces.
	 * 
	 * @param element
	 * @param force
	 */
	public void addCustomElement(int element, int force){
		h.put(element,force);
		sum += force;
	}
	
	/**
	 * Change the force of the specified element. If it had no previous
	 * force, this calls the addCustomElement method.
	 * @param element
	 * @param force
	 */
	public void editElement(int element, int force){
		if(h.containsKey(element))
		{
			sum += force-h.get(element);
			h.put(element, force);
		}
		else
			addCustomElement(element, force);
	}
	
	/**
	 * Return the force of the specified element or null if it does not exists.
	 * @param element
	 * @return
	 */
	public Integer getForce(int element){
		return h.get(element);
	}
	
	/**
	 * Delete the specified element from the list of possibly returned elements.
	 * @param element
	 */
	public void deleteElement(Integer element){
		sum -= h.get(element);
		h.remove(element);
	}

	@Override
	public int simulate() {
		if(h.size() != 0)
		{
			int u = Math2.randomInt(sum)+1;
			int sumLeft = 0;
			for(Integer elem : h.keySet()){
				sumLeft += h.get(elem);
				if(u <= sumLeft)
					return elem;
			}
		}
		
		return 0;
	}


}

// TODO Relire
// TODO Refactor
// TODO Commenter
