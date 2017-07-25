package graphTheory.steinLib;

/**
 * This enumeration contains all the possible error descriptions one can find in
 * a .stp file.
 *
 * @author Watel Dimitri
 *
 */
public enum STPTranslationEolienneExceptionEnum{

    BAD_FORMAT_CODE, EDGE_DESCRIPTION_BAD_FORMAT, NO_SECTION_PARAMETERS, NO_SECTION_PARAMETERS_CONTENT,
    PARAMETERS_DESC_BAD_FORMAT, FILE_ENDED_BEFORE_EOF_SPAR, PARAMETERS_KEYWORD_USED_TWICE, PARAMETERS_KEYWORD_MISSING_DEGSS, PARAMETERS_KEYWORD_MISSING_NBSEC, PARAMETERS_KEYWORD_MISSING_DMIN, PARAMETERS_KEYWORD_MISSING_JONCTIONSTST, PARAMETERS_KEYWORD_MISSING_JONCTIONSTDYN, NO_SECTION_CAPACITIES, NO_SECTION_CAPACITIES_CONTENT, CAPACITIES_DESC_BAD_FORMAT, FILE_ENDED_BEFORE_EOF_SCAP;

    @Override
    public String toString() {
        switch (this) {
            case BAD_FORMAT_CODE:
                return "The file format is not the one from version 1.0 with the code 33d32946,\n "
                        + "or the first line do not contains that code.";
            case EDGE_DESCRIPTION_BAD_FORMAT:
                return "This line is not correctly written. \n"
                        + "Expected format : A xx xx xx or A xx xx xx.xx if the graph is directed, " +
                        "or E xx xx xx or E xx xx xx.xx if not\n"
                        + "where xx xx xx(.xx) are respectively the ids of the linked nodes and the cost of the edge/arc in the graph.";
            case NO_SECTION_PARAMETERS:
                return "The file does not contain the Parameters section or does not contain the line " +
                        "\"SECTION Parameters\" introducing it.";
            case NO_SECTION_PARAMETERS_CONTENT:
                return "The Parameters section has an empty content.";
            case PARAMETERS_DESC_BAD_FORMAT:
                return "This line is not correctly written. \n"
                        + "Expected formats : \n"
                        + "DEGSS xx where xx is the maximum output degree of the root of the instance\n"
                        + "NBSEC xx where xx is the maximum number of section types in the solutions\n"
                        + "DMIN xx where xx is the radius of the circle around the required vertices in which we must use" +
                        " a dynamic cable\n"
                        + "JONCTION STST xx where xx is the cost of a jonction between two static cables\n"
                        + "JONCTION STDYN xx where xx is the cost of a jonction between a static and a dynamic cable\n";
            case FILE_ENDED_BEFORE_EOF_SPAR:
                return "The file is ended before closing the Parameters section and writing EOF at the end.";
            case PARAMETERS_KEYWORD_USED_TWICE:
                return "The same keyword was used twice in the section Parameters.";
            case PARAMETERS_KEYWORD_MISSING_DEGSS:
                return "The keyword degss is missing in the section Parameters";
            case PARAMETERS_KEYWORD_MISSING_NBSEC:
                return "The keyword degss is missing in the section Parameters";
            case PARAMETERS_KEYWORD_MISSING_DMIN:
                return "The keyword degss is missing in the section Parameters";
            case PARAMETERS_KEYWORD_MISSING_JONCTIONSTST:
                return "The keyword degss is missing in the section Parameters";
            case PARAMETERS_KEYWORD_MISSING_JONCTIONSTDYN:
                return "The keyword degss is missing in the section Parameters";
            case NO_SECTION_CAPACITIES:
                return "The file does not contain the Capacities section or does not contain the line " +
                        "\"SECTION Capacities\" introducing it.";
            case NO_SECTION_CAPACITIES_CONTENT:
                return "The Capacities section has an empty content.";
            case CAPACITIES_DESC_BAD_FORMAT:
                return "This line is not correctly written. \n"
                        + "Expected formats : \n"
                        + "ST xx xx where xx xx are respectively a capacity and the cost of one meter of a static cable" +
                        "with that capacity\n"
                        + "DY xx xx where xx xx are respectively a capacity and the cost of one meter of a static cable\" +\n" +
                        "                        \"with that capacity\n\n";
            case FILE_ENDED_BEFORE_EOF_SCAP:
                return "The file is ended before closing the Capacities section and writing EOF at the end.";

            default:
                return "";

        }
    }
}
