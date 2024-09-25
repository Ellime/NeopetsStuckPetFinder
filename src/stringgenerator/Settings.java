package stringgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public final class Settings {

	// Settings
	private static String generationType = "random"; // Choices: random, iteration
	private static int qty = 1; // Qty. of strings to output
	private static int start = 0; // Where to start the iteration
	private static int length = 5; // Length of chars in string to generate
	private static String core = ""; // A string to include in the output. Core is inserted at index 0.
	private static String letters = "on"; // Choices: on, off
	private static String numbers = "on"; // Choices: on, off
	private static String underscore = "on";  // Choices: on, off
	
	private static String filename = "settings.txt";
	
	
	// HELPER FUNCTIONS
	
	public static void loadSettings() {
		try {
			File settings = new File(filename);
			Scanner reader = new Scanner(settings);
			
			// generationType
			String gt = reader.nextLine();
			if(!gt.equalsIgnoreCase("random") && !gt.equalsIgnoreCase("iteration")) {
				System.out.println("Error reading settings (generation type). Default is \"random\".");
			}
			else {
				generationType = gt;
			}
			
			// qty
			try {
				int q = Integer.parseInt(reader.nextLine());
				if(q < 1) { System.out.println("Qty. too small. Default is 1."); }
				else { qty = q; }
			} catch(NumberFormatException e) {
				System.out.println("Error reading settings (qty. strings). Default is 1.");
			}
			
			// start
			try {
				int s = Integer.parseInt(reader.nextLine());
				if(s < 0) { System.out.println("Start value invalid. Default is 0."); }
				else { start = s; }
			} catch(NumberFormatException e) {
				System.out.println("Error reading settings (start). Default is 0.");
			}
			
			// length
			try {
				int len = Integer.parseInt(reader.nextLine());
				if(len < 1) { System.out.println("Length too small. Default is 5."); }
				else { length = len; }
			} catch(NumberFormatException e) {
				System.out.println("Error reading settings (length). Default is 5.");
			}
			
			// core
			String c = reader.nextLine();
			int len = c.length() - 2;
			if(len < 0) {
				System.out.println("Error reading settings (core). Default is no core.");
			}
			else {
				core = c;
			}
			
			// letters
			String let = reader.nextLine();
			if(!let.equalsIgnoreCase("on") && !let.equalsIgnoreCase("off")) {
				System.out.println("Error reading settings (letters). Default is \"on\".");
			}
			else {
				letters = let;
			}
			
			// numbers
			String num = reader.nextLine();
			if(!num.equalsIgnoreCase("on") && !num.equalsIgnoreCase("off")) {
				System.out.println("Error reading settings (letters). Default is \"on\".");
			}
			else {
				numbers = num;
			}
			
			// underscore
			String us = reader.nextLine();
			if(!num.equalsIgnoreCase("on") && !num.equalsIgnoreCase("off")) {
				System.out.println("Error reading settings (underscore). Default is \"on\".");
			}
			else {
				underscore = us;
			}
			
			reader.close();
			Generator.setChars();
			checkStart();
		} catch(FileNotFoundException e) {
			System.out.println(filename + " not found. Using default settings.");
		}
	}
	
	public static void checkStart() {
		/*
		 * Checks whether the start itr is within the range of generatable strings.
		 * If not, it resets it to 0.
		 */

		int base = Generator.getAvailableChars().length();
		double permutations = Math.pow(base, length);
		
		if(start >= permutations) {
			System.out.println("Start too large. Reverting to 0.");
			start = 0;
		}
		
	}
	
	public static void printSettings() {
		System.out.println("SETTINGS");
		System.out.println("Generation type: " + generationType);
		System.out.println("Qty: " + qty);
		System.out.println("Start: " + start);
		System.out.println("Length: " + length);
		System.out.println("Core: " + core);
		System.out.println("Letters: " + letters);
		System.out.println("Numbers: " + numbers);
		System.out.println("Underscore: " + underscore);
	}
	
	public static void saveSettings() {
		String newline = System.getProperty("line.separator");
		String q = Integer.toString(qty);
		String s = Integer.toString(start);
		String l = Integer.toString(length);
		try {
			FileWriter settings = new FileWriter(filename, false);
			settings.write(generationType);
			settings.write(newline);
			settings.write(q);
			settings.write(newline);
			settings.write(s);
			settings.write(newline);
			settings.write(l);
			settings.write(newline);
			settings.write(core);
			settings.write(newline);
			settings.write(letters);
			settings.write(newline);
			settings.write(numbers);
			settings.write(newline);
			settings.write(underscore);
			settings.close();
		} catch(IOException e) {
			System.out.println("Error when saving settings.");
		}
	}
	
	
	// GETTERS AND SETTERS
	
	public static String getGenerationType() {
		return generationType;
	}


	public static void setGenerationType() {
		// Toggle between iteration/random
		if(Settings.generationType.equalsIgnoreCase("random")) {
			Settings.generationType = "iteration";
		}
		else {
			Settings.generationType = "random";
		}
	}


	public static int getQty() {
		return qty;
	}


	public static void setQty(String input) {
		try {
			int q = Integer.parseInt(input);
			if(q < 1) { System.out.println("Qty. too small."); }
			else { qty = q; }
		} catch(NumberFormatException e) {
			System.out.println("Error setting settings (qty. outputs).");
		}
	}
	
	
	public static int getStart() {
		return start;
	}
	
	public static void setStart(String input) {
		try {
			int s = Integer.parseInt(input);
			int base = Generator.getAvailableChars().length();
			double permutations = Math.pow(base, length);
			
			if(s < 0 || s >= permutations) { System.out.println("Start invalid."); }
			else { start = s; }
		} catch(NumberFormatException e) {
			System.out.println("Error setting settings (start).");
		}
	}


	public static int getLength() {
		return length;
	}


	public static void setLength(String input) {
		try {
			int len = Integer.parseInt(input);
			if(len < 1) { System.out.println("Length too small."); }
			else { Settings.length = len; }
		} catch(NumberFormatException e) {
			System.out.println("Error settings settings (length).");
		}
	}


	public static String getCore() {
		// Returns the core minus surrounding chars
		int l = core.length();
		String str = core.substring(1, l - 1);
		return str;
	}


	public static void setCore(String input) {
		// Sets the core, including surrounding chars
		Settings.core = input;
	}


	public static String getLetters() {
		return letters;
	}


	public static void setLetters() {
		// Toggle between on/off
		if(Settings.letters.equalsIgnoreCase("on")) {
			Settings.letters = "off";
		}
		else {
			Settings.letters = "on";
		}

		Generator.setChars();
		checkStart();
	}


	public static String getNumbers() {
		return numbers;
	}


	public static void setNumbers() {
		// Toggle between on/off
		if(Settings.numbers.equalsIgnoreCase("on")) {
			Settings.numbers = "off";
		}
		else {
			Settings.numbers = "on";
		}

		Generator.setChars();
		checkStart();
	}
	
	public static String getUnderscore() {
		return underscore;
	}
	
	public static void setUnderscore() {
		// Toggle between on/off
		if(Settings.underscore.equalsIgnoreCase("on")) {
			Settings.underscore = "off";
		}
		else {
			Settings.underscore = "on";
		}

		Generator.setChars();
		checkStart();
	}
}
