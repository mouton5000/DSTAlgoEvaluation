package graphTheory.generators;

import graphTheory.instances.GraphInstance;

import java.util.ArrayList;

/**
 * Classe abstraite permettant de générer un graphe de type T. Chaque générateur
 * doit définir ses paramètres de générations et implémenter une méthode qui
 * utilisera ses paramètres pour générer un graphe de type T.
 * 
 * Cette génération peut être aléatoire ou non.
 * 
 * @author mouton
 * 
 * @param <T>
 */
public abstract class GraphGenerator<T extends GraphInstance> {

	/**
	 * Enregistre l'entrée de nom name avec l'objet o.
	 * 
	 * @param name
	 * @param o
	 */
	public abstract void setInput(String name, Object o);

	/**
	 * Effectue la génération de l'algorithme
	 */
	public abstract T generate();

	/**
	 * Liste des noms des entrées
	 */
	protected final ArrayList<String> inputNames;

	public GraphGenerator() {
		this.inputNames = new ArrayList<String>();
	}

	/**
	 * Renvoie la liste des noms des entrées
	 * 
	 * @return
	 */
	public ArrayList<String> getInputNames() {
		return new ArrayList<String>(inputNames);
	}
}

// TODO Relire
// TODO Refactor
// TODO Commenter
