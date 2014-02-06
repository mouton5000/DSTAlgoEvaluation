package graphTheory.latex;

import graphTheory.graph.Arc;
import graphTheory.graph.Graph;

/**
 * This class contains one method: it returns a string drawing a graph g in
 * Tikz, the drawing langage for latex.
 * 
 * @author Watel Dimitri
 * 
 */
public class TikzTranslator {

	/**
	 * @param g
	 * @return a string drawing a graph g in Tikz, the drawing langage for
	 *         latex, considering the nodes coordinates are already defined.
	 */
	public static String translateGraph(Graph g) {
		String s = "\n" + "\\begin{tikzpicture}\n"
				+ "\\tikzset{tinoeud/.style={draw, minimum height=0.01cm}}\n";

		s += "\n";
		for (Integer n : g.getVertices()) {
			s += "\\node[tinoeud,"
					+ (g.isSquareSymbol(n) ? "rectangle" : "circle") + ",fill="
					+ (g.isFill(n) ? "black" : "white") + "] (V" + n + ") at ("
					+ (double) g.getNodeAbscisse(n) / 100 + ","
					+ -(double) g.getNodeOrdonnee(n) / 100 + ") {};\n";
		}

		s += "\n";
		for (Arc a : g.getEdges()) {
			s += "\\draw[-" + (a.isDirected() ? ">" : "") + ",>=latex] (V"
					+ a.getInput() + ") -- (V" + a.getOutput() + ");\n";
		}

		s += "\\end{tikzpicture}\n";
		return s;
	}
}
