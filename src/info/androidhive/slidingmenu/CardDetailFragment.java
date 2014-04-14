package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.model.LowaCard;
import info.androidhive.slidingmenu.JSONParser;
import info.androidhive.slidingmenu.CreateCardFragment.CreateNewProduct;
import info.androidhive.slidingmenu.CreateCardFragment.GetLowaCardWithGPS;
import info.androidhive.slidingmenu.library.SessionManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class CardDetailFragment extends Fragment {
	
	EditText txtName;
	EditText txtPhone;
	EditText txtAddress;
	EditText txtCreatedAt;
	Button btnSave;
	Button btnDelete;

	String pid;
	LowaCard lowaCard;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_product_detials = "http://lowasoft.webfactional.com/api/v1/cards/";

	// url to update product
	private static final String url_update_product = "http://lowasoft.webfactional.com/api/v1/cards/";
	
	// url to delete product
	private static final String url_delete_product = "http://lowasoft.webfactional.com/api/v1/cards/";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "card";
	private static final String TAG_PID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_PRICE = "phone";
	private static final String TAG_DESCRIPTION = "address";
	
	// Session Manager Class
	SessionManager session;
	
	public CardDetailFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_card_detail, container, false);
        pid = this.getArguments().getString("lowaCardId");
        session = new SessionManager(getActivity().getApplicationContext());
        
        Log.d("I get the message hahahahaha", "message:::"+pid);
        lowaCard = new LowaCard();
		// Create button
        txtName = (EditText) rootView.findViewById(R.id.modifyName);
		txtPhone = (EditText) rootView.findViewById(R.id.modifyPhone);
		txtAddress = (EditText) rootView.findViewById(R.id.modifyAddress);
		Button btnSaveCard = (Button) rootView.findViewById(R.id.saveCard);
		Button btnDeleteCard = (Button) rootView.findViewById(R.id.removeCard);
		new GetLowaCardWithGPS().execute();

		// button click event
		btnSaveCard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				//new CreateNewProduct().execute();
				new SaveProductDetails().execute();
			}
		});
		
		
		// button click event
		btnDeleteCard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				//new CreateNewProduct().execute();
				new DeleteProduct().execute();
			}
		});
         
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
			pDialog.setMessage("Loading Card Information. Please wait...");
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
			
			Log.d("testWei", "Url::"+url_product_detials+""+pid);
			
			JSONObject json = jsonParser.makeHttpRequest(url_product_detials+""+pid, "GET", params, true, token);
			
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
				
				Log.d("Card Lowa", "lowcard::"+lowaCard.getAddress());
				txtName.setText(lowaCard.getName());
				txtPhone.setText(lowaCard.getPhone());
				txtAddress.setText(lowaCard.getAddress());
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading product details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					Boolean success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("pid", pid));
						//String token = session.getUserToken();
						String token = session.getUserToken();
						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_product_detials+""+pid, "GET", params, true, token);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getBoolean(TAG_SUCCESS);
						if (success) {
							// get first product object from JSON Array
							JSONObject product = json.getJSONObject(TAG_PRODUCT);
                            
							lowaCard.setName(product.getString(TAG_NAME));
							lowaCard.setPhone(product.getString(TAG_PRICE));
							lowaCard.setAddress(product.getString(TAG_DESCRIPTION));
							// product with this pid found
							// Edit Text
							/*txtName = (EditText) getView().findViewById(R.id.modifyName);
							txtPrice = (EditText) getView().findViewById(R.id.modifyPhone);
							txtDesc = (EditText) getView().findViewById(R.id.modifyAddress);*/

							

						}else{
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
			// display product data in EditText
			txtName.setText(lowaCard.getName());
			txtPhone.setText(lowaCard.getPhone());
			txtAddress.setText(lowaCard.getAddress());
		}
	}

	/**
	 * Background Async Task to  Save product Details
	 * */
	class SaveProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Saving product ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String name = txtName.getText().toString();
			String price = txtPhone.getText().toString();
			String description = txtAddress.getText().toString();
            
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PID, pid));
			params.add(new BasicNameValuePair(TAG_NAME, name));
			params.add(new BasicNameValuePair(TAG_PRICE, price));
			params.add(new BasicNameValuePair(TAG_DESCRIPTION, description));
			//String token = session.getUserToken();
			String token =  session.getUserToken();
			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product+""+pid,
					"PUT", params, true, token);

			// check json success tag
			try {
				Boolean success = json.getBoolean(TAG_SUCCESS);
				
				if (success) {
					Fragment newFragment = new HomeFragment(); 
					FragmentTransaction transaction = getFragmentManager().beginTransaction(); 
					transaction.replace(R.id.frame_container, newFragment);
					transaction.commit();
				} else {
					// failed to update product
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
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Product
	 * */
	class DeleteProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Deleting Product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			Boolean success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("id", pid));
				//String token = session.getUserToken();
				String token = session.getUserToken();
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product+""+pid, "DELETE", params, true, token);

				Log.d("URLS", url_delete_product+""+pid);
				// check your log for json response
				Log.d("Delete Product", json.toString());
				
				// json success tag
				success = json.getBoolean(TAG_SUCCESS);
				if (success) {
					Fragment newFragment = new HomeFragment(); 
					FragmentTransaction transaction = getFragmentManager().beginTransaction(); 
					transaction.replace(R.id.frame_container, newFragment);
					transaction.commit();
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
			// dismiss the dialog once product deleted
			pDialog.dismiss();

		}

	}
}

