package com.swufe.wy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreMachine extends AppCompatActivity {

    TextView scores;
    Button add1, add2, add3, reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_machine);

        scores = (TextView) findViewById(R.id.score);

    }

    public void btnAdd1(View btn) {
        oldScore(1);
    }

    public void btnAdd2(View btn) {
        oldScore(2);
    }

    public void btnAdd3(View btn) {
        oldScore(3);
    }

    public void btnReset(View btn) {
        scores.setText("0");
    }

    private void oldScore(int inc) {
        Log.i("show","inc="+inc);
        String oldScore = (String) scores.getText();
        int newScore = Integer.parseInt(oldScore)+inc;
        scores.setText("" + newScore);
    }

}
