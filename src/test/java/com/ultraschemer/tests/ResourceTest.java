package com.ultraschemer.tests;

import com.ultraschemer.microweb.utils.Resource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResourceTest {
    @Test
    public void evaluateEquivalentPathTest() {
        assertTrue(Resource.pathAreEquivalent("/", "/"));
        assertTrue(Resource.pathAreEquivalent("/abc//////////", "/abc"));
        assertTrue(Resource.pathAreEquivalent("/abc/def", "/abc/def"));
        assertTrue(Resource.pathAreEquivalent("/abc/def/ghi", "/abc/def/ghi"));
        assertTrue(Resource.pathAreEquivalent("/:abc", "/abc"));
        assertTrue(Resource.pathAreEquivalent("/:abc/def", "/abc/def"));
        assertTrue(Resource.pathAreEquivalent("/abc/:def/ghi", "/abc/def/ghi"));
        assertTrue(Resource.pathAreEquivalent("/abc", "/:abc"));
        assertTrue(Resource.pathAreEquivalent("/abc/def", "/:abc/def"));
        assertTrue(Resource.pathAreEquivalent("/abc/def/ghi", "/abc/:def/ghi"));
        assertTrue(Resource.pathAreEquivalent("/:abc", "/abc"));
        assertTrue(Resource.pathAreEquivalent("/:abc/def", "/:abc/def"));
        assertTrue(Resource.pathAreEquivalent("/abc/:def/ghi", "/abc/:def/ghi"));
        assertTrue(Resource.pathAreEquivalent("/:abc", "/abc"));
        assertTrue(Resource.pathAreEquivalent("/:abc/def", "/abc/:def"));
        assertTrue(Resource.pathAreEquivalent("/abc/:def/ghi", "/abc/def/:ghi"));
        assertFalse(Resource.pathAreEquivalent("/a", "/"));
        assertFalse(Resource.pathAreEquivalent("/abcd", "/abc"));
        assertFalse(Resource.pathAreEquivalent("/abc/defd", "/abc/def"));
        assertFalse(Resource.pathAreEquivalent("/abc/def/ghij", "/abc/def/ghi"));
        assertTrue(Resource.pathAreEquivalent("/:abc", "/abcPossoColocarualquerCoisaAqui"));
        assertFalse(Resource.pathAreEquivalent("/:abc/ddef", "/abc/def"));
        assertFalse(Resource.pathAreEquivalent("/abc/:def/gghi", "/abc/def/ghi"));
        assertFalse(Resource.pathAreEquivalent("/abc", "/a:abc"));
        assertFalse(Resource.pathAreEquivalent("/abc/cdef", "/:abc/def"));
        assertFalse(Resource.pathAreEquivalent("/abc/def/ghi", "/abcd/:def/ghi"));
        assertFalse(Resource.pathAreEquivalent("/:abc", "/abc/b"));
        assertFalse(Resource.pathAreEquivalent("/:abc/def/g", "/:abc/def"));
        assertFalse(Resource.pathAreEquivalent("/abcj/:def/ghi", "/abc/:def/ghi"));
        assertFalse(Resource.pathAreEquivalent("/:abc/d", "/abc"));
        assertFalse(Resource.pathAreEquivalent("/:abc/def/o", "/abc/:def"));
        assertTrue(Resource.pathAreEquivalent("/abc/:def/ghik", "/abc/def/:ghi"));
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
