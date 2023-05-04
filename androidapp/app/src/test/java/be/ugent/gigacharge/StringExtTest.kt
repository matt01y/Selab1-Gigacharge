package be.ugent.gigacharge

import be.ugent.gigacharge.common.ext.isValidEmail
import be.ugent.gigacharge.common.ext.isValidPassword
import be.ugent.gigacharge.common.ext.passwordMatches

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtTest {

    /*@Test
    fun testValidEmail() {
        val myValidEmail = "johndoe@gmail.com"
        assertEquals(myValidEmail.isValidEmail(), true)
    }

    @Test
    fun testInValidEmail1() {
        val myInValidEmail = "johndoeatgmail.com"
        assertEquals(myInValidEmail.isValidEmail(), false)
    }

    @Test
    fun testInValidEmail2() {
        val myInValidEmail = "johndoe@gmaildotcom"
        assertEquals(myInValidEmail.isValidEmail(), false)
    }

    @Test
    fun testValidPassword() {
        val myValidPassword = "i_am_a_good_passwerd"
        assertEquals(myValidPassword.isValidPassword(), true)
    }
    */

    @Test
    fun testInValidPassword1() {
        val myValidPassword = " "
        assertEquals(myValidPassword.isValidPassword(), false)
    }

    @Test
    fun testInValidPassword2() {
        val myValidPassword = "0000"
        assertEquals(myValidPassword.isValidPassword(), false)
    }

    @Test
    fun testMatchingPassword() {
        val myPassword = "i_am_a_good_passwerd"
        assertEquals(myPassword.passwordMatches(myPassword), true)
    }

}