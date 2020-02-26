package com.maedi.soft.ino.gallery.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.maedi.soft.ino.gallery.R;

public class CircleLayoutLinear extends LinearLayout {

    private Paint paint, paintBorder;
    private Context ctx;
    private int color, borderColor;

    public CircleLayoutLinear(Context context) {
        super(context);
        ctx = context;
        initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public CircleLayoutLinear(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleDotLayout,
                0, 0
        );
        try {
            if(null == a)
            {
                color = ctx.getColor(R.color.white);
                borderColor = ctx.getColor(R.color.gray);
            }
            else
            {
                color = a.getColor(R.styleable.CircleDotLayout_circleDotColor, ctx.getColor(R.color.white));
                borderColor = a.getColor(R.styleable.CircleDotLayout_circleDotBorderColor, ctx.getColor(R.color.gray));
            }
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        initialize();
    }

    public CircleLayoutLinear(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
        initialize();
    }

    private void initialize() {
        this.setWillNotDraw(false);
        paint = new Paint();
        paint.setAntiAlias(true);
        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);//(ctx.getResources().getColor(R.color.red));
        float x = w/2;
        float y = h/2;
        float radius = (x/2)+(x/3);//FuncHelper.hasMarshmallowAndAbove() ? 60 : 30;
        //canvas.translate(getWidth()/2f,getHeight()/2f);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(3f);
        paintBorder.setColor(borderColor);
        //this.setLayerType(LAYER_TYPE_SOFTWARE, paint);
        //paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.parseColor("#40ffffff"));
        canvas.drawCircle(x,y, radius+2, paintBorder);
        canvas.drawCircle(x,y, radius, paint);
    }

    public void setBackgroundColor(int newColor){
        color = newColor;
    }

    public int getBackgroundColor(){
        return color;
    }

    public void setBorderColor(int newColor){
        borderColor = newColor;
    }

    public int getBorderColor(){
        return borderColor;
    }
}