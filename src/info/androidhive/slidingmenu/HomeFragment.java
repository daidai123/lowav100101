package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import info.androidhive.slidingmenu.CreateCardFragment.CreateNewProduct;
import info.androidhive.slidingmenu.library.SessionManager;
import info.androidhive.slidingmenu.model.MyCard;
import info.androidhive.slidingmenu.model.MyImageCard;
import info.androidhive.slidingmenu.model.MyPlayCard;
import info.androidhive.slidingmenu.model.LowaCard;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class HomeFragment extends Fragment {
	
	private CardUI mCardView;
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<LowaCard> productsList;

	// url to get all products list
	private static String url_all_products = "http://lowasoft.webfactional.com/api/v1/cards/";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "cards";
	private static final String TAG_PID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_ADDRESS = "address";

	// products JSONArray
	JSONArray products = null;
	// Session Manager Class
	SessionManager session;
	boolean isSuccess = true;
	
	
	public HomeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
           View rootView = inflater.inflate(R.layout.fragment_home, container, false);
           productsList = new ArrayList<LowaCard>();
           session = new SessionManager(getActivity().getApplicationContext()); 
        
           // init CardView
     		mCardView = (CardUI) rootView.findViewById(R.id.cardsview);
     		mCardView.setSwipeable(true);

     		CardStack stackPlay = new CardStack();
     		stackPlay.setTitle("My Location Cards");
     		mCardView.addStack(stackPlay);
     		// draw cards
     		//mCardView.refresh();
     		
     	// Loading products in Background Thread
    		new LoadAllProducts().execute();
         
        return rootView;
    }
	
	
	

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading products. Please wait...");
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
			// getting JSON string from URL
			String token = session.getUserToken();
			//String token = "ca85d424416a94bc541d6d6c5e28cac2597643ff";
			
			JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params, true, token);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				Boolean success = json.getBoolean(TAG_SUCCESS);

				if (success) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_PID);
						String name = c.getString(TAG_NAME);
                        String phone = c.getString(TAG_PHONE);
                        String address = c.getString(TAG_ADDRESS);
                        
                        LowaCard lowaCard = new LowaCard(id, name, phone, address);
						
						Log.d("test log here", "name is"+name);
						// adding HashList to ArrayList
						productsList.add(lowaCard);
						
					}
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
			
			if(!isSuccess){
				// no products found
				// Launch Add New product Activity
				Fragment newFragment = new CreateCardFragment(); 
				FragmentTransaction transaction = getFragmentManager().beginTransaction(); 
				transaction.replace(R.id.frame_container, newFragment);
				transaction.commit();
			}else{
			
			
			// updating UI from Background Thread
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					
					String[] colorBD = new String[4];
					colorBD[0] = "#4ac925";
					colorBD[1] = "#e00707";
					colorBD[2] = "#33b6ea";
					colorBD[3] = "#f2a400";
					
					String[] word = new String[4];
					word[0] = "#222222";
					word[1] = "#e00707";
					word[2] = "#33b6ea";
					word[3] = "#9d36d0";
					
					int i = 0;
					//do something
					for(final LowaCard item :productsList){
						
						int k = i%4;
						i++;
						
						MyPlayCard newCard = new MyPlayCard(
								item.getName(),
								item.getPhone(),
								item.getAddress(),
								colorBD[k], word[k], true, true);
						if (k == 0){
							CardStack stack2 = new CardStack();
							stack2.setTitle("Lowa Card");
							mCardView.addStack(stack2);
						    mCardView.addCard(newCard);
						}else{
							mCardView.addCardToLastStack(newCard);
						}
						
						newCard.setOnCardSwipedListener(new Card.OnCardSwiped() {

							@Override
							public void onCardSwiped(Card card, View layout) {

								 Log.d("Test Swip", "Swpi click");
							}
						});
						
						newCard.setOnClickListener(new OnClickListener() {
							 
				            @Override
				            public void onClick(View v) {
				               int id = item.getId();
				               Log.d("Test Click", "Id is:::"+id);
				               Bundle bundle = new Bundle();
				               bundle.putString("lowaCardId", String.valueOf(id));
				               Fragment newFragment = new CardDetailFragment();
				               newFragment.setArguments(bundle);
							   FragmentTransaction transaction = getFragmentManager().beginTransaction(); 
							   transaction.replace(R.id.frame_container, newFragment);
							   transaction.commit();
				 
				            }
				        });
					}
				}
			});
			
			mCardView.refresh();
		   }
		}

	}
}
