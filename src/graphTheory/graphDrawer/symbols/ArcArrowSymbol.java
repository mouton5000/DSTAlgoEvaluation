package graphTheory.graphDrawer.symbols;

/**
 * 
 * Define the graphical arrow representation of the ending of an arc. Its ending
 * is defined by two parameters: the angle {@link #beta} of the tip of the
 * arrow, and the lenght {@link #sideDistance} of that tip
 * 
 * @author Watel Dimitri
 * 
 */
public class ArcArrowSymbol {

	private double beta;
	private int sideDistance;

	public ArcArrowSymbol() {
		beta = 0;
		sideDistance = 0;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public int getSideDistance() {
		return sideDistance;
	}

	public void setSideDistance(int sideDistance) {
		this.sideDistance = sideDistance;
	}

}
