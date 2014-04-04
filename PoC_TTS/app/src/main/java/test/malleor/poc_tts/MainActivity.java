package test.malleor.poc_tts;

import android.annotation.TargetApi;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    static String TAG = "TTS";

    // controls
    private TextView input_text;
    private Button say_button;

    private TextToSpeech ttobj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public void onInit(int status) {
                Log.d(TAG, "Opened the TTS interface");

                if(status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(Locale.US);
                    Log.d(TAG, "Set locale to US");

                    if(ttobj.setOnUtteranceProgressListener(new UtteranceProgressListener(){
                        @Override
                        public void onStart(String s) {
                            Log.d(TAG, "Staring the synthesis...");
                        }

                        @Override
                        public void onDone(String s) {
                            Log.i(TAG, "Done with synthesis");
                            Log.i(TAG, "Enabling the button");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    say_button.setEnabled(true);
                                }
                            });
                        }

                        @Override
                        public void onError(String s) {
                            Log.e(TAG, "Failed to synthesise");
                            Toast.makeText(getApplicationContext(), "Failed to synthesise",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }) == TextToSpeech.SUCCESS)
                        Log.d(TAG, "Set the UtteranceProgressListener");
                    else
                        Log.e(TAG, "Failed to set the UtteranceProgressListener");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        input_text = (TextView) findViewById(R.id.editText);
        say_button = (Button) findViewById(R.id.button);
    }

    @Override
    public void onDestroy(){
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void sayIt(View view)
    {
        Log.d(TAG, "Fetching the word...");
        String text = input_text.getText().toString();
        Log.i(TAG, "The word is: \"" + text + "\"");

        Log.i(TAG, "Disabling the button");
        say_button.setEnabled(false);

        Log.d(TAG, "Speech synthesis...");
        HashMap<String, String> hashTts = new HashMap<String, String>();
        hashTts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id");
        if(ttobj.speak(text, TextToSpeech.QUEUE_FLUSH, hashTts) == TextToSpeech.SUCCESS)
            Log.i(TAG, "Queued for speaking");
        else
            Log.e(TAG, "Failed to queue for speaking");
    }

}
