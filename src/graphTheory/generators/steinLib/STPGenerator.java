package graphTheory.generators.steinLib;

import graphTheory.generators.InstanceGenerator;
import graphTheory.instances.steiner.classic.SteinerInstance;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public abstract class STPGenerator<T extends SteinerInstance> extends
		InstanceGenerator<T> {

	protected String instancesDirectoryName;
	protected String resultsFileName;

	/**
	 * Nom du paramètre auquel on associe le nom de l'instance
	 */
	public static final String OUTPUT_NAME_PARAM_NAME = "STPGenerator_outputNameParamName";

	/**
	 * Nom du paramètre auquel on associe la valeur de l'optimum dans le graphe
	 * généré.
	 */
	public static final String OUTPUT_OPTIMUM_VALUE_PARAM_NAME = "STPGenerator_outputOptimumValueParamName";
	public static final String OUTPUT_OPTIMUM_PARAM_NAME = "STPGenerator_outputOptimumParamName";

	/**
	 * Crée un générateur de graphes non orienté de Steiner à partir des
	 * instances découvertes sur le site de SteinLib sans paramètre d'entrée
	 * (non utilisable tel quel).
	 */
	public STPGenerator() {
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
	public STPGenerator(String instancesDirectoryName, String resultsFileName) {
		super();
		this.instancesDirectoryName = instancesDirectoryName;
		this.resultsFileName = resultsFileName;

		instanceFiles = new File(instancesDirectoryName).listFiles();
		Arrays.sort(instanceFiles, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

	}

	public String getInstancesPath() {
		return instancesDirectoryName + "/" + instanceFiles[index].getName();
	}

	/**
	 * Définit le dossier contenant les instances
	 * 
	 * @param iDN
	 */
	public void setInstancesDirectoryName(String iDN) {
		instancesDirectoryName = iDN;
		instanceFiles = new File(instancesDirectoryName).listFiles();
		Arrays.sort(instanceFiles, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	/**
	 * Définit le fichier contenant le poids de la solution optimale.
	 * 
	 * @param rFN
	 */
	public void setResultsFileName(String rFN) {
		resultsFileName = rFN;
	}

	protected int index = 0;
	protected File[] instanceFiles;

	/**
	 * Renvoie le nombre d'instances maximal du générateur.
	 */
	public int getNumberOfInstances() {
		return instanceFiles.length;
	}

	public void incrIndex() {
		index++;
		if (index >= instanceFiles.length)
			index = 0;
	}

	public void incrIndex(int value) {
		index += value;
		index %= instanceFiles.length;
	}
}

// TODO Relire
// TODO Refactor
// TODO Commenter
