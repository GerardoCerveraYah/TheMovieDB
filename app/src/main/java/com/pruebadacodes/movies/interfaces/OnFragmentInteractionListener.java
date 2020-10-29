package com.pruebadacodes.movies.interfaces;

import android.view.View;

public interface OnFragmentInteractionListener {
    void onMessage(String message, String actionText, View.OnClickListener action);
    void onNavigationChanged(boolean showBack, String title);
}