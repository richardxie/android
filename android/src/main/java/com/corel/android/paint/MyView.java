package com.corel.android.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MyView extends View {

        private Bitmap  mBitmap;
       // private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        private int alpha;
        private Paint mPaint, mTextPaint;
        private Matrix mMatrix;
       // private FingerPaintActivity fingerPaint;
        
        //gesture animation
       
        public MyView(Context c) {
            super(c);
           // fingerPaint = (FingerPaintActivity) c;

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mMatrix = new Matrix();
        }
        
        public MyView(Context context, AttributeSet attrs) {
			super(context, attrs);
			//fingerPaint = (FingerPaintActivity) context;
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
            //mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            
            canvas.save();
            //String word = fingerPaint.mWords.get(fingerPaint.mCurrentPos).getChinese();
            //float w = mTextPaint.measureText(word);
            float h = getTextHeight(mTextPaint);
            mMatrix.reset();
            
            //float ratio = w /h;
           // mMatrix.setRectToRect(new RectF(0, 0, w, h), new RectF(0, 0, canvas.getWidth(), canvas.getHeight() * ratio), Matrix.ScaleToFit.FILL);
            canvas.concat(mMatrix);
           
            canvas.translate(60, 110);
           // canvas.drawText(word, 0, 0, mTextPaint);
            canvas.restore();
           
            
            canvas.drawPath(mPath, mPaint);
            
           /* if (fingerPaint.gesture != null) {
				canvas.save();
				w = canvas.getWidth();
				h = canvas.getHeight();
				canvas.scale(0.2f, 0.2f, w / 2,	h / 2);
				canvas.translate(2 * w, 2 * h);
				final ArrayList<GestureStroke> strokes = fingerPaint.gesture
						.getStrokes();
				final int index = fingerPaint.gestureIndex;
				for (int i = 0; i < index; i++) {
					GestureStroke stroke = strokes.get(i);
					canvas.drawPath(stroke.getPath(), mPaint);
				}
				canvas.restore();
            }*/
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            //mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            //mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            //mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
        
        public static float getTextHeight(final Paint p) {
    		return -p.getFontMetrics().ascent + p.getFontMetrics().descent + p.getFontMetrics().leading;
    	}
    }
