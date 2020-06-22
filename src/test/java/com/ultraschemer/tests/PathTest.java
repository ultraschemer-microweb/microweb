package com.ultraschemer.tests;

import com.ultraschemer.microweb.utils.Path;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathTest {
    @Test
    public void evaluateEquivalentPathTest() throws Throwable {
        assertTrue(Path.areEquivalent("/", "/"));
        assertTrue(Path.areEquivalent("/abc//////////", "/abc"));
        assertTrue(Path.areEquivalent("/abc/def", "/abc/def"));
        assertTrue(Path.areEquivalent("/abc/def/ghi", "/abc/def/ghi"));
        assertTrue(Path.areEquivalent("/:abc", "/abc"));
        assertTrue(Path.areEquivalent("/:abc/def", "/abc/def"));
        assertTrue(Path.areEquivalent("/abc/:def/ghi", "/abc/def/ghi"));
        assertTrue(Path.areEquivalent("/abc", "/:abc"));
        assertTrue(Path.areEquivalent("/abc/def", "/:abc/def"));
        assertTrue(Path.areEquivalent("/abc/def/ghi", "/abc/:def/ghi"));
        assertTrue(Path.areEquivalent("/:abc", "/abc"));
        assertTrue(Path.areEquivalent("/:abc/def", "/:abc/def"));
        assertTrue(Path.areEquivalent("/abc/:def/ghi", "/abc/:def/ghi"));
        assertTrue(Path.areEquivalent("/:abc", "/abc"));
        assertTrue(Path.areEquivalent("/:abc/def", "/abc/:def"));
        assertTrue(Path.areEquivalent("/abc/:def/ghi", "/abc/def/:ghi"));
        assertFalse(Path.areEquivalent("/a", "/"));
        assertFalse(Path.areEquivalent("/abcd", "/abc"));
        assertFalse(Path.areEquivalent("/abc/defd", "/abc/def"));
        assertFalse(Path.areEquivalent("/abc/def/ghij", "/abc/def/ghi"));
        assertTrue(Path.areEquivalent("/:abc", "/abcPossoColocarualquerCoisaAqui"));
        assertFalse(Path.areEquivalent("/:abc/ddef", "/abc/def"));
        assertFalse(Path.areEquivalent("/abc/:def/gghi", "/abc/def/ghi"));
        assertFalse(Path.areEquivalent("/abc", "/a:abc"));
        assertFalse(Path.areEquivalent("/abc/cdef", "/:abc/def"));
        assertFalse(Path.areEquivalent("/abc/def/ghi", "/abcd/:def/ghi"));
        assertFalse(Path.areEquivalent("/:abc", "/abc/b"));
        assertFalse(Path.areEquivalent("/:abc/def/g", "/:abc/def"));
        assertFalse(Path.areEquivalent("/abcj/:def/ghi", "/abc/:def/ghi"));
        assertFalse(Path.areEquivalent("/:abc/d", "/abc"));
        assertFalse(Path.areEquivalent("/:abc/def/o", "/abc/:def"));
        assertTrue(Path.areEquivalent("/abc/:def/ghik", "/abc/def/:ghi"));
    }
}
