package graphTheory.steinLib;

/**
 * This enumeration contains all the possible error descriptions 
 * one can find in a .stp file.
 * @author Watel Dimitri
 *
 */
public enum STPTranslationExceptionEnum {

	EMPTY_FILE,
	BAD_FORMAT_CODE,
	NO_SECTION_GRAPH,
	EMPTY_SECTION_GRAPH,
	NODE_NUMBER_BAD_FORMAT, 
	EDGE_NUMBER_BAD_FORMAT,
	NO_SECTION_GRAPH_CONTENT,
	EDGE_DESCRIPTION_BAD_FORMAT,
	FILE_ENDED_BEFORE_EOF_SG,
	INCOHERENT_NB_EDGES,
	NO_SECTION_TERM,
	EMPTY_SECTION_TERM,
	STRANGE_NB_TERM, 
	TERMINALS_NUMBER_BAD_FORMAT,
	NO_SECTION_TERM_CONTENT,
	TOO_MUCH_ROOT_SET,
	TERMINALS_DESC_BAD_FORMAT,
	FILE_ENDED_BEFORE_EOF_ST,
	INCOHERENT_NB_TERMS,
	FILE_ENDED_BEFORE_EOF
	;
	
	
	@Override
	public String toString() {
		switch (this){
		case BAD_FORMAT_CODE:
			return "Le fichier n'est pas au format de la version 1.0 : code 33d32945,\n "
					+ "ou la première ligne ne contient pas ce code.";
		case EMPTY_FILE:
			return "Le fichier ne contient pas de ligne";
		case NO_SECTION_GRAPH:
			return "Le fichier ne contient pas la section Graph ou ne contient pas la ligne \"SECTION Graph\" l'introduisant.";
		case EMPTY_SECTION_GRAPH:
			return "La section Graph est vide.";
		case NODE_NUMBER_BAD_FORMAT:
			return "La section Graph ne contient pas la ligne \"Nodes xxx\" indiquant le nombre de noeuds du graphe,\n "
					+ "ou celle ci est mal écrite.";
		case EDGE_NUMBER_BAD_FORMAT:
			return "La section Graph ne contient pas la ligne \"Arcs xxx\" indiquant le nombre d'arcs ou d'arrête du graphe,\n"
					+ "ou celle ci est mal écrite.";
		case NO_SECTION_GRAPH_CONTENT:
			return "La section Graph ne contient aucune ligne décrivant son contenu.";
		case EDGE_DESCRIPTION_BAD_FORMAT:
			return "Le format de cette ligne est incorrect. \n"
					+ "Format attendu : A xx xx xx si le graphe est orienté, ou E xx xx xx s'il ne l'est pas\n"
					+ "avec xx xx xx représentant respectivement le identifiant des deux noeuds reliés et le poids de l'arc.";
		case FILE_ENDED_BEFORE_EOF_SG:
			return "Le fichier est terminé sans avoir fermé la section Graph, décrit la section Terminal et placé EOF à la fin.";
		case INCOHERENT_NB_EDGES:
			return "Le nombre d'arcs décrit en début de section et le nombre d'arcs lu est différent.";
		case NO_SECTION_TERM:
			return "Le fichier ne contient pas la section Terminals ou ne contient pas la ligne \"SECTION Terminals\" l'introduisant.";
		case EMPTY_SECTION_TERM:
			return "La section Terminals est vide.";
		case STRANGE_NB_TERM:
			return "Il y a lus de terminaux que de noeud, ou moins de 0 terminal";
		case TERMINALS_NUMBER_BAD_FORMAT:
			return "La section Terminals ne contient pas la ligne \"Terminals xxx\" indiquant le nombre de terminaux du graphe,\n "
					+ "ou celle ci est mal écrite.";
		case NO_SECTION_TERM_CONTENT:
			return "La section Terminals ne contient aucune ligne décrivant son contenu.";
		case TOO_MUCH_ROOT_SET:
			return "La racine ne doit pas être décrite dans un graphe non orienté et une seule fois dans un graphe orienté.";
		case TERMINALS_DESC_BAD_FORMAT:
			return "Le format de cette ligne est incorrect. \n"
					+"Format attendu : T xx où xx désigne l'indentifiant du noeud terminal.";
		case INCOHERENT_NB_TERMS:
			return "Le nombre de terminaux décrit en début de section et le nombre de terminaux lu est différent.";
		case FILE_ENDED_BEFORE_EOF_ST:
			return "Le fichier est terminé sans avoir fermé la section Terminal et placé EOF à la fin.";
		case FILE_ENDED_BEFORE_EOF:
			return "Le fichier est terminé sans avoir placé EOF à la fin.";
		default:
			return "";
		
		}
	}
}
