package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import common.ResultObject;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Pairnei tin IP kai to port kai to URI tou arxeiou apo to proigoumeno activity
        Intent previousIntent = getIntent();
        final String master_ip = previousIntent.getStringExtra("master_ip");
        final int master_port = Integer.parseInt( previousIntent.getStringExtra("master_port") );
        final String file_uri_path = previousIntent.getStringExtra("file_uri_path");

        // Xekinaei to AsyncTask gia na ferei ta apotelesmata apo ton Master
        MasterFetcherAsyncTask masterFetcherAsyncTask = new MasterFetcherAsyncTask(master_ip, file_uri_path, master_port, this);
        masterFetcherAsyncTask.execute();
    }

    // Synartisi pou emfanizei ston xristi ta telika apotelesmata
    public void presentFinalResults(ResultObject ro, ResultObject tro){

        TextView view = findViewById(R.id.textViewDis);
        view.setText( String.format("%.4f                                    %.4f", ro.getTotalDistance(), tro.getTotalDistance()) );

        view = findViewById(R.id.textViewElev);
        view.setText( String.format("%.4f                                    %.4f", ro.getTotalElevation(), tro.getTotalElevation()) );

        view = findViewById(R.id.textViewSpeed);
        view.setText( String.format("%.4f                                    %.4f", ro.getAverageSpeed(), tro.getAverageSpeed()) );

        view = findViewById(R.id.textViewTime);
        view.setText( String.format("%d                                         %d", ro.getTotalTime(), tro.getTotalTime()) );
    }
}