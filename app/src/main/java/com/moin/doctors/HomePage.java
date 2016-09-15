package com.moin.doctors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.moin.doctors.AddEntries.AddNewPatient;
import com.moin.doctors.PatientInformation.PatientList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.newEntryButton)void newEntryButtonClicked(View view){
        startActivity(new Intent(HomePage.this, AddNewPatient.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    @OnClick(R.id.checkPatientListButton)void checkPatientListButtonClicked(View view){
        startActivity(new Intent(HomePage.this, PatientList.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }
}
