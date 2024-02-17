package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class PickGpxActivity extends AppCompatActivity {

//->dexetai ip kai port apo thn firstactivity
    private String master_ip;
    private String master_port;
//->metablith gia ekkinish drasthriothtas pou perimenei apotelesma gia na to epeksergastei
    private ActivityResultLauncher<Intent> resultIntentLauncher;

//->kaleitai kata th dhmioyrgia ths activity 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//->apo auto to xml tha dhmioyrghthei h "eikona" ths activity
        setContentView(R.layout.activity_pick_gpx);


        // Pairnei tin IP kai to port apo to proigoumeno activity
        Intent previousIntent = getIntent();
        master_ip = previousIntent.getStringExtra("master_ip");
        master_port = previousIntent.getStringExtra("master_port");


        // Dinei prosvasi sto koumpi gia na dialexei arxeio Gpx
        Button pickButton = findViewById(R.id.buttonPickGpx);

        // Exigei ti tha ginei an patithei to koumpi
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // An patisei to koumpi tote dialegei arxeio
                selectGPX();
            }
        });


        // Auto to tmima kaleitai otan o xristis exei epilexei arxeio
        resultIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {

//->pairnei ta apotelesmata apo to resultIntentLauncher
                Intent data = result.getData();

                // An eixe epilexei arxeio
                if (data != null) {

                    // Pairnei to Uri tou arxeiou
                    Uri uri = data.getData();

//->dhmiourgei neo intent me auta ta uri, port kai ip 
                    // Ta proothei sto epomeno activity gia na syndethei ston Master
                    Intent nextActivityIntent = new Intent(getApplicationContext(), ResultsActivity.class);
                    nextActivityIntent.putExtra( "master_ip", master_ip );
                    nextActivityIntent.putExtra( "master_port", master_port );
                    MasterFetcherAsyncTask.uri = uri;

                    // Xekinaei to activity gia na provalei ta apotelesmata
                    startActivity( nextActivityIntent );
                }
            }
        });
    }

    // Synartisi gia na dialegei to arxeio
    private void selectGPX()
    {
        // Xekinei ena intent gia pou na dialegei to arxeio
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Orizei ton typo tou arxeiou
        intent.setType("*/*");
//->ekkinei th drastriothta kai anamenei apotelesma mesw ths launch kai otan oloklhrwthei ta apothhkuei sthn onActivityResult
        resultIntentLauncher.launch(intent);
    }
}