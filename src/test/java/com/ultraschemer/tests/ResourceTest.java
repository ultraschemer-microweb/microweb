package com.ultraschemer.tests;

import com.ultraschemer.microweb.utils.Resource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResourceTest {
    @Test
    public void evaluateEquivalentPathTest() {
        assertTrue(Resource.pathsAreEquivalent("/", "/"));
        assertTrue(Resource.pathsAreEquivalent("/abc//////////", "/abc"));
        assertTrue(Resource.pathsAreEquivalent("/abc/def", "/abc/def"));
        assertTrue(Resource.pathsAreEquivalent("/abc/def/ghi", "/abc/def/ghi"));
        assertTrue(Resource.pathsAreEquivalent("/:abc", "/abc"));
        assertTrue(Resource.pathsAreEquivalent("/:abc/def", "/abc/def"));
        assertTrue(Resource.pathsAreEquivalent("/abc/:def/ghi", "/abc/def/ghi"));
        assertTrue(Resource.pathsAreEquivalent("/abc", "/:abc"));
        assertTrue(Resource.pathsAreEquivalent("/abc/def", "/:abc/def"));
        assertTrue(Resource.pathsAreEquivalent("/abc/def/ghi", "/abc/:def/ghi"));
        assertTrue(Resource.pathsAreEquivalent("/:abc", "/abc"));
        assertTrue(Resource.pathsAreEquivalent("/:abc/def", "/:abc/def"));
        assertTrue(Resource.pathsAreEquivalent("/abc/:def/ghi", "/abc/:def/ghi"));
        assertTrue(Resource.pathsAreEquivalent("/:abc", "/abc"));
        assertTrue(Resource.pathsAreEquivalent("/:abc/def", "/abc/:def"));
        assertTrue(Resource.pathsAreEquivalent("/abc/:def/ghi", "/abc/def/:ghi"));
        assertFalse(Resource.pathsAreEquivalent("/a", "/"));
        assertFalse(Resource.pathsAreEquivalent("/abcd", "/abc"));
        assertFalse(Resource.pathsAreEquivalent("/abc/defd", "/abc/def"));
        assertFalse(Resource.pathsAreEquivalent("/abc/def/ghij", "/abc/def/ghi"));
        assertTrue(Resource.pathsAreEquivalent("/:abc", "/abcPossoColocarualquerCoisaAqui"));
        assertFalse(Resource.pathsAreEquivalent("/:abc/ddef", "/abc/def"));
        assertFalse(Resource.pathsAreEquivalent("/abc/:def/gghi", "/abc/def/ghi"));
        assertFalse(Resource.pathsAreEquivalent("/abc", "/a:abc"));
        assertFalse(Resource.pathsAreEquivalent("/abc/cdef", "/:abc/def"));
        assertFalse(Resource.pathsAreEquivalent("/abc/def/ghi", "/abcd/:def/ghi"));
        assertFalse(Resource.pathsAreEquivalent("/:abc", "/abc/b"));
        assertFalse(Resource.pathsAreEquivalent("/:abc/def/g", "/:abc/def"));
        assertFalse(Resource.pathsAreEquivalent("/abcj/:def/ghi", "/abc/:def/ghi"));
        assertFalse(Resource.pathsAreEquivalent("/:abc/d", "/abc"));
        assertFalse(Resource.pathsAreEquivalent("/:abc/def/o", "/abc/:def"));
        assertTrue(Resource.pathsAreEquivalent("/abc/:def/ghik", "/abc/def/:ghi"));
    }

    @Test
    public void resourceEquivalentToPathTest() {
        assertTrue(Resource.resourceIsEquivalentToPath("GET /#", "/", "get"));
        assertTrue(Resource.resourceIsEquivalentToPath("POST /abc/:param#", "/abc/123", "Post"));
        assertTrue(Resource.resourceIsEquivalentToPath("PUT /abc/:p1/:p2#", "/abc/123/def", "PUT"));
        assertTrue(Resource.resourceIsEquivalentToPath("PUT /abc/:p1/:p2#", "/abc/:param1/:param2", "PUT"));
        assertFalse(Resource.resourceIsEquivalentToPath("PUT /abc/:p1/:p2/:p3#", "/abc/:param1/:param2", "PUT"));
    }
}
