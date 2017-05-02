package com.hanuor.onyx_sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.squarecamera.CameraActivity;
import com.hanuor.onyx.Onyx;
import com.hanuor.onyx.hub.OnTaskCompletion;
import com.hanuor.pearl.Pearl;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btn;
    TextView tv;
    private int REQUESTCODE_TAKE_PHOTO = 20;
    private String TAG = getClass().getSimpleName();
    private ProgressDialog pd;

    public static final String NOTIFICATION_CLASS_NAME = "Viciy";
    public static final String NOTIFICATION_TABLE_SENDER_ID = "sender_id";
    public static final String NOTIFICATION_TABLE_MESSAGE = "message";
    public static final String NOTIFICATION_TABLE_TITLE = "title";
    public static final String NOTIFICATION_TABLE_NOTIFICTION_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.ivy);
        tv = (TextView) findViewById(R.id.textView);
        pd = new ProgressDialog(MainActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Processing Request...");
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, REQUESTCODE_TAKE_PHOTO);
        final String m = "http://blogs-images.forbes.com/kurtbadenhausen/files/2015/06/fm-e1433941678273.jpg";
        processImage(m);

    }

    private void processImage(final String m) {
        Pearl.imageLoader(MainActivity.this, m, imageView, 0);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                pd.show();
*/
                Onyx.with(MainActivity.this).fromURL(m).getTagsfromApi(new OnTaskCompletion() {
                    @Override
                    public void onComplete(ArrayList<String> response) {
                        if (response != null) {
                            Log.d("Class", "" + response);
/*
                            pd.dismiss();
*/
                            tv.setText(response.toString());
                            tv.setTextColor(Color.parseColor("#ffffff"));
                        }
                    }
                });
            }
        });
    }

    private void uPloadParseFile(final Uri uri) {
        pd.show();
        final ParseFile file;
        try {
            file = new ParseFile(imagetoByteArray(new File(uri.getPath())), "image/jpg");
            Log.d(TAG, "Uploading: " + uri.getPath());
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(MainActivity.this, "Done! 1", Toast.LENGTH_SHORT).show();
                    if (e == null) {
                        savePostOBJ(file);
                    } else {
                        Log.d(TAG, "Error: " + e.getMessage(), e);
                        Snackbar.make(imageView, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void savePostOBJ(final ParseFile imageParseFile) {
        final ParseObject postObject = new ParseObject(NOTIFICATION_CLASS_NAME);
        postObject.put(NOTIFICATION_TABLE_MESSAGE, imageParseFile);
        postObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Done! 2", Toast.LENGTH_SHORT).show();
                if (e == null) {
                    processImage(imageParseFile.getUrl());
                } else {
                    Log.d(TAG, "Error: " + e.getMessage(), e);
                    Snackbar.make(imageView, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public static byte[] imagetoByteArray(File fileArg) throws IOException {
        File file = fileArg;
        FileInputStream fis = null;
        fis = new FileInputStream(file);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        assert fis != null;
        for (int readNum; (readNum = fis.read(buf)) != -1; ) {
            //Writes to this byte array output stream
            bos.write(buf, 0, readNum);
            System.out.println("read " + readNum + " bytes,");
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_TAKE_PHOTO) {
            processImage("file://" + data.getData().getPath());
            uPloadParseFile(data.getData());
        }
    }
}
