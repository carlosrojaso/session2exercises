package com.example.p13_xml_json_parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class JSONParser {
	
	private String country = "country";
	private String temperature = "temperature";
	private String humidity = "humidity";
	private String pressure = "pressure";
	
	private String urlString = null;
	private ParserInterface activityInterface = null;

	public JSONParser(Context activity, String urlString) {
		super();
		this.activityInterface = (ParserInterface) activity;
		this.urlString = urlString;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getPressure() {
		return pressure;
	}

	public void setPressure(String pressure) {
		this.pressure = pressure;
	}
	
	public void fetchJSON(){
		AsyncTask<String, String, Boolean> async = new AsyncTask<String, String, Boolean>() {
			private boolean doTheParse(String text){
				try {
					JSONObject object = new JSONObject(text);
					JSONObject objectSys = object.getJSONObject("sys");
					
					country = objectSys.getString("country");
					
					JSONObject objectMain = object.getJSONObject("main");
					
					temperature = objectMain.get("temp").toString();
					pressure = objectMain.get("pressure").toString();
					humidity = objectMain.get("humidity").toString();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				
				return true;
			}
			
			String convertStreamToString(InputStream is) {
				// El regex \A es el inicio del documento, lo que indica que lee todo
				
				Scanner s = new Scanner(is);
				s.useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";

				// StringWriter writer = new StringWriter();
				// IOUtils.copy(is, writer, encoding);
				// String theString = writer.toString();
				// return theString;
			}
			
			@Override
			protected Boolean doInBackground(String... arg0) {
				try {

					// La url y la conexión
					URL url = new URL(arg0[0]);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					
					publishProgress("URL creada");
					
					// Las características de la conexión
					conn.setConnectTimeout(15*1000);
					conn.setDoInput(true);
					conn.setReadTimeout(15*1000);
					conn.setRequestMethod("GET");

					// Conectamos
					conn.connect();

					publishProgress("Conexión activa");
					
					InputStream streamIn = conn.getInputStream();
					
					publishProgress("Convirtiendo a String");

					String data = convertStreamToString(streamIn);

					publishProgress("Empezando el parseo");
					
					boolean result = doTheParse(data);
					// Cerrar el lector
					streamIn.close();
					
					publishProgress("Parseo completado");

					return result;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onProgressUpdate(String... values) {
				activityInterface.doUpdate(values[0]);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				activityInterface.finished(result);
			}
		};
		
		async.execute(urlString);
	}
}
