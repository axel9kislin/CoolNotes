package com.axel.coolnotes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailNote extends AppCompatActivity {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final int REQUEST = 1;
    public String id;
    private ImageView mImage;
    private String mSavePath;
    private static final String PLACEHOLDER = "placeholder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_note);

        TextView title = (TextView) findViewById(R.id.title_detail);
        TextView desc = (TextView) findViewById(R.id.desc_detail);
        mImage = (ImageView) findViewById(R.id.image_detail);

        mSavePath = this.getCacheDir().toString();

        Intent intent = getIntent();
        id = intent.getStringExtra(EXTRA_ID);
        Log.d(MainActivity.LOG_TAG,"we receive extra "+id);
        Cursor cursor = DBHelper.getNoteByID(this, id);
        cursor.moveToFirst();

        assert title != null;
        title.setText(cursor.getString(1));
        assert desc != null;
        desc.setText(cursor.getString(2));
        if (cursor.getString(3)==PLACEHOLDER)
        {
            mImage.setImageResource(R.drawable.placeholder);
        }
        else
        {
            try {
                mImage.setImageURI(Uri.parse(cursor.getString(3)));
            }
            catch (Exception e)
            {
                mImage.setImageResource(R.drawable.placeholder);
                Toast.makeText(this,"Problem with load it image, maybe it was deleted or moved",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void btn_editClick(View v)
    {
        Log.d(MainActivity.LOG_TAG, "we in edit button handler");
        //оповещение от EventBus о том поменяли
    }

    public void btn_deleteClick(View v)
    {
        DBHelper.deleteNote(this, id);
        finish();
        //оповещение что элемент удалили
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap img = null;
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImage.setImageBitmap(img);
            String test = savePicture(mImage,mSavePath);
            Log.d(MainActivity.LOG_TAG,"we have in test: "+test);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onImageViewClick(View view) {
        Log.d(MainActivity.LOG_TAG,"we in onImageViewClick");
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, REQUEST);
    }

    private String savePicture(ImageView iv, String folderToSave)
    {
        OutputStream fOut = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String newPath = null;
        try {
            File file = new File(folderToSave, imageFileName +".jpg");
            fOut = new FileOutputStream(file);

            iv.buildDrawingCache();
            Bitmap bitmap = iv.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            newPath = file.getAbsolutePath();
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Some problem "+e.getMessage(),Toast.LENGTH_LONG).show();
            return e.getMessage();
        }
        return newPath;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        mImage = null;
    }
}
