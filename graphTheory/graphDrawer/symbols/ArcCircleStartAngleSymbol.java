package graphTheory.graphDrawer.symbols;

/**
 * 
 * Define the graphical representation of an arc: a circular curve which angle
 * {@link #startAngle} is the angle separating the segment linking its two
 * endings and the tangent of the curve at one of the endings.
 * 
 * @author Watel Dimitri
 * 
 */
public class ArcCircleStartAngleSymbol extends ArcSymbol {

	private double startAngle;

	public ArcCircleStartAngleSymbol() {
		startAngle = 0;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	@Override
	public boolean isCircleStartAngleSymbol() {
		return true;
	}

}
