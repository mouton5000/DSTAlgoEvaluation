package graphTheory.graphDrawer.symbols;

/**
 * 
 * Define the graphical node representation: a square of side lenght
 * {@link #sideLength}.
 * 
 * @author Watel Dimitri
 * 
 */
public class NodeSquareSymbol extends NodeSymbol {

	public static final int DEFAULT_SIDE_LENGTH = 50;

	int sideLength;

	public NodeSquareSymbol() {
		super();
		this.sideLength = DEFAULT_SIDE_LENGTH;
	}

	public int getSideLength() {
		return sideLength;
	}

	public void setSideLength(int sideLength) {
		this.sideLength = sideLength;
	}

	@Override
	public boolean isSquare() {
		return true;
	}

}
