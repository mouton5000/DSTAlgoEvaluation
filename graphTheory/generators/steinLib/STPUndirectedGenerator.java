package graphTheory.generators.steinLib;

import graphTheory.graph.Arc;
import graphTheory.instances.steiner.classic.SteinerUndirectedInstance;
import graphTheory.steinLib.STPTranslationException;
import graphTheory.steinLib.STPTranslator;
import graphTheory.utils.FileManager;

import java.io.File;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Générateur de graphes non orienté de Steiner à partir des instances
 * découvertes sur le site de SteinLib. En entrée, il prends le dossier
 * contenant les instances. Il les ordonne. Lorsqu'on lui demande de les
 * générer, il prend le premier fichier, et génère le graphe. A la génération
 * suivante, il prendra le fichier suivant,... Si après la dernière génération,
 * on lui demande de générer à nouveau il regénèrera le premier fichier.
 * 
 * Les fichiers du dossier doivent être au format STP Le fichier de résultat
 * doit etre au format results (une suite de lignes "nom valeur")
 * 
 * Paramètres : le dossier contenant les instances le fichier contenant le poids
 * de la solution optimale.
 * 
 * Le graphe généré se voit doté d'un paramètre supplémentaire contenant le
 * poids de la solution optimale.
 * 
 * @author Watel DImitri
 * 
 */
public class STPUndirectedGenerator extends
		STPGenerator<SteinerUndirectedInstance> {

	/**
	 * Crée un générateur de graphes non orienté de Steiner à partir des
	 * instances découvertes sur le site de SteinLib sans paramètre d'entrée
	 * (non utilisable tel quel).
	 */
	public STPUndirectedGenerator() {
		this(null, null);
	}

	/**
	 * Crée un générateur de graphes non orienté de Steiner à partir des
	 * instances découvertes sur le site de SteinLib avec ces paramètres
	 * d'entrée
	 * 
	 * @param instancesDirectoryName
	 *            : le dossier contenant les instances
	 * @param resultsFileName
	 *            : le fichier contenant le poids de la solution optimale.
	 */
	public STPUndirectedGenerator(String instancesDirectoryName,
			String resultsFileName) {
		super(instancesDirectoryName, resultsFileName);
	}
	
	
	

	@Override
	public SteinerUndirectedInstance generate() {
		File f = instanceFiles[index];
		Pattern p = Pattern.compile("((\\w|-)+)\\.stp");
		Matcher m = p.matcher(f.getName());
		Integer optValue = null;
		String opt = null;
		if (m.matches()) {
			String name = m.group(1);

			if(resultsFileName != null){
			FileManager fm = new FileManager();
			fm.openRead(resultsFileName);
			String ligne;
			optValue = 0;
			for (int i = 0; i <= index; i++) {
				ligne = fm.readLine();
				p = Pattern.compile("((\\w|-)+) (\\d+)(.*)");
				m = p.matcher(ligne);
				if (m.matches()) {
					if (m.group(1).equals(name)) {
						optValue = Integer.valueOf(m.group(3));
						opt = m.group(4).trim();
						break;
					}
				} else
					return null;
			}
			}
			
			SteinerUndirectedInstance sug = null;
			try {
				sug = STPTranslator.translateUndirectedFile(f.getPath());
				sug.getGraph().defineParam(OUTPUT_NAME_PARAM_NAME, name);
				if(optValue == null)
					optValue = -1;
				sug.getGraph().defineParam(OUTPUT_OPTIMUM_VALUE_PARAM_NAME, optValue);
				HashSet<Arc> arborescence = null;
				if(opt != null && !opt.equals(""))
				{
					opt = opt.trim();
					opt = opt.substring(1,opt.length()-1);
					String[] arcs = opt.split(", ");
					arborescence = new HashSet<Arc>();
					for(String arc : arcs)
						arborescence.add(Arc.valueOf(arc));
				}
				sug.getGraph().defineParam(OUTPUT_OPTIMUM_PARAM_NAME, arborescence);
				
				incrIndex();
			} catch (STPTranslationException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
			
			return sug;
		} else {
			System.out.println(f.getName());
			return null;
		}
	}

	

}

// TODO Relire
// TODO Refactor
// TODO Commenter
