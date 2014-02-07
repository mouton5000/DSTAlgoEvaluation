package graphTheory.graphDrawer.symbols;

/**
 * 
 * Define the graphical node representation: a circle of radius {@link #radius}.
 * 
 * @author Watel Dimitri
 * 
 */
public class NodeCircleSymbol extends NodeSymbol {

	public static final int DEFAULT_RADIUS = 25;

	int radius;

	public NodeCircleSymbol() {
		radius = DEFAULT_RADIUS;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public boolean isCircle() {
		return true;
	}

}
