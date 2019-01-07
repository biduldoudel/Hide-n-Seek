package com.example.jonat.hideandseek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int REGISTER_PROFILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonNewGame = findViewById(R.id.NewGameButton);
        buttonNewGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, RegisterNewGame.class);
                startActivity(intent);

            }

        });

        Button buttonJoinGame = findViewById(R.id.JoinGameButton);
        buttonJoinGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, RegisterNewPlayer.class);
                intent.putExtra("RegisterNewGame", false);
                startActivity(intent);

            }

        });
    }
}
