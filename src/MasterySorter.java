import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

public class MasterySorter {
	static String accountName;
	static String key = "c6f39229-9774-45c4-a457-fef3ac87b820";
	static JFrame J;
	static String summonerName;
	static String region = "na";
	final static String[] regions = { "BR", "EUNE", "EUW", "KR", "LAN", "LAS", "NA", "OCE", "RU", "TR" };

	/**
	 * This creates Frame containing an input text field for the Summoner Name
	 * and then runs the rest of the code if a summoner name is input. Currently
	 * is missing a check for valid summoner name.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		final ArrayList<ChampionName> List = loadChampionList();
		J = new JFrame("Mastery Sorter");
		J.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JTextField name = new JTextField(10);
		JLabel nameLabel = new JLabel("Summoner Name", JLabel.TRAILING);
		JButton OKButton = new JButton("OK");
		final JComboBox<String> regionDropDownList = new JComboBox<String>(regions);
		regionDropDownList.setSelectedItem("NA");
		JPanel firstRowPane = new JPanel();
		firstRowPane.setLayout(new BoxLayout(firstRowPane, BoxLayout.LINE_AXIS));
		firstRowPane.add(nameLabel);
		firstRowPane.add(Box.createRigidArea(new Dimension(5, 0)));
		firstRowPane.add(name);

		JPanel secondRowPane = new JPanel();
		secondRowPane.setLayout(new BoxLayout(secondRowPane, BoxLayout.LINE_AXIS));
		secondRowPane.add(regionDropDownList);

		J.setSize(400, 600);
		J.setLocationRelativeTo(null);
		J.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container content = J.getContentPane();
		content.setLayout(new BorderLayout());

		content.add(firstRowPane, BorderLayout.NORTH);
		content.add(secondRowPane, BorderLayout.CENTER);
		content.add(OKButton, BorderLayout.SOUTH);

		J.pack();
		J.setVisible(true);

		AbstractAction action = new AbstractAction() {
			/**
			 * NO IDEA WHAT THIS IS PLS HALP ECLIPSE AUTO GENERATED STUB
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					accountName = name.getText();
					region = regionDropDownList.getSelectedItem().toString();
					printData(sortData(getMasteryData(getAccountID(accountName))), List);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(0);
				}
			}
		};
		name.addActionListener(action);

		OKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accountName = name.getText();
				region = regionDropDownList.getSelectedItem().toString();
				long start = System.currentTimeMillis();
				try {
					printData(sortData(getMasteryData(getAccountID(accountName))), List);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(0);
				}
				System.out.println("Completed after " + (System.currentTimeMillis() - start) + " milliseconds.");
			}

		});
	}

	public static int getAccountID(String name) throws IOException {
		name = name.replace(" ", "");
		URL url = new URL(
				"https://na.api.pvp.net/api/lol/" + region + "/v1.4/summoner/by-name/" + name + "?api_key=" + key);
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String lineTemp = null;
		while ((lineTemp = br.readLine()) != null) {
			line = line + lineTemp;
		}

		Scanner s = new Scanner(line);
		String championInfo = s.nextLine();
		s.close();
		int position = championInfo.indexOf("\"id\"") + 5;
		int position2 = championInfo.indexOf(",", position);
		int accountID = Integer.parseInt(line.substring(position, position2));

		position = championInfo.indexOf("\"name\":\"") + 8;
		position2 = championInfo.indexOf(",", position) - 1;
		accountName = line.substring(position, position2);
		return accountID;
	}

	public static String getMasteryData(int accID) throws IOException {
		URL url = new URL(
				"https://na.api.pvp.net/championmastery/location/NA1/player/" + accID + "/champions?api_key=" + key);
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;
		while ((line = br.readLine()) != null) {
			return (line);
		}
		if (line == null)
			System.err.println("Failed to retrieve data");
		return line;
	}

	/**
	 * Loads a cached list of champions on local hard drive. If file is not up
	 * to date or nonexistent, it will call saveChampionList()
	 * 
	 * @return an array list of "championName" containing fields id and name.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static ArrayList<ChampionName> loadChampionList() throws NumberFormatException, IOException {
		// ChampionName[] c = new ChampionName[500];
		ArrayList<ChampionName> championList = new ArrayList<ChampionName>();
		for (int i = 0; i < 500; i++) {
			championList.add(null);
		}
		File s = new File("ChampionList.txt");
		if (!(s.isFile() && s.canRead()))
			s.createNewFile();

		Scanner s1 = new Scanner(s);
		while (s1.hasNextLine()) {
			String line = s1.nextLine();
			int champID = Integer.parseInt(line.substring(0, line.indexOf(",")));
			String champName = line.substring(line.indexOf(",") + 1);
			ChampionName tempChamp = new ChampionName(champID, champName);
			championList.set(tempChamp.CHAMPIONID, tempChamp);
		}
		s1.close();
		int size = 0;
		for (int i = 0; i < 500; i++) {
			if (championList.get(i) != null)
				size++;
		}
		if (size != getNumChampions()) {

			System.out.println("Updating database, please allow approximately 20 seconds.");
			saveChampionList();

			return loadChampionList();
		}

		System.out.println("Database is up to date with " + size + " champions saved.");
		return championList;
	}

	public static void saveChampionList() throws IOException {
		System.out.println("Currently writing champions to database, one moment please...");
		long timer = System.currentTimeMillis();
		File s = new File("ChampionList.txt");
		s.delete();
		s.createNewFile();
		FileWriter filewriter = new FileWriter(s);

		URL url2 = new URL(
				"https://global.api.pvp.net/api/lol/static-data/" + region + "/v1.2/champion?api_key=" + key);
		URLConnection con2 = url2.openConnection();
		InputStream is2 = con2.getInputStream();
		String line2 = new BufferedReader(new InputStreamReader(is2)).readLine();
		int position3 = 0;
		while (line2.substring(position3, line2.length()).contains("\"id\":")) {
			int position4 = line2.indexOf("\"id\":", position3) + 5;
			String id = line2.substring(position4, line2.indexOf(",", position4));
			int position5 = line2.indexOf("\"name\":", position4) + 8;
			String name = line2.substring(position5, line2.indexOf(",", position5) - 1);
			position3 = position5;
			filewriter.write(id + "," + name + String.format("%n"));
			System.out.println(name);
		}

		filewriter.close();
		System.out.println("Just finished after " + (int) ((System.currentTimeMillis() - timer) / 1000) + " seconds.");
	}

	private static int getNumChampions() throws IOException {
		URL url = new URL("https://na.api.pvp.net/api/lol/" + region + "/v1.2/champion?api_key=" + key);
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();

		String line = new BufferedReader(new InputStreamReader(is)).readLine();

		return line.length() - line.replaceAll("\"id\"", "...").length();
	}

	private static ArrayList<Champion> sortData(String data) {
		String[] splitData = data.split("}");
		int champID;
		int champPointsToLevel;
		int champLevel;
		int champPoints;
		ArrayList<Champion> listOfChampions = new ArrayList<Champion>();

		for (int i = 0; i < splitData.length - 1; i++) {
			int position = splitData[i].indexOf("championId");
			int position2 = splitData[i].indexOf(",", position);
			int position3;
			champID = Integer.parseInt(splitData[i].substring(position + 12, position2));

			champLevel = Integer.parseInt(splitData[i].substring(position2 + 17, position2 + 18));

			position3 = splitData[i].indexOf(",", position2 + 36);
			champPoints = Integer.parseInt(splitData[i].substring(position2 + 36, position3));
			position = splitData[i].indexOf("NextLevel");
			position2 = splitData[i].indexOf(",", position);
			
			champPointsToLevel = Integer.parseInt(splitData[i].substring(position + 11, position2));
			listOfChampions.add(new Champion(champID, champPointsToLevel, champLevel, champPoints));
		}
		return listOfChampions;
	}

	private static void printData(ArrayList<Champion> champions, ArrayList<ChampionName> championNames)
			throws IOException {
		Collections.sort(champions);
		int sum = 0;
		String output = "";
		System.out.println("Sorting champions by mastery points until next level...");
		for (int i = 0; i < champions.size(); i++) {
			sum += champions.get(i).CHAMPIONPOINTS;
			if (champions.get(i).CHAMPIONPOINTSTONEXTLEVEL > 0) {
				String name = championNames.get(champions.get(i).CHAMPIONID).CHAMPIONNAME;
				String tempOutput = name.substring(0, Math.min(name.length(), 10)) + "\t with "
						+ String.format("%4d", champions.get(i).CHAMPIONPOINTSTONEXTLEVEL) + " points to level "
						+ (champions.get(i).CHAMPIONLEVEL + 1) + ".";
				output += tempOutput + "\n";

			}
		}
		output = accountName + " has a total of " + sum + " mastery points over " + champions.size()
				+ " champions.\n\nListing champions sorted by points needed to next level...\n" + output;
		StyleContext sc = new StyleContext();
		final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
		JTextPane JText = new JTextPane(doc);
		try {
			doc.insertString(0, output, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		JFrame f = new JFrame();
		JText.setCaretPosition(0);
		JScrollPane SP = new JScrollPane(JText);

		f.getContentPane().add(SP);
		int outputWidth = 400;
		int outputHeight = 600;
		f.setSize(outputWidth, outputHeight);
		// integer totalOwnedWindows = J.getOwnedWindows().length;
		f.setLocation(J.getX() + J.getWidth(), J.getY());
		f.setVisible(true);

		System.out.println(
				"You have a total of " + sum + " mastery points over " + champions.size() + " champions. Nice!");

	}

	public static String getChampionName(int id) throws IOException {
		URL url = new URL("https://global.api.pvp.net/api/lol/static-data/" + region + "/v1.2/champion/" + id
				+ "?api_key=" + key);
		URLConnection con = url.openConnection();
		InputStream is;
		try {
			is = con.getInputStream();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error retrieving ", "Inane warning", JOptionPane.WARNING_MESSAGE);
			return "";
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String lineTemp = null;
		while ((lineTemp = br.readLine()) != null) {
			line = line + lineTemp;
		}
		Scanner s = new Scanner(line);
		String championInfo = s.nextLine();
		s.close();
		int position = championInfo.indexOf("name") + 7;
		int position2 = championInfo.indexOf(",", position);

		return line.substring(position, position2 - 1);
	}
}