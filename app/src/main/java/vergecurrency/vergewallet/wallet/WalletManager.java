package vergecurrency.vergewallet.wallet;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import io.horizontalsystems.bitcoinkit.BitcoinKit;
import io.horizontalsystems.bitcoinkit.models.BlockInfo;
import io.horizontalsystems.bitcoinkit.models.TransactionInfo;
import vergecurrency.vergewallet.service.model.MnemonicManager;
import vergecurrency.vergewallet.service.model.PreferencesManager;

import static io.horizontalsystems.bitcoinkit.BitcoinKit.KitState;
import static io.horizontalsystems.bitcoinkit.BitcoinKit.Listener;
import static io.horizontalsystems.bitcoinkit.BitcoinKit.NetworkType;
import static vergecurrency.vergewallet.service.model.PreferencesManager.setMnemonic;

public final class WalletManager implements Listener {

	private static WalletManager INSTANCE = null;
	private static MutableLiveData<Long> balance;
	private static BitcoinKit wallet;

	private WalletManager() {
		balance = new MutableLiveData<>();
	}

	public static WalletManager init() {
		if (INSTANCE != null) {
			throw new AssertionError("You already initialized an object of this type");
		}
		return INSTANCE = new WalletManager();
	}

	public static WalletManager getInstance() {
		if (INSTANCE == null) {
			throw new AssertionError("You haven't initialized an object of this type yet.");
		}
		return INSTANCE;
	}

	public static   void startWallet() throws Exception {
		NetworkType netType = NetworkType.MainNet;
		String[] mnemonic = MnemonicManager.getMnemonicFromJSON(PreferencesManager.getMnemonic());
		if (mnemonic != null) {
			wallet = new BitcoinKit((List<String>) Arrays.asList(mnemonic), PreferencesManager.getPassphrase(), netType, "wallet", 10, true, 1);
			wallet.setListener(INSTANCE);
			String networkName = netType.name();

			wallet.start();

			balance.setValue(wallet.getBalance());

		} else {
			//I don't know, I'll see how to handle this.
			throw new Exception();
		}
	}


	public static String getReceiveAddress() {
		return wallet.receiveAddress();
	}

	public static void getTransactions() {
	}

	public static void generateMnemonic() {

		MnemonicManager mnemonicManager = new MnemonicManager();
		mnemonicManager.setMnemonic(new io.horizontalsystems.hdwalletkit.Mnemonic().generate(io.horizontalsystems.hdwalletkit.Mnemonic.Strength.Default).toArray(new String[0]));
		setMnemonic(mnemonicManager.getMnemonicAsJSON());
	}

	public static MutableLiveData<Long> getBalance() {
		balance.setValue(wallet.getBalance());
		return balance;
	}

	public void setBalance(MutableLiveData<Long> balance) {
		this.balance = balance;
	}

	@Override
	public void onBalanceUpdate(@NotNull BitcoinKit bitcoinKit, long l) {

	}

	@Override
	public void onKitStateUpdate(@NotNull BitcoinKit bitcoinKit, @NotNull KitState kitState) {

	}

	@Override
	public void onLastBlockInfoUpdate(@NotNull BitcoinKit bitcoinKit, @NotNull BlockInfo blockInfo) {

	}

	@Override
	public void onTransactionsDelete(@NotNull List<String> list) {

	}

	@Override
	public void onTransactionsUpdate(@NotNull BitcoinKit bitcoinKit, @NotNull List<TransactionInfo> list, @NotNull List<TransactionInfo> list1) {

	}
}
