package graphTheory.steinLib;

import graphTheory.utils.Couple;
import graphTheory.utils.FileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get from the web site http://steinlib.zib.de/ the differente steinlib name
 * groups and optimal results for each instance.
 * 
 * Those results can then be stored in a file to compare for each instance
 * approximated solutions returned by algorithms and optimal solutions given by
 * SteinLib.
 * 
 * @author Watel Dimitri
 * 
 */
public class SteinLibResultsParser {

	/**
	 * Create a folder Results in the root folder, and add for each SteinLib
	 * instances group of name "X" a file "X.results" containing all the
	 * instances in X and for each instance the optimal solution
	 */
	public static void saveResults() {
		ArrayList<Couple<String, String>> ar = getUrls();
		FileManager fm = new FileManager();
		for (Couple<String, String> c : ar) {
			fm.openErase("Results/" + c.second + ".results");
			fm.write(getNameAndResult(c.first));
			fm.closeWrite();
		}
	}

	/**
	 * Get from the web site http://steinlib.zib.de/ the name of all SteinLib
	 * instances groups, and for each group return the url of the webpage
	 * describing that group, associated with the name of the group.
	 */
	public static ArrayList<Couple<String, String>> getUrls() {
		String url = "http://steinlib.zib.de/testset.php";
		ArrayList<String> sourceCode = getSourceCode(url);
		ArrayList<Couple<String, String>> ar = new ArrayList<Couple<String, String>>();
		Pattern p = Pattern
				.compile("<tr><td><a href=\"(showset.php\\?\\w+)\">(\\w+)</a></td>");
		Matcher m;
		for (String sc : sourceCode) {
			m = p.matcher(sc);
			if (m.matches()) {
				ar.add(new Couple<String, String>("http://steinlib.zib.de/"
						+ m.group(1), m.group(2)));
			}
		}
		return ar;

	}

	/**
	 * @param url
	 * @return the http source code from the webpage at the url address given in
	 *         parameter.
	 */
	public static ArrayList<String> getSourceCode(String url) {
		URL oracle;
		ArrayList<String> ar = new ArrayList<String>();
		try {
			oracle = new URL(url);

			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				ar.add(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ar;
	}

	/**
	 * 
	 * @param url
	 *            address of a webpage describing a steinlib instances group.
	 *            For example "http://steinlib.zib.de/showset.php?I640"
	 * @return for each instance in the steinlib instances group described at
	 *         the given url, the name and the optimal solution of that
	 *         instance.
	 */
	public static String getNameAndResult(String url) {
		ArrayList<String> sourceCode = getSourceCode(url);
		StringBuilder s = new StringBuilder();
		Pattern p = Pattern
				.compile("<td align=\"left\" bgcolor=\"#C0C0C0\">&nbsp;((\\w|-)+)</td>.+<td bgcolor=\"#FFFF00\"><b>(\\d+)</b>&nbsp;</td></tr>");
		Matcher m;
		for (String sc : sourceCode) {
			m = p.matcher(sc);
			if (m.matches()) {
				s.append(m.group(1));
				s.append(" ");
				s.append(m.group(3));
				s.append("\n");
			}
		}
		return s.toString();
	}

	public static void main(String[] args) {
		System.out
				.println(getNameAndResult("http://steinlib.zib.de/showset.php?I640"));
	}
}
