package graphTheory.instances.steiner.classic;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.graph.Graph;
import graphTheory.graph.UndirectedGraph;
import graphTheory.utils.Collections2;
import graphTheory.utils.HighQualityRandom;
import graphTheory.utils.Math2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


public class SteinerDirectedInstance extends SteinerInstance implements Cloneable {

	public SteinerDirectedInstance(DirectedGraph g) {
		super(g);
	}

	Integer root;

	public Integer getRoot() {
		return root;
	}

	public void setRoot(Integer root) {
		if (root != null) {
			Integer r = getRoot();
			if(r != null)
				graph.setCircleSymbol(r);
			this.root = root;
			graph.setSquareSymbol(root);
		}
	}

	public DirectedGraph getGraph(){
		return (DirectedGraph) graph;
	}

	/**
	 * Renvoie une instance de dl'arborescence de Steiner orienté à partir d'une
	 * instance de Steiner non orienté sug et sa solution optimale. On place dessus une
	 * racine aléatoirement. On oriente ensuite les arcs de cette solution de la
	 * racine vers les terminaux, assurant d'avoir une solution optimale pour le
	 * graphe généré. Les autres arcs sont ensuite orientés aléatoirement. Le
	 * graphe est ensuite fortement connexisé avec des arcs de poids le poids
	 * maximal de sug.
	 * 
	 * @param sug
	 *            : instance de steiner non orienté.
	 * @param optTree solution optimale de sug
	 * @return une instance de steiner orientée fortement connexe, dont la
	 *         solution optimale est de même poids que cette de sug.
	 */
	public static SteinerDirectedInstance getRandomGraphStronglyConnectedFromUndirectedInstance(
			SteinerUndirectedInstance sug, HashSet<Arc> optTree) {
		DirectedGraph dg = new DirectedGraph();
		SteinerDirectedInstance sdg = new SteinerDirectedInstance(dg);

		UndirectedGraph ug = sug.getGraph();

		int s = ug.getNumberOfVertices();
		Integer[][] shpInSug = new Integer[s][s];
		boolean[][] connectionsInSdg = new boolean[s][s];

		// On place déjà tous les noeuds de sdg, on conserve les
		// noeuds requis, mais on ne place pas encore la racine
		Iterator<Integer> it = ug.getVerticesIterator();
		Integer n;

		HashMap<Integer,Integer> nodes2ids = new HashMap<Integer, Integer>();
		HashMap<Integer,Integer> ids2nodes = new HashMap<Integer, Integer>();
		int id = 0;
		while (it.hasNext()) {
			n = it.next();
			dg.addVertice(n);
			sdg.setRequired(n, sug.isRequired(n));

			shpInSug[id][id] = 0;
			connectionsInSdg[id][id] = true;

			nodes2ids.put(n,id);
			ids2nodes.put(id++,n);
		}

		// On récupère le graphe induit par la solution optimale
		Graph inducedGraph = ug.getInducedGraphFromArc(optTree);

		// On choisit au sein de la solution optimale une racine au hasard
		Integer root;
		root = inducedGraph.getRandomVertice();
		sdg.setRoot(root);

		// On place pour chaque arc de la solution optimale un
		// arc orienté de la racine vers les terminaux dans le
		// graphe orienté
		HashSet<Arc> alreadyDirected = new HashSet<Arc>();
		it = inducedGraph.getVerticesIterator();
		Arc aInducedGraph;
		Integer m,p;
		while (it.hasNext()) {
			n = it.next();
			if (!sug.isRequired(n))
				continue;
			ArrayList<Arc> arcs = inducedGraph.getMinimumNumberOfEdgePath(
					root, n);
			m = root;
			for (int i = 0; i < arcs.size(); i++) {
				// On oriente le premier arc de la racine vers l'autre noeud
				aInducedGraph = arcs.get(i);
				p = inducedGraph.getNeighbourNode(m,aInducedGraph);
				if(!alreadyDirected.contains(aInducedGraph)){
					alreadyDirected.add(aInducedGraph);
					Arc a = dg.addDirectedEdge(m,p);
					sdg.setCost(a, sug.getCost(aInducedGraph));
					connectionsInSdg[nodes2ids.get(a.getInput())][nodes2ids.get(a.getOutput())] = true;
				}
				m = p;
			}

		}

		Iterator<Arc> it2 = ug.getEdgesIterator();
		Arc a;
		while (it2.hasNext()) {
			a = it2.next();

			boolean b = Math2.randomBoolean();
			if (b) {
				n = a.getInput();
				m = a.getOutput();
			} else {
				n = a.getOutput();
				m = a.getInput();
			}

			Integer cost = sug.getCost(a); 

			if (!optTree.contains(a)){
				Arc c = dg.addDirectedEdge(n, m);
				sdg.setCost(c, cost);
				connectionsInSdg[nodes2ids.get(n)][nodes2ids.get(m)] = true;
			}


			shpInSug[nodes2ids.get(n)][nodes2ids.get(m)] = cost;
			shpInSug[nodes2ids.get(m)][nodes2ids.get(n)] = cost;
		}

		Iterator<Integer> itu,itv,itw;
		Integer idu, idv, idw, uv,vw,uw;
		itv = ug.getVerticesIterator();
		while(itv.hasNext()){
			idv = nodes2ids.get(itv.next());
			itu = ug.getVerticesIterator();
			while(itu.hasNext()){
				idu = nodes2ids.get(itu.next());
				itw = ug.getVerticesIterator();
				while(itw.hasNext()){
					idw = nodes2ids.get(itw.next());
					uw = shpInSug[idu][idw];
					uv = shpInSug[idu][idv];
					vw = shpInSug[idv][idw];
					if(uv != null && vw != null){
						if (uw != null)
							shpInSug[idu][idw] = shpInSug[idw][idu] = Math.min(uw, uv+vw);
						else
							shpInSug[idu][idw] = shpInSug[idw][idu] = uv+vw;
					}
					connectionsInSdg[idu][idw] = connectionsInSdg[idu][idw] || (connectionsInSdg[idu][idv] && connectionsInSdg[idv][idw]);
				}
			}
		}

		// Permute randomly couples of nodes
		int[] shuffledCouples = Math2.getRandomPermutation(s*s);
		
		
		

		for(int i = 0; i<s*s; i++){
			int shc = shuffledCouples[i];
			int raw = shc/s;
			int column = shc%s;
			
			
			if(connectionsInSdg[raw][column])
				continue;

			Arc b = dg.addDirectedEdge(ids2nodes.get(raw), ids2nodes.get(column));
			sdg.setCost(b, shpInSug[raw][column]);

			for(int k = 0; k<s; k++){
				for(int l = 0; l<s; l++){
					connectionsInSdg[k][l] = connectionsInSdg[k][l] || (connectionsInSdg[k][raw] && connectionsInSdg[column][l]);
				}
			}

		}



		return sdg;
	}


	public static SteinerDirectedInstance getAcyclicGraphFromUndirectedInstance(SteinerUndirectedInstance sug,
			HashSet<Arc> optTree){

		DirectedGraph dg = new DirectedGraph();
		SteinerDirectedInstance sdg = new SteinerDirectedInstance(dg);
		UndirectedGraph ug = sug.getGraph();

		Iterator<Integer> it = sug.getGraph().getVerticesIterator();
		Integer v;
		while(it.hasNext()){
			v = it.next();
			dg.addVertice(v);
			sdg.setRequired(v, sug.isRequired(v));
		}


		// On récupère le graphe induit par la solution optimale
		Graph inducedGraph = ug.getInducedGraphFromArc(optTree);

		Integer r = inducedGraph.getRandomVertice();
		sdg.setRoot(r);
		if(sug.getNumberOfRequiredVertices() != 1)
			sdg.setRequired(r, false);

		// On place pour chaque arc de la solution optimale un
		// arc orienté de la racine vers les terminaux dans le
		// graphe orienté
		HashSet<Arc> alreadyUsed = new HashSet<Arc>();
		it = inducedGraph.getVerticesIterator();
		Arc aInducedGraph;
		Integer n,m,p;
		while (it.hasNext()) {
			n = it.next();
			if (!sug.isRequired(n))
				continue;
			ArrayList<Arc> arcs = inducedGraph.getMinimumNumberOfEdgePath(
					r, n);
			m = r;
			for (int i = 0; i < arcs.size(); i++) {
				// On oriente le premier arc de la racine vers l'autre noeud
				aInducedGraph = arcs.get(i);
				p = inducedGraph.getNeighbourNode(m,aInducedGraph);
				if (!alreadyUsed.contains(aInducedGraph)){
					alreadyUsed.add(aInducedGraph);
					Arc a = dg.addDirectedEdge(m,p);
					sdg.setCost(a, sug.getCost(aInducedGraph));
				}
				m = p;
			}
		}


		HashSet<Integer> seen = new HashSet<Integer>();
		LinkedList<Integer> toSee = new LinkedList<Integer>();

		toSee.add(r);
		Integer u;
		while(!toSee.isEmpty()){
			u = toSee.pollFirst();
			if(seen.contains(u))
				continue;

			Iterator<Arc> it2 = ug.getUndirectedNeighbourEdgesIterator(u);
			while(it2.hasNext()){
				Arc a = it2.next();
				v = ug.getNeighbourNode(u,a);
				if(alreadyUsed.contains(a)){
					toSee.add(v);
					continue;
				}
				if(seen.contains(v))
					continue;
				Arc b = dg.addDirectedEdge(u,v);
				sdg.setCost(b, sug.getCost(a));
				toSee.add(v);

			}


			seen.add(u);
		}

		return sdg;

	}

	/**
	 * Renvoie une instance de Steiner orienté à partir de sug
	 * en symétrisant tous les arcs.
	 *  
	 * S'il n'y a qu'un seul terminal, la racine est placé en ce terminal. 
	 * Sinon La racine est placée sur un des terminaux et celui ci n'est plus un terminal.
	 * @param sug
	 * @return
	 */
	public static SteinerDirectedInstance getSymetrizedGraphFromUndirectedInstance(
			SteinerUndirectedInstance sug) {
		DirectedGraph dg = new DirectedGraph();
		SteinerDirectedInstance sdg = new SteinerDirectedInstance(dg);

		Iterator<Integer> it = sug.getGraph().getVerticesIterator();
		Integer v,r = sug.getRandomRequiredVertice();
		while(it.hasNext()){
			v = it.next();
			dg.addVertice(v);
			sdg.setRequired(v, sug.isRequired(v));
			if(v.equals(r)){
				sdg.setRoot(v);
				if(sug.getNumberOfRequiredVertices() != 1)
					sdg.setRequired(v, false);
			}
		}

		Iterator<Arc> it2 = sug.getGraph().getEdgesIterator();
		Arc a;
		Integer u;
		while(it2.hasNext()){
			a = it2.next();
			u = a.getInput();
			v = a.getOutput();

			Arc b1 = dg.addDirectedEdge(u,v);
			Arc b2 = dg.addDirectedEdge(v,u);

			sdg.setCost(b1, sug.getCost(a));
			sdg.setCost(b2, sug.getCost(a));
		}
		return sdg;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Noeuds\n\n");
		for (Integer n : getGraph().getVertices()) {
			s.append(n);
			if (isRequired(n))
				s.append(" ").append("x");
			if (getRoot().equals(n))
				s.append(" ").append("o");
			s.append("\n");
		}

		s.append("\nArcs\n\n");
		for (Arc a : getGraph().getEdges()) {
			s.append(a).append(" ").append(getCost(a)).append("\n");
		}

		return s.toString();
	}


	@Override
	public boolean hasSolution() {
		// On vérifie qu'il existe une solution
		ListIterator<Integer> it = this.getRequiredVerticesIterator();
		while (it.hasNext()) {
			if (!graph.areConnectedByDirectedPath(root, it.next()))
				return false;
		}
		return true;
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
