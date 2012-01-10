package com.blork.safetrip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.blork.safetrip.util.Debug;
import com.blork.safetrip.util.Map;
import com.blork.safetrip.util.Network;
import com.google.android.maps.GeoPoint;

public class SafeTripActivity extends FragmentActivity {



	public static final String SERVICE = "http://rezippy.com/safe/route.php";
	public static List<Route> routes = new ArrayList<Route>();
	private LocationManager locationManager;
	private Button findRoute;
	private ImageButton geoLocate;
	private ProgressBar geoProgress;
	private ImageButton geoDelete;
	private EditText originEditText;
	private EditText destinationEditText;
	private LocationListener locationListener;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findRoute = (Button)findViewById(R.id.buttonFindRoute);
		geoLocate = (ImageButton)findViewById(R.id.buttonGeolocate);
		geoProgress = (ProgressBar)findViewById(R.id.progressGeo);
		geoDelete = (ImageButton)findViewById(R.id.buttonRemoveGeolocate);

		originEditText = (EditText)findViewById(R.id.editTextOrigin);
		destinationEditText = (EditText)findViewById(R.id.editTextDestination);

		if (savedInstanceState != null) {
			String origin = savedInstanceState.getString("origin");
			if (origin != null) originEditText.setText(origin);

			String destination = savedInstanceState.getString("destination");
			if (destination != null) destinationEditText.setText(destination);
		}

		findRoute.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Network.isNetworkConnected(SafeTripActivity.this)) {
					new GetRouteTask(
							originEditText.getText().toString(), 
							destinationEditText.getText().toString()
							).execute();
				} else {
					showErrorConnectionDialog();
				}
			}

		});

		geoDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				geoProgress.setVisibility(View.GONE);
				geoLocate.setVisibility(View.VISIBLE);
				geoDelete.setVisibility(View.GONE);
				originEditText.setEnabled(true);
				originEditText.setText("");
			}
		});

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		geoLocate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				geoProgress.setVisibility(View.VISIBLE);
				geoLocate.setVisibility(View.GONE);
				//originEditText.setTextColor(Color.GREEN);
				originEditText.setEnabled(false);

				// Acquire a reference to the system Location Manager
				// Define a listener that responds to location updates
				locationListener = new LocationListener() {
					public void onLocationChanged(Location location) {
						new GeoLocateTask(location).execute();
					}

					public void onStatusChanged(String provider, int status, Bundle extras) {}

					public void onProviderEnabled(String provider) {}

					public void onProviderDisabled(String provider) {}
				};		

				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);        
				criteria.setCostAllowed(true);
				criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

				String bestProvider = locationManager.getBestProvider(criteria, true);
				locationManager.requestLocationUpdates(
						bestProvider != null ? bestProvider : LocationManager.NETWORK_PROVIDER, 0, 0, locationListener
						);
			}
		});
	}


	private void showErrorConnectionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.no_internet_title)
		.setMessage(getString(R.string.no_internet_text))
		.setCancelable(false)
		.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}


	private class GetRouteTask extends AsyncTask<Void, Integer, Boolean> {
		private String origin;
		private String destination;
		private ProgressDialog dialog;
		private double oLat;
		private double oLng;
		private double dLng;
		private double dLat;

		public GetRouteTask(String origin, String destination) {
			this.origin = origin;
			this.destination = destination;
		}

		protected void onPreExecute() {
			dialog = ProgressDialog.show(SafeTripActivity.this, "", "Calculating route. Please wait...", true);
			routes.clear();
		}

		protected Boolean doInBackground(Void... nothing) {

			Geocoder geoCoder = new Geocoder(SafeTripActivity.this, Locale.getDefault());

			try {
				List<Address> addresses;
				addresses = geoCoder.getFromLocationName(origin, 1);
				if (addresses.size() > 0) {
					GeoPoint originGeoPoint = new GeoPoint(
							(int) (addresses.get(0).getLatitude() * 1E6), 
							(int) (addresses.get(0).getLongitude() * 1E6));
					oLat = (double)originGeoPoint.getLatitudeE6()/1E6;
					oLng = (double)originGeoPoint.getLongitudeE6()/1E6;
				} else {
					return false;
				}

				addresses = geoCoder.getFromLocationName(destination, 1);
				if (addresses.size() > 0) {
					GeoPoint originGeoPoint = new GeoPoint(
							(int) (addresses.get(0).getLatitude() * 1E6), 
							(int) (addresses.get(0).getLongitude() * 1E6));
					dLat = (double)originGeoPoint.getLatitudeE6()/1E6;
					dLng = (double)originGeoPoint.getLongitudeE6()/1E6;
				} else {
					return false;
				}   
			} catch (Exception e) {
				Debug.log("No route?", e);
				return false;
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(SERVICE);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("originLat", Double.toString(oLat)));
				nameValuePairs.add(new BasicNameValuePair("originLon", Double.toString(oLng)));
				nameValuePairs.add(new BasicNameValuePair("destinationLat", Double.toString(dLat)));
				nameValuePairs.add(new BasicNameValuePair("destinationLon", Double.toString(dLng)));
				nameValuePairs.add(new BasicNameValuePair("lang", Locale.getDefault().getLanguage()));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				String responseString = Network.streamToString(response.getEntity().getContent());

				JSONTokener tokener = new JSONTokener(responseString);
				JSONObject json = (JSONObject) tokener.nextValue();

				boolean success = json.getString("status").equals("OK") ? true : false;

				if (success) {
					JSONArray jRoutes = json.getJSONArray("routes");
					Debug.log(jRoutes.length() + " routes");
					for (int i = 0; i < jRoutes.length(); i++) {
						JSONObject jRoute = jRoutes.getJSONObject(i);
						JSONArray jLegs = jRoute.getJSONArray("legs");

						ArrayList<Step> steps = new ArrayList<Step>();

						for (int j = 0; j < jLegs.length(); j++) {
							JSONObject jLeg = jLegs.getJSONObject(j);
							JSONArray jSteps = jLeg.getJSONArray("steps");

							for (int k = 0; k < jSteps.length(); k++) {  
								JSONObject step = jSteps.getJSONObject(k);

								String html_instructions = step.getString("html_instructions");

								String polyline = step.getJSONObject("polyline").getString("points");

								steps.add(new Step(Map.decodePoly(polyline), html_instructions));
							}
						}
						int safeScore;
						try {
							safeScore = jRoute.getInt("safescore");
						} catch (JSONException e) {
							Debug.log("No safescore found", e);
							safeScore = 0;
						}
						routes.add(new Route("name", "desc", safeScore, steps));
					}
				}

			} catch (ClientProtocolException e) {
				Debug.log("Exception 1", e);
				return false;
			} catch (IOException e) {
				Debug.log("Exception 2", e);
				return false;
			} catch (JSONException e) {
				Debug.log("Exception 3", e);
				return false;
			}

			return true;
		}

		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (result) {
				Intent mapIntent = new Intent(getApplicationContext(), DirectionListActivity.class);			
				startActivity(mapIntent);
			} else {
				dialog.dismiss();

				AlertDialog.Builder builder = new AlertDialog.Builder(SafeTripActivity.this);
				builder.setTitle("Can't find a route.")
				.setCancelable(false)
				.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				this.cancel(true);
			}
		}

	}


	private class GeoLocateTask extends AsyncTask<Void, Integer, String> {

		private Location location;

		public GeoLocateTask(Location location) {
			this.location = location;
		}

		protected String doInBackground(Void... nothing) {			
			Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
			String addressText = "";
			try {
				Address address = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
					addressText += address.getAddressLine(i) + ", ";
				} 
			} catch (Exception e) {
				addressText = location.getLatitude() + ", " + location.getLongitude();
			}

			return addressText;
		}

		protected void onPostExecute(String text) {
			originEditText.setText(text);
			geoProgress.setVisibility(View.GONE);
			geoLocate.setVisibility(View.GONE);
			geoDelete.setVisibility(View.VISIBLE);
			locationManager.removeUpdates(locationListener);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("origin", originEditText.getText().toString());
		outState.putString("destination", destinationEditText.getText().toString());
		super.onSaveInstanceState(outState);
	}

}