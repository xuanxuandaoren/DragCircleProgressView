package zhuyunpeng.com.dragcircleprogress.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import zhuyunpeng.com.dragcircleprogress.R;
import zhuyunpeng.com.dragcircleprogress.utils.DeminUtils;
import zhuyunpeng.com.dragcircleprogress.utils.DisplayUtils;
import zhuyunpeng.com.dragcircleprogress.utils.ToastUtils;


/**
 * 拖拽圆进度条
 * Created by 玉光 on 2016-11-10.
 */

public class DragCircleProgress extends View {
    /**
     * 画外圆的颜色
     */
    private int mCircleColor;
    /**
     * 画内圆的颜色
     */
    private int mLineColor;
    /**
     * 画外圆的笔
     */
    private Paint mCirclePaint;
    /**
     * 画文字的笔
     */
    protected Paint mTextPaint;
    /**
     * 画线条的笔
     */
    private Paint mLinePaint;
    /**
     * 拖拽圆的图片
     */
    protected Drawable mThumb;
    /**
     * 拖拽圆的半径
     */
    private int mDraggerRadius = 0;
    /**
     * 当前图片的圆心x坐标
     */
    protected int mCenterPointX;
    /**
     * 当前图片圆心y坐标
     */
    protected int mCenterPointY;
    /**
     * 拖拽圆的x坐标
     */
    private int mThumbXPos;
    /**
     * 拖拽圆的Y坐标
     */
    private int mThumbYPos;
    /**
     * 内圆的半径
     */
    private float mRadius;
    /**
     * 当前触摸的角度
     */
    private double mTouchAngle;
    /**
     * 内圆的正方形区域
     */
    protected RectF mArcRect = null;
    protected static final int THUMB_ANGLE = 18;
    private static final int LINES_NUMBER = 200;
    protected double mCurrentValueD = 0;
    protected long mCurrentValue = 0;
    // 一圈代表的数值
    protected int mRoundValue = 60;
    private static final int LINE_LENTH = 18;
    /**
     * 总得计时
     */
    protected long mTotalValue = 1000 * 60 * 60 * 1;
    // 为了减少误操作，只有按下区域在thumb范围内才能拖动
    private boolean mDragThumb = false;
    private static final int SENSITIVE_RADIUS = 30;
    /**
     * 倒计时的状态
     */
    private int state = OFF;
    /**
     * 关闭
     */
    private static final int OFF = 0;
    /**
     * 拖拽中
     */
    private static final int ON_DRAGING = 1;
    /**
     * 计时中
     */
    private static final int ON_COUNT = 2;
    /***
     * 循环
     */
    private Handler mHandler = new Handler();
    /**
     * 系统要到的时间
     */
    private long systemTime = 0l;
    private boolean isCount;
    /**
     * 文字的宽
     */
    private int width = 0;
    private long circleCount = 0;
    /**
     * 记录上次滑动的角度
     */
    private double lastAngle = 0;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public DragCircleProgress(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public DragCircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public DragCircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected Resources mRes;

    /**
     * 初始化
     */
    private void init() {
        mRes = getContext().getResources();
        float density = mRes.getDisplayMetrics().density;
        setWillNotDraw(false);
        mCircleColor = Color.WHITE;
        mLineColor = Color.WHITE;
        //画圆
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(density);
        //画文字
        mTextPaint = new Paint();


        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(density * 18);

        // mTypeface = FontManager.getFont(getContext(), FONTNAME);
        // mTextPaint.setTypeface(mTypeface);
        //画内圆
        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1 * density);
        //初始化拖拽圆的图片
        initThumb(false);
    }

    /**
     * 初始化拖拽圆的图片
     */
    private void initThumb(Boolean drag) {
        if (drag) {
            mThumb = mRes.getDrawable(R.mipmap.count_down_timer_thumb_on);
        } else {
            mThumb = mRes.getDrawable(R.mipmap.count_down_timer_thumb_off);
        }

        int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
        if (mDraggerRadius == 0) {
            mDraggerRadius = thumbHalfWidth;
        }
        mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
                thumbHalfheight);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        mCenterPointX = x;
        mCenterPointY = y;
        //更新拖动圆的坐标
        updateThumbPosition();
    }

    /**
     * 更新拖动图标的位置
     */
    protected void updateThumbPosition() {
        mThumbXPos = (int) (mRadius * Math.sin(Math.toRadians(mTouchAngle)));
        mThumbYPos = (int) (mRadius * Math.cos(Math.toRadians(mTouchAngle)));
    }

    /**
     * 设置定时到的时间
     *
     * @param systemTime
     */
    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;


        mCurrentValue = systemTime - System.currentTimeMillis();
        if (systemTime <= System.currentTimeMillis() || systemTime == 0) {
            mCurrentValue = 0;
            state = OFF;
            mTouchAngle = 0;
            circleCount = 0;
            initThumb(false);
            isCount = false;
        } else {
            circleCount = mCurrentValue / mTotalValue;
            mTouchAngle = (mCurrentValue * 360 / mTotalValue) % 360;
            state = ON_COUNT;

            initThumb(true);
            isCount = true;
            startCountDown();
        }

        lastAngle = mTouchAngle;

        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mArcRect == null) {
            initArcRect();
        }

        mCirclePaint.setColor(mCircleColor);
        // canvas.drawCircle(mCenterPointX, mCenterPointY, mRadius,
        // mCirclePaint);
        canvas.drawArc(mArcRect, (float) mTouchAngle - 90 + THUMB_ANGLE
                / 2, 360 - THUMB_ANGLE, false, mCirclePaint);
        if (mThumb != null) {
            updateThumbPosition();
            canvas.save();
            canvas.translate(mCenterPointX + mThumbXPos, mCenterPointY
                    - mThumbYPos);
            canvas.rotate((float) mTouchAngle, mThumb.getBounds()
                    .exactCenterX(), mThumb.getBounds().exactCenterY());
            mThumb.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        float rotate = 360.0f / LINES_NUMBER;
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAlpha(255);
        float density = mRes.getDisplayMetrics().density;

        for (int i = 0; i < LINES_NUMBER; i++) {

            if (rotate * i > mTouchAngle && circleCount == 0) {

                mLinePaint.setColor(mLineColor);

            } else {

                mLinePaint.setColor(0xFF3DFDAF);

            }
            if (mTouchAngle > 0 && i == 1) {
                mLinePaint.setColor(0xFF3DFDAF);
            }
            if (mCurrentValue <= 0) {
                mLinePaint.setColor(mLineColor);
            }
            canvas.drawLine(mCenterPointX, mCenterPointY - mRadius
                            + mDraggerRadius + 5 * density, mCenterPointX,
                    mCenterPointY - mRadius + mDraggerRadius + 5 * density
                            + LINE_LENTH * density, mLinePaint);
            canvas.rotate(rotate, mCenterPointX, mCenterPointY);
        }
        canvas.restore();
        onDrawText(canvas, mCurrentValue, mTotalValue);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        // isLocateInThumbArea(x, y);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isLocateInThumbArea(x, y)) {
                mDragThumb = true;
            } else {
                mDragThumb = false;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isTrackingStart()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                onStartTrackingTouch();
                if (!updateOnDownTouch(event)) {
                    return false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                setPressed(true);
                double angle = getTouchDegrees(event.getX(), event.getY());
                if (!updateOnTouch(angle)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:

//                onStopTrackingTouch();
                if (mCurrentValue > 6 * mTotalValue) {
                    mCurrentValue = 6 * mTotalValue;
                    mTouchAngle = 0;
                    circleCount = 6;
                    ToastUtils.showMsg(getContext(), getResources().getString(R.string.delayTipStr4));
                }
                if (circleCount < 0) {
                    circleCount = 0;
                    mTouchAngle = 0;
                    lastAngle = 0;
                }
                if (state != OFF) {
                    if (mDragThumb) {
                        if (mCurrentValue < 1) {
                            state = OFF;
                            isCount = false;
                            mTouchAngle = 0;
                            initThumb(false);
                            stopCountDown();
                            systemTime = mCurrentValue + System.currentTimeMillis();
                            if (onCountDownListener != null) {
                                onCountDownListener.onStop(this, systemTime, true);
                            }

                            invalidate();
                            break;
                        }
                        state = ON_COUNT;
                        systemTime = mCurrentValue + System.currentTimeMillis();
                        isCount = true;
                        initThumb(true);
                        if (onCountDownListener != null) {
                            onCountDownListener.onStart(this, systemTime);
                        }
                        startCountDown();

                    }
                }


//                lastAngle=mTouchAngle;

                invalidate();
                setPressed(false);
                break;
            case MotionEvent.ACTION_CANCEL:
//                onStopTrackingTouch();
                setPressed(false);
                break;
        }

        return true;
    }


    /**
     * 开始计时
     */
    private void startCountDown() {


        if (isCount && state != OFF) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mCurrentValue = systemTime - System.currentTimeMillis();
                    mTouchAngle = (mCurrentValue % mTotalValue) * 360 / mTotalValue;
                    circleCount = mCurrentValue / mTotalValue;

                    if (mCurrentValue <= 0) {
                        mTouchAngle = 0;
                        isCount = false;
                        initThumb(false);
                        state = OFF;
                        mHandler.removeCallbacksAndMessages(null);
                        if (onCountDownListener != null) {
                            onCountDownListener.onStop(DragCircleProgress.this, systemTime, false);
                        }
                    }

                    invalidate();
                    startCountDown();
                }
            }, 1000);
        }


    }

    /**
     * 触屏的时候更新的状态
     */
    protected boolean updateOnTouch(double angle) {
        if (!mDragThumb) {
            return true;
        }

        if (mCurrentValue <= 0) {
            lastAngle = 0;
        }

        state = ON_DRAGING;


        if (lastAngle - angle < -270) {
            circleCount--;

        }
        if (lastAngle - angle > 270) {
            if (circleCount < 0) {
                circleCount = 0;
            }
            circleCount++;


        }

        mCurrentValue = (long) ((angle + circleCount * 360) * mTotalValue / 360);
        if (mCurrentValue <= 0) {
            mCurrentValue = 0;
            mTouchAngle = 0;
            lastAngle = 0;


        } else {
            mTouchAngle = angle;
            lastAngle = angle;
        }
//        else if (mCurrentValue >= 6 * mTotalValue) {
//            mCurrentValue = 6 * mTotalValue;
//            mTouchAngle = 360;
//        } else {
//            mTouchAngle = angle;
//        }
        updateThumbPosition();
        if (onCountDownListener != null) {
            onCountDownListener.onSlide(this, systemTime);
        }


        invalidate();
        return true;
    }

    /**
     * 获取当前拖拽的角度
     *
     * @param xPos
     * @param yPos
     * @return
     */
    protected double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mCenterPointX;
        float y = mCenterPointY - yPos;
        double angle = Math.toDegrees(Math.atan2(x, y));
        if (xPos < mCenterPointX && yPos > mCenterPointY) {
            angle += 360;
        } else if (xPos < mCenterPointX && yPos < mCenterPointY) {
            angle += 360;
        }
        return angle;
    }

    protected boolean updateOnDownTouch(MotionEvent event) {
        if (mDragThumb) {
            isCount = false;
            mHandler.removeCallbacksAndMessages(null);
        }
        if (circleCount < 0) {
            circleCount = 0;
        }
        if (state == OFF) {
            if (mDragThumb) {
                state = ON_DRAGING;
            }
        }

        if (state == ON_COUNT) {
            if (!mDragThumb && Math.abs(event.getX() - mCenterPointX) < 1 * mRadius / 3 && Math.abs(event.getY() - mCenterPointY) < 1 * mRadius / 3) {
                state = OFF;
                mCurrentValue = 0;
                mTouchAngle = 0;
                lastAngle = 0;
                systemTime = 0;
                circleCount = 0;
                if (onCountDownListener != null) {
                    onCountDownListener.onStop(this, systemTime, true);
                }
                stopCountDown();
                initThumb(false);
            } else {
//                lastAngle = getTouchDegrees(event.getX(), event.getY());
            }
        }


        invalidate();
        return true;
    }

    /**
     * 取消计时
     */
    private void stopCountDown() {
        isCount = false;
        mCurrentValue = 0;
        mHandler.removeCallbacksAndMessages(null);

    }

    protected boolean isTrackingStart() {
        return true;
    }

    // x y为view内相对坐标，而非屏幕的绝对坐标
    public boolean isLocateInThumbArea(int x, int y) {
        int thumbX = mCenterPointX + mThumbXPos;
        int thumbY = mCenterPointY - mThumbYPos;
        return (thumbX - SENSITIVE_RADIUS < x && thumbY - SENSITIVE_RADIUS < y
                && (thumbX + mThumb.getIntrinsicWidth() + SENSITIVE_RADIUS) > x && (thumbY
                + mThumb.getIntrinsicHeight() + SENSITIVE_RADIUS) > y);
    }

    /**
     * 画文字
     *
     * @param canvas
     * @param currentValue
     * @param totalValue
     */
    protected void onDrawText(Canvas canvas, long currentValue, long totalValue) {
        mTextPaint.setTextSize(mRadius / 4);
        if (currentValue <= 0) {
            currentValue = 0;
        }
        String time = format(currentValue);
        String des = null;
        switch (state) {
            case OFF:
                mTextPaint.setColor(Color.WHITE);
                des = getContext().getResources().getString(R.string.delayTipStr1);
                break;
            case ON_COUNT:
                mTextPaint.setColor(0xFF3DFDAF);
                des = getContext().getResources().getString(R.string.delayTipStr2);
                break;
            default:
                mTextPaint.setColor(0xFF3DFDAF);
                des = getContext().getResources().getString(R.string.delayTipStr3);
                break;
        }
        if (width == 0) {
            width = DeminUtils.getTextWidth(mTextPaint, time);
        }
        if ("00:00:00".equals(time)) {
            mTextPaint.setColor(Color.WHITE);
        }
        canvas.drawText(time, 0, time.length(), mCenterPointX - width / 2, mCenterPointY - DisplayUtils.dip2px(getContext(), 4), mTextPaint);
        mTextPaint.setTextSize(mRadius / 10);
        mTextPaint.setColor(0x80FFFFFF);
        int width = DeminUtils.getTextWidth(mTextPaint, des);
        int height = DeminUtils.getTextHeight(mTextPaint, des);

        canvas.drawText(des, 0, des.length(), mCenterPointX - width / 2, mCenterPointY + DisplayUtils.dip2px(getContext(), 4) + height, mTextPaint);

    }

    /**
     * 把毫秒格式化为时间格式
     *
     * @param currentValue
     * @return
     */
    private String format(long currentValue) {
        currentValue = currentValue / 1000;
        int second = (int) (currentValue % 60);
        int min = (int) (currentValue / 60 % 60);
        int hour = (int) (currentValue / 3600);
        return lenghtFormat(hour) + ":" + lenghtFormat(min) + ":" + lenghtFormat(second);
    }

    private String lenghtFormat(int time) {
        if ((time + "").length() < 2) {
            return "0" + time;
        }
        return String.valueOf(time);
    }

    /**
     * 初始化内圆的半径
     */
    private void initArcRect() {
        final int height = getHeight();
        final int width = getWidth();
        final int min = Math.min(width, height);
        float top = 0;
        float left = 0;
        int arcDiameter = 0;

        arcDiameter = (int) (min - getPaddingLeft() - getPaddingRight() - mDraggerRadius * 2);
        /**内圆的半径*/
        mRadius = arcDiameter / 2;
        top = height / 2 - mRadius;
        left = width / 2 - mRadius;
        mArcRect = new RectF();
        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);
    }

    /**
     * 关闭闹钟
     */
    public void colse() {
        state = OFF;
        mTouchAngle = 0;
        lastAngle = 0;
        circleCount = 0;
        mCurrentValue = 0;
        initThumb(false);
        isCount = false;
        mHandler.removeCallbacksAndMessages(null);

        invalidate();
    }

    /**
     * 计时状态监听
     */
    public interface OnCountDownListener {
        /**
         * 开始监听
         *
         * @param dragCircleProgress
         * @param descTime
         */
        void onStart(DragCircleProgress dragCircleProgress, long descTime);

        /**
         * 滑动中
         *
         * @param dragCircleProgress
         * @param descTime
         */
        void onSlide(DragCircleProgress dragCircleProgress, long descTime);

        void countSlide(DragCircleProgress dragCircleProgress, long descTime);

        /***
         * 结束监听
         *
         * @param dragCircleProgress
         * @param descTime
         */
        void onStop(DragCircleProgress dragCircleProgress, long descTime, boolean byUser);
    }

    private OnCountDownListener onCountDownListener;

    public void setOnCountDownListener(OnCountDownListener onCountDownListener) {
        this.onCountDownListener = onCountDownListener;
    }
}
