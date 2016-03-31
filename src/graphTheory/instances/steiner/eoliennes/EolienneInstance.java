package graphTheory.instances.steiner.eoliennes;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;

import java.util.*;

/**
 * Created by mouton on 01/03/16.
 */
public class EolienneInstance extends SteinerDirectedInstance implements
        Cloneable {

    public EolienneInstance(DirectedGraph g) {
        super(g);
        capacities = new HashMap<Arc, Integer>();
        branchingNodeCosts = new HashMap<Integer, Integer>();
        maximumOutputDegree = new HashMap<Integer, Integer>();
    }

    private HashMap<Arc, Integer> capacities;

    private HashMap<Integer, Integer> branchingNodeCosts;

    private HashMap<Integer, Integer> maximumOutputDegree;

    /**
     * @param a
     * @return the capacity associated with the arc a in this instance.
     */
    public Integer getCapacity(Arc a) {
        return capacities.get(a);
    }

    /**
     * @param n1
     * @param n2
     * @return the capacity associated with the arc (n1,n2) in this instance. If (n1,n2) does not belong to
     * the graph associated with this instance, return null.
     */
    public Integer getCapacity(Integer n1, Integer n2){
        Arc a = this.getGraph().getLink(n1, n2);
        if (a != null)
            return getCost(a);
        else
            return null;
    }

    /**
     * Set the capacity of the arc a to capacity.
     *
     * @param a
     * @param capacity
     */
    public void setCapacity(Arc a, Integer capacity) {
        capacities.put(a, capacity);
    }

    /**
     * Set the capacity of the arc (n1,n2) to capacity. If (n1,n2) does not belong to
     * the graph associated with this instance, do nothing.
     *
     * @param n1
     * @param n2
     * @param capacity
     */
    public void setCapacity(Integer n1, Integer n2, Integer capacity) {
        Arc a = this.getGraph().getLink(n1, n2);
        if (a != null)
            setCapacity(a, capacity);
    }

    /**
     * @return all the capacities associated with the arc of this instance.
     */
    public HashMap<Arc, Integer> getCapacities() {
        HashMap<Arc, Integer> capacitiesCopy = new HashMap<Arc, Integer>();
        Iterator<Arc> it = graph.getEdgesIterator();
        while (it.hasNext()) {
            Arc a = it.next();
            capacitiesCopy.put(a, this.getCapacity(a));
        }
        return capacitiesCopy;
    }


    /**
     * Reset the cost of all the arcs, and associate the cost of each arc to the
     * one defined in the map costs.
     *
     * @param capacities
     */
    public void setCapacities(HashMap<Arc, Integer> capacities) {
        this.capacities = capacities;
    }

    public Integer getBranchingNodeCost(Integer node){
        return branchingNodeCosts.get(node);
    }

    public void setBranchingNodeCost(Integer node, Integer cost){
        branchingNodeCosts.put(node, cost);
    }

    public HashMap<Integer, Integer> getBranchingNodeCosts(){
        return new HashMap<Integer, Integer>(branchingNodeCosts);
    }

    public void setBranchingNodeCosts(HashMap<Integer, Integer> branchingNodeCosts) {
        this.branchingNodeCosts = branchingNodeCosts;
    }

    public Integer getMaximumOutputDegree(Integer node){
        return maximumOutputDegree.get(node);
    }

    public void setMaximumOutputDegree(Integer node, Integer degree){
        maximumOutputDegree.put(node, degree);
    }

    public HashMap<Integer, Integer> getMaximumOutputDegree(){
        return new HashMap<Integer, Integer>(maximumOutputDegree);
    }

    public void setMaximumOutputDegree(HashMap<Integer, Integer> maximumOutputDegree) {
        this.maximumOutputDegree = maximumOutputDegree;
    }

    public static EolienneInstance fromSteinerInstance(SteinerDirectedInstance sdi, int capacity, int branchingCost){
        EolienneInstance eol = new EolienneInstance(sdi.getGraph());
        for(Arc arc : eol.getGraph().getEdges()) {
            eol.setCapacity(arc, capacity);
            eol.setCost(arc, sdi.getCost(arc));
        }
        for(Integer node : eol.getGraph().getVertices()){
            eol.setBranchingNodeCost(node, branchingCost);
        }
        eol.setRoot(sdi.getRoot());
        for(Integer node : sdi.getRequiredVertices()){
            eol.setRequired(node);
        }


        return eol;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Noeuds\n\n");
        for (Integer n : graph.getVertices()) {
            s.append(n);
            if (isRequired(n))
                s.append(" ").append("x");
            s.append("\n");
        }

        s.append("\nArcs\n\n");
        for (Arc a : graph.getEdges())
            s.append(a).append(" ").append(getCost(a)).append(" ").append(getCapacity(a)).append("\n");

        return s.toString();
    }

}
