package graphTheory.utils;

/*
 * JGraphT : a free Java graph-theory library Project Info:
 * http://jgrapht.sourceforge.net/ Project Creator: Barak Naveh
 * (barak_naveh@users.sourceforge.net) (C) Copyright 2003-2007, by Barak Naveh
 * and Contributors. This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/*
 * -------------------------- FibonnaciHeap.java -------------------------- (C)
 * Copyright 1999-2003, by Nathan Fiedler and Contributors. Original Author:
 * Nathan Fiedler Contributor(s): John V. Sichi $Id: FibonacciHeap.java 603
 * 2008-06-28 07:51:50Z perfecthash $ Changes ------- 03-Sept-2003 : Adapted
 * from Nathan Fiedler (JVS); Name Date Description ---- ---- ----------- nf
 * 08/31/97 Initial version nf 09/07/97 Removed FibHeapData interface nf
 * 01/20/01 Added synchronization nf 01/21/01 Made Node an inner class nf
 * 01/05/02 Added clear(), renamed empty() to isEmpty(), and renamed printHeap()
 * to toString() nf 01/06/02 Removed all synchronization
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Implementation of a FibonnacciHeap, from http://jgrapht.sourceforge.net/ with
 * a little difference.
 * 
 * In the original version, each key had to be a double. It is here a subClass
 * of the Comparable2 abstract class. It means that each key can be compared to
 * another, and that it exits a positive infinity key and a negative infinity
 * key.
 * 
 */
public class CustomFibonacciHeap<Data, Key extends Comparable2<Key>> {
	//~ Static fields/initializers ---------------------------------------------

	private static final double oneOverLogPhi = 1.0 / Math.log((1.0 + Math
			.sqrt(5.0)) / 2.0);

	//~ Instance fields --------------------------------------------------------

	/**
	 * Points to the minimum node in the heap.
	 */
	private CustomFibonacciHeapNode<Data, Key> minNode;

	/**
	 * Number of nodes in the heap.
	 */
	private int nNodes;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Constructs a FibonacciHeap object that contains no elements.
	 */
	public CustomFibonacciHeap() {
	} // FibonacciHeap

	//~ Methods ----------------------------------------------------------------

	/**
	 * Tests if the Fibonacci heap is empty or not. Returns true if the heap is
	 * empty, false otherwise.
	 * 
	 * <p>
	 * Running time: O(1) actual
	 * </p>
	 * 
	 * @return true if the heap is empty, false otherwise
	 */
	public boolean isEmpty() {
		return minNode == null;
	}

	// isEmpty

	/**
	 * Removes all elements from this heap.
	 */
	public void clear() {
		minNode = null;
		nNodes = 0;
	}

	// clear

	/**
	 * Decreases the key value for a heap node, given the new value to take on.
	 * The structure of the heap may be changed and will not be consolidated.
	 * 
	 * <p>
	 * Running time: O(1) amortized
	 * </p>
	 * 
	 * @param x
	 *            node to decrease the key of
	 * @param k
	 *            new key value for node x
	 * 
	 * @exception IllegalArgumentException
	 *                Thrown if k is larger than x.key value.
	 */
	public void decreaseKey(CustomFibonacciHeapNode<Data, Key> x, Key k) {
		if (x.key.isStrLess(k)) {
			throw new IllegalArgumentException(
					"decreaseKey() got larger key value");
		}

		x.key = k;

		CustomFibonacciHeapNode<Data, Key> y = x.parent;

		if ((y != null) && (x.key.isStrLess(y.key))) {
			cut(x, y);
			cascadingCut(y);
		}

		if (x.key.isStrLess(minNode.key)) {
			minNode = x;
		}
	}

	// decreaseKey

	/**
	 * Deletes a node from the heap given the reference to the node. The trees
	 * in the heap will be consolidated, if necessary. This operation may fail
	 * to remove the correct element if there are nodes with key value
	 * -Infinity.
	 * 
	 * <p>
	 * Running time: O(log n) amortized
	 * </p>
	 * 
	 * @param x
	 *            node to remove from heap
	 */
	public void delete(CustomFibonacciHeapNode<Data, Key> x) {
		// make x as small as possible
		decreaseKey(x, x.getKey().getNegativeInfinity()); //Boarf

		// remove the smallest, which decreases n also
		removeMin();
	}

	// delete

	/**
	 * Inserts a new data element into the heap. No heap consolidation is
	 * performed at this time, the new node is simply inserted into the root
	 * list of this heap.
	 * 
	 * <p>
	 * Running time: O(1) actual
	 * </p>
	 * 
	 * @param node
	 *            new node to insert into heap
	 * @param key
	 *            key value associated with data object
	 */
	public CustomFibonacciHeapNode<Data, Key> insert(Data data, Key key) {
		CustomFibonacciHeapNode<Data, Key> node = new CustomFibonacciHeapNode<Data, Key>(
				data, key);

		// concatenate node into min list
		if (minNode != null) {
			node.left = minNode;
			node.right = minNode.right;
			minNode.right = node;
			node.right.left = node;

			if (key.isStrLess(minNode.key)) {
				minNode = node;
			}
		} else {
			minNode = node;
		}

		nNodes++;
		return node;
	}

	// insert

	/**
	 * Returns the smallest element in the heap. This smallest element is the
	 * one with the minimum key value.
	 * 
	 * <p>
	 * Running time: O(1) actual
	 * </p>
	 * 
	 * @return heap node with the smallest key
	 */
	public CustomFibonacciHeapNode<Data, Key> min() {
		return minNode;
	}

	// min

	/**
	 * Removes the smallest element from the heap. This will cause the trees in
	 * the heap to be consolidated, if necessary.
	 * 
	 * <p>
	 * Running time: O(log n) amortized
	 * </p>
	 * 
	 * @return node with the smallest key
	 */
	public CustomFibonacciHeapNode<Data, Key> removeMin() {
		CustomFibonacciHeapNode<Data, Key> z = minNode;

		if (z != null) {
			int numKids = z.degree;
			CustomFibonacciHeapNode<Data, Key> x = z.child;
			CustomFibonacciHeapNode<Data, Key> tempRight;

			// for each child of z do...
			while (numKids > 0) {
				tempRight = x.right;

				// remove x from child list
				x.left.right = x.right;
				x.right.left = x.left;

				// add x to root list of heap
				x.left = minNode;
				x.right = minNode.right;
				minNode.right = x;
				x.right.left = x;

				// set parent[x] to null
				x.parent = null;
				x = tempRight;
				numKids--;
			}

			// remove z from root list of heap
			z.left.right = z.right;
			z.right.left = z.left;

			if (z == z.right) {
				minNode = null;
			} else {
				minNode = z.right;
				consolidate();
			}

			// decrement size of heap
			nNodes--;
		}

		return z;
	}

	// removeMin

	/**
	 * Returns the size of the heap which is measured in the number of elements
	 * contained in the heap.
	 * 
	 * <p>
	 * Running time: O(1) actual
	 * </p>
	 * 
	 * @return number of elements in the heap
	 */
	public int size() {
		return nNodes;
	}

	// size

	/**
	 * Joins two Fibonacci heaps into a new one. No heap consolidation is
	 * performed at this time. The two root lists are simply joined together.
	 * 
	 * <p>
	 * Running time: O(1) actual
	 * </p>
	 * 
	 * @param h1
	 *            first heap
	 * @param h2
	 *            second heap
	 * 
	 * @return new heap containing h1 and h2
	 */
	public static <T, W extends Comparable2<W>> CustomFibonacciHeap<T, W> union(
			CustomFibonacciHeap<T, W> h1, CustomFibonacciHeap<T, W> h2) {
		CustomFibonacciHeap<T, W> h = new CustomFibonacciHeap<T, W>();

		if ((h1 != null) && (h2 != null)) {
			h.minNode = h1.minNode;

			if (h.minNode != null) {
				if (h2.minNode != null) {
					h.minNode.right.left = h2.minNode.left;
					h2.minNode.left.right = h.minNode.right;
					h.minNode.right = h2.minNode;
					h2.minNode.left = h.minNode;

					if (h2.minNode.key.isStrLess(h1.minNode.key)) {
						h.minNode = h2.minNode;
					}
				}
			} else {
				h.minNode = h2.minNode;
			}

			h.nNodes = h1.nNodes + h2.nNodes;
		}

		return h;
	}

	// union

	public String greatString() {
		if (minNode == null) {
			return "FibonacciHeap=[]";
		}

		StringBuilder sb = new StringBuilder();

		CustomFibonacciHeapNode<Data, Key> x = minNode;

		do {
			sb.append(x);
			sb.append(x.children());
			sb.append("\n");

			x = x.right;

		} while (x != minNode);

		sb.append("----------\n\n");

		return sb.toString();
	}

	/**
	 * Creates a String representation of this Fibonacci heap.
	 * 
	 * @return String of this.
	 */
	public String toString() {

		if (minNode == null) {
			return "FibonacciHeap=[]";
		}

		// create a new stack and put root on it
		Stack<CustomFibonacciHeapNode<Data, Key>> stack = new Stack<CustomFibonacciHeapNode<Data, Key>>();
		stack.push(minNode);

		StringBuilder sb = new StringBuilder();
		sb.append("FibonacciHeap=[");

		// do a simple breadth-first traversal on the tree
		while (!stack.empty()) {
			CustomFibonacciHeapNode<Data, Key> curr = stack.pop();
			sb.append(curr);
			sb.append(", ");

			if (curr.child != null) {
				stack.push(curr.child);
			}

			CustomFibonacciHeapNode<Data, Key> start = curr;
			curr = curr.right;

			while (curr != start) {
				sb.append(curr);
				sb.append(", ");

				if (curr.child != null) {
					stack.push(curr.child);
				}

				curr = curr.right;
			}
		}

		sb.append(']');

		return sb.toString();
	}

	// toString

	/**
	 * Performs a cascading cut operation. This cuts y from its parent and then
	 * does the same for its parent, and so on up the tree.
	 * 
	 * <p>
	 * Running time: O(log n); O(1) excluding the recursion
	 * </p>
	 * 
	 * @param y
	 *            node to perform cascading cut on
	 */
	protected void cascadingCut(CustomFibonacciHeapNode<Data, Key> y) {
		CustomFibonacciHeapNode<Data, Key> z = y.parent;

		// if there's a parent...
		if (z != null) {
			// if y is unmarked, set it marked
			if (!y.mark) {
				y.mark = true;
			} else {
				// it's marked, cut it from parent
				cut(y, z);

				// cut its parent as well
				cascadingCut(z);
			}
		}
	}

	// cascadingCut

	protected void consolidate() {
		int arraySize = ((int) Math.floor(Math.log(nNodes) * oneOverLogPhi)) + 1;

		List<CustomFibonacciHeapNode<Data, Key>> array = new ArrayList<CustomFibonacciHeapNode<Data, Key>>(
				arraySize);

		// Initialize degree array
		for (int i = 0; i < arraySize; i++) {
			array.add(null);
		}

		// Find the number of root nodes.
		int numRoots = 0;
		CustomFibonacciHeapNode<Data, Key> x = minNode;

		if (x != null) {
			numRoots++;
			x = x.right;

			while (x != minNode) {
				numRoots++;
				x = x.right;
			}
		}

		// For each node in root list do...
		while (numRoots > 0) {
			// Access this node's degree..
			int d = x.degree;
			CustomFibonacciHeapNode<Data, Key> next = x.right;

			// ..and see if there's another of the same degree.
			for (;;) {
				CustomFibonacciHeapNode<Data, Key> y = array.get(d);
				if (y == null) {
					// Nope.
					break;
				}

				// There is, make one of the nodes a child of the other.
				// Do this based on the key value.
				if (x.key.isStrBigger(y.key)) {
					CustomFibonacciHeapNode<Data, Key> temp = y;
					y = x;
					x = temp;
				}

				// CustomFibonacciHeapNode<Data,Key> y disappears from root list.
				link(y, x);

				// We've handled this degree, go to next one.
				array.set(d, null);
				d++;
			}

			// Save this node for later when we might encounter another
			// of the same degree.
			array.set(d, x);

			// Move forward through list.
			x = next;
			numRoots--;
		}

		// Set min to null (effectively losing the root list) and
		// reconstruct the root list from the array entries in array[].
		minNode = null;

		for (int i = 0; i < arraySize; i++) {
			CustomFibonacciHeapNode<Data, Key> y = array.get(i);
			if (y == null) {
				continue;
			}

			// We've got a live one, add it to root list.
			if (minNode != null) {
				// First remove node from root list.
				y.left.right = y.right;
				y.right.left = y.left;

				// Now add to root list, again.
				y.left = minNode;
				y.right = minNode.right;
				minNode.right = y;
				y.right.left = y;

				// Check if this is a new min.
				if (y.key.isStrLess(minNode.key)) {
					minNode = y;
				}
			} else {
				minNode = y;
			}
		}
	}

	// consolidate

	/**
	 * The reverse of the link operation: removes x from the child list of y.
	 * This method assumes that min is non-null.
	 * 
	 * <p>
	 * Running time: O(1)
	 * </p>
	 * 
	 * @param x
	 *            child of y to be removed from y's child list
	 * @param y
	 *            parent of x about to lose a child
	 */
	protected void cut(CustomFibonacciHeapNode<Data, Key> x,
			CustomFibonacciHeapNode<Data, Key> y) {
		// remove x from childlist of y and decrement degree[y]
		x.left.right = x.right;
		x.right.left = x.left;
		y.degree--;

		// reset y.child if necessary
		if (y.child == x) {
			y.child = x.right;
		}

		if (y.degree == 0) {
			y.child = null;
		}

		// add x to root list of heap
		x.left = minNode;
		x.right = minNode.right;
		minNode.right = x;
		x.right.left = x;

		// set parent[x] to nil
		x.parent = null;

		// set mark[x] to false
		x.mark = false;
	}

	// cut

	/**
	 * Make node y a child of node x.
	 * 
	 * <p>
	 * Running time: O(1) actual
	 * </p>
	 * 
	 * @param y
	 *            node to become child
	 * @param x
	 *            node to become parent
	 */
	protected void link(CustomFibonacciHeapNode<Data, Key> y,
			CustomFibonacciHeapNode<Data, Key> x) {
		// remove y from root list of heap
		y.left.right = y.right;
		y.right.left = y.left;

		// make y a child of x
		y.parent = x;

		if (x.child == null) {
			x.child = y;
			y.right = y;
			y.left = y;
		} else {
			y.left = x.child;
			y.right = x.child.right;
			x.child.right = y;
			y.right.left = y;
		}

		// increase degree[x]
		x.degree++;

		// set mark[y] false
		y.mark = false;
	}

	// link

	// FibonacciHeap
	/*
	 * JGraphT : a free Java graph-theory library Project Info:
	 * http://jgrapht.sourceforge.net/ Project Creator: Barak Naveh
	 * (barak_naveh@users.sourceforge.net) (C) Copyright 2003-2007, by Barak
	 * Naveh and Contributors. This library is free software; you can
	 * redistribute it and/or modify it under the terms of the GNU Lesser
	 * General Public License as published by the Free Software Foundation;
	 * either version 2.1 of the License, or (at your option) any later version.
	 * This library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
	 * General Public License for more details. You should have received a copy
	 * of the GNU Lesser General Public License along with this library; if not,
	 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
	 * Boston, MA 02111-1307, USA.
	 */
}
