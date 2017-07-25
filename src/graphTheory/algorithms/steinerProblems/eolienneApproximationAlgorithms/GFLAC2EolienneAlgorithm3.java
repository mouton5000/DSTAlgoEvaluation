package graphTheory.algorithms.steinerProblems.eolienneApproximationAlgorithms;

import graphTheory.graph.Arc;
import graphTheory.utils.*;

import java.util.*;

/**
 *
 * Implementation of the G_F algorithm.
 * <p>
 * This algorithm returns a feasible solution for the Directed Steiner Tree
 * problem. It is a greedy algorihm which uses a sub-algorihthm named FLAC.
 * <p>
 * FLAC is designed to returns a feasible solution for the minimum Density
 * Directed Steiner Tree problem: it searches for a tree rooted in the root of
 * the instance which spans a part of the terminal. It try to minimize the total
 * cost of the tree divided by the number of terminals.
 * <p>
 * Gf uses FLAC to span some terminals, delete those terminals, and repeat FLAC
 * to span other terminals. It continues until all the terminals are reached.
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
 *
 *
 *
 * @author Watel Dimitri
 *
 */
public class GFLAC2EolienneAlgorithm3 extends EolienneApproximationAlgorithm {


	// --------------------   Directed Steiner Tree Part --------------------//

	/**
	 * Copy of the costs of the instance. It let the algorithm modify the costs
	 * withouth modifying the instance it self.
	 */
	private HashMap<Arc, Double> costs;

	/**
	 * Copy of the required vertices of the instance. It let the algorithm
	 * modify this set without modifiying the instance.
	 */
	private HashSet<Integer> requiredVertices;

    /**
     * For each arc, this map stores how many terminals can be linked to the root with a path containing this arc.
     */
	private HashMap<Arc, Integer> leftCapacities;

    /**
     * For each node, this map stores how many output arcs of that node can be used to the solution. When such an arc
     * is added, the stored numbered is reduced by 1.
     */
    private HashMap<Integer, Integer> leftOutputDegree;

	/**
	 * For each node, this map sorts its input arcs by cost.
	 */
	private HashMap<Integer, TreeSet<Arc>> sortedInputArcs;

	/**
	 * A comparator used to sort arcs by costs.
	 */
	private Comparator<Arc> comp;

    /**
     * Contain the arcs currently added in the solution.
     */
	private HashSet<Arc> currentSol;

	@Override
	protected void computeWithoutTime() {

		// Copy the required vertices
		requiredVertices = new HashSet<Integer>(instance.getRequiredVertices());
		// Remove the root, if it is a terminal, otherwise, this algorithm could start
		// a infinite loop
		requiredVertices.remove(instance.getRoot());


		// This set will merge the trees returned by FLAC
		currentSol = new HashSet<Arc>();

        // Copy the costs
		this.costs = instance.getDoubleCosts();

        // Cost comparator
		comp = getArcsComparator();

        // Init maps
		sortedInputArcs = new HashMap<Integer, TreeSet<Arc>>();

		Integer maxCapacity = Collections.max(instance.getStaticCapacities());

		leftCapacities = new HashMap<Arc, Integer>();
        for(Arc a : instance.getGraph().getEdges())
            leftCapacities.put(a, maxCapacity);

        leftOutputDegree = this.getInstance().getMaximumOutputDegree();

		// Initialize the local parameters
		this.init();

		// Until all the terminals are reached


		while (requiredVertices.size() > 0) {
			try {
				boolean result = applyFLAC(); // Search a low Density Directed Steiner Tree with the FLAC algorithm

				if (!result) {
					setNoSolution();
					return;
				}
			}
			catch(NullPointerException e){
				setNoSolution();
				return;
			}

//            System.out.println(currentSol);
		}

		// Set the output of this algorithm : the returned tree and its cost

		Double c = 0D;
        HashMap<Arc, Integer> arborescenceFlow = new HashMap<Arc, Integer>();

        for (Arc a : currentSol) {
            arborescenceFlow.put(a, maxCapacity - leftCapacities.get(a));
        }

        arborescence = instance.unviolateMaxNbSecConstraint(arborescenceFlow);
        for(Map.Entry<Arc,Integer> entry : arborescence.entrySet()){
            c += instance.getRealCableCost(entry.getKey(), entry.getValue());
        }
		cost = c;

	}

	private Comparator<Arc> getArcsComparator() {
		return new Comparator<Arc>() {

			@Override
			public int compare(Arc o1, Arc o2) {
				if (o1 == null)
					return 1;
				else if (o2 == null)
					return -1;
				else if (o1.equals(o2))
					return 0;
				else {
					Double i1 = costs.get(o1);
					Double i2 = costs.get(o2);
					int comp = i1.compareTo(i2);
					if (comp != 0)
						return comp;
					comp = o1.getInput().compareTo(o2.getInput());
					if (comp != 0)
						return comp;
					return o1.getOutput().compareTo(o2.getOutput());
				}
			}
		};
	}

	private TreeSet<Arc> getSortedInputArcs(Integer v){
		TreeSet<Arc> tree = sortedInputArcs.get(v);
		if(tree == null)
			tree = sortInputArcs(v);
		return tree;
	}

	/**
	 * Sort the input arcs of v by cost
	 *
	 * @param v
	 */
	private TreeSet<Arc> sortInputArcs(Integer v) {
		Iterator<Arc> it = instance.getGraph().getInputArcsIterator(v);

		TreeSet<Arc> ts = new TreeSet<Arc>(comp);
		while (it.hasNext()) {
			ts.add(it.next());
		}

		sortedInputArcs.put(v, ts);
		return ts;
	}

	// ---------- density Directed Steiner Tree part ---------

	/**
	 * @return a tree rooted in the root of the instance spanning a part of the
	 *         terminals, and the set of those terminals.
	 */
	private boolean applyFLAC() {

		// Reinitialize the parameters to let FLAC restart normally
		reinit();

//		System.out.println(">>>>>>>>>>>>>");

		while (true) {

			Integer v = nextSaturatedNode();

//			System.out.println(v);

			if(isFictive(v)) {

				Integer w = getReal(v);
				LinkedList<Arc> waitingEnteringArcs = getWaitingOutgoingArcs(w);
				LinkedList<Arc> conflictedWaitingEnteringArcs = getConflictedArcs(waitingEnteringArcs);

				if(!conflictedWaitingEnteringArcs.isEmpty())
                    updateConflictedFictiveNode(v, conflictedWaitingEnteringArcs);
				else {
                    Integer clod = currentLeftOutDegree.get(w);
                    if (clod != null && clod < waitingEnteringArcs.size()){
                        Arc minFlowRateEnteringArc = Collections.min(waitingEnteringArcs,
                                (arc, other) -> getFlowRate(
                                        arc.getOutput()).compareTo(getFlowRate(other.getOutput())));
                        conflictedWaitingEnteringArcs.add(minFlowRateEnteringArc);
                        updateConflictedFictiveNode(v, conflictedWaitingEnteringArcs);
                    }
                    else{
                        Integer missingCapacity = checkMaxCapacities(w, getFlowRate(v));
                        if (!missingCapacity.equals(0)) {
                            conflictedWaitingEnteringArcs = getCapacityConflictedArcs(missingCapacity, waitingEnteringArcs);
                            updateConflictedFictiveNode(v, conflictedWaitingEnteringArcs);
                        } else
                            saturateArcsAndUpdate(w, waitingEnteringArcs);
                    }
                }
			}
			else {

				Arc a = nextSaturatedEnteringArc(v);

//				System.out.println(a);

				if (a == null)
					return false;

				Integer u = a.getInput();

				// If the root is reached by the terminals, we can return a tree
                Integer clod = currentLeftOutDegree.get(u);
				if (u.equals(instance.getRoot()) && (clod == null || clod != 0 || currentSol.contains(a))) {
					saturated.add(a);
					return buildTree(u);
				}

				// We now check if a node is linked to the root with two paths of saturated arcs: it is called a conflict
				boolean conflict = findConflict(a);

				// Whatever the case, we have to check which arc of v will be its next saturated entering arc, and when
				// it will be saturated
				updateNextSaturatedArc(v);

				// If there is a conflict, we just ignore the arc saturation, as if it was never added to the arc
				if (!conflict)
					// If there is no conflict, we have to update the flow rate of other arcs as a new arc is
					// saturated.
					saturateArcAndUpdate(a);
			}
		}
	}

    /**
	 * Set of saturated arcs (ie full of fluid)
	 */
	private HashSet<Arc> saturated;

	/**
	 * For each node, this map saves the sources this node is linked to with a
	 * path of saturated arcs
	 */
	private HashMap<Integer, Integer> flowRates;

	/**
	 * Set of nodes for which one entering arc is saturating. The key of each
	 * node in the Fibonacci Heap is the physical time when its next saturating
	 * arc will be saturated.
	 */
	private CustomFibonacciHeap<Integer, DoubleBoolean> sortedSaturating;

	private HashSet<Arc> waiting;

    private HashMap<Arc, LinkedList<Couple<Double, Integer>>> waitingFlowRates;

	/**
	 * Set of nodes associated with the next saturated entering arc of this
	 * node.
	 */
	private HashMap<Integer, Iterator<Arc>> nextSaturatedEnteringArcIterators;

	/**
	 * Set of nodes associated with the next saturated entering arc of this
	 * node.
	 */
	private HashMap<Integer, Arc> nextSaturatedEnteringArcs;

	/**
	 * The actual physical time since the beginning of saturation
	 */
	private double time;

	/**
	 * This maps each node to the node of the fibonacci heap containing it.
	 */
	private HashMap<Integer, CustomFibonacciHeapNode<Integer, DoubleBoolean>> n2fbn;

	private WeightedQuickUnionPathCompressionUF unionFind;

	private HashMap<Integer, NodeState3> nodeStates;

    private HashMap<Integer, Integer> currentLeftOutDegree;

	/**
	 * Initialize the maps, sets and lists used by the algorithm FLAC
	 */
	private void init() {
		saturated = new HashSet<Arc>();
		flowRates = new HashMap<Integer, Integer>();
		sortedSaturating = new CustomFibonacciHeap<Integer, DoubleBoolean>();
		nextSaturatedEnteringArcIterators = new HashMap<Integer, Iterator<Arc>>();
		nextSaturatedEnteringArcs = new HashMap<Integer, Arc>();
		n2fbn = new HashMap<Integer, CustomFibonacciHeapNode<Integer, DoubleBoolean>>();

		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		unionFind = new WeightedQuickUnionPathCompressionUF(Collections2.max(it));

		nodeStates = new HashMap<Integer, NodeState3>();
		waiting = new HashSet<Arc>();
        waitingFlowRates = new HashMap<Arc, LinkedList<Couple<Double,Integer>>>();

        currentLeftOutDegree = new HashMap<Integer, Integer>();
	}

	/**
	 * Clear the maps, sets and lists used by the algorithm FLAC. Reinitialize
	 * the parameters used by FLAC.
	 */
	private void reinit() {
		saturated.clear();
		flowRates.clear();
		sortedSaturating.clear();
		nextSaturatedEnteringArcIterators.clear();
		nextSaturatedEnteringArcs.clear();
		n2fbn.clear();
		unionFind.reinit();
		waiting.clear();
        waitingFlowRates.clear();

        currentLeftOutDegree.clear();
        currentLeftOutDegree.putAll(leftOutputDegree);

		// The saturation begin at 0 seconds
		time = 0D;

		// Init parameters for each terminal
		Iterator<Integer> it = instance.getGraph().getVerticesIterator();
		while (it.hasNext()) {
			Integer v = it.next();
			if (requiredVertices.contains(v)) {

				// define the sources feeding that terminal as the terminal itself
				setFlowRate(v, 1);

				// define the next saturated arc entering v, and compute the time
				// in seconds needed to saturate it.
				updateNextSaturatedArc(v);
			}

			Iterator<Arc> it2 = instance.getGraph().getOutputArcsIterator(v);
			int numberOfChoosenOutputArcs = 0;
			while(it2.hasNext()){
				Arc a = it2.next();
				if(currentSol.contains(a)){
					numberOfChoosenOutputArcs++;
				}
				if(numberOfChoosenOutputArcs == 2)
					break;
			}
			switch (numberOfChoosenOutputArcs) {
				case 1:
					nodeStates.put(v, NodeState3.EXPLORED);
					break;
				case 2:
					nodeStates.put(v, NodeState3.JONCTIONNED);
					break;
				default:
			}
		}

	}

	public LinkedList<Arc> getWaitingOutgoingArcs(Integer v){
		LinkedList<Arc> waitingOutgoingArcs = new LinkedList<Arc>();
		Iterator<Arc> it = this.getInstance().getGraph().getOutputArcsIterator(v);
		while(it.hasNext()){
			Arc a = it.next();
			if(isWaiting(a))
				waitingOutgoingArcs.add(a);
		}
		return waitingOutgoingArcs;
	}
	/**
	 * Assuming an entering arc of v is saturated, the next one is the next in
	 * the list of entering arcs of v sorted by weigths. <br/>
	 * This method find this arc and compute when it will be saturated
	 *
	 * @param v
	 */
	private void updateNextSaturatedArc(Integer v) {

		// Iterator over the entering arcs of v sorted by weights
		Iterator<Arc> it = nextSaturatedEnteringArcIterators.get(v);

		if (it == null) {
			// Init the iterator, if it was not the case
			it = getSortedInputArcs(v).iterator();
			nextSaturatedEnteringArcIterators.put(v, it);
		}

		// Last saturated arc entering v
		Arc b = nextSaturatedEnteringArc(v);

		// Next saturated arc entering v
		Arc a = null;
		if (it.hasNext()) {
			// If b was not the arc entering v with biggest cost
			// a exists
			a = it.next();
			nextSaturatedEnteringArcs.put(v, a);
		} else {
			// Else, all arcs entering v are already saturated
			nextSaturatedEnteringArcs.remove(v);
			n2fbn.remove(v);
			return;
		}

		// Saturated time of a
		Double satTime;

		if (b == null)
			// No arc entering v start to saturate
			satTime = getVolume(a) / ((double)getFlowRate(v));
		else
			// All arc entering v start to saturate, including a
			satTime = (getVolume(a) - getVolume(b)) / ((double)getFlowRate(v));

		// Reinsert v in the list with the saturated time of a
		CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = sortedSaturating
				.insert(v, new DoubleBoolean(time + satTime, !a.getInput()
						.equals(instance.getRoot())));
		n2fbn.put(v, fbn);

	}

	/**
	 * @param v
	 * @return the next saturated time entering v
	 */
	private Arc nextSaturatedEnteringArc(Integer v) {
		return nextSaturatedEnteringArcs.get(v);
	}

	/**
	 *
	 * @return the next saturated time entering the first node if the fibonnacci
	 *         heap
	 */
	private Integer nextSaturatedNode() {
		CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = sortedSaturating
				.removeMin();
		if(fbn == null)
			return null;
		time = fbn.getKey().getDoubleValue();
		return fbn.getData();
	}

	/**
	 *
	 * @param a
	 * @return true if the saturation of arc a = (u,v) implies a conflict
	 */
	private boolean findConflict(Arc a) {
        Integer u = a.getInput();
        Integer v = a.getOutput();

        Integer clod = currentLeftOutDegree.get(u);

        return (clod != null
                && clod == 0
                && !currentSol.contains(a))
                || unionFind.connected(u - 1, v - 1)
                || waitingConflict(u, v)
                || (!checkCapacities(u, getFlowRate(v)));
    }

    private boolean waitingConflict(Integer u, Integer v){
        List<Arc> arcs = getWaitingOutgoingArcs(u);
        int rootV = unionFind.find(v-1);
        for(Arc a : arcs){
            Integer w = a.getOutput();
            if(w.equals(v))
                continue;
            int rootW = unionFind.find(w-1);
            if(rootW == rootV)
                return true;
        }
        return false;
    }

	private boolean checkCapacities(Integer u, int k){
		return checkMaxCapacities(u,k) == 0;
	}

    private Integer checkMaxCapacities(Integer u, int k){
        LinkedList<Integer> toCheck  = new LinkedList<Integer>();
        toCheck.add(u);

        while(!toCheck.isEmpty()){
            Integer w = toCheck.pollFirst();
            Iterator<Arc> it = this.getInstance().getGraph().getInputArcsIterator(w);
            while(it.hasNext()) {
                Arc b = it.next();
                Integer capacity = leftCapacities.get(b);
                if(capacity != null && capacity < getFlowRate(w) + k)
                    return getFlowRate(w) + k - capacity;
                if(isSaturated(b))
                    toCheck.add(b.getInput());
            }
        }
        return 0;
    }

	private LinkedList<Arc> getConflictedArcs(LinkedList<Arc> arcs){
		LinkedList<Arc> conflictedArcs = new LinkedList<Arc>();

        for(Arc a : arcs){
			if(findConflict(a))
				conflictedArcs.add(a);
		}

		return conflictedArcs;
	}

    private LinkedList<Arc> getCapacityConflictedArcs(Integer missingCapacity, LinkedList<Arc> waitingEnteringArcs) {
        LinkedList<Arc> conflictedWaitingEnteringArcs = new LinkedList<Arc>();
        Collections.sort(waitingEnteringArcs,
                (arc, other) -> getFlowRate(
                        arc.getOutput()).compareTo(getFlowRate(other.getOutput())));

        while (missingCapacity > 0) {
            Arc a = waitingEnteringArcs.pop();
            conflictedWaitingEnteringArcs.add(a);
            missingCapacity -= getFlowRate(a.getOutput());
        }
        return conflictedWaitingEnteringArcs;
    }


	/**
	 * Add the arc a to the set of saturated arcs, and update the flow rate of
	 * all the arcs affected by this saturation
	 *
	 * @param a
	 */
	private void saturateArcAndUpdate(Arc a) {
		// This list will contain the set of node for which the flow rate will change
		// after the saturation of a...
		LinkedList<Arc> toUpdate = new LinkedList<Arc>();
		// ...including u
		toUpdate.add(a);

		// The sources of v we have to add to update the affected nodes

		Integer v = a.getOutput();
		Integer vFlowRate = getFlowRate(v);

		Integer u = a.getInput();
		NodeState3 state = getState(u);
		if(state == NodeState3.EXPLORED && !currentSol.contains(a))
			waiting.add(a);
		else {
			if(state == NodeState3.UNEXPLORED)
				nodeStates.put(u, NodeState3.EXPLORED);
			saturated.add(a);
			unionFind.union(u-1,v-1);
            if(!currentSol.contains(a)){
                Integer clod = currentLeftOutDegree.get(u);
                if(clod != null)
                    currentLeftOutDegree.put(u, clod-1);
            }
		}

		while (!toUpdate.isEmpty()) {
			Arc b = toUpdate.pollFirst();
			Integer w = b.getInput();

			if(isWaiting(b))
				updateExplored(b, vFlowRate);
			else{
				updateUnexploredOrJonctionned(w, vFlowRate);

				Arc saturatingInputArc = nextSaturatedEnteringArc(w);

				// For each node linked to w with a saturated arc, we insert it in the list
				// of nodes we have to update
				for (Arc inputArc : getSortedInputArcs(w)) {
					if (inputArc.equals(saturatingInputArc))
						break;
					if (isSaturated(inputArc) || isWaiting(inputArc))
						toUpdate.add(inputArc);
				}
			}
		}

	}

	private void updateExplored(Arc a, Integer vFlowRate){
        Integer w = a.getInput();

        Integer fw = getFictive(w);

        Integer prevFlowRate = getFlowRate(fw);
		Integer newFlowRate = prevFlowRate + vFlowRate;
		setFlowRate(fw, newFlowRate); // sources disjoint union, because there is no conflict

		if (prevFlowRate != 0) {

			Double prevFlowRateD = (double)prevFlowRate;

			// if w already received flow before a became saturated
			// the time the next entering arc of w is saturated is accelerated like this:
			CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = n2fbn
					.get(fw);
			// by the following test, we test if there is an entering arc of w which is not fully saturated
			// in the other case we do nothing
			double prevNextSaturatedEnteringArcTime = fbn.getKey()
						.getDoubleValue();
            double newVolFlowRateD = newFlowRate;

            double newNextSaturatedEnteringArcTime = time
                    + (prevNextSaturatedEnteringArcTime - time)
                    * (prevFlowRateD / newVolFlowRateD);
            sortedSaturating.decreaseKey(fbn, new DoubleBoolean(
                    newNextSaturatedEnteringArcTime, true));


            LinkedList<Couple<Double,Integer>> wFR = getWaitingFlowRates(a);
            Integer prevFlowRateOfArc;
            if(wFR.isEmpty())
                prevFlowRateOfArc = 0;
            else
                prevFlowRateOfArc = wFR.getLast().second;
            wFR.add(new Couple<Double, Integer>(time, prevFlowRateOfArc + vFlowRate));


        }
        else{
			// a is the first entering waiting arc of the explored node

			Double satTime = (this.getInstance().getStaticStaticBranchingNodeCost())/newFlowRate;

			CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = sortedSaturating
					.insert(fw, new DoubleBoolean(time + satTime, true));
			n2fbn.put(fw, fbn);

            LinkedList<Couple<Double,Integer>> wFR = getWaitingFlowRates(a);
            wFR.add(new Couple<Double,Integer>(time, vFlowRate));
		}


	}

	private void updateUnexploredOrJonctionned(Integer w, Integer vFlowRate){

		// The current flow rate inside each entering arc of w, before a is saturated
		Integer prevFlowRate = getFlowRate(w);
		Integer newFlowRate = prevFlowRate + vFlowRate;
		setFlowRate(w, newFlowRate); // sources disjoint union, because there is no conflict

		if (prevFlowRate != 0) {

			Double prevFlowRateD = (double)prevFlowRate;

			// if w already received flow before a became saturated
			// the time the next entering arc of w is saturated is accelerated like this:
			CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = n2fbn
					.get(w);
			// by the following test, we test if there is an entering arc of w which is not fully saturated
			// in the other case we do nothing
			if (fbn != null) {
				double prevNextSaturatedEnteringArcTime = fbn.getKey()
						.getDoubleValue();
				double newVolFlowRateD = newFlowRate;

				double newNextSaturatedEnteringArcTime = time
						+ (prevNextSaturatedEnteringArcTime - time)
						* (prevFlowRateD / newVolFlowRateD);
				sortedSaturating.decreaseKey(fbn, new DoubleBoolean(
						newNextSaturatedEnteringArcTime, fbn.getKey()
						.getBooleanValue()));
			}
		} else
			// if w did not receive any flow from the source, we initialize its saturation like this
			updateNextSaturatedArc(w);
	}

	private void saturateArcsAndUpdate(Integer v, LinkedList<Arc> waitingEnteringArcs){
		nodeStates.put(v, NodeState3.JONCTIONNED);
		for(Arc a : waitingEnteringArcs){
			waiting.remove(a);
			saturateArcAndUpdate(a);
		}
	}


    private void updateConflictedFictiveNode(Integer fv, LinkedList<Arc> conflictedWaitingEnteringArcs) {
        double flowLeft = 0D;
        Integer newFlowRate = getFlowRate(fv);
        for(Arc a : conflictedWaitingEnteringArcs) {
            waiting.remove(a);
            LinkedList<Couple<Double, Integer>> flowRatesOfArc = getWaitingFlowRates(a);
            Iterator<Couple<Double, Integer>> it = flowRatesOfArc.descendingIterator();
            double t = time;
            while(it.hasNext()){
                Couple<Double, Integer> c = it.next();
                double flow = (t - c.first) * c.second;
                flowLeft += flow;
            }
            newFlowRate -= getFlowRate(a.getOutput());
        }

        setFlowRate(fv, newFlowRate);

        if(newFlowRate == 0)
            return;

        Double satTime = flowLeft/newFlowRate;

        CustomFibonacciHeapNode<Integer, DoubleBoolean> fbn = sortedSaturating
                .insert(fv, new DoubleBoolean(time + satTime, true));
        n2fbn.put(fv, fbn);

    }

	/**
	 *
	 * @return the set of saturated arcs linked to the root, and the terminals
	 *         this set of arcs reach.
	 */
	private boolean buildTree(Integer u) {
		LinkedList<Integer> toList = new LinkedList<Integer>();
		toList.add(u);

		HashSet<Arc> tree = new HashSet<Arc>();
		HashSet<Integer> leaves = new HashSet<Integer>();

		while (!toList.isEmpty()) {
			Integer v = toList.pollFirst();
			if (requiredVertices.contains(v))
				leaves.add(v);

			Iterator<Arc> it = instance.getGraph().getOutputArcsIterator(v);
			while (it.hasNext()) {
				Arc a = it.next();
				if (!isSaturated(a))
					continue;

				tree.add(a);
				Integer capacity = leftCapacities.get(a);
                if(capacity != null)
					leftCapacities.put(a, capacity-getFlowRate(a.getOutput()));

                if(!currentSol.contains(a)) {
                    Integer lod = leftOutputDegree.get(a.getInput());
                    if(lod != null)
                        leftOutputDegree.put(a.getInput(), lod - 1);

                }
                toList.add(a.getOutput());

			}
		}

        for(Arc a : tree)
            costs.put(a, 0D);

			/*
			 * For each arc in the tree set the cost of that arc to 0 As a
			 * consequence, the next tree returned by FLAC is preferentially
			 * merged by the current partial solution. In addition, this loop
			 * add the arc to the current partial solution.
			 */

        currentSol.addAll(tree);
        requiredVertices.removeAll(leaves); // Remove the reached terminals from the required vertices of the instance

		return true;
	}

	/**
	 *
	 * @param a
	 * @return the maximum value of flow an arc a can contain: its cost
	 */
	private double getVolume(Arc a) {
		return (double) costs.get(a);
	}

	/**
	 *
	 * @param v
	 * @return the current flow rate entering v: the sources it can reach with
	 *         saturated arcs
	 */
	private Integer getFlowRate(Integer v) {
		Integer flowRate = flowRates.get(v);
		if (flowRate == null) {
			return 0;
		}
		return flowRate;
	}

	private void setFlowRate(Integer v, Integer flowRate){
		flowRates.put(v, flowRate);
	}

	/**
	 *
	 * @param a
	 * @return true if a is full of flow
	 */
	private boolean isSaturated(Arc a) {
		return saturated.contains(a);
	}

	private boolean isWaiting(Arc a) {
		return waiting.contains(a);
	}

	private Integer getFictive(Integer v){return -v;}

	private Integer getReal(Integer fictiveV){return -fictiveV;}

	private boolean isFictive(Integer v){return v < 0;}

    private LinkedList<Couple<Double, Integer>> getWaitingFlowRates(Arc a){
        LinkedList<Couple<Double, Integer>> list = waitingFlowRates.get(a);
        if(list == null){
            list = new LinkedList<Couple<Double,Integer>>();
            waitingFlowRates.put(a,list);
        }
        return list;
    }

	/**
	 *
	 * @param v
	 * @return the current flow rate entering v: the sources it can reach with
	 *         saturated arcs
	 */
	private NodeState3 getState(Integer v) {
		NodeState3 state = nodeStates.get(v);
		if(state == null) {
			state = NodeState3.UNEXPLORED;
			nodeStates.put(v, state);
		}
		return state;
	}
}

enum NodeState3 {
	UNEXPLORED,
	EXPLORED,
	JONCTIONNED;
}