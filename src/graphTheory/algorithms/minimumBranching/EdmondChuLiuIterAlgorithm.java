package graphTheory.algorithms.minimumBranching;

import graphTheory.graph.Arc;
import graphTheory.graph.Graph;
import graphTheory.utils.Couple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The Edmond algorithm, or the Chu Liu Algorithm, compute in a directed graph a
 * minimum branching tree, equivalent to the minimum spanning tree in a directed
 * graph. Given a root r and weight on the arcs, the algorithm compute the
 * minimum weighted directed tree rooted in r spanning every nodes of the graph.
 * 
 * This algorithm is similar to the Kruskal algorithm.
 * 
 * Every node choose an input arc with minimum cost. If the union of all those
 * arcs is a tree rooted in r, return the tree. Otherwise there is a cycle.
 * 
 * Replace that cycle by a node vc. Every input arc a = (u,v) in the cycle
 * becomes an input arc of vc with modified cost. The modified cost is equals to
 * the cost of a minus the cost of v chosen arc (belonging to the cycle). Every
 * output arc of the cycle becomes an output arc of vc with same cost. Every arc
 * where input and output belongs to the cycle does not appear in that new
 * graph.
 * 
 * Restart the algorithm on that new graph, until there is no cycle.
 * 
 * Once a tree T is returned, backtrack on every contracted cycle. Replace the
 * node vc by the corresponding cycle. Add every arc of the cycle. Every arc of
 * T incomming or outgoing from vc is replaced by the corresponding arc
 * incomming or outgoing from the cycle. If an incomming arc (u,v) is added to
 * T, then remove the v choosen arc from the cycle and T.
 * 
 * Contrary to the other verion of Edmond Chu Liu, this version is iterative.
 * But it modifies the graph during computation by adding the contracted nodes
 * and then deleting them at the end of the algorithm.
 * 
 * @author Watel Dimitri
 * 
 * @see #EdmongChuLiuAlgorithm
 */
public class EdmondChuLiuIterAlgorithm extends MinimumBranchingAlgorithm {
	
	/**
	 * Comparator to compare arc costs.
	 */
	private Comparator<Arc> comp = new Comparator<Arc>() {

		@Override
		public int compare(Arc o1, Arc o2) {
			Integer i1 = getCost(o1);
			Integer i2 = getCost(o2);
			if (i1 == null)
				return -1;
			else if (i1.equals(i2))
				return 0;
			else if (i2 == null)
				return 1;
			else
				return i1.compareTo(i2);
		}

	};

	/**
	 * Costs of the arcs added during the algorithm (linked to the contracted
	 * nodes)
	 */
	private HashMap<Arc, Integer> virtCost;

	/**
	 * @return the cost of the arc a, even if a was added during the algorithm.
	 */
	private Integer getCost(Arc a) {
		Integer i = virtCost.get(a);
		if (i == null)
			i = instance.getIntCost(a);
		return i;
	}

	/**
	 * Map containing for each node the input arc of minimum cost.
	 */
	private HashMap<Integer, Arc> arcOfNode;

	@Override
	protected void computeWithoutTime() {

		arcOfNode = new HashMap<Integer, Arc>();
		contractions = new LinkedList<Couple<Integer, HashSet<Integer>>>();
		arcContractionLink = new HashMap<Arc, Arc>();
		virtCost = new HashMap<Arc, Integer>();

		HashSet<Arc> h = new HashSet<Arc>();
		markEdges(h);
		HashSet<Integer> cycle;
		while (true) {

			cycle = findCycle(h);
			if (cycle == null)
				break;

			contractCycle(cycle);

			h.clear();
			markEdges(h);
		}

		returnCyclesBack(h);

		cost = 0;
		arborescence = h;
		for (Arc a : arborescence)
			cost += instance.getIntCost(a);

	}

	/**
	 * Id used to add new nodes to the graph.
	 */
	private int maxId = -1;

	/**
	 * Fill h with every input arc of minimum cost for each node. arcOfNode is
	 * modified.
	 * 
	 * @param h
	 */
	private void markEdges(HashSet<Arc> h) {
		for (Integer v : instance.getGraph().getVertices()) {
			maxId = Math.max(maxId, v + 1);
			if (v.equals(instance.getRoot()))
				continue;
			markEdge(h, v);
		}
	}

	/**
	 * Add to h the input arc of minimum cost of v. arcOfNode is modified.
	 */
	private void markEdge(HashSet<Arc> h, Integer v) {
		Arc b = Collections.min(instance.getGraph().getInputArcs(v), comp);
		h.add(b);
		arcOfNode.put(v, b);
	}

	/**
	 * @param h
	 * @return one cycle of the induced graph by h.
	 */
	private HashSet<Integer> findCycle(HashSet<Arc> h) {
		Graph g = instance.getGraph().getInducedGraphFromArc(h);
		ArrayList<Integer> ar = g.getOneDirectedCycle();
		if (ar == null)
			return null;
		HashSet<Integer> cycle = new HashSet<Integer>();
		for (Integer v : ar)
			cycle.add(v);
		return cycle;
	}

	/**
	 * Map linking every arc entering or outgoing from a contraction node to the
	 * corresponding arc entering or outgoing from the corresponding cycle.
	 */
	private HashMap<Arc, Arc> arcContractionLink;

	/**
	 * List of couples linking every contraction node to the corresponding
	 * cycle.
	 */
	private LinkedList<Couple<Integer, HashSet<Integer>>> contractions;

	/**
	 * Virtually remove every node and arc in the cycle, and add a new node
	 * replacing the cycle. Each ingoing or outgoing arc in or out the cycle are
	 * copied as an arc ingoing or outgoing this new node.
	 */
	private void contractCycle(HashSet<Integer> cycle) {
		instance.getGraph().addVertice(maxId);
		int contractNode = maxId;
		maxId++;

		setContractionInputs(contractNode, cycle);
		setContractionOutputs(contractNode, cycle);

		removeCycle(cycle);

		contractions.addFirst(new Couple<Integer, HashSet<Integer>>(
				contractNode, cycle));
	}

	/**
	 * Virtually remove every node of the cycle.
	 * 
	 * @param cycle
	 */
	private void removeCycle(HashSet<Integer> cycle) {
		for (Integer n : cycle)
			instance.getGraph().virtuallyRemoveVertice(n);

	}

	/**
	 * Add for each input arc (u,v) of the cycle an input arc of contractInteger
	 * with modified cost : cost(u,v) - cost(v input arc in the cycle). If
	 * multiple arcs (u,*) enter the cycle, chose the one with minimum modified
	 * cost.
	 * 
	 * @param contractNode
	 * @param cycle
	 */
	private void setContractionInputs(Integer contractNode,
			HashSet<Integer> cycle) {
		HashMap<Integer, Arc> inputs = new HashMap<Integer, Arc>();
		HashMap<Integer, Integer> inputsC = new HashMap<Integer, Integer>();

		Arc a;
		Iterator<Arc> it;
		for (Integer n : cycle) {
			Arc aon = arcOfNode.get(n);
			it = instance.getGraph().getInputArcsIterator(n);
			while (it.hasNext()) {
				a = it.next();
				Integer input = a.getInput();
				if (cycle.contains(input))
					continue;
				int relCostA = getCost(a) - getCost(aon);
				Arc b = inputs.get(input);
				if (b != null) {
					int relCostB = inputsC.get(input);
					if (relCostA < relCostB) {
						inputs.put(input, a);
						inputsC.put(input, relCostA);
					}
				} else {
					inputs.put(input, a);
					inputsC.put(input, relCostA);
				}
			}
		}
		for (Integer input : inputs.keySet()) {
			Arc b = instance.getGraph().addDirectedEdge(input, contractNode);
			arcContractionLink.put(b, inputs.get(input));
			virtCost.put(b, inputsC.get(input));
		}
	}

	/**
	 * Add for each output arc (v,u) of the cycle an output arc of contractNode
	 * with same cost. If multiple arcs (*,u) out from the cycle, chose the one
	 * with minimum cost.
	 * 
	 * @param contractNode
	 * @param cycle
	 */
	private void setContractionOutputs(Integer contractNode,
			HashSet<Integer> cycle) {
		HashMap<Integer, Arc> outputs = new HashMap<Integer, Arc>();
		HashMap<Integer, Integer> outputsC = new HashMap<Integer, Integer>();

		Arc a;
		Iterator<Arc> it;
		for (Integer n : cycle) {

			it = instance.getGraph().getOutputArcsIterator(n);
			while (it.hasNext()) {
				a = it.next();
				Integer output = a.getOutput();
				if (cycle.contains(output))
					continue;

				int costA = getCost(a);
				Arc b = outputs.get(output);
				if (b != null) {
					int costB = outputsC.get(output);
					if (costA < costB) {
						outputs.put(output, a);
						outputsC.put(output, costA);
					}
				} else {
					outputs.put(output, a);
					outputsC.put(output, costA);
				}

			}

		}
		for (Integer output : outputs.keySet()) {
			Arc b = instance.getGraph().addDirectedEdge(contractNode, output);
			arcContractionLink.put(b, outputs.get(output));
			virtCost.put(b, outputsC.get(output));
		}
	}

	/**
	 * Backtrack on every contracted cycle to : - remove the added contraction
	 * node vc - replace every arc entering or outgoing from vc by the
	 * corresponding arc entering or outgoing from the cycle
	 * 
	 * @param h
	 */
	private void returnCyclesBack(HashSet<Arc> h) {
		for (Couple<Integer, HashSet<Integer>> contraction : contractions) {
			Integer contractNode = contraction.first;
			HashSet<Integer> cycle = contraction.second;

			Iterator<Arc> it = instance.getGraph().getInputArcsIterator(
					contractNode);
			while (it.hasNext()) {
				Arc b = it.next();
				if (!h.contains(b))
					continue;
				Arc a = arcContractionLink.remove(b);
				arcOfNode.put(a.getOutput(), a);
				h.remove(b);
				break;
			}

			it = instance.getGraph().getOutputArcsIterator(contractNode);
			while (it.hasNext()) {
				Arc c = it.next();
				if (h.remove(c))
					h.add(arcContractionLink.get(c));
			}

			instance.getGraph().removeVertice(contractNode);

			for (Integer n : cycle) {
				instance.getGraph().cancelVirtuallyRemoveVertice(n);
				h.add(arcOfNode.get(n));
			}
		}
	}

}
