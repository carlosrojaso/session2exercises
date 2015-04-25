package com.example.p13_xml_json_parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.AsyncTask;

public class XMLParser {

	private XmlPullParserFactory xmlFactory;
	
	private String country = "country";
	private String temperature = "temperature";
	private String humidity = "humidity";
	private String pressure = "pressure";
	
	private String urlString = null;
	private ParserInterface activityInterface = null;

	public XMLParser(Context activity, String urlString) {
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
	
	public void fetchXML(){
		AsyncTask<String, String, Boolean> async = new AsyncTask<String, String, Boolean>() {
			private boolean doTheParse(XmlPullParser parser){
				int event;
				String text = null;
				try {
					event = parser.getEventType();
					while(event != XmlPullParser.END_DOCUMENT){
						String name = parser.getName();

						publishProgress("Leyendo: "+name);
						
						switch (event) {
						case XmlPullParser.TEXT:
							text = parser.getText();
							break;
						case XmlPullParser.END_TAG:
							switch (name) {
							case "country":
								country = text;
								break;
							case "humidity":
								humidity = parser.getAttributeValue(null, "value");
								break;
							case "temperature":
								temperature = parser.getAttributeValue(null, "value");
								break;
							case "pressure":
								pressure = parser.getAttributeValue(null, "value");
								break;
							}
							break;
						}
						event = parser.next();
						//See-read updates in-screen
						Thread.sleep(50);
					}
				} catch (XmlPullParserException | IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				return true;
			}
			
			@Override
			protected Boolean doInBackground(String... arg0) {
				try {

					// Url y conexión
					URL url = new URL(arg0[0]);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					
					publishProgress("URL creada");
					
					// Parámetros de conexión
					conn.setConnectTimeout(15*1000);
					conn.setDoInput(true);
					conn.setReadTimeout(15*1000);
					conn.setRequestMethod("GET");
					conn.connect();

					publishProgress("Conexión activa");
					
					
					InputStream streamIn = conn.getInputStream();
					xmlFactory = XmlPullParserFactory.newInstance();
					
					XmlPullParser myParser = xmlFactory.newPullParser();
					myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
					myParser.setInput(streamIn,"UTF-8");
					
					publishProgress("Empezando el parseo");
					
					boolean result = doTheParse(myParser);
					streamIn.close();
					
					publishProgress("Parseo completado");

					return result;
					
				} catch (IOException | XmlPullParserException e) {
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
