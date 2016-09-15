package com.moin.doctors.PatientInformation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class PatientList extends AppCompatActivity {

    private ArrayList<Patient>data = new ArrayList<>();
    @Bind(R.id.recycler)RecyclerView mRecyclerView;
    private PatientsListAdapter myAdapter;
    private String packageName = "com.moin.doctors";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Patient List");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        myAdapter = new PatientsListAdapter(PatientList.this);
        mRecyclerView.setAdapter(myAdapter);

        getPatientDataFromDB(); // in case of network use get PatientListFromServer
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    /*

    Expected API Str for GET
    {
        status : "OK",
        errorMessage : "",
        response : [
        {
            id : 1,
            patientName : "Patient 1",
            result : 40,
        },
        {
            id : 2,
            patientName : "Patient 2",
            result : 60,
        }]
    }


     */

    private void PatientListFromServer(){
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
                data = new ArrayList<>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    Patient current = new Patient();
                    JSONObject indexObject = jsonArr.getJSONObject(i);
                    current.id = Integer.parseInt(indexObject.get("id").toString());
                    current.patientName = indexObject.get("patientName").toString();
                    current.result = Float.parseFloat(indexObject.get("result").toString());
                    data.add(current);
                }
            }
        }catch (Exception e){
            showAlert("Error In Server JSON");
        }

        myAdapter.notifyDataSetChanged();

    }


    private void getPatientDataFromDB(){

        //getting patient Info saved from DB

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded().build();
        Realm realm = Realm.getInstance(realmConfig);

        RealmResults<Patient> dataFromDB = realm.where(Patient.class).findAll();
        for (int i=0;i<dataFromDB.size();i++){
            Patient curr = new Patient();
            curr.id = dataFromDB.get(i).id;
            curr.result = dataFromDB.get(i).result;
            curr.patientName = dataFromDB.get(i).patientName;
            data.add(curr);
        }
        myAdapter.notifyDataSetChanged();
    }

    private void showDetailForPatient(int index){
        Intent myIntent = new Intent(PatientList.this,PatientDetail.class);
        myIntent.putExtra(packageName+"patientInfo",data.get(index));
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    public class PatientsListAdapter extends RecyclerView.Adapter<PatientsListAdapter.PatientListViewHolder> {

        private int pos = 0;
        private Context myContext;
        private LayoutInflater inflator;

        public PatientsListAdapter(Context context) {
            inflator = LayoutInflater.from(context);
            myContext = context;
        }

        @Override
        public PatientListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.patients_list_cell, parent, false);
            PatientListViewHolder holder = new PatientListViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(PatientListViewHolder holder, int position) {
            Patient curr = data.get(position);
            holder.nameTextField.setText(curr.patientName);
            holder.resultTextField.setText(curr.result + "%");
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        public class PatientListViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextField;
            TextView resultTextField;



            public PatientListViewHolder(final View itemView) {
                super(itemView);
                nameTextField = (TextView) itemView.findViewById(R.id.nameTextField);
                resultTextField = (TextView) itemView.findViewById(R.id.resultTextField);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailForPatient(getAdapterPosition());
                    }
                });

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
