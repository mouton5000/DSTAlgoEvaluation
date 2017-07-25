package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.graph.Arc;
import graphTheory.utils.Couple;
import graphTheory.utils.CustomFibonacciHeap;
import graphTheory.utils.CustomFibonacciHeapNode;
import graphTheory.utils.DoubleBoolean;
import graphTheory.utils.TreeIterator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * 
 * Faster Implementation of the G_F_Triangle algorithm.
 * <p>
 * This algorithm returns a feasible solution for the Directed Steiner Tree
 * problem. It is a greedy algorihm which uses a sub-algorihthm named FLAC.
 * <p>
 * FLAC is designed to returns a feasible solution for the minimum Density
 * Directed Steiner Tree problem: it searches for a tree rooted in the root of
 * the instance which spans a part of the terminal. It try to minimize the total
 * cost of the tree divided by the number of terminals. 
 * <p>
 * To span the terminals, FLAC grows a flow from the terminals: we call them
 * sources. That flow is send through the entering arcs, with a flow rate equals
 * to 1 L/s, considering the volume of an arc is its weight. Then, when an arc
 * is full (we say saturated), the source feeds its entering arcs. If two
 * sources reach the same arc with a path of saturated arcs, the flow rate is 2
 * L/s. And if more sources reaches the arc, the flow rate increases again: the
 * flow rate inside an arc is equals to the number of sources it can reach with
 * paths of saturated arcs
 * <p>
 * FLAC continue until the root is reached, and we return the set of saturated
 * arcs linked to the root. As we want to return a tree, each time an arc is
 * saturated, if a node is connected to the same source with two paths of
 * saturated arcs, we delete that last saturated arc.
 * <p>
 * The previous implementation of G_F_Triangle first builds the closure metric of the graph where each arc (u,v) is associated with a shortest path linking u to v. 
 * It then works exclusively in that new graph. It uses FLAC to span some terminals, delete those terminals, and repeat FLAC
 * to span other terminals. It continues until all the terminals are reached. At the end, each arc in the solution is replaced by the shortest path associated with.
 * <p>
 * This version of G_F_Triangle does not build the closure metric at the beggining. The costs are initialized with +infinity for each arc which does not appear in the original graph.
 * And, when necessary, it uses a Roy-Floyd-Warshall-like operation to decrease the weights some arcs. The main property of this version is that the cost of any saturated arc at any iteration is always the cost of a shortest path in the original graph.
 *     
 * 
 * @author Watel Dimitri
 * 
 */
public class GFLACTR2Algorithm extends SteinerArborescenceApproximationAlgorithm {

	// --------------------   Directed Steiner Tree Part --------------------//


	/**
	 * Copy of the costs of the instance. Each arc is here represented by a couple of nodes (~ integers).
	 * As this algorithm works in the complete metric closure  graph, all the couples exist in this map, except when the first node
	 * is equal to the second one. 
	 * <p>
	 * This map lets the algorithm modify the costs without modifying the instance itself.
	 * <p>
	 * Note that, for an couple c, the cost of c is initialized if and only if we need it.
	 */
	private HashMap<Couple<Integer,Integer>, Integer> costs;

	/**
	 * For each couple of nodes (u,v), contains, at the end of the algorithm, the ordered list of arcs 
	 * in a shortest path from u to v in the original graph.
	 */
	private HashMap<Couple<Integer,Integer>, TreeIterator<Arc>> shortestPath;

	/**
	 * Copy of the required vertices of the instance. It lets the algorithm
	 * modify this set without modifiying the instance.
	 */
	private HashSet<Integer> requiredVertices;

	/**
	 * List of nodes that are currently reached by the root in the current partial solution.
	 */
	private HashSet<Integer> reachedNodes;

	/**
	 * A comparator used to sort couples of nodes by costs.
	 */
	private Comparator<Couple<Integer,Integer>> comp;

	/**
	 * Set of saturated arcs (ie full of fluid)
	 */
	private HashSet<Couple<Integer,Integer>> saturated;

	/**
	 * For each node, this map saves the sources this node is linked to with a
	 * path of saturated arcs
	 */
	private HashMap<Integer, HashSet<Integer>> sources;

	/**
	 * Set of nodes for which one entering arc is saturating. The key of each
	 * node in the Fibonacci Heap is the physical time when its next saturating
	 * arc will be saturated.
	 */
	private CustomFibonacciHeap<Integer, DoubleBoolean> sortedSaturating;

	/**
	 * The actual physical time since the beginning of saturation
	 */
	private double time;

	/**
	 * This maps each node to the node of the fibonacci heap containing it.
	 */
	private HashMap<Integer, CustomFibonacciHeapNode<Integer, DoubleBoolean>> n2fbn;

	/**
	 * For each node, this map sorts its input arcs (in the complete metric closure graph) by cost.
	 */
	private HashMap<Integer, TreeSet<Couple<Integer,Integer>>> sortedInputArcs;
	
	/**
	 * For each node, this map contains an iterator to its sorted list of inputs arc
	 * described by {@link #sortedInputArcs}
	 */
	private HashMap<Integer, Iterator<Couple<Integer,Integer>>> sortedInputArcsIterator;
	
	/**
	 * For each node v , this map contains the last arc iterated by the iterator in {@link #sortedInputArcsIterator} associated with v
	 */
	private HashMap<Integer, Couple<Integer,Integer>> nextSaturatedInputArc;

	/**
	 * Initialize the maps, sets and lists used by the algorithm.
	 */
	private void init() {
		// Copy the required vertices
		requiredVertices = new HashSet<Integer>(instance.getRequiredVertices());
		// Remove the root, if it is a terminal, otherwise, this algorithm could start
		// a infinite loop
		requiredVertices.remove(instance.getRoot());

		// Initialize the set of nodes that are reached from the root by the current solution.
		reachedNodes = new HashSet<Integer>();
		// At the beggining, the root only reaches itself.
		reachedNodes.add(instance.getRoot());

		// Build the comparator.
		comp = getArcsComparator();
		
		// Initialisation of every parameter
		// Note that those parameters are currently empty. Elements are added to them later
		// when it is necessary.
		costs = new HashMap<Couple<Integer,Integer>, Integer>();
		shortestPath = new HashMap<Couple<Integer,Integer>, TreeIterator<Arc>>();
		saturated = new HashSet<Couple<Integer,Integer>>();
		sources = new HashMap<Integer, HashSet<Integer>>();
		sortedSaturating = new CustomFibonacciHeap<Integer, DoubleBoolean>();
		n2fbn = new HashMap<Integer, CustomFibonacciHeapNode<Integer, DoubleBoolean>>();
		sortedInputArcs = new HashMap<Integer, TreeSet<Couple<Integer,Integer>>>();
		sortedInputArcsIterator = new HashMap<Integer, Iterator<Couple<Integer,Integer>>>();
		nextSaturatedInputArc = new HashMap<Integer, Couple<Integer,Integer>>();
	}

	@Override
	protected void computeWithoutTime() {

		// Initialize parameters
		this.init();

		// This set will merge the trees returned by FLAC
		HashSet<Arc> currentSol = new HashSet<Arc>();

		
		// Until all the terminals are reached		
		while (requiredVertices.size() > 0) {

			// Search a low Density Directed Steiner Tree with the FLAC algorithm
			Couple<HashSet<Couple<Integer,Integer>>, HashSet<Integer>> result = 
					applyFLAC();

			if (result == null) {
				// If the algorithm get here, there is either an error in the algorithm
				// or an error in the instance (it does not contain a feasible solution)
				this.arborescence = null;
				this.cost = null;
				return;
			}

			// This is the tree returned by FLAC
			HashSet<Couple<Integer,Integer>> tbest = result.first;

			// Those are the terminals reached by the previous tree
			HashSet<Integer> reached = result.second;

			/*
			 * Merge the tree and the current solution.
			 * Update the list of nodes reached by the current solution so that
			 * the next tree returned by FLAC is preferentially
			 * merged by the current partial solution. 
			 */
			for (Couple<Integer,Integer> a : tbest) {
				// path is the list of arcs in a shortest path from a.first to a.second in the original graph.
				TreeIterator<Arc> path = getShortestPaths(a);
				while(path.hasNext()){
					Arc b = path.next();
					currentSol.add(b);
					reachedNodes.add(b.getOutput());
				}
			}

			// Remove the reached terminals from the required vertices of the instance
			requiredVertices.removeAll(reached);
		}
		
		// Set the output of this algorithm : the returned tree and its cost
		arborescence = currentSol;
		cost = 0;
		for(Arc a : arborescence){
			cost += instance.getIntCost(a);
		}
	}

	/**
	 * @return a comparator for the arcs of the complete graph.
	 */
	private Comparator<Couple<Integer, Integer>> getArcsComparator() {
		return new Comparator<Couple<Integer, Integer>>() {

			@Override
			public int compare(Couple<Integer, Integer> o1, Couple<Integer, Integer> o2) {
				// If arcs are the same : return 0
				if(o1 == o2)
					return 0;
				// If the first one is null, its cost is considered as Positive Infinity
				else if (o1 == null)
					return 1;
				// If the second one is null, its cost is considered as Positive Infinity
				else if (o2 == null)
					return -1;
				// If the arcs are equals : return 0 (we do not need to compute their respective costs)
				else if (o1.equals(o2))
					return 0;
				else {
					// Compute the costs of o1 and o2
					Integer i1 = GFLACTR2Algorithm.this.getCost(o1);
					Integer i2 = GFLACTR2Algorithm.this.getCost(o2);
					int comp;
					
					// Compare the costs
					if (i1 == i2)
						comp = 0;
					else if(i1 == null)
						return 1;
					else if (i2 == null)
						return -1;
					else
						comp = i1.compareTo(i2);
					if (comp != 0)
						return comp;
					
					// If the costs are the same, return the comparison of the nodes of the arc
					// so that arcs with same cost are ordered.
					comp = o1.first.compareTo(o2.first);
					if (comp != 0)
						return comp;
				
					return o1.second.compareTo(o2.second);
				}
			}
		};	
	}

	// ---------- density Directed Steiner Tree part ---------

	/**
	 * @return a tree rooted in the root of the instance spanning a part of the
	 *         terminals, and the set of those terminals.
	 */
	private Couple<HashSet<Couple<Integer,Integer>>, HashSet<Integer>> applyFLAC() {

		// Reinitialize the parameters to let FLAC restart normally
		reinit();

		while (true) {
			// Check which arc will be the next saturated one
			Couple<Integer,Integer> a = nextSaturatedArc();

			Integer u = a.first;
			Integer v = a.second;

			// If the root or a node already linked to the root in the current solution is reached by the terminals, we can return a tree
			if (reachedNodes.contains(u)) {
				saturated.add(a);
				return buildTree();
			}

			// We now check if a node is linked to the root with two paths of saturated arcs: it is called a conflict
			boolean conflict = findConflict(u, v);

			// Whatever the case,
			// we first have to update the costs of all non saturated arcs entering v if possible except (u,v)
			// we then have to check which arc of v will be its next saturated entering arc, and when
			// it will be saturated
			saturationConsequences(a);
			
			// If there is a conflict, we just ignore the arc saturation, as if it was never added to the arc
			if (!conflict){
				// If there is no conflict, we have to update the flow rate of other arcs as a new arc is
				// saturated.
				saturateArcAndUpdate(a);
			}
		}
	}

	/**
	 * Clear the maps, sets and lists used by the algorithm FLAC. Reinitialize
	 * the parameters used by FLAC.
	 */
	private void reinit() {
		saturated.clear();
		sources.clear();
		sortedSaturating.clear();
		n2fbn.clear();

		// The saturation begin at 0 seconds
		time = 0D;

		// Clear the Iterators and next Saturating input arcs as we reset the flow : no more arc is saturated.
		// Note that the sortedInputArcs map is not cleared : it does not depend on the quantity of flow in the graph.
		nextSaturatedInputArc.clear();
		sortedInputArcsIterator.clear();

		// Init parameters for each terminal
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while (it.hasNext()) {
			Integer v = it.next();
			if (requiredVertices.contains(v)) {

				// define the sources feeding that terminal as the terminal itself
				getSources(v).add(v);

				// define the next saturated arc entering v, and compute the time
				// in seconds needed to saturate it.
				updateNextSaturatedArc(v);
			}
		}
	}

	/**
	 * @param v
	 * @return the set of sources of v. If it was not initialized, init it.
	 */
	private HashSet<Integer> getSources(Integer v) {
		HashSet<Integer> srcs = sources.get(v);
		if (srcs == null) {
			srcs = new HashSet<Integer>();
			sources.put(v, srcs);
		}
		return srcs;
	}

	/**
	 * For an arc a = (u,v) full of flow, update the costs of every arc entering v except a, if necessary
	 * Then check which arc entering v will be the next arc full of flow after a.
	 * @param a
	 */
	private void saturationConsequences(Couple<Integer,Integer> a ) {

		boolean updated = updateCosts(a);
		
		// If the cost of at least one arc was updated and consequently the orederd set of arcs entering v was updated, we have to update the iterator of that set before using it again.
		if(updated)
			resetSortedInputArcsIterator(a.second);
		updateNextSaturatedArc(a.second);
	}

	/**
	 * For an arc a = (u,v) update the costs of every arcs (w,v) entering v if and only if firstly, (w,u) is an arc in the original graph, and secondly, the cost of (w,v) is greater than the cost of (w,u) plus the cost of (u,v). 
	 * @param cuv
	 * @return true if at least one cost was modified, and consequently, if the list of ordered set of arcs entering v was updated.
	 */
	private boolean updateCosts(Couple<Integer,Integer> cuv){

		// The cost of the arc (u,v)
		Integer cost_uv = this.getCost(cuv);

		Integer u = cuv.first;
		Integer v = cuv.second;

		// Check if at least one cost is modified
		boolean modified = false;

		// We iterate over each arc entering u in the ORIGINAL graph
		Iterator<Arc> it = instance.getGraph().getInputArcsIterator(u);
		while(it.hasNext()){
			Arc b = it.next();
			Integer w = b.getInput();

			// If w equals v then, whatever the case, we will not update (u,v)
			if(w.equals(v))
				continue;

			// Arc (w,v)
			Couple<Integer, Integer> cwv = new Couple<Integer, Integer>();
			cwv.first = w;
			cwv.second = v;

			// Cost of the arc (w,v)
			Integer cost_wv = this.getCost(cwv);


			// Arc (w,u)
			Couple<Integer, Integer> cwu = new Couple<Integer, Integer>();
			cwu.first = w;
			cwu.second = u;

			// Cost of the arc (w,v)
			Integer cost_wu = this.getCost(cwu);
			
			// Updated cost of the arc (w,v)
			Integer cost_wvp;
			if(cost_wu == null)
				// If the cost of (w,u) is positive_infinitly then, whatever the case, cost_wvp can not be less than cost_wv 
				continue;
			else
				// Value of the updated cost of (w,v)
				cost_wvp = cost_wu + cost_uv;

			// If the updated cost if less than the previous cost of (w,v) we update that cost.
			if(cost_wv == null || cost_wvp < cost_wv){
				// At least one cost was updated so the flag modified is true
				modified = true;
				
				// We decrease the cost of (w,v) and update its position in the oredered set of arcs entering v
				decreaseKey(cwv, cost_wvp);

				// We update the list of arcs in the shortest path linking w to v in the original graph (it is not possible to only define the predecessor of v in that path as this leads to infinite loops)
				TreeIterator<Arc> t = TreeIterator.getTreeIterator(getShortestPaths(cwu), getShortestPaths(cuv));
				shortestPath.put(cwv, t);
			}

		}
		return modified;
	}

	/**
	 * Assuming an entering arc of v is saturated, the next one is the next in
	 * the list of entering arcs of v sorted by weigths. <br/>
	 * This method find this arc and compute when it will be saturated
	 * 
	 * @param v
	 */
	private void updateNextSaturatedArc(Integer v) {

		// Last saturated arc entering v
		Couple<Integer,Integer> cuv = getNextSaturatedInputArc(v);
		
		// Shift the iterator of arcs entering v to the next one
		boolean shifted = shiftNextSaturatedInputArc(v);
		if(!shifted){
			// If cuv was the last saturated arc entering v
			n2fbn.remove(v);
			return;
		}
		// If not, we continue ...
		
		// a is the new next saturating entering arc of v
		Couple<Integer,Integer> a = getNextSaturatedInputArc(v);

		// Saturated time of a
		Double satTime;
		Double volume = getVolume(a);
		double rate = getVolFlowRate(v);

		if(Double.isInfinite(volume))
			satTime = Double.POSITIVE_INFINITY;
		else if (cuv == null)
			// If a is the first arc entering v which starts to saturate
			satTime = volume / rate;
		else
			// If not...
			satTime = (volume - getVolume(cuv)) / rate;

		// Reinsert v in the list with the saturated time of a
		CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = sortedSaturating
				.insert(v, new DoubleBoolean(time + satTime, !a.first
						.equals(instance.getRoot())));
		n2fbn.put(v, fbn);

	}


	/**
	 * @param v
	 * @return the next arc entering v which will saturated. In other words, the first arc in the ordered set of arcs entering v which is not aleready saturated (or marked). 
	 */
	private Couple<Integer, Integer> getNextSaturatedInputArc(Integer v) {
		return nextSaturatedInputArc.get(v);
	}

	/**
	 * Take the next arc returned by the iterator of the ordered set of arcs entering v and define it as the next saturated arc entering v.
	 * @param v
	 * @return true if there is one arc entering v which is not saturated
	 */
	private boolean shiftNextSaturatedInputArc(Integer v) {
		Iterator<Couple<Integer,Integer>> it = getSortedInputArcsIterator(v);
		if(it.hasNext()){
			nextSaturatedInputArc.put(v, it.next());
			return true;
		}
		return false;
	}

	/**
	 * Reset the iterator of the ordered set of arcs entering v : let (u,v) be the next saturated arc entering v, the next arc which is iterated is the one following (u,v) in the ordered set of arcs entering v. 
	 * @param v
	 */
	private void resetSortedInputArcsIterator(Integer v){
		Couple<Integer,Integer> nextSaturatedInputArc = getNextSaturatedInputArc(v);
		TreeSet<Couple<Integer,Integer>> tree = getSortedInputArcs(v);
		Iterator<Couple<Integer,Integer>> it = tree.tailSet(nextSaturatedInputArc, false).iterator(); // According to the documentation, this should be done in O(log(n))
		sortedInputArcsIterator.put(v, it);
	}

	/**
	 * @param v
	 * @return the iterator of the ordered set of arcs entering v. If it does not exists, init it.
	 */
	private Iterator<Couple<Integer,Integer>> getSortedInputArcsIterator(Integer v){
		Iterator<Couple<Integer,Integer>> it = sortedInputArcsIterator.get(v);
		if(it == null){
			TreeSet<Couple<Integer,Integer>> tree = getSortedInputArcs(v);
			it = tree.iterator();
			sortedInputArcsIterator.put(v, it);
		}
		return it;
	}

	/**
	 * 
	 * @param v
	 * @return the ordered set of arcs entering v. If it does not exists, init it.
	 */
	private TreeSet<Couple<Integer,Integer>> getSortedInputArcs(Integer v){
		TreeSet<Couple<Integer,Integer>> tree = sortedInputArcs.get(v);
		if(tree == null){
			tree = sortInputArcs(v);
			sortedInputArcs.put(v,tree);
		}
		return tree;
	}

	/**
	 * Sort the input arcs of v by cost
	 * @param v
	 */
	private TreeSet<Couple<Integer,Integer>>  sortInputArcs(Integer v) {

		TreeSet<Couple<Integer,Integer>> tree = new TreeSet<Couple<Integer,Integer>>(this.comp);
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();

		while (it.hasNext()) {
			Integer w = it.next();
			if(w.equals(v))
				continue;
			Couple<Integer, Integer> c = new Couple<Integer, Integer>();
			c.first = w;
			c.second = v;

			tree.add(c);
		}

		return tree;
	}

	/**
	 * Decrease the cost of the arc cuv (~ (u,v)) to newCost. Update the set of ordered set entering v.
	 * @param cuv
	 * @param newCost
	 */
	private void decreaseKey(Couple<Integer,Integer> cuv, Integer newCost){
		Integer v = cuv.second;
		TreeSet<Couple<Integer,Integer>> tree = sortedInputArcs.get(v);
		// We first have to remove the arc from the tree or it will not be able to find it anymore.
		tree.remove(cuv);
		// Change the cost
		costs.put(cuv, newCost);
		// Add it in the tree again.
		tree.add(cuv);
	}

	/**
	 * @param cuv
	 * @return the current cost of the arc cuv. If it does not exists, init it.
	 */
	private Integer getCost(Couple<Integer,Integer> cuv){
		Integer cost;
		if(!costs.containsKey(cuv)){
			Arc b = instance.getGraph().getLink(cuv.first, cuv.second);
			cost = instance.getIntCost(b, true);
			costs.put(cuv, cost);
		}
		else
			cost = costs.get(cuv);
		return cost;
	}

	/**
	 * 
	 * @param cuv
	 * @return the maximum value of flow an arc a can contain: its cost
	 */
	private Double getVolume(Couple<Integer,Integer> cuv) {
		Integer cost = getCost(cuv);
		if (cost == null)
			return Double.POSITIVE_INFINITY;
		else
			return (double) cost;
	}

	/**
	 * @param cuv : the arc (u,v) in the complete graph
	 * @return the list of arcs in a shortest path from u to v in the original graph.
	 */
	private TreeIterator<Arc> getShortestPaths(Couple<Integer,Integer> cuv){
		TreeIterator<Arc> t;
		if(!shortestPath.containsKey(cuv)){
			Arc b = instance.getGraph().getLink(cuv.first, cuv.second);
			t = TreeIterator.getTreeIterator(b);
			shortestPath.put(cuv, t);
		}
		else
			t = shortestPath.get(cuv);
		return t;
	}

	/**
	 * 
	 * @param v
	 * @return the current flow rate entering v: the sources it can reach with
	 *         saturated arcs
	 */
	private double getVolFlowRate(Integer v) {
		return (double) getSources(v).size();
	}


	/**
	 * @return the next arc which will be full of flow in the graph.
	 */
	private Couple<Integer,Integer> nextSaturatedArc() {
		// Find the first node for which an entering arc will be full of flow
		CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = sortedSaturating
				.removeMin();
		time = fbn.getKey().getDoubleValue();
		
		// Return the first saturated entering arc of that node
		return getNextSaturatedInputArc(fbn.getData());
	}


	/**
	 * @return the set of arcs reaching the current solution with a path of saturated arcs. This shoud be a tree. This method also returns the set of terminals which are reached by that tree.
	 */
	private Couple<HashSet<Couple<Integer, Integer>>, HashSet<Integer>> buildTree() {
		LinkedList<Integer> toList = new LinkedList<Integer>();
		toList.addAll(reachedNodes);

		HashSet<Couple<Integer, Integer>> tree = new HashSet<Couple<Integer, Integer>>();
		HashSet<Integer> leaves = new HashSet<Integer>();

		while (!toList.isEmpty()) {
			Integer v = toList.pollFirst();
			if (requiredVertices.contains(v))
				leaves.add(v);

			Iterator<Integer> it = instance.getGraph().getVerticesIterator();
			while (it.hasNext()) {
				Integer w = it.next();

				if(w.equals(v))
					continue;

				Couple<Integer, Integer> c = new Couple<Integer,Integer>();
				c.first = v;
				c.second = w;
				if (!isSaturated(c))
					continue;

				tree.add(c);
				toList.add(w);
			}
		}
		return new Couple<HashSet<Couple<Integer, Integer>>, HashSet<Integer>>(tree, leaves);
	}

	/**
	 * 
	 * @param a
	 * @return true if a is full of flow
	 */
	private boolean isSaturated(Couple<Integer,Integer> a) {
		return saturated.contains(a);
	}

	private boolean findConflict(Integer u, Integer v) {
		// Will contain the list of nodes linked to u with a saturated path...
		LinkedList<Integer> toList = new LinkedList<Integer>();
		// ... including u
		toList.add(u);

		// If one of those nodes is already linked to one of the sources linked to v
		// there is a conflict.
		HashSet<Integer> vsrcs = getSources(v);

		while (!toList.isEmpty()) {
			Integer w = toList.pollFirst();

			// If the sources reaching w intersect the sources reaching v there is a conflict
			if (nonEmptyIntersection(getSources(w), vsrcs))
				return true;

			// Add all the saturated arcs entering w to the list of arcs we have to check
			Couple<Integer,Integer> saturatingInputArc = getNextSaturatedInputArc(w);
			if (saturatingInputArc == null)
				continue;
			
			// For each node linked to w with a saturated arc, we insert it in the list
			// of nodes we have to check
			Iterator<Couple<Integer,Integer>> it = getSortedInputArcs(w).iterator();
			while(it.hasNext()){
				Couple<Integer,Integer> inputArc = it.next();
				if(saturatingInputArc.equals(inputArc))
					break;
				if(isSaturated(inputArc)) // Notice this test is necessary as some arcs could be full of flow but not saturated (and then marked)
					toList.add(inputArc.first);
			}
		}
		return false;
	}

	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return true if s1 and s2 have a non empty intersection
	 */
	private boolean nonEmptyIntersection(HashSet<Integer> s1,
			HashSet<Integer> s2) {
		HashSet<Integer> sa, sb;

		if (s2.size() > s1.size()) {
			sa = s1;
			sb = s2;
		} else {
			sa = s2;
			sb = s1;
		}

		for (Integer v : sa) {
			if (sb.contains(v))
				return true;
		}

		return false;
	}


	private void saturateArcAndUpdate(Couple<Integer, Integer> a) {
		Integer u = a.first;
		Integer v = a.second;

		// This list will contain the set of node for which the flow rate will change
		// after the saturation of a...
		LinkedList<Integer> toUpdate = new LinkedList<Integer>();
		// ...including u
		toUpdate.add(u);

		// The sources of v we have to add to update the affected nodes
		HashSet<Integer> vsrcs = getSources(v);

		while (!toUpdate.isEmpty()) {
			Integer w = toUpdate.pollFirst();

			// The current flow rate inside each entering arc of w, before a is saturated
			double prevVolFlowRate = getVolFlowRate(w);
			getSources(w).addAll(vsrcs); // disjoint union, because there is no conflict

			if (prevVolFlowRate != 0) {
				// if w already received flow before a became saturated
				// the time the next entering arc of w is saturated is accelerated like this:
				CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = n2fbn
						.get(w);
				// by the following test, we test if there is an entering arc of w which is not fully saturated
				// in the other case we do nothing
				if (fbn != null) {
					Double prevNextSaturatedEnteringArcTime = fbn.getKey()
							.getDoubleValue();

					if(!Double.isInfinite(prevNextSaturatedEnteringArcTime)){

						double newVolFlowRate = getVolFlowRate(w);

						Double newNextSaturatedEnteringArcTime = time
								+ (prevNextSaturatedEnteringArcTime - time)
								* (prevVolFlowRate / newVolFlowRate);

						sortedSaturating.decreaseKey(fbn, new DoubleBoolean(
								newNextSaturatedEnteringArcTime, fbn.getKey()
								.getBooleanValue()));
					}
				}
			} else
				// if w did not receive any flow from the source, we initialize its saturation like this
				updateNextSaturatedArc(w);

			// For each node linked to w with a saturated arc, we insert it in the list
			// of nodes we have to update
			Couple<Integer,Integer> saturatingInputArc = getNextSaturatedInputArc(w);
			if (saturatingInputArc == null)
				continue;
			Iterator<Couple<Integer,Integer>> it = getSortedInputArcs(w).iterator();
			while(it.hasNext()){
				Couple<Integer,Integer> inputArc = it.next();
				if(saturatingInputArc.equals(inputArc))
					break;
				if(isSaturated(inputArc))// Notice this test is necessary as some arcs could be full of flow but not saturated (and then marked)
					toUpdate.add(inputArc.first);
			}
		}

		saturated.add(a);
	}

}


