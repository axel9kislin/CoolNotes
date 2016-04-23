package com.axel.coolnotes;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DetailNote extends AppCompatActivity {

    public static final String EXTRA_ID = "EXTRA_ID";
    public String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_note);

        TextView title = (TextView) findViewById(R.id.title_detail);
        TextView desc = (TextView) findViewById(R.id.desc_detail);

        Intent intent = getIntent();
        id = intent.getStringExtra(EXTRA_ID);
        Log.d(MainActivity.LOG_TAG,"we receive extra "+id);
        Cursor cursor = DBHelper.getNoteByID(this, id);
        cursor.moveToFirst();

        assert title != null;
        title.setText(cursor.getString(1));
        assert desc != null;
        desc.setText(cursor.getString(2));
        //todo прикрутить вставку изображения из бд 24.04.2016
    }

    public void btn_editClick(View v)
    {
        Log.d(MainActivity.LOG_TAG,"we in edit button handler");
        //оповещение от EventBus о том что элемент удалён
    }

    public void btn_deleteClick(View v)
    {
        DBHelper.deleteNote(this, id);
        finish();
    }
}
