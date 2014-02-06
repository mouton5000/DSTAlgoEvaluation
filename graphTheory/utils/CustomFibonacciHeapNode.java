package graphTheory.utils;

import java.util.LinkedList;

/*
 * -------------------------- FibonnaciHeapNode.java --------------------------
 * (C) Copyright 1999-2007, by Nathan Fiedler and Contributors. Original Author:
 * Nathan Fiedler Contributor(s): John V. Sichi $Id: FibonacciHeapNode.java 568
 * 2007-09-30 00:12:18Z perfecthash $ Changes ------- 03-Sept-2003 : Adapted
 * from Nathan Fiedler (JVS); Name Date Description ---- ---- ----------- nf
 * 08/31/97 Initial version nf 09/07/97 Removed FibHeapData interface nf
 * 01/20/01 Added synchronization nf 01/21/01 Made Node an inner class nf
 * 01/05/02 Added clear(), renamed empty() to isEmpty(), and renamed printHeap()
 * to toString() nf 01/06/02 Removed all synchronization JVS 06/24/06 Generics
 */

/**
 * Implementation of a node of FibonnacciHeap, from
 * http://jgrapht.sourceforge.net/ with a little difference.
 * 
 * In the original version, each key had to be a double. It is here a subClass
 * of the Comparable2 abstract class. It means that each key can be compared to
 * another, and that it exits a positive infinity key and a negative infinity
 * key.
 * 
 */
public class CustomFibonacciHeapNode<Data, Key extends Comparable2<Key>> {
	//~ Instance fields --------------------------------------------------------

	/**
	 * Node data.
	 */
	Data data;

	/**
	 * first child node
	 */
	CustomFibonacciHeapNode<Data, Key> child;

	/**
	 * left sibling node
	 */
	CustomFibonacciHeapNode<Data, Key> left;

	/**
	 * parent node
	 */
	CustomFibonacciHeapNode<Data, Key> parent;

	/**
	 * right sibling node
	 */
	CustomFibonacciHeapNode<Data, Key> right;

	/**
	 * true if this node has had a child removed since this node was added to
	 * its parent
	 */
	boolean mark;

	/**
	 * key value for this node
	 */
	Key key;

	/**
	 * number of children of this node (does not count grandchildren)
	 */
	int degree;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Default constructor. Initializes the right and left pointers, making this
	 * a circular doubly-linked list.
	 * 
	 * @param data
	 *            data for this node
	 * @param key
	 *            initial key for node
	 */
	public CustomFibonacciHeapNode(Data data, Key key) {
		right = this;
		left = this;
		this.data = data;
		this.key = key;
	}

	//~ Methods ----------------------------------------------------------------

	public LinkedList<CustomFibonacciHeapNode<Data, Key>> children() {
		LinkedList<CustomFibonacciHeapNode<Data, Key>> l = new LinkedList<CustomFibonacciHeapNode<Data, Key>>();
		l.add(this);
		CustomFibonacciHeapNode<Data, Key> y, x = this.child;
		y = x;
		if (x != null) {
			do {
				l.addAll(x.children());
				x = x.right;
			} while (y != x);
		}
		return l;
	}

	/**
	 * Obtain the key for this node.
	 * 
	 * @return the key
	 */
	public final Key getKey() {
		return key;
	}

	/**
	 * Obtain the data for this node.
	 */
	public final Data getData() {
		return data;
	}

	public String toString() {
		return '[' + data.toString() + " " + key + ']';
	}
	// toString
}

//End FibonacciHeapNode.java
