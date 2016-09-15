package com.moin.doctors.AddEntries;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.moin.doctors.ClassStructures.QuestionsStr;
import com.moin.doctors.Helper.CardAdapter;
import com.moin.doctors.R;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment {

    private CardView mCardView;
    private QuestionsStr currentQuestion;
    private TextView titleTextView;
    private TextView option1;
    private TextView option2;
    private JellyToggleButton toggleButton;
    private FragmentManager mFrgManager;
    private Button nextButton;
    public int lastIndex;
    private pagerInterface m_pagerInterface;

    public void setM_pagerInterface(pagerInterface m_pagerInterface) {
        this.m_pagerInterface = m_pagerInterface;
    }

    public boolean getAnswerSelected(){
        return toggleButton.isChecked();
    }

    private LayoutInflater inflator;
    private Context myContext;

    public void setmFrgManager(FragmentManager mFrgManager) {
        this.mFrgManager = mFrgManager;
    }

    public void setQuestion(QuestionsStr question){
        this.currentQuestion = question;
    }

    public CardFragment() {
        // Required empty public constructor
    }

    public CardView getCardView() {
        return mCardView;
    }

    public static CardFragment getInstance() {
        return (new CardFragment());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);

        titleTextView = (TextView)view.findViewById(R.id.titleTextView);
        option1 = (TextView)view.findViewById(R.id.option1);
        option2 = (TextView)view.findViewById(R.id.option1);
        toggleButton = (JellyToggleButton)view.findViewById(R.id.toggleButton);
        nextButton = (Button)view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    m_pagerInterface.nextButtonPressed();
            }
        });
        return view;
    }

    public void setValuesForFragment(){
        titleTextView.setText(currentQuestion.mainQuestion);
        option1.setText(currentQuestion.option1);
        option2.setText(currentQuestion.option2);
        toggleButton.setChecked(currentQuestion.valueSelected);
    }

    public float calculatedIndividualWeight(){
        if (toggleButton.isChecked()){
            return currentQuestion.weightForPositiveValue;
        }else{
            return 0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setValuesForFragment();
    }

    public interface pagerInterface {
        public void nextButtonPressed();
    }
}
