package com.sun_post.crypto;

import java.io.IOException;

/**
 * Created by jiasongsun on 4/7/15.
 */
public class PasswordManagerSingleton {
    private static PasswordManager _passwordManager;

    private PasswordManagerSingleton() {
        // private constructor - prevent initialization
    }

    public static void setPasswordManagerFileName(String passwordFileName) throws IOException {
        _passwordManager = new PasswordManagerImpl(passwordFileName);
    }

    public static PasswordManager getInstance() {
        if (_passwordManager == null) {
            throw new IllegalStateException("Password filename has not be set. Call setPasswordManagerFileName() first.");
        } else {
            return _passwordManager;
        }
    }
}
