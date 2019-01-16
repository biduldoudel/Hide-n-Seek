package com.example.jonat.hideandseek;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class RulesRecall extends AppCompatActivity {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference gamesRef = databaseGame.getReference("games");

    private TextView rules;
    private String[] rulesText;
    private int ruleN;
    private String role;
    private String team;
    private String gameId;
    final Animation in = new AlphaAnimation(0.0f, 1.0f);
    final Animation out = new AlphaAnimation(1.0f, 0.0f);
    private String username;
    private int nReadyPlayer;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_recall);

        Intent intent = getIntent();

        role = intent.getExtras().getString("role");
        team = intent.getExtras().getString("team");
        gameId = intent.getExtras().getString("gameId");
        username = intent.getExtras().getString("username");

        Resources res = getResources();


        //Get set of rules for selected role/team
        switch (team) {
            case "Zombie":
                switch (role) {
                    case "Master":
                        rulesText = res.getStringArray(R.array.PsychicRules);
                        break;
                    case "Runner":
                        rulesText = res.getStringArray(R.array.ZombieRules);
                        break;
                }
                break;
            case "Survivor":

                switch (role) {
                    case "Master":
                        rulesText = res.getStringArray(R.array.OverseerRules);
                        break;
                    case "Runner":
                        rulesText = res.getStringArray(R.array.SurvivorRules);
                        break;
                }
                break;
        }


        //Goes through the set of rules f
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

                //When all rules have been displayed
                //Incrment number of ready players
                if (ruleN == rulesText.length) {
                    rules.setText("Please wait");
                    rules.startAnimation(in);
                    gamesRef.child(gameId).child("nReadyPlayers").setValue(nReadyPlayer + 1);
                } else {
                    rules.setText(rulesText[ruleN]);
                    rules.startAnimation(in);
                    ruleN++;
                }


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        gamesRef.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                //If all players are ready, start required activity and sets the game status once
                nReadyPlayer = dataSnapshot.child(gameId).child("nReadyPlayers").getValue(Integer.class);
                if (dataSnapshot.child(gameId).child("nExpectedPlayers").getValue(Integer.class) == nReadyPlayer) {
                gamesRef.removeEventListener(valueEventListener);
                    if (dataSnapshot.child(gameId).child("gameStatus").getValue(String.class).equals("RulesRecall")) {
                        gamesRef.child(gameId).child("gameStatus").setValue("GameSetup");
                    }

                    Intent intent;
                    switch (role) {
                        case "Master":
                            intent = new Intent(RulesRecall.this, MasterPlayer.class);
                            intent.putExtra("role", role);
                            intent.putExtra("team", team);
                            intent.putExtra("gameId", gameId);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            break;
                        case "Runner":
                            intent = new Intent(RulesRecall.this, RunnerPlayer.class);
                            intent.putExtra("role", role);
                            intent.putExtra("team", team);
                            intent.putExtra("gameId", gameId);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            break;
                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        gamesRef.removeEventListener(valueEventListener);
    }

    public void screenTapped(View view) {
        rules.startAnimation(out);
    }
}
