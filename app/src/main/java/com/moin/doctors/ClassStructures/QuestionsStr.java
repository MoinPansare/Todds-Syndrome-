package com.moin.doctors.ClassStructures;

import io.realm.RealmObject;

/**
 * Created by macpro on 9/15/16.
 */
public class QuestionsStr extends RealmObject {
    public int UserId;
    public int id;
    public String mainQuestion;
    public String option1;
    public String option2;
    public boolean valueSelected;
    public float weightForPositiveValue;

    public QuestionsStr(){
        this.valueSelected = false;
    }
}
