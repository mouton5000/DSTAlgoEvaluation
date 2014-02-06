package graphTheory.graphDrawer.symbols;

/**
 * 
 * This abstract class define the graphical representation of an arc. It may be
 * a line, or a circular curve for instance.
 * 
 * @author Watel Dimitri
 * 
 */
public abstract class ArcSymbol {

	/**
	 * 
	 * @return true if this representation is a line linking its two endings.
	 */
	public boolean isLineSymbol() {
		return false;
	}

	/**
	 * 
	 * @return true if this representation is a circular curve defined with the
	 *         angle separating the segment linking its two endings and the
	 *         tangent of the curve at one of the endings.
	 */
	public boolean isCircleStartAngleSymbol() {
		return false;
	}

	/**
	 * 
	 * @return true if this representation is a circular curve defined with the
	 *         maximal distance from the segment linking its ending is defined.
	 */
	public boolean isCircleMiddleDistanceSymbol() {
		return false;
	}
}
