package com.swufe.wy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreMachine2 extends AppCompatActivity {
    TextView scoresA,scoresB;
    Button add1, add2, add3, reset;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String scoreA=((TextView)findViewById(R.id.scoreA)).toString();
        String scoreB=((TextView)findViewById(R.id.scoreB)).toString();

        outState.putString("score_A",scoreA);
        outState.putString("score_B",scoreB);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String score_A=savedInstanceState.getString("score_A");
        String score_B=savedInstanceState.getString("score_B");

        ((TextView)findViewById(R.id.scoreA)).setText(score_A);
        ((TextView)findViewById(R.id.scoreB)).setText(score_B);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_machine2);
        scoresA = (TextView) findViewById(R.id.scoreA);
        scoresB = (TextView) findViewById(R.id.scoreB);
    }
    public void btnAdd1(View btn) {
        if(btn.getId()==R.id.add1A) {
            showScoreA(1);
        }else{
            showScoreB(1);
        }
    }
    public void btnAdd2(View btn) {
        if(btn.getId()==R.id.add2A) {
            showScoreA(2);
        }else{
            showScoreB(2);
        }
    }
    public void btnAdd3(View btn) {
        if(btn.getId()==R.id.add3A) {
            showScoreA(3);
        }else{
            showScoreB(3);
        }
    }
    public void btnReset(View btn) {
        scoresA.setText("0");
        scoresB.setText("0");
    }

    private void showScoreA(int inc) {
        Log.i("show","inc="+inc);
        String oldScore = (String) scoresA.getText();
        int newScore = Integer.parseInt(oldScore)+inc;
        scoresA.setText("" + newScore);
    }
    private void showScoreB(int inc) {
        Log.i("show","inc="+inc);
        String oldScore = (String) scoresB.getText();
        int newScore = Integer.parseInt(oldScore)+inc;
        scoresB.setText("" + newScore);
    }
}
