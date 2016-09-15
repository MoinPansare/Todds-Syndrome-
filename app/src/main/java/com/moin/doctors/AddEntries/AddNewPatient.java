package com.moin.doctors.AddEntries;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.doctors.ClassStructures.Patient;
import com.moin.doctors.ClassStructures.QuestionsStr;
import com.moin.doctors.Helper.CardAdapter;
import com.moin.doctors.Helper.CustomViewPager;
import com.moin.doctors.Helper.ShadowTransformer;
import com.moin.doctors.Helper.VolleySingelton;
import com.moin.doctors.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fanrunqi.waveprogress.WaveProgressView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class AddNewPatient extends AppCompatActivity implements CardFragment.pagerInterface {

    @Bind(R.id.waveProgressbar)WaveProgressView progressView;
    @Bind(R.id.viewPager)CustomViewPager mViewPager;
    private ShadowTransformer mFragmentCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private int currentPage;
    private ArrayList<CardFragment> mFragments = new ArrayList<>();
    private String userName;
    private float patientResult;


    private ArrayList<QuestionsStr>questionSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_patient);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Add New Patient");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressView.setmWaveSpeed(5);
        progressView.setMaxProgress(100);


        getDataForViewPager(); // in case of fetching from network use getDataForViewPagerFromServer();
        loadViewPager();

    }

    /*
    Expected API Str for GET
    {
        status : "OK",
        errorMessage : "",
        response : [
        {
            id : 1,
            question : "Question 1",
            option1 : "Yes",
            option2 : "No",
            weightForPositiveValue : 25
        },
        {
            id : 2,
            question : "Question 2",
            option1 : "Yes",
            option2 : "No",
            weightForPositiveValue : 25
        }]
    }


     */

    private void getDataForViewPagerFromServer(){

        JsonObjectRequest getLiveFeedsRequest = new JsonObjectRequest(Request.Method.GET, "Some Url To Get Questions",new JSONObject(),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("errorMessage");

                            if (!status.equalsIgnoreCase("Error")) {
                                getDataFromJSON(response);
                            } else {
                                showAlert("Error In Server Data");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert("There was some problem please try again");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showAlert("There was some problem please try again");
                    }
                }

        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getLiveFeedsRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getLiveFeedsRequest);
    }

    private void getDataFromJSON(JSONObject myObj){
        try{
            if (!myObj.isNull("response")){
                JSONArray jsonArr = myObj.getJSONArray("response");
                questionSet = new ArrayList<>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    QuestionsStr current = new QuestionsStr();
                    JSONObject indexObject = jsonArr.getJSONObject(i);
                    current.mainQuestion = indexObject.get("question").toString();
                    current.option1 = indexObject.get("option1").toString();
                    current.option2 = indexObject.get("option2").toString();
                    current.weightForPositiveValue = Float.parseFloat(indexObject.get("weightForPositiveValue").toString());
                    current.id = Integer.parseInt(indexObject.get("id").toString());


                    questionSet.add(current);
                }
            }
        }catch (Exception e){
            showAlert("Error In Server JSON");
        }
    }


    private void getDataForViewPager(){

        //Loading Dummy Content as input

        QuestionsStr question = new QuestionsStr();
        question.id = 1;
        question.mainQuestion = "Select Gender";
        question.option1 = "Female";
        question.option2 = "Male";
        question.weightForPositiveValue = 25; // weightForPositiveValue * number Of Questions should be == 100
        questionSet.add(question);

        QuestionsStr question2 = new QuestionsStr();
        question2.id = 2;
        question2.mainQuestion = "Is Age Greater Than 15 ?";
        question2.option1 = "No";
        question2.option2 = "Yes";
        question2.weightForPositiveValue = 25;
        questionSet.add(question2);

        QuestionsStr question3 = new QuestionsStr();
        question3.id = 3;
        question3.mainQuestion = "Does The Patient Suffer From Migraines ?";
        question3.option1 = "No";
        question3.option2 = "Yes";
        question3.weightForPositiveValue = 25;
        questionSet.add(question3);

        QuestionsStr question4 = new QuestionsStr();
        question4.id = 4;
        question4.mainQuestion = "Does The Patient Use Any Hallucinogenic Drugs ?";
        question4.option1 = "No";
        question4.option2 = "Yes";
        question4.weightForPositiveValue = 25;
        questionSet.add(question4);

    }

    private void loadViewPager(){

        //View Pager Operations

        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));

        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mFragmentCardShadowTransformer.enableScaling(true);
        mViewPager.setAdapter(mFragmentCardAdapter);
        mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        mViewPager.setPagingEnabled(false);

        int index = mViewPager.getCurrentItem();
        Log.d("index",index+"");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //back button selected
        finish();
        return super.onOptionsItemSelected(item);
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void nextButtonPressed() {
        if (mViewPager.getCurrentItem() == (mFragments.size()-1)){
            progressView.setCurrent(100,"");
            getAllDataFromFragments();
        }else{
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
        }
        progressView.setCurrent(mViewPager.getCurrentItem()*(100/mFragments.size()),"");
    }

    private void getAllDataFromFragments(){

        //get user Entries from all fragments

        patientResult = 0;
        for (int i=0;i<mFragments.size();i++){
            patientResult+=mFragments.get(i).calculatedIndividualWeight();
        }

        for (int i=0;i<mFragments.size();i++){
            questionSet.get(i).valueSelected = mFragments.get(i).getAnswerSelected();
        }

        new AlertDialog.Builder(this)
                .setTitle("Result")
                .setMessage("The patient has "+ patientResult + "% probability of being diagnosed with Todd’s Syndrome")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialogForUserName();
                    }
                })
                .show();
    }

    public class CardFragmentPagerAdapter extends FragmentPagerAdapter implements CardAdapter {

        private float mBaseElevation;

        public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation) {
            super(fm);
            mFragments = new ArrayList<>();
            mBaseElevation = baseElevation;

            for(int i = 0; i< questionSet.size(); i++){

                //Load Number of fragments depending upon questions recieved

                CardFragment card = new CardFragment();
                card.setM_pagerInterface(AddNewPatient.this);
                card.setQuestion(questionSet.get(i));
                card.lastIndex = questionSet.size()-1;
                mFragments.add(card);
            }
        }

        @Override
        public float getBaseElevation() {
            return mBaseElevation;
        }

        @Override
        public CardView getCardViewAt(int position) {
            return mFragments.get(position).getCardView();
        }

        @Override
        public int getCount() {
            return questionSet.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object fragment = super.instantiateItem(container, position);
            mFragments.set(position, (CardFragment) fragment);
            return fragment;
        }

    }

    private void  AlertDialogForUserName(){
        final EditText edittext = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Patient Name");
        alert.setTitle("Please enter the name of the patient for future reference");
        alert.setView(edittext);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userName = edittext.getText().toString();

                // saving the user values into local DB

                saveUserInfoToDB();

                // in case of using Server communication use UploadResultInfoToServer();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    /*
    Expected API structure

    {
        patientName : "John",
        patientResult : 40,
        QuestionIdArray : [1,2,4,5]
    }


    response : {
    status : "OK",
    errorMessage : ""
    }
     */



    private void UploadResultInfoToServer(){
        JSONObject params = new JSONObject();
        try {
            params.put("patientName", userName);
            params.put("patientResult", patientResult);
            ArrayList<String>list = new ArrayList<>();
            for (int i=0;i<questionSet.size();i++){
                list.add(questionSet.get(i).id+"");
            }
            params.put("QuestionIdArray",new JSONArray(Arrays.asList(list)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest getLiveFeedsRequest = new JsonObjectRequest(Request.Method.POST, "Some URLTo Upload Result", params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("errorMessage");

                            if (!status.equalsIgnoreCase("Error")) {
                                // some success message
                            } else {
                                showAlert(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert("There was some problem please try again");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showAlert("There was some problem please try again");
                    }
                }

        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getLiveFeedsRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getLiveFeedsRequest);
    }

    private void saveUserInfoToDB(){

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded().build();
        Realm realm = Realm.getInstance(realmConfig);

        RealmResults<Patient> dataFromDB = realm.where(Patient.class).findAll();
        int max = 0;
        for (int i=0;i<dataFromDB.size();i++){
            try{
                int val = dataFromDB.get(i).id;
                if (val>=max){
                    max= val+1;
                }
            }catch (Exception e){

            }
        }

        Patient current = new Patient();
        current.id = max;
        current.patientName = userName;
        current.result = patientResult;

        try{
            realm.beginTransaction();
            realm.copyToRealm(current);
        }finally {
            realm.commitTransaction();
        }



        RealmResults<QuestionsStr> dataFromDBQuestions = realm.where(QuestionsStr.class).findAll();
        int questionsMax = 0;

        for (int i=0;i<dataFromDBQuestions.size();i++){
            try{
                int val = dataFromDBQuestions.get(i).id;
                if (val>=questionsMax){
                    questionsMax= val+1;
                }
            }catch (Exception e){

            }
        }

        for (int i=0;i<questionSet.size();i++){
            questionSet.get(i).UserId = current.id;
            questionSet.get(i).id = questionsMax++;
            try{
                realm.beginTransaction();
                realm.copyToRealm(questionSet.get(i));
            }finally {
                realm.commitTransaction();
            }
        }

        showSuccessAlert();

    }

    private void showSuccessAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("The patient information has been saved successfully")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void showAlert(String msg){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

}
