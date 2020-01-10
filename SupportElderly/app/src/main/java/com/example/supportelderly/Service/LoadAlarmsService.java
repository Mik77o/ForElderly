package com.example.supportelderly.Service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.example.supportelderly.Data.DatabaseHelperForAlarms;
import com.example.supportelderly.Model.Alarm;
import com.example.supportelderly.R;
import com.example.supportelderly.Util.AlarmUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.O;
import static com.example.supportelderly.UI.AlarmLandingPageActivity.launchIntent;

/**
 * Klasa odpowiedzialna za ustawienia usługi alarmów, ustawienie parametrów notyfikacji.
 */
public final class LoadAlarmsService extends IntentService {

    private static final String TAG = LoadAlarmsService.class.getSimpleName();
    public static final String ACTION_COMPLETE = TAG + ".ACTION_COMPLETE";
    public static final String ALARMS_EXTRA = "alarms_extra";

    @SuppressWarnings("unused")
    public LoadAlarmsService() {
        this(TAG);
    }

    public LoadAlarmsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final List<Alarm> alarms = DatabaseHelperForAlarms.getInstance(this).getAlarms();

        final Intent i = new Intent(ACTION_COMPLETE);
        i.putParcelableArrayListExtra(ALARMS_EXTRA, new ArrayList<>(alarms));
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }

    public static void launchLoadAlarmsService(Context context) {
        final Intent launchLoadAlarmsServiceIntent = new Intent(context, LoadAlarmsService.class);
        context.startService(launchLoadAlarmsServiceIntent);
    }

    public static final class AlarmReceiver extends BroadcastReceiver {

        private static final String TAG = AlarmReceiver.class.getSimpleName();
        private static final String CHANNEL_ID = "alarm_channel";

        private static final String BUNDLE_EXTRA = "bundle_extra";
        private static final String ALARM_KEY = "alarm_key";

        @Override
        public void onReceive(Context context, Intent intent) {

            final Alarm alarm = intent.getBundleExtra(BUNDLE_EXTRA).getParcelable(ALARM_KEY);
            if (alarm == null) {
                Log.e(TAG, "Alarm is null", new NullPointerException());
                return;
            }

            final int id = alarm.notificationId();

            final NotificationManager manager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


            NotificationChannel notificationChannel;
            Uri path = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + context.getPackageName() + "/" + R.raw.alarm_sound);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, "not_channel", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLightColor(Color.WHITE);
                notificationChannel.enableLights(true);
                notificationChannel.setDescription("notification");
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                notificationChannel.setSound(path, audioAttributes);

                if (manager != null) {
                    manager.createNotificationChannel(notificationChannel);
                }
            }

            createNotificationChannel(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.time_icon);
            builder.setColor(ContextCompat.getColor(context, R.color.colorAccent));
            builder.setContentTitle(alarm.getLabel().toUpperCase());
            //builder.setContentText(alarm.getLabel());
            builder.setOngoing(true);
            builder.setVibrate(new long[]{1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000});
            if (alarm.getImage() == null) {
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.info)).bigLargeIcon(null));
            } else {
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(alarm.getImage()));
            }

            builder.setSound(path);

            builder.setContentIntent(launchAlarmLandingPage(context, alarm));
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setCategory(NotificationCompat.CATEGORY_ALARM);

            assert manager != null;
            manager.notify(id, builder.build());

            //Reset Alarm manually
            setReminderAlarm(context, alarm);
        }

        //Convenience method for setting a notification
        public static void setReminderAlarm(Context context, Alarm alarm) {

            //Check whether the alarm is set to run on any days
            if (!AlarmUtils.isAlarmActive(alarm)) {
                //If alarm not set to run on any days, cancel any existing notifications for this alarm
                cancelReminderAlarm(context, alarm);
                return;
            }

            final Calendar nextAlarmTime = getTimeForNextAlarm(alarm);
            alarm.setTime(nextAlarmTime.getTimeInMillis());

            final Intent intent = new Intent(context, AlarmReceiver.class);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(ALARM_KEY, alarm);
            intent.putExtra(BUNDLE_EXTRA, bundle);

            final PendingIntent pIntent = PendingIntent.getBroadcast(
                    context,
                    alarm.notificationId(),
                    intent,
                    FLAG_UPDATE_CURRENT
            );

            ScheduleAlarm.with(context).schedule(alarm, pIntent);
        }

        public static void setReminderAlarms(Context context, List<Alarm> alarms) {
            for (Alarm alarm : alarms) {
                setReminderAlarm(context, alarm);
            }
        }

        /**
         * Calculates the actual time of the next alarm/notification based on the user-set time the
         * alarm should sound each day, the days the alarm is set to run, and the current time.
         *
         * @param alarm Alarm containing the daily time the alarm is set to run and days the alarm
         *              should run
         * @return A Calendar with the actual time of the next alarm.
         */
        private static Calendar getTimeForNextAlarm(Alarm alarm) {

            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alarm.getTime());

            final long currentTime = System.currentTimeMillis();
            final int startIndex = getStartIndexFromTime(calendar);

            int count = 0;
            boolean isAlarmSetForDay;

            final SparseBooleanArray daysArray = alarm.getDays();

            do {
                final int index = (startIndex + count) % 7;
                isAlarmSetForDay =
                        daysArray.valueAt(index) && (calendar.getTimeInMillis() > currentTime);
                if (!isAlarmSetForDay) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    count++;
                }
            } while (!isAlarmSetForDay && count < 7);

            return calendar;

        }

        public static void cancelReminderAlarm(Context context, Alarm alarm) {

            final Intent intent = new Intent(context, AlarmReceiver.class);
            final PendingIntent pIntent = PendingIntent.getBroadcast(
                    context,
                    alarm.notificationId(),
                    intent,
                    FLAG_UPDATE_CURRENT
            );

            final AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            manager.cancel(pIntent);
        }

        private static int getStartIndexFromTime(Calendar c) {

            final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            int startIndex = 0;
            switch (dayOfWeek) {
                case Calendar.MONDAY:
                    startIndex = 0;
                    break;
                case Calendar.TUESDAY:
                    startIndex = 1;
                    break;
                case Calendar.WEDNESDAY:
                    startIndex = 2;
                    break;
                case Calendar.THURSDAY:
                    startIndex = 3;
                    break;
                case Calendar.FRIDAY:
                    startIndex = 4;
                    break;
                case Calendar.SATURDAY:
                    startIndex = 5;
                    break;
                case Calendar.SUNDAY:
                    startIndex = 6;
                    break;
            }

            return startIndex;

        }

        private static void createNotificationChannel(Context ctx) {
            if (SDK_INT < O) return;

            final NotificationManager mgr = ctx.getSystemService(NotificationManager.class);
            if (mgr == null) return;

            final String name = ctx.getString(R.string.channel_name);
            if (mgr.getNotificationChannel(name) == null) {
                final NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, name, IMPORTANCE_HIGH);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{1000, 500, 1000, 500, 1000, 500});
                channel.setBypassDnd(true);
                mgr.createNotificationChannel(channel);
            }
        }

        private static PendingIntent launchAlarmLandingPage(Context ctx, Alarm alarm) {
            return PendingIntent.getActivity(
                    ctx, alarm.notificationId(), launchIntent(ctx), FLAG_UPDATE_CURRENT
            );
        }

        private static class ScheduleAlarm {

            @NonNull
            private final Context ctx;
            @NonNull
            private final AlarmManager am;

            private ScheduleAlarm(@NonNull AlarmManager am, @NonNull Context ctx) {
                this.am = am;
                this.ctx = ctx;
            }

            static ScheduleAlarm with(Context context) {
                final AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                if (am == null) {
                    throw new IllegalStateException("AlarmManager is null");
                }
                return new ScheduleAlarm(am, context);
            }

            void schedule(Alarm alarm, PendingIntent pi) {
                if (SDK_INT > LOLLIPOP) {
                    am.setAlarmClock(new AlarmManager.AlarmClockInfo(alarm.getTime(), launchAlarmLandingPage(ctx, alarm)), pi);
                } else if (SDK_INT > KITKAT) {
                    am.setExact(AlarmManager.RTC_WAKEUP, alarm.getTime(), pi);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, alarm.getTime(), pi);
                }
            }

        }

    }
}
