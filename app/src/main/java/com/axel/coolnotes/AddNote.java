package com.axel.coolnotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddNote extends AppCompatActivity {

    private EditText title;
    private EditText desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_note);

        title = (EditText)findViewById(R.id.userTitle_Add);
        desc = (EditText)findViewById(R.id.userDesc_Add);
        /* TODO добавить поддержку изображений 24.04.2016 и проверку данных, оповещения о том что не заполненно в snackBar */
    }

    public void onAddNote(View v)
    {
        DBHelper.insertNote(this,title.getText().toString(),desc.getText().toString(),"resource");
        //добавить EventBus объявление что данные обновились
        finish();
    }
}
