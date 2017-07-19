package com.u1337.filemanager.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FSObject {

    private File mFile;
    private FSObject  parent;
    private boolean mRoot = false;
    private List<FSObject> mSubTree = new ArrayList<>();
    private boolean isDelayed = false;
    private boolean isOpened = false;

    public FSObject(FSObject parentDir, File f)
    {
        mFile = f;
        parent = parentDir;
    }

    public FSObject(FSObject parentDir, File f, boolean isRoot)
    {
        mFile = f;
        parent = parentDir;
        mRoot = isRoot;
    }

    public File getFile()
    {
        return mFile;
    }

    public String getAbsolutePath()
    {
        return mFile.getAbsolutePath();
    }

    public String getName()
    {
        if (this.isRoot())
            return getPath();
        else
            return mFile.getName();
    }

    public FSObject getParent()
    {
        return parent;
    }

    public String getPath()
    {
        return mFile.getPath();
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isRoot()
    {
        return mRoot;
    }

    public boolean isFile()
    {
        return getFile().isFile();
    }

    public boolean isDirectory()
    {
        return getFile().isDirectory();
    }

    public List<FSObject> getChildren()
    {
        return mSubTree;
    }

    public void setChildren(List<FSObject> fs)
    {
        mSubTree = fs;
    }

    public void setDelayedStatus(boolean status)
    {
        isDelayed = status;
    }

    public boolean getDelayedStatus()
    {
        return isDelayed;
    }

    public void setOpenedStatus(boolean status)
    {
        isOpened = status;
    }

    public boolean getOpenedStatus()
    {
        return isOpened;
    }

    public String getExtension()
    {
        String s = getAbsolutePath();
        String file = s.substring(s.lastIndexOf(File.separatorChar));
        int index = file.lastIndexOf(".");
        if (index >= 0) {
            return file.substring(index + 1).toLowerCase();
        } else {
            return "";
        }
    }
}
