package com.stellum.reactorcontrol;

/**
 * Created by Charl Gouws on 2017/04/04
 * <p>
 * This app is a simple spaceship reactor control simulation.
 * Increasing the reactor output will cause an increase in reactor
 * temperature, which have to be controlled by increasing
 * coolant flow.
 */

//Todo
/*
        if (mShowReactorTemp > 1000){
                mTempRunawayTask.run();
                }
                */

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.stellum.reactorcontrol.R;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener {
    
    private static final long REP_DELAY = 100;
    Handler mTempRunawayHandler = new Handler();
    //Handler mReactorTempHandler = new Handler();
    
    boolean mAutoIncrement = false;
    
    Animation mFlash;
    
    int mShowReactorOutput;
    int mShowReactorTemp = 0;
    int mShowCoolantFlow;
    
    //Reactor mainReactor = new Reactor();
    
    TextView reactorOutput;
    TextView reactorTemp;
    TextView reactorCoolantFlow;
    TextView reactorStatus;
    
    Button increaseOutput;
    Button decreaseOutput;
    Button inreaseCoolantFlow;
    Button decreaseCoolantFlow;
    
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadUI();
        mReactorTempHandler.post(new MainRunnable());
    }
    
    void loadUI() {
        // Get a reference to the TextView widgets and the buttons
        reactorOutput = (TextView) findViewById(R.id.reactoroutput);
        reactorTemp = (TextView) findViewById(R.id.reactortemp);
        reactorCoolantFlow = (TextView) findViewById(R.id.reactorcoolantflow);
        reactorStatus = (TextView) findViewById(R.id.tempwarning);
        
        increaseOutput = (Button) findViewById(R.id.outputincrease);
        decreaseOutput = (Button) findViewById(R.id.outputdecrease);
        inreaseCoolantFlow = (Button) findViewById(R.id.coolantflowincrease);
        decreaseCoolantFlow = (Button) findViewById(R.id.coolantflowdecrease);
        
        // Add an onClickListener for each button
        increaseOutput.setOnClickListener(this);
        decreaseOutput.setOnClickListener(this);
        inreaseCoolantFlow.setOnClickListener(this);
        decreaseCoolantFlow.setOnClickListener(this);
        
        reactorTemp.setText(String.valueOf(mShowReactorTemp));
        reactorStatus.setText(R.string.reactor_offline);
        
        mFlash = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash);
        //reactorStatus.setTextColor(ContextCompat.getColor(this,R.color.my_green));
    }
    @Override public void onClick(View v) {
        // The switch block to listen for button clicks
        
        switch (v.getId()) {
            case R.id.outputincrease:
                reactorOutputIncrease(100);
                reactorOutput.setText(String.valueOf(mShowReactorOutput));
                reactorTemp();
                reactorTemp.setText(String.valueOf(mShowReactorTemp));
                break;
            
            case R.id.outputdecrease:
                reactorOutputDecrease(100);
                reactorOutput.setText(String.valueOf(mShowReactorOutput));
                // Change reactor status display when output 0
                if (mShowReactorOutput <= 0) {
                    reactorStatus.setText(R.string.reactor_offline);
                    reactorStatus.setTextColor(ContextCompat.getColor(this, R.color.my_white));
                }
                reactorTemp();
                reactorTemp.setText(String.valueOf(mShowReactorTemp));
                break;
            
            case R.id.coolantflowincrease:
                reactorCoolantFlowIncrease(10);
                reactorCoolantFlow.setText(String.valueOf(mShowCoolantFlow));
                reactorTemp();
                reactorTemp.setText(String.valueOf(mShowReactorTemp));
                break;
            
            case R.id.coolantflowdecrease:
                reactorCoolantFlowDecrease(10);
                reactorCoolantFlow.setText(String.valueOf(mShowCoolantFlow));
                reactorTemp();
                reactorTemp.setText(String.valueOf(mShowReactorTemp));
                break;
        }
    }
    
    // The reactorTemp method
    // This method simply changes the mShowReactorTemp readout
    // based on the following calculation being done
    // when each button is being clicked
    // Reactor temperature = Reactor output - Coolant flow * 10
    void reactorTemp() {
        mShowReactorTemp = (mShowReactorOutput - mShowCoolantFlow * 10);
        autoIncrementCondition();
        // The minimum temperature inside the reactor is 100 degrees
        //if (mShowReactorTemp > 1000) {
        //mTempRunawayTask.run();
        if (mShowReactorTemp < 100) {
            mShowReactorTemp = 100;
            //        } else {
            //            if (mShowReactorTemp >= 1000) {
            //                autoIncrementCondition();
        } else {
            if (mShowReactorTemp >= 700) {
                reactorStatus.setText(R.string.temp_critical);
                reactorStatus.setTextColor(ContextCompat.getColor(this, R.color.my_red));
                reactorStatus.setAnimation(mFlash);
                mFlash.setDuration(100);
            } else {
                if (mShowReactorTemp >= 400) {
                    reactorStatus.setText(R.string.high_temp);
                    reactorStatus.setTextColor(ContextCompat.getColor(this, R.color.my_orange));
                    reactorStatus.setAnimation(mFlash);
                    mFlash.setDuration(500);
                } else {
                    if (mShowReactorTemp < 400) {
                        reactorStatus.setText(R.string.temp_normal);
                    }
                    reactorStatus.setTextColor(ContextCompat.getColor(this, R.color.my_green));
                    reactorStatus.clearAnimation();
                }
            }
        }
    }
    // Starts the runnable when reactor temp exceeds 1000
    void autoIncrementCondition() {
        if (mShowReactorTemp > 1000) { //possibly redundant
            mAutoIncrement = true;
            mTempRunawayHandler.post(new RptUpdater());
        }
    }
    // The reactorOutput methods
    // They increase or decrease the mShowReactorOutput readouts
    // when their respective buttons are clicked in onClick
    void reactorOutputIncrease(int outputIncrease) {
        mShowReactorOutput = mShowReactorOutput + outputIncrease;
    }
    void reactorOutputDecrease(int outputDecrease) {
        mShowReactorOutput = mShowReactorOutput - outputDecrease;
        // Reactor output cannot be less than zero
        if (mShowReactorOutput <= 0) {
            mShowReactorOutput = 0;
        }
    }
    // The reactorCoolantFlow methods
    // They increase or decrease the mShowCoolantFlow readouts
    // when their respective buttons are clicked in onClick
    void reactorCoolantFlowIncrease(int coolantFlowIncrease) {
        mShowCoolantFlow = mShowCoolantFlow + coolantFlowIncrease;
    }
    void reactorCoolantFlowDecrease(int coolantFlowDecrease) {
        mShowCoolantFlow = mShowCoolantFlow - coolantFlowDecrease;
        // Reactor coolant flow cannot be less than zero
        if (mShowCoolantFlow <= 0) {
            mShowCoolantFlow = 0;
        }
    }
    @Override protected void onResume() {
        super.onResume();
        
        //if (mShowReactorTemp > 1000) {
        //mTempRunaway.getLooper();
        //}
    }
    
    // The runnable class and its methods
    private class RptUpdater
            implements Runnable {
        @Override public void run() {
            if (mAutoIncrement) {
                increment();
                mTempRunawayHandler.postDelayed(new RptUpdater(), REP_DELAY);
                stopRunnable();
            }
        }
        private void increment() {
            mShowReactorTemp++;
            reactorTemp.setText(String.valueOf(mShowReactorTemp));
        }
        private void stopRunnable() {
            if (mShowReactorTemp < 1000) {
                mAutoIncrement = false;
                mTempRunawayHandler.removeCallbacks(new RptUpdater());
            }
        }
    }
    private class MainRunnable implements Runnable{
        
        @Override public void run() {
            reactorTemp.setText(String.valueOf(mShowReactorTemp));
        }
    }
}
