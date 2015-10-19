package com.sun_post.mypasswordmanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sun_post.crypto.PasswordManagerSingleton;
import com.sun_post.mypasswordmanager.R;

public class ChangeMasterPasswordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_master_password);

        final Button submitButton = (Button) findViewById(R.id.changePasswordSubmitButton);
        final EditText password = (EditText) findViewById(R.id.changePasswordPassword);
        final EditText passwordConfirm = (EditText) findViewById(R.id.changePasswordConfirmation);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = password.getText().toString();
                if (newPassword.equals(passwordConfirm.getText().toString())) {
                    if (PasswordSetupActivity.isPasswordValid(newPassword)) {
                        try {
                            PasswordManagerSingleton.getInstance().changeMasterPassword(newPassword);
                            finish();
                            Toast.makeText(getApplicationContext(), "Master password changed.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "There was an error " +
                                    "changing your password.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password does not meet requirements", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
