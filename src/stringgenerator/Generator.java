package stringgenerator;

/*
 * Libraries used for parallelization are included.
 * See line 51 for details.
 */
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import httprequester.Requester;

public class Generator {
	
	private static String letters = "abcdefghijklmnopqrstuvwxyz"; // Neopet names are case-insensitive
	private static String numbers = "0123456789";
	private static String underscore = "_";
	private static String availableChars = ""; // Valid chars
	private static String[] output;
//	private static String filename = "strings.txt"; // For debugging - to save generated strings to
	
	
	// HELPER FUNCTIONS
	
	public static void setChars() {
		/*
		 * Generates the range of possible chars.
		 * Called once per string generation.
		 * Ordered alphabetically.
		 */
		availableChars = "";
		// Call each time before generating strings.
		if(Settings.getLetters().equalsIgnoreCase("on")) {
			availableChars = availableChars.concat(letters);
		}
		if(Settings.getNumbers().equalsIgnoreCase("on")) {
			availableChars = availableChars.concat(numbers);
		}
		if(Settings.getUnderscore().equalsIgnoreCase("on")) {
			availableChars = availableChars.concat(underscore);
		}
	}
	
	public static char generateRandomChar() {
		Random r = new Random();
		int l = availableChars.length();
		char c = availableChars.charAt(r.nextInt(l));
		return c;
	}
	
	public static String generateRandomString() throws InterruptedException {
		int length = Settings.getLength();
		String output = Settings.getCore();
		
		/*
		 * The commented out block is an alternative implementation to parallelize the assigning of chars to a string.
		 * The overhead may make it slower than the sequential implementation, unless you have many processors available.
		 */
		
//		int toGenerate = length - output.length();
		
		// Setup for parallelization
/*		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService EXEC = Executors.newFixedThreadPool(cores);
		List<Callable<Character>> tasks = new ArrayList<Callable<Character>>();
		
		// Char generation parallelized
		for(int i = 0; i < toGenerate; i++) {
			Callable<Character> c = new Callable<Character>() {
				@Override
				public Character call() throws Exception {
					return generateRandomChar();
				}
			};
			tasks.add(c); // Shared resource
		}
		List<Future<Character>> cList = EXEC.invokeAll(tasks);
		
		// Put together output string
		for(int i = 0; i < cList.size(); i++) {
			output += cList.get(i);
		}
*/
		
		/*
		 * The following is the sequential implementation of assigning chars to a string.
		 * Comment this for-loop out if you wish to use the parallel implementation.
		 */
		for(int i = 0; i < length; i++) {
			char c = generateRandomChar();
			output += c; // Shared resource
		}
		
		return output;
	}
	
	public static int[] generateIterationIndices(int itr) {
		// Input: iterator value
		// Converts iterator into an array of ints to be used to index the range of chars
		// Iterator is first converted from decimal to the right base
		
		int length = Settings.getLength();
		int indices[] = new int[length];
		int base = availableChars.length();
		double remainder = itr;
		
		for(int i = 0; i < length; i++) {
			remainder /= base;
			// Using BigDecimal to separate the remainder
			double d = new BigDecimal(remainder - Math.floor(remainder)).doubleValue();
			indices[i] = (int) Math.round(base * d);
			// Prepare remainder for next iteration
			remainder = Math.floor(remainder);
		}
		return indices;
	}
	
	public static String generateIterationString(int[] indices) {
		/*
		 * Converts an array of indices into a string.
		 * Returns the string.
		 */
		int length = Settings.getLength();
		String output = Settings.getCore();

		for(int i = 0; i < length; i++) {
			int index = indices[i];
			char c = availableChars.charAt(index);
			output += c;
		}
		
		return output;
	}
	
	public static void generateOutput(){
		
		System.out.println("Generating pet names...");

		// Check chars contains chars to iterate with
		if(availableChars.length() == 0) {
			System.out.println("You set the length to 0! What do you expect to generate?");
			return;
		}
		
		int qty = Settings.getQty();
		output = new String[qty];
		String generationType = Settings.getGenerationType();
		
		if(generationType.equalsIgnoreCase("random")) {
			// Generate strings through randomness
			for(int i = 0; i < qty; i++) {
				try {
					output[i] = generateRandomString();
				} catch (InterruptedException e) {
					System.out.println("Interruption when generating strings.");
				} 
			}
		}
		
		else if(generationType.equalsIgnoreCase("iteration")) {
			
			// Create starting iteration value
			int itr = Settings.getStart();
			int indices[];
			int base = availableChars.length();
			double permutations = Math.pow(base, Settings.getLength());
			
			// Generate strings through iteration
			for(int i = 0; i < qty; i++) {
				indices = generateIterationIndices(itr);
				output[i] = generateIterationString(indices);
				itr++;
				if(itr >= permutations) { itr = 0; } // Loop back to beginning
			}
		}
		
		System.out.println("Names successfully generated.");
	}
	
	public static void saveOutput() {
		/*
		 * Sends the generated strings to the Requester.
		 * Can also save strings to a file.
		 */
		Requester.setPetnames(output);
		
//		// Block for saving strings to a file
//		String newline = System.getProperty("line.separator");
//		try {
//			FileWriter strings = new FileWriter(filename, false);
//			for(int i = 0; i < output.length; i++) {
//				strings.write(output[i]);
//				strings.write(newline);
//			}
//			strings.close();
//			System.out.println("Finished generating strings!");
//		} catch(IOException e) {
//			System.out.println("Error when saving strings.");
//		}
//		// Uncomment this block to save strings to a file
		
	}
	
	// GETTERS
	
	public static String getAvailableChars() {
		return availableChars;
	}
	
	public static String[] getOutput() {
		return output;
	}
}
