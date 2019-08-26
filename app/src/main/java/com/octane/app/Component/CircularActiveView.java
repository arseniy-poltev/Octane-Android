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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.octane.app.GlobalConstant;
import com.octane.app.R;
import com.octane.app.activity.HomeActivity;

import java.util.Timer;
import java.util.TimerTask;

public class CircularActiveView extends View {

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
    private Bitmap mCenterRIcon;
    private Bitmap mCenterGIcon;
    private Bitmap[] mIcons;
    private Bitmap[] mSelIcons;
    private int[] mColors;
    private String[] mTitles;
    private int mTitleColor;
    private int mTitleSize;
    private int mTitlePadding;
    private int mCenterColor;
    private Bitmap mOuterImg;
    private Bitmap mBorderGImg;
    private Bitmap mBorderRImg;

    private SparseIntArray mHeightMap;
    private SparseIntArray mWidthMap;

    private GestureDetector gestureDetector;
    private OnCircularItemClickListener mClickListener;

    public CircularActiveView(Context context) {
        this(context, null);
    }

    public CircularActiveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setM_curIndex(int m_curIndex) {
        this.m_curIndex = m_curIndex;
    }

    public CircularActiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //m_curIndex = 0;
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
        mCenterRIcon = BitmapFactory.decodeResource(getResources(), R.drawable.img_active_red);
        mCenterGIcon = BitmapFactory.decodeResource(getResources(), R.drawable.img_active_green);
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
        mBorderGImg = BitmapFactory.decodeResource(getResources(),R.drawable.img_br_green);
        mBorderRImg = BitmapFactory.decodeResource(getResources(),R.drawable.img_br_red);
        icons.recycle();
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent event) {
                Log.d("TEST", "onDoubleTap");
                int x , y;
                int inner = mInnerRadius - 15;

                x = (int)event.getX(); //- (int)getX();
                y = (int)event.getY(); //- (int)getY();

                if (event.getAction() == MotionEvent.ACTION_DOWN &&
                        (Math.pow(x - (getWidth() / 2), 2) + Math.pow(y - (getHeight() / 2), 2) <= mOuterRadius * mOuterRadius)) {
                    if ((Math.pow(x - (getWidth() / 2), 2) + Math.pow(y - (getHeight() / 2), 2) <= inner * inner)) {
                        if (mClickListener != null) {
                            mClickListener.onCircularItemClick();
                        }
                    }
                }
                return super.onDoubleTap(event);
            }
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                Log.d("TEST", "onSingleTap");
                Toast.makeText(getContext(),GlobalConstant.MSG_ONE_CLICK,Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        mCenterRIcon.recycle();
        mCenterGIcon.recycle();
        mBorderGImg.recycle();
        mBorderRImg.recycle();
        mOuterImg.recycle();
        for (Bitmap icon : mIcons)
            icon.recycle();
        super.onDetachedFromWindow();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("Drawing");

        int width = getWidth();
        int height = getHeight();
        int borderR1 = mOuterRadius + 230;
        @SuppressLint("DrawAllocation")
        RectF iconRect1 = new RectF(width / 2 - borderR1, height / 2 - borderR1, width / 2 + borderR1, height / 2 + borderR1);
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

            if(i == m_curIndex){
                if(i == GlobalConstant.RED_COLOR_INDEX)
                    mPaint.setShader(new RadialGradient(mOvalRect.centerX(),mOvalRect.centerY(),mOuterRadius,0xffd44928,0xf0933b2e,Shader.TileMode.MIRROR));
                else
                    mPaint.setShader(new RadialGradient(mOvalRect.centerX(),mOvalRect.centerY(),mOuterRadius,0xff87f883,0xf0568b58,Shader.TileMode.MIRROR));

                canvas.drawArc(mOvalRect,startAngle, mSweepAngle,true,mPaint);
                mPaint.setShader(null);
                //mPaint.reset();
            }

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

        /*
        //mPaint.setColor(0xffd44928);
        mPaint.setColor(0xff53c667);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mOvalRect,0,360,true,mPaint);
        */

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



        int inner = mInnerRadius - 15;


        mPaint.setColor(0xff202020);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, mInnerRadius - 13, mPaint);

        @SuppressLint("DrawAllocation")
        Matrix matrix = new Matrix();
        int borderR2 = (int) (mOuterRadius + 0.078f * mOuterRadius) + 2;
        RectF iconRect2 = new RectF(width / 2 - borderR2, height / 2 - borderR2, width / 2 + borderR2, height / 2 + borderR2);
        matrix.reset();

        matrix.postScale(2.f*inner/mCenterGIcon.getWidth(),2.f*inner/mCenterGIcon.getWidth());
        matrix.postTranslate(width/2 - inner , height/2 - inner);

        if(m_curIndex != -1) {
            if (m_curIndex == GlobalConstant.RED_COLOR_INDEX) {
                canvas.drawBitmap(mCenterRIcon, matrix, null);
                canvas.drawBitmap(mBorderRImg, null, iconRect2, null);
            } else {
                canvas.drawBitmap(mCenterGIcon, matrix, null);
                canvas.drawBitmap(mBorderGImg, null, iconRect2, null);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        return true;
        //return super.onTouchEvent(event);
    }

    public void setOnCircularItemClickListener(OnCircularItemClickListener listener) {
        mClickListener = listener;
    }

    public interface OnCircularItemClickListener {
        void onCircularItemClick();
    }
}
