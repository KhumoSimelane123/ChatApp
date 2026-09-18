/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;


import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Login.java
 */
public class Login {

    // ---- Stored user details (set once registration succeeds) 
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    public Login() {
    }

    
    // 1. checkUserName()
    // Rule: must contain an underscore AND be no more than 5 characters long
    
    public boolean checkUserName(String username) {
        boolean result;

        if (username == null) {
            result = false;
        } else if (!username.contains("_")) {
            result = false;
        } else if (username.length() > 5) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

   
    // 2. checkPasswordComplexity()
    // Rule: at least 8 chars, 1 capital, 1 number, 1 special character
    
    public boolean checkPasswordComplexity(String password) {
        boolean result;

        if (password == null) {
            result = false;
        } else if (password.length() < 8) {
            result = false;
        } else {
            boolean hasCapital = false;
            boolean hasDigit = false;
            boolean hasSpecial = false;

            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasCapital = true;
                } else if (Character.isDigit(c)) {
                    hasDigit = true;
                } else if (!Character.isLetterOrDigit(c)) {
                    hasSpecial = true;
                }
            }

            result = hasCapital && hasDigit && hasSpecial;
        }

        return result;
    }

   
    // 3. checkCellPhoneNumber()
    // Rule: must contain the international country code (+27) followed by
    // the number, with the number portion no more than ten characters long.
    
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        boolean result;

        if (cellPhoneNumber == null) {
            result = false;
        } else {
            // Matches "+27" followed by 7 to 10 digits, nothing else
            String regex = "^\\+27[0-9]{7,10}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cellPhoneNumber);
            result = matcher.matches();
        }

        return result;
    }

    // 4. registerUser()
    
    public String registerUser(String username, String password, String cellPhoneNumber,
                                String firstName, String lastName) {

        String result;

        if (!checkUserName(username)) {
            result = "Username is not correctly formatted; please ensure that your username "
                   + "contains an underscore and is no more than five characters in length.";
        } else if (!checkPasswordComplexity(password)) {
            result = "Password is not correctly formatted; please ensure that the password "
                   + "contains at least eight characters, a capital letter, a number, and a "
                   + "special character.";
        } else if (!checkCellPhoneNumber(cellPhoneNumber)) {
            result = "Cell phone number is incorrectly formatted or does not contain an "
                   + "international code; please correct the number and try again.";
        } else {
            // All checks passed - store the details for later login
            this.username = username;
            this.password = password;
            this.cellPhoneNumber = cellPhoneNumber;
            this.firstName = firstName;
            this.lastName = lastName;

            result = "Username successfully captured. Password successfully captured. "
                   + "Cell phone number successfully captured. Registration successful.";
        }

        return result;
    }

    
    // 5. loginUser()
    
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        boolean result;

        if (username == null || password == null) {
            result = false; // no one has registered yet
        } else if (!username.equals(enteredUsername)) {
            result = false;
        } else if (!password.equals(enteredPassword)) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    
    // 6. returnLoginStatus()
    
    public String returnLoginStatus(boolean loginSuccess) {
        if (loginSuccess) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
 