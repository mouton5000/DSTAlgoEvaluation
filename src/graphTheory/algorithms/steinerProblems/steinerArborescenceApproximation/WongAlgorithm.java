package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.algorithms.minimumBranching.EdmondChuLiuIterAlgorithm;
import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.spanningTree.MinimumBranchingArborescenceInstance;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Heuristic for Steiner Directed Tree problem with no performance guarantee.
 * 
 * Source : The algorithm is described in R. Wong,
 * "A dual ascent approach for Steiner Tree Problems on a directed graph". But
 * the source used was the description in M. P. de Aragao :
 * "Dual Heuristics on the Exact Solution of Large Steiner problems".
 * 
 * The algorithm search, from the terminals, for a path to the root by
 * saturating arcs. Each arc is associated with a reduced cost initiated to the
 * steiner cost of the arc. An arc with a 0 reduced cost is saturated. A
 * component is one of the maximal (not necessarily strongly) connected
 * component reaching one or more terminals with saturated arcs. At the
 * begining, if the graph do not contains arcs with cost 0, the first components
 * are the singletons containing each terminals.
 * 
 * Each turn, we consider every components not containing the root. If there is
 * no, we build a tree with the saturated arcs. If there are some, we compute
 * the minimum reduced cost C over all the entering arcs over all the components
 * (an entering arc is an arc with input outside and output inside). If a is the
 * arc entering the component Comp and is choosed, we then reduce the reduced
 * cost of every of the comp entering arcs by C (at least one arc become
 * saturated). And we continue with the new components until each terminals is
 * reached by the root with saturated arcs.
 * 
 * Notice that each turn, we add in fact C to the dual solution of this steiner
 * problem. As a consequence, we easily get a lower bound to the Steiner
 * problem. This lower bound is registered as an output of the algorithm. The
 * dual solution is also registered.
 * 
 * Finally one can initiate the dual solution to get an other solution.
 * 
 * 
 * 
 * @author Watel Dimitri
 * 
 */
public class WongAlgorithm extends SteinerArborescenceApproximationAlgorithm {

	protected HashMap<HashSet<Integer>, Double> dualInitialization;
	protected HashMap<HashSet<Integer>, Double> cut;

	protected Double dualCost;

	public WongAlgorithm() {
		super();
	}

	public void setDualInitialization(
			HashMap<HashSet<Integer>, Double> dualInitialization) {
		this.dualInitialization = dualInitialization;
	}

	public Double getDualcost() {
		return this.dualCost;
	}

	public HashMap<HashSet<Integer>, Double> getCut() {
		return this.cut;
	}

	/**
	 * Comparator to compare arc costs.
	 */
	private Comparator<Arc> comp = new Comparator<Arc>() {

		@Override
		public int compare(Arc o1, Arc o2) {
			Double i1 = reducedCost.get(o1);
			Double i2 = reducedCost.get(o2);
			if (i1 == i2)
				return 0;
			if (i1 == null)
				return -1;
			else if (i2 == null)
				return 1;
			else
				return i1.compareTo(i2);
		}

	};

	/**
	 * Costs of the arcs described by the variable "d" in Wong
	 * "A dual ascent approach for Steiner Tree problems on a directed Graph"
	 */
	private HashMap<Arc, Double> reducedCost;

	/**
	 * For each couple of nodes of index i and j, connections[i](j] is true if
	 * there is a path from the first to the second node in the auxilliary
	 * graph.
	 */
	private boolean[][] connections;

	private HashMap<Integer, Integer> nodeIndexes;

	private boolean areConnected(int i1, int i2) {
		return connections[i1][i2];
	}

	private Double tempDualCost;
	private HashMap<HashSet<Integer>, Double> tempCut;

	@Override
	protected void computeWithoutTime() {

		// Step 0
		DirectedGraph gp = new DirectedGraph(); // Auxilliary graph G'
		reducedCost = new HashMap<Arc, Double>(); // S

		tempDualCost = 0D;
		tempCut = new HashMap<HashSet<Integer>, Double>();
		// init auxilliary graph and connections

		nodeIndexes = new HashMap<Integer, Integer>();
		int s = instance.getGraph().getNumberOfVertices();
		connections = new boolean[s][s];
		for (int i = 0; i < s; i++)
			Arrays.fill(connections[i], false);

		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		int index = 0;
		while (it.hasNext()) {
			Integer n = it.next();
			gp.addVertice(n);

			connections[index][index] = true;
			nodeIndexes.put(n, index++);
		}

		Iterator<Arc> it2 = instance.getGraph().getEdgesIterator();
		while (it2.hasNext()) {
			Arc a = it2.next();
			reducedCost.put(a, (double) instance.getIntCost(a));
		}

		initWithDualInit();

		// Step 1
		HashSet<Integer> h = findRootComp(gp);
		while (h != null) {
			Arc a = findMinArc(gp, h); // (i*,j*)
			editCosts(gp, h, a); // Step 2
			addArc(gp, a); // step 3

			// Back to step 1
			h = findRootComp(gp);
		}

		setSolution(gp);
	}

	private void initWithDualInit() {
		if (dualInitialization == null)
			return;

		for (HashSet<Integer> comp : dualInitialization.keySet()) {
			Double dualValue = dualInitialization.get(comp);
			tempDualCost += dualValue;
			for (Integer n : comp) {
				Iterator<Arc> it = instance.getGraph().getInputArcsIterator(n);
				while (it.hasNext()) {
					Arc a = it.next();
					if (comp.contains(a.getInput()))
						continue;
					reducedCost.put(a, reducedCost.get(a) - dualValue);
				}
			}
		}

		tempCut.putAll(dualInitialization);
	}

	/**
	 * Step 3, add arc a = (i*,j*) to gp. Edit connections.
	 * 
	 * @param gp
	 * @param a
	 */
	private void addArc(DirectedGraph gp, Arc a) {
		Integer u = a.getInput();
		Integer v = a.getOutput();

		gp.addDirectedEdge(u, v);

		int iu = nodeIndexes.get(u);
		int iv = nodeIndexes.get(v);

		int s = instance.getGraph().getNumberOfVertices();
		for (int i = 0; i < s; i++)
			for (int j = 0; j < s; j++)
				if (areConnected(i, iu) && areConnected(iv, j))
					connections[i][j] = true;
	}

	/**
	 * Set the solution to any directed spanning tree of gp rooted in the
	 * instance root.
	 * 
	 * @param gp
	 */
	private void setSolution(DirectedGraph gp) {

		int ir = nodeIndexes.get(instance.getRoot());

		Iterator<Integer> it = gp.getVerticesIterator();
		HashSet<Integer> toRemove = new HashSet<Integer>();
		while (it.hasNext()) {
			Integer n = it.next();
			int index = nodeIndexes.get(n);
			if (!areConnected(ir, index))
				toRemove.add(n);
		}

		for (Integer n : toRemove)
			gp.removeVertice(n);

		MinimumBranchingArborescenceInstance mbai = new MinimumBranchingArborescenceInstance(
				gp);

		mbai.setRoot(instance.getRoot());
		mbai.setCosts(instance.getCosts());

		EdmondChuLiuIterAlgorithm ecl = new EdmondChuLiuIterAlgorithm();
		ecl.setInstance(mbai);
		ecl.compute();

		HashSet<Arc> sol = ecl.getArborescence();
		pruneTree(sol);

		arborescence = sol;

		cost = 0;
		for (Arc a : arborescence)
			cost += instance.getIntCost(a);

		dualCost = tempDualCost;
		cut = tempCut;
	}

	private void pruneTree(HashSet<Arc> sol) {
		// Remove useless arcs
		boolean doItAgain;
		do {
			doItAgain = false;
			Iterator<Arc> it = sol.iterator();
			while (it.hasNext()) {
				Arc a = it.next();
				Integer v = a.getOutput();
				if (instance.isRequired(v))
					continue;

				boolean leaf = true;
				Iterator<Arc> it2 = instance.getGraph()
						.getOutputArcsIterator(v);
				while (it2.hasNext()) {
					Arc b = it2.next();
					if (sol.contains(b)) {
						leaf = false;
						break;
					}
				}

				if (leaf) {
					it.remove();
					doItAgain = true;
				}
			}
		} while (doItAgain);
	}

	/**
	 * Step 2 in Wong
	 * "A dual ascent approach for Steiner Tree problems on a directed Graph".
	 * Edit gp. h is a root component of gp. a = (i*,j*) is the minimum cost arc
	 * entering h. gp contains a path from j* to i*.
	 * 
	 * @param gp
	 * @param h
	 * @param a
	 */
	private void editCosts(DirectedGraph gp, HashSet<Integer> h, Arc a) {

		double minCost = reducedCost.get(a);
		tempDualCost += minCost;
		tempCut.put(h, minCost);

		for (Integer n : h) {

			Iterator<Arc> it = instance.getGraph().getInputArcsIterator(n);
			while (it.hasNext()) {
				Arc b = it.next();
				if (h.contains(b.getInput()))
					continue;
				double cost = reducedCost.get(b);
				reducedCost.put(b, cost - minCost);
			}
		}
	}

	/**
	 * Find the minimum cost arc entering h which is not in gp.
	 * 
	 * @param gp
	 * @param h
	 * @return
	 */
	private Arc findMinArc(DirectedGraph gp, HashSet<Integer> h) {

		Arc aMin = null;

		for (Integer n : h) {
			Iterator<Arc> it = instance.getGraph().getInputArcsIterator(n);
			while (it.hasNext()) {
				Arc a = it.next();
				if (gp.contains(a))
					continue;
				if (aMin == null || comp.compare(aMin, a) > 0)
					aMin = a;
			}
		}

		return aMin;
	}

	/**
	 * Find a root component in gp. ie a maximal strongly connected component of
	 * gp which cannot be reached neither from the root nor any other node which
	 * is not is that component. There cannot be any node n which can reach the
	 * component such that no node of the component can reach n. That component
	 * do not contains the root.
	 * 
	 * 
	 * @param gp
	 * @return
	 */
	private HashSet<Integer> findRootComp(DirectedGraph gp) {
		Iterator<Integer> it = instance.getRequiredVerticesIterator();
		Integer n = null;

		HashSet<Integer> rootComponent = new HashSet<Integer>();

		int ir = nodeIndexes.get(instance.getRoot());
		// Find node for which neither root nor any other node dangles from.
		boucle1: while (it.hasNext()) {
			rootComponent.clear();
			Integer n1 = it.next();
			if (n1.equals(instance.getRoot()))
				continue;

			int i1 = nodeIndexes.get(n1);
			if (areConnected(ir, i1))
				continue;

			rootComponent.add(n1);
			Iterator<Integer> it2 = instance.getRequiredVerticesIterator();

			while (it2.hasNext()) {
				Integer n2 = it2.next();
				if (n2.equals(instance.getRoot()) || n2.equals(n1))
					continue;

				int i2 = nodeIndexes.get(n2);
				boolean con21 = areConnected(i2, i1);
				boolean con12 = areConnected(i1, i2);
				if (con21 && !con12)
					continue boucle1;
				else if (!con21)
					continue;
				rootComponent.add(n2);
			}

			it2 = gp.getVerticesIterator();
			while (it2.hasNext()) {
				Integer n2 = it2.next();
				if (n2.equals(instance.getRoot()) || rootComponent.contains(n2))
					continue;
				int i2 = nodeIndexes.get(n2);
				if (areConnected(i2, i1))
					rootComponent.add(n2);
			}

			n = n1;
			break;
		}
		if (n == null)
			return null;

		return rootComponent;
	}

	@Override
	protected void setNoSolution() {
		dualCost = -1D;
		cut = null;
		super.setNoSolution();
	}

}
