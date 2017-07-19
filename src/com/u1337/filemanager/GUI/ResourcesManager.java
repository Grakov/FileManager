package com.u1337.filemanager.GUI;

import javax.swing.*;
import java.net.URL;

public class ResourcesManager {

    public static final Icon CLOSED_DIRECTORY_ICON = UIManager.getIcon("Tree.closedIcon");
    public static final Icon OPENED_DIRECTORY_ICON = UIManager.getIcon("Tree.openIcon");
    public static final Icon FILE_ICON;
    public static final Icon FILE_DOCUMENT_ICON;
    public static final Icon FILE_MUSIC_ICON;
    public static final Icon FILE_IMAGE_ICON;
    public static final Icon FILE_VIDEO_ICON;
    public static final Icon HARD_DRIVE_ICON = UIManager.getIcon("FileView.hardDriveIcon");
    public static final Icon LOADING_ICON;

    public static final String BASE_URL = "/images/";

    public static URL getPngResource(String name)
    {
        String imageAddr = BASE_URL.concat(name).concat(".png");
        return ResourcesManager.class.getResource(imageAddr);
    }

    private static ImageIcon initResource(String name)
    {
        ImageIcon result = null;
        URL url = getPngResource(name);
        try {
            result = new ImageIcon(url);
        } catch (NullPointerException e) {
            System.out.println("Resource " + name + " not found. Package is broken :(");
            System.exit(0);
        }
        return result;
    }

    static {
        LOADING_ICON = initResource("clock");
        FILE_ICON = initResource("file");
        FILE_DOCUMENT_ICON = initResource("file-document");
        FILE_IMAGE_ICON = initResource("file-image");
        FILE_MUSIC_ICON = initResource("file-music");
        FILE_VIDEO_ICON = initResource("file-video");
    }

}
