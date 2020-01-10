package com.example.supportelderly.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.example.supportelderly.Data.DatabaseHelperForAlarms;
import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.Helpers.CustomAlertDialogClassHelper;
import com.example.supportelderly.Model.Alarm;
import com.example.supportelderly.R;
import com.example.supportelderly.Service.LoadAlarmsService;
import com.example.supportelderly.Util.ViewUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.supportelderly.Helpers.ConverterForImageClassHelper.imageViewToBitmap;
import static com.example.supportelderly.UI.SettingsActivity.ModeAlarm;
import static com.example.supportelderly.UI.SettingsActivity.MyPreference;

/**
 * Klasa dotycząca widoku związanego z edycją alarmów.
 */
public final class EditSettingsForAlarmFragment extends Fragment {

    private final int REQUEST_CODE_GALLERY = 1;

    private TimePicker mTimePicker;
    private EditText mLabel;
    private CheckBox mMon, mTues, mWed, mThurs, mFri, mSat, mSun;
    private ImageView mMedicineImage;
    private SharedPreferences sharedPreferences;

    public static EditSettingsForAlarmFragment newInstance(Alarm alarm) {

        Bundle args = new Bundle();
        args.putParcelable(EditSettingsForAlarmActivity.ALARM_EXTRA, alarm);

        EditSettingsForAlarmFragment fragment = new EditSettingsForAlarmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        sharedPreferences = (Objects.requireNonNull(getActivity())).getSharedPreferences(MyPreference,
                Context.MODE_PRIVATE);
        final View v;
        if (sharedPreferences.contains(ModeAlarm)) {
            v = inflater.inflate(R.layout.fragment_add_edit_alarm, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_add_edit_alarm_spinner, container, false);
        }

        setHasOptionsMenu(true);

        final Alarm alarm = getAlarm();

        mTimePicker = v.findViewById(R.id.edit_alarm_time_picker);
        ViewUtils.setTimePickerTime(mTimePicker, alarm.getTime());

        mLabel = v.findViewById(R.id.edit_alarm_label);
        mLabel.setText(alarm.getLabel());

        CardView btnChoose = v.findViewById(R.id.choose_image_button);

        btnChoose.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), REQUEST_CODE_GALLERY);
        });

        mMedicineImage = v.findViewById(R.id.image_of_medicine);
        if (alarm.getImage() == null) {
            mMedicineImage.setImageResource(R.drawable.medicine_icon);
        } else {
            mMedicineImage.setImageBitmap(alarm.getImage());
        }

        mMon = v.findViewById(R.id.edit_alarm_mon);
        mTues = v.findViewById(R.id.edit_alarm_tues);
        mWed = v.findViewById(R.id.edit_alarm_wed);
        mThurs = v.findViewById(R.id.edit_alarm_thurs);
        mFri = v.findViewById(R.id.edit_alarm_fri);
        mSat = v.findViewById(R.id.edit_alarm_sat);
        mSun = v.findViewById(R.id.edit_alarm_sun);

        setDayCheckboxes(alarm);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_alarm_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Alarm getAlarm() {
        assert getArguments() != null;
        return getArguments().getParcelable(EditSettingsForAlarmActivity.ALARM_EXTRA);
    }

    private void setDayCheckboxes(Alarm alarm) {
        mMon.setChecked(alarm.getDay(Alarm.MON));
        mTues.setChecked(alarm.getDay(Alarm.TUES));
        mWed.setChecked(alarm.getDay(Alarm.WED));
        mThurs.setChecked(alarm.getDay(Alarm.THURS));
        mFri.setChecked(alarm.getDay(Alarm.FRI));
        mSat.setChecked(alarm.getDay(Alarm.SAT));
        mSun.setChecked(alarm.getDay(Alarm.SUN));
    }

    private void save() {

        final Alarm alarm = getAlarm();

        final Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, ViewUtils.getTimePickerMinute(mTimePicker));
        time.set(Calendar.HOUR_OF_DAY, ViewUtils.getTimePickerHour(mTimePicker));
        alarm.setTime(time.getTimeInMillis());
        alarm.setLabel(mLabel.getText().toString());
        alarm.setImage(imageViewToBitmap(mMedicineImage));
        alarm.setDay(Alarm.MON, mMon.isChecked());
        alarm.setDay(Alarm.TUES, mTues.isChecked());
        alarm.setDay(Alarm.WED, mWed.isChecked());
        alarm.setDay(Alarm.THURS, mThurs.isChecked());
        alarm.setDay(Alarm.FRI, mFri.isChecked());
        alarm.setDay(Alarm.SAT, mSat.isChecked());
        alarm.setDay(Alarm.SUN, mSun.isChecked());

        final int rowsUpdated = DatabaseHelperForAlarms.getInstance(getContext()).updateAlarm(alarm);

        if (rowsUpdated == 1) {
            new BigToastClassHelper().setBigToast("Zmiany zostały zapisane :)", getContext());
        } else {
            new BigToastClassHelper().setBigToast("Aktualizacja alarmu nie powiodła się :(", getContext());
        }


        LoadAlarmsService.AlarmReceiver.setReminderAlarm(getContext(), alarm);

        Objects.requireNonNull(getActivity()).finish();

    }

    private void delete() {

        final Alarm alarm = getAlarm();

        new CustomAlertDialogClassHelper().setCustomAlert("Potwierdź usunięcie alarmu", "Na pewno chcesz usunąć ten alarm z listy?", getContext(), (dialogInterface, i) -> {

            //Cancel any pending notifications for this alarm
            LoadAlarmsService.AlarmReceiver.cancelReminderAlarm(getContext(), alarm);

            final int rowsDeleted = DatabaseHelperForAlarms.getInstance(getContext()).deleteAlarm(alarm);
            if (rowsDeleted == 1) {
                new BigToastClassHelper().setBigToast("Usunięto alarm z listy.", getContext(), true);
                LoadAlarmsService.launchLoadAlarmsService(getContext());
                Objects.requireNonNull(getActivity()).finish();
            } else {

                new BigToastClassHelper().setBigToast("Alarm nie został usunięty.", getContext(), true);
            }
        }, (dialog, which) -> dialog.dismiss());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                assert uri != null;
                InputStream inputStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mMedicineImage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

