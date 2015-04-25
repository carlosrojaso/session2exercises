package com.example.p13_xml_json_parsers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ParserInterface{

	enum ParsingMethod {
		xml,
		json,
		notSet
	};
	
	private static final String URL1 = "http://api.openweathermap.org/data/2.5/weather?q=";
	private static final String URL2_XML = "&mode=xml";
	private static final String URL2_JSON = "&mode=json";
	
	private EditText search, country, temperature, pressure, humidity;
	private TextView log;

	private XMLParser xmlParser;
	private JSONParser jsonParser;
	
	private ParsingMethod method = ParsingMethod.notSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		search = (EditText) findViewById(R.id.editTextLocation);
		country = (EditText) findViewById(R.id.editTextCountry);
		temperature = (EditText) findViewById(R.id.editTextTemperature);
		pressure = (EditText) findViewById(R.id.editTextPressure);
		humidity = (EditText) findViewById(R.id.editTextHumidity);
		log = (TextView) findViewById(R.id.textViewLog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void parse(View view){
		cleanValues();
		clearLog();
		doUpdate("Iniciando parseo: "+search.getText().toString());
		String urlToSend = URL1 + search.getText().toString();
		switch (view.getId()) {
		case R.id.buttonJson:
			method = ParsingMethod.json;
			urlToSend += URL2_JSON;
			jsonParser = new JSONParser(this, urlToSend);
			jsonParser.fetchJSON();
			break;
		case R.id.buttonXml:
			method = ParsingMethod.xml;
			urlToSend += URL2_XML;
			xmlParser = new XMLParser(this, urlToSend);
			xmlParser.fetchXML();
			break;
		default:
			return;
		}
	}
	
	public void updateData(){
		switch(method){
		case xml:
			country.setText(xmlParser.getCountry());
			temperature.setText(xmlParser.getTemperature());
			humidity.setText(xmlParser.getHumidity());
			pressure.setText(xmlParser.getPressure());
			break;
		case json:
			country.setText(jsonParser.getCountry());
			temperature.setText(jsonParser.getTemperature());
			humidity.setText(jsonParser.getHumidity());
			pressure.setText(jsonParser.getPressure());
			break;
		case notSet: default:
			cleanValues();
			break;
		}
	}
	
	public void cleanValues(){
		country.setText("");
		temperature.setText("");
		humidity.setText("");
		pressure.setText("");
	}

	@Override
	public void finished(boolean success) {
		if(success){
			updateData();
		}else{
			cleanValues();
			Toast.makeText(this, "Error en el parseo.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void doUpdate(String status) {
		log.append(status+"\n");
	}
	
	public void clearLog(){
		log.setText("");
	}

}