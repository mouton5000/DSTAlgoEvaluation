package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation;

import graphTheory.algorithms.Algorithm;
import graphTheory.graph.Arc;
import graphTheory.instances.Instance;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;

import java.util.HashSet;

public abstract class SteinerArborescenceApproximationAlgorithm extends
		Algorithm {

	protected SteinerDirectedInstance instance;

	protected HashSet<Arc> arborescence;
	protected Integer cost;

	public SteinerArborescenceApproximationAlgorithm() {
		this(null);
	}
	
	public SteinerArborescenceApproximationAlgorithm(SteinerDirectedInstance instance) {
		super();
		this.instance = instance;
	}

	public HashSet<Arc> getArborescence() {
		return arborescence;
	}
	
	public Integer getCost() {
		return cost;
	}

	public void setInstance(SteinerDirectedInstance instance) {
		this.instance = instance;
	}
	
	@Override
	protected void setNoSolution() {
		arborescence = null;
		cost = -1;
	}

	@Override
	protected Instance getInstance() {
		return instance;
	}

}

// TODO Relire
// TODO Refactor
// TODO Commenter
