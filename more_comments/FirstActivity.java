package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {

    private boolean permissionsGranted = false;
    private static final int STORAGE_PERMISSION_CODE = 128;

//-> onCreate xrhsimopoieitai fia arxikopoihsh activity kata thn ekkinhsh kai ekteleitai otan to activity ektestei gia proth fora
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//->apo auto to xml tha dhmioyrghthei h "eikona" ths activity
        setContentView(R.layout.activity_first);

        // Koitazei na dei an h egarmogi exei ta dikaiwmata gia na diavasei ta arxeia apo to kinito
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        // Dinei prosvasi sto koumpi kai pairnei ta stoixeia syndesis
        ImageButton goButton = findViewById(R.id.imageButtonGo);

        // Exigei ti tha ginei an patithei to koumpi
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // An den exoun dothei ta dikaiomata den synexizei
                if(permissionsGranted == false) {
                    Toast.makeText(FirstActivity.this, "Read storage permission denied", Toast.LENGTH_SHORT).show();
                    return ;
                }

                // Apothikeuei ta stoixeia tou Master
                EditText etIP =  findViewById(R.id.editTextIP);
                EditText etPort =  findViewById(R.id.editTextPort);

                // An einai adeia den kanei tipota kai enimrwnei
                if( etIP.getText().length() == 0 || etPort.getText().length() == 0 ) {
                    Toast.makeText( getApplicationContext(), "Fill IP and Port", Toast.LENGTH_LONG).show();
                    return ;
                }

                String master_ip = etIP.getText().toString();
                String master_port = etPort.getText().toString();

                // Ta proothei sto epomeno activity gia na syndethei ston Master
                Intent nextActivityIntent = new Intent(getApplicationContext(), PickGpxActivity.class);
                nextActivityIntent.putExtra( "master_ip", master_ip );
                nextActivityIntent.putExtra( "master_port", master_port );

                // Xekinaei to epomeno activity gia na dialexei to Gpx Arxeio
                startActivity( nextActivityIntent );
            }
        });
    }

    // Elegxei an h efarmogi exei ta diaiomata gia prosvasi sta arxeia tis efarmogis
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(FirstActivity.this, permission) == PackageManager.PERMISSION_DENIED)
//->an den exei dothei adeia zhta apo ton xristi 
            ActivityCompat.requestPermissions(FirstActivity.this, new String[] { permission }, requestCode);
        else
            permissionsGranted = true;
    }

//->kaleitai otan o xristos apodextei h aporiptei th adeia kai emfanizei katallhlo mhnyma
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // An o kwdikos tis aitisis tairiazei me auton pou exoume dilwsei
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FirstActivity.this, "Read storage permission granted", Toast.LENGTH_SHORT).show();
                permissionsGranted = true;
            }
            else {
                Toast.makeText(FirstActivity.this, "Read storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}