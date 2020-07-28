package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.Random;

import javax.xml.transform.Result;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG ="QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String ANSWER_INDEX = "answered";
    private static final String SCORE_INDEX = "score";
    private static final String QUESTION_INDEX = "questions";
    private static final String CHEATED="cheat";
    private static final String CHEAT_LIMIT="limit";
    private static final int REQUEST_CODE_CHEAT=0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mResetButton;
    private Button mCheatButton;
    private Button mResultButton;
    private Button mRandomButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private TextView mCheatLimitView;
    private TextView mScoreView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true, false),
            new Question(R.string.question_oceans, true, false),
            new Question(R.string.question_mideast, false,false),
            new Question(R.string.question_africa, false, false),
            new Question(R.string.question_americas, true, false),
            new Question(R.string.question_asia, true, false),
    };

    private int mCurrentIndex = 0;
    private int mCurrentScore = 0;
    private int mTotalQuestions = 0;
    private int mCheatLimit = 3;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        //Saved instances
        if(savedInstanceState !=null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mCurrentScore = savedInstanceState.getInt(SCORE_INDEX);
            mTotalQuestions = savedInstanceState.getInt(QUESTION_INDEX);
            mQuestionBank = (Question[]) savedInstanceState.getSerializable(ANSWER_INDEX);
            mIsCheater = savedInstanceState.getBoolean(CHEATED);
            mCheatLimit = savedInstanceState.getInt(CHEAT_LIMIT);
        }

        //View Initialization
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mScoreView = (TextView) findViewById(R.id.score_view);
        mCheatLimitView = (TextView) findViewById(R.id.cheat_limit_view);
        updateCheatLimit();

        //True Button
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);

            }
        });

        //False Button
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        //Next Button
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1)% mQuestionBank.length;
                if(!mQuestionBank[mCurrentIndex].isHasAnswered()){
                    mIsCheater = false;
                }
                updateQuestion();
            }
        });

        //Previous Button
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 0){
                    mCurrentIndex = (mCurrentIndex - 1)% mQuestionBank.length;
                }else{
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                updateQuestion();
            }
        });

        //Additional Feature: Random Button. Lets players get a random question from the list when pressed
        mRandomButton = (Button) findViewById(R.id.random_button);
        mRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mQuestionBank.length - mTotalQuestions) >= 1){
                    mCurrentIndex = randomQuestion(0,mQuestionBank.length-1);
                    if(mQuestionBank[mCurrentIndex].isHasAnswered() == true){
                        while(mQuestionBank[mCurrentIndex].isHasAnswered() == true){
                            mCurrentIndex = randomQuestion(0,mQuestionBank.length-1);
                        }
                    }
                    updateQuestion();
                }
            }
        });

        //Reset Quiz
        mResetButton = (Button) findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mCurrentIndex = 0;
                mCurrentScore = 0;
                mTotalQuestions = 0;
                mCheatLimit = 3;
                for(int i = 0; i <mQuestionBank.length;i++){
                    if(mQuestionBank[i].isHasAnswered() == true){
                        mQuestionBank[i].setHasAnswered(false);
                    }
                    mQuestionBank[i].setCheated(false);
                }

                Toast.makeText(QuizActivity.this, "Quiz Reset!", Toast.LENGTH_SHORT).show();
                mScoreView.setVisibility(View.INVISIBLE);
                updateQuestion();
                updateCheatLimit();
            }
        });

        //Make Question Text View clickable. Goes to the next question
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1)% mQuestionBank.length;
                if(!mQuestionBank[mCurrentIndex].isHasAnswered()){
                    mIsCheater = false;
                }
                updateQuestion();
            }
        });

        //Access cheats. Disabled when cheat limit reached
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCheatLimit > 0){
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent,REQUEST_CODE_CHEAT);
                }else{
                    Toast.makeText(QuizActivity.this, "Maximum cheat limit reached!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Access Result screen
        mResultButton = (Button) findViewById(R.id.result_button);
        mResultButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int questions = mTotalQuestions;
                int score = mCurrentScore;
                int numCheat = 3 - mCheatLimit;
                if(numCheat < 0){
                    numCheat = 0;
                }

                int[] result = {questions,score, numCheat};
                Intent i = ResultActivity.newIntent(QuizActivity.this, result);
                startActivity(i);
            }
        });

        updateQuestion();
    }

    //Activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            if(mIsCheater == true){
                mQuestionBank[mCurrentIndex].setHasAnswered(true);
                mQuestionBank[mCurrentIndex].setCheated(true);
                mTotalQuestions++;
                mCheatLimit--;
                updateCheatLimit();
            }
        }
    }

    //Update question according to current index
    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    //Display current number of cheats
    private void updateCheatLimit(){
        mCheatLimitView.setText("Cheats left: "+mCheatLimit);
    }

    //Checks answers and update score accordingly
    private void checkAnswer(boolean userPresedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        boolean hasAnswered = mQuestionBank[mCurrentIndex].isHasAnswered();
        int messageResID = 0;

        if(mIsCheater){
            messageResID = R.string.judgement_toast;
        }else{
            if(!hasAnswered){
                if(userPresedTrue == answerIsTrue){
                    messageResID = R.string.correct_toast;
                    mCurrentScore++;
                }else{
                    messageResID = R.string.false_toast;
                }
                mQuestionBank[mCurrentIndex].setHasAnswered(true);
                mTotalQuestions++;
            }else{
                if(mQuestionBank[mCurrentIndex].getCheated()== true){
                    messageResID = R.string.judgement_toast;
                }else{
                    messageResID=R.string.answered;
                }

            }
        }

        Toast.makeText(this, messageResID, Toast.LENGTH_SHORT).show();

        if(mTotalQuestions == mQuestionBank.length){
            String finalScore = "Your final score is: "+100*mCurrentScore/mTotalQuestions+"%";
            Toast.makeText(this,finalScore,Toast.LENGTH_SHORT).show();
            mScoreView.setVisibility(View.VISIBLE);
            mScoreView.setText(finalScore);
        }


    }

    //Question Randomizer
    private int randomQuestion(int min, int max){
        Random r = new Random();
        int randNum = r.nextInt((max-min)+1)+min;
        return randNum;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG,"onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putSerializable(ANSWER_INDEX,mQuestionBank);
        savedInstanceState.putInt(SCORE_INDEX,mCurrentScore);
        savedInstanceState.putInt(QUESTION_INDEX,mTotalQuestions);
        savedInstanceState.putBoolean(CHEATED,mIsCheater);
        savedInstanceState.putInt(CHEAT_LIMIT,mCheatLimit);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }
}
