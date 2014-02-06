package graphTheory.graphDrawer.symbols;

/**
 * 
 * Define the graphical representation of an arc: a circular curve which maximal
 * distance from the segment linking its ending is defined by
 * {@link #middleDistance}.
 * 
 * @author Watel Dimitri
 * 
 */
public class ArcCircleMiddleDistanceSymbol extends ArcSymbol {

	private double middleDistance;

	public ArcCircleMiddleDistanceSymbol() {
		middleDistance = 0;
	}

	public double getMiddleDistance() {
		return middleDistance;
	}

	public void setMiddleDistance(double middleDistance) {
		this.middleDistance = middleDistance;
	}

	@Override
	public boolean isCircleMiddleDistanceSymbol() {
		return true;
	}

}
