package httprequester;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * CREDITS:
 * 
 * HTTP request functions are borrowed from mkyong:
 * https://mkyong.com/java/how-to-automate-login-a-website-java-example/
 */

public class Requester {
	
	// Login credentials
	private static String credentialsFile = "credentials.txt";
	private static String username;
	private static String password;
	
	// Global values
	private static String loginUrl = "http://www.neopets.com/login/";
	private static String sendLoginUrl = "http://www.neopets.com/login.phtml";
	private static String poundUrl = "http://www.neopets.com/pound/adopt.phtml?search=";
	private static String charset = "UTF-8";
	private static String userAgent = "Mozilla/5.0";
	private static String petnames[];
	
	// Connection variables
	private static HttpURLConnection connection;
	private static CookieManager cookieManager;
	
	// Results
	private static String resultsFile = "results.txt";
	

	public static void login() throws IOException {
		// Login once per program duration
		
		// Setup cookie storage (will automatically store cookies and add them to requests)
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
		// Get login page
		String loginPage = sendGetRequest(loginUrl);
		
		// Fill out login form
		String loginCredentials = fillLoginElements(loginPage);
		 
		// Now login
		sendPostRequest(loginCredentials);
	}
	
	public static void searchInPound() throws IOException {
		/*
		 * Searches for each petname in the Pound.
		 *  
		 * IMPORTANT:
		 * The flow of requests is constrained for the safety of the server.
		 * Current settings: Max 50 requests, then wait 5 seconds.
		 */
		System.out.println("Search started...");
		
//		// The snippet below to test if response is correct (testName must be a pet in the pound)
//		String testName = "";
//		String petInPoundUrl = poundUrl + URLEncoder.encode(testName, charset);
//		String poundPage = sendGetRequest(petInPoundUrl);
//		checkPetInPound(poundPage, testName);
//		// Use the above block to test if you're properly returnSing positive results
		
		// Setup RPS limit
		// Note: Server will return 503 if too many requests are made without a break (ex: 50)
		int itr = 0;
		int cap1 = 2;
		int cap2 = 20;
		int cap3 = 100;
		int waitTime1 = 500; // Milliseconds
		int waitTime2 = 20; // Seconds
		int waitTime3 = 1; // Minute
		int qty = petnames.length;
		
		// Comment out this block if using test pet
		while(itr < qty) {
			String petname = petnames[itr];
			String petInPoundUrl = poundUrl + URLEncoder.encode(petname, charset);
			String poundPage = sendGetRequest(petInPoundUrl);
			checkPetInPound(poundPage, petname);
			
			itr++;
			if(itr == qty) { break; }
			if(itr % cap3 == 0) {
				System.out.println("Searched " + itr + " names. Pausing for " + waitTime3 + " minutes...");
				// Note: Thread sleeping in a loop may cause drift, but do we really need precision?
				try {
					TimeUnit.MINUTES.sleep(waitTime3);
				} catch (InterruptedException e) {
					System.out.println("Interrupted while searching petnames.");
				}
			}
			else if(itr % cap2 == 0) {
				System.out.println("Searched " + itr + " names. Pausing for " + waitTime2 + " seconds...");
				// Note: Thread sleeping in a loop may cause drift, but do we really need precision?
				try {
					TimeUnit.SECONDS.sleep(waitTime2);
				} catch (InterruptedException e) {
					System.out.println("Interrupted while searching petnames.");
				}
			}
			else if(itr % cap1 == 0) {
//				System.out.println("Pausing for " + waitTime1 + " microseconds..."); // Commented out to reduce printouts on console
				// Note: Thread sleeping in a loop may cause drift, but do we really need precision?
				try {
					TimeUnit.MILLISECONDS.sleep(waitTime1);
				} catch (InterruptedException e) {
					System.out.println("Interrupted while searching petnames.");
				}
			}
		}
		// Comment out the above block if using test pet
		
		System.out.println("Search finished!");
	}
	
	public static void readCredentials() {
		// Saves login credentials
		
		try {
			File credentials = new File(credentialsFile);
			Scanner reader = new Scanner(credentials);
			
			String user = reader.nextLine();
			username = user;
			
			String pw = reader.nextLine();
			password = pw;
			
			reader.close();
		} catch(FileNotFoundException e) {
			System.out.println(credentialsFile + " not found.");
		}
		
	}
	
	public static String sendGetRequest(String url) throws IOException {
		
		/*
		 * Sends a GET request to the input URL.
		 * Returns the response body (expecting page HTML).
		 * Two uses: fetching login page and fetching Pound page.
		 * 
		 * Using do-while loop to retry in case of a non-200 response.
		 */
		
		int responseCode = 0;
		int waitTime = 1; // Minute
		do {
			
			// Setup GET request
			URL obj = new URL(url);
			connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setRequestProperty("User-Agent", userAgent);
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");

			// Send request and check server response
			responseCode = connection.getResponseCode();
			if(responseCode != 200) {
				System.out.println("Received a bad response - waiting 1 min.");
				try {
					TimeUnit.MINUTES.sleep(waitTime);
				} catch (InterruptedException e) {
					System.out.println("Interrupted while paused after bad response.");
				}
			}
			
		} while(responseCode != 200);
		
		
		
//		// Send request and check server response
//		int responseCode = connection.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);
		

		// Check stored cookies
		// LOGIN CREDENTIALS WILL BE VISIBLE on console
//		List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();
//		System.out.println("prelogin cookies");
//		for(int i = 0; i < cookieList.size(); i++) {
//			System.out.println(cookieList.get(i).getName() + " and " + cookieList.get(i).getValue());
//		}
		
		// Saving response body
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();

	    // Converting response body to string
	    while((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();
	    String rsp = response.toString();
	    
	    // Return response body
	    return rsp;
	}
	
	public static void sendPostRequest(String params) throws IOException {
		/*
		 * Sends a POST request to the login URL (not the login page, the URL requested when you hit "Log In".
		 * Used to get the session ID needed to access the Pound page.
		 */
		
		// Setup POST request
		URL obj = new URL(sendLoginUrl);
		connection = (HttpURLConnection) obj.openConnection();
		connection.setUseCaches(false);
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("User-Agent", userAgent);
	    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
	    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
	    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    connection.setRequestProperty("Connection", "keep-alive");
	    connection.setRequestProperty("Content-Length", Integer.toString(params.length()));
	    connection.setRequestProperty("Referer", "http://www.neopets.com/login/index.phtml");
	    connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
	    connection.setDoOutput(true);
	    connection.setDoInput(true);
	    
	    // Send the credentials to the server
	    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	    wr.writeBytes(params);
	    wr.flush();
	    wr.close();
	    
	    // Send request and check server response
	    // NOTE: 403 is expected but we don't care about the response
	    int responseCode = connection.getResponseCode();
	    System.out.println("\nSending 'POST' request to URL : " + sendLoginUrl);
//	    System.out.println("Post parameters : " + params); // PASSWORD WILL BE VISIBLE on console
	    System.out.println("Response Code : " + responseCode + " (expected: 403)");
	    
	    // Check stored cookies
	 	// LOGIN CREDENTIALS WILL BE VISIBLE on console
// 		List<HttpCookie> cookieList = cookieManager.getCookieStore().getCookies();
// 		for(int i = 0; i < cookieList.size(); i++) {
// 			System.out.println(cookieList.get(i).getName() + " and " + cookieList.get(i).getValue());
// 		}    
	}
	
	public static String fillLoginElements(String loginPage) throws UnsupportedEncodingException {
		/*
		 * Fills out the login fields (username and password) on the input website.
		 * Returns the parameters (values) used to fill them out.
		 * Using Jsoup to parse the HTML.
		 * The output is sent to the output stream in our login POST request.
		 */
		
	    Document doc = Jsoup.parse(loginPage);
	    Element loginform = doc.getElementById("login");
	    Elements inputElements = loginform.getElementsByTag("input"); // Get all <input> elements
	    List<String> paramList = new ArrayList<String>();
	    
	    // Find the elements we want to fill
	    for(Element inputElement : inputElements) {
	        String key = inputElement.attr("name");
	        String value = inputElement.attr("value");

	        if (key.equals("username"))
	            value = username;
	        else if (key.equals("password"))
	            value = password;
	        paramList.add(key + "=" + URLEncoder.encode(value, charset)); // Encoding to convert special chars
	    }
	    
	    // Put parameters (username, password) together
	    StringBuilder result = new StringBuilder();
	    for(String param : paramList) {
	        if(result.length() == 0) {
	            result.append(param);
	        } else {
	            result.append("&" + param);
	        }
	    }
	    String params = result.toString();
	    return params;
	}
	
	public static void checkPetInPound(String poundPage, String petName) {
		/*
		 * Get response from petInPoundUrl
		 * Use Jsoup to check if pet is in Pound
		 * Return true if pet is in Pound
		 */
		
		Document doc = Jsoup.parse(poundPage);
		Element pet = doc.getElementById("pet1_table");
		if(pet != null) {
			// Pet found in pound
			
			try {
				Desktop.getDesktop().browse(new URI(poundUrl + URLEncoder.encode(petName, charset)));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			Element petNameElement = doc.selectFirst("div#pet1_name");
//			String petName = petNameElement.text();
			Element petColorElement = doc.selectFirst("span#pet1_color");
			String petColor = petColorElement.text();
			Element petSpeciesElement = doc.selectFirst("span#pet1_species");
			String petSpecies = petSpeciesElement.text();
			
			// Save pet to resultsFile
			String newline = System.getProperty("line.separator");
			try {
				FileWriter results = new FileWriter(resultsFile, true);
				results.write(petName + ": " + petColor + " " + petSpecies);
				results.write(newline);
				results.close();
			} catch (IOException e) {
				System.out.println("Error when saving results.");
			}
			
			// For debugging
//			System.out.println(petName + ": " + petColor + " " + petSpecies);
			
//			Elements petFeatures = pet.getElementsByTag("span"); // Get all <span> elements
//			List<String> featuresList = new ArrayList<String>();
//			
//			for(Element featureElement : petFeatures) {
//				String key = featureElement.attr("id");
//		        String value = inputElement.attr("value");
//			}
		}
		// For testing
//		else {
//			System.out.println("Pet not found.");
//		}
		
	}
	
	
	// SETTER
	
	public static void setPetnames(String names[]) {
		petnames = names;
	}
}
