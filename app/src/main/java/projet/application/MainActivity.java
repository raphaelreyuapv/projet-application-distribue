package projet.application;

import static android.speech.SpeechRecognizer.createOnDeviceSpeechRecognizer;
import static android.speech.SpeechRecognizer.createSpeechRecognizer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import projet.application.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Context context;
    private static final String TAG = "MyActivity";
    private View myView;
    private boolean recording = false;
    SpeechRecognizer speechRec = null;
    private String url = "http://10.0.2.2:5000/";
    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private String postBodyString;
    private MediaType mediaType;
    private RequestBody requestBody;
    private Response serverResponse;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(R.layout.activity_main);
        fragment = new ListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();


        setSupportActionBar(binding.toolbar);

        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (speechRec==null && !SpeechRecognizer.isRecognitionAvailable(context)) {
                    Snackbar.make(view, "Votre telephone ne permet pas la reconnaissance vocale", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else{

                    if(speechRec==null){

                        speechRec = SpeechRecognizer.createSpeechRecognizer(context);
                        speechRec.setRecognitionListener(new listener());
                        myView = view;

                        String requiredPermission = Manifest.permission.RECORD_AUDIO;

                        // If the user previously denied this permission then show a message explaining why
                        // this permission is needed
                        if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                            requestPermissions(new String[]{requiredPermission}, 101);
                        }
                    }

                    if(!recording) {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        speechRec.startListening(intent);
                    }
                    else {

                        speechRec.stopListening();
                    }

                    recording = !recording;

                }

            }
        });
        getListSongServeur(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
            Snackbar.make(myView, "Debut de la reconnaissance audio", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            if(error==SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error==SpeechRecognizer.ERROR_NO_MATCH){

                Snackbar.make(myView, "Je n'ai pas compris votre question.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else{
                Snackbar.make(myView, "error " + error, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }

            Snackbar.make(myView, data.get(0).toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            postRequest(data.get(0).toString(),url+"recon");
            openSongPage("VLC stream");
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    private void playSong(){
        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        if(mLibVLC==null) {
            mLibVLC = new LibVLC(this, args);
            mMediaPlayer = new MediaPlayer(mLibVLC);
            try {
                final Media media = new Media(mLibVLC, Uri.parse("http://10.0.2.2:1234/stream.mp3"));
                mMediaPlayer.setMedia(media);
                media.release();
                mMediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void stopSong(){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mLibVLC.release();
            mMediaPlayer = null;
            mLibVLC = null;
    }
    private void getListSongServeur( String URL){
        JSONArray json = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:5000/search")
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();
            Log.d("fuckyou",jsonData);
            json = new JSONArray(jsonData);
        }catch(Exception e){
            Log.d("fuckyou",e.toString());

        }
        Log.d(TAG, "request sent");

        if(fragment.getClass()==ListFragment.class){



            //Log.d(TAG, "verif valeur = " + serverResponse);


            // Convert String to json object
            try {

                // get LL json object


                // get value from LL Json Object
                String str_value=json.getString(0);

                Log.d(TAG, str_value);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                for(int i=0;i<json.length();i++) {
                    ((ListFragment) fragment).addToMusicArrayList(json.getString(i), json.getString(i));

                }
            }catch(Exception e){

            }
            fragment = new ListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        }

    }

    private void openSongPage(String name) {
                fragment = new SecondFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

                ((SecondFragment) fragment).setTitle("Stream VLC");
                ((SecondFragment) fragment).setArtist("Stream VLC");




    }

    private RequestBody buildRequestBody(String msg) {
        postBodyString = msg;
        mediaType = MediaType.parse("text/plain");
        requestBody = RequestBody.create(postBodyString, mediaType);
        return requestBody;
    }

    public void postRequest(String message, String URL) {
        RequestBody requestBody = buildRequestBody(message);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(MainActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();

                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String cont = response.body().string();
                            Toast.makeText(MainActivity.this, cont, Toast.LENGTH_LONG).show();
                            if(cont.equals("OK")){
                                playSong();
                            }else if(cont.equals("stop")){
                                    stopSong();
                            }
                            Log.d(TAG, "response received");
                            serverResponse = response;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
