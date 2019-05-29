package kritoffer.minesweeper.views;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import kritoffer.minesweeper.R;

public class Statistic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        printStatistic();
    }

    private void printStatistic(){
        TextView wonView = (TextView) findViewById(R.id.textView_game_won);
        TextView lostView = (TextView) findViewById(R.id.textView_game_lost);
        TextView percentView = (TextView) findViewById(R.id.textView_percent);
        TextView totView = (TextView) findViewById(R.id.textView_total);
        TextView bestView = (TextView) findViewById(R.id.textView_best);
        TextView avgView = (TextView) findViewById(R.id.textView_average);

        int won = Integer.parseInt(readFromFile(this, "won.txt"));
        int lost = Integer.parseInt(readFromFile(this, "lost.txt"));
        int tot = won + lost;
        int percent;
        int avg;
        if(tot == 0){
            percent = 0;
            avg = 0;
        }
        else {
            percent = (won*100)/(tot);
            avg = (Integer.parseInt(readFromFile(this, "average.txt")))/won;
        }
        int best = Integer.parseInt(readFromFile(this, "best.txt"));

        wonView.setText("Games won: " + won);
        lostView.setText("Games lost: " + lost);
        percentView.setText("Win rate: " + percent + "%");
        totView.setText("Total: " + tot);
        bestView.setText("Best time: " + best + "s");
        avgView.setText("Average time: " + avg + "s");
    }

    /**
     * Method used to read out the value in a file.
     * @param context
     * @param filename
     * @return          Returns a string of the content in file.
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

    /**
     * Method that reset all statistic by setting the local files to "0".
     * @param view
     */
    public void resetStatisticFile(View view) {
        final Context context = this;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Reset statistic")
                .setMessage("Remove all statistic? This action can not be undone.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        String data = "0";
                        String badTime = "999";
                        TextView lostView = (TextView) findViewById(R.id.textView_game_lost);
                        TextView wonView = (TextView) findViewById(R.id.textView_game_won);
                        TextView percentView = (TextView) findViewById(R.id.textView_percent);
                        TextView totView = (TextView) findViewById(R.id.textView_total);
                        TextView bestView = (TextView) findViewById(R.id.textView_best);
                        TextView avgView = (TextView) findViewById(R.id.textView_average);

                        lostView.setText("Games won: " + data);
                        wonView.setText("Games lost: " + data);
                        percentView.setText("Win rate: " + data + "%");
                        totView.setText("Total: " + data);
                        bestView.setText("Best time: " + badTime + "s");
                        avgView.setText("Average time: " + data + "s");

                        try {
                            File path = context.getFilesDir();
                            File fileLost = new File(path, "lost.txt");
                            File fileWon = new File(path, "won.txt");
                            File filePersent = new File(path, "percent.txt");
                            File fileBest = new File(path, "best.txt");
                            File fileAvg = new File(path, "average.txt");

                            FileOutputStream streamLost = new FileOutputStream(fileLost);
                            FileOutputStream streamWon = new FileOutputStream(fileWon);
                            FileOutputStream streamPercent = new FileOutputStream(filePersent);
                            FileOutputStream streamBest = new FileOutputStream(fileBest);
                            FileOutputStream streamAvg = new FileOutputStream(fileAvg);

                            try{
                                streamLost.write(data.getBytes());
                                streamWon.write(data.getBytes());
                                streamPercent.write(data.getBytes());
                                streamBest.write(badTime.getBytes());
                                streamAvg.write(data.getBytes());
                            }finally {
                                streamLost.close();
                                streamWon.close();
                                streamPercent.close();
                                streamBest.close();
                                streamAvg.close();
                            }
                        }
                        catch (Exception e) {
                            Log.e("Exception", "File write failed: " + e.toString());
                        }
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
}
