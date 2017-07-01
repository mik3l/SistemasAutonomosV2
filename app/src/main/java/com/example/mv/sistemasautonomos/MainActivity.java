package com.example.mv.sistemasautonomos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private Button openMic;
    private TextView showVoicetext;
    private final int REQ_CODE_SPEECH_OUTPUT = 143;
    private TextView verifica;
    private static String CAPT_VOZ = "http://192.168.193.248:80/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openMic = (Button) findViewById(R.id.button2);
        showVoicetext = (TextView) findViewById(R.id.voz);
        verifica = (TextView) findViewById(R.id.textView3);
        openMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOpenMic();
            }
        });

    }

    private void btnOpenMic(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hiii fala agora");

        try{
            startActivityForResult(intent, REQ_CODE_SPEECH_OUTPUT);
        }
            catch (ActivityNotFoundException tim){

            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQ_CODE_SPEECH_OUTPUT:{
                if (resultCode == RESULT_OK && data != null){
                    ArrayList<String> voiceInText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    showVoicetext.setText(voiceInText.get(0));
                    new SendTask(voiceInText.get(0)).execute();
                    }
                }

                break;
            }
        }

    public static URL buildUrl(String Voz) {
        Uri.Builder uriBuilder = Uri.parse(CAPT_VOZ).buildUpon().appendPath(Voz);
        uriBuilder.build();
        URL url = null;

        try {
            url = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            Scanner scanner = new Scanner(urlConnection.getInputStream());
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public class SendTask extends AsyncTask<String, Void, String> {

        public SendTask(String voz) {

            this.voz = voz;
        }

        String voz;

        @Override
        protected String doInBackground(String... strings) {
            URL url = buildUrl(voz);

            String result = null;
            try {
                result = getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }


}


