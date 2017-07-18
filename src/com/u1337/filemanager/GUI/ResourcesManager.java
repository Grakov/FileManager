package com.u1337.filemanager.GUI;

import javax.swing.*;
import java.net.URL;

public class ResourcesManager {

    public static final Icon CLOSED_DIRECTORY_ICON = UIManager.getIcon("Tree.closedIcon");
    public static final Icon OPENED_DIRECTORY_ICON = UIManager.getIcon("Tree.openIcon");
    public static final Icon FILE_ICON = UIManager.getIcon("Tree.leafIcon");
    public static final Icon HARD_DRIVE_ICON = UIManager.getIcon("FileView.hardDriveIcon");
    public static final Icon LOADING_ICON;

    public static final String BASE_URL = "/images/";
    public static final int DefaultTreeIconWidth = FILE_ICON.getIconWidth();

    public static URL getPngResource(String name)
    {
        String imageAddr = BASE_URL.concat(name).concat(".png");
        return ResourcesManager.class.getResource(imageAddr);
    }

    static {
        LOADING_ICON = new ImageIcon(getPngResource("clock"));
    }

}
