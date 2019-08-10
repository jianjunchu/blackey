package org.blackey.ui.wallet.send;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackey.databinding.FragmentWalletSendBinding;
import com.m2049r.xmrwallet.OnBackPressedListener;
import com.m2049r.xmrwallet.OnUriScannedListener;
import com.m2049r.xmrwallet.data.BarcodeData;
import com.m2049r.xmrwallet.data.PendingTx;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.data.TxDataBtc;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.layout.SpendViewPager;
import com.m2049r.xmrwallet.model.PendingTransaction;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.ui.base.BlackeyBaseFragment;
import org.blackey.ui.wallet.WalletActivity;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class SendFragment extends BlackeyBaseFragment<FragmentWalletSendBinding,SendViewModel>
        implements SendAddressWizardFragment.Listener,
        SendAmountWizardFragment.Listener,
        SendConfirmWizardFragment.Listener,
        SendSuccessWizardFragment.Listener,
        OnBackPressedListener, OnUriScannedListener{

    final static public int MIXIN = 10;

    static private int MAX_FALLBACK = Integer.MAX_VALUE;

    public static SendFragment newInstance(String uri) {
        SendFragment f = new SendFragment();
        Bundle args = new Bundle();
        args.putString(WalletActivity.REQUEST_URI, uri);
        f.setArguments(args);
        return f;
    }


    private Drawable arrowPrev;
    private Drawable arrowNext;

    private TxData txData = new TxData();

    private BarcodeData barcodeData;

    @Override
    public void setBarcodeData(BarcodeData data) {
        barcodeData = data;
    }

    @Override
    public BarcodeData getBarcodeData() {
        return barcodeData;
    }

    @Override
    public BarcodeData popBarcodeData() {
        Timber.d("POPPED");
        BarcodeData data = barcodeData;
        barcodeData = null;
        return data;
    }

    @Override
    public void setMode(Mode aMode) {

        if (mode != aMode) {
            mode = aMode;
            switch (aMode) {
                case XMR:
                    txData = new TxData();
                    break;
                case BTC:
                    txData = new TxDataBtc();
                    break;
                default:
                    throw new IllegalArgumentException("Mode " + String.valueOf(aMode) + " unknown!");
            }
            getView().post(new Runnable() {
                @Override
                public void run() {
                    pagerAdapter.notifyDataSetChanged();
                }
            });
            Timber.d("New Mode = %s", mode.toString());
        }

    }

    @Override
    public Listener getActivityCallback() {
        return activityCallback;
    }

    @Override
    public TxData getTxData() {
        return txData;
    }


    PendingTx committedTx;

    @Override
    public PendingTx getCommittedTx() {
        return committedTx;
    }


    @Override
    public void enableDone() {
        binding.llNavBar.setVisibility(View.INVISIBLE);
        binding.bDone.setVisibility(View.VISIBLE);
    }

    @Override
    public void commitTransaction() {
        Timber.d("REALLY SEND");
        disableNavigation(); // committed - disable all navigation
        activityCallback.onSend(txData.getUserNotes());
        committedTx = pendingTx;
    }

    void disableNavigation() {
        spendViewPager.allowSwipe(false);
    }

    void enableNavigation() {
        spendViewPager.allowSwipe(true);
    }

    PendingTx pendingTx;

    public PendingTx getPendingTx() {
        return pendingTx;
    }

    public void onCreateTransactionFailed(String errorText) {
        final SendConfirm confirm = getSendConfirm();
        if (confirm != null) {
            confirm.createTransactionFailed(errorText);
        }
    }

    SendConfirm getSendConfirm() {
        final SendWizardFragment fragment = pagerAdapter.getFragment(SpendPagerAdapter.POS_CONFIRM);
        if (fragment instanceof SendConfirm) {
            return (SendConfirm) fragment;
        } else {
            return null;
        }
    }

    @Override
    public void disposeTransaction() {
        pendingTx = null;
        activityCallback.onDisposeRequest();
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    boolean isComitted() {
        return committedTx != null;
    }

    private SpendViewPager spendViewPager;
    private SpendPagerAdapter pagerAdapter;

    @Override
    public boolean onBackPressed() {
        if (isComitted()) return true; // no going back
        if (spendViewPager.getCurrentItem() == 0) {
            return false;
        } else {
            spendViewPager.previous();
            return true;
        }
    }

    public void onTransactionCreated(final String txTag, final PendingTransaction pendingTransaction) {
        final SendConfirm confirm = getSendConfirm();
        if (confirm != null) {
            pendingTx = new PendingTx(pendingTransaction);
            confirm.transactionCreated(txTag, pendingTransaction);
        } else {
            // not in confirm fragment => dispose & move on
            disposeTransaction();
        }
    }

    public void onTransactionSent(final String txId) {
        Timber.d("txid=%s", txId);
        pagerAdapter.addSuccess();
        Timber.d("numPages=%d", spendViewPager.getAdapter().getCount());
        spendViewPager.setCurrentItem(SpendPagerAdapter.POS_SUCCESS);
    }

    public void onSendTransactionFailed(final String error) {
        Timber.d("error=%s", error);
        committedTx = null;
        final SendConfirm confirm = getSendConfirm();
        if (confirm != null) {
            confirm.sendFailed(getString(R.string.status_transaction_failed, error));
        }
        enableNavigation();
    }

    enum Mode {
        XMR, BTC
    }

    static Mode mode = Mode.XMR;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_send;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
        activityCallback.setSubtitle(getString(R.string.send_title));
        if (spendViewPager.getCurrentItem() == SpendPagerAdapter.POS_SUCCESS) {
        }
    }

    @Override
    public void initData() {
        spendViewPager = binding.pager;

        arrowPrev = getResources().getDrawable(R.drawable.ic_navigate_prev_white_24dp);
        arrowNext = getResources().getDrawable(R.drawable.ic_navigate_next_white_24dp);

        pagerAdapter = new SpendPagerAdapter(getChildFragmentManager());

        binding.pager.setOffscreenPageLimit(pagerAdapter.getCount()); // load & keep all pages in cache
        binding.pager.setAdapter(pagerAdapter);

        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int fallbackPosition = MAX_FALLBACK;
            private int currentPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int newPosition) {
                Timber.d("onPageSelected=%d/%d", newPosition, fallbackPosition);
                if (fallbackPosition < newPosition) {
                    binding.pager.setCurrentItem(fallbackPosition);
                } else {
                    pagerAdapter.getFragment(currentPosition).onPauseFragment();
                    pagerAdapter.getFragment(newPosition).onResumeFragment();
                    updatePosition(newPosition);
                    currentPosition = newPosition;
                    fallbackPosition = MAX_FALLBACK;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    if (!binding.pager.validateFields(binding.pager.getCurrentItem())) {
                        fallbackPosition = binding.pager.getCurrentItem();
                    } else {
                        fallbackPosition = binding.pager.getCurrentItem() + 1;
                    }
                }
            }
        });
    }


    void updatePosition(int position) {
        binding.dotBar.setActiveDot(position);
        CharSequence nextLabel = pagerAdapter.getPageTitle(position + 1);
        binding.bNext.setText(nextLabel);
        if (nextLabel != null) {
            binding.bNext.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowNext, null);
        } else {
            binding.bNext.setCompoundDrawables(null, null, null, null);
        }
        CharSequence prevLabel = pagerAdapter.getPageTitle(position - 1);
        binding.bPrev.setText(prevLabel);
        if (prevLabel != null) {
            binding.bPrev.setCompoundDrawablesWithIntrinsicBounds(arrowPrev, null, null, null);
        } else {
            binding.bPrev.setCompoundDrawables(null, null, null, null);
        }
    }

    private Listener activityCallback;



    public interface Listener {
        SharedPreferences getPrefs();

        long getTotalFunds();

        boolean isStreetMode();

        void onPrepareSend(String tag, TxData data);

        boolean verifyWalletPassword(String password);

        void onSend(UserNotes notes);

        void onDisposeRequest();

        void onFragmentDone();

        void setToolbarButton(int type);

        void setTitle(String title);

        void setSubtitle(String subtitle);

        void setOnUriScannedListener(OnUriScannedListener onUriScannedListener);
    }

    public class SpendPagerAdapter extends FragmentStatePagerAdapter {
        private static final int POS_ADDRESS = 0;
        private static final int POS_AMOUNT = 1;
        private static final int POS_CONFIRM = 2;
        private static final int POS_SUCCESS = 3;
        private int numPages = 3;

        SparseArray<WeakReference<SendWizardFragment>> myFragments = new SparseArray<>();

        public SpendPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addSuccess() {
            numPages++;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return numPages;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Timber.d("instantiateItem %d", position);
            SendWizardFragment fragment = (SendWizardFragment) super.instantiateItem(container, position);
            myFragments.put(position, new WeakReference<>(fragment));
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Timber.d("destroyItem %d", position);
            myFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public SendWizardFragment getFragment(int position) {
            WeakReference ref = myFragments.get(position);
            if (ref != null)
                return myFragments.get(position).get();
            else
                return null;
        }

        public SendFragment newInstance(String uri) {
            SendFragment f = new SendFragment();
            Bundle args = new Bundle();
            args.putString(WalletActivity.REQUEST_URI, uri);
            f.setArguments(args);
            return f;
        }




        @Override
        public SendWizardFragment getItem(int position) {
            Timber.d("getItem(%d) CREATE", position);
            Timber.d("Mode=%s", mode.toString());
            if (mode == Mode.XMR) {
                switch (position) {
                    case POS_ADDRESS:
                        return SendAddressWizardFragment.newInstance(SendFragment.this);
                    case POS_AMOUNT:
                        return SendAmountWizardFragment.newInstance(SendFragment.this);
                    case POS_CONFIRM:
                        return SendConfirmWizardFragment.newInstance(SendFragment.this);
                    case POS_SUCCESS:
                        return SendSuccessWizardFragment.newInstance(SendFragment.this);
                    default:
                        throw new IllegalArgumentException("no such send position(" + position + ")");
                }
            } else if (mode == Mode.BTC) {
                switch (position) {
                    case POS_ADDRESS:
                        return SendAddressWizardFragment.newInstance(SendFragment.this);
                    case POS_AMOUNT:
                        return SendBtcAmountWizardFragment.newInstance(SendFragment.this);
                    case POS_CONFIRM:
                        return SendBtcConfirmWizardFragment.newInstance(SendFragment.this);
                    case POS_SUCCESS:
                        return SendBtcSuccessWizardFragment.newInstance(SendFragment.this);
                    default:
                        throw new IllegalArgumentException("no such send position(" + position + ")");
                }
            } else {
                throw new IllegalStateException("Unknown mode!");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Timber.d("getPageTitle(%d)", position);
            if (position >= numPages) return null;
            switch (position) {
                case POS_ADDRESS:
                    return getString(R.string.send_address_title);
                case POS_AMOUNT:
                    return getString(R.string.send_amount_title);
                case POS_CONFIRM:
                    return getString(R.string.send_confirm_title);
                case POS_SUCCESS:
                    return getString(R.string.send_success_title);
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            Timber.d("getItemPosition %s", String.valueOf(object));
            if (object instanceof SendAddressWizardFragment) {
                // keep these pages
                return POSITION_UNCHANGED;
            } else {
                return POSITION_NONE;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        Timber.d("onAttach %s", context);
        super.onAttach(context);
        if (context instanceof Listener) {
            activityCallback = (Listener) context;
            activityCallback.setOnUriScannedListener(this);
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public boolean onUriScanned(BarcodeData barcodeData) {
        if (binding.pager.getCurrentItem() == SpendPagerAdapter.POS_ADDRESS) {
            final SendWizardFragment fragment = pagerAdapter.getFragment(SpendPagerAdapter.POS_ADDRESS);
            if (fragment instanceof SendAddressWizardFragment) {
                ((SendAddressWizardFragment) fragment).processScannedData(barcodeData);
                return true;
            }
        }
        return false;
    }




}
