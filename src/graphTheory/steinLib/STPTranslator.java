package graphTheory.steinLib;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.graph.UndirectedGraph;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.instances.steiner.classic.SteinerInstance;
import graphTheory.instances.steiner.classic.SteinerUndirectedInstance;
import graphTheory.utils.FileManager;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This class contains static method to translate .stp files into a
 * {@link SteinerInstance}, directed or not, and to translate a
 * {@link SteinerInstance} into a .stp file.
 * 
 * @author Watel Dimitri
 * 
 */
public class STPTranslator {

	/**
	 * Return the {@link SteinerDirectedInstance} or the
	 * {@link SteinerUndirectedInstance} conresponding to the .stp file in
	 * input. If the file describes a graph containing directed arcs and
	 * undirected edges, then this method returns null.
	 * 
	 * @param nomFic
	 *            path of the file describing the instance we want to build.
	 * @return
	 * @throws STPTranslationException
	 */
	public static SteinerInstance translateFile(String nomFic)
			throws STPTranslationException {
		FileManager f = new FileManager();
		f.openRead(nomFic);
		String s;
		int lineNumber = 0;
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EMPTY_FILE, nomFic, lineNumber,
					null);
		}

		s = s.toLowerCase();

		// On vérifie que le fichier est au bon format

		if (!s.contains("33d32945")) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.BAD_FORMAT_CODE, nomFic,
					lineNumber, s);
		}

		// On saute l'espace réservé aux commentaires.
		while (!s.contains("section graph")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.NO_SECTION_GRAPH, nomFic,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}

		// On récupère le nombre de noeuds.
		s = f.readLine();
		lineNumber++;

		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EMPTY_SECTION_GRAPH, nomFic,
					lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		Pattern p = Pattern.compile("nodes +(\\d+)");
		Matcher m = p.matcher(s);
		int nov;
		if (!m.matches())
			throw new STPTranslationException(
					STPTranslationExceptionEnum.NODE_NUMBER_BAD_FORMAT, nomFic,
					lineNumber, s);
		nov = Integer.valueOf(m.group(1));
		// On récupère le nombre d'arcs
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EDGE_NUMBER_BAD_FORMAT, nomFic,
					lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		p = Pattern.compile("(edges|arcs) +(\\d+)");
		m = p.matcher(s);
		boolean isDirected;
		int noe;
		char letter;
		SteinerInstance g;
		if (m.matches()) {
			isDirected = m.group(1).equals("arcs");
			if (isDirected) {
				DirectedGraph dg = new DirectedGraph();
				g = new SteinerDirectedInstance(dg);
				letter = 'a';
			} else {
				UndirectedGraph ug = new UndirectedGraph();
				g = new SteinerUndirectedInstance(ug);
				letter = 'e';
			}
			noe = Integer.valueOf(m.group(2));
		} else {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EDGE_NUMBER_BAD_FORMAT, nomFic,
					lineNumber, s);
		}

		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.NO_SECTION_GRAPH_CONTENT,
					nomFic, lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		while (s.equals("")) {
			s = f.readLine();
			lineNumber++;

			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.NO_SECTION_GRAPH_CONTENT,
						nomFic, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
		}
		p = Pattern.compile(letter + " +(\\d+) +(\\d+) +(\\d+)");
		int cost;
		Integer n1, n2;
		while (!s.equals("end")) {
			m = p.matcher(s);
			if (m.matches()) {
				n1 = Integer.valueOf(m.group(1));
				n2 = Integer.valueOf(m.group(2));
				cost = Integer.valueOf(m.group(3));
				if (!g.getGraph().contains(n1)) {
					g.getGraph().addVertice(n1);
					nov--;
				}
				if (!g.getGraph().contains(n2)) {
					g.getGraph().addVertice(n2);
					nov--;
				}

				Arc a;
				if (isDirected)
					a = g.getGraph().addDirectedEdge(n1, n2);
				else
					a = g.getGraph().addUndirectedEdge(n1, n2);
				g.setCost(a, cost);
			} else {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.EDGE_DESCRIPTION_BAD_FORMAT,
						nomFic, lineNumber, s);
			}

			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.FILE_ENDED_BEFORE_EOF_SG,
						nomFic, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
			noe--;
		}

		if (nov != 0) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.INCOHERENT_NB_NODES, nomFic,
					lineNumber, s);
		}
		if (noe != 0) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.INCOHERENT_NB_EDGES, nomFic,
					lineNumber, s);
		}

		// On saute jusqu'aux terminaux
		while (!s.contains("section terminals")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.NO_SECTION_TERM, nomFic,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}

		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.EMPTY_SECTION_TERM, nomFic,
					lineNumber, s);
		}
		s = s.toLowerCase();
		p = Pattern.compile("terminals +(\\d+)");
		m = p.matcher(s);
		int not;
		int size = g.getGraph().getNumberOfVertices();
		if (m.matches()) {
			not = Integer.valueOf(m.group(1));
			if (not > size || not <= 0) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.STRANGE_NB_TERM, nomFic,
						lineNumber, s);
			}
		} else {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.TERMINALS_NUMBER_BAD_FORMAT,
					nomFic, lineNumber, s);
		}

		boolean rootSet = false;
		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.NO_SECTION_TERM_CONTENT,
					nomFic, lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		p = Pattern.compile("(t +(\\d+))|(root +(\\d+))");
		while (!s.equals("end")) {
			m = p.matcher(s);
			if (m.matches()) {
				if (m.group(3) != null) {
					if (rootSet || !isDirected) {
						throw new STPTranslationException(
								STPTranslationExceptionEnum.TOO_MUCH_ROOT_SET,
								nomFic, lineNumber, s);
					} else {
						rootSet = true;
						((SteinerDirectedInstance) g).setRoot(Integer.valueOf(m
								.group(4)));
					}
				} else {
					n1 = Integer.valueOf(m.group(2));
					g.setRequired(n1, true);
					not--;
				}
			} else {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.TERMINALS_DESC_BAD_FORMAT,
						nomFic, lineNumber, s);
			}
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.FILE_ENDED_BEFORE_EOF_ST,
						nomFic, lineNumber, s);
			}
			s = s.toLowerCase();

		}
		if (not != 0) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.INCOHERENT_NB_TERMS, nomFic,
					lineNumber, s);
		}

		// On saute l'espace reservé aux coordonnées
		while (!s.contains("eof")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.FILE_ENDED_BEFORE_EOF,
						nomFic, lineNumber, s);
			}
			s = s.toLowerCase();
		}
		return g;
	}

	/**
	 * 
	 * Return the {@link SteinerDirectedInstance} conresponding to the .stp file
	 * in input. If the file describes a graph containing undirected edges, then
	 * this method returns null.
	 * 
	 * @param nomFic
	 *            path of the file describing the instance we want to build.
	 * @return
	 * @throws STPTranslationException
	 */
	public static SteinerDirectedInstance translateDirectedFile(String nomFic)
			throws STPTranslationException {
		SteinerInstance g = translateFile(nomFic);
		if (g == null || g instanceof SteinerUndirectedInstance)
			return null;
		else
			return (SteinerDirectedInstance) g;
	}

	/**
	 * 
	 * Return the {@link SteinerUndirectedInstance} conresponding to the .stp
	 * file in input. If the file describes a graph containing directed arcs,
	 * then this method returns null.
	 * 
	 * @param nomFic
	 *            path of the file describing the instance we want to build.
	 * @return
	 * @throws STPTranslationException
	 */
	public static SteinerUndirectedInstance translateUndirectedFile(
			String nomFic) throws STPTranslationException {
		SteinerInstance g = translateFile(nomFic);
		if (g == null || g instanceof SteinerDirectedInstance)
			return null;
		else
			return (SteinerUndirectedInstance) g;
	}

	/**
	 * Translate the Steiner instance g into a .stp file, which path is nomFic.
	 * 
	 * @param g
	 * @param nomFic
	 */
	public static void translateSteinerGraph(SteinerInstance g, String nomFic) {
		translateSteinerGraph(g, nomFic, "");
	}

	/**
	 * Translate the Steiner instance g into a .stp file, which path is nomFic.
	 * Associate the instance to the name given in parameter in the file.
	 * 
	 * @param g
	 * @param nomFic
	 */
	public static void translateSteinerGraph(SteinerInstance g, String nomFic,
			String name) {
		boolean isDirected = g instanceof SteinerDirectedInstance;
		FileManager f = new FileManager();
		f.openErase(nomFic);

		f.writeln("33d32945 STP File, STP Format Version 1.0");
		f.writeln();
		f.writeln("SECTION Comment");
		f.writeln("Name    \"" + name + "\"");
		f.writeln("Creator \"Dimitri Watel\"");
		f.writeln("Remark  \"\"");
		f.writeln("END");
		f.writeln();
		f.writeln("SECTION Graph");
		f.writeln("Nodes " + g.getGraph().getNumberOfVertices());
		f.writeln((isDirected ? "Arcs" : "Edges") + " "
				+ g.getGraph().getNumberOfEdges());
		Iterator<Arc> it = g.getGraph().getEdgesIterator();
		Arc a;
		while (it.hasNext()) {
			a = it.next();
			f.writeln((isDirected ? 'A' : 'E') + " " + a.getInput() + " "
					+ a.getOutput() + " " + g.getIntCost(a));
		}
		f.writeln("END");
		f.writeln();
		f.writeln("SECTION Terminals");
		f.writeln("Terminals " + g.getNumberOfRequiredVertices());
		if (isDirected) {
			f.writeln("Root " + ((SteinerDirectedInstance) g).getRoot());
		}
		Iterator<Integer> it2 = g.getRequiredVerticesIterator();
		Integer n;
		while (it2.hasNext()) {
			n = it2.next();
			f.writeln("T " + n);
		}
		f.writeln("END");
		f.writeln();
		f.writeln("EOF");

		f.closeWrite();
	}
}