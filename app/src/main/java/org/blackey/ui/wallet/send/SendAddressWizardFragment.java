package org.blackey.ui.wallet.send;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletSendAddressBinding;
import com.m2049r.xmrwallet.data.BarcodeData;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.data.TxDataBtc;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.model.PendingTransaction;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.util.BitcoinAddressValidator;
import com.m2049r.xmrwallet.util.Helper;
import com.m2049r.xmrwallet.util.OpenAliasHelper;
import com.m2049r.xmrwallet.util.PaymentProtocolHelper;
import com.m2049r.xmrwallet.xmrto.XmrToError;
import com.m2049r.xmrwallet.xmrto.XmrToException;

import java.util.Map;

import timber.log.Timber;


public class SendAddressWizardFragment extends SendWizardFragment<FragmentWalletSendAddressBinding,SendAddressWizardViewModel> {



    static final int INTEGRATED_ADDRESS_LENGTH = 106;

    public static SendAddressWizardFragment newInstance(Listener listener) {
        SendAddressWizardFragment instance = new SendAddressWizardFragment();
        instance.setSendListener(listener);
        return instance;
    }

    private boolean resolvingOA = false;
    private boolean resolvingPP = false;
    private String resolvedPP = null;

    OnScanListener onScanListener;

    Listener sendListener;

    public interface OnScanListener {
        void onScan();
    }


    public SendAddressWizardFragment setSendListener(Listener listener) {
        this.sendListener = listener;
        return this;
    }

    public interface Listener {
        void setBarcodeData(BarcodeData data);

        BarcodeData getBarcodeData();

        BarcodeData popBarcodeData();

        void setMode(SendFragment.Mode mode);

        TxData getTxData();
    }



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_send_address;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        binding.tvXmrTo.setText(Html.fromHtml(getString(R.string.info_xmrto)));
        binding.etAddress.getEditText().setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        binding.etPaymentId.getEditText().setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        binding.etNotes.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);
        binding.etDummy.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        binding.etDummy.requestFocus();
    }

    @Override
    public void initViewObservable() {
        binding.etAddress.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ignore ENTER
                return ((event != null) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER));
            }
        });

        binding.etAddress.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    View next = binding.etAddress;
                    String enteredAddress = binding.etAddress.getEditText().getText().toString().trim();
                    String dnsOA = dnsFromOpenAlias(enteredAddress);
                    Timber.d("OpenAlias is %s", dnsOA);
                    if (dnsOA != null) {
                        processOpenAlias(dnsOA);
                        next = null;
                    } else {
                        // maybe a bip72 or 70 URI
                        String bip70 = PaymentProtocolHelper.getBip70(enteredAddress);
                        if (bip70 != null) {
                            // looks good - resolve through xmr.to
                            processBip70(bip70);
                            next = null;
                        } else if (checkAddress()) {
                            if (binding.llPaymentId.getVisibility() == View.VISIBLE) {
                                next = binding.etPaymentId;
                            } else {
                                next = binding.etNotes;
                            }
                        }
                    }
                    if (next != null) {
                        final View focus = next;
                        binding.etAddress.post(new Runnable() {
                            @Override
                            public void run() {
                                focus.requestFocus();
                            }
                        });
                    }
                }
            }
        });
        binding.etAddress.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                Timber.d("AFTER: %s", editable.toString());
                if (editable.toString().equals(resolvedPP)) return; // no change required
                resolvedPP = null;
                binding.etAddress.setError(null);
                if (isIntegratedAddress()) {
                    Timber.d("isIntegratedAddress");
                    binding.etPaymentId.getEditText().getText().clear();
                    binding.llPaymentId.setVisibility(View.INVISIBLE);
                    binding.tvPaymentIdIntegrated.setVisibility(View.VISIBLE);
                    binding.llXmrTo.setVisibility(View.INVISIBLE);
                    sendListener.setMode(SendFragment.Mode.XMR);
                } else if (isBitcoinAddress() || (resolvedPP != null)) {
                    Timber.d("isBitcoinAddress");
                    setBtcMode();
                } else {
                    Timber.d("isStandardAddress or other");
                    binding.llPaymentId.setVisibility(View.VISIBLE);
                    binding.tvPaymentIdIntegrated.setVisibility(View.INVISIBLE);
                    binding.llXmrTo.setVisibility(View.INVISIBLE);
                    sendListener.setMode(SendFragment.Mode.XMR);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        binding.etPaymentId.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))
                        || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    if (checkPaymentId()) {
                        binding.etNotes.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        binding.etPaymentId.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                binding.etPaymentId.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        binding.bPaymentId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etPaymentId.getEditText().setText((Wallet.generatePaymentId()));
            }
        });


        binding.etNotes.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    binding.etDummy.requestFocus();
                    Helper.hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        binding.bScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanListener.onScan();
            }
        });

        Helper.hideKeyboard(getActivity());
    }

    private void setBtcMode() {
        Timber.d("setBtcMode");
        binding.etPaymentId.getEditText().getText().clear();
        binding.llPaymentId.setVisibility(View.INVISIBLE);
        binding.tvPaymentIdIntegrated.setVisibility(View.INVISIBLE);
        binding.llXmrTo.setVisibility(View.VISIBLE);
        sendListener.setMode(SendFragment.Mode.BTC);
    }

    private void processOpenAlias(String dnsOA) {
        if (resolvingOA) return; // already resolving - just wait
        sendListener.popBarcodeData();
        if (dnsOA != null) {
            resolvingOA = true;
            binding.etAddress.setError(getString(R.string.send_address_resolve_openalias));
            OpenAliasHelper.resolve(dnsOA, new OpenAliasHelper.OnResolvedListener() {
                @Override
                public void onResolved(Map<BarcodeData.Asset, BarcodeData> dataMap) {
                    resolvingOA = false;
                    BarcodeData barcodeData = dataMap.get(BarcodeData.Asset.XMR);
                    if (barcodeData == null) barcodeData = dataMap.get(BarcodeData.Asset.BTC);
                    if (barcodeData != null) {
                        Timber.d("Security=%s, %s", barcodeData.security.toString(), barcodeData.address);
                        processScannedData(barcodeData);
                    } else {
                        binding.etAddress.setError(getString(R.string.send_address_not_openalias));
                        Timber.d("NO XMR OPENALIAS TXT FOUND");
                    }
                }

                @Override
                public void onFailure() {
                    resolvingOA = false;
                    binding.etAddress.setError(getString(R.string.send_address_not_openalias));
                    Timber.e("OA FAILED");
                }
            });
        } // else ignore
    }

    private void processBip70(final String bip70) {
        Timber.d("RESOLVED PP: %s", resolvedPP);
        if (resolvingPP) return; // already resolving - just wait
        resolvingPP = true;
        sendListener.popBarcodeData();
        binding.etAddress.setError(getString(R.string.send_address_resolve_bip70));
        PaymentProtocolHelper.resolve(bip70, new PaymentProtocolHelper.OnResolvedListener() {
            @Override
            public void onResolved(BarcodeData.Asset asset, String address, double amount, String resolvedBip70) {
                resolvingPP = false;
                if (asset != BarcodeData.Asset.BTC)
                    throw new IllegalArgumentException("only BTC here");

                if (resolvedBip70 == null)
                    throw new IllegalArgumentException("success means we have a pp_url - else die");

                final BarcodeData barcodeData =
                        new BarcodeData(BarcodeData.Asset.BTC, address, null,
                                resolvedBip70, null, null, String.valueOf(amount),
                                BarcodeData.Security.BIP70);
                binding.etNotes.post(new Runnable() {
                    @Override
                    public void run() {
                        Timber.d("security is %s", barcodeData.security);
                        processScannedData(barcodeData);
                        binding.etNotes.requestFocus();
                    }
                });
            }

            @Override
            public void onFailure(final Exception ex) {
                resolvingPP = false;
                binding.etAddress.post(new Runnable() {
                    @Override
                    public void run() {
                        int errorMsgId = R.string.send_address_not_bip70;
                        if (ex instanceof XmrToException) {
                            XmrToError error = ((XmrToException) ex).getError();
                            if (error != null) {
                                errorMsgId = error.getErrorMsgId();
                            }
                        }
                        binding.etAddress.setError(getString(errorMsgId));
                    }
                });
                Timber.d("PP FAILED");
            }
        });
    }

    private boolean checkAddressNoError() {
        String address = binding.etAddress.getEditText().getText().toString();
        return Wallet.isAddressValid(address)
                || BitcoinAddressValidator.validate(address)
                || (resolvedPP != null);
    }

    private boolean checkAddress() {
        boolean ok = checkAddressNoError();
        if (!ok) {
            binding.etAddress.setError(getString(R.string.send_address_invalid));
        } else {
            binding.etAddress.setError(null);
        }
        return ok;
    }

    private boolean isIntegratedAddress() {
        String address = binding.etAddress.getEditText().getText().toString();
        return (address.length() == INTEGRATED_ADDRESS_LENGTH)
                && Wallet.isAddressValid(address);
    }

    private boolean isBitcoinAddress() {
        final String address = binding.etAddress.getEditText().getText().toString();
        return BitcoinAddressValidator.validate(address);
    }

    private boolean checkPaymentId() {
        String paymentId = binding.etPaymentId.getEditText().getText().toString();
        boolean ok = paymentId.isEmpty() || Wallet.isPaymentIdValid(paymentId);
        if (!ok) {
            binding.etPaymentId.setError(getString(R.string.receive_paymentid_invalid));
        } else {
            if (!paymentId.isEmpty() && isIntegratedAddress()) {
                ok = false;
                binding.etPaymentId.setError(getString(R.string.receive_integrated_paymentid_invalid));
            } else {
                binding.etPaymentId.setError(null);
            }
        }
        return ok;
    }

    private void shakeAddress() {
        binding.etAddress.startAnimation(Helper.getShakeAnimation(getContext()));
    }

    @Override
    public boolean onValidateFields() {
        if (!checkAddressNoError()) {
            shakeAddress();
            String enteredAddress = binding.etAddress.getEditText().getText().toString().trim();
            String dnsOA = dnsFromOpenAlias(enteredAddress);
            Timber.d("OpenAlias is %s", dnsOA);
            if (dnsOA != null) {
                processOpenAlias(dnsOA);
            } else {
                String bip70 = PaymentProtocolHelper.getBip70(enteredAddress);
                if (bip70 != null) {
                    processBip70(bip70);
                }
            }
            return false;
        }

        if (!checkPaymentId()) {
            binding.etPaymentId.startAnimation(Helper.getShakeAnimation(getContext()));
            return false;
        }

        if (sendListener != null) {
            TxData txData = sendListener.getTxData();
            if (txData instanceof TxDataBtc) {
                if (resolvedPP != null) {
                    // take the value from the field nonetheless as this is what the user sees
                    // (in case we have a bug somewhere)
                    ((TxDataBtc) txData).setBip70(binding.etAddress.getEditText().getText().toString());
                    ((TxDataBtc) txData).setBtcAddress(null);
                } else {
                    ((TxDataBtc) txData).setBtcAddress(binding.etAddress.getEditText().getText().toString());
                    ((TxDataBtc) txData).setBip70(null);
                }
                txData.setDestinationAddress(null);
                txData.setPaymentId("");
            } else {
                txData.setDestinationAddress(binding.etAddress.getEditText().getText().toString());
                txData.setPaymentId(binding.etPaymentId.getEditText().getText().toString());
            }
            txData.setUserNotes(new UserNotes(binding.etNotes.getEditText().getText().toString()));
            txData.setPriority(PendingTransaction.Priority.Priority_Default);
            txData.setMixin(SendFragment.MIXIN);
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScanListener) {
            onScanListener = (OnScanListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ScanListener");
        }
    }

    // QR Scan Stuff

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
        processScannedData();
    }

    public void processScannedData(BarcodeData barcodeData) {
        sendListener.setBarcodeData(barcodeData);
        if (isResumed())
            processScannedData();
    }

    public void processScannedData() {
        resolvedPP = null;
        BarcodeData barcodeData = sendListener.getBarcodeData();
        if (barcodeData != null) {
            Timber.d("GOT DATA");

            if (barcodeData.bip70 != null) {
                setBtcMode();
                if (barcodeData.security == BarcodeData.Security.BIP70) {
                    resolvedPP = barcodeData.bip70;
                    binding.etAddress.setError(getString(R.string.send_address_bip70));
                } else {
                    processBip70(barcodeData.bip70);
                }
                binding.etAddress.getEditText().setText(barcodeData.bip70);
            } else if (barcodeData.address != null) {
                binding.etAddress.getEditText().setText(barcodeData.address);
                if (checkAddress()) {
                    if (barcodeData.security == BarcodeData.Security.OA_NO_DNSSEC)
                        binding.etAddress.setError(getString(R.string.send_address_no_dnssec));
                    else if (barcodeData.security == BarcodeData.Security.OA_DNSSEC)
                        binding.etAddress.setError(getString(R.string.send_address_openalias));
                }
            } else {
                binding.etAddress.getEditText().getText().clear();
                binding.etAddress.setError(null);
            }

            String scannedPaymentId = barcodeData.paymentId;
            if (scannedPaymentId != null) {
                binding.etPaymentId.getEditText().setText(scannedPaymentId);
                checkPaymentId();
            } else {
                binding.etPaymentId.getEditText().getText().clear();
                binding.etPaymentId.setError(null);
            }
            String scannedNotes = barcodeData.description;
            if (scannedNotes != null) {
                binding.etNotes.getEditText().setText(scannedNotes);
            } else {
                binding.etNotes.getEditText().getText().clear();
                binding.etNotes.setError(null);
            }
        } else
            Timber.d("barcodeData=null");
    }

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        Timber.d("onResumeFragment()");
        Helper.hideKeyboard(getActivity());
        binding.etDummy.requestFocus();
    }

    String dnsFromOpenAlias(String openalias) {
        Timber.d("checking openalias candidate %s", openalias);
        if (Patterns.DOMAIN_NAME.matcher(openalias).matches()) return openalias;
        if (Patterns.EMAIL_ADDRESS.matcher(openalias).matches()) {
            openalias = openalias.replaceFirst("@", ".");
            if (Patterns.DOMAIN_NAME.matcher(openalias).matches()) return openalias;
        }
        return null; // not an openalias
    }
}
