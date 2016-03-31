package graphTheory.utils;

	/****************************************************************************
	 *  Compilation:  javac WeightedQuickUnionPathCompressionUF.java
	 *  Execution:  java WeightedQuickUnionPathCompressionUF < input.txt
	 *  Dependencies: StdIn.java StdOut.java
	 *
	 *  Weighted quick-union with path compression.
	 *
	 ****************************************************************************/

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
	 *  The <tt>WeightedQuickUnionPathCompressionUF</tt> class represents a
	 *  union-find data structure.
	 *  It supports the <em>union</em> and <em>find</em> operations, along with
	 *  methods for determinig whether two objects are in the same component
	 *  and the total number of components.
	 *  <p>
	 *  This implementation uses weighted quick union (by size) with full path compression.
	 *  Initializing a data structure with <em>N</em> objects takes linear time.
	 *  Afterwards, <em>union</em>, <em>find</em>, and <em>connected</em> take
	 *  logarithmic time (in the worst case) and <em>count</em> takes constant
	 *  time. Moreover, the amortized time per <em>union</em>, <em>find</em>,
	 *  and <em>connected</em> operation has inverse Ackermann complexity.
	 *  <p>
	 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/15uf">Section 1.5</a> of
	 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *     
	 *  @author Robert Sedgewick
	 *  @author Kevin Wayne
	 */
	public class WeightedQuickUnionPathCompressionUF {
	    private int[] parent;  // parent[i] = parent of i
	    private int[] size;    // size[i] = number of objects in subtree rooted at i
	    private int count;     // number of components

	    private int numberOfNodes;
	    
	    /**
	     * Initializes an empty union-find data structure with N isolated components 0 through N-1.
	     * @throws java.lang.IllegalArgumentException if N < 0
	     * @param N the number of objects
	     */
	    public WeightedQuickUnionPathCompressionUF(int N) {
	    	numberOfNodes = N;
	        parent = new int[numberOfNodes];
	        size = new int[numberOfNodes];
	    	reinit();
	    }

	    public void reinit(){
	    	count = numberOfNodes;
	        for (int i = 0; i < numberOfNodes; i++) {
	            parent[i] = i;
	            size[i] = 1;
	        }
	    }
	    
	    /**
	     * Returns the number of components.
	     * @return the number of components (between 1 and N)
	     */
	    public int count() {
	        return count;
	    }

	  
	    /**
	     * Are the two sites <tt>p</tt> and <tt>q</tt> in the same component?
	     * @param p the integer representing one site
	     * @param q the integer representing the other site
	     * @return <tt>true</tt> if the two sites <tt>p</tt> and <tt>q</tt>
	     *    are in the same component, and <tt>false</tt> otherwise
	     * @throws java.lang.IndexOutOfBoundsException unless both 0 <= p < N and 0 <= q < N
	     */
	    public boolean connected(int p, int q) {
	        return find(p) == find(q);
	    }


	    /**
	     * Returns the component identifier for the component containing site <tt>p</tt>.
	     * @param p the integer representing one site
	     * @return the component identifier for the component containing site <tt>p</tt>
	     * @throws java.lang.IndexOutOfBoundsException unless 0 <= p < N
	     */
	    public int find(int p) {
	        validate(p);
	        int root = p;
	        while (root != parent[root])
	            root = parent[root];
	        while (p != root) {
	            int newp = parent[p];
	            parent[p] = root;
	            p = newp;
	        }
	        return root;
	    }

	     
	    // validate that p is a valid index
	    private void validate(int p) {
	        int N = parent.length;
	        if (p < 0 || p >= N) {
	            throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + N);
	        }
	    }  

	    /**
	     * Merges the component containing site<tt>p</tt> with the component
	     * containing site <tt>q</tt>.
	     * @param p the integer representing one site
	     * @param q the integer representing the other site
	     * @throws java.lang.IndexOutOfBoundsException unless both 0 <= p < N and 0 <= q < N
	     */
	  public boolean union(int p, int q) {
	        int rootP = find(p);
	        int rootQ = find(q);
	        if (rootP == rootQ) return false;

	        // make smaller root point to larger one
	        if (size[rootP] < size[rootQ]) {
	            parent[rootP] = rootQ;
	            size[rootQ] += size[rootP];
	        }
	        else {
	            parent[rootQ] = rootP;
	            size[rootP] += size[rootQ];
	        }
	        count--;
	        return true;
	    }



	    /**
	     * Reads in a sequence of pairs of integers (between 0 and N-1) from standard input, 
	     * where each integer represents some object;
	     * if the objects are in different components, merge the two components
	     * and print the pair to standard output.
	     */
	    public static void main(String[] args) {
	        int n = 20;
	        WeightedQuickUnionPathCompressionUF uf = new WeightedQuickUnionPathCompressionUF(n);
//	        for(int i = 0; i<n*n;i++) {
//	            int p = Math2.randomInt(n);
//	            int q = Math2.randomInt(n);
//	            System.out.print(p+" "+q);
//	            if (uf.connected(p, q)) {
//	            	System.out.println();
//	            	continue;
//	            }
//	            uf.union(p, q);
//	            System.out.println(" ok");
//	        }
	        uf.union(0, 1);
	        uf.union(1, 2);
	        uf.union(2, 3);
	        uf.union(3, 4);
	        System.out.println(uf.count() + " components");
	    }

}
