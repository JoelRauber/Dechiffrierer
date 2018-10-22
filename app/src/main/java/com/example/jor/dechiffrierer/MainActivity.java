package com.example.jor.dechiffrierer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{


    @SuppressLint("StaticFieldLeak")
    private static ImageView imgTakenPic;

    private static final int CAM_REQUEST = 1313;

    private String logmessage;

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
        if(requestCode == CAM_REQUEST)
        {
            super.onActivityResult(requestCode, resultCode, intent);

            Bitmap bitmap = (Bitmap) Objects.requireNonNull(intent.getExtras()).get("data");
            imgTakenPic.setImageBitmap(bitmap);
        }

        else return;
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
                return log();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private boolean log()
    {
        Intent intent = new Intent("ch.appquest.intent.LOG");
        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty())
        {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return false;
        }

        GetLogMessage();

        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);
        return true;
    }

    private void GetLogMessage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logmessage = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

}
