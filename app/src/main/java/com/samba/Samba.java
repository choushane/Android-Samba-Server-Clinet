package com.samba;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samba.nanohttp.HttpService;
import com.samba.smbserver.AndroidSMBConstants;
import com.samba.smbserver.AndroidSMBService;

import org.alfresco.jlan.app.XMLServerConfiguration;
import org.alfresco.jlan.netbios.server.NetBIOSNameServer;
import org.alfresco.jlan.server.NetworkServer;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.filesys.FilesystemsConfigSection;
import org.alfresco.jlan.smb.server.SMBServer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;


public class Samba extends AppCompatActivity implements AndroidSMBConstants{

    SmbFile[] fileaerray;
    String yourPeerPassword = "1234";
    String yourPeerName = "admin";
    String yourPeerIP = "10.1.1.91";
    String smbURL = "smb://" + yourPeerIP;
    String smbPath = "";

    private AndroidSMBService mService;
    private boolean mIsRunning = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            /*
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mService = ((AndroidSMBService.LocalBinder)service).getService();
            mService.getLogHandler().attach(Samba.this);
            */
            // Tell the user about this for our demo.
            Log.e("onServiceConnected", "smb_service_connected");
            mIsRunning = mService.getStatus() == RUNNING;


        }

        public void onServiceDisconnected(ComponentName className) {
            /*
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mService.getLogHandler().dettach(Samba.this);
            */
            mService = null;
            Log.e("onServiceConnected", "smb_service_disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samba);

        Button sambaclient_button = (Button) findViewById(R.id.button);
        sambaclient_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setContentView(R.layout.filelist);
                new LongOperation().execute("");
                onStartHttpServer();
            }
        });
        Button sambaseerver_button = (Button) findViewById(R.id.button2);
        sambaseerver_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Log.e("Samba Server", "Starting Service");
                onStartSambaServer();
            }
        });
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            SmbFile[] listFiles = {};
            try {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
                        null, yourPeerName, yourPeerPassword);
                Log.e("Connected", "Yes");
                SmbFile smbFile = new SmbFile(smbURL, auth);
                /** Printing Information about SMB file which belong to your Peer **/
                String nameoffile = smbFile.getName();
                String pathoffile = smbFile.getPath();
                Log.e(nameoffile, pathoffile);
                listFiles = smbFile.listFiles();
                Log.e(nameoffile, String.valueOf(listFiles.length));
                for(int i = 0;i < listFiles.length;i++){
                    Log.e("PATH", listFiles[i].toString());
                }
                fileaerray = listFiles;
                showFIleList(pathoffile);
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

    public void onStartHttpServer() {
        Intent intent = new Intent(this, HttpService.class);
        startService(intent);
    }
    public void onStartSambaServer() {
        Intent intent = new Intent(this, AndroidSMBService.class);
        mIsRunning=true;
        startService(intent);
    }

    public void alerttest(String llog){
        Log.e("Button Event", llog);
    }

    private void EnterDirectory(String enter_path){
        Log.e("EnterDirectory", enter_path);
        for (SmbFile f : fileaerray) {
            if(f.toString().equals(enter_path)) {
                try{
                    fileaerray = f.listFiles();
                    showFIleList(f.toString());
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Connected", e.getMessage());
                }

            }
        }
    }

/*
          //  Read txt

    private StringBuilder readFileContent(SmbFile sFile, StringBuilder builder) {
        BufferedReader reader = null;
        Log.e("readFileContent", "Start");
        try {
            reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(sFile)));
        } catch (SmbException e) {
            e.printStackTrace();
            Log.e("openfile", e.getMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("openfile", e.getMessage());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e("openfile", e.getMessage());
        }
        String lineReader = null;
        {
            try {
                while ((lineReader = reader.readLine()) != null) {
                    Log.e("readFileContent",lineReader );
                    builder.append(lineReader).append("\n");
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    Log.e("readFileContent", "Close");
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("readFileContent", e.getMessage());
                }
            }
        }
        return builder;
    }


    private void openFileFromSmb(String path){
        StringBuilder builder = null;

        Log.e("openFileFromSmb", path);
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
                    null, yourPeerName, yourPeerPassword);
            SmbFile sFile = new SmbFile(path, auth);
            try {
                builder = new StringBuilder();
                builder = readFileContent(sFile, builder);



                Log.e("", "========================== display all .txt info  here ==============");
                Log.e("", builder.toString());
                Log.e("", "========================== End  here ================================");

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }
*/
    public static String filepath;
    MediaPlayer mediaPlayer;
    private void openFileFromSmb(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //Uri uri = Uri.parse(Uri.fromFile(new File(Uri.parse(path).getPath())).getEncodedPath());

        File f1 = new File(path);
        String vlowerFileName = path.toLowerCase();

        if (vlowerFileName.endsWith("mpg")
                || vlowerFileName.endsWith("mp4")
                ){


            filepath = "http://127.0.0.1:30000" + path.substring(smbURL.length());
            Log.e("!!!!!!!!!!!",filepath);
            /*
            setContentView(R.layout.video);
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    Log.e("MediaPlayer", "First surface created!");

                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDisplay(surfaceHolder);
                        mediaPlayer.setScreenOnWhilePlaying(true);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                Log.e("MediaPlayer", "PLAY");
                                mediaPlayer.start();
                            }
                        });
                        mediaPlayer.setDataSource(filepath);
                    }catch (IOException e){
                        Log.e("MediaPlayer",e.toString());
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
            */
            intent.setDataAndType(Uri.parse(filepath), "video/*");
// 音樂
        }else if( vlowerFileName.endsWith("mp3") )
            intent.setDataAndType( Uri.fromFile(f1), "audio/*" );
// 影像
        else if( vlowerFileName.endsWith("bmp")
                || vlowerFileName.endsWith("gif")
                || vlowerFileName.endsWith("jpg")
                || vlowerFileName.endsWith("png")
                )
            openImageFile(path);
            //intent.setDataAndType( Uri.fromFile(f1), "image/*" );
// 文字檔
        else if( vlowerFileName.endsWith("txt")
                || vlowerFileName.endsWith("html")
                )
            intent.setDataAndType( Uri.fromFile(f1), "text/*" );
// Android APK
        else if( vlowerFileName.endsWith("apk")
                )
            intent.setDataAndType( Uri.fromFile(f1), "application/vnd.android.package-archive" );
// 其他
        else
            intent.setDataAndType( Uri.fromFile(f1), "application/*" );

// 切換到開啟的檔案
        startActivity(intent);

        //openImageFile(path);
    }


    private void openImageFile(String path){
        BufferedInputStream reader = null;
        setContentView(R.layout.image);
        Log.e("openFileFromSmb", path);
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.imageLayout);
        ImageView image = new ImageView(Samba.this);
        setContentView(relativeLayout);

        Button button_up = new Button(Samba.this);
        button_up.setText("Home");
        button_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.filelist);
                new LongOperation().execute("");
            }
        });
        relativeLayout.addView(button_up);
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
                    null, yourPeerName, yourPeerPassword);
            SmbFile sFile = new SmbFile(path, auth);
            try {

                reader = new BufferedInputStream(new SmbFileInputStream(sFile));
                Bitmap bMap = BitmapFactory.decodeStream(reader);
                image.setImageBitmap(bMap);
            } catch (final IOException e) {
            } finally {
                reader.close();
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
        relativeLayout.addView(image);
    }

    private void showFIleList(String nowPath){

        if(fileaerray.length > 0) {
            smbPath = nowPath;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout linearLayout = (LinearLayout)findViewById(R.id.layout1);
                    setContentView(linearLayout);
                    if(((LinearLayout) linearLayout).getChildCount() > 0)
                        ((LinearLayout) linearLayout).removeAllViews();

                    if(!smbPath.equals(smbURL)) {
                        Button button_up = new Button(Samba.this);
                        button_up.setText("Home");
                        button_up.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new LongOperation().execute("");
                                //alerttest(smbURL);
                                //EnterDirectory(smbURL);
                            }
                        });
                        linearLayout.addView(button_up);
                    }

                    TextView smbPathText = new TextView(Samba.this);
                    smbPathText.setText(smbPath);
                    Log.e("Now Path", smbPath);
                    linearLayout.addView(smbPathText);
                    linearLayout.setOrientation(linearLayout.VERTICAL);
                    for (int i = 0; i < fileaerray.length; i++) {
                        try {
                            Button button_item = new Button(Samba.this);
                            button_item.setText(fileaerray[i].toString());
                            if (fileaerray[i].isDirectory()) {
                                button_item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Button c = (Button) v;
                                        alerttest(c.getText().toString());
                                        EnterDirectory(c.getText().toString());
                                    }
                                });
                            }else{
                                button_item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Button c = (Button) v;
                                        alerttest(c.getText().toString());
                                        openFileFromSmb(c.getText().toString());
                                    }
                                });
                            }
                            linearLayout.addView(button_item);
                            /*
                            if (fileaerray[i].isDirectory()) {
                                Button button_item = new Button(Samba.this);
                                button_item.setText(fileaerray[i].toString());
                                button_item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Button c = (Button) v;
                                        alerttest(c.getText().toString());
                                        EnterDirectory(c.getText().toString());
                                    }
                                });
                                linearLayout.addView(button_item);
                            } else {
                                TextView textview_item = new TextView(Samba.this);
                                textview_item.setText(fileaerray[i].toString());
                                linearLayout.addView(textview_item);
                            }
                            */
                        }catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Connected", e.getMessage());
                        }

                    }
                }
            });
        }

    }

}
