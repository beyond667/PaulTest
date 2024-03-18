package com.paul.test.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyMemoryShareService extends Service {
    private static final String TAG = "MyMemoryShareService";

    public MyMemoryShareService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "====================onBind");
//        return myBinder;
        return binder;
    }

    private final IMemoryFileApi.Stub binder = new IMemoryFileApi.Stub() {
        private ParcelFileDescriptor pfd;
        private  MemoryFile memoryFile;
        @Override
        public ParcelFileDescriptor getParcelFileDescriptor(String name) throws RemoteException {
            byte[] bytes = getBytes();
            memoryFile =ShareMemoryUtils.getMemoryFile(name,bytes);
            pfd = ShareMemoryUtils.getPfdFromMemoryFile(memoryFile);
            return pfd;
        }

        @Override
        public boolean setParcelFileDescriptor(String name, ParcelFileDescriptor pfd) throws RemoteException {

            return false;
        }

        @Override
        public void releaseParcelFileDescriptor(String type) throws RemoteException {
            ShareMemoryUtils.closeMemoryFile(memoryFile,pfd);
        }
    };

    private byte[] getBytes() {
        byte[] bytes = null;
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.bugreport);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String info = "";
            StringBuffer sb = new StringBuffer();
            while ((info = bufferedReader.readLine()) != null) {
                Log.i("info", info);
                sb.append(info);
            }
            bytes = sb.toString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }

}