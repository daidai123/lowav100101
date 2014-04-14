/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package info.androidhive.slidingmenu.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import info.androidhive.slidingmenu.*;
import android.content.Context;
import android.util.Log;

public class UserFunctions {
	
	private JSONParser jsonParser;
	
	private static String loginURL = "http://lowasoft.webfactional.com/api/v1/users/login/";
	private static String registerURL = "http://lowasoft.webfactional.com/api/v1/users/register/";
	
	private static String login_tag = "login";
	private static String register_tag = "register";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("username", email));
		params.add(new BasicNameValuePair("password", password));
		Log.d("Login here", "what is goint on?");
		JSONObject json = jsonParser.makeHttpRequest(loginURL, "POST", params, false, "");
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	
	/**
	 * function make Login Request
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String name, String email, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("username", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		
		// getting JSON Object
		JSONObject json = jsonParser.makeHttpRequest(registerURL, "POST",params, false, "");
		// return json
		return json;
	}
	
	/**
	 * Function get Login status
	 * */
	/*public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}*/
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	/*public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}*/
	
}
