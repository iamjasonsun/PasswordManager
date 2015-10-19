package com.sun_post.mypasswordmanager;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sun_post.crypto.PasswordManagerImpl;
import com.sun_post.mypasswordmanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataImportActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_import);getIntent();

        final Button okButton = (Button) findViewById(R.id.importOkButton);
        final Button cancelButton = (Button) findViewById(R.id.importCancelButton);
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Data was not imported.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        final String importPath = getIntent().getData().toString();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastText = "Data imported successfully! You may login with the " +
                        "password associated with the imported data.";
                boolean success = true;
                try {
                    copyFile(importPath.replace("file://", ""),
                            getFilesDir() + "/" + PasswordManagerImpl.passwordFilename);
                } catch (IOException e) {
                    toastText = "Something went wrong while importing your file.";
                    success = false;
                }
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                if (success) {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                finish();
            }
        });

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
            }
        }, 3000);
    }

    private void copyFile(String src, String dst) throws IOException {
        File old = new File(dst);
        if (old.exists() && !old.delete())
            throw new IOException();
        InputStream in = new FileInputStream(new File(src));
        OutputStream out = new FileOutputStream(new File(dst));
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = in.read(buffer)) > 0)
            out.write(buffer, 0, bytes);
        in.close();
        out.close();
    }
}
