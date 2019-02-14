package ru.pushapp.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

import ru.pushapp.scan.JsonUtil.CellUnit;
import ru.pushapp.scan.JsonUtil.RowUnit;


public class CustomGameTable extends View {
    float CELL_SIZE = 100;
    float WIDTH_SCREEN;
    float HEIGHT_SCREEN;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    ArrayList<RowUnit> items;

    Paint paint = new Paint();
    Paint textPaint = new Paint();
    Canvas localCanvas = null;


    float startMoveX = 0;
    float startMoveY = 0;

    float topX = 0;
    float topY = 0;
    float bottomX = 0;
    float bottomY = 0;

    float lengthX = 0;
    float lengthY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startMoveX = event.getX();
                startMoveY = event.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float x = event.getX();
                float y = event.getY();

                float dx = x - startMoveX;
                float dy = y - startMoveY;

                topX += dx / mScaleFactor;
                topY += dy / mScaleFactor;

                if (topX > WIDTH_SCREEN * .2){
                    topX = (float) (WIDTH_SCREEN * .2);
                }else if (topX <  - WIDTH_SCREEN * .2){
                    topX = (float) (- WIDTH_SCREEN * .2);
                }

                if (topY > HEIGHT_SCREEN* .2){
                    topY = (float) (HEIGHT_SCREEN* .2);
                }else if (topY <  - HEIGHT_SCREEN* .2){
                    topY = (float) (- HEIGHT_SCREEN* .2);
                }

                startMoveX = x;
                startMoveY = y;

                invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WIDTH_SCREEN = getMeasuredWidth();
        HEIGHT_SCREEN = getMeasuredHeight();
    }

    public CustomGameTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        paint.setColor(getResources().getColor(R.color.black));

        textPaint.setColor(getResources().getColor(R.color.purple));
        textPaint.setTextSize(24);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setLinearText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        localCanvas = canvas;
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        float startX = topX;
        float startY = topY;

        lengthY = 0;
        lengthX = 0;

        if (items.size() != 0) {
            for (RowUnit raw : items) {
                for (CellUnit cell : raw.cellsInRow) {

                    //draw cell
                    canvas.drawLine(startX, startY, startX + CELL_SIZE, startY, paint);
                    startX += CELL_SIZE;
                    canvas.drawLine(startX, startY, startX, startY + CELL_SIZE, paint);
                    startY += CELL_SIZE;
                    canvas.drawLine(startX, startY, startX - CELL_SIZE, startY, paint);
                    startX -= CELL_SIZE;
                    canvas.drawLine(startX, startY, startX, startY - CELL_SIZE, paint);
                    startY -= CELL_SIZE;

                    if (cell.getLetter() == null) {
                        canvas.drawText("" + cell.getQuestion(), 0, cell.getQuestion().length(), startX + CELL_SIZE / 2, startY + CELL_SIZE / 2, textPaint);
                    } else {
                        canvas.drawText("" + cell.getLetter(), 0, cell.getLetter().length(), startX + CELL_SIZE / 2, startY + CELL_SIZE / 2, textPaint);
                    }

                    startX += CELL_SIZE;
                    if (lengthX < startX + CELL_SIZE) {
                        lengthX += CELL_SIZE;
                    }
                }
                startX = topX;
                startY += CELL_SIZE;
                lengthY += CELL_SIZE;
            }
        }

        bottomX = topX + lengthX;
        bottomY = topY + lengthY;
        canvas.restore();
    }

    public void setContent(ArrayList<RowUnit> items) {
        this.items = items;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 3.0f));

            WIDTH_SCREEN = getMeasuredWidth() / mScaleFactor;
            HEIGHT_SCREEN = getMeasuredHeight() / mScaleFactor;

            invalidate();
            return true;
        }
    }
}
