package graphTheory.graphDrawer.symbols;

/**
 * 
 * This abstract class define the graphical representation of a node. It may be
 * a circle or a square.
 * 
 * @author Watel Dimitri
 * 
 */
public abstract class NodeSymbol {

	/**
	 * 
	 * @return true if this representation is a circle.
	 */
	public boolean isCircle() {
		return false;
	}

	/**
	 * 
	 * @return true if this representation is a square.
	 */
	public boolean isSquare() {
		return false;
	}
}
