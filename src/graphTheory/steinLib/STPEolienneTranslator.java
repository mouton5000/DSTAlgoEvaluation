package graphTheory.steinLib;

import graphTheory.graph.Arc;
import graphTheory.graph.DirectedGraph;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.instances.steiner.classic.SteinerInstance;
import graphTheory.instances.steiner.classic.SteinerUndirectedInstance;
import graphTheory.instances.steiner.eoliennes.EolienneInstance;
import graphTheory.utils.FileManager;

import java.util.HashSet;
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
public class STPEolienneTranslator {

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
	public static EolienneInstance translateFile(String nomFic)
			throws STPTranslationException, STPTranslationEolienneException {
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

		if (!s.contains("33d32946")) {
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
		EolienneInstance g;
		if (m.matches()) {
			isDirected = m.group(1).equals("arcs");
			if (isDirected) {
				DirectedGraph dg = new DirectedGraph();
				g = new EolienneInstance(dg);
				letter = 'a';
			} else {
				throw new STPTranslationException(
						STPTranslationExceptionEnum.EDGE_NUMBER_BAD_FORMAT, nomFic,
						lineNumber, s);
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
		p = Pattern.compile(letter + " +(\\d+) +(\\d+) +(\\d+)(\\.(\\d+))?");
		Double cost;
		Integer n1, n2;

		while (!s.equals("end")) {
			m = p.matcher(s);
			if (m.matches()) {
				n1 = Integer.valueOf(m.group(1));
				n2 = Integer.valueOf(m.group(2));

                if(m.group(4) == null){
					cost = Double.valueOf(m.group(3));
				}
				else{
					cost = Double.valueOf(m.group(3)+m.group(4));
				}

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
				throw new STPTranslationEolienneException(
						STPTranslationEolienneExceptionEnum.EDGE_DESCRIPTION_BAD_FORMAT,
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
//		if (nov != 0) {
//			throw new STPTranslationException(
//					STPTranslationExceptionEnum.INCOHERENT_NB_NODES, nomFic,
//					lineNumber, s);
//		}
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
					g.setMaximumOutputDegree(n1, 1);
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
			s = s.trim();
		}
		if (not != 0) {
			throw new STPTranslationException(
					STPTranslationExceptionEnum.INCOHERENT_NB_TERMS, nomFic,
					lineNumber, s);
		}

		// On saute jusqu'à la section Parameters.
		while (!s.contains("section parameters")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationEolienneException(
						STPTranslationEolienneExceptionEnum.NO_SECTION_PARAMETERS, nomFic,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}


		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationEolienneException(
					STPTranslationEolienneExceptionEnum.NO_SECTION_PARAMETERS_CONTENT,
					nomFic, lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		HashSet<String> keywords = new HashSet<String>();
        keywords.add("degss");
        keywords.add("nbsec");
        keywords.add("dmin");
        keywords.add("jonction stst");
        keywords.add("jonction stdyn");
        p = Pattern.compile("(degss|nbsec|dmin|jonction stst|jonction stdyn) +(\\d+)(\\.(\\d+))?");
		while (!s.equals("end")) {
			m = p.matcher(s);
			if (m.matches()) {
				String parameter = m.group(1);
                if(!keywords.remove(parameter))
                    throw new STPTranslationEolienneException(
                            STPTranslationEolienneExceptionEnum.PARAMETERS_KEYWORD_USED_TWICE,
                            nomFic, lineNumber, s);
				if(parameter.equals("degss") && m.group(3) == null){
					Integer deg = Integer.valueOf(m.group(2));
					g.setMaximumOutputDegree(g.getRoot(), deg);
				}
				else if(parameter.equals("nbsec") && m.group(3)==null){
					Integer nbSec = Integer.valueOf(m.group(2));
					g.setMaxNbSec(nbSec);
				}
				else if(parameter.equals("dmin")){
					Double dmin;
					if(m.group(3) == null){
						dmin = Double.valueOf(m.group(2));
					}
					else{
						dmin = Double.valueOf(m.group(2)+m.group(3));
					}
					g.setDistanceMin(dmin);
				}
				else if(parameter.equals("jonction stst")){
					Double jstst;
					if(m.group(3) == null){
						jstst = Double.valueOf(m.group(2));
					}
					else{
						jstst = Double.valueOf(m.group(2)+m.group(3));
					}
					g.setStaticStaticBranchingNodeCost(jstst);
				}
				else if(parameter.equals("jonction stdyn")){
					Double jstdyn;
					if(m.group(3) == null){
						jstdyn = Double.valueOf(m.group(2));
					}
					else{
						jstdyn = Double.valueOf(m.group(2)+m.group(3));
					}
					g.setDynamicStaticBranchingNodeCost(jstdyn);
				}
				else {
					throw new STPTranslationEolienneException(
							STPTranslationEolienneExceptionEnum.PARAMETERS_DESC_BAD_FORMAT,
							nomFic, lineNumber, s);
				}
			} else {
				throw new STPTranslationEolienneException(
						STPTranslationEolienneExceptionEnum.PARAMETERS_DESC_BAD_FORMAT,
						nomFic, lineNumber, s);
			}
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationEolienneException(
						STPTranslationEolienneExceptionEnum.FILE_ENDED_BEFORE_EOF_SPAR,
						nomFic, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
		}
        if(!keywords.isEmpty()) {
            String keyword = keywords.iterator().next();
            if (keyword.equals("degss"))
                throw new STPTranslationEolienneException(
                        STPTranslationEolienneExceptionEnum.PARAMETERS_KEYWORD_MISSING_DEGSS,
                        nomFic, lineNumber, s);
            else if (keyword.equals("nbsec"))
                throw new STPTranslationEolienneException(
                        STPTranslationEolienneExceptionEnum.PARAMETERS_KEYWORD_MISSING_NBSEC,
                        nomFic, lineNumber, s);
            else if (keyword.equals("dmin"))
                throw new STPTranslationEolienneException(
                        STPTranslationEolienneExceptionEnum.PARAMETERS_KEYWORD_MISSING_DMIN,
                        nomFic, lineNumber, s);
            else if (keyword.equals("jonction stst"))
                throw new STPTranslationEolienneException(
                        STPTranslationEolienneExceptionEnum.PARAMETERS_KEYWORD_MISSING_JONCTIONSTST,
                        nomFic, lineNumber, s);
            else if (keyword.equals("jonction stdyn"))
                throw new STPTranslationEolienneException(
                        STPTranslationEolienneExceptionEnum.PARAMETERS_KEYWORD_MISSING_JONCTIONSTDYN,
                        nomFic, lineNumber, s);
        }

		// On saute jusqu'à la section Capacities.
		while (!s.contains("section capacities")) {
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationEolienneException(
						STPTranslationEolienneExceptionEnum.NO_SECTION_CAPACITIES, nomFic,
						lineNumber, s);
			}
			s = s.toLowerCase();
		}


		s = f.readLine();
		lineNumber++;
		if (s == null) {
			throw new STPTranslationEolienneException(
					STPTranslationEolienneExceptionEnum.NO_SECTION_CAPACITIES_CONTENT,
					nomFic, lineNumber, s);
		}
		s = s.toLowerCase();
		s = s.trim();
		p = Pattern.compile("(st|dy) +(\\d+) +(\\d+)(\\.(\\d+))?");
		while (!s.equals("end")) {
			m = p.matcher(s);
			if (m.matches()) {
				Integer capacity = Integer.valueOf(m.group(2));
				Double capacost;
				if(m.group(4) == null){
					capacost = Double.valueOf(m.group(3));
				}
				else{
					capacost = Double.valueOf(m.group(3)+m.group(4));
				}
				if(m.group(1).equals("st")){
					g.setStaticCapacityCost(capacity, capacost);
				}
				else{
					g.setDynamicCapacityCost(capacity, capacost);
				}
			} else {
				throw new STPTranslationEolienneException(
						STPTranslationEolienneExceptionEnum.CAPACITIES_DESC_BAD_FORMAT,
						nomFic, lineNumber, s);
			}
			s = f.readLine();
			lineNumber++;
			if (s == null) {
				throw new STPTranslationEolienneException(
						STPTranslationEolienneExceptionEnum.FILE_ENDED_BEFORE_EOF_SCAP,
						nomFic, lineNumber, s);
			}
			s = s.toLowerCase();
			s = s.trim();
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
}