import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class saveChampions {
	/**
	 * this method  will load champions saved in format: id name separated by lines
	 */
	public static void LoadChampionList() throws FileNotFoundException {
		File s = new File("tempFile.txt");
		if (!(s.isFile() && s.canRead())) 
			System.err.println("File does not exist or is not readable!");
		
		Scanner s1 = new Scanner(s);
		s1.nextLine();
		s1.close();
	}
}
