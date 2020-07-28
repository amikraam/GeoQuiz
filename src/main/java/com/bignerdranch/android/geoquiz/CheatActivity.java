package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE="com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_IS_SHOWN="com.bignerdranch.android.geoquiz.answer_shown";
    private static final String HAS_CHEATED="cheat";

    private boolean mAnswerIsTrue;
    private boolean mCheated;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    //Declare Intent
    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }

    //Check if answer was shown
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_IS_SHOWN,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        //Saved instance for loophole purposes
        if(savedInstanceState!= null){
            setAnswerShownResult(savedInstanceState.getBoolean(HAS_CHEATED));
        }else{
            setAnswerShownResult(false);
        }

        //Get data from QuizActivity
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        //Show answer
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnswerIsTrue){
                    mAnswerTextView.setText(R.string.true_button);
                }else{
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
            }
        });

    }

    //Check whether answer is shown
    private void setAnswerShownResult(boolean isAnswerShown){
        mCheated = isAnswerShown;
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_IS_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    //Save result whether answer was shown
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(HAS_CHEATED,mCheated);
    }
}
