/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginTest.java
 */
public class LoginTest {

    
    // assertTrue / assertFalse tests - boolean check methods
    

    @Test
    public void testUserNameCorrectlyFormatted() {
        Login login = new Login();
        // Test Data: "kyl_1" -> contains underscore, 5 characters long
        assertTrue(login.checkUserName("kyl_1"));
    }
    
    @Test
public void testUserNameExactlyFiveCharactersWithUnderscore() {
    Login login = new Login();
    // Boundary case: exactly 5 characters, contains underscore - should pass
    assertTrue(login.checkUserName("ab_de"));
}

@Test
public void testUserNameSixCharactersWithUnderscore() {
    Login login = new Login();
    // Boundary case: 6 characters (one over the limit), contains underscore - should fail
    assertFalse(login.checkUserName("ab_def"));
}
    @Test
    public void testUserNameIncorrectlyFormatted() {
        Login login = new Login();
        // Test Data: "kyle!!!!!!" -> no underscore, too long
        assertFalse(login.checkUserName("kyle!!!!!!"));
    }

    @Test
    public void testPasswordMeetsComplexity() {
        Login login = new Login();
        // Test Data: "Ch&&sec@ke99!"
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        Login login = new Login();
        // Test Data: "password"
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCellPhoneNumberCorrectlyFormatted() {
        Login login = new Login();
        // Test Data: +27838968976
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormatted() {
        Login login = new Login();
        // Test Data: 08966553 (no country code)
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testLoginSuccessful() {
        Login login = new Login();
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        Login login = new Login();
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.loginUser("kyl_1", "WrongPassword1!"));
    }

    
    // assertEquals tests - message-returning methods
    // NOTEregisterUser() checks username, then password, then
    // cell phone number in that order.

    @Test
    public void testRegisterUserUsernameIncorrectlyFormatted() {
        Login login = new Login();
        String result = login.registerUser("kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertEquals("Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.", result);
    }

    @Test
    public void testRegisterUserPasswordDoesNotMeetComplexity() {
        Login login = new Login();
        // Username is valid here so the password message is what gets returned
        String result = login.registerUser("kyl_1", "password", "+27838968976",
                "Kyle", "Smith");
        assertEquals("Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, and a "
                + "special character.", result);
    }

    @Test
    public void testRegisterUserCellPhoneIncorrectlyFormatted() {
        Login login = new Login();
        // Username and password are valid here so the cell phone message is returned
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "08966553",
                "Kyle", "Smith");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code; please correct the number and try again.", result);
    }

    @Test
    public void testRegisterUserAllFieldsValid() {
        Login login = new Login();
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertEquals("Username successfully captured. Password successfully captured. "
                + "Cell phone number successfully captured. Registration successful.", result);
    }

    @Test
    public void testReturnLoginStatusSuccess() {
        Login login = new Login();
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        boolean loginResult = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(loginResult));
    }

    @Test
    public void testReturnLoginStatusFailure() {
        Login login = new Login();
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        boolean loginResult = login.loginUser("kyl_1", "WrongPassword1!");
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus(loginResult));
    }
}
