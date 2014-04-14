/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.library.SessionManager;
import info.androidhive.slidingmenu.library.UserFunctions;
import info.androidhive.slidingmenu.model.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {
	Button btnRegister;
	Button btnLinkToLogin;
	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword;
	TextView registerErrorMsg;
	
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	// Session Manager Class
	SessionManager session;
	JSONParser jsonParser = new JSONParser();
    User loginUser;
    private static String registerURL = "http://lowasoft.webfactional.com/api/v1/users/register/";
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	boolean inCorrect = true;
	boolean isEmpty = true;
	// Progress Dialog
	private ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		// Session Manager
        session = new SessionManager(getApplicationContext()); 

		// Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		
		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				new LoginUser().execute();
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(i);
				// Close Registration View
				finish();
			}
		});
	}
	
	
	/**
	 * Background Async Task to Create new product
	 * */
	class LoginUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("Creating Product..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			
			Log.d("WeiTest", "I am hehrehehehe");
			String name = inputFullName.getText().toString();
			String email = inputEmail.getText().toString();
			String password = inputPassword.getText().toString();
			UserFunctions userFunction = new UserFunctions();

			if(email.trim().length() > 0 && password.trim().length() > 0){
				isEmpty = false;
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("password", password));
				params.add(new BasicNameValuePair("username", name));
				
				//String token = "ca85d424416a94bc541d6d6c5e28cac2597643ff";
				
				Log.d("username", "username:::"+email+"password::::"+password);
				// getting JSON Object
				// Note that create product url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(registerURL,
						"POST", params, false, "");
				
				// check log cat fro response
				Log.d("Create Response", json.toString());
	
				// check for success tag
				try {
					Boolean success = json.getBoolean("success");
	
					if (success) {
						
						Log.d("here is passed", "passed");
						JSONObject json_user = json.getJSONObject("user");
						String token = json.getString("token");
						String useremail = json_user.getString("email");
						
						loginUser = new User(email, token);
						inCorrect = false;
						
					} else {
						inCorrect = true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			
			if(isEmpty){
				alert.showAlertDialog(RegisterActivity.this, "Registerfailed..", "Please enter username and password", false);
			}else{
				if(inCorrect){
					isEmpty = true;
					alert.showAlertDialog(RegisterActivity.this, "Register failed..", "Email already be taken!", false);
				}else{		
					session.createLoginSession(loginUser.getToken(), loginUser.getEmail());
					Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
					// Close all views before launching Dashboard
					dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(dashboard);
				}
			}
		}

	}
}
