package com.axel.coolnotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNote extends AppCompatActivity {

    private EditText mTitle;
    private EditText mDesc;
    private ImageView mImage;
    private String mSavePath;
    private RelativeLayout mLayout;

    private boolean mUseDefaultImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_note);

        mSavePath = this.getCacheDir().toString();

        mTitle = (EditText)findViewById(R.id.userTitle_Add);
        mDesc = (EditText)findViewById(R.id.userDesc_Add);
        mImage = (ImageView)findViewById(R.id.image_add);
        mLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
    }

    public void onAddNote(View v)
    {
        if (validateData()) {
            if (mUseDefaultImage == false)
            {
                String newPath = savePicture(mImage, mSavePath);
                DBHelper.insertNote(this, mTitle.getText().toString(), mDesc.getText().toString(), newPath);
            } else {
                DBHelper.insertNote(this, mTitle.getText().toString(), mDesc.getText().toString(), DetailNote.PLACEHOLDER);
            }
            UpdateData upd = new UpdateData(NotesListFragment.ADD_TAG);
            EventBus.getDefault().post(upd);
            finish();
        }
    }

    public void onImageViewAddClick(View v)
    {
        int tmp=0;
        Log.d(MainActivity.LOG_TAG, "we in onImageViewClick");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    tmp);
        else
        {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, EditNote.REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap img = null;
        if (requestCode == EditNote.REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImage.setImageBitmap(img);
            mUseDefaultImage = false;
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
            Snackbar snackbar = Snackbar
                    .make(mLayout, "Not enough space in the internal memory", Snackbar.LENGTH_LONG);
            snackbar.show();
            return e.getMessage();
        }
        return newPath;
    }
    public boolean validateData()
    {
        if (mTitle.getText().length()==0)
        {
            Snackbar snackbar = Snackbar
                    .make(mLayout, "Please, add the title", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }else  {return true;}
    }

    @Override
    protected void onDestroy() {
        mLayout=null;
        mTitle=null;
        mImage=null;
        mDesc=null;
        mSavePath=null;//не знаю, надо ли строковые занулять
        super.onDestroy();
    }
}
