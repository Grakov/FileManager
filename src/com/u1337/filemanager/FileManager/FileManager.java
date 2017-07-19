package com.u1337.filemanager.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public List<FSObject> mRoots = new ArrayList<>();

    public static final String[] IMAGE_FILES_EXTS = {"jpeg", "jpg", "png", "gif", "tiff", "svg"};
    public static final String[] MUSIC_FILES_EXTS = {"mp3", "wma", "aac", "flac", "wav", "ogg"};
    public static final String[] VIDEO_FILES_EXTS = {"mp4", "mpeg", "webm", "mkv", "avi", "wmv"};
    public static final String[] DOCUMENT_FILES_EXTS = {"doc", "docx", "txt", "rtf", "html", "htm", "pdf"};

    private FSObject current;
    public FSObject mRootsObject;
    private List<FSObject> clipboard;

    public static final int CLIPBOARD_COPY = 1;
    public static final int CLIPBOARD_MOVE = 2;
    public static final int CLIPBOARD_RESET = -1;

    private int clipboardType = CLIPBOARD_RESET;

    public FileManager()
    {
        File[] a = File.listRoots();
        for (File f : a)
        {
            mRoots.add(new FSObject(null, f, true));
        }

        mRootsObject = new FSObject(null, null);
        mRootsObject.setChildren(mRoots);
    }

    public FSObject getCurrent()
    {
        return current;
    }

    public void setCurrent(FSObject obj)
    {
        current = obj;
    }

    public List<FSObject> loadAndActivate(FSObject obj)
    {
        List<FSObject> res;
        res = this.load(obj);
        obj.setChildren(res);
        this.setCurrent(obj);
        return res;
    }

    public List<FSObject> load(FSObject obj)
    {
        List<FSObject> res = new ArrayList<>();
        File[] a = obj.getFile().listFiles();

        if (a != null) {
            for (File f : a) {
                if (f.isDirectory()) {
                    FSObject objD = new FSObject(obj, f);

                    res.add(objD);
                }
                else
                {
                    FSObject objF = new FSObject(obj, f);

                    res.add(objF);
                }
            }
        }

        res.sort(new FSObjectCompator());
        return res;
    }

    public static boolean makeFile(File current, String name)
    {
        File nFile = new File(current.getAbsolutePath().concat("/").concat(name));
        try {
            nFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean makeDir(File currentDir, String name)
    {
        File nFile = new File(currentDir.getAbsolutePath().concat("/").concat(name));
        return nFile.mkdir();
    }

    public boolean insert(FSObject currentDir, FSObject obj, int clipboardActionType)
    {
        File sourceFile = obj.getFile();
        String name = currentDir.getAbsolutePath().concat("/").concat(obj.getName());

        switch (clipboardActionType)
        {
            case CLIPBOARD_MOVE: return sourceFile.renameTo(new File(name));
            case CLIPBOARD_COPY: {
                try {
                    copy(sourceFile, new File(name));
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
        }

        return false;
    }

    public boolean delete(FSObject obj)
    {
        if (obj.isDirectory())
        {
            for (FSObject f : obj.getChildren())
            {
                boolean res = delete(f);
                if (!res)
                    return false;
            }
        }

        return obj.getFile().delete();
    }

    public void copy(File sourceLocation, File destinationLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, destinationLocation);
        } else {
            copyFile(sourceLocation, destinationLocation);
        }
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    public void setClipboard(List<FSObject> obj, int ctype)
    {
        clipboard = obj;
        clipboardType = ctype;
    }

    public List<FSObject> getClipboard()
    {
        return clipboard;
    }

    public void clearClipboard()
    {
        clipboard.clear();
        clipboardType = CLIPBOARD_RESET;
    }

    public int getClipboardType()
    {
        return clipboardType;
    }

}
