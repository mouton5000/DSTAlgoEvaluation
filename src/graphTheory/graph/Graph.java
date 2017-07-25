package graphTheory.graph;

import graphTheory.graphDrawer.symbols.ArcArrowSymbol;
import graphTheory.graphDrawer.symbols.ArcCircleMiddleDistanceSymbol;
import graphTheory.graphDrawer.symbols.ArcCircleStartAngleSymbol;
import graphTheory.graphDrawer.symbols.ArcLineSymbol;
import graphTheory.graphDrawer.symbols.ArcSymbol;
import graphTheory.graphDrawer.symbols.NodeCircleSymbol;
import graphTheory.graphDrawer.symbols.NodeSquareSymbol;
import graphTheory.graphDrawer.symbols.NodeSymbol;
import graphTheory.utils.Collections2;
import graphTheory.utils.Couple;
import graphTheory.utils.Foncteur;
import graphTheory.utils.MultiIterator;
import graphTheory.utils.Parametable;
import graphTheory.utils.PartialIterator;
import graphTheory.utils.Triplet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// Referenced classes of package graph:
// Node, Arc

/**
 * This class represent a Graph, with its vertices and its arcs or edges
 * (depending those are directed or not).
 */
public class Graph implements Parametable {

	/**
	 * List of nodes of this.
	 */
	protected Set<Integer> vertices;

	/**
	 * Set of virtually removed vertices. Those vertices do not appear while
	 * reading the graph.
	 */
	protected Set<Integer> vrmVertices;

	/**
	 * List of edges and arcs of this.
	 */
	protected Set<Arc> edges;

	/**
	 * Set of virtually removed edges and arcs. Those edges do not appear while
	 * reading the graph.
	 */
	protected Set<Arc> vrmEdges;

	/**
	 * Association between a node and the lists of directed arcs entering it,
	 * directed arcs outgoing from it and undirected arcs linked to it. <br/>
	 * links.get(n).first returns all the (-,n) directed arcs
	 * links.get(n).second returns all the (n,-) directed arcs
	 * links.get(n).third returns all the (n,-) or (-,n) undirected arcs.
	 */
	protected Map<Integer, Triplet<Set<Arc>, Set<Arc>, Set<Arc>>> links;

	/**
	 * This constructor build an empty graph.
	 * 
	 * @see #addVertice(Node)
	 * @see #addDirectedEdge(Node, Node)
	 * @see #addDirectedEdges(Node, Node...)
	 * @see #addUndirectedEdge(Node, Node)
	 * @see #addUndirectedEdges(Node, Node...)
	 */
	public Graph() {
		vertices = new HashSet<Integer>();
		edges = new HashSet<Arc>();
		params = new HashMap<String, Object>();

		vrmVertices = new HashSet<Integer>();
		vrmEdges = new HashSet<Arc>();

		links = new HashMap<Integer, Triplet<Set<Arc>, Set<Arc>, Set<Arc>>>();

		// Graph drawer fields
		this.nodeAbscissa = new HashMap<Integer, Integer>();
		this.nodeOrdinates = new HashMap<Integer, Integer>();
		this.drawnNodes = new HashMap<Integer, Boolean>();
		this.nodeColors = new HashMap<Integer, Color>();
		this.nodeFill = new HashMap<Integer, Boolean>();
		this.nodeSymbols = new HashMap<Integer, NodeSymbol>();
		this.nodeTextColor = new HashMap<Integer, Color>();
		this.drawnArcs = new HashMap<Arc, Boolean>();
		this.arcSymbols = new HashMap<Arc, ArcSymbol>();
		this.arcOutputSymbols = new HashMap<Arc, ArcArrowSymbol>();
		this.arcColors = new HashMap<Arc, Color>();
	}

	/*
	 * --------------------------------------------- Access to nodes and
	 * vertices ---------------------------------------------
	 */

	/**
	 * O(|Number of nodes|)
	 * 
	 * @return An ArrayList containing every node of this, excluding the
	 *         virtually removed vertices.
	 * @see #virtuallyRemoveVertice(Integer)
	 */
	public ArrayList<Integer> getVertices() {
		ArrayList<Integer> ar = new ArrayList<Integer>(vertices);
		ar.removeAll(vrmVertices);
		return ar;
	}

	/**
	 * O(|Number of nodes|)
	 * 
	 * @return A LinkedList containing every node of this, excluding the
	 *         virtually removed vertices.
	 * @see #virtuallyRemoveVertice(Integer)
	 */
	public LinkedList<Integer> getLinkedListVertices() {
		LinkedList<Integer> ar = new LinkedList<Integer>(vertices);
		ar.removeAll(vrmVertices);
		return ar;
	}

	/**
	 * O(|Number of nodes|)
	 * 
	 * @return An HashSet containing every node of this, excluding the virtually
	 *         removed vertices.
	 * @see #virtuallyRemoveVertice(Integer)
	 */
	public HashSet<Integer> getHashSetVertices() {
		HashSet<Integer> ar = new HashSet<Integer>(vertices);
		ar.removeAll(vrmVertices);
		return ar;
	}

	/**
	 * O(|Number of edges + arcs|)
	 * 
	 * @return An ArrayList containing every edge or arc of this, excluding the
	 *         virtually removed arcs and edges.
	 * @see #virtuallyRemoveEdge(Arc)
	 */
	public ArrayList<Arc> getEdges() {
		ArrayList<Arc> ar = new ArrayList<Arc>();
		for (Arc a : edges)
			if (!this.hasVirtuallyRemoved(a))
				ar.add(a);
		return ar;
	}

	/**
	 * O(|Number of edges + arcs|)
	 * 
	 * @return A LinkedList containing every edge or arc of this, excluding the
	 *         virtually removed arcs and edges.
	 * @see #virtuallyRemoveEdge(Arc)
	 */
	public LinkedList<Arc> getLinkedListEdges() {
		LinkedList<Arc> ar = new LinkedList<Arc>(edges);
		for (Arc a : edges)
			if (!this.hasVirtuallyRemoved(a))
				ar.add(a);
		return ar;
	}

	/**
	 * O(|Number of edges + arcs|)
	 * 
	 * @return An HashSet containing every edge or arc of this, excluding the
	 *         virtually removed arcs and edges.
	 * @see #virtuallyRemoveEdge(Arc)
	 */
	public HashSet<Arc> getHashSetEdges() {
		HashSet<Arc> ar = new HashSet<Arc>(edges);
		for (Arc a : edges)
			if (!this.hasVirtuallyRemoved(a))
				ar.add(a);
		return ar;
	}

	/**
	 * @return The number of nodes of this, excluding virtually removed nodes
	 * @see #virtuallyRemoveVertice(Integer)
	 */
	public int getNumberOfVertices() {
		return vertices.size() - vrmVertices.size();
	}

	/**
	 * @return The number of edges and arcs of this
	 * @see #virtuallyRemoveEdge(Arc)
	 */
	public int getNumberOfEdges() {
		int s = 0;
		for (Arc a : edges)
			if (!this.hasVirtuallyRemoved(a))
				s++;
		return s;
	}

	/**
	 * O(|ln(nodes)|)
	 * 
	 * @param n
	 * @return True if n belongs to this and is not virtually removed.
	 */
	public boolean contains(Integer n) {
		return links.containsKey(n) && !hasVirtuallyRemoved(n);
	}

	/**
	 * O(|(edges + arcs)|)
	 * 
	 * @param a
	 * @return True if a belongs to this and is not virtually removed.
	 */
	public boolean contains(Arc a) {
		return edges.contains(a) && !hasVirtuallyRemoved(a);
	}

	/*
	 * --------------------------------------------- Iterate on the vertices and
	 * the edges ---------------------------------------------
	 */

	/**
	 * @return A ListIterator over the list of nodes in this. The order of this
	 *         iterator is by default the order of insertion of each node. If
	 *         the methods {@link #sortVertices()} or
	 *         {@link #sortVertices(Comparator)} were called, this order is used
	 *         instead.
	 * 
	 * @see #sortVertices()
	 * @see #sortVertices(Comparator)
	 */
	public Iterator<Integer> getVerticesIterator() {
		return new PartialIterator<Integer>(vertices,
				new Foncteur<Integer, Boolean>() {

					@Override
					public Boolean apply(Integer n) {
						return !Graph.this.hasVirtuallyRemoved(n);
					}
				});

	}

	/**
	 * @return A ListIterator over the list of edges and arcs in this. The order
	 *         of this iterator is by default the order of insertion of each
	 *         edge and arc. If the methods {@link #sortEdges()} or
	 *         {@link #sortEdges(Comparator)} were called, this order is used
	 *         instead.
	 * 
	 * @see #sortEdges()
	 * @see #sortEdges(Comparator)
	 */
	public Iterator<Arc> getEdgesIterator() {
		return new PartialIterator<Arc>(edges, new Foncteur<Arc, Boolean>() {
			@Override
			public Boolean apply(Arc a) {
				return !Graph.this.hasVirtuallyRemoved(a);
			}
		});

	}

	/*
	 * --------------------------------------------- Get access to neighbours
	 * ---------------------------------------------
	 */

	/**
	 * @return The neighbour of n in this linked with a or null if either n or a
	 *         do not belong to this or if n do not belong to a, or if a or n
	 *         are virtually removed.
	 */
	public Integer getNeighbourNode(Integer n, Arc a) {
		if (!this.contains(n) || !this.contains(a))
			return null;
		if (a.getInput().equals(n))
			return a.getOutput();
		else if (a.getOutput().equals(n))
			return a.getInput();
		else
			return null;

	}

	/**
	 * Return the (n1,n2) directed arc, the (n1,n2) undirected arc, or the (n2,
	 * n1) undirected arc in this. If more than one arc link those nodes, the
	 * directed ones are returned in priority. There is no mean to know which
	 * one is returned if there are more than one directed arc, or more than one
	 * undirected edge and no directed arc. If there is no arc, null is
	 * returned.
	 * 
	 * @param n1
	 * @param n2
	 * @return An arc or edge linking n1 and n2, null if no link exists or if n1
	 *         or n2 does not belong to this, or are virtually removed.
	 */
	public Arc getLink(Integer n1, Integer n2) {
		Arc a = getDirectedArcLinking(n1, n2);
		if (a != null)
			return a;
		a = getUndirectedEdgeLinking(n1, n2);
		return a;
	}

	/**
	 * 
	 * @return True if it exists a (n1,n2) directed arc, a (n1,n2) undirected
	 *         edge, or a (n2, n1) undirected edge in this. False if not or if
	 *         no link exists or f n1 or n2 does not belong to this, or are
	 *         virtually removed.
	 */
	public boolean areLinkedByArcOrEdge(Integer n1, Integer n2) {
		return getLink(n1, n2) != null;
	}

	/**
	 * Return the (input,output) directed arc in this. If more than one arc link
	 * those nodes, there is no mean to know which one is returned. If there is
	 * no arc, null is returned.
	 * 
	 * @param input
	 * @param output
	 * @return An arc linking input and output, null if no link exists or f n1
	 *         or n2 does not belong to this, or are virtually removed.
	 */
	public Arc getDirectedArcLinking(Integer input, Integer output) {
		Set<Arc> inputOutputs = getOutputs(input);
		if (inputOutputs == null)
			return null;
		int ioSize = inputOutputs.size();

		Set<Arc> outputInputs = getInputs(output);
		if (outputInputs == null)
			return null;
		int oiSize = outputInputs.size();

		Set<Arc> searchList = (ioSize < oiSize) ? inputOutputs : outputInputs;
		for (Arc a : searchList) {
			if (a.getInput().equals(input) && a.getOutput().equals(output))
				return a;
		}

		return null;
	}

	/**
	 * @return True if it exists a (input,output) directed arc in this. False if
	 *         not or if no link exists or f n1 or n2 does not belong to this,
	 *         or are virtually removed.
	 */
	public boolean areLinkedByDirectedArc(Integer input, Integer output) {
		return getDirectedArcLinking(input, output) != null;
	}

	/**
	 * Return the (n1,n2) undirected edge, or the (n2, n1) undirected edge in
	 * this. If more than one arc link those nodes, there is no mean to know
	 * which one is returned if there are more than one undirected edge. If
	 * there is no edge, null is returned.
	 * 
	 * @param n1
	 * @param n2
	 * @return An edge linking n1 and n2, null if no link exists or if n1 or n2
	 *         does not belong to this, or are virtually removed.
	 */
	public Arc getUndirectedEdgeLinking(Integer n1, Integer n2) {
		Set<Arc> n1UndirNeigh = getUndirectedNeighours(n1);
		if (n1UndirNeigh == null)
			return null;
		int n1uSize = n1UndirNeigh.size();

		Set<Arc> n2UndirNeigh = getUndirectedNeighours(n2);
		if (n2UndirNeigh == null)
			return null;
		int n2uSize = n2UndirNeigh.size();

		Set<Arc> searchList = (n1uSize < n2uSize) ? n1UndirNeigh : n2UndirNeigh;
		for (Arc a : searchList) {
			if ((a.getInput().equals(n1) && a.getOutput().equals(n2))
					|| (a.getInput().equals(n2) && a.getOutput().equals(n1)))
				return a;
		}

		return null;
	}

	/**
	 * 
	 * @return True if it exists a (n1,n2) directed arc, a (n1,n2) undirected
	 *         edge, or a (n2, n1) undirected edge in this. False if not or if
	 *         n1 or n2 does not belong to this, or are virtually removed.
	 */
	public boolean areLinkedByUndirectedEdge(Integer n1, Integer n2) {
		return getUndirectedEdgeLinking(n1, n2) != null;
	}

	/**
	 * @param n
	 * @return The list of arcs keyed by the node n in links. Null if n do not
	 *         belong to this graph, or is virtually removed.
	 */
	private Triplet<Set<Arc>, Set<Arc>, Set<Arc>> getLinks(Integer n) {
		if (this.hasVirtuallyRemoved(n))
			return null;
		Triplet<Set<Arc>, Set<Arc>, Set<Arc>> nodeLinks = links.get(n);
		return nodeLinks;
	}

	/*
	 * --------------------------------------------- Get access to directed
	 * input neighbours ---------------------------------------------
	 */

	/**
	 * @param n
	 * @return The list of directed arcs entering this node in this, including
	 *         the virtually removed edges. Null if n does not belong to this
	 *         graph, or is virtually removed.
	 */
	private Set<Arc> getInputs(Integer n) {
		Triplet<Set<Arc>, Set<Arc>, Set<Arc>> nodeLinks = getLinks(n);
		if (nodeLinks == null)
			return null;
		return getLinks(n).first;
	}

	private boolean fillWithInputArcs(Integer n, Collection<Arc> col) {
		Set<Arc> l = getInputs(n);
		if (l == null)
			return false;
		for (Arc a : l)
			if (!this.hasVirtuallyRemoved(a))
				col.add(a);
		return true;
	}

	/**
	 * O(|number of input arcs|))
	 * 
	 * Return an ArrayList of directed arcs entering this node in this.
	 * getInputArcs(n) returns all the (-,n) directed arcs of this. It returns
	 * null if n do not belong to this.
	 * 
	 * @return An ArrayList of directed arcs entering this node in this, null if
	 *         n does not belong to this graph, or is virtually removed.
	 */
	public ArrayList<Arc> getInputArcs(Integer n) {
		ArrayList<Arc> col = new ArrayList<Arc>();
		if (fillWithInputArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * O(|number of input arcs|))
	 * 
	 * Return a LinkedList of directed arcs entering this node in this.
	 * getInputArcs(n) returns all the (-,n) directed arcs of this. It returns
	 * null if n do not belong to this.
	 * 
	 * @return A LinkedList of directed arcs entering this node in this, null if
	 *         n does not belong to this graph, or is virtually removed.
	 */
	public LinkedList<Arc> getLinkedListInputArcs(Integer n) {
		LinkedList<Arc> col = new LinkedList<Arc>();
		if (fillWithInputArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * O(|number of input arcs|))
	 * 
	 * Return an HashSet of directed arcs entering this node in this.
	 * getInputArcs(n) returns all the (-,n) directed arcs of this. It returns
	 * null if n do not belong to this.
	 * 
	 * @return An HashSet of directed arcs entering this node in this, null if n
	 *         does not belong to this graph, or is virtually removed.
	 */
	public HashSet<Arc> getHashSetInputArcs(Integer n) {
		HashSet<Arc> col = new HashSet<Arc>();
		if (fillWithInputArcs(n, col))
			return col;
		else
			return null;

	}

	/**
	 * @return The number of arcs entering n in this, null if n does not belong
	 *         to this graph, or is virtually removed.
	 */
	public Integer getInputSize(Integer n) {
		Set<Arc> l = getHashSetInputArcs(n);
		if (l == null)
			return null;
		return l.size();
	}

	/**
	 * @param n
	 * @return a list iterator over the arcs entering n in this null if n does
	 *         not belong to this graph, or is virtually removed.
	 */
	public Iterator<Arc> getInputArcsIterator(Integer n) {
		Set<Arc> l = getInputs(n);

		if (l == null)
			return null;
		return new PartialIterator<Arc>(l, new Foncteur<Arc, Boolean>() {

			@Override
			public Boolean apply(Arc a) {
				return !Graph.this.hasVirtuallyRemoved(a);
			}
		});
	}

	private boolean fillWithInputNodes(Integer n, Collection<Integer> col) {
		Set<Arc> l = getInputs(n);
		if (l == null)
			return false;
		for (Arc a : l)
			if (!this.hasVirtuallyRemoved(a))
				col.add(a.getInput());
		return true;
	}

	/**
	 * @return An ArrayList containing the nodes m linked to the node n with a
	 *         directed (m,n) arc, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public ArrayList<Integer> getInputNodes(Integer n) {
		ArrayList<Integer> col = new ArrayList<Integer>();
		if (fillWithInputNodes(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return A LinkedList containing the nodes m linked to the node n with a
	 *         directed (m,n) arc, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public LinkedList<Integer> getLinkedListInputNodes(Integer n) {
		LinkedList<Integer> col = new LinkedList<Integer>();
		if (fillWithInputNodes(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return An HashSet containing the nodes m linked to the node n with a
	 *         directed (m,n) arc, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public HashSet<Integer> getHashSetInputNodes(Integer n) {
		HashSet<Integer> col = new HashSet<Integer>();
		if (fillWithInputNodes(n, col))
			return col;
		else
			return null;
	}

	/*
	 * --------------------------------------------- Get access to output
	 * neighbours ---------------------------------------------
	 */

	/**
	 * Return t
	 * 
	 * @param n
	 * @return The list of arcs keyed by the node n in outputs, null if n do not
	 *         belong to this graph.
	 */
	private Set<Arc> getOutputs(Integer n) {
		Triplet<Set<Arc>, Set<Arc>, Set<Arc>> nodeLinks = getLinks(n);
		if (nodeLinks == null)
			return null;
		return nodeLinks.second;
	}

	private boolean fillWithOutputArcs(Integer n, Collection<Arc> col) {
		Set<Arc> l = getOutputs(n);
		if (l == null)
			return false;
		for (Arc a : l)
			if (!this.hasVirtuallyRemoved(a))
				col.add(a);
		return true;
	}

	/**
	 * O(|number of output arcs|))
	 * 
	 * Return an ArrayList of directed arcs outgoing from this node in this.
	 * getOutputArcs(n) returns all the (n,-) directed arcs of this.
	 * 
	 * @return An ArrayList of directed arcs entering this node in this, null if
	 *         n does not belong to this graph, or is virtually removed.
	 */
	public ArrayList<Arc> getOutputArcs(Integer n) {
		ArrayList<Arc> col = new ArrayList<Arc>();
		if (fillWithOutputArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * O(|number of output arcs|))
	 * 
	 * Return a LinkedList of directed arcs outgoing from this node in this.
	 * getOutputArcs(n) returns all the (n,-) directed arcs of this.
	 * 
	 * @return A LinkedList of directed arcs entering this node in this, null if
	 *         n does not belong to this graph, or is virtually removed.
	 */
	public LinkedList<Arc> getLinkedListOutputArcs(Integer n) {
		LinkedList<Arc> col = new LinkedList<Arc>();
		if (fillWithOutputArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * O(|number of output arcs|))
	 * 
	 * Return an HashSet of directed arcs outgoing from this node in this.
	 * getOutputArcs(n) returns all the (n,-) directed arcs of this.
	 * 
	 * @return An HashSet of directed arcs entering this node in this, null if n
	 *         does not belong to this graph, or is virtually removed.
	 */
	public HashSet<Arc> getHashSetOutputArcs(Integer n) {
		HashSet<Arc> col = new HashSet<Arc>();
		if (fillWithOutputArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return The number of arcs outgoing from n in this, null if n does not
	 *         belong to this graph, or is virtually removed.
	 */
	public Integer getOutputSize(Integer n) {
		Set<Arc> l = getHashSetOutputArcs(n);
		if (l == null)
			return null;
		return l.size();
	}

	/**
	 * @param n
	 * @return a list iterator over the arcs outgoing from n in this, null if n
	 *         does not belong to this graph, or is virtually removed.
	 */
	public Iterator<Arc> getOutputArcsIterator(Integer n) {
		Set<Arc> l = getOutputs(n);
		if (l == null)
			return null;
		return new PartialIterator<Arc>(l, new Foncteur<Arc, Boolean>() {

			@Override
			public Boolean apply(Arc a) {
				return !Graph.this.hasVirtuallyRemoved(a);
			}
		});
	}

	private boolean fillWithOutputNodes(Integer n, Collection<Integer> col) {
		Set<Arc> l = getOutputs(n);
		if (l == null)
			return false;
		for (Arc a : l)
			if (!this.hasVirtuallyRemoved(a))
				col.add(a.getOutput());
		return true;
	}

	/**
	 * @return An ArrayList containing the nodes m linked to the node n with a
	 *         directed (n,m) arc, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public ArrayList<Integer> getOutputNodes(Integer n) {
		ArrayList<Integer> col = new ArrayList<Integer>();
		if (fillWithOutputNodes(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return A LinkedList containing the nodes m linked to the node n with a
	 *         directed (n,m) arc, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public LinkedList<Integer> getLinkedListOutputNodes(Integer n) {
		LinkedList<Integer> col = new LinkedList<Integer>();
		if (fillWithOutputNodes(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return An HashSet containing the nodes m linked to the node n with a
	 *         directed (n,m) arc, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public HashSet<Integer> getHashSetOutputNodes(Integer n) {
		HashSet<Integer> col = new HashSet<Integer>();
		if (fillWithOutputNodes(n, col))
			return col;
		else
			return null;
	}

	/*
	 * --------------------------------------------- Get access to undirected
	 * neighbours ---------------------------------------------
	 */

	/**
	 * @param n
	 * @return The list of arcs keyed by the node n in undirectedNeighbour, null
	 *         if n do not belong to this graph.
	 */
	private Set<Arc> getUndirectedNeighours(Integer n) {
		Triplet<Set<Arc>, Set<Arc>, Set<Arc>> nodeLinks = getLinks(n);
		if (nodeLinks == null)
			return null;
		return nodeLinks.third;
	}

	private boolean fillWithUndirectedNeighbourArcs(Integer n,
			Collection<Arc> col) {
		Set<Arc> l = getUndirectedNeighours(n);
		if (l == null)
			return false;
		for (Arc a : l)
			if (!this.hasVirtuallyRemoved(a))
				col.add(a);
		return true;
	}

	/**
	 * O(|number of undirected edges linked to n|))
	 * 
	 * Return an ArrayList of undirected edges linked to this node in this.
	 * getUndirectedNeighbourEdges(n) returns all the (-,n) and (n,-) undirected
	 * edges of this.
	 * 
	 * @return An ArrayList of undirected edges linked to this node in this,
	 *         null if n does not belong to this graph, or is virtually removed.
	 */
	public ArrayList<Arc> getUndirectedNeighbourEdges(Integer n) {
		ArrayList<Arc> col = new ArrayList<Arc>();
		if (fillWithUndirectedNeighbourArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * O(|number of undirected edges linked to n|))
	 * 
	 * Return a LinkedList of undirected edges linked to this node in this.
	 * getLinkedListUndirectedNeighbourEdges(n) returns all the (-,n) and (n,-)
	 * undirected edges of this.
	 * 
	 * @return A LinkedList of undirected edges linked to this node in this,
	 *         null if n does not belong to this graph, or is virtually removed.
	 */
	public LinkedList<Arc> getLinkedListUndirectedNeighbourEdges(Integer n) {
		LinkedList<Arc> col = new LinkedList<Arc>();
		if (fillWithUndirectedNeighbourArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * O(|number of undirected edges linked to n|))
	 * 
	 * Return an HashSet of undirected edges linked to this node in this.
	 * getHashSetUndirectedNeighbourEdges(n) returns all the (-,n) and (n,-)
	 * undirected edges of this.
	 * 
	 * @return An HashSet of undirected edges linked to this node in this, null
	 *         if n does not belong to this graph, or is virtually removed.
	 */
	public HashSet<Arc> getHashSetUndirectedNeighbourEdges(Integer n) {
		HashSet<Arc> col = new HashSet<Arc>();
		if (fillWithUndirectedNeighbourArcs(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return The number of undirected edges linked to n in this, null if n
	 *         does not belong to this graph, or is virtually removed.
	 */
	public Integer getUndirectedNeighbourSize(Integer n) {
		Set<Arc> l = getHashSetUndirectedNeighbourEdges(n);
		if (l == null)
			return null;
		return l.size();
	}

	/**
	 * @param n
	 * @return a list iterator over the undirected edges linked to n in this,
	 *         null if n does not belong to this graph, or is virtually removed.
	 */
	public Iterator<Arc> getUndirectedNeighbourEdgesIterator(Integer n) {
		Set<Arc> l = getUndirectedNeighours(n);
		if (l == null)
			return null;
		return new PartialIterator<Arc>(l, new Foncteur<Arc, Boolean>() {

			@Override
			public Boolean apply(Arc a) {
				return !Graph.this.hasVirtuallyRemoved(a);
			}
		});
	}

	private boolean fillWithUndirectedNeighbourNodes(Integer n,
			Collection<Integer> col) {
		Set<Arc> l = getUndirectedNeighours(n);
		if (l == null)
			return false;
		for (Arc a : l)
			if (!this.hasVirtuallyRemoved(a))
				col.add(this.getNeighbourNode(n, a));
		return true;
	}

	/**
	 * @return An ArrayList containing the nodes m linked to the node n with a
	 *         undirected (n,m) or (m,n) edge, null if n does not belong to this
	 *         graph, or is virtually removed.
	 */
	public ArrayList<Integer> getUndirectedNeighbourNodes(Integer n) {
		ArrayList<Integer> col = new ArrayList<Integer>();
		if (fillWithUndirectedNeighbourNodes(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return A linkedList containing the nodes m linked to the node n with a
	 *         undirected (n,m) or (m,n) edge, null if n does not belong to this
	 *         graph, or is virtually removed.
	 */
	public LinkedList<Integer> getLinkedListUndirectedNeighbourNodes(Integer n) {
		LinkedList<Integer> col = new LinkedList<Integer>();
		if (fillWithUndirectedNeighbourNodes(n, col))
			return col;
		else
			return null;
	}

	/**
	 * @return An HashSet containing the nodes m linked to the node n with a
	 *         undirected (n,m) or (m,n) edge, null if n does not belong to this
	 *         graph, or is virtually removed.
	 */
	public HashSet<Integer> getHashSetUndirectedNeighbourNodes(Integer n) {
		HashSet<Integer> col = new HashSet<Integer>();
		if (fillWithUndirectedNeighbourNodes(n, col))
			return col;
		else
			return null;
	}

	/**
	 * O(|number of n neighbours (directed and undirected)|)
	 * 
	 * @param n
	 * @return An HashSet of directed and undirected arcs linked to the node n
	 *         in this, null if n does not belong to this graph, or is virtually
	 *         removed.
	 */
	public Set<Arc> getAllNeighbourEdges(Integer n) {

		Set<Arc> col = new HashSet<Arc>();
		if (!fillWithInputArcs(n, col))
			return null;
		if (!fillWithOutputArcs(n, col))
			return null;
		if (!fillWithUndirectedNeighbourArcs(n, col))
			return null;
		return col;
	}

	/**
	 * 
	 * @param n
	 * @return The number of arcs (directed and undirected) linked to the node n
	 *         in this, null if n does not belong to this graph, or is virtually
	 *         removed.
	 */
	public int getAllNeighboursSize(Integer n) {
		return this.getInputSize(n) + this.getOutputSize(n)
				+ this.getUndirectedNeighbourSize(n);
	}

	/**
	 * 
	 * @param n
	 * @return An iterator over all the arcs (directed and undirected) linked to
	 *         the node n in this, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public Iterator<Arc> getAllNeighbourIterator(Integer n) {

		final Iterator<Arc> li = getInputArcsIterator(n);
		final Iterator<Arc> lo = getOutputArcsIterator(n);
		final Iterator<Arc> lu = getUndirectedNeighbourEdgesIterator(n);

		if (li == null || lo == null || lu == null)
			return null;

		return new Iterator<Arc>() {

			@Override
			public boolean hasNext() {
				return lu.hasNext() || lo.hasNext() || li.hasNext();
			}

			@Override
			public Arc next() {
				if (li.hasNext())
					return li.next();
				if (lo.hasNext())
					return lo.next();
				if (lu.hasNext())
					return lu.next();
				return null;
			}

			@Override
			public void remove() {
			}
		};
	}

	/**
	 * 
	 * @param n
	 * @return An HashSet of nodes linked with directed and undirected arcs to
	 *         the node n in this, null if n does not belong to this graph, or
	 *         is virtually removed.
	 */
	public Set<Integer> getAllNeighbourNodes(Integer n) {
		HashSet<Integer> h = new HashSet<Integer>();
		for (Arc a : getAllNeighbourEdges(n))
			h.add(this.getNeighbourNode(n, a));
		return h;
	}

	/*
	 * --------------------------------------------- Add and remove vertices,
	 * edges and arcs ---------------------------------------------
	 */

	/**
	 * Add node to this (unless it already contains it). This node is not linked
	 * to any other node of this.
	 * 
	 * @param node
	 *            The node to be added.
	 * @return true if the node was added. False if this already contains it.
	 */
	public boolean addVertice(int node) {
		if (links.containsKey(node))
			return false;
		Triplet<Set<Arc>, Set<Arc>, Set<Arc>> nodeLinks = new Triplet<Set<Arc>, Set<Arc>, Set<Arc>>(
				new HashSet<Arc>(), new HashSet<Arc>(), new HashSet<Arc>());
		links.put(node, nodeLinks);
		return vertices.add(node);

	}

	/**
	 * Remove node from this (unless it do not contains it). Remove also every
	 * arcs and edges linked to it. <br/>
	 * 
	 * @param node
	 * @return true if the vertice was removed, false if the node was null or
	 *         not in the graph, or if it was virtually removed.
	 */
	public boolean removeVertice(Integer node) {
		if (node == null || !this.contains(node))
			return false;

		for (Arc a : this.getAllNeighbourEdges(node))
			removeEdge(a);

		links.remove(node);
		return vertices.remove(node);
	}

	/**
	 * Add the (input,output) directed arc to this and return it. Return null
	 * and do nothing if input or output does not belong to this or if the arc
	 * was already added or virtually removed.
	 * 
	 * @param input
	 *            Input node of the added arc
	 * @param output
	 *            Output node of the added arc
	 * @return The new (input, output) directed arc, null if input or output
	 *         does not belong to this.
	 */
	public Arc addDirectedEdge(Integer input, Integer output) {
		return addArc(input, output, true);
	}

	/**
	 * 
	 * Add the (input,output) directed arc to this for each node output in
	 * outputs and returns the list of added arcs. If input does not belong to
	 * this, a empty list is returned. For each output node in outputs which
	 * does not belong to this, the arc is not added neither to the graph nor to
	 * the returned list.
	 * 
	 * @param input
	 *            Input node of each added arc
	 * @param outputs
	 *            List of output nodes of all added arcs.
	 * @return The list of added arcs.
	 */
	public LinkedList<Arc> addDirectedEdges(Integer input, Integer... outputs) {
		LinkedList<Arc> l = new LinkedList<Arc>();
		for (Integer output : outputs) {
			Arc a = addDirectedEdge(input, output);
			if (a != null)
				l.add(a);
		}
		return l;
	}

	/**
	 * Remove every directed arc (input, output) in this. Do nothing if input or
	 * output does not belong to this. <br/>
	 * .
	 * 
	 * @param input
	 * @param output
	 */
	public void removeDirectedArcs(Integer input, Integer output) {
		removeArcs(input, output, true);
	}

	/**
	 * Add the (n1,n2) undirected arc to this and return it. Return null and do
	 * nothing if input or output does not belong to this or if the arc was
	 * already added or virtually removed.
	 * 
	 * @param n1
	 *            First end node of the added arc
	 * @param n2
	 *            Second end node of the added arc
	 * @return The new (n1,n2) undirected arc, null if n1 or n2 does not belong
	 *         to this.
	 */
	public Arc addUndirectedEdge(Integer n1, Integer n2) {
		return addArc(n1, n2, false);
	}

	/**
	 * Add the (n1,n2) undirected arc to this for each node n2 in n2s and return
	 * the list of added arcs. If n1 does not belong to this, a empty list is
	 * returned. For each n2 node in n2s which does not belong to this, the arc
	 * is not added neither to the graph nor to the returned list.
	 * 
	 * @param n1
	 *            First end node of each added arc
	 * @param n2s
	 *            List of second end node of all added arc
	 * @return The list of all undirected added arc
	 */
	public LinkedList<Arc> addUndirectedEdges(Integer n1, Integer... n2s) {
		LinkedList<Arc> l = new LinkedList<Arc>();
		for (Integer n2 : n2s) {
			Arc a = addUndirectedEdge(n1, n2);
			if (a != null)
				l.add(a);

		}
		return l;
	}

	/**
	 * Remove every undirected arc linking nodes n1 and n2 in this. Do nothing
	 * if n1 or n2t does not belong to this. <br/>
	 * 
	 * @param n1
	 * @param n2
	 */
	public void removeUndirectedEdges(Integer n1, Integer n2) {
		removeArcs(n1, n2, false);
	}

	/**
	 * Remove every directed and undirected arc linking nodes n1 and n2 in this
	 * (including (n2,n1) directed arcs). Do nothing if n1 or n2 does not belong
	 * to this.
	 * 
	 * @param n1
	 * @param n2
	 */
	public void removeEdges(Integer n1, Integer n2) {
		removeDirectedArcs(n1, n2);
		removeDirectedArcs(n2, n1);
		removeUndirectedEdges(n1, n2);
	}

	/**
	 * Remove the edge a (if it was contained in this).
	 * 
	 * @return True if the arc or edge was successfully removed, False if a does
	 *         not belong to this or if it was virtually removed.
	 */
	public boolean removeEdge(Arc a) {
		if (this.hasVirtuallyRemoved(a) || !edges.remove(a)) // true if a == null
			return false;

		if (a.isDirected()) {
			getOutputs(a.getInput()).remove(a);
			getInputs(a.getOutput()).remove(a);
		} else {
			getUndirectedNeighours(a.getInput()).remove(a);
			getUndirectedNeighours(a.getOutput()).remove(a);
		}
		return true;

	}

	/**
	 * Link n1 and n2 by a arc (n1,n2), add the arc to this and return it. If
	 * directed is true, the arc is directed, else it is directed. Return null
	 * if n1 or n2 do not belong to this graph.
	 * 
	 * @param n1
	 * @param n2
	 * @param directed
	 * @return A new arc in this linking n1 to n2, null if n1 or n2 do not
	 *         belong to this graph or if a was already added or virtually
	 *         removed.
	 */
	public Arc addArc(Integer n1, Integer n2, boolean directed) {
		if (n1 == null || n2 == null || !this.contains(n1)
				|| !this.contains(n2))
			return null;
		Arc a = new Arc(n1, n2, directed);
		if (!edges.add(a))
			return null;

		if (directed) {
			getOutputs(n1).add(a);
			getInputs(n2).add(a);
		} else {
			getUndirectedNeighours(n1).add(a);
			getUndirectedNeighours(n2).add(a);
		}

		return a;
	}

	/**
	 * Remove every link between nodes n1 and n2. Remove directed arcs if
	 * directed is True, undirected if not. Do nothing if n1 or n2 does not
	 * belong to this. <br/>
	 * 
	 * @param n1
	 * @param n2
	 * @param directed
	 * @throws NodeNotContainedException
	 *             If n1 or n2 does not belong to this.
	 */
	private void removeArcs(Integer n1, Integer n2, boolean directed) {
		if (n1 == null || n2 == null)
			return;

		Iterator<Arc> it;
		if (directed)
			it = getOutputArcsIterator(n1);
		else
			it = getUndirectedNeighbourEdgesIterator(n1);
		if (it == null || !this.contains(n2)) // is null if !this.contain(n1)
			return;

		while (it.hasNext()) {
			Arc a = it.next();
			if ((directed && a.getOutput().equals(n2))
					|| (!directed && n2.equals(this.getNeighbourNode(n1, a))))
				removeEdge(a);
		}
	}

	/*
	 * --------------------------------------------- Virtually removed vertices,
	 * edges and arcs ---------------------------------------------
	 */

	/**
	 * Virtually remove the vertice from the graph. The vertice will not appear
	 * anymore while reading the vertices of the graph (by listing the vertices,
	 * or reading the neighbour of any node). Every arc containing n is also
	 * virtually removed from this. <br/>
	 * 
	 * @param n
	 *            The node to be virtually removed.
	 * @return True if the node was virtually removed from this. False if it was
	 *         null, it does not belong to this, or if it was already virtually
	 *         removed.
	 * @see #cancelVirtuallyRemoveVertice(Integer)
	 */
	public boolean virtuallyRemoveVertice(Integer n) {
		if (n == null || !this.contains(n))
			return false;
		else
			return vrmVertices.add(n);
	}

	/**
	 * Cancel the virtually removal of n. <br/>
	 * 
	 * @param n
	 * @return True if cancelling succeed. False if it was null, it does not
	 *         belong to this, or if it was not virtually removed.
	 *         {@link #virtuallyRemoveVertice(Integer)}
	 */
	public boolean cancelVirtuallyRemoveVertice(Integer n) {
		if (n == null)
			return false;
		else
			return vrmVertices.remove(n);
	}

	/**
	 * @return The set of virtually removed vertices.
	 */
	public HashSet<Integer> getVirtuallyRemovedVertices() {
		return new HashSet<Integer>(vrmVertices);
	}

	/**
	 * @return An iterator over the set of virtually removed vertices.
	 */
	public Iterator<Integer> getVirtuallyRemovedVerticesIterator() {
		return vrmVertices.iterator();
	}

	/**
	 * @param n
	 * @return True if n was virtually removed from this.
	 */
	public boolean hasVirtuallyRemoved(Integer n) {
		return n != null && vrmVertices.contains(n);
	}

	/**
	 * Virtually remove the edge from the graph. The edge will not appear
	 * anymore while reading the edges of the graph (by listing the edges, or
	 * reading the neighbour of any node). <br/>
	 * 
	 * @param a
	 *            The edge to be virtually removed.
	 * @return True if the edge was virtually removed from this. False if it was
	 *         null, it does not belong to this, or if it was already virtually
	 *         removed. {@link #cancelVirtuallyRemoveEdge(Arc)}
	 */
	public boolean virtuallyRemoveEdge(Arc a) {
		if (a == null || !this.contains(a))
			return false;
		else
			return vrmEdges.add(a);
	}

	/**
	 * Cancel the virtually removal of a. Do not work is one of the edges is
	 * virtually removed. <br/>
	 * 
	 * @param a
	 * @return True if cancelling succeed. False if it was null, it does not
	 *         belong to this, if it was not virtually removed or if one of its
	 *         ends it virtually removed. {@link #virtuallyRemoveEdge(Arc)}
	 */
	public boolean cancelVirtuallyRemoveEdge(Arc a) {
		if (a == null || this.hasVirtuallyRemoved(a.getInput())
				|| this.hasVirtuallyRemoved(a.getOutput()))
			return false;
		else
			return vrmEdges.remove(a);
	}

	/**
	 * Return the set of virtually removed edges and arcs. Do not show the set
	 * of edges virtually removed when one of their ends was removed.
	 * 
	 * 
	 * @return The set of virtually removed edges and arcs,
	 * 
	 */
	public HashSet<Arc> getVirtuallyRemovedEdges() {
		return new HashSet<Arc>(vrmEdges);
	}

	/**
	 * Return an iterator over the set of virtually removed edges and arcs. Do
	 * not show the set of edges virtually removed when one of their ends was
	 * removed.
	 * 
	 * @return An iterator over the set of virtually removed edges and arcs.
	 * 
	 */
	public Iterator<Arc> getVirtuallyRemovedEdgesIterator() {
		return vrmEdges.iterator();
	}

	/**
	 * @param a
	 * @return True if a was virtually removed from this, or if one of its ends
	 *         was virtually removed.
	 */
	public boolean hasVirtuallyRemoved(Arc a) {
		return a != null
				&& (vrmEdges.contains(a) || vrmVertices.contains(a.getInput()) || vrmVertices
						.contains(a.getOutput()));
	}

	/*
	 * ---------------------------------------------
	 */

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Noeuds\n\n");
		for (Integer n : getVertices()) {
			s.append((new StringBuilder()).append(n).append("\n").toString());
		}
		s.append("\nArcs\n\n");
		for (Arc a : getEdges()) {
			s.append((new StringBuilder()).append(a).append("\n").toString());
		}

		return s.toString();
	}

	/*
	 * --------------------------------------------- Connected components
	 * ---------------------------------------------
	 */

	/**
	 * @return The lists of connected components of this.
	 */
	public ArrayList<ArrayList<Integer>> getConnectedComponents() {
		HashSet<Integer> read = new HashSet<Integer>();
		ArrayList<ArrayList<Integer>> l = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> h;
		for (Integer n : vertices) {
			if (read.contains(n))
				continue;
			h = getConnectedComponent(n);
			read.addAll(h);
			l.add(h);
		}
		return l;
	}

	/**
	 * @param n
	 * @return The connected component of this containing the node n, null if n
	 *         do not belong to this
	 */
	public ArrayList<Integer> getConnectedComponent(Integer n) {
		LinkedList<Integer> l = new LinkedList<Integer>();
		HashSet<Integer> h = new HashSet<Integer>();
		l.add(n);
		Integer u = l.pop();
		h.add(u);
		Iterator<Arc> it = this.getAllNeighbourIterator(u);
		if (it == null)
			return null;

		do {
			while (it.hasNext())
				l.add(this.getNeighbourNode(u, it.next()));

			if (l.isEmpty())
				break;

			u = l.pop();
			if ((h.contains(u)))
				continue;
			h.add(u);
			it = this.getAllNeighbourIterator(u);

		} while (true);

		return new ArrayList<Integer>(h);
	}

	/**
	 * Return true if a path connecting n1 and n2 exists in this. This path can
	 * use only undirected edges.
	 * 
	 * @param n1
	 * @param n2
	 * @return true if a undirected path connecting n1 and n2 exists in this.
	 *         False if not or if n1 or n2 do not belong to this.
	 * @see #areConnectedByDirectedPath(Integer, Integer)
	 */
	public boolean areConnectedByUndirectedEdges(Integer n1, Integer n2) {
		return areConnected(n1, n2, true, false);
	}

	/**
	 * 
	 * Return true if a path connecting n1 and n2 exists in this. This path can
	 * use only directed edges.
	 * 
	 * @param n1
	 * @param n2
	 * @return true if a directed path from n1 to n2 exists in this. False if
	 *         not or if n1 or n2 do not belong to this.
	 */
	public boolean areConnectedByDirectedPath(Integer n1, Integer n2) {
		return areConnected(n1, n2, false, true);
	}

	/**
	 * Return true if a path connecting n1 and n2 exists in this. This path can
	 * use directed and undirected edges.
	 * 
	 * @param n1
	 * @param n2
	 * @return true if a path connecting n1 and n2 exists in this. False if not
	 *         or if n1 or n2 do not belong to this.
	 * @see #areConnectedByDirectedPath(Integer, Integer)
	 */
	public boolean areConnected(Integer n1, Integer n2) {
		return areConnected(n1, n2, true, true);
	}

	/**
	 * Return true if a path connecting n1 and n2 exists in this. This path can
	 * use directed edges if useDirected is true and undirected edges if use
	 * undirected is true.
	 * 
	 * @param n1
	 * @param n2
	 * @param useUndirected
	 * @param useDirected
	 * @return true if a path connecting n1 and n2 exists in this while
	 *         respecting useUndirected and useDirected. False if not or if n1
	 *         or n2 do not belong to this.
	 */
	private boolean areConnected(Integer n1, Integer n2, boolean useUndirected,
			boolean useDirected) {

		boolean contains1 = this.contains(n1);
		if (!contains1)
			return false;
		if ((n1 == n2 || n1.equals(n2)))
			return true;

		if (!useUndirected && !useDirected)
			return false;

		LinkedList<Integer> l = new LinkedList<Integer>();
		HashSet<Integer> h = new HashSet<Integer>();
		l.add(n1);
		Integer u, v;
		Iterator<Arc> it;
		while (!l.isEmpty()) {
			u = l.pop();
			if (h.contains(u))
				continue;

			h.add(u);
			it = new MultiIterator<Arc>(this.getOutputArcsIterator(u),
					this.getUndirectedNeighbourEdgesIterator(u));
			while (it.hasNext()) {
				Arc a = it.next();
				if ((!useDirected && a.isDirected())
						|| (!useUndirected && !a.isDirected()))
					continue;

				v = this.getNeighbourNode(u, a);
				if (v.equals(n2))
					return true;
				l.add(v);
			}
		}
		return false;
	}

	/**
	 * Return a path connecting n1 and n2 if it exists, null if it doed not.
	 * This path can use only undirected edges.
	 * 
	 * @param n1
	 * @param n2
	 * @return true if a undirected path connecting n1 and n2 exists in this.
	 *         False if not or if n1 or n2 do not belong to this.
	 * @see #getDirectedPathConnecting(Integer, Integer)
	 */
	public ArrayList<Arc> getUndirectedPathConnecting(Integer n1, Integer n2) {
		return getPathConnecting(n1, n2, true, false);
	}

	/**
	 * 
	 * Return a path connecting n1 and n2 if it exists, null if it doed not.
	 * This path can use only directed edges.
	 * 
	 * @param n1
	 * @param n2
	 * @return true if a directed path from n1 to n2 exists in this. False if
	 *         not or if n1 or n2 do not belong to this.
	 */
	public ArrayList<Arc> getDirectedPathConnecting(Integer n1, Integer n2) {
		return getPathConnecting(n1, n2, false, true);
	}

	/**
	 * Return a path connecting n1 and n2 if it exists, null if it doed not.
	 * This path can use directed and undirected edges.
	 * 
	 * @param n1
	 * @param n2
	 * @return true if a path connecting n1 and n2 exists in this. False if not
	 *         or if n1 or n2 do not belong to this.
	 * @see #getDirectedPathConnecting(Integer, Integer)
	 */
	public ArrayList<Arc> getPathConnecting(Integer n1, Integer n2) {
		return getPathConnecting(n1, n2, true, true);
	}

	/**
	 * Return a path connecting n1 and n2 if it exists, null if it doed not.
	 * This path can use directed edges if useDirected is true and undirected
	 * edges if use undirected is true.
	 * 
	 * @param n1
	 * @param n2
	 * @param useUndirected
	 * @param useDirected
	 * @return true if a path connecting n1 and n2 exists in this while
	 *         respecting useUndirected and useDirected. False if not or if n1
	 *         or n2 do not belong to this.
	 */
	private ArrayList<Arc> getPathConnecting(Integer n1, Integer n2,
			boolean useUndirected, boolean useDirected) {

		boolean contains1 = this.contains(n1);
		if (!contains1)
			return null;
		if ((n1 == n2 || n1.equals(n2)))
			return new ArrayList<Arc>();

		LinkedList<ArrayList<Arc>> l = new LinkedList<ArrayList<Arc>>();
		HashSet<Integer> h = new HashSet<Integer>();
		l.add(new ArrayList<Arc>());
		Integer u, v;
		ArrayList<Arc> path, pathCopy;

		Iterator<Arc> it;
		while (!l.isEmpty()) {
			path = l.pop();
			if (path.isEmpty())
				u = n1;
			else
				u = path.get(path.size() - 1).getOutput();

			if (h.contains(u))
				continue;

			h.add(u);
			it = new MultiIterator<Arc>(this.getOutputArcsIterator(u),
					this.getUndirectedNeighbourEdgesIterator(u));
			while (it.hasNext()) {
				Arc a = it.next();
				if ((!useDirected && a.isDirected())
						|| (!useUndirected && !a.isDirected()))
					continue;

				v = this.getNeighbourNode(u, a);
				if (h.contains(v))
					continue;

				pathCopy = new ArrayList<Arc>(path);
				pathCopy.add(a);
				if (v.equals(n2))
					return pathCopy;
				l.add(pathCopy);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param n1
	 * @param n2
	 * @return one path (with directed edges or not) from n1 to n2 using as
	 *         least number of edges as possible. Return null if n1 or n2 does
	 *         not belong to this.
	 */
	public ArrayList<Arc> getMinimumNumberOfEdgePath(Integer n1, Integer n2) {

		ArrayList<ArrayList<Integer>> arar = getNodeLayers(n1, n2, true);
		if (arar == null)
			return null;
		ArrayList<Integer> ar;
		ArrayList<Arc> arcs = new ArrayList<Arc>();
		Integer u = n2;
		Arc a;
		for (int i = arar.size() - 2; i >= 0; i--) {
			ar = arar.get(i);
			for (Integer v : ar) {
				a = this.getLink(v, u);
				if (a != null) {
					arcs.add(0, a);
					u = v;
					break;
				}
			}

		}

		return arcs;

	}

	/**
	 * 
	 * Return every successive node layers from n1 to n2; or null if n1 or n2
	 * does not belong to this or if this does not contain any path from n1 to
	 * n2.
	 * 
	 * If a node n belongs to layer i, there is minimum number of edges path
	 * from n1 to in with i edges.
	 * 
	 * If checkOrientation is true, the path check orientation of directed arcs.
	 * For example, in the graph n1 --> n3 <-- n2, the algorithm returns null if
	 * checkOrientation is true, and [[n1],[n3],[n2]] if not.
	 * 
	 * 
	 * @param n1
	 * @param n2
	 * @param checkOrientation
	 * @return Every successive node layers from n1 to n2; or null if n1 or n2
	 *         does not belong to this or if this does not contain any path from
	 *         n1 to n2.
	 */
	public ArrayList<ArrayList<Integer>> getNodeLayers(Integer n1, Integer n2,
			final boolean checkOrientation) {
		return getNodeLayers(n1, n2,
				new Foncteur<Couple<Integer, Arc>, Boolean>() {

					@Override
					public Boolean apply(Couple<Integer, Arc> c) {
						return !checkOrientation || !c.second.isDirected()
								|| c.first.equals(c.second.getInput());
					}

				});

	}

	/**
	 * 
	 * Return every successive node layers from n1 to n2; or null if n1 or n2
	 * does not belong to this or if this does not contain any path from n1 to
	 * n2.
	 * 
	 * If isDirected is true, if a node n belongs to layer i, there is minimum
	 * number of arcs directed path from n1 to in with i arcs. If isDirected is
	 * false, if a node n belongs to layer i, there is minimum number of edges
	 * undirected path from n1 to in with i edges.
	 * 
	 * 
	 * @param n1
	 * @param n2
	 * @param checkOrientation
	 * @return Every successive node layers from n1 to n2; or null if n1 or n2
	 *         does not belong to this or if this does not contain any path from
	 *         n1 to n2.
	 * 
	 */
	public ArrayList<ArrayList<Integer>> getNodeLayersWithSpecificArcs(
			Integer n1, Integer n2, final boolean isDirected) {
		return getNodeLayers(n1, n2,
				new Foncteur<Couple<Integer, Arc>, Boolean>() {

					@Override
					public Boolean apply(Couple<Integer, Arc> c) {
						return (isDirected && c.second.isDirected() && c.first
								.equals(c.second.getInput()))
								|| (!isDirected && !c.second.isDirected());
					}

				});
	}

	private ArrayList<ArrayList<Integer>> getNodeLayers(Integer n1, Integer n2,
			Foncteur<Couple<Integer, Arc>, Boolean> fonc) {

		if (n1 == null || n2 == null || !this.contains(n1)
				|| !this.contains(n2))
			return null;

		ArrayList<ArrayList<Integer>> arar = new ArrayList<ArrayList<Integer>>();
		arar.add(new ArrayList<Integer>());
		arar.get(0).add(n1);
		if (n1 == n2 || n1.equals(n2)) {
			return arar;
		}
		boolean b = true, bo;
		HashSet<Integer> h = new HashSet<Integer>();
		h.add(n1);
		Integer v;
		Arc a;
		ArrayList<Integer> ar, ar2;
		Couple<Integer, Arc> c = new Couple<Integer, Arc>();
		Iterator<Arc> it;
		while (b) {
			ar = arar.get(arar.size() - 1);
			ar2 = new ArrayList<Integer>();
			bo = false;
			for (Integer u : ar) {
				it = this.getAllNeighbourIterator(u);
				while (it.hasNext()) {
					a = it.next();
					// Soit a est non orienté, soit on ne vérifie par
					// l'orientation des arcs, soit n est origine de l'arc
					// orienté.
					c.first = u;
					c.second = a;
					if (fonc.apply(c)) {
						v = this.getNeighbourNode(u, a);
						if (!h.contains(v)) {
							h.add(v);
							if (v.equals(n2)) {
								b = false;
							}
							ar2.add(v);
							bo = true;
						}
					}
				}
			}
			arar.add(ar2);
			// Si bo est faux, alors on a rajouté aucun noeud lors de la
			// précédente couche. Donc on a
			// parcouru toute la composante connexe de n1 sans trouver n2, donc
			// il n'y a pas de chemin
			// entre ces noeuds.
			if (!bo)
				return null;
		}
		return arar;
	}

	/**
	 * @return One cycle in this.
	 */
	public ArrayList<Integer> getOneDirectedCycle() {
		ArrayList<ArrayList<Integer>> ar = this.getConnectedComponents();
		for (ArrayList<Integer> comp : ar) {
			for (Integer v : comp) {
				ArrayList<Integer> cycle = getDirectedCycleFrom(v);
				if (cycle == null)
					continue;
				else
					return cycle;
			}
		}
		return null;
	}

	/**
	 * @param v
	 * @return One cycle in this containing v. Return null if v does not belong
	 *         to this.
	 */
	public ArrayList<Integer> getDirectedCycleFrom(Integer v) {

		if (v == null)
			return null;

		final HashMap<Integer, Integer> depth = new HashMap<Integer, Integer>();
		depth.put(v, 1);

		TreeSet<Integer> l = new TreeSet<Integer>(new Comparator<Integer>() {

			@Override
			public int compare(Integer v1, Integer v2) {
				int i = depth.get(v1).compareTo(depth.get(v2));
				if (i != 0)
					return i;
				else
					return 1;
			}

		});
		l.add(v);

		Integer u, w;
		Arc a;

		while (!l.isEmpty()) {
			u = l.pollFirst();
			Iterator<Arc> it = this.getOutputArcsIterator(u);
			if (it == null)
				return null;
			while (it.hasNext()) {
				a = it.next();
				w = a.getOutput();
				if (w.equals(v)) {
					ArrayList<Integer> cycle = new ArrayList<Integer>();
					while (!u.equals(v)) {

						cycle.add(u);
						Iterator<Arc> it2 = this.getInputArcsIterator(u);
						while (it2.hasNext()) {
							w = it2.next().getInput();
							if (depth.get(w) != null
									&& (depth.get(w).equals(depth.get(u) - 1))) {
								u = w;
								break;
							}
						}
					}
					cycle.add(v);

					return cycle;
				}
				if (!depth.containsKey(w)) {
					depth.put(w, depth.get(u) + 1);
					l.add(w);
				}

			}
		}

		return null;
	}

	/*
	 * --------------------------------------------- Random elements
	 * ---------------------------------------------
	 */

	/**
	 * @return One vertice of this at random.
	 */
	public Integer getRandomVertice() {
		ArrayList<Integer> vertices = this.getVertices();
		return Collections2.randomElement(vertices);
	}

	/**
	 * @return One edge of this at random.
	 */
	public Arc getRandomEdge() {
		ArrayList<Arc> edges = this.getEdges();
		return Collections2.randomElement(edges);
	}

	/*
	 * --------------------------------------------- Induced Graphs
	 * ---------------------------------------------
	 */

	/**
	 * Renvoie le graphe induit par les noeuds de l'ensemble nodes, ainsi que
	 * l'association des noeuds du graphe induit vers le graphe d'origine, et
	 * les arc du graphe induit vers les arcs du graphe d'origine.
	 * 
	 * @param nodes
	 * @return un triplet contenant le graphe induit par les noeuds de
	 *         l'ensemble nodes, et les associations noeuds-noeuds et arcs-arcs
	 *         qui permettent de savoir dans le graphe d'origine à quel noeud
	 *         (arc) correspond ce noeud (arc) du graphe induit.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Graph> T getInducedGraphFromNodes(
			Collection<Integer> nodes) {

		T g = null;
		try {
			g = (T) this.getClass().newInstance();
			for (Integer n : nodes)
				if (this.contains(n))
					g.addVertice(n);
			Integer n1, n2;
			for (Arc a : this.getEdges()) {
				n1 = a.getInput();
				n2 = a.getOutput();
				if (nodes.contains(n1) && nodes.contains(n2)) {
					Arc b = g.addArc(n1, n2, a.isDirected());
					g.copyParams(this, a, b);
				}
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		g.copyParams(this);
		return g;

	}

	/**
	 * Renvoie le graphe induit par les arcs de l'ensemble arcs, ainsi que
	 * l'association des noeuds du graphe induit vers le graphe d'origine, et
	 * les arc du graphe induit vers les arcs du graphe d'origine.
	 * 
	 * @param arcs
	 * @return un triplet contenant le graphe induit par les arcs de l'ensemble
	 *         arcs, et les associations noeuds-noeuds et arcs-arcs qui
	 *         permettent de savoir dans le graphe d'origine à quel noeud (arc)
	 *         correspond ce noeud (arc) du graphe induit.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Graph> T getInducedGraphFromArc(Collection<Arc> arcs) {

		T g = null;
		try {
			g = (T) this.getClass().newInstance();

			Integer n1, n2;
			Arc b;
			for (Arc a : arcs) {
				if (this.contains(a)) {
					n1 = a.getInput();
					n2 = a.getOutput();

					g.addVertice(n1);
					g.addVertice(n2);
					b = g.addArc(n1, n2, a.isDirected());
					g.copyParams(this, a, b);
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		g.copyParams(this);
		return g;
	}

	/**
	 * Return a copy of this graph with same nodes and same arcs/edges.
	 * 
	 * @return
	 */
	public Graph copy() {
		return this.getInducedGraphFromNodes(getVertices());
	}

	// -----------------------------------------------
	// Paramètres
	// -----------------------------------------------

	private HashMap<String, Object> params;

	@Override
	public void clearParams() {
		params.clear();
	}

	@Override
	public boolean containsParam(String name) {
		return params.containsKey(name);
	}

	@Override
	public void defineParam(String name, Object value) {
		params.put(name, value);
	}

	@Override
	public Object getParam(String name) {
		return params.get(name);
	}

	@Override
	public Integer getParamInteger(String name) {
		return (Integer) params.get(name);
	}

	@Override
	public Double getParamDouble(String name) {
		return (Double) params.get(name);
	}

	@Override
	public Long getParamLong(String name) {
		return (Long) params.get(name);
	}

	@Override
	public String getParamString(String name) {
		return (String) params.get(name);
	}

	@Override
	public Boolean getParamBoolean(String name) {
		return (Boolean) params.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyParams(Parametable g) {
		if (g instanceof Graph) {
			params = (HashMap<String, Object>) ((Graph) g).params.clone();
		}
	}

	@Override
	public Set<String> getParamsNames() {
		return new HashSet<String>(params.keySet());
	}

	// -----------------------------------------------
	// GraphDrawer informations
	// -----------------------------------------------

	// Node informations

	private HashMap<Integer, Integer> nodeAbscissa;
	private HashMap<Integer, Integer> nodeOrdinates;

	public int getNodeAbscisse(Integer n) {
		Integer x = nodeAbscissa.get(n);
		if (x == null)
			x = 0;
		return x;
	}

	public void setNodeAbscisse(Integer n, int x) {
		this.nodeAbscissa.put(n, x);
	}

	public int getNodeOrdonnee(Integer n) {
		Integer y = nodeOrdinates.get(n);
		if (y == null)
			y = 0;
		return y;
	}

	public void setNodeOrdonnee(Integer n, int y) {
		this.nodeOrdinates.put(n, y);
	}

	public void setNodeCoordinates(Integer n, int x, int y) {
		setNodeAbscisse(n, x);
		setNodeOrdonnee(n, y);
	}

	private HashMap<Integer, Boolean> drawnNodes;

	public Boolean isDrawn(Integer n) {
		Boolean b = drawnNodes.get(n);
		if (b == null)
			b = true;
		return b;
	}

	public void setDrawn(Integer n, Boolean toDraw) {
		drawnNodes.put(n, toDraw);
	}

	private HashMap<Integer, NodeSymbol> nodeSymbols;

	public NodeSymbol getNodeSymbol(Integer n) {
		NodeSymbol s = nodeSymbols.get(n);
		if (s == null) {
			s = new NodeCircleSymbol();
			nodeSymbols.put(n, s);
		}
		return s;
	}

	public boolean isCircleSymbol(Integer n) {
		return getNodeSymbol(n).isCircle();
	}

	public boolean isSquareSymbol(Integer n) {
		return getNodeSymbol(n).isSquare();
	}

	public int getSideLength(Integer n) {
		if (isSquareSymbol(n))
			return ((NodeSquareSymbol) getNodeSymbol(n)).getSideLength();
		else
			return 0;
	}

	public int getRadius(Integer n) {
		if (isSquareSymbol(n))
			return ((NodeSquareSymbol) getNodeSymbol(n)).getSideLength() / 2;
		else
			return ((NodeCircleSymbol) getNodeSymbol(n)).getRadius();
	}

	public void setSquareSymbol(Integer n) {
		NodeSquareSymbol symb = new NodeSquareSymbol();
		nodeSymbols.put(n, symb);
	}

	public void setSideLength(Integer n, int sideLength) {
		if (isSquareSymbol(n))
			((NodeSquareSymbol) nodeSymbols.get(n)).setSideLength(sideLength);
	}

	public void setSquareSymbol(Integer n, int sideLength) {
		NodeSquareSymbol symb = new NodeSquareSymbol();
		symb.setSideLength(sideLength);
		nodeSymbols.put(n, symb);
	}

	public void setCircleSymbol(Integer n) {
		NodeCircleSymbol symb = new NodeCircleSymbol();
		nodeSymbols.put(n, symb);
	}

	public void setRadius(Integer n, int radius) {
		if (isCircleSymbol(n))
			((NodeCircleSymbol) nodeSymbols.get(n)).setRadius(radius);
	}

	public void setCircleSymbol(Integer n, int radius) {
		NodeCircleSymbol symb = new NodeCircleSymbol();
		symb.setRadius(radius);
		nodeSymbols.put(n, symb);
	}

	private HashMap<Integer, Color> nodeColors;

	public Color getColor(Integer n) {
		Color c = nodeColors.get(n);
		if (c == null)
			c = Color.BLACK;
		return c;
	}

	public void setColor(Integer n, Color color) {
		nodeColors.put(n, color);
	}

	private HashMap<Integer, Boolean> nodeFill;

	public boolean isFill(Integer n) {
		Boolean b = nodeFill.get(n);
		if (b == null)
			b = false;
		return b;
	}

	public void setFill(Integer n, boolean fill) {
		nodeFill.put(n, fill);
	}

	private HashMap<Integer, Color> nodeTextColor; // default : black

	public Color getTextColor(Integer n) {
		Color c = nodeTextColor.get(n);
		if (c == null)
			c = Color.black;
		return c;
	}

	public void setTextColor(Integer n, Color textColor) {
		nodeTextColor.put(n, textColor);
	}

	/**
	 * Copy the parameters of the node n into the copy node cp
	 */
	public void copyParams(Graph g, Integer n, Integer cp) {
		this.setColor(cp, g.getColor(n));
		this.setTextColor(cp, g.getTextColor(n));
		this.setNodeAbscisse(cp, g.getNodeAbscisse(n));
		this.setNodeOrdonnee(cp, g.getNodeOrdonnee(n));
		this.setFill(cp, g.isFill(n));

		if (g.isCircleSymbol(n))
			this.setCircleSymbol(cp, g.getRadius(n));
		else if (g.isSquareSymbol(n))
			this.setSquareSymbol(cp, g.getRadius(n) * 2);
	}

	// --------------------------------------
	// Arc informations

	private HashMap<Arc, Boolean> drawnArcs;

	public Boolean isDrawn(Arc a) {
		Boolean b = drawnArcs.get(a);
		if (b == null)
			b = true;
		return b;
	}

	public void setDrawn(Arc a, Boolean toDraw) {
		drawnArcs.put(a, toDraw);
	}

	private HashMap<Arc, ArcSymbol> arcSymbols;

	public void setSymbolLine(Arc a) {
		arcSymbols.put(a, new ArcLineSymbol());
	}

	public void setSymbolCircleArc1(Arc a, double startAngle) {
		ArcCircleStartAngleSymbol symbol = new ArcCircleStartAngleSymbol();
		symbol.setStartAngle(startAngle);
		arcSymbols.put(a, symbol);
	}

	public double getStartAngle(Arc a) {
		if (this.isCircleStartAngleSymbol(a)) {
			ArcSymbol symbol = arcSymbols.get(a);
			return ((ArcCircleStartAngleSymbol) symbol).getStartAngle();
		}
		return 0D;
	}

	public void setSymbolCircleArc2(Arc a, double middleDistance) {
		ArcCircleMiddleDistanceSymbol symbol = new ArcCircleMiddleDistanceSymbol();
		symbol.setMiddleDistance(middleDistance);
		arcSymbols.put(a, symbol);
	}

	public double getMiddleDistance(Arc a) {
		if (this.isCircleMiddleDistanceSymbol(a)) {
			ArcSymbol symbol = arcSymbols.get(a);
			return ((ArcCircleMiddleDistanceSymbol) symbol).getMiddleDistance();
		}
		return 0D;
	}

	public ArcSymbol getArcSymbol(Arc a) {
		ArcSymbol s = arcSymbols.get(a);
		if (s == null) {
			s = new ArcLineSymbol();
			arcSymbols.put(a, s);
		}
		return s;
	}

	public boolean isLineSymbol(Arc a) {
		return getArcSymbol(a).isLineSymbol();
	}

	public boolean isCircleStartAngleSymbol(Arc a) {
		return getArcSymbol(a).isCircleStartAngleSymbol();
	}

	public boolean isCircleMiddleDistanceSymbol(Arc a) {
		return getArcSymbol(a).isCircleMiddleDistanceSymbol();
	}

	private HashMap<Arc, ArcArrowSymbol> arcOutputSymbols;

	public ArcArrowSymbol getArcOutputSymbol(Arc a) {
		ArcArrowSymbol s = arcOutputSymbols.get(a);
		if (s == null) {
			s = new ArcArrowSymbol();
			arcOutputSymbols.put(a, s);
		}
		return s;
	}

	public boolean isArrowOutputSymbol(Arc a) {
		return a.isDirected();
	}

	public void setOutputSymbolArrow(Arc a, double beta, int sideDistance) {
		ArcArrowSymbol symbol = new ArcArrowSymbol();
		symbol.setBeta(beta);
		symbol.setSideDistance(sideDistance);

		arcOutputSymbols.put(a, symbol);
	}

	public double getBeta(Arc a) {
		if (this.isArrowOutputSymbol(a)) {
			ArcArrowSymbol symbol = getArcOutputSymbol(a);
			return symbol.getBeta();
		}
		return 0D;
	}

	public int getSideDistance(Arc a) {
		if (this.isArrowOutputSymbol(a)) {
			ArcArrowSymbol symbol = arcOutputSymbols.get(a);
			return symbol.getSideDistance();
		}
		return 0;
	}

	private HashMap<Arc, Color> arcColors;

	public Color getColor(Arc a) {
		Color color = arcColors.get(a);
		if (color == null)
			color = Color.black;
		return color;
	}

	public void setColor(Arc a, Color color) {
		arcColors.put(a, color);
	}

	/**
	 * Copy the parameters of the arc/edge a into the copy arc/edge cp
	 * 
	 * @param n
	 */
	public void copyParams(Graph g, Arc a, Arc cp) {
		this.setColor(cp, g.getColor(a));

		if (g.isLineSymbol(a)) {
			this.setSymbolLine(cp);
		} else if (g.isCircleStartAngleSymbol(a)) {
			this.setSymbolCircleArc1(cp, g.getStartAngle(a));
		} else if (g.isCircleMiddleDistanceSymbol(a)) {
			this.setSymbolCircleArc2(cp, g.getMiddleDistance(a));
		}

		if (g.isArrowOutputSymbol(a)) {
			this.setOutputSymbolArrow(cp, g.getBeta(a), g.getSideDistance(a));
		}
	}

}
