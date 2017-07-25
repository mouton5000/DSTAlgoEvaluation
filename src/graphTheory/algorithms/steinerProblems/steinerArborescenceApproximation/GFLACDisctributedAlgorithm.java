
package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.graph.Arc;
import graphTheory.utils.Couple;
import graphTheory.utils.Triplet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeSet;

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
 * @author Watel Dimitri
 *
 */
public class GFLACDisctributedAlgorithm extends SteinerArborescenceApproximationAlgorithm {

	// -------------------- Directed Steiner Tree Part --------------------//

	/**
	 * Copy of the required vertices of the instance. It let the algorithm
	 * modify this set without modifiying the instance.
	 */
	private HashSet<Integer> requiredVertices;

	/**
	 * For each node, this map sorts its input arcs by cost.
	 */
	private HashMap<Integer, TreeSet<Arc>> sortedInputArcs;

	/**
	 * A comparator used to sort arcs by costs.
	 */
	private Comparator<Arc> comp;


	private HashSet<Integer> reached;

	@Override
	protected void computeWithoutTime() {

		// Copy the required vertices
		requiredVertices = new HashSet<Integer>(instance.getRequiredVertices());
		// Remove the root, if it is a terminal, otherwise, this algorithm could start
		// a infinite loop
		requiredVertices.remove(instance.getRoot());

		// This set will merge the trees returned by FLAC
		HashSet<Arc> currentSol = new HashSet<Arc>();

		reached = new HashSet<Integer>();
		reached.add(instance.getRoot());

		comp = getArcsComparator();

		sortedInputArcs = new HashMap<Integer, TreeSet<Arc>>();

		// Initialize parameters
		this.init();

//		System.out.println();
		
		// Until all the terminals are reached
		while (requiredVertices.size() > 0) {
			Triplet<HashSet<Arc>, HashSet<Integer>, HashSet<Integer>> result = applyFLAC(); // Search a low Density Directed Steiner Tree with the FLAC algorithm

			if (result == null) {
				this.arborescence = null;
				this.cost = null;
				return;
			}

			// This is the tree returned by FLAC
			HashSet<Arc> tbest = result.first;

			HashSet<Integer> nodesReached = result.second;

			// Those are the terminals reached by the previous tree
			HashSet<Integer> terminalsReached = result.third;

//			System.out.println(this.requiredVertices);
//			System.out.println(tbest);
//			System.out.println(nodesReached);
//			System.out.println(terminalsReached);
			
//			System.exit(-1);
			
			/*
			 * For each arc in the tree set the cost of that arc to 0 As a
			 * consequence, the next tree returned by FLAC is preferentially
			 * merged by the current partial solution. In addition, this loop
			 * add the arc to the current partial solution.
			 */
			currentSol.addAll(tbest);
			reached.addAll(nodesReached);

			requiredVertices.removeAll(terminalsReached); // Remove the reached terminals from the required vertices of the instance
		
		}

		// Set the output of this algorithm : the returned tree and its cost

		arborescence = currentSol;
		int c = 0;
		if (arborescence != null)
			for (Arc a : arborescence)
				c += instance.getIntCost(a);

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
					Integer i1 = GFLACDisctributedAlgorithm.this.instance.getIntCost(o1);
					Integer i2 = GFLACDisctributedAlgorithm.this.instance.getIntCost(o2);
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

	private TreeSet<Arc> getSortedInputArcs(Integer saturatingNode) {
		TreeSet<Arc> tree = sortedInputArcs.get(saturatingNode);
		if(tree == null)
			tree = sortInputArcs(saturatingNode);
		return tree;
	}

	// ---------- density Directed Steiner Tree part ---------

	/**
	 * @return a tree rooted in the root of the instance spanning a part of the
	 * terminals, and the set of those terminals.
	 */
	private Triplet<HashSet<Arc>, HashSet<Integer>, HashSet<Integer>> applyFLAC() {
		reinit();

		while(true){
			//			System.out.println("==>");
			Integer saturatingNode = getNextSaturatingNode();
			//			System.out.println(saturatingNode +" "+flowTimeArrivals);
			if(saturatingNode != null){
				buildFlowTimeArrivals(saturatingNode);
				if(reached.contains(saturatingNode))
					return buildTree(saturatingNode);
				computeEnteringArcsSaturatingTimes(saturatingNode);
			}
			else{
				markArc();
			}
			//			System.out.println(saturatingNode +" "+flowTimeArrivals);
			//			System.out.println("<==");
			//			System.out.println();
		}
	}

	private HashSet<Arc> markedArcs;

	private LinkedList<Integer> nextSaturatingNodes;

	private HashMap<Integer, List<Triplet<Double, List<Integer>, Arc>>> flowTimeArrivals;

	private HashMap<Arc, Double> arcsSaturatingTimes;

	private HashMap<Integer, Integer> numberOfWaitingArcs;

	private void init(){
		markedArcs = new HashSet<Arc>();
		nextSaturatingNodes = new LinkedList<Integer>();
		flowTimeArrivals = new HashMap<Integer, List<Triplet<Double, List<Integer>, Arc>>>();
		arcsSaturatingTimes = new HashMap<Arc, Double>();
		numberOfWaitingArcs = new HashMap<Integer, Integer>();
	}

	private void reinit(){
		markedArcs.clear();
		nextSaturatingNodes.clear();
		flowTimeArrivals.clear();
		arcsSaturatingTimes.clear();
		numberOfWaitingArcs.clear();

		for(Integer terminal : this.requiredVertices)
			initTerminal(terminal);
	}

	private void initTerminal(Integer v){
		Integer nb = instance.getGraph().getOutputSize(v);
		if(nb == 0)
			this.addToSaturatingNodes(v);
		else
			this.numberOfWaitingArcs.put(v, nb);
	}

	private void addToSaturatingNodes(Integer node){
		nextSaturatingNodes.add(node);
	}

	private Integer getNextSaturatingNode(){
		return nextSaturatingNodes.pollFirst();
	}

	private Triplet<HashSet<Arc>, HashSet<Integer>, HashSet<Integer>> buildTree(Integer saturatingNode) {
		List<Triplet<Double, List<Integer>, Arc>> flowTimeArrivals = this.getFlowTimeArrivals(saturatingNode);
		Triplet<Double, List<Integer>, Arc> firstReachers = flowTimeArrivals.get(0);

		Triplet<HashSet<Arc>, HashSet<Integer>, HashSet<Integer>> result = 
				new Triplet<HashSet<Arc>, HashSet<Integer>, HashSet<Integer>>();
		HashSet<Arc> saturatedArcs = new HashSet<>();
		HashSet<Integer> reachedNodes = new HashSet<Integer>();
		HashSet<Integer> reachedTerminals = new HashSet<Integer>(firstReachers.second);
		result.first = saturatedArcs;
		result.second = reachedNodes;
		result.third = reachedTerminals;

		LinkedList<Couple<Arc, HashSet<Integer>>> toVisit = new LinkedList<Couple<Arc, HashSet<Integer>>>();
		toVisit.add(new Couple<Arc, HashSet<Integer>>(firstReachers.third, reachedTerminals));
		while(!toVisit.isEmpty()){
			Couple<Arc, HashSet<Integer>> c = toVisit.pollFirst();
			Arc a = c.first;
//			System.out.println(saturatingNode+" "+c);
			HashSet<Integer> toReach = c.second;
			Integer v = a.getOutput();
			saturatedArcs.add(a);
			reachedNodes.add(v);

			List<Triplet<Double, List<Integer>, Arc>> flowTimeArrivals2 = this.getFlowTimeArrivals(v);
			for(Triplet<Double, List<Integer>, Arc> triplet : flowTimeArrivals2){
				if(triplet.third == null)
					continue;
				Couple<Arc, HashSet<Integer>> toAdd = null;
				for(Integer terminal : triplet.second){
					if(toReach.contains(terminal)){
						if(toAdd == null){
							toAdd = new Couple<Arc, HashSet<Integer>>();
							toAdd.first = triplet.third;
							toAdd.second = new HashSet<Integer>();
						}
						toAdd.second.add(terminal);
					}
				}
				if(toAdd != null)
					toVisit.add(toAdd);
			}
		}
		return result;
	}

	private void setFlowTimeArrivals(Integer saturatingNode,
			List<Triplet<Double, List<Integer>, Arc>> flowTimeArrivals) {
		this.flowTimeArrivals.put(saturatingNode, flowTimeArrivals);

	}

	private List<Triplet<Double, List<Integer>, Arc>> getFlowTimeArrivals(Integer v) {
		return this.flowTimeArrivals.get(v);
	}

	private void buildFlowTimeArrivals(Integer saturatingNode){
		HashSet<Integer> isUsed = new HashSet<Integer>();

		List<Triplet<Double, List<Integer>, Arc>> flowTimeArrivals = new LinkedList<Triplet<Double, List<Integer>, Arc>>();
		List<FTAIterator> childrenFlowTimeArrivalsInformation = getChildrenFlowTimeArrivalsInformation(saturatingNode);
		
		while(!childrenFlowTimeArrivalsInformation.isEmpty()){
			Iterator<FTAIterator> cFTAIIterator = childrenFlowTimeArrivalsInformation.iterator();    
			Triplet<Double, List<Integer>, Arc> minFlowTimeArrival = getMinFlowTimeArrival(cFTAIIterator, isUsed);

			if(minFlowTimeArrival != null){
				for(Integer terminal : minFlowTimeArrival.second)
					isUsed.add(terminal);
				flowTimeArrivals.add(minFlowTimeArrival);
			}
		}
		
//		if(saturatingNode.equals(10))
//			System.out.println(flowTimeArrivals);
//		if(saturatingNode.equals(33))
//			System.out.println(flowTimeArrivals);

		this.setFlowTimeArrivals(saturatingNode, flowTimeArrivals);
	}



	private List<FTAIterator> getChildrenFlowTimeArrivalsInformation(Integer saturatingNode){

		List<FTAIterator> childrenFlowTimeArrivalsInformation = new LinkedList<FTAIterator>(); 

		Iterator<Arc> it = this.getInstance().getGraph().getOutputArcsIterator(saturatingNode);
		while(it.hasNext()){
			Arc a = it.next();
			if(this.isMarked(a))
				continue;

			Integer v = a.getOutput();

			Double saturatingTime = this.getArcSaturatingTime(a);
			List<Triplet<Double, List<Integer>, Arc>> childFlowTimeArrival = this.getFlowTimeArrivals(v);
			childrenFlowTimeArrivalsInformation.add(new FTAIterator(a, saturatingTime, childFlowTimeArrival));
		}

		if(this.requiredVertices.contains(saturatingNode)){
			Double saturatingTime = 0D;
			List<Triplet<Double, List<Integer>, Arc>> childFlowTimeArrival = new LinkedList<Triplet<Double, List<Integer>, Arc>>();
			Triplet<Double, List<Integer>, Arc> c = new Triplet<Double, List<Integer>, Arc>();
			c.first = 0D;
			c.second = new LinkedList<Integer>();
			c.second.add(saturatingNode);
			c.third = null;
			childFlowTimeArrival.add(c);
			childrenFlowTimeArrivalsInformation.add(new FTAIterator(null, saturatingTime, childFlowTimeArrival)); 
		}

		return childrenFlowTimeArrivalsInformation;
	}

	private Triplet<Double, List<Integer>, Arc> getMinFlowTimeArrival(Iterator<FTAIterator> cFTAIIterator, HashSet<Integer> isUsed){

		Triplet<Double, List<Integer>, Arc> minFlowTimeArrival = null;
		FTAIterator minFTAIterator = null;

		while(cFTAIIterator.hasNext()){
			FTAIterator childFlowTimeArrivalIterator = cFTAIIterator.next();

			Triplet<Double, List<Integer>, Arc> childNextFlowTimeArrival = childFlowTimeArrivalIterator.getCurrent();
			if (childNextFlowTimeArrival == null || this.isUsed(childNextFlowTimeArrival.second, isUsed)){
				cFTAIIterator.remove();
				continue;
			}

			Double childNextNewFlowRateTime = childNextFlowTimeArrival.first;

			if(minFlowTimeArrival == null || childNextNewFlowRateTime < minFlowTimeArrival.first){
				minFlowTimeArrival = childNextFlowTimeArrival;
				minFTAIterator = childFlowTimeArrivalIterator;
			}
		}
		
		if(minFTAIterator != null)
			minFTAIterator.next();
		return minFlowTimeArrival;
	}

	private boolean isUsed(List<Integer> terminals, HashSet<Integer> isUsed){
		for(Integer terminal : terminals)
			if(isUsed.contains(terminal))
				return true;
		return false;
	}

	private void computeEnteringArcsSaturatingTimes(Integer saturatingNode){
		TreeSet<Arc> tree = getSortedInputArcs(saturatingNode);
		Iterator<Arc> itTree = tree.iterator();

		Arc currentArc = null;
		while(itTree.hasNext()){
			Arc a = itTree.next();
			if(!isMarked(a)){
				currentArc = a;
				break;
			}
		}
		if(currentArc == null)
			return;

		Integer arcCost = instance.getIntCost(currentArc);

		List<Triplet<Double, List<Integer>, Arc>> flowTimeArrivals = this.getFlowTimeArrivals(saturatingNode);
		Iterator<Triplet<Double, List<Integer>, Arc>> itFTA = flowTimeArrivals.iterator();
		Triplet<Double, List<Integer>, Arc> nextFta = itFTA.next();

		Double flow = 0D;
		Double prevTime = 0D;
		Integer prevSources = 0;

		Double nextTime = nextFta.first;
		Integer nextSources = nextFta.second.size();


		while(true){
			Double deltaFlow = (nextTime - prevTime) * prevSources;

			if(deltaFlow == 0D || arcCost > deltaFlow + flow){
				prevTime = nextTime;
				prevSources += nextSources;
				flow = flow + deltaFlow;

				if(itFTA.hasNext()){
					nextFta = itFTA.next();
					nextTime = nextFta.first;
					nextSources = nextFta.second.size();
				}
				else{
					nextTime = Double.POSITIVE_INFINITY;
					nextSources = 0;
				}
			}
			else{
				Double time = (arcCost - flow) / prevSources + prevTime;
				this.setArcSaturatingTime(currentArc, time);

				currentArc = null;
				while(itTree.hasNext()){
					Arc a = itTree.next();
					if(!isMarked(a)){
						currentArc = a;
						break;
					}
				}
				if(currentArc == null)
					break;
				arcCost = instance.getIntCost(currentArc);
			}

		}
	}

	private Double getArcSaturatingTime(Arc a){
		return this.arcsSaturatingTimes.get(a);
	}

	private void setArcSaturatingTime(Arc a, Double time){
		this.arcsSaturatingTimes.put(a, time);
		Integer v = a.getInput();
		this.decreaseNumberOfWaitingArcs(v);
	}

	private void decreaseNumberOfWaitingArcs(Integer v){
		Integer nb = this.numberOfWaitingArcs.remove(v);
		if(nb == null)
			nb = instance.getGraph().getOutputSize(v);
		if(nb == 1){
			this.addToSaturatingNodes(v);
		}
		else
			this.numberOfWaitingArcs.put(v, nb - 1);
	}

	private void markArc(){
		Integer blockedNode = pollBlockedNode();
		Iterator<Arc> it = instance.getGraph().getOutputArcsIterator(blockedNode);

		while(it.hasNext()){
			Arc a = it.next();
			if(this.getArcSaturatingTime(a) == null)
				mark(a);
		}

		this.addToSaturatingNodes(blockedNode);
	}

	private Integer pollBlockedNode(){
		//		System.out.println(numberOfWaitingArcs);
		Integer min = Integer.MAX_VALUE;
		Integer minNode = null;
		for(Map.Entry<Integer,Integer> entry : this.numberOfWaitingArcs.entrySet()){
			Integer nbWA = entry.getValue();
			if(nbWA < min){
				min = nbWA;
				minNode = entry.getKey();
			}
		}
		this.numberOfWaitingArcs.remove(minNode);
		return minNode;
	}

	private void mark(Arc a){
		//		System.out.println("Mark " + a);
		markedArcs.add(a);
	}

	private boolean isMarked(Arc a){
		return markedArcs.contains(a);
	}

}

class FTAIterator{

	private Arc correspondingArc;
	private Double saturatingTime;
	private Triplet<Double, List<Integer>, Arc> current;
	private ListIterator<Triplet<Double, List<Integer>, Arc>> flowTimeArrivalIterator;

	public FTAIterator(Arc correspondingArc, Double saturatingTime, List<Triplet<Double, List<Integer>, Arc>> childFlowTimeArrival){

		this.correspondingArc = correspondingArc;
		this.saturatingTime = saturatingTime;
		this.flowTimeArrivalIterator = childFlowTimeArrival.listIterator();
		this.next();
	}

	public Triplet<Double, List<Integer>, Arc> getCurrent(){
		return this.current;
	}

	public Triplet<Double, List<Integer>, Arc> next(){
		if(!this.flowTimeArrivalIterator.hasNext()){
			this.current = null;
			return this.current;
		}
		
		Triplet<Double, List<Integer>, Arc> next = this.flowTimeArrivalIterator.next();
		this.current = new Triplet<Double, List<Integer>, Arc>();
		if(next.first <= this.saturatingTime){
			this.current.first = this.saturatingTime;
			this.current.second = new LinkedList<Integer>();
			do{
				this.current.second.addAll(next.second);
				if(this.flowTimeArrivalIterator.hasNext())
					next = this.flowTimeArrivalIterator.next();
				else
					next = null;
			} while(next != null && next.first <= this.saturatingTime);
			if(next != null)
				this.flowTimeArrivalIterator.previous();
			this.current.third = correspondingArc;

		}
		else{
			this.current.first = next.first;
			this.current.second = next.second;
			this.current.third = correspondingArc;
		}
		return this.current;
	}
}
