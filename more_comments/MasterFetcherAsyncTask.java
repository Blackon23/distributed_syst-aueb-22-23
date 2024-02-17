package com.example.myapplication;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

//->ektelei ypologismou sto ypobathro xoris na enoxlei to nhma toy xristh
// Async task gia na ferneit is plirofories apo ton Master
public class MasterFetcherAsyncTask extends AsyncTask {

    private String master_ip;
    public static Uri uri;
    private int master_port;
    private ResultsActivity resultsActivity;

    public MasterFetcherAsyncTask(String master_ip, String filepathURI, int master_port, ResultsActivity resultsActivity) {
        this.master_ip = master_ip;
        this.master_port = master_port;
        this.resultsActivity = resultsActivity;
    }

//->ektelesh sto paraskhnio
    @Override
    protected Object doInBackground(Object[] objects) {


        try {
            InputStream inputStream = resultsActivity.getApplicationContext().getContentResolver().openInputStream(uri);

//->diavazei to periexomeno apo to uri kai to apothhkeyei sto string s
            String s = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            System.out.println(s);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
//->kaleitai h presentFinalResults gia emfanish apotelesmatwn
        resultsActivity.presentFinalResults();
    }
}
