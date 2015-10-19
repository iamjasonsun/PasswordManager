package com.sun_post.mypasswordmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sun_post.crypto.PasswordManager;
import com.sun_post.crypto.PasswordManagerSingleton;

import java.io.IOException;


/**
 *
 */
public class PasswordSetupActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSetPasswordTask mAuthTask = null;

    // UI references.
    private EditText passwordField;
    private EditText passwordConfirmField;
    private View mProgressView;
    private View mLoginFormView;
    private PasswordManager passwordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setup);
        passwordManager = PasswordManagerSingleton.getInstance();
        if (passwordManager.doesMasterPasswordExist()) {
            goToLoginActivity();
        }

        passwordField = (EditText) findViewById(R.id.password_setup);
        passwordConfirmField = (EditText) findViewById(R.id.password_setup_confirm);
        passwordField.setText("");
        passwordConfirmField.setText("");

        Button passwordCreateButton = (Button) findViewById(R.id.make_password_button);
        passwordCreateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptMasterPasswordCreate();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptMasterPasswordCreate() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        passwordField.setError(null);

        // Store values at the time of the login attempt.
        String password = passwordField.getText().toString();
        String passwordConfirm = passwordConfirmField.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!password.equals(passwordConfirm)) {
            passwordField.setError("Passwords do not match.");
            focusView = passwordField;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordField.setError("This password is too short.");
            focusView = passwordField;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserSetPasswordTask(password);
            mAuthTask.execute((Void) null);
        }
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 10;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void goToLoginActivity () {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public class UserSetPasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPassword;

        UserSetPasswordTask(String password) {
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                passwordManager.createMasterPassword(mPassword);
            } catch (Exception e) {
                return false;
            }
            goToLoginActivity();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                passwordField.setError("Something went wrong while setting up your password.");
                passwordField.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



