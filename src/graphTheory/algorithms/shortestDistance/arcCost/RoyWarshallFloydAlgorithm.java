package graphTheory.algorithms.shortestDistance.arcCost;

import graphTheory.algorithms.Algorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.shortestPath.ArcShortestPathsInstance;
import graphTheory.utils.Couple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * The Roy-Warshall-Floyd algorithm returns in O(n^3) all the shortest
 * paths between all couples of nodes.
 * 
 * 
 * 
 * @author Dimitri Watel
 *
 */
public class RoyWarshallFloydAlgorithm extends Algorithm<ArcShortestPathsInstance> {


	/**
	 * If true, do not compute the shortest paths, but only the costs of the
	 * shortest paths
	 */
	protected boolean computeOnlyCosts;

	/**
	 * For each couple of nodes (u,v), this map contains the shortest path from u to v
	 */
	protected HashMap<Couple<Integer,Integer>,LinkedList<Arc>> shPs;

	/**
	 * For each couple of nodes (u,v), this map contains the cost of the shortest path 
	 * from u to v
	 */
	protected HashMap<Couple<Integer,Integer>,Integer> costs;

	public HashMap<Couple<Integer,Integer>,LinkedList<Arc>> getShortestPaths() {
		return shPs;
	}
	
	public HashMap<Couple<Integer,Integer>,Integer> getCosts(){
		return costs;
	}

	public void setComputeOnlyCosts(boolean computeOnlyCosts){
		this.computeOnlyCosts = computeOnlyCosts;
	}
	
	@Override
	protected void setNoSolution() {
		shPs = null;
		costs = null;
	}
	
	@Override
	protected void computeWithoutTime() {
		HashMap<Couple<Integer, Integer>, Integer> csts = new HashMap<Couple<Integer,Integer>,Integer>();
		HashMap<Couple<Integer,Integer>,LinkedList<Arc>> shortestPaths = new HashMap<Couple<Integer,Integer>, LinkedList<Arc>>();

		init(csts, shortestPaths);

		Iterator<Integer> it = instance.getGraph().getVerticesIterator(), it2,it3;
		Integer u,v,w;
		while(it.hasNext()){
			u = it.next();
			it2 = instance.getGraph().getVerticesIterator();
			while(it2.hasNext()){
				v = it2.next();
				it3 = instance.getGraph().getVerticesIterator();
				while(it3.hasNext()){
					w = it3.next();
					update(csts,shortestPaths,u,v,w);
				}
			}
		}

		costs = csts;
		shPs = shortestPaths;
	}

	/**
	 * Initialise the shortest paths arrays.
	 * @param shorstestPaths 
	 */
	private void init(HashMap<Couple<Integer, Integer>, Integer> costs, 
			HashMap<Couple<Integer, Integer>, LinkedList<Arc>> shorstestPaths) {

		Iterator<Arc> it = instance.getGraph().getEdgesIterator();
		Arc a;
		Couple<Integer,Integer> c;
		LinkedList<Arc> l;
		while(it.hasNext()){
			a = it.next();
			c = new Couple<Integer,Integer>(a.getInput(), a.getOutput());
			int omega = instance.getIntCost(a);
			Integer c_om = costs.get(c);
			if(c_om == null || omega < c_om){
				costs.put(c, omega);
				if(!computeOnlyCosts){
					l = new LinkedList<Arc>();
					l.add(a);
					shorstestPaths.put(c, l);
				}
				if(!a.isDirected()){
					c = new Couple<Integer,Integer>(a.getOutput(), a.getInput());
					costs.put(c, omega);
					if(!computeOnlyCosts){
						l = new LinkedList<Arc>();
						l.add(a);
						shorstestPaths.put(c, l);
					}
				}
			}

		}

		Iterator<Integer> it2 = instance.getGraph().getVerticesIterator();
		Integer u;
		while(it2.hasNext()){
			u = it2.next();
			c = new Couple<Integer,Integer>(u,u);
			costs.put(c,0);
			if(!computeOnlyCosts){
				l = new LinkedList<Arc>();
				shorstestPaths.put(c, l);
			}
		}

	}


	/**
	 * Check if it is better to keep the current path from v to w or
	 * use the path going from v to u and then u to w. If the last case
	 * is the one, update the costs and shortest paths
	 */
	private void update(HashMap<Couple<Integer, Integer>, Integer> cst,
			HashMap<Couple<Integer,Integer>,LinkedList<Arc>> sh,
			Integer u, Integer v, Integer w) {
		Couple<Integer, Integer> 
		c1 = new Couple<Integer,Integer>(),
		c2 = new Couple<Integer,Integer>(),
		c3 = new Couple<Integer,Integer>();

		c1.first = v;
		c1.second = u;
		Integer vu = cst.get(c1);

		c2.first = u;
		c2.second = w;
		Integer uw = cst.get(c2);

		c3.first = v;
		c3.second = w;
		Integer vw = cst.get(c3);

		if(vu == null || uw == null)
			return;
		else if(vw == null){
			cst.put(c3, vu+uw);
			if(!computeOnlyCosts){
				LinkedList<Arc> l = new LinkedList<Arc>(sh.get(c1));
				l.addAll(sh.get(c2));
				sh.put(c3, l);
			}

		}
		else{
			if(vw > vu+uw)
			{
				cst.put(c3, vu+uw);
				if(!computeOnlyCosts){
					LinkedList<Arc> l = sh.get(c3);
					l.clear();
					l.addAll(sh.get(c1));
					l.addAll(sh.get(c2));
					sh.put(c3, l);
				}
			}
		}
	}

}