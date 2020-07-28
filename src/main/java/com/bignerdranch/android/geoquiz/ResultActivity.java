package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultActivity extends AppCompatActivity {

    private static final String EXTRA_RESULT = "com.bignerdranch.android.geoquiz.results";

    private TextView mResultView;
    private int[] mResult;

    //Declare intent
    public static Intent newIntent(Context packageContext, int[] result){
        Intent i = new Intent(packageContext,ResultActivity.class);
        i.putExtra(EXTRA_RESULT,result);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Get result from QuizActivity
        mResult = getIntent().getIntArrayExtra(EXTRA_RESULT);
        mResultView = (TextView) findViewById(R.id.result_view);

        //Display result
        String summary = "Total Questions Answered: "+mResult[0]+"\n"+"Total Score: "+mResult[1]+"\n"+"Total Cheat Attempts: "+mResult[2];
        mResultView.setText(summary);
    }
}
