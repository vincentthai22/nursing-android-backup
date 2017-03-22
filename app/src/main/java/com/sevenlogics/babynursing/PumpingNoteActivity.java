package com.sevenlogics.babynursing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by vincent on 3/21/17.
 */

public class PumpingNoteActivity extends AppCompatActivity {

    static final String NOTE_KEY = "notekey";
    private EditText editText;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        editText = (EditText) findViewById(R.id.editText);
    }

    public void onClickHandler(View v){

        switch(v.getId()) {
            case R.id.doneTextView:
                Intent intent = getIntent();
                intent.putExtra(NOTE_KEY, editText.getText());
                setResult(RESULT_OK, intent);
                this.finish();
                return;
            case R.id.clearTextView:
                editText.setText("");
                return;

        }
    }

}
