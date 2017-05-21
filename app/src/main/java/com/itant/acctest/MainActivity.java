package com.itant.acctest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.activeButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(killIntent);
            }
        });

        this.findViewById(R.id.installButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // We don't have permission so prompt the user
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    }


                    MyAccessibilityService.INVOKE_TYPE = MyAccessibilityService.TYPE_INSTALL_APP;
                    String fileName = Environment.getExternalStorageDirectory() + "/test.apk";
                    File installFile = new File(fileName);
                    if (installFile.exists()) {
                        installFile.delete();
                    }

                    installFile.createNewFile();
                    FileOutputStream out = new FileOutputStream(installFile);
                    byte[] buffer = new byte[512];
                    InputStream in = MainActivity.this.getAssets().open("test.apk");
                    int count;
                    while ((count = in.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                    in.close();
                    out.close();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                    startActivity(intent);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }



            }
        });
        this.findViewById(R.id.uninstallButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                MyAccessibilityService.INVOKE_TYPE = MyAccessibilityService.TYPE_UNINSTALL_APP;
                Uri packageURI = Uri.parse("package:com.example.test");
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                startActivity(uninstallIntent);
            }
        });
        this.findViewById(R.id.killAppButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                MyAccessibilityService.INVOKE_TYPE = MyAccessibilityService.TYPE_KILL_APP;
                Intent killIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri packageURI = Uri.parse("package:com.example.test");
                killIntent.setData(packageURI);
                startActivity(killIntent);
            }
        });
    }
}
