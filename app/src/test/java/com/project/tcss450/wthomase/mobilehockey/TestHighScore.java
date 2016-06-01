package com.project.tcss450.wthomase.mobilehockey;


import com.project.tcss450.wthomase.mobilehockey.model.HighScore;

import junit.framework.TestCase;

import org.junit.Test;

public class TestHighScore extends TestCase {

    @Test
    public void testConstructor() {
        HighScore highscore = new HighScore("test", "45");
        assertNotNull(highscore);
    }

    @Test
    public void testCompareTo() {
        HighScore hs1 = new HighScore("test1", "45");
        HighScore hs2 = new HighScore("test2", "44");
        assertTrue(hs1.compareTo(hs2) > 0);
    }

    @Test
    public void testGetUser() {
        HighScore hs = new HighScore("test", "35");
        assertEquals("test", hs.getmUserId());
    }

    @Test
    public void testGetHighScore() {
        HighScore hs = new HighScore("test", "25");
        assertEquals("25", hs.getmHighScore());
    }

    @Test
    public void testSetHighScore() {
        HighScore hs = new HighScore("test", "20");
        try {
            hs.setmHighScore("eead;");
            fail("HighScore can accept scores with non-numeric characters");
        } catch (NumberFormatException e) {

        }
    }

    @Test
    public void testSetUserId() {
        HighScore hs = new HighScore("test", "31");
        hs.setmUserId("hello");
        assertEquals("hello", hs.getmUserId());
    }

}
