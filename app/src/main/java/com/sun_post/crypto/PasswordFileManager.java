package com.sun_post.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PasswordFileManager {
    private static final String KEY_VALUE_SEPARATOR = ",";

    private final String _passwordFilename;
    private String _masterPasswordHash;
    private final Map<String, String> _passwordNameToEncryptedPasswordMap;

    public PasswordFileManager(String passwordFileName) throws IOException {
        _passwordFilename = passwordFileName;
        // if expected file does not exist, create one
        File passwordFile = new File(_passwordFilename);
        if (!passwordFile.exists() || passwordFile.isDirectory()) {
            PrintWriter pw = new PrintWriter(passwordFile);
            pw.close();
        }
        // initialize password file manager
        _passwordNameToEncryptedPasswordMap = new HashMap<String, String>();
        BufferedReader passwordFileReader = new BufferedReader(new FileReader(passwordFile));
        String inputLine = passwordFileReader.readLine();
        if (inputLine != null) {
            _masterPasswordHash = inputLine;
            inputLine = passwordFileReader.readLine();
            if (inputLine.length() != 0) {
                passwordFileReader.close();
                throw new IllegalStateException(
                        "Invalid password file format: blank line expected after master password hash.");
            }
            inputLine = passwordFileReader.readLine();
            while (inputLine != null) {
                String[] keyValuePair = inputLine.split(KEY_VALUE_SEPARATOR);
                _passwordNameToEncryptedPasswordMap.put(keyValuePair[0], keyValuePair[1]);
                inputLine = passwordFileReader.readLine();
            }
        }
        passwordFileReader.close();
    }

    public String getMasterPasswordHashValue() {
        return _masterPasswordHash;
    }

    public Set<String> getAllPasswordNames() {
        return _passwordNameToEncryptedPasswordMap.keySet();
    }

    public String getEncryptedPassword(String passwordName) {
        return _passwordNameToEncryptedPasswordMap.get(passwordName);
    }

    public void setMasterPasswordHashValue(String masterPasswordHash) throws FileNotFoundException {
        _masterPasswordHash = masterPasswordHash;
        saveChanges();
    }

    public void addPassword(String passwordName, String encryptedPassword) throws FileNotFoundException {
        if (_masterPasswordHash == null) {
            throw new IllegalStateException("Master password has to exist before adding passwords.");
        }
        if (_passwordNameToEncryptedPasswordMap.keySet().contains(passwordName)) {
            throw new IllegalStateException("Password name: " + passwordName + " already exists.");
        }
        _passwordNameToEncryptedPasswordMap.put(passwordName, encryptedPassword);
        saveChanges();
    }

    public void removePassword(String passwordName) throws FileNotFoundException {
        _passwordNameToEncryptedPasswordMap.remove(passwordName);
        saveChanges();
    }

    public void clearAllData() throws FileNotFoundException {
        _masterPasswordHash = null;
        _passwordNameToEncryptedPasswordMap.clear();
        saveChanges();
    }

    private void saveChanges() throws FileNotFoundException {
        File passwordFile = new File(_passwordFilename);
        PrintWriter pw = new PrintWriter(passwordFile);
        if (_masterPasswordHash != null) {
            pw.println(_masterPasswordHash);
            pw.println();
            for (Map.Entry<String, String> entry : _passwordNameToEncryptedPasswordMap.entrySet()) {
                pw.println(entry.getKey() + KEY_VALUE_SEPARATOR + entry.getValue());
            }
        }
        pw.close();
    }

    public static void main(String[] args) throws IOException {
        PasswordFileManager pfm = new PasswordFileManager("passwd");
        System.out.println(pfm.getMasterPasswordHashValue());
        System.out.println(pfm.getAllPasswordNames());
        System.out.println(pfm.getEncryptedPassword("key2"));
    }
}
