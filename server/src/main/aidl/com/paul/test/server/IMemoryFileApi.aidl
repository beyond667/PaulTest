// IPaulAidlInterface.aidl
package com.paul.test.server;

// Declare any non-default types here with import statements

interface IMemoryFileApi {
 ParcelFileDescriptor getParcelFileDescriptor(String name);
    boolean setParcelFileDescriptor(String name, in ParcelFileDescriptor pfd);
    oneway void releaseParcelFileDescriptor(String type);
}