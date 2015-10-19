package com.sun_post.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

public interface PasswordManager {

    /**
     * Given the plain master password, calculate its hash value and store it on
     * disk. If master password hash already exists on disk, throw exception.
     * 
     * @param masterPassword
     *            plain master password
     * @throws Exception
     */
    void createMasterPassword(String masterPassword) throws Exception;

    /**
     * Login with the given plain master password. If master password has not
     * been created, throw exception.
     * 
     * @param masterPassword
     *            the given master password to be checked
     * @return true, if login succeed; false, if login fail
     * @throws Exception
     */
    boolean login(String masterPassword) throws Exception;

    /**
     * Given the password name and plain password, encrypt the plain password
     * and store the encrypted password on disk. If has not login, throw
     * exception.
     * 
     * @param passwordName
     *            password name
     * @param plainPassword
     *            plain password to be encrypted
     * @throws Exception
     */
    void storePassword(String passwordName, String plainPassword) throws Exception;

    /**
     * Given password name, remove its password on disk. If given password does
     * not exist, do nothing. If has not login, throw exception.
     * 
     * @param passwordName
     *            given password name
     * @throws Exception
     */
    void removePassword(String passwordName) throws Exception;

    /**
     * Given password name, retrieve plain password. If the given password name
     * does not exist, return null. If has not login, throw exception.
     * 
     * @param passwordName
     *            given password name
     * @return the plain password
     * @throws Exception
     */
    String retrievePassword(String passwordName) throws Exception;

    /**
     * Get all the password names.
     * 
     * @return a set of all existing password names.
     */
    Set<String> getAllPasswordNames();

    /**
     * Clear all the data related to this master password.
     *
     */
    void removeUserAndData() throws Exception;

    /**
     * Return the master password already exists on disk or not.
     * 
     * @return true, if it exist; false, if it does not exist
     */
    boolean doesMasterPasswordExist();

    /**
     *
     * @return the name + extension of the password file
     */
    String getPasswordFileName();

    /**
     * Change the old master password to the new one. If user has not login, throw exception.
     *
     * @param newMasterPassword
     *              new master password
     */
    void changeMasterPassword(String newMasterPassword) throws Exception;

    /**
     * user logout
     */
    void logout();

}
