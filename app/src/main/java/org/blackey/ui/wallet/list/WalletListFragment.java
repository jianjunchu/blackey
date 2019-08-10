package org.blackey.ui.wallet.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.Observable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.m2049r.xmrwallet.model.WalletManager;
import com.m2049r.xmrwallet.util.Helper;
import com.m2049r.xmrwallet.util.KeyStoreHelper;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.view.RxToast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletListBinding;
import org.blackey.ui.base.BlackeyBaseFragment;
import org.blackey.ui.wallet.detail.GenerateReviewFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import timber.log.Timber;

/**
 * 钱包管理
 */
public class WalletListFragment extends BlackeyBaseFragment<FragmentWalletListBinding, WalletListViewModel> {

    private WalletListViewModel.Listener activityCallback;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_list;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WalletListViewModel.Listener) {
            this.activityCallback = (WalletListViewModel.Listener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.listener = listener;
        viewModel.context = getActivity();
        viewModel.spinnerview =  binding.include.ivAddIcon ;
        viewModel.swipeRefresh = binding.swipeRefresh;
        viewModel.ivGunther = binding.ivGunther;
        binding.swipeRefresh.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));

        viewModel.initToolbar();

    }

    public interface OnInteractionListener {
        void onInteraction(View view, WalletManager.WalletInfo item);

        boolean onContextInteraction(MenuItem item, WalletManager.WalletInfo infoItem);
    }

    public final OnInteractionListener listener = new OnInteractionListener() {
        @Override
        public void onInteraction(View view, WalletManager.WalletInfo item) {

        }

        @Override
        public boolean onContextInteraction(MenuItem item, WalletManager.WalletInfo listItem) {
            switch (item.getItemId()) {
                case R.id.action_info:
                    showInfo(listItem.getName());
                    break;
                case R.id.action_delete:
                    onWalletArchive(listItem.getName());
                    break;
                case R.id.action_backup_nfc: //backup to NFC
                    String walletName = listItem.getName();
                    final File walletFile = Helper.getWalletFile(getActivity(), walletName);
                    if (WalletManager.getInstance().walletExists(walletFile)) {
                        Helper.promptPassword(getActivity(), walletName, true, new Helper.PasswordAction() {
                            @Override
                            public void action(String walletName, String password, boolean fingerprintUsed) {
                                RxKeyboardTool.hideSoftInput(getActivity());
                                activityCallback.onWalletBackupToNFC(walletName,password);
                            }
                        });
                    } else { // this cannot really happen as we prefilter choices
                        Timber.e("Wallet missing: %s", walletName);
                        RxToast.error(getActivity().getString(R.string.bad_wallet));

                    }

            }
            return true;
        }
    };


    public void onWalletArchive(final String walletName) {
        Timber.d("archive for wallet ." + walletName + ".");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        new AsyncArchive().execute(walletName);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.archive_alert_message))
                .setTitle(walletName)
                .setPositiveButton(getString(R.string.archive_alert_yes), dialogClickListener)
                .setNegativeButton(getString(R.string.archive_alert_no), dialogClickListener)
                .show();
    }


    private class AsyncArchive extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(getString(R.string.loading));
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length != 1) return false;
            String walletName = params[0];
            if (backupWallet(walletName) && deleteWallet(Helper.getWalletFile(getContext(), walletName))) {
                KeyStoreHelper.removeWalletUserPass(getContext(), walletName);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            dismissDialog();
            if (result) {
                reloadWalletList();
            } else {
                RxToast.showToast(R.string.archive_failed);
            }
        }
    }

    void reloadWalletList() {
        Timber.d("reloadWalletList()");
        try {
            viewModel.requestNetWork();
        } catch (ClassCastException ex) {
        }
    }


    private boolean backupWallet(String walletName) {
        File backupFolder = new File(Helper.getWalletRoot(getContext()), "backups");
        if (!backupFolder.exists()) {
            if (!backupFolder.mkdir()) {
                Timber.e("Cannot create backup dir %s", backupFolder.getAbsolutePath());
                return false;
            }
            // make folder visible over USB/MTP
            MediaScannerConnection.scanFile(getContext(), new String[]{backupFolder.toString()}, null, null);
        }
        File walletFile = Helper.getWalletFile(getContext(), walletName);
        File backupFile = new File(backupFolder, walletName);
        Timber.d("backup " + walletFile.getAbsolutePath() + " to " + backupFile.getAbsolutePath());
        // TODO probably better to copy to a new file and then rename
        // then if something fails we have the old backup at least
        // or just create a new backup every time and keep n old backups
        boolean success = copyWallet(walletFile, backupFile, true, true);
        Timber.d("copyWallet is %s", success);
        return success;
    }

    boolean walletExists(File walletFile, boolean any) {
        File dir = walletFile.getParentFile();
        String name = walletFile.getName();
        if (any) {
            return new File(dir, name).exists()
                    || new File(dir, name + ".keys").exists()
                    || new File(dir, name + ".address.txt").exists();
        } else {
            return new File(dir, name).exists()
                    && new File(dir, name + ".keys").exists()
                    && new File(dir, name + ".address.txt").exists();
        }
    }

    boolean copyWallet(File srcWallet, File dstWallet, boolean overwrite,
                       boolean ignoreCacheError) {
        if (walletExists(dstWallet, true) && !overwrite) return false;
        boolean success = false;
        File srcDir = srcWallet.getParentFile();
        String srcName = srcWallet.getName();
        File dstDir = dstWallet.getParentFile();
        String dstName = dstWallet.getName();
        try {
            try {
                copyFile(new File(srcDir, srcName), new File(dstDir, dstName));
            } catch (IOException ex) {
                Timber.d("CACHE %s", ignoreCacheError);
                if (!ignoreCacheError) { // ignore cache backup error if backing up (can be resynced)
                    throw ex;
                }
            }
            copyFile(new File(srcDir, srcName + ".keys"), new File(dstDir, dstName + ".keys"));
            copyFile(new File(srcDir, srcName + ".address.txt"), new File(dstDir, dstName + ".address.txt"));
            success = true;
        } catch (IOException ex) {
            Timber.e("wallet copy failed: %s", ex.getMessage());
            // try to rollback
            deleteWallet(dstWallet);
        }
        return success;
    }

    void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    // do our best to delete as much as possible of the wallet files
    boolean deleteWallet(File walletFile) {
        Timber.d("deleteWallet %s", walletFile.getAbsolutePath());
        File dir = walletFile.getParentFile();
        String name = walletFile.getName();
        boolean success = true;
        File cacheFile = new File(dir, name);
        if (cacheFile.exists()) {
            success = cacheFile.delete();
        }
        success = new File(dir, name + ".keys").delete() && success;
        File addressFile = new File(dir, name + ".address.txt");
        if (addressFile.exists()) {
            success = addressFile.delete() && success;
        }
        Timber.d("deleteWallet is %s", success);
        return success;
    }

    private void showInfo(@NonNull final String walletName) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        final File walletFile = Helper.getWalletFile(getActivity(), walletName);
                        if (WalletManager.getInstance().walletExists(walletFile)) {
                            Helper.promptPassword(getActivity(), walletName, true, new Helper.PasswordAction() {
                                @Override
                                public void action(String walletName, String password, boolean fingerprintUsed) {
                                    startDetails(walletFile, password, GenerateReviewFragment.VIEW_TYPE_DETAILS);
                                }
                            });
                        } else { // this cannot really happen as we prefilter choices
                            Timber.e("Wallet missing: %s", walletName);
                            RxToast.error(getActivity().getString(R.string.bad_wallet));

                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.details_alert_message))
                .setPositiveButton(getActivity().getString(R.string.details_alert_yes), dialogClickListener)
                .setNegativeButton(getActivity().getString(R.string.details_alert_no), dialogClickListener)
                .show();
    }

    private void startDetails(File walletFile, String password, String type) {

        Bundle b = new Bundle();
        b.putString("path", walletFile.getAbsolutePath());
        b.putString("password", password);
        b.putString("type", type);

        startContainerActivity(GenerateReviewFragment.class.getCanonicalName(),b);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.requestNetWork();
    }

    @Override
    public void initViewObservable() {
        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                //结束刷新
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }
}
