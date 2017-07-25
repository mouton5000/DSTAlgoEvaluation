package graphTheory.steinLib;

/**
 * This enumeration contains all the possible error descriptions one can find in
 * a .stp file.
 * 
 * @author Watel Dimitri
 * 
 */
public enum STPTranslationExceptionEnum {

	EMPTY_FILE, BAD_FORMAT_CODE, NO_SECTION_GRAPH, EMPTY_SECTION_GRAPH, NODE_NUMBER_BAD_FORMAT, EDGE_NUMBER_BAD_FORMAT,
	NO_SECTION_GRAPH_CONTENT, EDGE_DESCRIPTION_BAD_FORMAT, FILE_ENDED_BEFORE_EOF_SG, INCOHERENT_NB_NODES,
	INCOHERENT_NB_EDGES, NO_SECTION_TERM, EMPTY_SECTION_TERM, STRANGE_NB_TERM, TERMINALS_NUMBER_BAD_FORMAT,
	NO_SECTION_TERM_CONTENT, TOO_MUCH_ROOT_SET, TERMINALS_DESC_BAD_FORMAT, FILE_ENDED_BEFORE_EOF_ST,
	INCOHERENT_NB_TERMS, FILE_ENDED_BEFORE_EOF;

	@Override
	public String toString() {
		switch (this) {
		case BAD_FORMAT_CODE:
			return "The file format is not the one from version 1.0 with the code 33d32945,\n "
					+ "or the first line do not contains that code.";
		case EMPTY_FILE:
			return "The file is empty.";
		case NO_SECTION_GRAPH:
			return "The file does not contain the Graph section or does not contain the line \"SECTION Graph\" introducing it.";
		case EMPTY_SECTION_GRAPH:
			return "The Graph section is empty.";
		case NODE_NUMBER_BAD_FORMAT:
			return "The graph section does not contain the line \"Nodes xxx\" precising the number of nodes in the graph,\n "
					+ "or it is not well written.";
		case EDGE_NUMBER_BAD_FORMAT:
			return "The graph section does not contain the line \"Arcs xxx\" precising the number of edges or arcs in the graph,\n "
					+ "or it is not well written.";
		case NO_SECTION_GRAPH_CONTENT:
			return "The graph section has an empty content.";
		case EDGE_DESCRIPTION_BAD_FORMAT:
			return "This line is not correctly written. \n"
					+ "Expected format : A xx xx xx if the graph is directed, or E xx xx xx if not\n"
					+ "where xx xx xx are respectively the ids of the linked nodes and the cost of the edge/arc in the graph.";
		case FILE_ENDED_BEFORE_EOF_SG:
			return "The file is ended before closing the section graph, describing the section Terminals and writing EOF at the end.";
		case INCOHERENT_NB_NODES:
			return "The number of nodes described at the beginning of the Graph section and the number of nodes described"
					+ "in that section is not the same.";
			case INCOHERENT_NB_EDGES:
			return "The number of arcs/edges described at the beginning of the Graph section and the number of arcs/edges described"
					+ "in that section is not the same.";
		case NO_SECTION_TERM:
			return "The file does not contain the Terminals section or does not contain the line \"SECTION Terminals\" introducing it.";
		case EMPTY_SECTION_TERM:
			return "The Terminals section is empty.";
		case STRANGE_NB_TERM:
			return "There are more terminals than nodes, or less than 0 terminals.";
		case TERMINALS_NUMBER_BAD_FORMAT:
			return "The Terminals section does not contain the line \"Terminals xxx\" precising the number of terminals in the graph,\n "
			+ "or it is not correctly written.";
		case NO_SECTION_TERM_CONTENT:
			return "The Terminals section has an empty content.";
		case TOO_MUCH_ROOT_SET:
			return "In an undirected graph, the root must not be described. In a directed graph, it must be described only once.";
		case TERMINALS_DESC_BAD_FORMAT:
			return "This line is not correctly written. \n"
					+ "Expected format : T xx where xx is the id of the node terminal in the graph.";
		case INCOHERENT_NB_TERMS:
			return "The number of terminals described at the beginning of the Terminals section and the number of terminals described"
			+ "in that section is not the same.";
		case FILE_ENDED_BEFORE_EOF_ST:
			return "The file is ended before closing the Terminals section and writing EOF at the end.";
		case FILE_ENDED_BEFORE_EOF:
			return "The file is ended before writing EOF at the end.";
		default:
			return "";

		}
	}
}
