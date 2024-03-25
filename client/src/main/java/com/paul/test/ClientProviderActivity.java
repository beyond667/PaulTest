package com.paul.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class ClientProviderActivity extends Activity {

    private String uri = "content://ProgramAndroid/person";
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        mEditText = (EditText) findViewById(R.id.ed_name);
    }

    @SuppressLint("Range")
    public void qureyData(View view) {
        String name = null;
        Cursor cursor = getContentResolver().query(Uri.parse(uri), null, null, null, null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        mEditText.setText(name);
    }

    public void insertData(View view) {
        String editName = mEditText.getText().toString();
        ContentValues values = new ContentValues();
        values.put("name", editName);

        Uri result = getContentResolver().insert(Uri.parse(uri), values);
//             注意 ： 此条添加上才ContentObserver可以监听数据库改变
        getContentResolver().notifyChange(Uri.parse(uri), null);
        long parseid = ContentUris.parseId(result);
        if (parseid > 0) {
            Toast.makeText(ClientProviderActivity.this, "保存成功", Toast.LENGTH_LONG).show();
            mEditText.setText("");
        }

    }

}