package com.sba.lexilearnbe.shared.infrastructure.storage;

public enum ImageUploadTarget {
    AUTHOR_PORTRAIT("lexilearn/authors"),
    WORK_COVER("lexilearn/works");

    private final String folder;

    ImageUploadTarget(String folder) {
        this.folder = folder;
    }

    public String folder() {
        return folder;
    }

    public boolean owns(String publicId) {
        return publicId != null && publicId.startsWith(folder + "/");
    }
}
