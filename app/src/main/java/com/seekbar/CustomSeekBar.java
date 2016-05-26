package com.seekbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;



/**
 *
 * SeekBar 自定义
 * @author jiaowenzheng
 *
 * Created by jwz on 2016/5/11.
 */
public class CustomSeekBar extends View {

    private String mGreenColor = "#5DB98B";
    private String mGrayColor = "#E2E2E2";
    private String mTextColor = "#999999";

    private Paint mPaint;
    private Paint mTextPaint;

    private float mWidth;
    private float mHeight;
    private float mAverageWidth;
    private float mCenterY;
    private float mPointX;

    private Bitmap mThumberBitmap;
    private float mBitmapWidth;
    private float mBitmapHeight;

    private RectF rectF;
    private Rect mTextRect;
    private float mThumberY;

    private String[] timeArray = {"15", "30", "45", "60"};
    private TextView mPopupTextView;
    private PopupWindow mPopup;
    private float[] timeBands = new float[4];

    private int mStrokeWidth = 10;
    private int mTextSize = 40;
    private int mRadius = 15;
    private int mPopWidth =183 ;

    private SeekBarChangeListener mListener;
    int progress = 15;


    public CustomSeekBar(Context context) {
        super(context);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,dm);
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,dm);
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,dm);
        mPopWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,61,dm);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor(mGreenColor));
        mPaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor(mTextColor));
        mTextPaint.setTextSize(mTextSize);

        mTextRect = new Rect();
        mTextPaint.getTextBounds(timeArray[0], 0, timeArray[0].length(), mTextRect);

        mThumberBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_seekbar_thumber, null);
        mBitmapWidth = mThumberBitmap.getWidth();
        mBitmapHeight = mThumberBitmap.getHeight();

        post(new Runnable() {
            @Override
            public void run() {
                mWidth = getWidth();
                mHeight = getHeight();

                mCenterY =  mHeight / 2;
                mThumberY = mCenterY - mBitmapHeight / 2;

                rectF = new RectF(0, 0, x + mBitmapWidth, mThumberY + mBitmapHeight);

            }
        });

        initPopWindow();
    }


    public void setProgress(int progress){

    }

    float x = 0;
    float y = 0;

    boolean isMove = false;
    boolean isShowPop = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x1 = event.getX();
        float y1 = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isShowPop = true;

                if (rectF.contains(x1, y1)) {
                    isMove = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (isMove && (x1 >= 0 && (mWidth - mBitmapWidth) >= x1)) {
                    x = x1;
                    invalidate();
                }

                if (x1 < 0) {
                    x = 0;
                    invalidate();
                }

                if (mWidth - mBitmapWidth < x) {
                    x = mWidth - mBitmapWidth;
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isMove) {
                    rectF.set(0, 0, x + mBitmapWidth, rectF.bottom);
                    isMove = false;
                }
                break;

        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        mPaint.setColor(Color.parseColor(mGreenColor));
        canvas.drawLine(0, mCenterY, x, mCenterY, mPaint);

        mPaint.setColor(Color.parseColor(mGrayColor));

        if (x == 0) {
            canvas.drawLine(0, mCenterY, mWidth, mCenterY, mPaint);
        } else {
            canvas.drawLine(x + mBitmapWidth / 2, mCenterY, mWidth, mCenterY, mPaint);
        }


        mAverageWidth = mWidth / (timeArray.length - 1);


        for (int i = 0; i < timeArray.length; i++) {

            if (i == 0) {
                mPointX = mAverageWidth * i + mRadius;
            } else if (i == timeArray.length - 1) {
                mPointX = mAverageWidth * i - mRadius;
            } else {
                mPointX = mAverageWidth * i;
            }

            timeBands[i] = mPointX;

            if (mPointX - mBitmapWidth / 2 >= x) {
                mPaint.setColor(Color.parseColor(mGrayColor));
                canvas.drawCircle(mPointX, mCenterY, mRadius, mPaint);
            } else {
                mPaint.setColor(Color.parseColor(mGreenColor));
                canvas.drawCircle(mPointX, mCenterY, mRadius, mPaint);
            }


            if (i == timeArray.length - 1) {
                canvas.drawText(timeArray[i], mPointX - mTextRect.right , mCenterY + mBitmapHeight + mRadius, mTextPaint);

            } else {
                canvas.drawText(timeArray[i], mPointX - mTextRect.right / 2, mCenterY + mBitmapHeight + mRadius, mTextPaint);
            }
        }

        canvas.drawBitmap(mThumberBitmap, x, mThumberY, mPaint);

        if(mPopup.isShowing()){
            mPopup.update(this,(int)(x - mBitmapWidth), (int) (-mHeight - mPopup.getHeight() - mBitmapHeight / 2),-1,-1);


            String bands;
            if(timeBands[0] >=x && timeBands[1] >=x){
                bands = timeArray[0]+"—"+timeArray[1];
            } else if (timeBands[1] < x && timeBands[2] >= x){
                bands = timeArray[1]+"—"+timeArray[2];
            } else if (timeBands[2] < x && timeBands[3] >= x){
                bands = timeArray[2]+"—"+timeArray[3];
            }else{
                bands = timeArray[0]+"—"+timeArray[1];
            }

            progress = (int) (((int) (x + mBitmapWidth / 2))  * 45 / mWidth)+ 15;

            if(x <= 0){
                progress = 15;
            }else if (x >= (mWidth - mBitmapWidth - mRadius)){
                progress = 60;
            }

            mPopupTextView.setText(String.valueOf(progress));

            if(mListener != null){
                mListener.onProgress(String.valueOf(progress));
            }

//            removeCallbacks(runnable);
//            postDelayed(runnable,2000);

        }else if(isShowPop){
            if (!mPopup.isShowing())
                mPopup.showAsDropDown(this,0,(int) (-mHeight- mPopup.getHeight() - mBitmapHeight / 2));
        }


    }



    public void initPopWindow(){

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View undoView = inflater.inflate(R.layout.popup, null);
        mPopupTextView = (TextView)undoView.findViewById(R.id.text);
        mPopup = new PopupWindow(undoView, mPopWidth, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopup.setOutsideTouchable(false);
        mPopup.setFocusable(false);
        mPopup.setTouchable(false);
        mPopup.setAnimationStyle(R.style.fade_animation);

    }



    public interface SeekBarChangeListener{
        void onProgress(String progress);
    }

    public void setSeekBarChangeListener(SeekBarChangeListener listener){
        this.mListener = listener;
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mPopup != null && mPopup.isShowing()){
                mPopup.dismiss();
            }
        }
    };
}
