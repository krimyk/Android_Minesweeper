package kritoffer.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Game extends AppCompatActivity {

    GameEngine gameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameEngine = GameEngine.getInstance();
        gameEngine.createGrid(this);
    }

    public void restartGame(View view) {
        gameEngine.createGrid(this);
    }
}
