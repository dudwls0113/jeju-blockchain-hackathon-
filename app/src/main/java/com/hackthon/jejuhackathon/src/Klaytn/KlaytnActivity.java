package com.hackthon.jejuhackathon.src.Klaytn;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.hackthon.jejuhackathon.R;
import com.klaytn.caver.Caver;
import com.klaytn.caver.crpyto.KlayCredentials;
import com.klaytn.caver.methods.response.KlayTransactionReceipt;
import com.klaytn.caver.tx.ValueTransfer;
import com.klaytn.caver.tx.exception.PlatformErrorException;
import com.klaytn.caver.tx.manager.PollingTransactionReceiptProcessor;
import com.klaytn.caver.tx.manager.TransactionManager;
import com.klaytn.caver.tx.manager.TransactionReceiptProcessor;
import com.klaytn.caver.tx.model.KlayRawTransaction;
import com.klaytn.caver.tx.model.ValueTransferTransaction;
import com.klaytn.caver.utils.ChainId;
import com.klaytn.caver.utils.Convert;
import com.klaytn.caver.wallet.KlayWalletUtils;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Keys;
import org.web3j.protocol.exceptions.TransactionException;

import org.web3j.utils.Numeric;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import static com.hackthon.jejuhackathon.src.utils.PermissionUtils.requestPermission;

public class KlaytnActivity extends AppCompatActivity {

    KlayCredentials credentials;
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestLog";
    final static String filename = "logfile.txt";
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    Caver caver;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klaytn);

        checkPermissions();
        String text = "{ \"version\": 3, \"id\": \"d4d6cfc3-d02a-48e5-9e64-f67d182f1b0d\", \"address\": \"0x2bd63c9322931efa76b77fd7f7a61f7885dd3625\", \"crypto\": { \"ciphertext\": \"1963a33df85e3bac7ad7a79cfa048299cdb13feb094cda33faff0dd8e163082c\", \"cipherparams\": { \"iv\": \"ace1e46741b285484fe1b09fadada04e\" }, \"cipher\": \"aes-128-ctr\", \"kdf\": \"scrypt\", \"kdfparams\": { \"dklen\": 32, \"salt\": \"07eeaed98437c989beee8241f948588615bef98e58ead564b476a5e78d1684c9\", \"n\": 4096, \"r\": 8, \"p\": 1 }, \"mac\": \"7fb355ee123f49abc2fa4905c606536ed5b147a0a72e354307d3705716696692\" } }";

        WriteTextFile(foldername, filename, text);
        File source = new File(foldername + "/" + filename);


        caver = Caver.build(Caver.BAOBAB_URL);  // Caver.BAOBAB_URL = https://api.baobab.klaytn.net:8651

        try {
            credentials = KlayWalletUtils.loadCredentials("ans1152214@", source);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("클레이", e.toString());
        } catch (CipherException e) {
            e.printStackTrace();
            Log.d("클레이", e.toString());
        }
        Log.d("클레이", credentials.getAddress());
//        Log.d("클레이", credentials.get());

        new Thread() {
            public void run() {
                try {
                    KlayTransactionReceipt.TransactionReceipt transactionReceipt
                            = ValueTransfer.create(caver, credentials, ChainId.BAOBAB_TESTNET).sendFunds(
                            credentials.getAddress(),  // fromAddress
                            "0x4e53b46c31b2a78c5dc3c0c5561af2cbe0356722",  // toAddress
                            BigDecimal.ONE,  // value
                            Convert.Unit.PEB,  // unit
                            BigInteger.valueOf(100_000)  // gasLimit
                    ).send();
                } catch (Exception e) {
                    Log.d("클레이", e.toString());
                    e.printStackTrace();
                }

            }
        }.start();


//        try {
//            sendKlay();
//        } catch (Exception e) {
//            Log.d("클레이", e.toString());
//            e.printStackTrace();
//        }

//        Log.d("Klay", credentials.getAddress());
//        KlayTransactionReceipt.TransactionReceipt transactionReceipt
//                = ValueTransfer.create(caver, credentials, ChainId.BAOBAB_TESTNET).sendFunds(
//                credentials.getAddress(),  // fromAddress
//                "0xe97f27e9a5765ce36a7b919b1cb6004c7209217e",  // toAddress
//                BigDecimal.ONE,  // value
//                Convert.Unit.,  // unit
//                BigInteger.valueOf(100_000)  // gasLimit
//        ).send();


    }

    public void sendKlay() {
        TransactionManager transactionManager = new TransactionManager.Builder(caver, credentials)
                .setChaindId(ChainId.BAOBAB_TESTNET).build();

        ValueTransferTransaction valueTransferTransaction = ValueTransferTransaction.create(
                credentials.getAddress(),  // fromAddress
                "0xe97f27e9a5765ce36a7b919b1cb6004c7209217e",  // toAddress
                BigInteger.ONE,  // value
                BigInteger.valueOf(100_000)  // gasLimit
        );

        KlayRawTransaction klayRawTransaction = transactionManager.sign(valueTransferTransaction);
        String transactionHash = null;
        try {
            transactionHash = transactionManager.send(klayRawTransaction);
        } catch (IOException e) {
            Log.d("클레이", e.toString());
            e.printStackTrace();
        } catch (PlatformErrorException e) {
            Log.d("클레이", e.toString());
            e.printStackTrace();
        }

        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);  // pollingSleepDuration = 1000, pollingAttempts = 15
        try {
            KlayTransactionReceipt.TransactionReceipt transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
        } catch (TransactionException e) {
            Log.d("클레이", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("클레이", e.toString());
            e.printStackTrace();
        }
    }

    //텍스트내용을 경로의 텍스트 파일에 쓰기
    public void WriteTextFile(String foldername, String filename, String contents) {
        try {
            File dir = new File(foldername);
            //디렉토리 폴더가 없으면 생성함
            if (!dir.exists()) {
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername + "/" + filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("클레이", e.toString());
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            }
                        }
                    }
                } else {
                }
                return;
            }
        }
    }
}
