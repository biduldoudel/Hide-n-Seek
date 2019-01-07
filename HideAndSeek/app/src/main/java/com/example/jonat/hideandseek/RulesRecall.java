package com.example.jonat.hideandseek;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class RulesRecall extends AppCompatActivity {

    private TextView rules;
    private String[] rulesText;
    private int ruleN;
    final Animation in = new AlphaAnimation(0.0f, 1.0f);
    final Animation out = new AlphaAnimation(1.0f, 0.0f);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_recall);

        Resources res = getResources();
        rulesText = res.getStringArray(R.array.OverseerRules);

        rules = findViewById(R.id.rulesText);
        rules.setText("Welcome to the apocalypse");

        ruleN = 0;

        in.setDuration(1000);

        out.setDuration(1000);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                if(ruleN==rulesText.length){
                    rules.setText("");
                    Intent intent = new Intent(RulesRecall.this, MasterPlayer.class);
                    startActivity(intent);
                }
                else {
                    rules.setText(rulesText[ruleN]);
                    rules.startAnimation(in);
                    ruleN++;
                }


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void screenTapped(View view) {
        rules.startAnimation(out);
    }
}
