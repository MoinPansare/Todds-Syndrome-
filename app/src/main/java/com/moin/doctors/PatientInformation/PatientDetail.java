package com.moin.doctors.PatientInformation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.doctors.ClassStructures.Patient;
import com.moin.doctors.ClassStructures.QuestionsStr;
import com.moin.doctors.Helper.VolleySingelton;
import com.moin.doctors.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class PatientDetail extends AppCompatActivity {

    private Patient selectedPatient;
    private String packageName = "com.moin.doctors";
    private ArrayList<QuestionsStr>data = new ArrayList<>();
    private QuestionsListAdapter myAdapter;

    @Bind(R.id.patientNameTextField)TextView patientnameTextView;
    @Bind(R.id.patientResultTextField)TextView patientResultTextField;
    @Bind(R.id.recycler)RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Patient Detail");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedPatient = getIntent().getParcelableExtra(packageName+"patientInfo");
        fillPatientInfo();

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        myAdapter = new QuestionsListAdapter(PatientDetail.this);
        mRecyclerView.setAdapter(myAdapter);

        getQuestionsAnswered(); // in case of network use getQuestionsFromServer();
    }

    private void fillPatientInfo(){
        patientnameTextView.setText(selectedPatient.patientName);
        patientResultTextField.setText(selectedPatient.result + "% probability of being diagnosed with Todd's Syndrome");
    }


    /*

    Expected API Str for POST
    request{
        patientId : "1"
    }

    response
    {
        status : "OK",
        errorMessage : "",
        response : [
        {
            id : 1,
            mainQuestion : "Question 1",
            option1 : "Male",
            option2 : "Female",
            weightForPositiveValue : 50
        },
        {
            id : 2,
            mainQuestion : "Question 2",
            option1 : "Yes",
            option2 : "No",
            weightForPositiveValue : 50
        }]
    }


     */

    private void PatientListFromServer(){
        JSONObject params = new JSONObject();
        try {
            params.put("patientId", selectedPatient.id+"");

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
                                parseServerResponse(response);
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

    private void parseServerResponse(JSONObject myObj){

        try{
            if (!myObj.isNull("response")){
                JSONArray jsonArr = myObj.getJSONArray("response");
                data = new ArrayList<>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    QuestionsStr current = new QuestionsStr();
                    JSONObject indexObject = jsonArr.getJSONObject(i);
                    current.id = Integer.parseInt(indexObject.get("id").toString());
                    current.mainQuestion = indexObject.get("mainQuestion").toString();
                    current.option1 = indexObject.get("option1").toString();
                    current.option2 = indexObject.get("option2").toString();
                    current.weightForPositiveValue = Float.parseFloat(indexObject.get("weightForPositiveValue").toString());
                    data.add(current);
                }
            }
        }catch (Exception e){
            showAlert("Error In Server JSON");
        }

        myAdapter.notifyDataSetChanged();

    }


    private void getQuestionsAnswered(){
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded().build();
        Realm realm = Realm.getInstance(realmConfig);

        RealmResults<QuestionsStr> dataFromDB = realm.where(QuestionsStr.class)
                .equalTo("UserId", selectedPatient.id)
                .findAll();

        data = new ArrayList<>();
        for (int i=0;i<dataFromDB.size();i++){
            QuestionsStr curr = new QuestionsStr();
            curr.UserId = selectedPatient.id;
            curr.id = dataFromDB.get(i).id;
            curr.mainQuestion = dataFromDB.get(i).mainQuestion;
            curr.option1 = dataFromDB.get(i).option1;
            curr.option2 = dataFromDB.get(i).option2;
            curr.valueSelected = dataFromDB.get(i).valueSelected;
            curr.weightForPositiveValue = dataFromDB.get(i).weightForPositiveValue;
            data.add(curr);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public class QuestionsListAdapter extends RecyclerView.Adapter<QuestionsListAdapter.QuestionsListViewHolder> {

        private int pos = 0;
        private Context myContext;
        private LayoutInflater inflator;

        public QuestionsListAdapter(Context context) {
            inflator = LayoutInflater.from(context);
            myContext = context;
        }

        @Override
        public QuestionsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.questions_list_cell, parent, false);
            QuestionsListViewHolder holder = new QuestionsListViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(QuestionsListViewHolder holder, int position) {
            QuestionsStr curr = data.get(position);
            holder.questionTextView.setText("Question : " + curr.mainQuestion);
            holder.option1.setText("Option 1 : " + curr.option1);
            holder.option2.setText("Option 2 : " + curr.option2);
            if (curr.valueSelected == true){
                holder.answeredAs.setText("Answer : " + curr.option2);
            }else {
                holder.answeredAs.setText("Answer : " + curr.option1);
            }

        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        public class QuestionsListViewHolder extends RecyclerView.ViewHolder {
            TextView questionTextView;
            TextView option1,option2;
            TextView answeredAs;



            public QuestionsListViewHolder(final View itemView) {
                super(itemView);
                questionTextView = (TextView) itemView.findViewById(R.id.questionTextView);
                option1 = (TextView) itemView.findViewById(R.id.option1);
                option2 = (TextView) itemView.findViewById(R.id.option2);
                answeredAs = (TextView) itemView.findViewById(R.id.answeredAs);



            }
        }


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
