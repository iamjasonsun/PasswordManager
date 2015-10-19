package com.sun_post.mypasswordmanager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sun_post.crypto.PasswordManager;
import com.sun_post.crypto.PasswordManagerSingleton;
import com.sun_post.mypasswordmanager.R;

public class AddPasswordActivity extends ActionBarActivity {

    private PasswordManager passwordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        passwordManager = PasswordManagerSingleton.getInstance();
        Button submitButton = (Button) findViewById(R.id.addPasswordSubmitButton);
        final EditText username = (EditText) findViewById(R.id.addPasswordUsername);
        final EditText password = (EditText) findViewById(R.id.addPasswordPassword);
        final EditText note = (EditText) findViewById(R.id.addPasswordNote);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = true;
                try {
                    passwordManager.storePassword(
                        usernameNoteSaveFormat(username.getText().toString(), note.getText().toString()),
                        password.getText().toString());
                } catch (Exception e) {
                    success = false;
                }
                if (success) {
                    Toast.makeText(getApplicationContext(), "Password saved.", Toast.LENGTH_SHORT).show();
                    username.setText("");
                    password.setText("");
                    note.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "There was an error " +
                            "saving your data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    static String usernameNoteSaveFormat(String username, String note) {
        return new String(username + "\0" + note);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
