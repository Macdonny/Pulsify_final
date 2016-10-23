package com.pulsify.my.pulsify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Random;

import static android.R.id.message;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.harman.pulsesdk.DeviceModel;
import com.harman.pulsesdk.ImplementPulseHandler;
import com.harman.pulsesdk.PulseColor;
import com.harman.pulsesdk.PulseHandlerInterface;
import com.harman.pulsesdk.PulseNotifiedListener;
import com.harman.pulsesdk.PulseThemePattern;

import java.util.Random;

import java.util.ArrayList;
import java.lang.String;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    Random randomPattern;
    PulseColor bgColor;
    PulseHandlerInterface pulseHandler;

    protected static final int RESULT_SPEECH = 1;
    private String s;
    int randomNumber = 0, r, g, b;
    private ImageButton btnSpeak;
    private TextView txtText;

    //--------------------------------------------------

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "c17d2870fc3c4b95a62998eae6c01aa9";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "pulsify-app-login://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;
    private String mood;
    SpotifyModel spotifyModel = new SpotifyModel();

    //--------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        //--------------------------------------------------

        pulseHandler = new ImplementPulseHandler();

        pulseHandler.registerPulseNotifiedListener(new PulseNotifiedListener() {
            @Override
            public void onConnectMasterDevice() {

            }

            @Override
            public void onDisconnectMasterDevice() {

            }

            @Override
            public void onRetBrightness(int i) {

            }

            @Override
            public void onLEDPatternChanged(PulseThemePattern pulseThemePattern) {

            }

            @Override
            public void onSoundEvent(int i) {

            }

            @Override
            public void onRetCaptureColor(PulseColor pulseColor) {

            }

            @Override
            public void onRetCaptureColor(byte b, byte b1, byte b2) {

            }

            @Override
            public void onRetSetDeviceInfo(boolean b) {

            }

            @Override
            public void onRetGetLEDPattern(PulseThemePattern pulseThemePattern) {

            }

            @Override
            public void onRetRequestDeviceInfo(DeviceModel[] deviceModels) {

            }

            @Override
            public void onRetSetLEDPattern(boolean b) {

            }
        });

        pulseHandler.SetBrightness(10);
        pulseHandler.ConnectMasterDevice(this);

        //button Google
        txtText = (TextView) findViewById(R.id.txtText);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
//--------------------------------------------------------------------------------------------------------------
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));

                    s = text.get(0);
                    s = s.toLowerCase();

                    switch (s) {
                        case "excited": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Fire);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "FIRE", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "angry": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Canvas);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "CANVAS", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "relax": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Firefly);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "FIREFLY", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "crazy": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Firework);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "FIREWORK", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "sleepy": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Hourglass);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "HOURGLASS", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "thoughtful": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Rain);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "RAIN", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "jazzy": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Ripple);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "RIPPLE", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "sparkle": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Star);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "STAR", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "dance": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Traffic);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "TRAFFIC", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "do something": {
                            pulseHandler.SetLEDPattern(PulseThemePattern.PulseTheme_Wave);
                            Toast.makeText(getApplicationContext(), "LED pattern shuffled successfully to " + "WAVE", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    break;
                }

            }
        }

    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


    @Override
    public void onLoggedIn() {
        Log.d("HomeActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("HomeActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.d("HomeActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("HomeActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d("HomeActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("HomeActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("HomeActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
