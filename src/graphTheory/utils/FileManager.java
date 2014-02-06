package graphTheory.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simplified methods to manage reading, erasing and writing text in files.
 * 
 * No exception has to be catch as it is already done in this class. If an
 * IOException occurs, it is printed in the err.
 * 
 * @author Watel Dimitri
 * 
 */
public class FileManager {

	private FileWriter fw;
	private BufferedReader br;

	/**
	 * Create a new FileManager. It can be associated with any file in order to
	 * read it, erase it or write in it.
	 * 
	 * @see #openErase(String)
	 * @see #openRead(String)
	 * @see #openWrite(String)
	 */
	public FileManager() {
	}

	/**
	 * Open the file which path is path in order to read it. One can then use
	 * the {@link #readLine()} method to read the file line by line. The file
	 * should be closed when the reading is finished.
	 * 
	 * @param path
	 * @see #readLine()
	 * @see #closeRead()
	 */
	public void openRead(String path) {
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the next line of a file, previously opened with method
	 *         {@link #openRead(String)}. Returns null if no file was opened or
	 *         if every lines of the file were read.
	 * @see #openRead(String)
	 */
	public String readLine() {
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Open the file which path is path in order to write in it. One can then
	 * use the {@link #write(String)}, {@link #writeln()} or
	 * {@link #writeln(String)} methods to insert text at the end of the file.
	 * The previous content of the file is not erased. The file should be closed
	 * with {@link #closeWrite()} when writing is finished. Notice that while
	 * the file is not closed, its content is not modified. <br/>
	 * One can flush the FileManager using {@link #flush()} method in order to
	 * put the new content into the file before closing it. <br/>
	 * In order to erase the content, use {@link #openErase(String)} method.
	 * 
	 * @param path
	 * @see #write(String)
	 * @see #writeln()
	 * @see #writeln(String)
	 * @see #openErase(String)
	 * @see #closeWrite()
	 * @see #flush()
	 */
	public void openWrite(String path) {
		try {
			fw = new FileWriter(path, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the file which path is path in order to erase it and replace its
	 * content. One can then use the {@link #write(String)}, {@link #writeln()}
	 * or {@link #writeln(String)} methods to insert text at the end of the
	 * file. The file should be closed when writing is finished. Notice that
	 * when the FileManager opens the file with this method, the content is
	 * immediately erased. But while the file is not closed, no new content is
	 * added. <br/>
	 * One can flush the FileManager using {@link #flush()} method in order to
	 * put the new content into the file before closing it. <br/>
	 * In order to write into the file without erasing the content, use
	 * {@link #openWrite(String)} method.
	 * 
	 * @param path
	 * @see #write(String)
	 * @see #writeln()
	 * @see #writeln(String)
	 * @see #openWrite(String)
	 * @see #closeWrite()
	 * @see #flush()
	 */
	public void openErase(String path) {
		try {

			fw = new FileWriter(path, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write at the end of a file, previously opened with
	 * {@link #openErase(String)} or {@link #openWrite(String)}, the String
	 * text. Notice that while the file is not closed with {@link #closeWrite()}
	 * or flush with {@link #flush()}, its content is not modified.
	 * 
	 * @param text
	 */
	public void write(String text) {
		try {
			if (fw != null)
				fw.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write at the end of a file, previously opened with
	 * {@link #openErase(String)} or {@link #openWrite(String)}, the String text
	 * and a new line. Notice that while the file is not closed with
	 * {@link #closeWrite()} or flush with {@link #flush()}, its content is not
	 * modified.
	 * 
	 * @param text
	 */
	public void writeln(String text) {
		try {
			if (fw != null)
				fw.write(text + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write at the end of a file, previously opened with
	 * {@link #openErase(String)} or {@link #openWrite(String)}, a new line.
	 * Notice that while the file is not closed with {@link #closeWrite()} or
	 * flush with {@link #flush()}, its content is not modified.
	 */
	public void writeln() {
		try {
			if (fw != null)
				fw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Flush the content added to a file, previously opened with
	 * {@link #openErase(String)} or {@link #openWrite(String)}.
	 */
	public void flush() {
		try {
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close a file, previously opened with {@link #openRead(String)}.
	 */
	public void closeRead() {
		try {
			if (br != null)
				br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close a file, previously opened with {@link #openWrite(String)} or
	 * {@link #openErase(String)}. This content added with
	 * {@link #write(String)} {@link #writeln()} or {@link #writeln(String)} is
	 * flushed into the file.
	 */
	public void closeWrite() {
		try {
			if (fw != null)
				fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
