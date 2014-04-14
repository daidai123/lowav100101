package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.library.SessionManager;
import info.androidhive.slidingmenu.model.LowaCard;
import info.androidhive.slidingmenu.model.User;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ProfileFragment extends Fragment {
	
	SessionManager session;
	JSONParser jsonParser = new JSONParser();
    User loginUser;
 // Progress Dialog
 	private ProgressDialog pDialog;

 	// Creating JSON Parser object
 	JSONParser jParser = new JSONParser();

 	ArrayList<LowaCard> productsList;
 	
 	HashMap<String, String> tempHolder;
 	
 	boolean isSuccess = false;
 	AlertDialogManager alert = new AlertDialogManager();

 	// url to get all products list
 	private static String url_get_user_profile = "http://lowasoft.webfactional.com/api/v1/profile/";

 	// JSON Node names
 	private static final String TAG_SUCCESS = "success";
 	
 	EditText userFullName;
	EditText userGender;
	EditText userEmail;
	EditText userHomePhone;
	EditText userOfficePhone;
	EditText userOfficeAddress;
	EditText userHomeAddress;
	
	
	public ProfileFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        session = new SessionManager(getActivity().getApplicationContext()); 
        userFullName = (EditText) rootView.findViewById(R.id.userFullName);
		userGender = (EditText) rootView.findViewById(R.id.userGender);
		userEmail = (EditText) rootView.findViewById(R.id.userEmail);
		userHomePhone = (EditText) rootView.findViewById(R.id.userHomePhone);
		userOfficePhone = (EditText) rootView.findViewById(R.id.userOfficePhone);
		userOfficeAddress = (EditText) rootView.findViewById(R.id.userOfficeAddress);
		userHomeAddress = (EditText) rootView.findViewById(R.id.userHomeAddress);
        
		new GetLowaCardWithGPS().execute();
		
        return rootView;
    }
	
	
	class GetLowaCardWithGPS extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading Profile Information. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
			//params.add(new BasicNameValuePair("ll", lalng));
			
			String token = session.getUserToken();
			
			Log.d("testWei", "Url::"+url_get_user_profile);
			
			JSONObject json = jsonParser.makeHttpRequest(url_get_user_profile, "GET", params, true, token);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				Boolean success = json.getBoolean(TAG_SUCCESS);
				if (success) {			
					JSONObject tempProduct = json.getJSONObject("user");
					isSuccess = true;
					tempHolder = new HashMap<String, String>();
					tempHolder.put("full_name", tempProduct.getString("full_name"));
					tempHolder.put("home_address", tempProduct.getString("home_address"));
					tempHolder.put("office_address", tempProduct.getString("office_address"));
					tempHolder.put("gender", tempProduct.getString("gender"));
					tempHolder.put("office_phone", tempProduct.getString("office_phone"));
					tempHolder.put("home_phone", tempProduct.getString("home_phone"));
					tempHolder.put("email", tempProduct.getString("email"));
					
					
				} else {
					isSuccess = false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
		
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			if(isSuccess){
				
				if(tempHolder.get("full_name") != "null")
				    userFullName.setText(tempHolder.get("full_name"));
				
				if(tempHolder.get("gender") != "null")
				    userGender.setText(tempHolder.get("gender"));
				userEmail.setText(tempHolder.get("email"));
				
				if(tempHolder.get("home_phone") != "null")
				    userHomePhone.setText(tempHolder.get("home_phone"));
				
				if(tempHolder.get("office_phone") != "null")
				    userOfficePhone.setText(tempHolder.get("office_phone"));
				
				if(tempHolder.get("office_address") != "null")
				   userOfficeAddress.setText(tempHolder.get("office_address"));
				if(tempHolder.get("home_address") != "null")
				   userHomeAddress.setText(tempHolder.get("home_address"));
				
			}else{
				alert.showAlertDialog(getActivity(), "Profile data..", "Cannot get your profile this time, please try later!", false);
			}
		}

	}
}
