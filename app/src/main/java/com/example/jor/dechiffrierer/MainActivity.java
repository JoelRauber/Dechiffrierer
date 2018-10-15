package com.example.jor.dechiffrierer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
    {
    private static final int SCAN_QR_CODE_REQUEST_CODE;

    @SuppressLint("StaticFieldLeak")
    private static ImageView imgTakenPic;

    private static final int CAM_REQUEST;

    static
    {
        SCAN_QR_CODE_REQUEST_CODE = 0;
        CAM_REQUEST = 1313;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPicture;
        btnPicture = findViewById(R.id.btnTakePicture);

        Button btnDechiffrieren;
        btnDechiffrieren = findViewById(R.id.btnDechiffrieren);

        imgTakenPic = findViewById(R.id.imageView);

        btnDechiffrieren.setOnClickListener(new btnDechiffrierenClicker(imgTakenPic));
        btnDechiffrieren.setVisibility(View.GONE);

        btnPicture.setOnClickListener(new btnTakePhotoClicker(btnDechiffrieren));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == SCAN_QR_CODE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String logMsg = intent.getStringExtra("SCAN_RESULT");
                log(logMsg);
            }
        }
        else
            {
            super.onActivityResult(requestCode, resultCode, intent);
            if(requestCode == CAM_REQUEST) {
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(intent.getExtras()).get("data");
                imgTakenPic.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    class btnTakePhotoClicker implements Button.OnClickListener
    {
        Button button;

        public btnTakePhotoClicker(Button button)
        {
            this.button = button;
        }

        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAM_REQUEST);
            button.setVisibility(View.VISIBLE);
        }
    }

    class btnDechiffrierenClicker implements Button.OnClickListener
    {
        private ImageView imageView;
        public btnDechiffrierenClicker(ImageView imageView)
        {
            this.imageView = imageView;
        }

        @Override
        public void onClick(View view)
        {
            Bitmap bitmap =((BitmapDrawable)imageView.getDrawable()).getBitmap();
            applyFilter(bitmap);
            imageView.setImageBitmap(bitmap);
        }

        private Bitmap applyFilter(Bitmap bitmap)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] data = new int[width * height];

            bitmap.getPixels(data, 0, width, 0, 0, width, height);

            ArrayList<Integer> pixelsAR = new ArrayList( );
            for (int pixel :data)
            {
                pixel = Color.red(pixel);
                pixelsAR.add(pixel);
            }

            int[] pixels = convertIntegers(pixelsAR);

            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        }

        public int[] convertIntegers(List<Integer> integers)
        {
            int[] ret = new int[integers.size()];
            for (int i=0; i < ret.length; i++)
            {
                ret[i] = integers.get(i).intValue();
            }
            return ret;
        }
    }

    private void log(String qrCode)
    {
        Intent intent = new Intent("ch.appquest.intent.LOG");
        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty())
        {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        String logmessage = "{\n" + "  \"task\": \"Metalldetektor\",\n" + "  \"solution\": \" "+ qrCode+" \"\n" +"}";
        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);
    }

}
