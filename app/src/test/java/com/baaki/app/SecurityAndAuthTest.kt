package com.baaki.app

import com.baaki.app.data.repository.UserSession
import org.junit.Assert.assertEquals
import org.junit.Test

class SecurityAndAuthTest {

    @Test
    fun testUserSessionDefaultState() {
        val session = UserSession(
            email = "rajesh@sharmapackaging.in",
            fullName = "Rajesh Sharma",
            businessName = "Sharma Packaging",
            phone = "9876543210",
            isLoggedIn = true
        )
        assertEquals(true, session.isLoggedIn)
        assertEquals("Sharma Packaging", session.businessName)
    }

    @Test
    fun testLogoutSessionStateTransition() {
        val initialSession = UserSession("test@example.com", "Test User", "Test Biz", "9876543210", true)
        val loggedOutSession = initialSession.copy(isLoggedIn = false)
        assertEquals(false, loggedOutSession.isLoggedIn)
    }
}
