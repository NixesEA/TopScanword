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
    float GRID_BORDER_WIDTH = 3;
    float CELL_SIZE;
    float WIDTH_SCREEN;
    float HEIGHT_SCREEN;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    ArrayList<RowUnit> items;
    Unit[][] unit = new Unit[18][9];

    Canvas localCanvas = null;
    Paint linePaint = new Paint();
    Paint letterCellPaint = new Paint();
    Paint selectedCellPaint = new Paint();
    Paint questionBackgroundPaint = new Paint();
    TextPaint textPaint = new TextPaint();


    float startMoveX = 0f;
    float startMoveY = 0f;

    float topX = 0f;
    float topY = 0f;

    float endX = 0f;
    float endY = 0f;

    float lengthX = 0f;
    float lengthY = 0f;

    int selectedCellX = 0;
    int selectedCellY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startMoveX = event.getRawX();
                startMoveY = event.getRawY();

                selectedCellX = (int) (event.getRawX() / CELL_SIZE);
                selectedCellY = (int) (event.getRawY() / CELL_SIZE);

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

                //check left limit
                if (topX > WIDTH_SCREEN * .2) {
                    topX = (float) (WIDTH_SCREEN * .2);
                }
                if (endX < WIDTH_SCREEN * .8) {
                    topX = (float) ((WIDTH_SCREEN * .8) - lengthX);         //fixme: wrong right border!
                }

                //check right limit
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

        //костыль
        float startX = topX;
        float startY = topY;

        lengthY = 0;
        lengthX = 0;

        if (items.size() != 0) {
            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.get(i).cellsInRow.size(); j++) {
                    unit[i][j] = new Unit();
                    unit[i][j].letter = items.get(i).cellsInRow.get(j).getLetter();
                    unit[i][j].question = items.get(i).cellsInRow.get(j).getQuestion();
                    unit[i][j].startX = startX;
                    unit[i][j].startY = startY;

                    if (items.get(i).cellsInRow.get(j).getQuestion() != null) {
                        unit[i][j].background = questionBackgroundPaint;
                    } else if (unit[i][j].selected) {
                        unit[i][j].background = selectedCellPaint;
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
            endX = topX + lengthX;
            endY = topY + lengthY;
        }
    }

    public CustomGameTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        linePaint.setColor(getResources().getColor(R.color.black));

        letterCellPaint.setColor(getResources().getColor(R.color.white));
        selectedCellPaint.setColor(getResources().getColor(R.color.selected));
        questionBackgroundPaint.setColor(getResources().getColor(R.color.enableButton));

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


        if (unit.length != 0 && CELL_SIZE != 0){
            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.get(i).cellsInRow.size(); j++) {
                    drawCell(canvas,unit[i][j]);
                }
            }
        }

        canvas.restore();
    }

    private void drawCell(Canvas canvas, Unit unit) {
        String letter = unit.letter;
        String question = unit.question;

        float startX = topX + unit.startX;
        float startY = topY + unit.startY;
        Paint background = unit.background;

        //draw cell
        canvas.drawRect(startX, startY, startX + CELL_SIZE, startY + GRID_BORDER_WIDTH, linePaint);
        startX += CELL_SIZE;
        canvas.drawRect(startX, startY, startX + GRID_BORDER_WIDTH, startY + CELL_SIZE, linePaint);
        startY += CELL_SIZE;
        canvas.drawRect(startX, startY, startX - CELL_SIZE, startY + GRID_BORDER_WIDTH, linePaint);
        startX -= CELL_SIZE;
        canvas.drawRect(startX, startY, startX + GRID_BORDER_WIDTH, startY - CELL_SIZE, linePaint);
        startY -= CELL_SIZE;


        if (background != null){
            canvas.drawRect(startX + GRID_BORDER_WIDTH, startY + GRID_BORDER_WIDTH, startX + CELL_SIZE, startY + CELL_SIZE, background);
        }

        //draw question or letter
        if (letter == null) {
            drawMultilineText(canvas, question, startX, startY);
        } else {
            canvas.drawText("" + letter, 0, letter.length(), startX + CELL_SIZE / 2, startY + CELL_SIZE / 2, textPaint);
        }
    }

    private void drawMultilineText(Canvas canvas, String question, float startX, float startY) {
        StaticLayout staticLayout = new StaticLayout(question, textPaint, (int) CELL_SIZE, Layout.Alignment.ALIGN_NORMAL, 1, 1, true);
        canvas.save();

        float textHeight = getTextHeight(question, textPaint);
        int numberOfTextLines = staticLayout.getLineCount();
        float textYCoordinate = startY + (CELL_SIZE - numberOfTextLines * textHeight) / 4;

        canvas.translate(startX + CELL_SIZE / 2, textYCoordinate);
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


    private class Unit {
        boolean selected = false;
        String letter;
        String question;
        Paint background;

        float startX;
        float startY;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

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
