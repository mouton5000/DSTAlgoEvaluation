package graphTheory.instances.steiner.classic;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.graph.Graph;
import graphTheory.graph.UndirectedGraph;
import graphTheory.utils.Math2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 
 * Instance for the Directed Steiner Tree problem : given a graph,
 * one node {@link #root} of that graph, some nodes called terminals
 * or required vertices,  and weight over the arcs, return the
 * minimum cost directed tree rooted in {@link #root} spanning all the terminals.
 * 
 * @author Watel Dimitri
 *
 */
public class SteinerDirectedInstance extends SteinerInstance implements
		Cloneable {

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
			if (r != null)
				graph.setCircleSymbol(r);
			this.root = root;
			graph.setSquareSymbol(root);
		}
	}

	public DirectedGraph getGraph() {
		return (DirectedGraph) graph;
	}

	/**
	 * 
	 * From a undirected steiner instance sui and an optimal solution optTree of that instance,
	 * build and return a Strongly Connected Directed Steiner instance sdi with same optimal cost
	 * as sui.
	 * <p>
	 * sdi contains all the nodes in sui.
	 * The root of sdi is chosen among the nodes in optTree. Then the arcs of opttree are
	 * directed from the root to the leaves of optTree.
	 * Each other edge is then uniformly randomly directed. The cost of the build arc is the same
	 * as the cost of the edge is was built from.
	 * <p>
	 * This does not ensure the graph is strongly connected. So we add arcs between couples of
	 * nodes chosen uniformly randomly until the graph is strongly connected. The cost of this
	 * arc is the shortest path cost between that couple of nodes in the undirected instance sui.
	 * <p>
	 * As a consequence, the cost of the optimale solution is the same in sui and sdi.
	 * 
	 * @param sui
	 * @param optTree
	 * @return a Strongly Connected Directed Steiner instance sdi with same optimal cost
	 * as sui 
	 */
	public static SteinerDirectedInstance getRandomGraphStronglyConnectedFromUndirectedInstance(
			SteinerUndirectedInstance sui, HashSet<Arc> optTree) {
		DirectedGraph dg = new DirectedGraph();
		SteinerDirectedInstance sdi = new SteinerDirectedInstance(dg);

		UndirectedGraph ug = sui.getGraph();

		int s = ug.getNumberOfVertices();
		
		// Those maps associate an id from 1 to s to each node (the nodes are not
		// necessarily numbered from 1 to s at the beginning of the methods)
		HashMap<Integer, Integer> nodes2ids = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> ids2nodes = new HashMap<Integer, Integer>();
		
		// shpInSui contains for each couple of nodes the shortest path costs in sui
		// between those nodes
		Integer[][] shpInSui = new Integer[s][s];
		
		// connectionsInSdi[i][j] is true if sdi contains a path from the node
		// if id i to the node of id j. At the beginning, sdi is empty and do not
		// contains any arc
		boolean[][] connectionsInSdi = new boolean[s][s];
		
		
		// We firstly build every node of sdi and place every terminals
		// but we do not place the root.
		
		Iterator<Integer> it = ug.getVerticesIterator();
		Integer n;

		int id = 0;
		while (it.hasNext()) {
			n = it.next();
			dg.addVertice(n);
			sdi.setRequired(n, sui.isRequired(n));

			shpInSui[id][id] = 0; // The shortest path from a node to itself is 0
			connectionsInSdi[id][id] = true; // sdi contains a path from any node to itself 

			nodes2ids.put(n, id);
			ids2nodes.put(id++, n);
		}

		// We get the induced graph of sui by the arcs in optTree
		Graph inducedGraph = ug.getInducedGraphFromArc(optTree);

		// A root is choosen at random inside optTree
		Integer root;
		root = inducedGraph.getRandomVertice();
		sdi.setRoot(root);
		if (sui.getNumberOfRequiredVertices() != 1)
			sdi.setRequired(root, false);
		
		// We directed the edges in optTree from the chosen root
		// to each terminal
		HashSet<Arc> alreadyDirected = new HashSet<Arc>();
		it = inducedGraph.getVerticesIterator();
		Arc aInducedGraph;
		Integer m, p;
		while (it.hasNext()) {
			n = it.next();
			if (!sui.isRequired(n))
				continue;
			ArrayList<Arc> arcs = inducedGraph.getMinimumNumberOfEdgePath(root,
					n);
			m = root;
			for (int i = 0; i < arcs.size(); i++) {
				aInducedGraph = arcs.get(i);
				p = inducedGraph.getNeighbourNode(m, aInducedGraph);
				if (!alreadyDirected.contains(aInducedGraph)) {
					alreadyDirected.add(aInducedGraph);
					Arc a = dg.addDirectedEdge(m, p);
					sdi.setCost(a, sui.getIntCost(aInducedGraph));
					
					// For each new arc in sdi, we update the connection between the nodes
					connectionsInSdi[nodes2ids.get(a.getInput())][nodes2ids
							.get(a.getOutput())] = true; 
				}
				m = p;
			}

		}

		// We randomly directed the other edges of sui
		
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

			Integer cost = sui.getIntCost(a);

			// If the edge was not already directed
			if (!optTree.contains(a)) {
				Arc c = dg.addDirectedEdge(n, m);
				sdi.setCost(c, cost);
				// We udpate the connection betwwen the nodes of the added arc
				connectionsInSdi[nodes2ids.get(n)][nodes2ids.get(m)] = true; 
			}

			// We set the currently known shortest path between the nodes of that arc
			// This shortest path will be updated later
			shpInSui[nodes2ids.get(n)][nodes2ids.get(m)] = cost;
			shpInSui[nodes2ids.get(m)][nodes2ids.get(n)] = cost;
		}

		// We now compute all the shortest path between all couple of nodes
		// using the raw warshall floyd algorithm
		
		Iterator<Integer> itu, itv, itw;
		Integer idu, idv, idw, uv, vw, uw;
		itv = ug.getVerticesIterator();
		while (itv.hasNext()) {
			idv = nodes2ids.get(itv.next());
			itu = ug.getVerticesIterator();
			while (itu.hasNext()) {
				idu = nodes2ids.get(itu.next());
				itw = ug.getVerticesIterator();
				while (itw.hasNext()) {
					idw = nodes2ids.get(itw.next());
					uw = shpInSui[idu][idw];
					uv = shpInSui[idu][idv];
					vw = shpInSui[idv][idw];
					if (uv != null && vw != null) {
						if (uw != null)
							shpInSui[idu][idw] = shpInSui[idw][idu] = Math.min(
									uw, uv + vw);
						else
							shpInSui[idu][idw] = shpInSui[idw][idu] = uv + vw;
					}
					
					// If u is connected to v and v is connected to w, then u is connected to w
					connectionsInSdi[idu][idw] = connectionsInSdi[idu][idw]
							|| (connectionsInSdi[idu][idv] && connectionsInSdi[idv][idw]);
				}
			}
		}
		
		// We now randomly add new arcs to the graph until it is strongly connected:
		// this is true if connectionsInSdi contains only true.
		
		
		// Permute randomly couples of nodes
		int[] shuffledCouples = Math2.getRandomPermutation(s * s);

		for (int i = 0; i < s * s; i++) {
			int shc = shuffledCouples[i];
			int raw = shc / s;
			int column = shc % s;

			// If the nodes are already connected we do nothing
			if (connectionsInSdi[raw][column])
				continue;

			// If not we add a new arc and update connections in sdi
			
			Arc b = dg.addDirectedEdge(ids2nodes.get(raw),
					ids2nodes.get(column));
			
			// The cost of that arc is the shortet path in sui
			sdi.setCost(b, shpInSui[raw][column]); 
			for (int k = 0; k < s; k++) {
				for (int l = 0; l < s; l++) {
					
					// Update connections
					connectionsInSdi[k][l] = connectionsInSdi[k][l]
							|| (connectionsInSdi[k][raw] && connectionsInSdi[column][l]);
				}
			}

		}

		return sdi;
	}

	/**
	 * 
	 * From a undirected steiner instance sui and an optimal solution optTree of that instance,
	 * build and return a Acyclic Directed Steiner instance sdi with same optimal cost
	 * as sui.
	 * <p>
	 * sdi contains all the nodes in sui.
	 * The root of sdi is chosen among the nodes in optTree. Then the arcs of opttree are
	 * directed from the root to the leaves of optTree.
	 * Each other edge is then directed in an acyclic way using breadth-first-search from the root. The cost of the build arc is the same
	 * as the cost of the edge is was built from.
	 * <p>
	 * As a consequence, the cost of the optimale solution is the same in sui and sdi.
	 * 
	 * @param sui
	 * @param optTree
	 * @return a Strongly Connected Directed Steiner instance sdi with same optimal cost
	 * as sui 
	 */
	public static SteinerDirectedInstance getAcyclicGraphFromUndirectedInstance(
			SteinerUndirectedInstance sui, HashSet<Arc> optTree) {

		DirectedGraph dg = new DirectedGraph();
		SteinerDirectedInstance sdi = new SteinerDirectedInstance(dg);
		UndirectedGraph ug = sui.getGraph();

		// We firstly build every node of sdi and place every terminals
				// but we do not place the root.
		
		Iterator<Integer> it = sui.getGraph().getVerticesIterator();
		Integer v;
		while (it.hasNext()) {
			v = it.next();
			dg.addVertice(v);
			sdi.setRequired(v, sui.isRequired(v));
		}

		// We get the induced graph of sui by the arcs in optTree
		Graph inducedGraph = ug.getInducedGraphFromArc(optTree);

		// A root is choosen at random inside optTree
		Integer r = inducedGraph.getRandomVertice();
		sdi.setRoot(r);
		if (sui.getNumberOfRequiredVertices() != 1)
			sdi.setRequired(r, false);

		// We directed the edges in optTree from the chosen root
				// to each terminal
		HashSet<Arc> alreadyUsed = new HashSet<Arc>();
		it = inducedGraph.getVerticesIterator();
		Arc aInducedGraph;
		Integer n, m, p;
		while (it.hasNext()) {
			n = it.next();
			if (!sui.isRequired(n))
				continue;
			ArrayList<Arc> arcs = inducedGraph.getMinimumNumberOfEdgePath(r, n);
			m = r;
			for (int i = 0; i < arcs.size(); i++) {
				aInducedGraph = arcs.get(i);
				p = inducedGraph.getNeighbourNode(m, aInducedGraph);
				if (!alreadyUsed.contains(aInducedGraph)) {
					alreadyUsed.add(aInducedGraph);
					Arc a = dg.addDirectedEdge(m, p);
					sdi.setCost(a, sui.getIntCost(aInducedGraph));
				}
				m = p;
			}
		}
		
		// Each other edge is directed in an acyclic way using breadth-first-search
		// from the root.

		HashSet<Integer> seen = new HashSet<Integer>();
		LinkedList<Integer> toSee = new LinkedList<Integer>();

		toSee.add(r);
		Integer u;
		while (!toSee.isEmpty()) {
			u = toSee.pollFirst();
			if (seen.contains(u))
				continue;

			Iterator<Arc> it2 = ug.getUndirectedNeighbourEdgesIterator(u);
			while (it2.hasNext()) {
				Arc a = it2.next();
				v = ug.getNeighbourNode(u, a);
				if (alreadyUsed.contains(a)) {
					toSee.add(v);
					continue;
				}
				if (seen.contains(v))
					continue;
				Arc b = dg.addDirectedEdge(u, v);
				sdi.setCost(b, sui.getIntCost(a));
				toSee.add(v);

			}

			seen.add(u);
		}

		return sdi;

	}

	/** 
	 * From a undirected steiner instance sui build and return a BiDirected Steiner instance sdi with same optimal cost
	 * as sui.
	 * <p>
	 * sdi contains all the nodes in sui and for each edge in sui, sdi contains 2 opposite arcs with same cost.
	 * <p>
	 * As a consequence, the cost of the optimale solution is the same in sui and sdi.
	 */
	public static SteinerDirectedInstance getSymetrizedGraphFromUndirectedInstance(
			SteinerUndirectedInstance sug) {
		DirectedGraph dg = new DirectedGraph();
		SteinerDirectedInstance sdg = new SteinerDirectedInstance(dg);

		Iterator<Integer> it = sug.getGraph().getVerticesIterator();
		Integer v, r = sug.getRandomRequiredVertice();
		while (it.hasNext()) {
			v = it.next();
			dg.addVertice(v);
			sdg.setRequired(v, sug.isRequired(v));
			if (v.equals(r)) {
				sdg.setRoot(v);
				if (sug.getNumberOfRequiredVertices() != 1)
					sdg.setRequired(v, false);
			}
		}

		Iterator<Arc> it2 = sug.getGraph().getEdgesIterator();
		Arc a;
		Integer u;
		while (it2.hasNext()) {
			a = it2.next();
			u = a.getInput();
			v = a.getOutput();

			Arc b1 = dg.addDirectedEdge(u, v);
			Arc b2 = dg.addDirectedEdge(v, u);

			sdg.setCost(b1, sug.getIntCost(a));
			sdg.setCost(b2, sug.getIntCost(a));
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
			s.append(a).append(" ").append(getIntCost(a)).append("\n");
		}

		return s.toString();
	}

	@Override
	public boolean hasSolution() {
		ListIterator<Integer> it = this.getRequiredVerticesIterator();
		while (it.hasNext()) {
			if (!graph.areConnectedByDirectedPath(root, it.next()))
				return false;
		}
		return true;
	}
	
	public boolean isFeasibleSolution(HashSet<Arc> tree){
		
		HashMap<Integer, HashSet<Integer>> adj = new HashMap<Integer, HashSet<Integer>>();
		for(Arc a : tree){
			Integer u = a.getInput();
			Integer v = a.getOutput();
			HashSet<Integer> neigh = adj.get(u);
			if(neigh == null)
			{
				neigh = new HashSet<Integer>();
				adj.put(u,neigh);
			}
			neigh.add(v);
		}
		
		LinkedList<Integer> toCheck = new LinkedList<Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();
		toCheck.add(this.getRoot());
		
		while(!toCheck.isEmpty()){
			Integer u = toCheck.pollFirst();
			if(visited.contains(u))
				continue;
			visited.add(u);
			
			HashSet<Integer> neigh = adj.get(u);
			if(neigh == null)
			{
				neigh = new HashSet<Integer>();
			}
			toCheck.addAll(neigh);
		}
		return visited.containsAll(this.getRequiredVertices());
		
	}

}
