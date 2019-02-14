package ru.pushapp.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

import ru.pushapp.scan.JsonUtil.CellUnit;
import ru.pushapp.scan.JsonUtil.RowUnit;


public class CustomGameTable extends View {
    float CELL_SIZE;
    float WIDTH_SCREEN;
    float HEIGHT_SCREEN;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    ArrayList<RowUnit> items;

    Paint paint = new Paint();
    TextPaint textPaint = new TextPaint();
    Canvas localCanvas = null;


    float startMoveX = 0f;
    float startMoveY = 0f;

    float topX = 0f;
    float topY = 0f;

    float endX = 0f;
    float endY = 0f;

    float lengthX = 0f;
    float lengthY = 0f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startMoveX = event.getRawX();
                startMoveY = event.getRawY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float x = event.getRawX();
                float y = event.getRawY();

                float dx = x - startMoveX;
                float dy = y - startMoveY;

                topX += dx / mScaleFactor;
                topY += dy / mScaleFactor;

                endX = topX + lengthX;
                endY = topY + lengthY;


                if (topX > WIDTH_SCREEN * .2) {
                    topX = (float) (WIDTH_SCREEN * .2);
                }
                if (endX < WIDTH_SCREEN * .8) {
                    topX = (float) ((WIDTH_SCREEN * .8) - lengthX);         //fixme: wrong right border!
                }


                if (topY > HEIGHT_SCREEN * .2) {
                    topY = (float) (HEIGHT_SCREEN * .2);
                }
                if (endY < HEIGHT_SCREEN * .8) {
                    topY = (float) ((HEIGHT_SCREEN * .8) - lengthY);
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
        CELL_SIZE = WIDTH_SCREEN / 9;
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
                    canvas.drawRect(startX, startY, startX + CELL_SIZE, startY + 3, paint);
                    startX += CELL_SIZE;
                    canvas.drawRect(startX, startY, startX + 3, startY + CELL_SIZE, paint);
                    startY += CELL_SIZE;
                    canvas.drawRect(startX, startY, startX - CELL_SIZE, startY + 3, paint);
                    startX -= CELL_SIZE;
                    canvas.drawRect(startX, startY, startX + 3, startY - CELL_SIZE/*+3*/, paint);
                    startY -= CELL_SIZE;

                    //draw question or letter
                    if (cell.getLetter() == null) {
                        drawMultilineText(canvas, cell.getQuestion(), startX, startY);
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

        endX = topX + lengthX;
        endY = topY + lengthY;
        canvas.restore();
    }

    private void drawMultilineText(Canvas canvas, String question, float startX, float startY) {
        StaticLayout staticLayout = new StaticLayout(question, textPaint, (int) CELL_SIZE, Layout.Alignment.ALIGN_NORMAL, 1, 1, true);
        canvas.save();

        float textHeight = getTextHeight(question, textPaint);
        int numberOfTextLines = staticLayout.getLineCount();
        float textYCoordinate = startY + (CELL_SIZE - numberOfTextLines*textHeight)/4;

        canvas.translate(startX + CELL_SIZE / 2, textYCoordinate );

        staticLayout.draw(canvas);
        canvas.restore();

    }

    public void setContent(ArrayList<RowUnit> items) {
        this.items = items;
    }

    private float getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 2.0f));

            WIDTH_SCREEN = getMeasuredWidth() / mScaleFactor;
            HEIGHT_SCREEN = getMeasuredHeight() / mScaleFactor;

            lengthX = lengthX * mScaleFactor;
            lengthY = lengthY * mScaleFactor;

            invalidate();
            return true;
        }
    }
}
