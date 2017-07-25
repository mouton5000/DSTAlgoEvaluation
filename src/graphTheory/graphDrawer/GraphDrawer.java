package graphTheory.graphDrawer;

import graphTheory.graph.Arc;
import graphTheory.graph.Graph;
import graphTheory.latex.TikzTranslator;
import graphTheory.utils.Drawing;
import graphTheory.utils.Math2;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;

/**
 *
 * This class implements two methods to draw a graph in a frame, knowing the
 * coordinates of each nodes. And a basic method to computes those coordinates.
 *
 * @author Watel Dimitri
 *
 */
public class GraphDrawer extends JFrame implements MouseListener,
		MouseMotionListener {

	/**
	 * For directed arcs, default angle between the triangle sides of the arrow
	 * and the strait (or not) line of the arrow.
	 */
	private static final double BETADEFAUT = 15;

	/**
	 * Default radius of the circle nodes, and middle of the default size of the
	 * edges of the square nodes.
	 */
	private static final int RAYONDEFAUT = 10;

	/**
	 * For semi circular arcs, default angle between the strait line linking the
	 * nodes and the tangent of the arc at the original node.
	 */
	private static final Double DEFAULT_START_ANGLE = Math.PI / 6;

	/**
	 * For semi circular arcs, default distance between the strait line linking
	 * the nodes and the arc at middle distance
	 */
	private static final Double DEFAULT_MIDDLE_DISTANCE = 30.0;

	private static final long serialVersionUID = 1L;

	/**
	 * Drawed graph
	 */
	public Graph graph;

	/**
	 * If a node is clicked and dragged, this let the frame knows which node is
	 * dragged.
	 */
	private Integer clikedNode;

	/**
	 * This button launch a conversion fonction from this representation to a
	 * TIKZ representation.
	 */
	private Button convButton;

	/**
	 * This HashMap map every arc with a parameter which is displayed on the
	 * screen next to the arc (like the cost of the arc).
	 */
	@SuppressWarnings("rawtypes")
	private HashMap arcDisplayedParam;

	/**
	 * Create a drawer and draw the graph g, with no parameters associated with
	 * the arcs.
	 *
	 * @param g
	 */
	public GraphDrawer(Graph g) {
		this(g, null);
	}

	/**
	 * Create a drawer and draw the graph g. arcDisplayedParam decide what
	 * parameter is associated with each arc.
	 *
	 * @param g
	 * @param arcDisplayedParam
	 */
	public GraphDrawer(Graph g,
					   @SuppressWarnings("rawtypes") HashMap arcDisplayedParam) {
		setLayout(new BorderLayout());

		convButton = new Button("Convert Tikz");
		this.add(convButton, BorderLayout.SOUTH);
		convButton.addMouseListener(this);

		setPreferredSize(new Dimension(800, 640));
		setSize(new Dimension(800, 640));
		setLocationRelativeTo(null);
		setBackground(Color.WHITE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		graph = g;

		this.arcDisplayedParam = arcDisplayedParam;

		setVerticesCoordinates(); // set the coordinates of every node before drawing them.

		repaint();
	}

	/**
	 * Set the coordinates of every node with a basic method : every nodes are
	 * uniformly placed on a circle.
	 */
	protected void setVerticesCoordinates() {
		ArrayList<Integer> nodes = graph.getVertices();
		ArrayList<Integer> h = new ArrayList<Integer>();
		for(Integer node : nodes){
			if(graph.isDrawn(node))
				h.add(node);
		}


		int s = h.size();
		int i = 0;
		for (Integer n : h) {
			int x1 = (int) (300D + 200D * Math
					.cos(((double) (2 * i) * 3.1415926535897931D) / (double) s));
			int y1 = (int) (300D + 200D * Math
					.sin(((double) (2 * i) * 3.1415926535897931D) / (double) s));

			graph.setNodeAbscisse(n, x1);
			graph.setNodeOrdonnee(n, y1);

			i++;
		}
	}

	/**
	 * Place every nodes on the frame, knowing its coordinates.
	 *
	 * @param g
	 */
	protected void paintVertices(Graphics g) {
		for (Integer n : graph.getVertices()) {
			if(graph.isDrawn(n))
				paintVertice(g, n);
		}
	}

	/**
	 * Place every arcs ou edges on the frame, knowing its coordinates.
	 *
	 * @param g
	 */
	protected void paintEdges(Graphics g) {
		ArrayList<Arc> arcs = graph.getEdges();
		/*
		 * It first checks if there is no two opposites arcs. In that case, if
		 * the arcs were drawed with strait lines, the drawer uses two opposite
		 * semi circular arcs instead.
		 */
		for (Arc a : arcs) {
			if(graph.isDrawn(a))
				for (Arc b : arcs) {
					if(graph.isDrawn(b))
						if (a.getInput().equals(b.getOutput())
								&& a.getOutput().equals(b.getInput())
								&& graph.isLineSymbol(a) && graph.isLineSymbol(a)) {
							graph.setSymbolCircleArc1(a, -DEFAULT_START_ANGLE);
							graph.setSymbolCircleArc1(a, -DEFAULT_START_ANGLE);
						}
				}
		}

		for (Arc arc : graph.getEdges()) {
			if(graph.isDrawn(arc)) {
				if (arc.isDirected())
					graph.setOutputSymbolArrow(arc, BETADEFAUT, RAYONDEFAUT);
				paintEdge(g, arc);
			}
		}
	}

	public void paint(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		paintVertices(g);
		paintEdges(g);

	}

	/**
	 * Draws the node n on the frame, knowing its coordinates.
	 *
	 * @param g
	 * @param n
	 */
	public void paintVertice(Graphics g, Integer n) {

		g.setColor(graph.getColor(n));

		// If the node is drawed as a circle
		if (graph.isCircleSymbol(n)) {
			Integer radius = graph.getRadius(n);
			if (radius == null)
				radius = 25;

			if (graph.isFill(n))
				g.fillOval(graph.getNodeAbscisse(n) - radius,
						graph.getNodeOrdonnee(n) - radius, 2 * radius,
						2 * radius);
			else
				g.drawOval(graph.getNodeAbscisse(n) - radius,
						graph.getNodeOrdonnee(n) - radius, 2 * radius,
						2 * radius);
		}
		// if the node is drawed as a square
		else if (graph.isSquareSymbol(n)) {
			Integer sideLength = graph.getSideLength(n);
			if (sideLength == null)
				sideLength = 50;
			//Set the four x and y coordinates of the square
			int[] xPoints = { graph.getNodeAbscisse(n) - sideLength / 2,
					graph.getNodeAbscisse(n) - sideLength / 2,
					graph.getNodeAbscisse(n) + sideLength / 2,
					graph.getNodeAbscisse(n) + sideLength / 2 },

					yPoints = { graph.getNodeOrdonnee(n) - sideLength / 2,
							graph.getNodeOrdonnee(n) + sideLength / 2,
							graph.getNodeOrdonnee(n) + sideLength / 2,
							graph.getNodeOrdonnee(n) - sideLength / 2 };
			if (graph.isFill(n))
				g.fillPolygon(xPoints, yPoints, 4);
			else
				g.drawPolygon(xPoints, yPoints, 4);
		}

		// else do nothing
		else {
		}

		// draw the string representation of n at the node coordinates 
		g.setColor(graph.getTextColor(n));
		g.drawString(String.valueOf(n), graph.getNodeAbscisse(n),
				graph.getNodeOrdonnee(n));
		g.setColor(Color.black);
	}

	/**
	 * Draws the arc on the frame, knowing the coordinates of its endings
	 *
	 * @param g
	 * @param arc
	 */
	public void paintEdge(Graphics g, Arc arc) {

		g.setColor(graph.getColor(arc));

		int x1, x2, y1, y2;
		x1 = graph.getNodeAbscisse(arc.getInput());
		y1 = graph.getNodeOrdonnee(arc.getInput());
		x2 = graph.getNodeAbscisse(arc.getOutput());
		y2 = graph.getNodeOrdonnee(arc.getOutput());

		// a is the slope of the strait line between the endings
		double a = ((double) y2 - (double) y1) / ((double) x2 - (double) x1);

		// alpha if the angle related to the slope
		double alpha = (Math.atan(a) + (x1 > x2 ? 3.1415926535897931D : 0.0D))
				% (2.0 * Math.PI);
		if (alpha < 0)
			alpha += Math.PI * 2.0;

		// d is the distance between the endings
		double d = Math.sqrt(Math.pow(x2 - x1, 2D) + Math.pow(y2 - y1, 2D));

		/*
		 * x11, x22, y11 and y22 are the coordinated of the endings of the
		 * drawed arc (if the arc link two circle nodes, the drawed arc do not
		 * link the center of the circles but the sides). It depends on the
		 * shapes of the endings.
		 */
		int x11 = 0, x22 = 0, y11 = 0, y22 = 0;
		Integer radius, sideLength;

		// If the input is a circle
		if (graph.isCircleSymbol(arc.getInput())) {
			radius = graph.getRadius(arc.getInput());
			if (radius == null)
				radius = 25;
			x11 = (int) (Math.cos(alpha) * (double) radius + (double) x1);
			y11 = (int) (Math.sin(alpha) * (double) radius + (double) y1);
		}
		// if the input is a square
		else if (graph.isSquareSymbol(arc.getInput())) {
			sideLength = graph.getSideLength(arc.getInput());
			if (sideLength == null)
				sideLength = 50;
			if (alpha <= Math.PI / 4.0 || alpha > Math.PI * 7.0 / 4.0) {
				x11 = (int) (sideLength / 2.0 + (double) x1);
				y11 = (int) (sideLength * Math.tan(alpha) / 2.0 + (double) y1);
			} else if (alpha <= Math.PI * 3.0 / 4.0 && alpha > Math.PI / 4.0) {
				x11 = (int) (sideLength / (2.0 * Math.tan(alpha)) + (double) x1);
				y11 = (int) (sideLength / 2.0 + (double) y1);
			} else if (alpha <= Math.PI * 5.0 / 4.0
					&& alpha > Math.PI * 3.0 / 4.0) {
				x11 = (int) (-sideLength / 2.0 + (double) x1);
				y11 = (int) (-sideLength * Math.tan(alpha) / 2.0 + (double) y1);
			} else {
				x11 = (int) (-sideLength / (2.0 * Math.tan(alpha)) + (double) x1);
				y11 = (int) (-sideLength / 2.0 + (double) y1);
			}
		}

		// If the output is a circle
		if (graph.isCircleSymbol(arc.getOutput())) {
			radius = graph.getRadius(arc.getOutput());
			if (radius == null)
				radius = 25;
			x22 = (int) (Math.cos(alpha) * (d - (double) radius) + (double) x1);
			y22 = (int) (Math.sin(alpha) * (d - (double) radius) + (double) y1);
		}
		// if the output is a square
		else if (graph.isSquareSymbol(arc.getOutput())) {
			sideLength = graph.getSideLength(arc.getOutput());
			if (sideLength == null)
				sideLength = 50;
			if (alpha <= Math.PI / 4.0 || alpha > Math.PI * 7.0 / 4.0) {
				x22 = (int) (-sideLength / 2.0 + (double) x2);
				y22 = (int) (-sideLength * Math.tan(alpha) / 2.0 + (double) y2);
			} else if (alpha <= Math.PI * 3.0 / 4.0 && alpha > Math.PI / 4.0) {
				x22 = (int) (-sideLength / (2.0 * Math.tan(alpha)) + (double) x2);
				y22 = (int) (-sideLength / 2.0 + (double) y2);
			} else if (alpha <= Math.PI * 5.0 / 4.0
					&& alpha > Math.PI * 3.0 / 4.0) {
				x22 = (int) (sideLength / 2.0 + (double) x2);
				y22 = (int) (sideLength * Math.tan(alpha) / 2.0 + (double) y2);
			} else {
				x22 = (int) (sideLength / (2.0 * Math.tan(alpha)) + (double) x2);
				y22 = (int) (sideLength / 2.0 + (double) y2);
			}
		}

		Double startAngle = null, middleDistance = null;

		// If the arc is a strait line
		if (graph.isLineSymbol(arc)) {
			g.drawLine(x11, y11, x22, y22);
		}

		/*
		 * For semi circular arcs defined by angles between the strait line
		 * linking the nodes and the tangent of the arc at the endings.
		 */
		else if (graph.isCircleStartAngleSymbol(arc)) {
			startAngle = graph.getStartAngle(arc);
			if (startAngle == null)
				startAngle = DEFAULT_START_ANGLE;
			Drawing.drawArc1(g, x11, y11, x22, y22, startAngle); // Special helper to draw semi circular arcs
			middleDistance = Drawing.fromStartAngleToMiddleDistance(x11, y11,
					x22, y22, startAngle);
		}

		/*
		 * For semi circular arcs defined by the distance between the strait
		 * line linking the nodes and the arc at middle distance
		 */
		else if (graph.isCircleMiddleDistanceSymbol(arc)) {
			middleDistance = graph.getMiddleDistance(arc);
			if (middleDistance == null)
				middleDistance = DEFAULT_MIDDLE_DISTANCE;
			Drawing.drawArc2(g, x11, y11, x22, y22, middleDistance); // Special helper to draw semi circular arcs
			startAngle = Drawing.fromMiddleDistanceToStartAngle(x11, y11, x22,
					y22, middleDistance);
		}

		// If the arc is directed, draw the ending triangle.
		if (arc.isDirected()) {
			if (graph.isArrowOutputSymbol(arc)) {
				Integer rayon = graph.getSideDistance(arc);
				Double beta = graph.getBeta(arc);
				if (rayon == null)
					rayon = RAYONDEFAUT;
				if (beta == null)
					beta = BETADEFAUT;
				if (graph.isCircleStartAngleSymbol(arc)
						|| graph.isCircleMiddleDistanceSymbol(arc)) {
					alpha += startAngle;
				}
				int x33 = (int) (Math.cos(alpha + (double) beta)
						* (double) rayon + (double) x22);
				int x44 = (int) (Math.cos(alpha - (double) beta)
						* (double) rayon + (double) x22);
				int y33 = (int) (Math.sin(alpha + (double) beta)
						* (double) rayon + (double) y22);
				int y44 = (int) (Math.sin(alpha - (double) beta)
						* (double) rayon + (double) y22);
				g.drawLine(x33, y33, x22, y22);
				g.drawLine(x44, y44, x22, y22);
			}
		}

		// If a parameter is associated with the arc, draw it.
		String param;
		double dist = Math2.dist(x11, y11, x22, y22, 2.0);
		double p1p2a = (x22 - x11) / (dist);
		double p1p2b = (y22 - y11) / (dist);
		double textDist = 10;
		if (middleDistance != null)
			textDist += middleDistance;
		int xt = (int) (dist / 2 * p1p2a + textDist * p1p2b + x11);
		int yt = (int) (dist / 2 * p1p2b - textDist * p1p2a + y11);
		if (arcDisplayedParam != null) {
			Object p = arcDisplayedParam.get(arc);
			if (p != null) {
				param = String.valueOf(p);
				g.drawString(param, xt, yt);
			}
		}
		g.setColor(Color.black);
	}

	/**
	 * If one click at coordinates (x,y) to select a node, returns that node
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private Integer getClikedVertice(int x, int y) {
		Iterator<Integer> it = graph.getVerticesIterator();
		Integer n, m = null;
		boolean b;
		Integer r;
		while (it.hasNext()) {
			n = it.next();
			b = graph.isCircleSymbol(n);
			r = graph.getRadius(n);

			if (b) {
				if (Math2.dist(graph.getNodeAbscisse(n),
						graph.getNodeOrdonnee(n), x, y, 2.0) < r)
					m = n;
			} else {
				if (Math2.dist(graph.getNodeAbscisse(n),
						graph.getNodeOrdonnee(n), x, y,
						Double.POSITIVE_INFINITY) < r)
					m = n;
			}
		}
		return m;
	}

	public void mousePressed(MouseEvent me) {
		if (me.getSource() == convButton) {
			System.out.println(TikzTranslator.translateGraph(graph));
		} else {
			int x = me.getX();
			int y = me.getY();

			Integer n = getClikedVertice(x, y);
			if (n != null)
				clikedNode = n;

			repaint();
		}
	}

	public void mouseReleased(MouseEvent me) {
	}

	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

	public void mouseMoved(MouseEvent me) {

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		int x = me.getX();
		int y = me.getY();
		if (clikedNode != null) {
			graph.setNodeCoordinates(clikedNode, x, y);
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent me) {

	}
}
