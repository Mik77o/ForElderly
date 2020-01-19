package com.example.supportelderly.UI;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.supportelderly.Adapter.AlarmsAdapter;
import com.example.supportelderly.Data.DatabaseHelperForAlarms;
import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.Helpers.CustomAlertDialogClassHelper;
import com.example.supportelderly.Model.Alarm;
import com.example.supportelderly.R;
import com.example.supportelderly.Service.LoadAlarmsReceiver;
import com.example.supportelderly.Service.LoadAlarmsService;
import com.example.supportelderly.Util.AlarmUtils;
import com.example.supportelderly.View.DividerItemDecoration;
import com.example.supportelderly.View.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.ALARM_SERVICE;
import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;
import static com.example.supportelderly.UI.EditSettingsForAlarmActivity.ADD_ALARM;
import static com.example.supportelderly.UI.EditSettingsForAlarmActivity.buildAddEditAlarmActivityIntent;

/**
 * Klasa dotycząca obsługi widoku listy alarmów.
 */
public final class MainFragmentForAlarms extends Fragment
        implements LoadAlarmsReceiver.OnAlarmsLoadedListener {

    private LoadAlarmsReceiver mReceiver;
    private AlarmsAdapter mAdapter;
    private ActionBar actionBar;
    private ArrayList<Alarm> tempAlarms;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        new BigToastClassHelper().setBigToastForLoadingData("Wczytywanie listy alarmów", getContext(), true);
        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        createCustomActionBar(actionBar, "Lista alarmów");
        super.onCreate(savedInstanceState);
        mReceiver = new LoadAlarmsReceiver(this);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_main_for_alarms, container, false);

        final EmptyRecyclerView rv = v.findViewById(R.id.recycler);
        mAdapter = new AlarmsAdapter();
        rv.setEmptyView(v.findViewById(R.id.empty_view));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(getContext()));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());

        final FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlarmUtils.checkAlarmPermissions(getActivity());
            final Intent i = buildAddEditAlarmActivityIntent(getContext(), ADD_ALARM);
            startActivity(i);
        });

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        final IntentFilter filter = new IntentFilter(LoadAlarmsService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).registerReceiver(mReceiver, filter);
        LoadAlarmsService.launchLoadAlarmsService(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).unregisterReceiver(mReceiver);
    }

    @Override
    public void onAlarmsLoaded(ArrayList<Alarm> alarms) {
        mAdapter.setAlarms(alarms);
        tempAlarms = alarms;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.back_remove_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                (Objects.requireNonNull(getActivity())).onBackPressed();
                return true;
            case R.id.action_delete:
                new CustomAlertDialogClassHelper().
                        setCustomAlert("Usuwanie listy alarmów!", "Czy na pewno chcesz usunąć listę zapisanych alarmów?",
                                getContext(), (dialog, which) -> {
                                    new DatabaseHelperForAlarms(getContext()).deleteAll();

                                    final Intent intent = new Intent(getContext(), LoadAlarmsService.AlarmReceiver.class);
                                    for (Alarm alarm : tempAlarms) {
                                        final PendingIntent pIntent = PendingIntent.getBroadcast(
                                                getContext(),
                                                alarm.notificationId(),
                                                intent,
                                                FLAG_UPDATE_CURRENT
                                        );
                                        final AlarmManager manager = (AlarmManager) Objects.requireNonNull(getContext()).getSystemService(ALARM_SERVICE);
                                        manager.cancel(pIntent);
                                    }

                                    new BigToastClassHelper().setBigToast("Lista alarmów została usunięta", getContext(), true);
                                    LoadAlarmsService.launchLoadAlarmsService(getContext());
                                    mAdapter.notifyDataSetChanged();
                                    (Objects.requireNonNull(getActivity())).onBackPressed();
                                }, (dialog, which) -> dialog.dismiss());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
