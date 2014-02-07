package graphTheory.generators.steinLib;

import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.steinLib.STPTranslationException;
import graphTheory.steinLib.STPTranslator;
import graphTheory.utils.FileManager;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This generator generated Directed Steiner Instances from an STP file.
 * The format is described at http://steinlib.zib.de/
 * 
 * @author Watel Dimitri
 *
 */
public class STPDirectedGenerator extends STPGenerator<SteinerDirectedInstance> {

	public STPDirectedGenerator() {
		this(null, null);
	}

	public STPDirectedGenerator(String instancesDirectoryName,
			String resultsFileName) {
		super(instancesDirectoryName, resultsFileName);
	}

	@Override
	public SteinerDirectedInstance generate() {
		File f = instanceFiles[index];
		Pattern p = Pattern.compile("((\\w|-)+)\\.stp");
		Matcher m = p.matcher(f.getName());
		if (m.matches()) {
			String name = m.group(1);

			Integer optValue = 0;
			if (resultsFileName != null) {
				FileManager fm = new FileManager();
				fm.openRead(resultsFileName);
				String opt;

				for (int i = 0; i <= index; i++) {
					opt = fm.readLine();
					p = Pattern.compile("((\\w|-)+) (\\d+)(.*)");
					m = p.matcher(opt);
					if (m.matches()) {
						if (m.group(1).equals(name)) {
							optValue = Integer.valueOf(m.group(3));
							break;
						}
					} else
						return null;
				}
			}

			SteinerDirectedInstance sdg = null;
			try {
				sdg = STPTranslator.translateDirectedFile(f.getPath());
				sdg.getGraph().defineParam(OUTPUT_NAME_PARAM_NAME, name);
				sdg.getGraph().defineParam(OUTPUT_OPTIMUM_VALUE_PARAM_NAME,
						optValue);
				incrIndex();

			} catch (STPTranslationException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				incrIndex();
				return null;
			}
			return sdg;
		} else {
			return null;
		}
	}

}