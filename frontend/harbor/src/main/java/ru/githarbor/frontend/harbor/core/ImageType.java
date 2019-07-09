package ru.githarbor.frontend.harbor.core;

public class ImageType {

    public static boolean isImage(String extension) {
        return extension.equals("jpg") ||
                extension.equals("jpeg") ||
                extension.equals("gif") ||
                extension.equals("png") ||
                extension.equals("ico");
    }

    public static boolean isSvg(String extension) {
        return extension.equals("svg");
    }
}
