package com.example.supportelderly.UI;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;

/**
 * Klasa odpowiedzialna za obsługę wysyłania wiadomości sms.
 */
public class SMSActivity extends AppCompatActivity {

    private static final int REQUEST_SMS = 0;
    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQUEST_READ_SMS = 2;

    private EditText phoneNumberEditText;
    private EditText messageEditText;
    private CardView cardVM;
    private TextView sendStatus;
    private TextView deliveryStatus;
    private CardView sendMessage, cancelMessage, deleteMessage;
    private ImageView pickContact;
    private ActionBar actionBar;

    private BroadcastReceiver sentStatusBroadcastReceiver, deliveredStatusBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        createCustomActionBar(actionBar, "Wyślij wiadomość");
        setContentView(R.layout.activity_sms);

        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendMessage = findViewById(R.id.send_button);
        cancelMessage = findViewById(R.id.cancel_button);
        deleteMessage = findViewById(R.id.delete_button);
        sendStatus = findViewById(R.id.message_status_text_view);
        deliveryStatus = findViewById(R.id.delivery_status_text_view);
        pickContact = findViewById(R.id.add_contact_image_view);
        cardVM = findViewById(R.id.show_messages);

        sendMessage.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
                if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                            REQUEST_SMS);
                    return;
                }
                sendMySMSToReceiver();
            }
        });


        cancelMessage.setOnClickListener(view -> onBackPressed());

        deleteMessage.setOnClickListener(view -> messageEditText.getText().clear());

        cardVM.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasSMSPermission = checkSelfPermission(Manifest.permission.READ_SMS);
                if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_SMS},
                            REQUEST_READ_SMS);
                    return;
                }
                Intent intent = getPackageManager()
                        .getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(this));
                startActivity(intent);
            }
        });


        pickContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(ContactsContract.Contacts.CONTENT_URI, ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, 1);
        });
    }

    public void sendMySMSToReceiver() {

        String phoneNumber = phoneNumberEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (phoneNumber.isEmpty()) {
            new BigToastClassHelper().
                    setBigToast("Proszę wprowadzić poprawny numer telefonu!", getApplicationContext());
        } else {

            SmsManager smsManager = SmsManager.getDefault();
            //Jeśli wiadomość jest zbyt długa, to jest ona podzielona.
            List<String> messagesDivided = smsManager.divideMessage(message);
            for (String msg : messagesDivided) {

                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                smsManager.sendTextMessage(phoneNumber, null, msg, sentIntent, deliveredIntent);

            }
        }
    }

    public void onResume() {
        super.onResume();
        sentStatusBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                AtomicReference<String> infoSMS = new AtomicReference<>("Nieznany błąd");
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        infoSMS.set("Wiadomość wysłana!");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        infoSMS.set("Generic Failure Error");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        infoSMS.set("Error : No Service Available");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        infoSMS.set("Error : Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        infoSMS.set("Error : Radio is off");
                        break;
                    default:
                        break;
                }
                sendStatus.setText(infoSMS.get());

            }
        };
        deliveredStatusBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                AtomicReference<String> infoDelivering = new AtomicReference<>("Wiadomość nie została dostarczona!");
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        infoDelivering.set("Wiadomość została dostarczona!");
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                deliveryStatus.setText(infoDelivering.get());
                phoneNumberEditText.setText("");
                messageEditText.setText("");
            }
        };
        registerReceiver(sentStatusBroadcastReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusBroadcastReceiver, new IntentFilter("SMS_DELIVERED"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusBroadcastReceiver);
        unregisterReceiver(deliveredStatusBroadcastReceiver);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new BigToastClassHelper().
                            setBigToast("Pozwolenie zaakceptowane, możesz teraz wysyłać smsy z tej aplikacji.", getApplicationContext());
                    sendMySMSToReceiver();

                } else {
                    new BigToastClassHelper().
                            setBigToast("Pozwolenie niezaakceptowane. Nie możesz wysyłać smsów.", getApplicationContext());
                }
                break;

            case REQUEST_READ_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new BigToastClassHelper().
                            setBigToast("Pozwolenie zaakceptowane, możesz teraz czytać smsy z tej aplikacji.", getApplicationContext());
                    sendMySMSToReceiver();

                } else {
                    new BigToastClassHelper().
                            setBigToast("Pozwolenie niezaakceptowane. Nie możesz czytać smsów.", getApplicationContext());
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                if (contactData == null) throw new AssertionError();
                Cursor cursor = new CursorLoader(getApplicationContext(), contactData, null, null, null, null).loadInBackground();
                if (cursor == null) throw new AssertionError();
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumberEditText.setText(number);
                phoneNumberEditText.setSelection(phoneNumberEditText.getText().length());
            }
        }
    }

}

