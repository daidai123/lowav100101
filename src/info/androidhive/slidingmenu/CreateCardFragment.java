package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.GPSTracker;
import info.androidhive.slidingmenu.JSONParser;
import info.androidhive.slidingmenu.library.SessionManager;
import info.androidhive.slidingmenu.model.LowaCard;
import info.androidhive.slidingmenu.model.MyCard;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateCardFragment extends Fragment {
	
	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	EditText inputName;
	EditText inputPhone;
	EditText inputAddress;

	private static String url_create_product = "http://lowasoft.webfactional.com/api/v1/cards/";
	// url to create new product
	private static String url_retrieve_card_url = "http://lowasoft.webfactional.com/api/v1/cards/auto/";
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	// Session Manager Class
	SessionManager session;
	// GPSTracker class
	GPSTracker gps;
	LowaCard lowaCard;
	
	public CreateCardFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		session = new SessionManager(getActivity().getApplicationContext()); 
 
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
        
        lowaCard = new LowaCard();
		// Create button
        inputName = (EditText) rootView.findViewById(R.id.editName);
		inputPhone = (EditText) rootView.findViewById(R.id.editPhone);
		inputAddress = (EditText) rootView.findViewById(R.id.editAddress);
		Button btnCreateProduct = (Button) rootView.findViewById(R.id.createCard);
		new GetLowaCardWithGPS().execute();

		// button click event
		btnCreateProduct.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				new CreateNewProduct().execute();
			}
		});
         
        return rootView;
    }
	
	
	
	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class GetLowaCardWithGPS extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading Card form GPS. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			 gps = new GPSTracker(getActivity());
			 LocationManager service = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
		     Criteria criteria = new Criteria();
		     String provider = service.getBestProvider(criteria, false);
		     Location location = service.getLastKnownLocation(provider);
		     double latitude = location.getLatitude();
		     double longitude =location.getLongitude();
		     
		    String lalng = String.valueOf(latitude)+","+String.valueOf(longitude); 
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ll", lalng));
			
			String token = session.getUserToken();
			Log.d("Seesion::", "See::"+token);
			
			JSONObject json = jsonParser.makeHttpRequest(url_retrieve_card_url, "GET", params, true, token);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				Boolean success = json.getBoolean(TAG_SUCCESS);
				if (success) {			
					JSONObject tempProduct = json.getJSONObject("card");
					lowaCard.setName(tempProduct.getString("name"));
					lowaCard.setPhone(tempProduct.getString("phone"));
					lowaCard.setAddress(tempProduct.getString("address"));
				} else {
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
			try{
				inputName.setText(lowaCard.getName());
				inputPhone.setText(lowaCard.getPhone());
				inputAddress.setText(lowaCard.getAddress());
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
	

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
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
			String name = inputName.getText().toString();
			String price = inputPhone.getText().toString();
			String description = inputAddress.getText().toString();


			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("phone", price));
			params.add(new BasicNameValuePair("address", description));
			String token = session.getUserToken();
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params, true, token);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				Boolean success = json.getBoolean(TAG_SUCCESS);

				if (success) {
					
					Fragment newFragment = new HomeFragment(); 
					FragmentTransaction transaction = getFragmentManager().beginTransaction(); 
					transaction.replace(R.id.frame_container, newFragment);
					transaction.commit();
					
				} else {
					// failed to create product
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
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
}
