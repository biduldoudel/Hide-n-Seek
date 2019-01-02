package com.example.jonat.hideandseek;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class JoinNewGame extends AppCompatActivity {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference databaseReference = databaseGame.getReference("games");

    private String gameId = null;
    private String gameCode; // We get it from the editText
    private int instantPlayerNumber;

    EditText editTextCodeNumber;
    TextView TextViewpeopleEnrolled;
    ProgressBar progressBar;
    Button buttonJoingame;
    private Game myGame = null;
    Boolean foundGame;
    Boolean flagOnce = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_new_game);

        // Listen to the intent
        Bundle extras = getIntent().getExtras();

        // If we are the creator (from RegisterNewGame Activity and not from MainActivity)
        if (extras.getBoolean("RegisterNewGame")) {
            gameCode = extras.getString("gameCode");


            editTextCodeNumber = findViewById(R.id.editTextCodeNumber);
            editTextCodeNumber.setText(gameCode);
            editTextCodeNumber.setEnabled(false);

            Button buttonJoin = findViewById(R.id.buttonJoin);
            buttonJoin.setVisibility(View.INVISIBLE);

            TextView textButton = findViewById(R.id.textView);
            textButton.setText("Please share this number with the players !");
            Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();
            
            getGameData();
        }



        buttonJoingame = findViewById(R.id.buttonJoin);
        buttonJoingame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextCodeNumber = findViewById(R.id.editTextCodeNumber);
                gameCode = editTextCodeNumber.getText().toString();
                Toast.makeText(getApplicationContext(), gameCode, Toast.LENGTH_LONG).show();

                if(checkCode()) {
                    // read database and get the playerNumber - for all !
                    Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();
                    getGameData();
                }
            }
        });

        Button buttonCreateUser = findViewById(R.id.buttonCreatePlayer);
        buttonCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gameId != null){
                    Intent intent = new Intent(JoinNewGame.this,RegisterNewPlayer.class);
                    intent.putExtra("gameId", gameId);
                    Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        });

    }



    private boolean checkCode() {
        if (gameCode.length() == 6){
            return true;
        }
        else{
            Toast.makeText(getApplicationContext(), "You have to enter a 6 digits number", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    private void getGameData() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot game : dataSnapshot.getChildren()) {
                    foundGame = false;
                    String gameCodeDB = game.child("gameCode").getValue(String.class);
                    String gameStatusDB = game.child("gameStatus").getValue(String.class);

                    if (gameCodeDB.equals(gameCode) && gameStatusDB.equals("Waiting")) {
                        gameId = game.getKey();
                        buttonJoingame.setEnabled(false);
                        editTextCodeNumber.setEnabled(false);
                        Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();
                        foundGame = true;
                        break;
                    }
                }

                if (foundGame){

                    // Get the player number !
                    int playerNumber = dataSnapshot.child(gameId).child("playerNumber").getValue(Integer.class);
                    int instantPlayerNumber = dataSnapshot.child(gameId).child("instantPlayerNumber").getValue(Integer.class);

                    if(flagOnce){
                        int newPlayerNumber = instantPlayerNumber+1;
                        databaseReference.child(gameId).child("instantPlayerNumber").setValue(newPlayerNumber);
                        flagOnce = false;
                    }

                    TextViewpeopleEnrolled = findViewById(R.id.textViewPeopleEnrolled);
                    TextViewpeopleEnrolled.setText(Integer.toString(instantPlayerNumber) + " / "  + Integer.toString(playerNumber) + " peoples enrolled !");

                    progressBar = findViewById(R.id.progressBar);
                    progressBar.setMax(playerNumber+1);
                    progressBar.setProgress(instantPlayerNumber+1);

                    Toast.makeText(getApplicationContext(), "Player added : " + Integer.toString(instantPlayerNumber + 1), Toast.LENGTH_LONG).show();

                    foundGame = false;
                }
                else{
                    Toast.makeText(getApplicationContext(), "Game not found !", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }

    private void updateInstantPlayer() {

        //databaseReference.child(gameId).child("instantPlayerNumber")

    }
}
