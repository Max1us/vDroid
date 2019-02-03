package vergecurrency.vergewallet.views.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vergecurrency.vergewallet.R;
import vergecurrency.vergewallet.views.activities.settings.DisconnectActivity;
import vergecurrency.vergewallet.views.activities.settings.DonateActivity;
import vergecurrency.vergewallet.views.activities.settings.PaperkeyActivity;
import vergecurrency.vergewallet.views.activities.settings.SelectCurrencyActivity;
import vergecurrency.vergewallet.views.activities.settings.TorSettingsActivity;
import vergecurrency.vergewallet.views.fragments.beans.HeaderData;
import vergecurrency.vergewallet.views.fragments.beans.ItemData;
import vergecurrency.vergewallet.views.adapters.SettingsListsAdapter;

public class FragmentSettings extends Fragment {


	public FragmentSettings() {
	}

	public void fillRecyclerView(View rootView, int recyclerViewID, HeaderData headerData, ItemData[] itemsData) {
		//Get the RecyclerView
		RecyclerView recView = (RecyclerView) rootView.findViewById(recyclerViewID);

		recView.setLayoutManager(new LinearLayoutManager(getContext()));
		//Sets the adapter to the RecyclerView
		recView.setAdapter(new SettingsListsAdapter(headerData, itemsData));
		recView.addItemDecoration(new DividerItemDecoration(recView.getContext(), DividerItemDecoration.VERTICAL));


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

		//Generate the data for the RecyclerView
		ItemData itemsDataWallet[] = {
				new ItemData("Disconnect this device", R.drawable.icon_disconnected, v -> startActivity(new Intent(v.getContext(), DisconnectActivity.class))),
				new ItemData("Paperkey", R.drawable.icon_paperkey, v -> {
					startActivity(new Intent(v.getContext(), PaperkeyActivity.class));
				}),
		};
		ItemData itemsDataSettings[] = {
				new ItemData("Fiat currency", R.drawable.icon_currency_exchange, v -> startActivity(new Intent(v.getContext(), SelectCurrencyActivity.class))),
				new ItemData("Change wallet PIN", R.drawable.icon_pin, null),
				new ItemData("Use fingerprint", R.drawable.icon_fingerprint, null),
				new ItemData("Tor connection", R.drawable.icon_onion, v -> startActivity(new Intent(v.getContext(), TorSettingsActivity.class)))
		};

		ItemData itemsDataOther[] = {
				new ItemData("Credits", R.drawable.icon_credits, null),
				new ItemData("Donate", R.drawable.icon_donate, v -> startActivity(new Intent(v.getContext(), DonateActivity.class))),
				new ItemData("Rate app", R.drawable.icon_star, null),
				new ItemData("Website", R.drawable.icon_web, v -> {
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("https://www.vergecurrency.com"));
					startActivity(intent);
				}),
				new ItemData("Contribute", R.drawable.icon_github, v -> {
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("https://www.github.com/vergecurrency/vDroid"));
					startActivity(intent);
				})
		};


		//populate the recyclerviews with their data
		fillRecyclerView(rootView, R.id.settings_list_wallet, new HeaderData("WALLET"), itemsDataWallet);
		fillRecyclerView(rootView, R.id.settings_list_settings, new HeaderData("SETTINGS"), itemsDataSettings);
		fillRecyclerView(rootView, R.id.settings_list_other, new HeaderData("OTHER"), itemsDataOther);

		return rootView;
	}
}