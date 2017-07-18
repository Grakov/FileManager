package com.u1337.filemanager.FileManager;

import java.util.Comparator;

public class FSObjectCompator implements Comparator<FSObject> {

    @Override
    public int compare(FSObject a, FSObject b) {
        if (a.isDirectory() && b.isDirectory()) {
            return a.getName().compareTo(b.getName());
        }

        if (a.isDirectory() && !b.isDirectory()) {
            return -1;
        }

        if (b.isDirectory() && !a.isDirectory()) {
            return 1;
        }

        return a.getName().compareTo(b.getName());
    }
}