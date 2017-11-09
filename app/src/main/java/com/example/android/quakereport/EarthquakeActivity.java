/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
//import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        EarthquakeAsyncTask EarthquakeEvent = new EarthquakeAsyncTask();
        EarthquakeEvent.execute(USGS_REQUEST_URL);


    }


    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        String jsonResponse ;

        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            // Create URL object
            URL url = createUrl(USGS_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back

            try {
                jsonResponse = makeHttpRequest(url);
                Log.i(LOG_TAG ,jsonResponse );
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Earthquake} object
            ArrayList<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);
            return earthquakes;
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
            if (earthquakes == null) {
                return;
            }

            updateUi(earthquakes);
        }

        private void updateUi(ArrayList<Earthquake> earthquakes){
            EarthquakeAdapter earthquakeadapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
            ListView earthquakeListView = (ListView) findViewById(R.id.list);
            earthquakeListView.setAdapter(earthquakeadapter);
            earthquakeListView.setAdapter(earthquakeadapter);
        }
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if(url == null){
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if(urlConnection.getResponseCode() == 200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                //Log.i(LOG_TAG ,Integer.toString(urlConnection.getResponseCode()));
                //Log.i(LOG_TAG ,jsonResponse);

            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
        private ArrayList<Earthquake> extractFeatureFromJson(String earthquakeJSON) {
            ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
            Log.i(LOG_TAG ,earthquakeJSON);
            if (earthquakeJSON == null)
                return null;
            try {
                // If there are results in the features array

                    // Extract out the first feature (which is an earthquake)
                    JSONObject jreader = new JSONObject(earthquakeJSON);
                    if (jreader.length() > 0) {
                    JSONArray jarray =  jreader.getJSONArray("features");;
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jsonObject = jarray.getJSONObject(i);
                        JSONObject jproperites = jsonObject.getJSONObject("properties");
                        String quakeplace_i = jproperites.getString("place");
                        double quakemag_i = Double.parseDouble(jproperites.getString("mag"));
                        long quaketime_i = Long.parseLong(jproperites.getString("time"));
                        earthquakes.add(new Earthquake(quakeplace_i, quakemag_i, quaketime_i));
                    }
                    // Create a new {@link Earthquake} object

                }
            } catch (JSONException e) {

                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            }
            // Return the list of earthquakes
            return earthquakes;
        }

    }
}
