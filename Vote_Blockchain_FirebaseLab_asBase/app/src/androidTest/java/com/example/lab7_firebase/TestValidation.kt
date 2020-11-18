package com.example.lab7_firebase

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse




class TestValidation {
    // TODO: Test passwords
    // Add two tests for a valid password
    // Add two tests for an invalid password

    @Test
    fun testPasswords() {

        val validator = Validators()

        // test invalid passwords
        assertFalse(validator.validPassword("long passwords are much better for security however"))
        assertFalse(validator.validPassword("no."))

        // test valid passwords
        assertTrue(validator.validPassword("easyP4ss"))
        assertTrue(validator.validPassword("4nother"))

    }
}
