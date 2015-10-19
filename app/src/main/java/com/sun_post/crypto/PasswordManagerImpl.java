package com.sun_post.crypto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class PasswordManagerImpl implements PasswordManager {
    private final PasswordFileManager _passwordFileManager;
    private String _masterPassword;
    public static final String passwordFilename = "passwd.mypwman";

    public PasswordManagerImpl(String passwordFileName) throws IOException {
        _passwordFileManager = new PasswordFileManager(passwordFileName);
    }

    @Override
    public void createMasterPassword(String masterPassword) throws FileNotFoundException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        if (doesMasterPasswordExist()) {
            throw new IllegalStateException("Master password already exists.");
        }
        _passwordFileManager.setMasterPasswordHashValue(PasswordHash.createHash(masterPassword));
    }

    @Override
    public boolean login(String masterPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!doesMasterPasswordExist()) {
            throw new IllegalStateException("Master password has not been created.");
        }
        String correctHashValue = _passwordFileManager.getMasterPasswordHashValue();
        boolean success = PasswordHash.validatePassword(masterPassword, correctHashValue);
        if (success) {
            _masterPassword = masterPassword;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void storePassword(String passwordName, String plainPassword) throws Exception {
        if (_masterPassword == null) {
            throw new IllegalStateException("User has not login.");
        }
        String encryptedPassword = AES.encrypt(plainPassword, _masterPassword);
        _passwordFileManager.addPassword(passwordName, encryptedPassword);
    }

    @Override
    public void removePassword(String passwordName) throws FileNotFoundException {
        if (_masterPassword == null) {
            throw new IllegalStateException("User has not login.");
        }
        _passwordFileManager.removePassword(passwordName);
    }

    @Override
    public String retrievePassword(String passwordName) throws Exception {
        if (_masterPassword == null) {
            throw new IllegalStateException("User has not login.");
        }
        String encryptedOutput = _passwordFileManager.getEncryptedPassword(passwordName);
        return encryptedOutput == null ? encryptedOutput : AES.decrypt(encryptedOutput, _masterPassword);
    }

    @Override
    public Set<String> getAllPasswordNames() {
        return _passwordFileManager.getAllPasswordNames();
    }

    @Override
    public void removeUserAndData() throws FileNotFoundException {
        //data should be clearable without login
        _passwordFileManager.clearAllData();
    }

    @Override
    public boolean doesMasterPasswordExist() {
        String masterPasswordHashValue = _passwordFileManager.getMasterPasswordHashValue();
        if (masterPasswordHashValue == null || masterPasswordHashValue.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getPasswordFileName() {
        return passwordFilename;
    }

    @Override
    public void changeMasterPassword(String newMasterPassword) throws Exception {
        if (_masterPassword == null) {
            throw new IllegalStateException("User has not login.");
        }
        String oldMasterPassword = _masterPassword;
        _masterPassword = newMasterPassword;
        _passwordFileManager.setMasterPasswordHashValue(PasswordHash.createHash(newMasterPassword));
        //re-encrypt all password with new master password
        Set<String> allPasswordNames = new HashSet<String>();
        allPasswordNames.addAll(_passwordFileManager.getAllPasswordNames());
        for(String passwordName: allPasswordNames){
            String encryptedOutput = _passwordFileManager.getEncryptedPassword(passwordName);
            String plainPassword = AES.decrypt(encryptedOutput, oldMasterPassword);
            String newEncryptedOutput = AES.encrypt(plainPassword, newMasterPassword);
            _passwordFileManager.removePassword(passwordName);
            _passwordFileManager.addPassword(passwordName, newEncryptedOutput);
        }
    }

    @Override
    public void logout(){
        _masterPassword = null;
    }


    public static void main(String[] args) throws Exception {
        Scanner sysIn = new Scanner(System.in);
        PasswordManager passwordManager = new PasswordManagerImpl( passwordFilename);
        if (passwordManager.doesMasterPasswordExist()) {
            System.out.print("Please login with your master password: ");
            boolean success = passwordManager.login(sysIn.nextLine());
            while (!success) {
                System.out.print("Please login with your master password: ");
                success = passwordManager.login(sysIn.nextLine());
            }
        } else {
            System.out.print("Please enter your master password to get started: ");
            String masterPassword = sysIn.nextLine();
            passwordManager.createMasterPassword(masterPassword);
            passwordManager.login(masterPassword);
        }
        System.out.print("Please choose your operation x(change password)/s(store)/r(retrieve)/m(remove)/c(clear)/e(exit): ");
        String cmd = sysIn.nextLine();
        while (!cmd.equals("e")) {
            if (cmd.equals("s")) {
                System.out.print("Please enter your password name: ");
                String passwordName = sysIn.nextLine();
                System.out.print("Please enter its password: ");
                String password = sysIn.nextLine();
                passwordManager.storePassword(passwordName, password);
            } else if (cmd.equals("r")) {
                System.out.print("Please enter the password name whose password you want to retrieve: ");
                String passwordName = sysIn.nextLine();
                String plainPassword = passwordManager.retrievePassword(passwordName);
                System.out.println(passwordName + ": " + plainPassword);
            } else if (cmd.equals("m")) {
                System.out.print("Please enter the password you want to remove: ");
                passwordManager.removePassword(sysIn.nextLine());
            } else if (cmd.equals("c")) {
                passwordManager.removeUserAndData();
            }else if(cmd.equals("x")){
                System.out.print("Please enter the new master password: ");
                passwordManager.changeMasterPassword(sysIn.nextLine());
            } else {
                System.out.println("Invalid operation.");
            }
            System.out.println("All the passwords you stored: " + passwordManager.getAllPasswordNames());
            System.out.print("Please choose your operation x(change password)/s(store)/r(retrieve)/m(remove)/c(clear)/e(exit): ");
            cmd = sysIn.nextLine();
        }
        sysIn.close();
    }
}
