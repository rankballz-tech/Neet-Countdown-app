package com.example

import com.example.model.CountdownState
import com.example.model.NeetConstants
import com.example.notification.NeetNotificationHelper
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CountdownTest {

    @Test
    fun testCountdownCalculation() {
        val state = CountdownState.calculate(isIstMode = true)
        assertNotNull(state)
        assertTrue("Days left should be positive before May 2027", state.days >= 0)
        assertTrue("Hours should be in 0..23", state.hours in 0..23)
        assertTrue("Minutes should be in 0..59", state.minutes in 0..59)
        assertTrue("Seconds should be in 0..59", state.seconds in 0..59)
        assertTrue("Progress fraction should be in 0..1", state.progressFraction in 0f..1f)
    }

    @Test
    fun testMilestonesOrder() {
        val milestones = NeetNotificationHelper.getUpcomingMilestones()
        assertTrue("Should have milestones configured", milestones.isNotEmpty())
        for (i in 0 until milestones.size - 1) {
            assertTrue(
                "Milestones should be in chronological order",
                milestones[i].epochMillis <= milestones[i + 1].epochMillis
            )
        }
    }
}
