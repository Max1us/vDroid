package vergecurrency.vergewallet.view.ui.activity.firstlaunch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import vergecurrency.vergewallet.R;
import vergecurrency.vergewallet.view.ui.activity.base.VergeActivity;

public class FirstLaunchActivity extends VergeActivity {

	Button bottomButton;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_launch);

		instantiateButtons();
	}

	private void instantiateButtons() {

		//create "Create new wallet" button and its and listener
		bottomButton = findViewById(R.id.button_create_wallet);
		bottomButton.setText(HtmlCompat.fromHtml(getResources().getString(R.string.first_launch_new_wallet_button), HtmlCompat.FROM_HTML_MODE_LEGACY), Button.BufferType.SPANNABLE);


		bottomButton.setOnClickListener(newWalletOnClickListener());

		//create "Restore wallet"  button and its and listener.
		bottomButton = findViewById(R.id.button_restore_wallet);
		bottomButton.setText(HtmlCompat.fromHtml(getResources().getString(R.string.first_launch_restore_wallet_button), HtmlCompat.FROM_HTML_MODE_LEGACY), Button.BufferType.SPANNABLE);
		bottomButton.setOnClickListener(restoreWalletOnClickListener());
	}


	private Button.OnClickListener newWalletOnClickListener() {
		return v -> {
			finish();
			startActivity(new Intent(this, PaperkeyInstructionsActivity.class));
		};
	}

	private Button.OnClickListener restoreWalletOnClickListener() {
		return v -> {
			//HERE TOO YOU ASSHOLE OF A DEV
		};
	}



}
