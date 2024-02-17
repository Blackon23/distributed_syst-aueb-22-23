package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class PickGpxActivity extends AppCompatActivity {

    private String master_ip;
    private String master_port;
    private ActivityResultLauncher<Intent> myLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_gpx);


        // Παίρνει τα στοιχεία σύνδεσης από το προηγούμενο activity
        Intent previousIntent = getIntent();
        master_ip = previousIntent.getStringExtra("master_ip");
        master_port = previousIntent.getStringExtra("master_port");


        // Δίνει πρόσβαση στο κουμπί ώστε να διαλέξει αρχείο Gpx
        Button pickButton = findViewById(R.id.buttonPickGpx);

        // Εξηγεί τι θα γίνει αν πατηθεί το κουμπί επιλογής αρχείου
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_file_selection();
            }
        });


        // Εξηγούμε τι θα γίνει όταν έχει επιλεχθεί το αρχείο
        // πώς το όνομα του θα επιστρέψει στο PickGpxActivity
        myLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {

                Intent data = result.getData();

                // Αν όντως επέλεξε και δεν ακύρωσε την διαδικασία
                if (data != null) {

                    // Βρίσκει το uri του αρχείου
                    Uri uri = data.getData();


                    // Μεταφέρουμε με το Intent τα στοιχεία του Master για να μπορέσει το επόμενο activity να συνδεθεί
                    Intent nextActivityIntent = new Intent(getApplicationContext(), ResultsActivity.class);
                    nextActivityIntent.putExtra( "master_ip", master_ip );
                    nextActivityIntent.putExtra( "master_port", master_port );
                    MasterFetcherAsyncTask.uri = uri;
                    startActivity( nextActivityIntent );
                }
            }
        });
    }

    /*
        Ξεκινάει ένα νέο Intent για πρόσβαση στα αρχεία του κινητού
        και από εκεί ο χρήστης διαλάγει ποιό θέλει.
     */
    private void user_file_selection()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Ορισμός τύπων αρχείων
        String [] mimeTypes = {"application/gpx+xml", "application/gpx", "text/xml", "application/octet-stream"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        myLauncher.launch(intent);
    }
}