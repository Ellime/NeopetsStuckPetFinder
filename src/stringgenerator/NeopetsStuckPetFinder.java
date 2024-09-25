package stringgenerator;

import java.io.IOException;
import java.util.Scanner;

import httprequester.Requester;

public class NeopetsStuckPetFinder {
	
	public static void printMenu() {
		System.out.println("MENU");
		System.out.println("0: Search in the pound");
		System.out.println("1: View settings");
		System.out.println("2: Toggle iteration/random");
		System.out.println("3: Set qty. strings");
		System.out.println("4: Set start (iteration only)");
		System.out.println("5: Set string length");
		System.out.println("6: Set core");
		System.out.println("7: Toggle letters");
		System.out.println("8: Toggle numbers");
		System.out.println("9: Toggle underscore");
		System.out.println("10: Save settings");
		System.out.println("11: Load settings");
		System.out.println();
		System.out.print("Choose an option: ");
	}

	public static void main(String[] args) throws IOException {
		
		// Initialize settings and login
		Settings.loadSettings();
		Requester.readCredentials();
		Requester.login();
		
		// User input variables
		Scanner user = new Scanner(System.in);
		int option;
		String input;
		
		while(true) {
			NeopetsStuckPetFinder.printMenu();
			try {
				option = Integer.parseInt(user.next());
			} catch(NumberFormatException e) {
				System.out.println("Closing program.");
				break;
			}
			
			if(option == 0) {
				Generator.generateOutput();
				Generator.saveOutput();
				Requester.searchInPound();
			}
			else if(option == 1) { Settings.printSettings(); }
			else if(option == 2) { Settings.setGenerationType(); }
			else if(option == 3) {
				System.out.print("Enter the new qty: ");
				input = user.next();
				Settings.setQty(input);
			}
			else if(option == 4) {
				System.out.print("Enter the new start (starting at 0): ");
				input = user.next();
				Settings.setStart(input);
			}
			else if(option == 5) {
				System.out.print("Enter the new length: ");
				input = user.next();
				Settings.setLength(input);
			}
			else if(option == 6) {
				System.out.print("Enter the new core with quotes (\"\" for no core): ");
				input = user.next();
				Settings.setCore(input);
			}
			else if(option == 7) { Settings.setLetters(); }
			else if(option == 8) { Settings.setNumbers(); }
			else if(option == 9) { Settings.setUnderscore(); }
			else if(option == 10) { Settings.saveSettings(); }
			else if(option == 11) { Settings.loadSettings(); }
			else {
				System.out.println("PROGRAM CLOSED");
				break;
			}
			System.out.println();
		}
		user.close();
	}

}
