package com.example.administrator.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

/**
 * Created by Administrator on 2017-03-11.
 */


public class Block implements  DrawbleItem{
    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;

    private boolean mIsCollision = false; //충돌상태를 기록하는 플래그
    private boolean mIsExist = true; //블럭이 존재하는가

    private static final String KEY_HARD = "hard";


    public Block(float top,float left,float bottom,float right) {
        mTop = top;
        mLeft = left;
        mBottom = bottom;
        mRight = right;
        mHard = 1;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (mIsExist) {
            //내구성이 0이상인 경우만
            if (mIsCollision) {
                mHard--;
                mIsCollision = false;
                if (mHard <=0 ) {
                    mIsExist = false;
                    return;
                }
            }
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mLeft,mTop,mRight, mBottom,paint);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            canvas.drawRect(mLeft,mTop,mRight, mBottom,paint);
        }
    }
    public void collision() {
        mIsCollision = true; //충돌사실만 기록하고 실제 파괴는 draw() 시에 한다.
    }
    public boolean isExist() {
        return mIsExist;
    }

    /**
     * Bundle 에 상태를 저장한다
     * @return 저장해야 할 상태가 저장된 Bundel
     */
    public Bundle save() {
        Bundle outState = new Bundle();
        outState.putInt(KEY_HARD,mHard);
        return outState;
    }
    /**
     * Bundle로 부터 상태를 복원한다.
     */
    public void  restore(Bundle inState) {
        mHard = inState.getInt(KEY_HARD);
        mIsExist = mHard > 0;
    }


}
