package com.shoestore.common.enums.media;

public enum MediaFolder {
    CATEGORIES("shoestore/categories"),
    BRANDS("shoestore/brands"),
    PRODUCTS("shoestore/products"),
    GENERAL("shoestore/general");

    private final String path;

    MediaFolder(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}