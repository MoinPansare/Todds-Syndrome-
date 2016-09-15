package com.moin.doctors.Helper;

import android.support.v7.widget.CardView;

/**
 * Created by macpro on 9/15/16.
 */
public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
