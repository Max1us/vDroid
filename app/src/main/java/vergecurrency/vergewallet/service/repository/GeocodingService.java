package vergecurrency.vergewallet.service.repository;

import org.json.JSONException;
import org.json.JSONObject;

public class GeocodingService {

	//TODO : Query should be done here
	//TODO : Make it a static class
	public GeocodingService() {
	}

	public String readCoordinates(String rawData) {


		if (rawData != null && !rawData.equals("")) {
			JSONObject reader;
			try {
				reader = new JSONObject(rawData);
				String lat = reader.getString("latitude");
				String lon = reader.getString("longitude");
				return String.format("%s;%s", lat, lon);
			} catch (JSONException e) {
				e.printStackTrace();
				return "error";
			}
		} else return "error";
	}
}
