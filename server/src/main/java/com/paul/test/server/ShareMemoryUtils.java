package com.paul.test.server;

import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;

public class ShareMemoryUtils {
    public static ParcelFileDescriptor getPfdFromMemoryFile(MemoryFile memoryFile) {
        ParcelFileDescriptor pfd = null;
        try {
            try {
                pfd = getParcelFileDescriptor(memoryFile);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                closeMemoryFile(memoryFile, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pfd;
    }

    public static MemoryFile getMemoryFile(final String name, final byte[] bytes) {
            MemoryFile memoryFile = null;
            try {
                memoryFile = new MemoryFile(name, bytes.length);
                memoryFile.allowPurging(true);
                memoryFile.writeBytes(bytes, 0, 0, bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                closeMemoryFile(memoryFile, null);
            }
        return memoryFile;
    }

    public static ParcelFileDescriptor getParcelFileDescriptor(MemoryFile memoryFile) {
        try {
            Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
            method.setAccessible(true);
            FileDescriptor fd = (FileDescriptor) method.invoke(memoryFile);
            return ParcelFileDescriptor.dup(fd);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeMemoryFile(MemoryFile memoryFile, ParcelFileDescriptor pfd) {
        if (pfd != null) {
            try {
                pfd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (memoryFile != null) {
            memoryFile.close();
        }
    }
}
