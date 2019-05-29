package kritoffer.minesweeper;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import kritoffer.minesweeper.util.Generator;
import kritoffer.minesweeper.util.PrintGrid;
import kritoffer.minesweeper.views.grid.Cell;

/**
 * Created by Kristoffer on 10.11.2017.
 */

public class GameEngine{
    private static GameEngine instance;

    public static final int BOMB_NUMBER = 10;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    private Context context;

    private boolean gameStarted = false;
    private long startTime = 0;
    private long endTime = 0;


    private Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT];

    public static GameEngine getInstance(){
        if( instance == null ){
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine(){}

    /**
     * Create a grid that can contain cells.
     * @param context
     */
    public void createGrid(Context context){
        Log.e("GameEngine","createGrid is working");
        this.context = context;

        // cteate the grid and store it
        int[][] GeneratedGrid = Generator.generate(BOMB_NUMBER,WIDTH,HEIGHT);
        PrintGrid.print(GeneratedGrid,WIDTH,HEIGHT);
        setGrid(context,GeneratedGrid);

        gameStarted = false;
        startTime = 0;
        endTime = 0;
    }

    /**
     * Set make the grid of cells from the generator.
     * @param context
     * @param grid
     */
    private void setGrid( final Context context, final int[][] grid ){
        for( int x = 0; x<WIDTH; x++ ){
            for( int y = 0; y<HEIGHT; y++ ){
                if( MinesweeperGrid[x][y] == null ){
                    MinesweeperGrid[x][y] = new Cell(context, x, y);
                }
                MinesweeperGrid[x][y].setValue(grid[x][y]);
                MinesweeperGrid[x][y].invalidate();
            }
        }
    }

    public View getCellAt(int position ){
        int x = position % WIDTH;
        int y = position / HEIGHT;

        return MinesweeperGrid[x][y];
    }

    /**
     * Return the cell given by x and y.
     * @param x
     * @param y
     * @return
     */
    public Cell getCellAt( int x , int y ){
        return MinesweeperGrid[x][y];
    }

    /**
     * Method handling what happens when a cell is clicked.
     * @param x Cell x position.
     * @param y Cell y position.
     */
    public void click( int x , int y ){
        if(!gameStarted){
            gameStarted = true;
            startTime = SystemClock.uptimeMillis();
        }

        if( x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT && !getCellAt(x,y).isClicked() && !getCellAt(x,y).isRevealed()){
            if( getCellAt(x,y).isFlagged() ){}

            else{
                getCellAt(x,y).setClicked();

                if( getCellAt(x,y).getValue() == 0 ){
                    for( int xt = -1 ; xt <= 1 ; xt++ ){
                        for( int yt = -1 ; yt <= 1 ; yt++){
                            click(x + xt , y + yt);
                        }
                    }
                }

                // Game lost!
                if( getCellAt(x,y).isBomb() ){
                    onGameLost();
                }
            }
        }

        checkEnd();
    }

    /**
     * Method checking if the game is lost or won.
     * @return  Returns a boolean. If false, the game is not finished.
     */
    private boolean checkEnd(){
        int bombNotFound = BOMB_NUMBER;
        int notRevealed = WIDTH * HEIGHT;
        int numFlag = 0;
        for ( int x = 0 ; x < WIDTH ; x++ ){
            for( int y = 0 ; y < HEIGHT ; y++ ){
                if( getCellAt(x,y).isRevealed() || getCellAt(x,y).isFlagged() ){
                    notRevealed--;
                }

                if( getCellAt(x,y).isFlagged() && getCellAt(x,y).isBomb() ){
                    bombNotFound--;
                }
                if( getCellAt(x,y).isFlagged()){
                    numFlag++;
                }
            }
        }

        // Game won!!!
        if( (bombNotFound == 0 && numFlag <= BOMB_NUMBER) || notRevealed == BOMB_NUMBER ){
            onGameWon();
        }
        return false;
    }

    /**
     * Set or remove flag.
     * @param x X position
     * @param y Y position
     */
    public void flag( int x , int y ){
        if( !getCellAt(x,y).isRevealed() ){
            boolean isFlagged = getCellAt(x,y).isFlagged();
            getCellAt(x,y).setFlagged(!isFlagged);
            getCellAt(x,y).invalidate();
            checkEnd();
        }
    }

    /**
     * Method taking care of everything that happens when the game i won.
     */
    private void onGameWon(){
        endTime = SystemClock.uptimeMillis();
        int timeUsed = (int) ((endTime-startTime)/1000);

        MediaPlayer mp = MediaPlayer.create(context, R.raw.winning_sound);
        mp.start();

        Toast.makeText(context,"Game won", Toast.LENGTH_SHORT).show();

        // Update number: won
        String readWon = readFromFile(context, "won.txt");
        int prevWin = Integer.parseInt(readWon);
        prevWin++;
        String antWon = "" + prevWin;
        writeToFile(antWon, context, "won.txt");

        // Update best time if so
        String readBest = readFromFile(context, "best.txt");
        int best = Integer.parseInt(readBest);
        if(best == 0){
            best = 999;
        }
        if(timeUsed < best){
            String newBest = "" + timeUsed;
            writeToFile(newBest, context, "best.txt");
        }
        else{
            String bestTime = "" + best;
            writeToFile(bestTime, context, "best.txt");
        }

        // Update average time
        String readAverage = readFromFile(context, "average.txt");
        int prevAverage = Integer.parseInt(readAverage);
        prevAverage = prevAverage + timeUsed;
        String avg = "" + prevAverage;
        writeToFile(avg, context, "average.txt");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Game won in " + timeUsed + "s!")
                .setMessage("Would you like to start a new game?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GameEngine.getInstance().createGrid(context);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Method taking care of everything that happens when the game is lost.
     */
    private void onGameLost(){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.bomb_sound);
        mp.start();

        Toast.makeText(context,"Game lost", Toast.LENGTH_SHORT).show();

        String read = readFromFile(context, "lost.txt");
        int prevLost = Integer.parseInt(read);
        prevLost++;
        String antLost = "" + prevLost;
        writeToFile(antLost, context, "lost.txt");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Game lost!")
                .setMessage("Would you like to start a new game?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GameEngine.getInstance().createGrid(context);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        for ( int x = 0 ; x < WIDTH ; x++ ) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x,y).setRevealed();
            }
        }
    }

    /**
     * Method used to write statistic to files.
     * @param data
     * @param context
     * @param filename
     */
    private void writeToFile(String data, Context context, String filename) {
        try {
            File path = context.getFilesDir();
            File file = new File(path, filename);

            FileOutputStream stream = new FileOutputStream(file);
            try{
                stream.write(data.getBytes());
            }finally {
                stream.close();
            }
        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Method used to read the content from a file.
     * @param context
     * @param filename  Name of the file to read from.
     * @return          Returns the content in a file.
     */
    private String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            ret = "0";
        }

        return ret;
    }
}
