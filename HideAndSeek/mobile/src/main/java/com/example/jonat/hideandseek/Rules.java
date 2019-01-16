package com.example.jonat.hideandseek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class Rules extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        TextView rules = findViewById(R.id.fullRules);

        //Set rules text
        rules.setText("The rules are simple. Two teams are formed: the zombies and the survivors. Each team is composed of one and only one master, and runners (all the non-master players). The goal of the zombies is to kill all the survivors, the goal of the survivors to collect resources to be able to survive for a while longer.\n" +
                "\n" +
                "Survivors:\n" +
                "\n" +
                "Master: The overseer stays at the survivor HQ. Thanks to the leftover pre-apocalyptic technologies, he has a map that allows him to track both his runners and the zombies. Additionally, he can see targeted areas on the map where the runners need to stay for as long as possible to score points. He can send commands to his runner by tapping on their icon on the map and then using the command buttons (A \"go\" command must always be followed by a direction or nothing will be sent!)\n" +
                "\n" +
                "Runners: As previously stated, the runners need to collect resources. In order to do that, they \tneed to stay in defined areas. The trick is that they don't know the location of these targets, \tonly the overseer can see them. For each second a runner stays in a target, he gets one point. \tMultiple runners can get points at the same time from the same targets: the more you are, the faster you collect, but you become easy targets for the zombies. \n" +
                "\n" +
                "Zombies:\n" +
                "\n" +
                "Master: The psychic is a special kind of zombie. His extra-sensory power allows him to localize the survivors and the other zombies. In a similar way as the overseer, he can send commands to the zombies to guide them to the survivors.\n" +
                "\n" +
                "Runners: The zombies need to catch all the survivors and by catching, we mean getting close enough (less than 10m). Since they can't always see the survivors, they need to be careful about the psychic information, that are the only way to efficiently find their meal. There is no need to physically catch the prey: once she is dead, she will receive un message on its device and must return to HQ.     \n" +
                "\n" +
                "How to play a game: \n" +
                "\n" +
                "One and only one player creates a game, stating the number of players expected (including masters). He can then communicate the game code to the other players that all choses a role and a team (it a required to have exactly one master for each team). Once every player has joined the game, the players receive a quick recall about their goals. After everyone went through the rules by tapping the screen, the game starts, the map is initialized, and the runners get and advantage of 3 minutes to leave HQ. After these 3minutes they can start scoring and the zombies start their hunt. Once either conditions for winning is met (all survivors are dead, or the targeted score is reached). The players receive the information on their device and return to the HQ.\n");

        //Enable scrolling
        rules.setMovementMethod(new ScrollingMovementMethod());
    }
}
