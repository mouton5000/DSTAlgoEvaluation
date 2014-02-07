package graphTheory.generators.steinLib;

import graphTheory.generators.InstanceGenerator;
import graphTheory.instances.steiner.classic.SteinerInstance;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 
 * This generator merge some elements in the classes
 * {@link STPUndirectedGenerator} and {@link STPDirectedGenerator}
 * 
 * Those generators create SteinerInstances from STP files.
 * The STP format is described at http://steinlib.zib.de/
 * 
 * @author Watel Dimitri
 *
 * @param <T>
 */
public abstract class STPGenerator<T extends SteinerInstance> extends
		InstanceGenerator<T> {

	protected String instancesDirectoryName;
	protected String resultsFileName;

	/**
	 * Parameter name to which the name of the instance is associated
	 */
	public static final String OUTPUT_NAME_PARAM_NAME = "STPGenerator_outputNameParamName";

	/**
	 * Parameter name to which the cost of an optimal solution is associated
	 */
	public static final String OUTPUT_OPTIMUM_VALUE_PARAM_NAME = "STPGenerator_outputOptimumValueParamName";
	
	/**
	 * Parameter name to which an optimal solution is associated if it was computed.
	 */
	public static final String OUTPUT_OPTIMUM_PARAM_NAME = "STPGenerator_outputOptimumParamName";

	public STPGenerator() {
		this(null, null);
	}

	/**
	 * 
	 * @param instancesDirectoryName
	 *            : a directory containing stp files
	 * @param resultsFileName
	 *            : a file containing for each instance the cost of an optimal solution and/or an optimal solution
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

	public void setResultsFileName(String rFN) {
		resultsFileName = rFN;
	}

	protected int index = 0;
	protected File[] instanceFiles;

	/**
	 * 
	 * @return the number of instances the directory contains 
	 */
	public int getNumberOfInstances() {
		return instanceFiles.length;
	}

	/**
	 * Tell the generator to increase by 1 the index pointing at the 
	 * next generated instance.
	 */
	public void incrIndex() {
		incrIndex(1);
	}

	/**
	 * Tell the generator to increase by value the index pointing at the 
	 * next generated instance.
	 * 
	 * @param value
	 */
	public void incrIndex(int value) {
		index += value;
		index %= instanceFiles.length;
	}
}
