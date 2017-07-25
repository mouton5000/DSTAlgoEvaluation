package graphTheory.generators.eolienne;

import graphTheory.generators.steinLib.STPGenerator;
import graphTheory.instances.steiner.classic.SteinerDirectedInstance;
import graphTheory.instances.steiner.eoliennes.EolienneInstance;
import graphTheory.steinLib.STPEolienneTranslator;
import graphTheory.steinLib.STPTranslationEolienneException;
import graphTheory.steinLib.STPTranslationException;
import graphTheory.steinLib.STPTranslator;

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
public class STPEolienneDirectedGenerator extends STPGenerator<EolienneInstance> {

	public STPEolienneDirectedGenerator(String instancesDirectoryName) {
		super(instancesDirectoryName, null);
	}

	@Override
	public EolienneInstance generate() {
		File f = instanceFiles[index];
		Pattern p = Pattern.compile("((\\w|-)+)\\.stp");
		Matcher m = p.matcher(f.getName());
		if (m.matches()) {
			String name = m.group(1);

            EolienneInstance eol;
			try {
				eol = STPEolienneTranslator.translateFile(f.getPath());
				incrIndex();
			} catch (STPTranslationException | STPTranslationEolienneException e) {
				e.printStackTrace();
				incrIndex();
				return null;
			}

			eol.getGraph().defineParam(OUTPUT_NAME_PARAM_NAME, name);
            return eol;
        } else {
			return null;
		}
	}

}