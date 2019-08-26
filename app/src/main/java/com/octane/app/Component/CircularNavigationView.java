package com.octane.app.Component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import com.octane.app.GlobalConstant;
import com.octane.app.R;

import java.util.Timer;
import java.util.TimerTask;

public class CircularNavigationView extends View {

    private final static int TOTAL_DEGREE = 360;
    private final static int START_DEGREE = -90;

    private int m_curIndex;
    private int m_wantIndex;
    private int m_rotateAngle;
    private boolean rotating;

    private Paint mPaint;
    private RectF mOvalRect = null;

    private int mItemCount;
    private int mSweepAngle;

    private int mInnerRadius;
    private int mOuterRadius;
    private Bitmap mCenterIcon;
    private Bitmap[] mIcons;
    private Bitmap[] mSelIcons;
    private int[] mColors;
    private String[] mTitles;
    private int mTitleColor;
    private int mTitleSize;
    private int mTitlePadding;
    private int mCenterColor;
    private Bitmap mOuterImg;

    private SparseIntArray mHeightMap;
    private SparseIntArray mWidthMap;

    private OnCircularItemClickListener mClickListener;
    private OnProfileItemClickListener mProfileItemClickListener;

    public CircularNavigationView(Context context) {
        this(context, null);
    }

    public CircularNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        m_curIndex = 0;
        m_wantIndex = -1;
        rotating = false;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CircularActiveView, 0, 0);
        mItemCount = attr.getInteger(R.styleable.CircularActiveView_item_count, 0);
        int centerIconResId = attr.getResourceId(R.styleable.CircularActiveView_center_icon, 0);
        int iconsResId = attr.getResourceId(R.styleable.CircularActiveView_item_icons, 0);
        int selIconsResId = attr.getResourceId(R.styleable.CircularActiveView_item_sel_icons, 0);
        int colorsResId = attr.getResourceId(R.styleable.CircularActiveView_item_colors, 0);
        int namesResId = attr.getResourceId(R.styleable.CircularActiveView_item_titles, 0);
        int outImgId = attr.getResourceId(R.styleable.CircularActiveView_outer_image, 0);
        mInnerRadius = attr.getDimensionPixelSize(R.styleable.CircularActiveView_inner_radius, 0);
        mOuterRadius = attr.getDimensionPixelSize(R.styleable.CircularActiveView_outer_radius, 0);



        mTitleColor = attr.getResourceId(R.styleable.CircularActiveView_title_color, 0);
        mTitleSize = attr.getDimensionPixelSize(R.styleable.CircularActiveView_title_size, 0);
        mTitlePadding = attr.getDimensionPixelOffset(R.styleable.CircularActiveView_title_padding, 0);
        mCenterColor = attr.getResourceId(R.styleable.CircularActiveView_center_color, 0);
        attr.recycle();

        mSweepAngle = TOTAL_DEGREE / mItemCount;
        mColors = getResources().getIntArray(colorsResId);
        mCenterIcon = BitmapFactory.decodeResource(getResources(), centerIconResId);
        mTitles = getResources().getStringArray(namesResId);
        TypedArray icons = getResources().obtainTypedArray(iconsResId);
        TypedArray selIcons = getResources().obtainTypedArray(selIconsResId);

        mHeightMap = new SparseIntArray();
        mWidthMap = new SparseIntArray();
        mSelIcons = new Bitmap[selIcons.length()];
        mIcons = new Bitmap[icons.length()];
        for (int i = 0; i < icons.length(); i++) {
            mIcons[i] = BitmapFactory.decodeResource(getResources(), icons.getResourceId(i, 0));
            mSelIcons[i] = BitmapFactory.decodeResource(getResources(), selIcons.getResourceId(i, 0));
            //mHeightMap.put(i, mIcons[i].getHeight());
            //mWidthMap.put(i, mIcons[i].getWidth());
            mHeightMap.put(i, mIcons[i].getHeight()/3);
            mWidthMap.put(i, mIcons[i].getWidth()/3);
        }
        mOuterImg = BitmapFactory.decodeResource(getResources(),R.drawable.img_boarder);
        icons.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        mCenterIcon.recycle();
        for (Bitmap icon : mIcons)
            icon.recycle();
        super.onDetachedFromWindow();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("Drawing");

        int width = getWidth();
        int height = getHeight();
        int borderR = mOuterRadius + 230;
        @SuppressLint("DrawAllocation")
        RectF iconRect1 = new RectF(width / 2 - borderR, height / 2 - borderR, width / 2 + borderR, height / 2 + borderR);
        canvas.drawBitmap(mOuterImg,null,iconRect1, null);
        //if (mOvalRect == null) {
            mOvalRect = new RectF(width / 2 - mOuterRadius, height / 2 - mOuterRadius, width / 2 + mOuterRadius, height / 2 + mOuterRadius);
        //}


        for (int i = 0; i < mItemCount && i < mIcons.length; i++) {
            int startAngle = START_DEGREE + i * mSweepAngle;
            //mPaint.setColor(mColors[1]);
            //mPaint.setShader(new LinearGradient(mOvalRect.left, mOvalRect.top, mOvalRect.right, mOvalRect.bottom, Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));
            mPaint.setColor(Color.rgb(41,41,41));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

            if(i == m_curIndex && !rotating){
                //mPaint.setShadowLayer(10.0f, -5.0f, 1.0f, Color.BLACK);
                mPaint.setShader(new RadialGradient(mOvalRect.centerX(),mOvalRect.centerY(),mOuterRadius,0xffffffff,0xa0707070,Shader.TileMode.MIRROR));
                canvas.drawArc(mOvalRect,startAngle, mSweepAngle,true,mPaint);
                mPaint.setShader(null);
                //mPaint.reset();
            }

            //mPaint.setShadowLayer(0,0,0,0);
            mPaint.setStrokeWidth(2);
            mPaint.setColor(Color.rgb(62,66,70));
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

            int centerX = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2)));
            int centerY = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2)));
            int iconWH = 60;
            @SuppressLint("DrawAllocation")
            RectF iconRect = new RectF(width / 2 + centerX - iconWH / 2, height / 2 + centerY - iconWH / 2,width / 2 + centerX + iconWH / 2, height / 2 + centerY + iconWH / 2);

            if(i == m_curIndex && !rotating){
                canvas.drawBitmap(mSelIcons[i], null,iconRect, null);
            }else{
                canvas.drawBitmap(mIcons[i], null,iconRect, null);
            }

            //canvas.drawBitmap(mIcons[i], null,new RectF(width / 2 + centerX - mIcons[i].getWidth() / 2, height / 2 + centerY - mIcons[i].getHeight() / 2,), null);
            //mPaint.setShader(null);
        }

        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mOvalRect,0,360,true,mPaint);

        if(rotating){
            int startAngle = START_DEGREE + m_curIndex * mSweepAngle + m_rotateAngle;
            Log.d("invalidate",Integer.toString(startAngle));
            mPaint.setShader(new RadialGradient(mOvalRect.centerX(),mOvalRect.centerY(),mOuterRadius,0xffffffff,0xa0707070,Shader.TileMode.MIRROR));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(mOvalRect,startAngle,mSweepAngle,true,mPaint);
            mPaint.setShader(null);
        }

        mPaint.setColor(getResources().getColor(mTitleColor));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTitleSize);
        for (int i = 0; i < mItemCount && i < mTitles.length; i++) {
            int h = mHeightMap.get(i);
            int w = mWidthMap.get(i);
            int startAngle = START_DEGREE + i * mSweepAngle;
            int centerX = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2)));
            int centerY = (int) ((mOuterRadius + mInnerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2)));
            canvas.drawText(mTitles[i], width / 2 + centerX - w / 2, height / 2 + centerY + h / 2 + mTitlePadding, mPaint);
        }

        mPaint.setColor(Color.rgb(50,50,50));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, mInnerRadius - 10, mPaint);

//        mPaint.setColor(Color.rgb(60,61,71));
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(width / 2, height / 2, mInnerRadius - 8, mPaint);

//        mPaint.setColor(Color.rgb(80,80,80));
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(width / 2, height / 2, mInnerRadius - 10, mPaint);

//        mPaint.setColor(0xff353636);
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(width / 2, height / 2, mInnerRadius - 18, mPaint);

        mPaint.setColor(Color.rgb(40,40,40));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, mInnerRadius - 26, mPaint);

        int inner = mInnerRadius - 23;
        int angle = m_curIndex * mSweepAngle + m_rotateAngle;
        @SuppressLint("DrawAllocation")
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postRotate(angle,mCenterIcon.getWidth()/2,mCenterIcon.getHeight()/2);
        matrix.postScale(2.f*inner/mCenterIcon.getWidth(),2.f*inner/mCenterIcon.getHeight());
        matrix.postTranslate(width/2 - inner , height/2 - inner);


        canvas.drawBitmap(mCenterIcon,matrix,null);


        super.onDraw(canvas);
    }
    public void rotateEffect(final int index){

        if(index == -1 || rotating || index == m_curIndex)
            return;
        int angle = mSweepAngle * (index - m_curIndex);
        int direction = angle <0 ? -1 : 1;
        int k = Math.abs(angle);
        if(k >= 180){
            angle = 360 - k;
            direction *= -1;
        }else{
            angle = k;
        }
        angle *= direction;
        final int mAngle = angle;
        int effectTime = (int)(Math.abs(mAngle) * GlobalConstant.ONE_EFFECT_TIME);
        m_wantIndex = index;
        m_rotateAngle = 0;
        rotating = true;
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(mAngle < 0 ){
                    m_rotateAngle--;
                }else{
                    m_rotateAngle++;
                }
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
                if(Math.abs(m_rotateAngle) > Math.abs(mAngle)){
                    rotating = false;
                    m_curIndex = m_wantIndex;
                    m_rotateAngle = 0;
                    m_wantIndex = -1;
                    timer.cancel();
                    if (mProfileItemClickListener != null) {
                        //mClickListener.onCircularItemClick(Math.abs(item - mItemCount) % mItemCount);
                        mProfileItemClickListener.onProfileItemClick(m_curIndex);
                    }
                }
            }
        },10,2);
    }

    public void setM_curIndex(int m_curIndex) {
        this.m_curIndex = m_curIndex;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!rotating){
            int x , y;
            int width = getWidth();
            int height = getHeight();
            x = (int)event.getX(); //- (int)getX();
            y = (int)event.getY(); //- (int)getY();
            int radius = mInnerRadius + mOuterRadius;
            if (event.getAction() == MotionEvent.ACTION_DOWN &&
                    (Math.pow(x - (getWidth() / 2), 2) + Math.pow(y - (getHeight() / 2), 2) <= radius * radius)) {
                if ((Math.pow(x - (getWidth() / 2), 2) + Math.pow(y - (getHeight() / 2), 2) <= mInnerRadius * mInnerRadius)) {
                    if (mClickListener != null) {
                        mClickListener.onCircularItemClick(m_curIndex);
                    }
                } else {
                    double angle = Math.toDegrees(Math.atan2(x - getWidth() / 2,getHeight() / 2 - y));
                    if (angle < 0) angle = 360 + angle;

                    int item = (int)(angle / mSweepAngle);
                    rotateEffect(item);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnCircularItemClickListener(OnCircularItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnProfileItemClickListener(OnProfileItemClickListener listener) {
        mProfileItemClickListener = listener;
    }

    public interface OnCircularItemClickListener {
        void onCircularItemClick(int index);
    }

    public interface OnProfileItemClickListener{
        void onProfileItemClick(int index);
    }
}
