package com.example.administrator.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017-03-11.
 */

public class GameView extends TextureView implements TextureView.SurfaceTextureListener,
        View.OnTouchListener {

    private Thread mThread;
    volatile private boolean mIsRunable;
    volatile private  float mTouchedX;
    volatile private  float mTouchedY;
    private Handler mHandler;
    private static final String KEY_LIFE = "life";
    private static final String KEY_GAME_START_TIME = "game_start_time";
    private static final String KEY_BALL = "ball";
    private static final String KEY_BLOCK = "block";
    private final Bundle mSaveInstanceState;

    public GameView(final Context context, Bundle savedInstancesState) {
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        mSaveInstanceState = savedInstancesState;
        mHandler = new Handler() {
            //UI Handler에서 실해오디는 Handler
            @Override
            public void handleMessage(Message  message) {
                //실행할처리
                Intent intent = new Intent(context,ClearActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(message.getData());
                context.startActivity(intent);
            }
        };
    }


    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_LIFE,mLife);
        outState.putLong(KEY_GAME_START_TIME,mGameStartTime);
        outState.putBundle(KEY_BALL, mBall.save(getWidth(),getHeight()));
        for (int i = 0;i<BLOCK_COUNT; i++){
            outState.putBundle(KEY_BLOCK + String.valueOf(i),mBlockList.get(i).save());
        }
    }


    public void start(){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                boolean isEnableSound = sharedPreferences.getBoolean("enable_sound",true);
                boolean isEnableVibrate = sharedPreferences.getBoolean("enable_vibrate",true);

//                paint.setColor(Color.RED);
//                paint.setStyle(Paint.Style.FILL);
                int collisionTime = 0;
                int soundIndex = 0;
                while (true) {
                    long startTime = System.currentTimeMillis();
                    synchronized (GameView.this) {
                        if (!mIsRunable) {
                            break;
                        }
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        canvas.drawColor(Color.BLACK);
                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft,padRight);
                        mBall.move();
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;

                        if (ballLeft < 0 && mBall.getSpeedX() < 0 || ballRight >= getWidth() && mBall.getSpeedX() > 0) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            if (isEnableSound) {
                                toneGenerator.startTone(ToneGenerator.TONE_DTMF_5, 10);
                            }
                            //가로방향 벽에 부딪혔으므로 가로 속도를 반전
                        }
                        if (ballTop < 0  ) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                            if (isEnableSound) {
                                toneGenerator.startTone(ToneGenerator.TONE_DTMF_3, 10);
                            }
                            //세로방향 벽에 부딪혔으므로 세로 속도를 반전
                        }
                        if (ballTop > getHeight()) {
                            mLife --;
                            if (mLife > 0 ) {
                                mBall.reset();
                            } else {
                                if (isEnableSound) {
                                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE);
                                }
                                unlockCanvasAndPost(canvas);
                                Message message = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR,false);
                                bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT,getBlockCount());
                                bundle.putLong(ClearActivity.EXTRA_TIME,System.currentTimeMillis()-mGameStartTime);
                                message.setData(bundle);
                                mHandler.sendMessage(message);
                                return;
                            }
                        }

                        Block leftBlock = getBlock(ballLeft,mBall.getY());
                        Block topBlock = getBlock(mBall.getX(),ballTop);
                        Block rightBlock = getBlock(ballRight,mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(),ballBottom);

                        boolean isCollision = false;

                        if (leftBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                            isCollision = true;
                        }
                        if (topBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                            isCollision = true;
                        }
                        if (rightBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                            isCollision = true;
                        }
                        if (bottomBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                            isCollision = true;
                        }
                        if (isCollision) {
                            //블록에 부딪힌경우
                            if (collisionTime > 0) {
                                if (soundIndex < 15) {
                                    soundIndex++;
                                }
                            } else {
                                soundIndex = 1;
                            }
                            collisionTime = 10;
                            if (isEnableSound) {
                                toneGenerator.startTone(soundIndex, 10);
                            }
                        }else if (collisionTime > 0) {
                            //블록에 부딪히지 않은 경우 남은시간을 줄임
                            collisionTime --;
                        }


                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();
                        if (ballBottom > padTop && ballBottom - ballSpeedY < padTop && padLeft < ballRight && padRight > ballLeft) {
                            if (isEnableSound) {
                                toneGenerator.startTone(ToneGenerator.TONE_DTMF_7, 10);
                            }
                            if (isEnableVibrate) {
                                vibrator.vibrate(30);
                            }
                            if (ballSpeedY < mBlockHeight / 3) {
                                ballSpeedY *= -1.05f;
                            } else {
                                ballSpeedY = -ballSpeedY;
                            }
                            float ballSpeedX = mBall.getSpeedX()+ (mBall.getX()-mTouchedX)/10;
                            if (ballSpeedX > mBlockWidth /5) {
                                ballSpeedX = mBlockWidth /5;
                            }
                            mBall.setSpeedY(ballSpeedY);
                            mBall.setSpeedX(ballSpeedX);
                        }


                        //mPad.draw(canvas,paint);
                        for (DrawbleItem item : mItemList) {
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);

                        if (isCollision && getBlockCount() == 0) {
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR,true);
                            bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT,getBlockCount());
                            bundle.putLong(ClearActivity.EXTRA_TIME,System.currentTimeMillis()-mGameStartTime);
                            message.setData(bundle);
                            mHandler.sendMessage(message);

                        }

                    }
                    long sleepTime = 16-(System.currentTimeMillis()-startTime);
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {

                        }  //try
                    }  // if
                } // while
                toneGenerator.release();  //toneGenerator를 릴리즈한다


            } //rsun
        }); //rsuable

        mIsRunable =  true;
        mThread.start();
    } //start
    public void stop(){
        mIsRunable = false;
    }




    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        synchronized ( this ) {
            return true;
        }


    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }

    private ArrayList<DrawbleItem> mItemList;
    private ArrayList<Block> mBlockList;
    private Pad mPad;
    private float mPadHalfWidth;
    private Ball mBall;
    private float mBallRadius;
    private float mBlockWidth;
    private float mBlockHeight;
    static final int BLOCK_COUNT = 50;
    private int mLife;
    private long mGameStartTime;

    public void readyObjects(int width, int height) {
        mLife = 1;
        mBlockWidth = width /10;
        mBlockHeight = height /20;
        mItemList = new ArrayList<DrawbleItem>();
        mBlockList = new ArrayList<Block>();
        for (int i=0; i<BLOCK_COUNT; i++) {
            float blockTop = i /10 * mBlockHeight;
            float blockLeft = i %10 * mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            //mItemList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
            mBlockList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
        }
        mItemList.addAll(mBlockList);
        mPad = new Pad(height*0.8f, height*0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;

        mBallRadius = width < height ? width / 40 : height / 40;
        mBall = new Ball(mBallRadius, width/2, height/2);
        mItemList.add(mBall);
        mGameStartTime = System.currentTimeMillis();

        if (mSaveInstanceState != null) {
            mLife  = mSaveInstanceState.getInt(KEY_LIFE);
            mGameStartTime = mSaveInstanceState.getLong(KEY_GAME_START_TIME);
            mBall.restore(mSaveInstanceState.getBundle(KEY_BALL),width,height);
            for (int i = 0; i < BLOCK_COUNT; i++) {
                mBlockList.get(i).restore(mSaveInstanceState.getBundle(KEY_BLOCK+String.valueOf(i)));
            }
        }

    }
    private Block getBlock(float x, float y) {
        int index = (int) (x/mBlockWidth) + (int) (y/mBlockHeight) * 10;
        if (0<= index && index < BLOCK_COUNT) {
            Block block = (Block) mItemList.get(index);
            if (block.isExist()) {
                return block;
            }
        }
        return  null;
    }

    private int getBlockCount () {
        int count = 0;
        for (Block block : mBlockList) {
            if (block.isExist()) {
                count++;
            }
        }
        return  count;

    }



}
