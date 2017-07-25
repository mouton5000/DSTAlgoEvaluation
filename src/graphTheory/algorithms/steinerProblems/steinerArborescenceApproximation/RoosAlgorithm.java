package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.algorithms.shortestDistance.arcCost.ArcDijkstraOneDestinationAlgorithm;
import graphTheory.algorithms.shortestDistance.arcCost.ArcDijkstraOneSourceAlgorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.shortestPath.ArcShortestPathOneDestinationInstance;
import graphTheory.instances.shortestPath.ArcShortestPathOneSourceInstance;
import graphTheory.utils.Couple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * 
 * Implementation of the Roos modified algorithm. Source :
 * "FasterDSP: A faster approximation algorithm for directed Steiner tree problem"
 * ; by Hsieh, Ming-I et Al.
 * <p>
 * It is a faster implementation for the Charikar and Al. algorithm from
 * "Approximation Algorithms for Directed Steiner Tree Problems" when the height
 * parameter is equals to 2.
 * <p>
 * This algorithm returns a feasible solution for the Directed Steiner Tree
 * problem. It is a greedy algorihm which uses a sub-algorihthm named CH2
 * designed to returns a feasible solution for the minimum Density Directed
 * Steiner Tree problem: it searches for a tree rooted in the root of the
 * instance which spans a part of the terminal, such that the height of the tree
 * is at most 2. It try to minimize the total cost of the tree divided by the
 * number of terminals.
 * <p>
 * The roos algorithm uses CH2 to span some terminals, delete those terminals,
 * and repeat CH2 to span other terminals. It continues until all the terminals
 * are reached.
 * <p>
 * Notice CH2 search for the tree of height 2 in the shortest path graph : the
 * complete directed graph where the wieght of the arc is the shortest path cost
 * in the original graph.
 * <p>
 * 
 * @author Watel Dimitri
 * 
 */
public class RoosAlgorithm extends SteinerArborescenceApproximationAlgorithm {

	/**
	 * Copy of the costs of the instance. It let the algorithm modify the costs
	 * withouth modifying the instance it self.
	 */
	private HashMap<Arc, Integer> costs;

	/**
	 * This map associates each couple of node (represented by an arc in the
	 * original graph) to a list of arcs in the original graphs corresponding to
	 * the shortest path between the two nodes.
	 * <p>
	 * Notice that not all the couple of nodes are registered, indeed, we just
	 * need the couple (root, v) for each node v, and (v,t) for each node v and
	 * each terminal t.
	 */
	private HashMap<Arc, List<Arc>> shortestPaths;

	/**
	 * This map associates for each node v the list of terminals sortest by
	 * distance from v : the cost of the shortest path from v to that terminal.
	 */
	private HashMap<Integer, TreeSet<Integer>> sortedRequiredVertices;
	
	@Override
	protected void computeWithoutTime() {	
		
		// We first compute all the shortest paths
		initShortestPaths();

		// Then we sort for each node v the list of terminals by the distance from v
		sortedRequiredVertices = new HashMap<Integer, TreeSet<Integer>>();
		sortRequiredVertices();
		
		// Copy of the terminals
		HashSet<Integer> req = new HashSet<Integer>(
				instance.getRequiredVertices());

		// Union of all the solutions returned by the subalgorithm CH2
		HashSet<Arc> currentSol = new HashSet<Arc>();

		// Until all terminals are reached
		while (req.size() > 0) {

			// We apply CH2 to reach some of the non reached terminals
			Couple<HashSet<Arc>, HashSet<Integer>> tree = applyCH2(req);

			// tree.first the tree returned by CH2
			// tree.second is the terminals of that tree

			currentSol.addAll(tree.first);
			for (Arc a : tree.first)
				costs.put(a, 0); // Set the cost of the arc to 0, as this arc is already used in the solution, it does not cost anything to use it again.
			req.removeAll(tree.second);
		}

		// Compute the returned solution and its cost.
		arborescence = new HashSet<Arc>();
		int c = 0;
		for (Arc a : currentSol) {
			List<Arc> l = shortestPaths.get(a);
			if (a.getInput().equals(a.getOutput()))
				l = new ArrayList<Arc>();
			for (Arc b : l) {
				arborescence.add(b);
				c += instance.getIntCost(b);
			}
		}
		cost = c;
	}

	/**
	 * Compute all the shortest paths from the root and to all terminals.
	 */
	private void initShortestPaths() {
		costs = new HashMap<Arc, Integer>();
		shortestPaths = new HashMap<Arc, List<Arc>>();

		initShortestPathsFromRoot();
		initShortestPathsToRequiredVertices();
	}

	/**
	 * Compute all the shortest paths from the root and registered them in the
	 * {@link #shortestPaths} map. Register also their cost in the
	 * {@link #costs} map.
	 */
	private void initShortestPathsFromRoot() {

		Integer root = instance.getRoot();

		// Create a instance of the problem searching for all the shortest path
		// from a single node
		ArcShortestPathOneSourceInstance ash = new ArcShortestPathOneSourceInstance(
				instance.getGraph());
		ash.setCosts(instance.getCosts(true));
		ash.setSource(root);

		// Use the dijkstra algorithm to solve that instance
		ArcDijkstraOneSourceAlgorithm adij = new ArcDijkstraOneSourceAlgorithm();
		adij.setInstance(ash);
		adij.setComputeOnlyCosts(false);
		adij.compute();

		HashMap<Integer, Integer> aDijCosts = adij.getCosts();
		HashMap<Integer, List<Arc>> aDijPaths = adij.getShortestPaths();

		// Register all the shortest paths and their cost.
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while (it.hasNext()) {
			Integer v = it.next();
			Arc a = new Arc(root, v, true);
			costs.put(a, aDijCosts.get(v));
			shortestPaths.put(a, aDijPaths.get(v));

		}
	}

	/**
	 * Compute all the shortest paths to all terminals and registered them in
	 * the {@link #shortestPaths} map. Register also their cost in the
	 * {@link #costs} map.
	 */
	private void initShortestPathsToRequiredVertices() {
		// Create an instance searching for all the shortest path to a single destination
		ArcShortestPathOneDestinationInstance ash = new ArcShortestPathOneDestinationInstance(
				instance.getGraph());
		ash.setCosts(instance.getCosts(false));
		// for now we do not specify that single instance

		// Create a dijstra algorithm to solve that instance
		ArcDijkstraOneDestinationAlgorithm adij = new ArcDijkstraOneDestinationAlgorithm();
		adij.setInstance(ash);
		adij.setComputeOnlyCosts(false);

		
		// For each terminal...
		Iterator<Integer> it = instance.getRequiredVerticesIterator();
		while (it.hasNext()) {
			Integer term = it.next();

			// ... adapt the instance so that the single destination is that instance
			ash.setDestination(term);
			// ... and compute the dijkstra algorithm over that instance
			adij.compute();

			HashMap<Integer, Integer> aDijCosts = adij.getCosts();
			HashMap<Integer, List<Arc>> aDijPaths = adij.getShortestPaths();

			// Register all the shortest paths and their cost.
			Iterator<Integer> it2 = instance.getGraph().getVerticesIterator();
			while (it2.hasNext()) {
				Integer v = it2.next();
				Arc a = new Arc(v, term, true);
				costs.put(a, aDijCosts.get(v));
				shortestPaths.put(a, aDijPaths.get(v));
			}

		}
	}

	/**
	 * For each node v, sort the list of terminals by distance from v
	 */
	private void sortRequiredVertices() {
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while (it.hasNext())
			sortRequiredVertices(it.next());
	}

	/**
	 * Sort the list of terminals by distance from v
	 * 
	 * @param v
	 */
	private void sortRequiredVertices(Integer v) {
		Iterator<Integer> it = instance.getRequiredVerticesIterator();

		// We represent the list as a TreeSet, with a specific comparator to compare to distance from v
		// of each terminal
		TreeSet<Integer> ts = new TreeSet<Integer>(
				getRequiredVerticesComparator(v));
		while (it.hasNext()) {
			Integer t = it.next();
			ts.add(t);
		}

		// Register the list
		sortedRequiredVertices.put(v, ts);
	}

	/**
	 * 
	 * @param v
	 * @return a comparator which compares two terminals by distance from v: the
	 *         closest terminals to v is the lowest.
	 */
	private Comparator<Integer> getRequiredVerticesComparator(final Integer v) {
		return new Comparator<Integer>() {

			@Override
			public int compare(Integer t1, Integer t2) {
				Integer i1 = costs.get(new Arc(v, t1, true));
				Integer i2 = costs.get(new Arc(v, t2, true));
				if (i1 == i2)
					return t1.compareTo(t2);
				else if (i1 == null)
					return -1;
				else if (i2 == null)
					return 1;

				int comp = i1.compareTo(i2);
				if (comp != 0)
					return comp;
				else
					return t1.compareTo(t2);
			}
		};
	}

	/**
	 * @param
	 * @return a tree rooted in the root of the instance spanning a part of the
	 *         terminals in req, and the set of those terminals.
	 */
	private Couple<HashSet<Arc>, HashSet<Integer>> applyCH2(HashSet<Integer> req) {

		// tBest contains the current best tree
		HashSet<Arc> tBest = null, t;

		// dBest contains the density of the tree tBest (cost divided by number of terminals
		Double dBest = Double.POSITIVE_INFINITY;

		// xBest contains all the temrinals reached by tBest.
		HashSet<Integer> xBest = null;

		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		Integer root = instance.getRoot();

		// For each node, we test the trees (root,v) U (v,X') for X' a subset of terminals.
		// Notice we do not test all the sets X', but only a usefull part so that the tree
		// is the minimum density tree among the list of all trees going through v
		while (it.hasNext()) {
			Integer v = it.next();

			// The possibly returned tree
			t = new HashSet<Arc>();
			Arc rv;
			Integer c;

			// The first part of the root : the arc (root,v)
			if (root.equals(v)) {
				// if v is the root, then the arc (root,v) is empty and costs 0
				c = 0;
			} else {
				// if not, we add the arc to the current tested solution
				// and register its cost
				rv = new Arc(instance.getRoot(), v, true);
				t.add(rv);
				Integer crv = costs.get(rv);
				if (crv == null)
					continue;
				else
					c = crv;
			}

			// d will contain the current density of t
			// while d decreases we continue, and when d stops decreasing, we 
			// immediately stops the loop : we found the minimum density tree
			// going thourgh v
			double d = Double.POSITIVE_INFINITY;

			// x is the set of terminals of the current tree t
			HashSet<Integer> x = new HashSet<Integer>();

			// kp is the size of x
			int kp = 0;

			// We look at the terminals term_1, term_2, term_3, ... ordered by distances from v
			// For each terminal term_i, we look at the tree (root,v) U (v,term_1) U (v, term_2) ... U (v,term_i)
			// we continue until the density of the tree decreases
			// because we know that when the density starts to increase, the previous tree was the best density tree
			// going through v
			for (Integer term : sortedRequiredVertices.get(v)) {
				if (!req.contains(term))
					continue;

				kp++;

				Arc a = null;
				boolean b;
				if (b = !term.equals(v)) {
					// If v is term, then the arc (v, term) is empty
					a = new Arc(v, term, true);
					Integer ca = costs.get(a);
					if (ca == null)
						break;
					else
						c += ca;
				}

				// density of the current tree
				double d2 = ((double) c) / kp;
				if (d2 >= d)
					// if the density of the current tree is greater than the density of the previous tree, we stop
					break;
				// else we continue, with this tree as the lowest density tree
				d = d2;
				x.add(term);
				if (b)
					t.add(a);
			}

			// we registrer the best tree going through v if this tree has a better density than tBest.
			if (d < dBest) {
				tBest = t;
				xBest = x;
				dBest = d;
			}
		}
		if (tBest == null)
			return null;
		return new Couple<HashSet<Arc>, HashSet<Integer>>(tBest, xBest);
	}

}
