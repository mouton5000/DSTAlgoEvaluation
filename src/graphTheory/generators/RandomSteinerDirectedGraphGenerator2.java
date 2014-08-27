package graphTheory.generators;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.utils.probabilities.BooleanProbabilityLaw;
import graphTheory.utils.probabilities.DConstantLaw;
import graphTheory.utils.probabilities.DiscreteProbabilityLaw;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class RandomSteinerDirectedGraphGenerator2 extends GraphGenerator<SteinerDirectedInstance> {
	private static final String INPUT_NUMBER_OF_VERTICES_LAW = "RandomSteinerGraphGenerator2_numberOfVerticesLaw";
	private static final String INPUT_PROBABILITY_OF_LINK_LAW = "RandomSteinerGraphGenerator2_probabilityOfLink";
	private static final String INPUT_NUMBER_OF_REQUIRED_VERTICES_LAW = "RandomSteinerGraphGenerator2_numberOfRequiredVerticeLaw";
	private static final String INPUT_COST_LAW = "RandomSteinerGraphGenerator2_costLaw";
	
	protected DiscreteProbabilityLaw numberOfVerticesLaw;
	protected BooleanProbabilityLaw probabilityOfLink;
	protected DiscreteProbabilityLaw numberOfRequiredVerticeLaw;
	protected DiscreteProbabilityLaw costLaw;
	
	@Override
	public void setInput(String name, Object o) {
		if (name.equals(INPUT_NUMBER_OF_VERTICES_LAW)) {
			if (o instanceof DiscreteProbabilityLaw)
				setNumberOfVerticesLaw((DiscreteProbabilityLaw) o);
			else if (o instanceof Integer)
				setNumberOfVerticesLaw((Integer)o);
		
		} else if(name.equals(INPUT_PROBABILITY_OF_LINK_LAW)) {
			if (o instanceof BooleanProbabilityLaw)
				setProbabilityOfLinkLaw((BooleanProbabilityLaw) o);
		} else if(name.equals(INPUT_NUMBER_OF_REQUIRED_VERTICES_LAW)) {
			if (o instanceof DiscreteProbabilityLaw)
				setNumberOfRequiredVerticesLaw((DiscreteProbabilityLaw) o);
			else if (o instanceof Integer)
				setNumberOfRequiredVerticesLaw((Integer) o);
		} else if (name.equals(INPUT_COST_LAW)) {
			if (o instanceof DiscreteProbabilityLaw)
				setCostLaw((DiscreteProbabilityLaw) o);
			else if (o instanceof Integer)
				setCostLaw((Integer) o);
		}
	}

	/**
	 * Crée un générateur d'instance de Steiner non orientée sans aucune entrée.
	 * (ne peut être utilisé tel quel).
	 */
	public RandomSteinerDirectedGraphGenerator2() {
		super();
		
		inputNames.add(INPUT_NUMBER_OF_VERTICES_LAW);
		inputNames.add(INPUT_PROBABILITY_OF_LINK_LAW);
		inputNames.add(INPUT_NUMBER_OF_REQUIRED_VERTICES_LAW);
		inputNames.add(INPUT_COST_LAW);

	}
	
	
	/**
	 * Définit la loi qui régit le nombre de noeuds dans le graphe généré.
	 * 
	 * @param novl
	 */
	public void setNumberOfVerticesLaw(DiscreteProbabilityLaw novl) {
		numberOfVerticesLaw = novl;
	}

	/**
	 * Définit le nombre de noeuds générés.
	 * 
	 * @param nov
	 */
	public void setNumberOfVerticesLaw(int nov) {
		numberOfVerticesLaw = new DConstantLaw(nov);
	}
	
	
	/**
	 * Définit la loi qui régit le nombre de noeuds dans le graphe généré.
	 * 
	 * @param novl
	 */
	public void setProbabilityOfLinkLaw(BooleanProbabilityLaw poll) {
		probabilityOfLink = poll;
	}
	
	/**
	 * Définit la loi qui régit le nombre de noeud 
	 * 
	 * @param norvl
	 */
	public void setNumberOfRequiredVerticesLaw(DiscreteProbabilityLaw norvl) {
		numberOfRequiredVerticeLaw = norvl;
	}

	/**
	 * Définit le nombre de noeud requis
	 * 
	 * @param norv
	 */
	public void setNumberOfRequiredVerticesLaw(int norv) {
		numberOfRequiredVerticeLaw = new DConstantLaw(norv);
	}

	/**
	 * Définit la loi qui régit le coût des arcs.
	 * 
	 * @param noel
	 */
	public void setCostLaw(DiscreteProbabilityLaw cl) {
		costLaw = cl;
	}

	/**
	 * Définit le coût de tous les arcs.
	 * 
	 * @param cost
	 */
	public void setCostLaw(int cost) {
		costLaw = new DConstantLaw(cost);
	}
	
	boolean[][] connections;
	
	@Override
	public SteinerDirectedInstance generate() {
		int nov = numberOfVerticesLaw.simulate();
		int norv = numberOfRequiredVerticeLaw.simulate();
		
		if(norv > nov)
			norv = nov;
		
		DirectedGraph dg = new DirectedGraph();
		SteinerDirectedInstance sdi = new SteinerDirectedInstance(dg);
		
		
		for(int i = 0; i<nov;i++)
			dg.addVertice(i);
		
		sdi.setRoot(0);
		
		connections = new boolean[nov][nov];
		for(int u = 0; u<nov; u++){
			Arrays.fill(connections[u], false);
			connections[u][u] = true;
		}
		
		Arc a;
		boolean linkUV, linkVU;
		for(int u=0; u<nov; u++){
			for(int v=u+1; v<nov; v++){
				linkUV = probabilityOfLink.simulate();
				if(linkUV){
					a = dg.addDirectedEdge(u, v);
					int cost = costLaw.simulate();
					sdi.setCost(a, cost);
					link(u,v,nov);
				}
				
				linkVU = probabilityOfLink.simulate();
				if(linkVU){
					a = dg.addDirectedEdge(v, u);
					int cost = costLaw.simulate();
					sdi.setCost(a, cost);
					link(v,u,nov);
				}
			}
		}
		
		
		// Remove nodes not linked to root;
		
		LinkedList<Integer> connectedToRoot = new LinkedList<Integer>();
		for(int u=1; u<nov; u++){
			if(!connections[0][u])
				dg.removeVertice(u);
			else
				connectedToRoot.add(u);
		}
		
		Iterator<Integer> it = connectedToRoot.descendingIterator();
		
		while(it.hasNext() && norv > 0){
			Integer u = it.next();
			sdi.setRequired(u);
			norv--;
		}
		
		
		
		return sdi;
	}
	
	private void link(Integer u, Integer v, int nov){
		connections[u][v] = true;
		for(int n=0; n<nov; n++)
			for(int m=0; m<nov; m++)
				if(connections[n][u] && connections[v][m])
					connections[n][m] = true;
	}
	
}


// TODO Commenter