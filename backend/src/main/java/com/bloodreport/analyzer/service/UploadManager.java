package com.bloodreport.analyzer.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadManager {

    private final Map<String, String> activeUploads = new ConcurrentHashMap<>();

    /**
     * Attempts to start a new upload for the given identifier
     * 
     * @param identifier Unique identifier for the upload (e.g., session ID or user
     *                   IP)
     * @param requestId  Unique ID for this specific request
     * @return true if upload can proceed, false if one is already in progress
     */
    public boolean startUpload(String identifier, String requestId) {
        String existing = activeUploads.putIfAbsent(identifier, requestId);
        return existing == null;
    }

    /**
     * Marks an upload as complete
     * 
     * @param identifier The identifier that was used to start the upload
     * @param requestId  The request ID to verify it matches
     */
    public void completeUpload(String identifier, String requestId) {
        activeUploads.remove(identifier, requestId);
    }

    /**
     * Checks if an upload is currently in progress for the identifier
     */
    public boolean isUploadInProgress(String identifier) {
        return activeUploads.containsKey(identifier);
    }

    /**
     * Gets the current active request ID for the identifier
     */
    public String getActiveRequestId(String identifier) {
        return activeUploads.get(identifier);
    }

    /**
     * Forces cleanup of an upload (use for error scenarios)
     */
    public void forceCleanup(String identifier) {
        activeUploads.remove(identifier);
    }

    /**
     * Gets the number of currently active uploads
     */
    public int getActiveUploadCount() {
        return activeUploads.size();
    }
}
