package com.example.final_6206021621066_1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GamePlay(this));
    }
}

class GamePlay extends View implements View.OnTouchListener {
    private Paint mPaint;

    private int score, time;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int[] FRUITS_IMAGES = {
            R.drawable.apple,
            R.drawable.apricot,
            R.drawable.banana,
            R.drawable.cherry,
            R.drawable.mango,
            R.drawable.pear,
            R.drawable.strawberry,
            R.drawable.watermalon
    };
    private int[] IMAGE_WIDTH = new int[8];
    private int[] IMAGE_HEIGHT = new int[8];
    private int[] SPEED = new int[8];
    private int gunSound;

    private float[] X = new float[8];
    private float[] Y = new float[8];

    private boolean finished = false;
    private boolean[] HIT = new boolean[8];

    private Random random = new Random();

    private CountDownTimer timer1, timer2;

    private SoundPool soundPool;

    private Bitmap[] FRUITS = new Bitmap[8];


    public GamePlay(Context context) {
        super(context);
        setBackgroundColor(Color.rgb(238, 165, 165));
        setOnTouchListener(this);
        mPaint = new Paint();

        score = 0;
        time = 31;

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        gunSound = soundPool.load(context, R.raw.gun2, 1);

        for (int i = 0; i < 8; i++) {
            FRUITS[i] = BitmapFactory.decodeResource(getResources(), FRUITS_IMAGES[i]);
            IMAGE_WIDTH[i] = FRUITS[i].getWidth();
            IMAGE_HEIGHT[i] = FRUITS[i].getHeight();
            X[i] = random.nextInt(screenWidth - IMAGE_WIDTH[i]);
            SPEED[i] = random.nextInt(10) + 10;
        }

        timer1 = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                --time;
                invalidate();
            }

            @Override
            public void onFinish() {
                finished = true;
                invalidate();
            }
        };

        timer2 = new CountDownTimer(30000, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                for (int i = 0; i < 8; i++) {
                    Y[i] += SPEED[i];
                    if (Y[i] > screenHeight - IMAGE_HEIGHT[i]) {
                        Y[i] = 0 - IMAGE_HEIGHT[i];
                        X[i] = random.nextInt(screenWidth - IMAGE_WIDTH[i]);
                    }
                }
                invalidate();
            }

            @Override
            public void onFinish() {
                finished = true;
                invalidate();
            }
        };

        timer1.start();
        timer2.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (finished) {
            finished = false;
            time = 31;
            timer1.start();
            timer2.start();
            score = 0;
            invalidate();
        } else {
            float x = event.getX();
            float y = event.getY();

            for (int i = 0; i < 8; i++) {
                if (x > X[i] && x < X[i] + IMAGE_WIDTH[i]) {
                    if (y > Y[i] && y < Y[i] + IMAGE_HEIGHT[i]) {
                        score++;
                        soundPool.play(gunSound, 1, 1, 1, 0, 1);
                        HIT[i] = true;
                        invalidate();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (finished) {
            mPaint.setColor(Color.rgb(255, 20, 147));
            mPaint.setTextSize(80);
            mPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("END GAME", screenWidth / 2, screenHeight / 2 - 500, mPaint);
            canvas.drawText("Your Score : " + score, screenWidth / 2, screenHeight / 2 - 200, mPaint);
            canvas.drawText("Touch for Play Game" , screenWidth / 2 , screenHeight / 2 , mPaint);
        } else {
            mPaint.setColor(Color.BLUE);
            mPaint.setTextSize(70);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Score : " + score, 20, 70, mPaint);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Time : " + time, screenWidth - 20, 70, mPaint);

            for (int i = 0; i < 8; i++) {
                if (HIT[i]) {
                    Y[i] = 0 - IMAGE_HEIGHT[i];
                    X[i] = random.nextInt(screenWidth - IMAGE_WIDTH[i]);
                    HIT[i] = false;
                }

                canvas.drawBitmap(FRUITS[i], X[i], Y[i], null);
            }
        }
    }
}