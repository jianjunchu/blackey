package org.blackey.utils;

import android.content.Intent;

import com.alibaba.fastjson.JSONArray;
import org.blackey.R;
import org.blackey.exception.AuthenticationFailedException;
import org.blackey.exception.DataFormatException;
import org.blackey.utils.nfc.AuthenticationException;
import org.blackey.utils.nfc.TagUtil;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.model.WalletManager;

import org.blackey.exception.AuthenticationFailedException;
import org.blackey.exception.DataFormatException;
import org.blackey.utils.nfc.AuthenticationException;
import org.blackey.utils.nfc.TagUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import me.goldze.mvvmhabit.utils.KLog;

public class NfcBackupHandle {

    private static final String secretKey = "49454D4B41455242214E4143554F5946";

    private TagUtil tagUtil ;

    private String  passwd ;

    private Intent intent;

    private Wallet wallet;

    private boolean saveSeed;

    /**
     *
     * @param intent
     * @param wallet 要备份到钱包
     * @param passwd nfc 8位密码
     * @param saveSeed 是否保存 seed
     * @throws Exception
     */
    public NfcBackupHandle(Intent intent, Wallet wallet,String  passwd,boolean saveSeed ) throws Exception {
        this.intent = intent;
        this.wallet = wallet;
        this.passwd = passwd;
        this.saveSeed = saveSeed;
        tagUtil = TagUtil.selectTag(intent, false);
    }

    public NfcBackupHandle(Intent intent,String  passwd) throws Exception {
        this.intent = intent;
        this.passwd = passwd;
        tagUtil = TagUtil.selectTag(intent, false);
    }

//    public boolean backupWalletToNFC() {
//        boolean result = false;
//        boolean authenticated = false;
//        try {
//            authenticated = tagUtil.authentication_internal(intent, secretKey, false);
//
//            if(authenticated)
//            {
//                result =  tagUtil.writeNewKey216SC(intent,passwd,false);
//                if(!result)
//                    result= false;
//            }else
//                result=false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    /**
     * Backup wallet primary address/viewkey/spendkey/seed To NFC Card
     * @return
     * @throws Exception
     */
    public boolean backupWalletToNFC() throws Exception {
        boolean result=false;
        //Timber.d("backup " + walletFile.getAbsolutePath() + " to " + backupFile.getAbsolutePath());
        boolean authenticated=false;
        authenticated = tagUtil.authentication_internal(intent, passwd, false);
        if(authenticated)
        {
            try {

                result = tagUtil.setAccess216SC(intent, (byte) 50, 1, false);
                if(!result) {
                    throw new Exception("backup to nfc config access failed");
                }
                JSONArray plainText = new JSONArray();
                plainText.add(wallet.getAddress());
                plainText.add(wallet.getSecretViewKey());
                result = tagUtil.writeTag(intent,(byte)4,plainText.toJSONString().getBytes(),false);
                if(result) {
                    JSONArray protectText = new JSONArray();
                    protectText.add(wallet.getSecretSpendKey());
                    if (saveSeed) {
                        protectText.add(wallet.getSeed());
                    }
                    result = tagUtil.writeTag(intent, (byte) 50, protectText.toJSONString().getBytes(), false);
                    if(!result) {
                        throw new Exception("backup to NFC failed");
                    }
                }else
                {
                    throw new AuthenticationFailedException();
                }
                return result;
            } catch (FileNotFoundException e) {
                throw e;
            } catch (AuthenticationException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw e;
            }finally {
                if(WalletManager.getInstance().getWallet()!=null)
                    wallet.close();
                tagUtil.close();
            }
        }
        else{
            tagUtil.close();
            throw new AuthenticationFailedException();
        }
    }

    /**
     * Read wallet primary address/viewkey/spendkey/seed from NFC
     * @return
     * @throws Exception
     */
    public String[] readNfc() throws Exception{
        String[] keys = new String[4];
        //Timber.d("backup " + walletFile.getAbsolutePath() + " to " + backupFile.getAbsolutePath());
        boolean authenticated=false;
        authenticated = tagUtil.authentication_internal(intent, passwd, false);

        if(authenticated)
        {
            try {

                StringBuffer stringBuffer1 = new StringBuffer();

                for(int i = 4 ; i < 50;i++){
                    byte[] bytes1 = tagUtil.readOnePage(intent,(byte) i,false);
                    stringBuffer1.append(new String(bytes1,"UTF-8"));
                }
                if(stringBuffer1.indexOf("[")==-1 ||stringBuffer1.indexOf("]")==-1)
                {
                    throw new DataFormatException("\"[\" or \"]\" not found in first sector");
                }
                String k1 = stringBuffer1.substring(stringBuffer1.indexOf("["),stringBuffer1.indexOf("]")+1);

                JSONArray jsonArray1 = JSONArray.parseArray(k1);
                if(jsonArray1!=null && jsonArray1.size()==2) {
                    keys[0] = jsonArray1.getString(0);
                    keys[1] = jsonArray1.getString(1);
                }else {
                    throw new DataFormatException("Elements number should be 2 in first sector");
                }

                StringBuffer stringBuffer2 = new StringBuffer();
                for(int i = 50 ; i < 200;i++){
                    byte[] bytes1 = tagUtil.readOnePage(intent,(byte) i,false);
                    stringBuffer2.append(new String(bytes1,"UTF-8"));
                }

                if(stringBuffer2.indexOf("[")==-1 ||stringBuffer2.indexOf("]")==-1)
                {
                    throw new DataFormatException("\"[\" or \"]\" not found in second sector");
                }
                String k2 = stringBuffer2.substring(stringBuffer2.indexOf("["),stringBuffer2.indexOf("]")+1);

                JSONArray jsonArray2 = JSONArray.parseArray(k2);
                if(jsonArray2!=null && jsonArray2.size()==2) {
                    keys[2] = jsonArray2.getString(0);
                    keys[3] = jsonArray2.getString(1);
                }else if(jsonArray2!=null && jsonArray2.size()==1) {
                    keys[2] = jsonArray2.getString(0);
                }else{
                    throw new DataFormatException("Elements number should be 1 or 2 in second sector");
                }
                KLog.e(Arrays.toString(keys));
                return keys;

            } catch (FileNotFoundException e) {
                throw e;
            } catch (AuthenticationException e) {
                e.printStackTrace();
                throw e;
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw e;
            }finally {
                tagUtil.close();
            }
        }
        else{
            throw new AuthenticationFailedException();
        }
    }
}
