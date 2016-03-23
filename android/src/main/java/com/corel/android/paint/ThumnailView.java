package com.corel.android.paint;

import java.util.List;
import com.corel.android.pinyin.PinYin;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ThumnailView extends View {

    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    private int alpha;
    private Paint mPaint, mTextPaint;
    private Matrix mMatrix;
    private List<PinYin> mWords;
        
       
    public ThumnailView(Context c) {
        super(c);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mMatrix = new Matrix();
    }
        
    public ThumnailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mMatrix = new Matrix();
	}
        
    public void setPaint(final Paint paint, final Paint textPaint) {
        mPaint = paint;
        mTextPaint = textPaint;
    }
        
	public void fade() {
        if(alpha == 255) alpha = 0;
        mTextPaint.setAlpha(alpha);
        alpha += 10;
    }
        
    public void reset() {
        mPath.reset();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFAAAAAA);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            
        canvas.save();
        String word = mWords.get(0).getChinese();
        float w = mTextPaint.measureText(word);
        float h = getTextHeight(mTextPaint);
        mMatrix.reset();
            
            //float ratio = w /h;
           // mMatrix.setRectToRect(new RectF(0, 0, w, h), new RectF(0, 0, canvas.getWidth(), canvas.getHeight() * ratio), Matrix.ScaleToFit.FILL);
            canvas.concat(mMatrix);
           
            canvas.translate(60, 110);
            canvas.drawText(word, 0, 0, mTextPaint);
            canvas.restore();
           
            
            canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

        
    public static float getTextHeight(final Paint p) {
    	return -p.getFontMetrics().ascent + p.getFontMetrics().descent + p.getFontMetrics().leading;
    }
}
