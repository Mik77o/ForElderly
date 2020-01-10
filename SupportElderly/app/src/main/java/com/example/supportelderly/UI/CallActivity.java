package com.example.supportelderly.UI;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.supportelderly.R;

import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;

/**
 * Klasa dotycząca możliwości wykonywania połączeń pod dowolny numer.
 */
public class CallActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQ_PICK_CONTACT = 1;

    private CardView callCV;
    private ImageView img;
    private EditText phoneNumberET;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        createCustomActionBar(actionBar, "Wybór numeru");

        setContentView(R.layout.activity_call);

        callCV = findViewById(R.id.call_number);
        callCV.setOnClickListener(this);

        img = findViewById(R.id.add_contact_from_list);
        img.setOnClickListener(this);

        phoneNumberET = findViewById(R.id.phone_number_edit_text);
        phoneNumberET.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_number:
                if (!phoneNumberET.getText().toString().isEmpty()) {
                    String tempStr = phoneNumberET.getText().toString().replaceFirst("^\\+48", "");
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tempStr)));
                }
                break;
            case R.id.add_contact_from_list:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactsData = data.getData();
                assert contactsData != null;
                CursorLoader loader = new CursorLoader(this, contactsData, null, null, null, null);
                Cursor c = loader.loadInBackground();
                assert c != null;
                c.moveToFirst();
                String number = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumberET.setText(number);
                phoneNumberET.setSelection(phoneNumberET.getText().length());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}




