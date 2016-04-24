package com.axel.coolnotes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class EditNote extends AppCompatActivity {

    public static final int REQUEST = 1;
    private String mId,newTitle,newDesc,newRes;
    private EditText mTitleEdit;
    private EditText mDescEdit;
    private ImageView mImgEdit;
    private String mSavePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mTitleEdit = (EditText)findViewById(R.id.userTitle_changed);
        mDescEdit = (EditText)findViewById(R.id.userDesc_changed);
        mImgEdit = (ImageView)findViewById(R.id.image_change);

        mSavePath = this.getCacheDir().toString();

        Intent intent = getIntent();
        mId = intent.getStringExtra(DetailNote.EXTRA_ID);
        Cursor cursor = DBHelper.getNoteByID(this, mId);
        cursor.moveToFirst();

        assert mTitleEdit != null;
        mTitleEdit.setText(cursor.getString(1));
        assert mDescEdit != null;
        mDescEdit.setText(cursor.getString(2));

        if (cursor.getString(3)!=DetailNote.PLACEHOLDER)
        {
            try {
                mImgEdit.setImageURI(Uri.parse(cursor.getString(3)));
            }
            catch (Exception e)
            {
                mImgEdit.setImageResource(R.drawable.placeholder);
                Toast.makeText(this,"Problem with loading image for it note, maybe it was deleted or moved", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            mImgEdit.setImageResource(R.drawable.placeholder);
        }
    }

    public void onImageViewClick(View view) {
        Log.d(MainActivity.LOG_TAG, "we in onImageViewClick");
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, REQUEST);
    }

    public void onSaveChanges(View v)
    {
        String newPath = savePicture(mImgEdit,mSavePath);
        DBHelper.updateNoteByID(this,mId,mTitleEdit.getText().toString(),mDescEdit.getText().toString(),newPath);
        Log.d(MainActivity.LOG_TAG,"we save in "+newPath+" photo");
        finish();
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
            mImgEdit.setImageBitmap(img);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            Toast.makeText(this, "Some problem " + e.getMessage(), Toast.LENGTH_LONG).show();
            return e.getMessage();
        }
        return newPath;
    }
}
