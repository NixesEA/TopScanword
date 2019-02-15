package ru.pushapp.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

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
    Paint selectedRowPaint = new Paint();
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

    int lastSelectedIndexX = -1;
    int lastSelectedIndexY = -1;

    int selectedCellX = -1;
    int selectedCellY = -1;

    //true - landscape
    //false - portrait
    boolean wordOrientation = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startMoveX = event.getRawX();
                startMoveY = event.getRawY();

                //get start coord selected cell
                selectedCellX = (int) ((event.getX() / mScaleFactor - topX) / CELL_SIZE);
                selectedCellY = (int) ((event.getY() / mScaleFactor - topY) / CELL_SIZE);

                if ((selectedCellX >= 0 && selectedCellY >= 0) && (selectedCellX < (lengthX / CELL_SIZE - 1) && selectedCellY < (lengthY / CELL_SIZE))) {
                    highlightingWord(selectedCellY, selectedCellX);
                }

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

        Log.d("TEST", "width = " + WIDTH_SCREEN);
        Log.d("TEST", "height = " + HEIGHT_SCREEN);

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
        }
        endX = topX + lengthX;
        endY = topY + lengthY;
    }

    public CustomGameTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        linePaint.setColor(getResources().getColor(R.color.black));

        letterCellPaint.setColor(getResources().getColor(R.color.white));
        selectedCellPaint.setColor(getResources().getColor(R.color.selectedCell));
        selectedRowPaint.setColor(getResources().getColor(R.color.selectedRow));
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

        if (unit.length != 0 && CELL_SIZE != 0) {
            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.get(i).cellsInRow.size(); j++) {
                    drawCell(canvas, unit[i][j]);
                }
            }
        }

        canvas.restore();
    }

    public void setLetter(String letter) {
        try {
            if (unit[selectedCellY][selectedCellX].letter != null) {
                unit[selectedCellY][selectedCellX].letter = letter.toUpperCase();
                unit[selectedCellY][selectedCellX].background = selectedRowPaint;

                Log.i("letterTEST","setLetter " + unit[selectedCellY][selectedCellX].letter);

                if (wordOrientation) {
                    if (unit[selectedCellY][selectedCellX + 1].background != questionBackgroundPaint) {
                        selectedCellX++;
                        topX -= CELL_SIZE;
                    }
                } else {
                    if (unit[selectedCellY + 1][selectedCellX].background != questionBackgroundPaint) {
                        selectedCellY++;
                        topY -= CELL_SIZE;
                    }
                }
            }


        } catch (ArrayIndexOutOfBoundsException ignored) {
            ignored.printStackTrace();
        }
        unit[selectedCellY][selectedCellX].background = selectedCellPaint;

        invalidate();
        Log.d("CustomView", "Current text: " + letter);
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


        if (background != null) {
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

    private void highlightingWord(int selectedCellY, int selectedCellX) {
        try {
            if (unit[selectedCellY][selectedCellX].letter != null) {
                if (selectedCellX == lastSelectedIndexX && selectedCellY == lastSelectedIndexY) {
                    coloringLine(letterCellPaint, lastSelectedIndexY, lastSelectedIndexX);
                    wordOrientation = !wordOrientation;
                    coloringLine(selectedRowPaint, selectedCellY, selectedCellX);
                } else {
                    coloringLine(letterCellPaint, lastSelectedIndexY, lastSelectedIndexX);
                    coloringLine(selectedRowPaint, selectedCellY, selectedCellX);
                }

                unit[selectedCellY][selectedCellX].background = selectedCellPaint;

                lastSelectedIndexX = selectedCellX;
                lastSelectedIndexY = selectedCellY;
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    private void coloringLine(Paint paint, int selectedCellY, int selectedCellX) {
        if (wordOrientation) {
            //landscape
            int x = selectedCellX;
            try {
                while (unit[selectedCellY][x].letter != null) {
                    unit[selectedCellY][x].background = paint;
                    x++;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }

            x = selectedCellX;
            try {
                while (unit[selectedCellY][x].letter != null) {
                    unit[selectedCellY][x].background = paint;
                    x--;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        } else {
            //portrait
            int y = selectedCellY;
            try {
                while (unit[y][selectedCellX].letter != null) {
                    unit[y][selectedCellX].background = paint;
                    y++;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }

            y = selectedCellY;
            try {
                while (unit[y][selectedCellX].letter != null) {
                    unit[y][selectedCellX].background = paint;
                    y--;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
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
