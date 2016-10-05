package com.samba;

import android.os.AsyncTask;
import android.util.Log;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * Created by shane on 9/23/16.
 */

public class sambaClient {
    public void run(){
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            SmbFile[] listFiles = {};
            try {
                String yourPeerPassword = "1234";
                String yourPeerName = "admin";
                String yourPeerIP = "10.1.1.91";
                String path = "smb://" + yourPeerIP;
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
                        null, yourPeerName, yourPeerPassword);
                Log.e("Connected", "Yes");
                SmbFile smbFile = new SmbFile(path, auth);
                /** Printing Information about SMB file which belong to your Peer **/
                String nameoffile = smbFile.getName();
                String pathoffile = smbFile.getPath();
                Log.e(nameoffile, pathoffile);
                listFiles = smbFile.listFiles();
                Log.e(nameoffile, String.valueOf(listFiles.length));
                for(int i = 0;i < listFiles.length;i++){
                    Log.e("PATH", listFiles[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Connected", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
