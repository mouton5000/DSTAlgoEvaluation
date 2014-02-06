package graphTheory.utils;

import java.awt.Graphics;

/**
 * This class is a helper in order to draw graphs.
 * 
 * @author Watel Dimitri
 * 
 */
public class Drawing {
	/**
	 * 
	 * Use g to draw an edge coming from coordinates (x1,y1) to coordinates
	 * (x2,y2) going through coordinates (x0,y0) by a semi-circular path. If
	 * direct is true, the path is drawn counterclockwise; if not, clockwise.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x0
	 * @param y0
	 * @param direct
	 */
	public static void drawArc(Graphics g, int x1, int y1, int x2, int y2,
			int x0, int y0, boolean direct) {
		int r = (int) Math2.dist(x1, y1, x0, y0, 2.0);
		int x = x0 - r;
		int y = y0 - r;
		int width = 2 * r;
		int height = 2 * r;
		int startAngle = (int) -(180 / Math.PI * Math.atan2(y1 - y0, x1 - x0));
		int endAngle = (int) -(180 / Math.PI * Math.atan2(y2 - y0, x2 - x0));
		while (direct && endAngle < startAngle)
			endAngle += 360;
		while (!direct && endAngle > startAngle)
			endAngle -= 360;

		g.drawArc(x, y, width, height, startAngle, endAngle - startAngle);
	}

	/**
	 * 
	 * Use g to draw an arc or an edge coming from coordinates (x1,y1) to
	 * coordinates (x2,y2) by a semi-circular path. The tangent of the path at
	 * (x1,y1) make an angle of value startAngle with the strait line linking
	 * (x2,y2). This defines if the path is drawn counterclockwise of clockwise.
	 * 
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x0
	 * @param y0
	 * @param direct
	 */
	public static void drawArc1(Graphics g, int x1, int y1, int x2, int y2,
			double startAngle) {
		double middleDistance = fromStartAngleToMiddleDistance(x1, y1, x2, y2,
				startAngle);
		drawArc2(g, x1, y1, x2, y2, middleDistance);
	}

	/**
	 * 
	 * Use g to draw an arc or an edge coming from coordinates (x1,y1) to
	 * coordinates (x2,y2) by a semi-circular path. The distance between the
	 * middle of this path and the middle of the strait line linking (x1,y1) and
	 * (x2,y2) is middleDistance.
	 * 
	 * If middleDistance is positive, the arc is drawn clockwise, if not, it is
	 * draw counterclockwise.
	 * 
	 * @param g
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param middleDistance
	 */
	public static void drawArc2(Graphics g, int x1, int y1, int x2, int y2,
			double middleDistance) {
		double dist = Math2.dist(x1, y1, x2, y2, 2.0);
		double d = Math.abs(middleDistance);
		double r = d / 2 + dist * dist / (8 * d);
		int x12 = (x1 + x2) / 2;
		int y12 = (y1 + y2) / 2;
		double a = (x2 - x1) / dist;
		double b = (y2 - y1) / dist;
		int x0 = ((int) (-(r - d) * Math.signum(middleDistance) * (b))) + x12;
		int y0 = ((int) (-(r - d) * Math.signum(middleDistance) * (-a))) + y12;

		drawArc(g, x1, y1, x2, y2, x0, y0, middleDistance <= 0);
	}

	/**
	 * Let C be a semi-circular path linking (x1,y1) to (x2,y2). If the distance
	 * between the middle of C and the middle of the strait line L linking
	 * (x1,y1) to (x2,y2) is middleDistance; such that the arc is drawn
	 * clockwise if middleDistance is positive, and counterclockwise if not;
	 * 
	 * this method returns the angle made by L and the tangent of C at (x1,y1).
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param middleDistance
	 * @return returns the angle made by L and the tangent of C at (x1,y1).
	 */
	public static double fromMiddleDistanceToStartAngle(int x1, int y1, int x2,
			int y2, double middleDistance) {
		double dist = Math2.dist(x1, y1, x2, y2, 2.0);
		double d = Math.abs(middleDistance);
		double r = d / 2 + dist * dist / (8 * d);
		double alpha = Math.acos(1 - d / r);
		return (middleDistance > 0 ? 1 : -1) * alpha;
	}

	/**
	 * Let C be a semi-circular path linking (x1,y1) to (x2,y2). If the angle
	 * between the strait line L linking (x1,y1) to (x2,y2) and the tangent of C
	 * at (x1,y1) is startAngle,
	 * 
	 * this method returns the distance between the middle of C and the middle
	 * of L, such that the arc is drawn clockwise if middleDistance is positive,
	 * and counterclockwise if not;
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param middleDistance
	 * @return returns the angle made by L and the tangent of C at (x1,y1).
	 */
	public static double fromStartAngleToMiddleDistance(int x1, int y1, int x2,
			int y2, double startAngle) {
		double dist = Math2.dist(x1, y1, x2, y2, 2.0);
		double d = Math.sqrt(((1 - Math.cos(startAngle)) / (1 + Math
				.cos(startAngle))) * dist * dist / 4);
		double middleDistance = Math.signum(startAngle) * d;
		return middleDistance;
	}
}
