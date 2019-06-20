package vergecurrency.vergewallet.service.model.network.layers;

import android.os.AsyncTask;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ClearnetGateway extends AsyncTask<String, Void, String> {


	public ClearnetGateway() {
	}


	@Override
	protected String doInBackground(String... url) {
		try {


			//TODO : CHECK INTERNET CONNECTION YOU IDIOT
			HttpGet httppost = new HttpGet(url[0]);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpResponse response = httpClient.execute(httppost);

			// StatusLine stat = response.getStatusLine();
			int status = response.getStatusLine().getStatusCode();

			if (status == 200) {
				HttpEntity entity = response.getEntity();


				return EntityUtils.toString(entity);
			} else return "error";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
