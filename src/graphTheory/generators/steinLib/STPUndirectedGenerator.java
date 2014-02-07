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
 * 
 * This generator generated Undirected Steiner Instances from an STP file.
 * The format is described at http://steinlib.zib.de/
 * 
 * @author Watel Dimitri
 *
 */
public class STPUndirectedGenerator extends
		STPGenerator<SteinerUndirectedInstance> {

	public STPUndirectedGenerator() {
		this(null, null);
	}

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

			if (resultsFileName != null) {
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
				if (optValue == null)
					optValue = -1;
				sug.getGraph().defineParam(OUTPUT_OPTIMUM_VALUE_PARAM_NAME,
						optValue);
				HashSet<Arc> arborescence = null;
				if (opt != null && !opt.equals("")) {
					opt = opt.trim();
					opt = opt.substring(1, opt.length() - 1);
					String[] arcs = opt.split(", ");
					arborescence = new HashSet<Arc>();
					for (String arc : arcs)
						arborescence.add(Arc.valueOf(arc));
				}
				sug.getGraph().defineParam(OUTPUT_OPTIMUM_PARAM_NAME,
						arborescence);

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
