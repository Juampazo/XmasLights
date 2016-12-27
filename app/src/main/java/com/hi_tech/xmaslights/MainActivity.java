package com.hi_tech.xmaslights;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnIniciar;
    private Button btnDetener;

    private NotificationCompat.Builder notiBuilder;
    private NotificationManager nm;
    private View view;
    private int color;
    private int posicion = 0;
    private ColorBlink colorBlink;
    private long intervalo[] = {300, 500};


    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ToggleButton tgbtnTune;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = getCurrentFocus();

        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        btnDetener = (Button) findViewById(R.id.btnDetener);

        btnIniciar.setOnClickListener(this);
        btnDetener.setOnClickListener(this);


        initUI();

        tgbtnTune.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tgbtnTune.isChecked()) {
                            try {
                                createMediaPlayer(1);
                                mediaPlayer.start();
                                mediaPlayer.setLooping(true);
                            } catch (IllegalStateException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        } else {
                            mediaPlayer.pause();
                            mediaPlayer.setLooping(false);
                        }
                    }
                });}


    public void initUI() {
        tgbtnTune = (ToggleButton) findViewById(R.id.tgbtnTune);
    }

    public void createMediaPlayer(int val) {
        switch (val) {
            case 1:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tonito);
        }
    }

    //protected void onCreate(Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_main);



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnIniciar:
                iniciar();
                break;
            case R.id.btnDetener:
                detener();
                break;
        }
    }

    public void iniciar() {
        Toast.makeText(getApplicationContext(), "Bloquee el movil", Toast.LENGTH_LONG).show();
        nm = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
        notiBuilder = new NotificationCompat.Builder(MainActivity.this);
        notiBuilder.setContentTitle("Notificacion LED")
                .setContentText("Bloquee la pantalla")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher);
        colorBlink = new ColorBlink();
        colorBlink.execute();
    }

    public int generaColor() {
        return Color.rgb((int) (Math.random() * 255) + 1, (int) (Math.random() * 255) + 1, (int) (Math.random() * 255) + 1);
    }

    private class ColorBlink extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Object... arg0) {
            while (!colorBlink.isCancelled()) {
                color = generaColor();
                try {
                    if (posicion == intervalo.length - 1) {
                        posicion = 0;
                    }
                    Thread.sleep(intervalo[posicion]);
                    posicion++;
                    notiBuilder.setLights(color, 500, 500);
                    Notification notification = notiBuilder.build();
                    nm.notify(1, notification);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    public void detener() {
        nm.cancelAll();
        colorBlink.cancel(true);
    }




}