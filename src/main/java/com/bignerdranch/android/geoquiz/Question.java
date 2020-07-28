package com.bignerdranch.android.geoquiz;

import java.io.Serializable;

public class Question implements Serializable {



    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mHasAnswered;
    private boolean mCheated;

    public Question(int textResID, boolean answerTrue, boolean hasAnswered){
        mTextResId =textResID;
        mAnswerTrue = answerTrue;
        mHasAnswered = hasAnswered;
        mCheated = false;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public boolean isHasAnswered(){
        return mHasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered){
        mHasAnswered = hasAnswered;
    }

    public boolean getCheated(){
        return mCheated;
    }

    public void setCheated(boolean cheat){
        mCheated = cheat;
    }

}
