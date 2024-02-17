package com.example.myapplication;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.stream.Collectors;

import common.ResultObject;


// Async task gia na ferneit is plirofories apo ton Master
public class MasterFetcherAsyncTask extends AsyncTask {

    private String master_ip;
    public static Uri uri;
    private int master_port;
    private ResultsActivity resultsActivity;

    private ResultObject ro, tro;

    public MasterFetcherAsyncTask(String master_ip, String filepathURI, int master_port, ResultsActivity resultsActivity) {
        this.master_ip = master_ip;
        this.master_port = master_port;
        this.resultsActivity = resultsActivity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {


        try {

            // Ανοίγει το αρχείο με βάση το uri του και το φορτώνει σε string
            InputStream inputStream = resultsActivity.getApplicationContext().getContentResolver().openInputStream(uri);
            String s = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            // Συνδέουμε τον client στον server
            Socket socket = new Socket(master_ip, master_port);

            // Στέλνουμε το αρχείο GPX στον server
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(s);

            // Παίρνουμε τα αποτελέσματα από τον server
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

            // Παίρνει τα ατομικά αποτελέσματα
            ro = (ResultObject) inStream.readObject();

            // Παίρνει τα συνολικά αποτελέσματα
            tro = (ResultObject) inStream.readObject();

            // Κλείνουμε το socket
            socket.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        resultsActivity.presentFinalResults(ro, tro);
    }
}
