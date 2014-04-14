/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import info.androidhive.slidingmenu.library.UserFunctions;
import info.androidhive.slidingmenu.library.SessionManager;
import info.androidhive.slidingmenu.model.User;

public class LoginActivity extends Activity {
	Button btnLogin;
	Button btnLinkToRegister;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;
	JSONParser jsonParser = new JSONParser();
	private static String loginURL = "http://lowasoft.webfactional.com/api/v1/users/login/";
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	
    User loginUser;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Session Manager Class
	SessionManager session;
	boolean inCorrect = true;
    boolean isEmpty = true;

    @Override
    public void onBackPressed() {
        Log.d("Mylog", "Existing app");
        this.moveTaskToBack(true);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// Session Manager
        session = new SessionManager(getApplicationContext()); 

		// Importing all assets like buttons, text fields
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				new LoginUser().execute();
			}
		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
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
			pDialog = new ProgressDialog(LoginActivity.this);
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
			String email = inputEmail.getText().toString();
			String password = inputPassword.getText().toString();

			if(email.trim().length() > 0 && password.trim().length() > 0){
				isEmpty = false;
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", email));
				params.add(new BasicNameValuePair("password", password));
				//String token = "ca85d424416a94bc541d6d6c5e28cac2597643ff";
				
				Log.d("username", "username:::"+email+"password::::"+password);
				// getting JSON Object
				// Note that create product url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(loginURL,
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
				alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
			}else{
				if(inCorrect){
					isEmpty = true;
					alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
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
