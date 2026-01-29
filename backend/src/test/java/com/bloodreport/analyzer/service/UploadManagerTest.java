package com.bloodreport.analyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UploadManagerTest {

    private UploadManager uploadManager;

    @BeforeEach
    void setUp() {
        uploadManager = new UploadManager();
    }

    @Test
    void testStartUpload_Success() {
        boolean started = uploadManager.startUpload("user1", "request1");
        assertTrue(started);
        assertTrue(uploadManager.isUploadInProgress("user1"));
    }

    @Test
    void testStartUpload_ConcurrentBlocked() {
        uploadManager.startUpload("user1", "request1");

        boolean secondStart = uploadManager.startUpload("user1", "request2");

        assertFalse(secondStart, "Second upload should be blocked");
        assertEquals("request1", uploadManager.getActiveRequestId("user1"));
    }

    @Test
    void testCompleteUpload() {
        uploadManager.startUpload("user1", "request1");
        uploadManager.completeUpload("user1", "request1");

        assertFalse(uploadManager.isUploadInProgress("user1"));
        assertNull(uploadManager.getActiveRequestId("user1"));
    }

    @Test
    void testForceCleanup() {
        uploadManager.startUpload("user1", "request1");
        uploadManager.forceCleanup("user1");

        assertFalse(uploadManager.isUploadInProgress("user1"));
    }

    @Test
    void testGetActiveUploadCount() {
        uploadManager.startUpload("user1", "request1");
        uploadManager.startUpload("user2", "request2");

        assertEquals(2, uploadManager.getActiveUploadCount());

        uploadManager.completeUpload("user1", "request1");

        assertEquals(1, uploadManager.getActiveUploadCount());
    }

    @Test
    void testMultipleUsersCanUploadSimultaneously() {
        assertTrue(uploadManager.startUpload("user1", "request1"));
        assertTrue(uploadManager.startUpload("user2", "request2"));

        assertTrue(uploadManager.isUploadInProgress("user1"));
        assertTrue(uploadManager.isUploadInProgress("user2"));
    }
}
