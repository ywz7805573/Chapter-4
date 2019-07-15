package com.bytedance.clockapplication.widget;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.os.Handler;
import android.os.Message;



import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.LogRecord;




public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;


    private boolean isRunning;



    private Runnable clockRunnable;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    private static class MyHandler extends Handler{

       private final WeakReference<Clock> Activty;

        private MyHandler(Clock Activty) {
            this.Activty = new WeakReference<>(Activty);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Clock activity = Activty.get();
            if (activity != null){
                // 把刷新的内容放在了Init里边

            }
        }
    }
    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }
    MyHandler mHandler = new MyHandler(this);
    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;


        clockRunnable = new Runnable() {
            @Override
            public void run() {
                postInvalidate();
                mHandler.postDelayed(this, 1000);
            }
        };
    }





    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;
        if(!isRunning){
            runClock();
        }
        else {
            if (mShowAnalog) {
                drawDegrees(canvas);
                drawHoursValues(canvas);
                drawNeedles(canvas);
                drawCenter(canvas);
            } else {
                drawNumbers(canvas);
            }
        }


    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        String[] clockNumbers = {"03","04","05","06","07","08","09","10","11","12","01","02"};//从最右边90°开始
        int length=mCenterX - (int) (mWidth * 0.12f);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(hoursValuesColor);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        for(int i=0;i<360;i+=30){
            int X = (int) (mCenterX + length * Math.cos(Math.toRadians(i)));
            int Y = (int) (mCenterX + length * Math.sin(Math.toRadians(i))-(top + bottom)/2);


            canvas.drawText(clockNumbers[i/30],X,Y,textPaint);
        }



    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        Calendar calendar = Calendar.getInstance();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float hourAngle,minAngle,secAngle;
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        hourAngle = (float)hour / 12 * 360 + (float)minute / 60 * (360 / 12);
        minAngle = (float)minute / 60 * 360;
        secAngle = (float)second / 60 * 360;
        paint.setStrokeWidth(10f);
        paint.setColor(hoursNeedleColor);
        canvas.save();
        canvas.rotate(hourAngle,getWidth()/2,getHeight()/2);
        canvas.drawLine(getWidth()/2,getHeight()/2,getWidth()/2,getHeight()/2-200,paint);

        paint.setColor(minutesNeedleColor);
        canvas.restore();
        canvas.save();
        canvas.rotate(minAngle,getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - 250 , paint);

        paint.setStrokeWidth(6f);
        paint.setColor(secondsNeedleColor);
        canvas.restore();
        canvas.save();
        canvas.rotate(secAngle,getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - 300 , paint);

        canvas.restore();



    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);


        paint.setColor(centerOuterColor);
        canvas.drawCircle(getWidth()/2,getHeight()/2,20f,paint);

        paint.setColor(centerInnerColor);
        canvas.drawCircle(getWidth()/2,getHeight()/2,15f,paint);

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }
    public void  runClock(){
        isRunning = true;
        mHandler.postDelayed(clockRunnable,1000);
    }

}