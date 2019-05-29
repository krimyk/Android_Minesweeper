package kritoffer.minesweeper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import kritoffer.minesweeper.views.Statistic;

public class MainActivity extends AppCompatActivity {

    GameEngine gameEngine;
    public Context mainContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("MainActivity","onCreate");

    }


    public void startGame(View view) {
        Intent startGameIntent = new Intent(this, Game.class);
        startActivity(startGameIntent);
    }

    public void statistic(View view){
        Intent statisticIntent = new Intent(this, Statistic.class);
        startActivity(statisticIntent);
    }
}
