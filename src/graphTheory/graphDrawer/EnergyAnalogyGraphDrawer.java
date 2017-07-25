package graphTheory.graphDrawer;

import graphTheory.graph.Arc;
import graphTheory.graph.Graph;
import graphTheory.utils.Couple;
import graphTheory.utils.Math2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Draw a graph using an analogy with electomagnetism.
 * 
 * Source : Graph Drawing by Force-directed Placement, THOMAS M. J. FRUCHTERMAN
 * AND EDWARD M. REINGOLD
 * 
 * @author Watel Dimitri
 * 
 */
public class EnergyAnalogyGraphDrawer extends GraphDrawer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnergyAnalogyGraphDrawer(Graph g) {
		super(g);
	}

	public EnergyAnalogyGraphDrawer(Graph g,
			@SuppressWarnings("rawtypes") HashMap arcDisplayedParam) {
		super(g, arcDisplayedParam);
	}

	/**
	 * This parameter increases the repulsion force and and reduces the
	 * attraction forces when growing.
	 */
	private double k;

	/**
	 * Number of iterations before stop the process.
	 */
	private static final int iterations = 1000;

	/**
	 * Place nodes at random at first.
	 */
	protected void initVerticesCoordinates(List<Integer> nodes) {

		for (Integer v : nodes) {
			graph.setNodeAbscisse(v, Math2.randomInt(getWidth()));
			graph.setNodeOrdonnee(v, Math2.randomInt(getHeight()));
		}
	}

	protected void setVerticesCoordinates() {
		int area = this.getWidth() * this.getHeight();
		k = Math.sqrt((double) area / graph.getNumberOfVertices());

		ArrayList<Integer> nodes = graph.getVertices();
		ArrayList<Integer> nodesToDraw = new ArrayList<Integer>();
		for(Integer node : nodes){
			if(graph.isDrawn(node))
				nodesToDraw.add(node);
		}

		initVerticesCoordinates(nodesToDraw); // Place nodes at random.

		Couple<Double, Double> c, delta;
		double rep, att;

		// Disp contains the displacement vectors of each nodes at each iteration.
		HashMap<Integer, Couple<Double, Double>> disp = new HashMap<Integer, Couple<Double, Double>>();

		for (int i = 0; i < iterations; i++) {

			// Start with repulsive forces
			for (Integer v : nodesToDraw) {
				disp.put(v, new Couple<Double, Double>(0.0, 0.0)); // Displacement initialized with 0
				for (Integer u : nodesToDraw) {
					if (u != v) {
						delta = new Couple<Double, Double>(
								(double) (graph.getNodeAbscisse(v) - graph
										.getNodeAbscisse(u)),
								(double) (graph.getNodeOrdonnee(v) - graph
										.getNodeOrdonnee(u)));

						// If nodes are at the same place, repulsion is infinite. The nodes are then moved a little.
						if (delta.first == 0.0 && delta.second == 0.0) {
							graph.setNodeAbscisse(
									v,
									graph.getNodeAbscisse(v)
											+ ((graph.getNodeAbscisse(v) > getWidth() / 2) ? -1
													: 1) * 50);
							delta.first = (double) (graph.getNodeAbscisse(v) - graph
									.getNodeAbscisse(u));
							delta.second = (double) (graph.getNodeOrdonnee(v) - graph
									.getNodeOrdonnee(u));
						}
						// We add the repulsion to the displacement vector.
						else {
							normalize(delta);
							c = disp.get(v);
							rep = repulsion(v, u);
							c.first = c.first + delta.first * rep;
							c.second = c.second + delta.second * rep;
						}
					}
				}
			}

			// Compute the attractions
			Integer u, v;
			for (Arc e : graph.getEdges()) {
				if(!graph.isDrawn(e))
					continue;
				v = e.getInput();
				u = e.getOutput();
				delta = new Couple<Double, Double>(
						(double) (graph.getNodeAbscisse(v) - graph
								.getNodeAbscisse(u)),
						(double) (graph.getNodeOrdonnee(v) - graph
								.getNodeOrdonnee(u)));
				normalize(delta);
				att = attraction(v, u);
				c = disp.get(v);
				c.first = c.first - delta.first * att;
				c.second = c.second - delta.second * att;

				c = disp.get(u);
				c.first = c.first + delta.first * att;
				c.second = c.second + delta.second * att;

			}

			// Move the vertices
			// At each iteration, the movement is maximized by an upper bound which decrease
			// as if temperature would decrease and reach 0. As a consequence, nodes can move
			// a lot at the beginning and very little at the end.
			double norm;
			for (Integer v1 : nodesToDraw) {
				c = disp.get(v1);
				norm = norm(c);
				normalize(c);
				graph.setNodeAbscisse(
						v1,
						graph.getNodeAbscisse(v1)
								+ (int) (c.first * Math.min(norm, iterations
										/ (double) (i + 1))));
				if (graph.getNodeAbscisse(v1) > this.getWidth() - 50)
					graph.setNodeAbscisse(v1,
							this.getWidth() - 50 + Math2.randomInt(-25, 25));
				else if (graph.getNodeAbscisse(v1) < 50)
					graph.setNodeAbscisse(v1, 50 + Math2.randomInt(-25, 25));

				graph.setNodeOrdonnee(
						v1,
						graph.getNodeOrdonnee(v1)
								+ (int) (c.second * Math.min(norm, iterations
										/ (double) (i + 1))));
				if (graph.getNodeOrdonnee(v1) > this.getHeight() - 50)
					graph.setNodeOrdonnee(v1,
							this.getHeight() - 50 + Math2.randomInt(-25, 25));
				else if (graph.getNodeOrdonnee(v1) < 50)
					graph.setNodeOrdonnee(v1, 50 + Math2.randomInt(-25, 25));

			}
		}
	}

	/**
	 * Divide a 2D vector by its norm
	 * 
	 * @param c
	 */
	private void normalize(Couple<Double, Double> c) {
		double norm = norm(c);
		c.first = c.first / norm;
		c.second = c.second / norm;
	}

	/**
	 * Compute the norm of a 2D vector
	 * 
	 * @param c
	 * @return
	 */
	private double norm(Couple<Double, Double> c) {
		return Math.sqrt(c.first * c.first + c.second * c.second);
	}

	/**
	 * Compute the distance between two nodes drawed on the frame
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	private double distance(Integer n1, Integer n2) {
		return Math.sqrt(Math.pow(
				graph.getNodeAbscisse(n1) - graph.getNodeAbscisse(n2), 2)
				+ Math.pow(
						graph.getNodeOrdonnee(n1) - graph.getNodeOrdonnee(n2),
						2));
	}

	/**
	 * Compute the attraction bewteen two nodes
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	private double attraction(Integer n1, Integer n2) {
		return Math.pow(distance(n1, n2), 2) / k;
	}

	/**
	 * Compute the repulsion bewteen two nodes.
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	private double repulsion(Integer n1, Integer n2) {
		return k * k / distance(n1, n2);
	}
}
