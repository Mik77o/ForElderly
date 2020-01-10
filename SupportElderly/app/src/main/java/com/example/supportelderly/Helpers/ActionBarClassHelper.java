package com.example.supportelderly.Helpers;

import android.support.v7.app.ActionBar;

/**
 * Helper dotyczący panelu nawigacyjnego (customowego).
 */
public class ActionBarClassHelper {

    public static void createCustomActionBar(ActionBar tmpActionBar, CharSequence title) {
        tmpActionBar.setHomeButtonEnabled(true);
        tmpActionBar.setDisplayHomeAsUpEnabled(true);
        tmpActionBar.setTitle(title);
    }
}
