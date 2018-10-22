package cn.zjt.iot.oncar.android.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Mr Dk.
 * @version 2018.4.20
 * @since 2018.4.20
 */

public class ScaleView extends View {

    /**
     * @Variables
     */
    private Paint mPaint;

    /**
     * @Methods
     */

    /*
     * @Constructor ScaleView
     * @Param Context context
     */
    public ScaleView(Context context) {
        this(context, null);
    }

    /*
     * @Constructor ScaleView
     * @Param Context context
     * @Param AttributeSet attrs
     */
    public ScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /*
     * @Constructor ScaleView
     * @Param Context context
     * @Param AttributeSet attrs
     * @Param int defStyleAttr
     */
    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    /**
     * @Override
     */

    /*
     * @Override onDraw
     * @Param Canvas canvas
     * @Return void
     */
    @Override
    protected void onDraw(Canvas canvas) {

        float deepestWidth = ((float) getHeight()) / 4 * 2;
        float weightWidth = ((float) getHeight()) / 4 * 3;

        int deepestAlpha = 100;
        int weightAlpha = 160;

        float center_X = ((float) getWidth()) / 2;
        float center_Y = ((float) getHeight()) / 4 * 5;

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(deepestWidth);
        mPaint.setColor(Color.parseColor("#933BF7"));
        mPaint.setAlpha(deepestAlpha);
        canvas.drawCircle(center_X, center_Y, deepestWidth, mPaint);

        mPaint.setStrokeWidth(weightWidth);
        mPaint.setAlpha(weightAlpha);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(center_X, center_Y, deepestWidth + weightWidth / 2, mPaint);
    }

}
