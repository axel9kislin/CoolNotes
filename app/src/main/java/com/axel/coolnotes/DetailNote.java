package com.axel.coolnotes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailNote extends AppCompatActivity {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String PLACEHOLDER = "placeholder";
    public String id;

    private RelativeLayout mLayout;
    private TextView mTitle;
    private TextView mDesc;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_note);

        mTitle = (TextView) findViewById(R.id.title_detail);
        mDesc = (TextView) findViewById(R.id.desc_detail);
        mImage = (ImageView)findViewById(R.id.image_detail);
        mLayout = (RelativeLayout)findViewById(R.id.relative_DetailActivity);

        Intent intent = getIntent();
        id = intent.getStringExtra(EXTRA_ID);
        Cursor cursor = DBHelper.getNoteByID(this, id);
        cursor.moveToFirst();

        mTitle.setText(cursor.getString(1));
        mDesc.setText(cursor.getString(2));

        if (!cursor.getString(3).equals(PLACEHOLDER))
        {
            try {
                mImage.setImageURI(Uri.parse(cursor.getString(3)));
            }
            catch (Exception e)
            {
                mImage.setImageResource(R.drawable.placeholder);
                Snackbar snackbar = Snackbar
                        .make(mLayout, "Problem with loading image for it note, maybe it was deleted or moved", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
        else mImage.setImageResource(R.drawable.placeholder);

        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void OnEvent(UpdateData event)
    {
        String data = event.getData();
        if (data.equals("editNote"))
        {
            Cursor cursor = DBHelper.getNoteByID(this, id);
            cursor.moveToFirst();
            mTitle.setText(cursor.getString(1));
            mDesc.setText(cursor.getString(2));
            if (!cursor.getString(3).equals(PLACEHOLDER))
            {
                try {
                    mImage.setImageURI(Uri.parse(cursor.getString(3)));
                }
                catch (Exception e)
                {
                    mImage.setImageResource(R.drawable.placeholder);
                    Snackbar snackbar = Snackbar
                            .make(mLayout, "Problem with loading image for it note, maybe it was deleted or moved", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
            else mImage.setImageResource(R.drawable.placeholder);
        }
    }

    public void btn_editClick(View v)
    {
        Intent intent = new Intent(this, EditNote.class);
        intent.putExtra(DetailNote.EXTRA_ID, id);
        startActivity(intent);
    }

    public void btn_deleteClick(View v)
    {
        DBHelper.deleteNote(this, id);
        finish();
        UpdateData upd = new UpdateData("deleteNote");
        EventBus.getDefault().post(upd);
        //оповещение что элемент удалили
    }

    @Override
    protected void onDestroy() {

        EventBus.getDefault().unregister(this);
        mLayout = null;

        super.onDestroy();
    }
}
