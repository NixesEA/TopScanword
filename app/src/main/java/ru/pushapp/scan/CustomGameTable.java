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
    Paint arrowPaint = new Paint();
    Paint rightWordPaint = new Paint();
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
        lastSelectedIndexY = selectedCellY;
        lastSelectedIndexX = selectedCellX;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startMoveX = event.getRawX();
                startMoveY = event.getRawY();

                //get start coord selected cell
                selectedCellX = (int) ((event.getX() / mScaleFactor - topX) / CELL_SIZE);
                selectedCellY = (int) ((event.getY() / mScaleFactor - topY) / CELL_SIZE);

                if ((selectedCellX >= 0 && selectedCellY >= 0) && (selectedCellX < (lengthX / CELL_SIZE - 1) && selectedCellY < (lengthY / CELL_SIZE))) {
                    highlightingWord();
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

        //!костыль
        float startX = topX;
        float startY = topY;

        lengthY = 0;
        lengthX = 0;

        if (items.size() != 0) {
            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.get(i).cellsInRow.size(); j++) {
                    unit[i][j] = new Unit();
                    unit[i][j].arrowPosition = items.get(i).cellsInRow.get(j).getWay();
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

        arrowPaint.setStrokeWidth(3f);
        arrowPaint.setColor(getResources().getColor(R.color.black));

        rightWordPaint.setColor(getResources().getColor(R.color.lightGreen));
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
            for (int i = 0; i < unit.length; i++) {
                for (int j = 0; j < unit[i].length; j++) {
                    drawCell(canvas, unit[i][j]);
                }
            }
        }

        canvas.restore();
    }

    private void drawCell(Canvas canvas, Unit unit) {
        String letter = unit.letter;
        String userLetter = unit.userLetter;
        String question = unit.question;

        float startX = topX + unit.startX;
        float startY = topY + unit.startY;
        Paint background = unit.background;

        try {
            unit.right = letter.equals(userLetter);
            //todo check row and column
        } catch (NullPointerException ignored) {
        }

        //draw cell
        canvas.drawRect(startX, startY, startX + CELL_SIZE, startY + GRID_BORDER_WIDTH, linePaint);
        startX += CELL_SIZE;
        canvas.drawRect(startX, startY, startX + GRID_BORDER_WIDTH, startY + CELL_SIZE, linePaint);
        startY += CELL_SIZE;
        canvas.drawRect(startX, startY, startX - CELL_SIZE, startY + GRID_BORDER_WIDTH, linePaint);
        startX -= CELL_SIZE;
        canvas.drawRect(startX, startY, startX + GRID_BORDER_WIDTH, startY - CELL_SIZE, linePaint);
        startY -= CELL_SIZE;

        //coloring
        if (background != null && background == questionBackgroundPaint) {
            canvas.drawRect(startX + GRID_BORDER_WIDTH, startY + GRID_BORDER_WIDTH, startX + CELL_SIZE, startY + CELL_SIZE, background);
        } else if (unit.selected) {
            canvas.drawRect(startX + GRID_BORDER_WIDTH, startY + GRID_BORDER_WIDTH, startX + CELL_SIZE, startY + CELL_SIZE, selectedRowPaint);
        }
        if (unit.inFocus) {
            canvas.drawRect(startX + GRID_BORDER_WIDTH, startY + GRID_BORDER_WIDTH, startX + CELL_SIZE, startY + CELL_SIZE, selectedCellPaint);
        }
        if (unit.right) {
            canvas.drawRect(startX + GRID_BORDER_WIDTH, startY + GRID_BORDER_WIDTH, startX + CELL_SIZE, startY + CELL_SIZE, rightWordPaint);
        }

        //draw question or letter
        if (letter == null) {
            drawMultilineText(canvas, question, startX, startY);
        } else {
            canvas.drawText("" + userLetter, 0, userLetter.length(), startX + CELL_SIZE / 2, startY + CELL_SIZE / 2, textPaint);
        }

        //draw arrow
        drawArrow(canvas, unit.arrowPosition, startX, startY);
    }

    private void drawArrow(Canvas canvas, int arrowPosition, float startX, float startY) {
        float arrowLength = CELL_SIZE / 6;

        float fstartY = 0;
        float fstartX = 0;
        float fstopY = 0;
        float fstopX = 0;

        float sstartY = 0;
        float sstartX = 0;
        float sstopY = 0;
        float sstopX = 0;

        float tstartY = 0;
        float tstartX = 0;
        float tstopY = 0;
        float tstopX = 0;
        float tsstopY = 0;
        float tsstopX = 0;

        //+1 убирает торчащие углы
        switch (arrowPosition) {
            case 1: {
                fstartX += startX + 1;
                fstartY += startY + 0;
                fstopX += fstartX + arrowLength;
                fstopY += fstartY + arrowLength;

//                canvas.drawLine(startX + 1, startY, startX + arrowLength, startY + arrowLength, arrowPaint);
//                startX += arrowLength;
//                startY += arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY + arrowLength;
//                canvas.drawLine(startX, startY, startX, startY + arrowLength, arrowPaint);
//                startY += arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX + arrowLength / 2;
                tsstopY += tstartY - arrowLength / 2;
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX + arrowLength / 2, startY - arrowLength / 2, arrowPaint);

                break;
            }
            case 2: {
                fstartX += startX + 1;
                fstartY += startY + 0;
                fstopX += fstartX + arrowLength;
                fstopY += fstartY + arrowLength;

//                canvas.drawLine(startX + 1, startY, startX + arrowLength, startY + arrowLength, arrowPaint);
//                startX += arrowLength;
//                startY += arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX + arrowLength;
                sstopY += sstartY;

//                canvas.drawLine(startX, startY, startX + arrowLength, startY , arrowPaint);
//                startX += arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX - arrowLength / 2;
                tsstopY += tstartY + arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY + arrowLength / 2, arrowPaint);
                break;
            }
            case 3: {
                fstartX += startX + 1 + CELL_SIZE/2;
                fstartY += startY + 0;
                fstopX += fstartX;
                fstopY += fstartY + arrowLength;

//                startX += CELL_SIZE/2;
//                canvas.drawLine(startX, startY, startX, startY + arrowLength, arrowPaint);
//                startY += arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY;


                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX + arrowLength / 2;
                tsstopY += tstartY - arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX + arrowLength / 2, startY - arrowLength / 2, arrowPaint);

                break;
            }
            case 4: {
                fstartX += startX + 1 + CELL_SIZE/2;
                fstartY += startY + 0;
                fstopX += fstartX;
                fstopY += fstartY + arrowLength;

//                startX += CELL_SIZE/2;
//                canvas.drawLine(startX, startY, startX, startY + arrowLength, arrowPaint);
//                startY += arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX + arrowLength;
                sstopY += sstartY;

//                canvas.drawLine(startX, startY, startX + arrowLength, startY , arrowPaint);
//                startX += arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX - arrowLength / 2;
                tsstopY += tstartY + arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY + arrowLength / 2, arrowPaint);

                break;
            }
            case 5: {
                fstartX += startX + 1 + CELL_SIZE;
                fstartY += startY + 0;
                fstopX += fstartX - arrowLength;
                fstopY += fstartY + arrowLength;

//                startX += CELL_SIZE;
//                canvas.drawLine(startX, startY, startX - arrowLength, startY + arrowLength, arrowPaint);
//                startX -= arrowLength;
//                startY += arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY + arrowLength;

//                canvas.drawLine(startX, startY, startX, startY + arrowLength , arrowPaint);
//                startY += arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX + arrowLength / 2;
                tsstopY += tstartY - arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX + arrowLength / 2, startY - arrowLength / 2, arrowPaint);
                break;
            }
            case 6: {
                fstartX += startX + 1 + CELL_SIZE;
                fstartY += startY + 0;
                fstopX += fstartX - 2 * arrowLength;
                fstopY += fstartY + 2 * arrowLength;

//                startX += CELL_SIZE;
//                canvas.drawLine(startX, startY, startX - 2 * arrowLength, startY + 2 * arrowLength, arrowPaint);
//                startX -= 2 * arrowLength;
//                startY += 2 * arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX + 1.5 * arrowLength;
                sstopY += sstartY;

//                canvas.drawLine(startX, startY, (float) (startX + 1.5 * arrowLength), startY , arrowPaint);
//                startX += 1.5 * arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX - arrowLength / 2;
                tsstopY += tstartY + arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY + arrowLength / 2, arrowPaint);
                break;
            }
            case 7: {
                fstartX += startX + 1;
                fstartY += startY + CELL_SIZE/2;
                fstopX += fstartX + arrowLength;
                fstopY += fstartY;

//                startY += CELL_SIZE/2;
//                canvas.drawLine(startX, startY, startX + arrowLength, startY, arrowPaint);
//                startX += arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY + arrowLength;

//                canvas.drawLine(startX, startY, startX, startY + arrowLength, arrowPaint);
//                startY += arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX + arrowLength / 2;
                tsstopY += tstartY - arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX + arrowLength / 2, startY - arrowLength / 2, arrowPaint);

                break;
            }
            case 8: {
                fstartX += startX + 1;
                fstartY += startY + CELL_SIZE/2;
                fstopX += fstartX + arrowLength;
                fstopY += fstartY;


//                startY += CELL_SIZE/2;
//                canvas.drawLine(startX, startY, startX + arrowLength, startY, arrowPaint);
//                startX += arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX - arrowLength / 2;
                tsstopY += tstartY + arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY + arrowLength / 2, arrowPaint);

                break;
            }
            case 9: {
                fstartX += startX + 1;
                fstartY += startY + CELL_SIZE;
                fstopX += fstartX + 2* arrowLength;
                fstopY += fstartY - 2 *arrowLength;

//                startY += CELL_SIZE;
//                canvas.drawLine(startX, startY, startX + 2 * arrowLength, startY - 2 * arrowLength, arrowPaint);
//                startX += 2* arrowLength;
//                startY -= 2* arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY + 1.5 * arrowLength;

//                canvas.drawLine(startX, startY, startX, (float) (startY + 1.5 * arrowLength), arrowPaint);
//                startY += 1.5 * arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX + arrowLength / 2;
                tsstopY += tstartY - arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX + arrowLength / 2, startY - arrowLength / 2, arrowPaint);

                break;
            }
            case 10: {
                fstartX += startX + 1;
                fstartY += startY + CELL_SIZE;
                fstopX += fstartX + arrowLength;
                fstopY += fstartY - arrowLength;

//                startY += CELL_SIZE;
//                canvas.drawLine(startX, startY, startX + arrowLength, startY - arrowLength, arrowPaint);
//                startX += arrowLength;
//                startY -= arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX + 1.5 * arrowLength;
                sstopY += sstartY;

//                canvas.drawLine(startX, startY, (float) (startX + 1.5 * arrowLength), startY, arrowPaint);
//                startX += 1.5 * arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX - arrowLength / 2;
                tsstopY += tstartY + arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY + arrowLength / 2, arrowPaint);

                break;
            }
            case 11: {
                fstartX += startX + 1 + CELL_SIZE;
                fstartY += startY + CELL_SIZE;
                fstopX += fstartX - 2* arrowLength;
                fstopY += fstartY - 2* arrowLength;

//                startY += CELL_SIZE;
//                startX += CELL_SIZE;
//                canvas.drawLine(startX, startY, startX - 2 * arrowLength, startY - 2 * arrowLength, arrowPaint);
//                startX -= 2* arrowLength;
//                startY -= 2* arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY + 1.5 * arrowLength;

//                canvas.drawLine(startX, startY, startX, (float) (startY + 1.5 * arrowLength), arrowPaint);
//                startY += 1.5 * arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX + arrowLength / 2;
                tsstopY += tstartY - arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX + arrowLength / 2, startY - arrowLength / 2, arrowPaint);

                break;
            }
            case 12: {
                fstartX += startX + 1 + CELL_SIZE;
                fstartY += startY + CELL_SIZE;
                fstopX += fstartX - 2* arrowLength;
                fstopY += fstartY - 2* arrowLength;

//                startY += CELL_SIZE;
//                startX += CELL_SIZE;
//                canvas.drawLine(startX, startY, startX - 2 * arrowLength, startY - 2 * arrowLength, arrowPaint);
//                startX -= 2* arrowLength;
//                startY -= 2* arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX + 1.5 * arrowLength;
                sstopY += sstartY;

//                canvas.drawLine(startX, startY, (float) (startX + 1.5 * arrowLength), startY , arrowPaint);
//                startX += 1.5 * arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX - arrowLength / 2;
                tsstopY += tstartY + arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY + arrowLength / 2, arrowPaint);

                break;
            }
            case 13: {
                fstartX += startX + 1 + CELL_SIZE/2;
                fstartY += startY + CELL_SIZE;
                fstopX += fstartX;
                fstopY += fstartY - arrowLength;

//                startY += CELL_SIZE;
//                startX += CELL_SIZE/2;
//                canvas.drawLine(startX, startY, startX, startY - arrowLength, arrowPaint);
//                startY -= arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX + 1.5 * arrowLength;
                sstopY += sstartY;

//                canvas.drawLine(startX, startY, (float) (startX + 1.5 * arrowLength), startY , arrowPaint);
//                startX += 1.5 * arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX - arrowLength / 2;
                tsstopY += tstartY + arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY + arrowLength / 2, arrowPaint);

                break;
            }
            case 14: {
                fstartX += startX + 1 + CELL_SIZE;
                fstartY += startY + CELL_SIZE/2;
                fstopX += fstartX - arrowLength;
                fstopY += fstartY;

//                startY += CELL_SIZE/2;
//                startX += CELL_SIZE;
//                canvas.drawLine(startX, startY, startX - arrowLength, startY, arrowPaint);
//                startX -= arrowLength;

                sstartX += fstopX;
                sstartY += fstopY;
                sstopX += sstartX;
                sstopY += sstartY + 1.5 * arrowLength;

//                canvas.drawLine(startX, startY, startX, (float) (startY + 1.5 * arrowLength), arrowPaint);
//                startY += 1.5 * arrowLength;

                tstartY += sstopY;
                tstartX += sstopX;
                tstopX += tstartX - arrowLength / 2;
                tstopY += tstartY - arrowLength / 2;

                tsstopX += tstartX + arrowLength / 2;
                tsstopY += tstartY - arrowLength / 2;

//                canvas.drawLine(startX, startY, startX - arrowLength / 2, startY - arrowLength / 2, arrowPaint);
//                canvas.drawLine(startX, startY, startX + arrowLength / 2, startY - arrowLength / 2, arrowPaint);

                break;
            }
        }


        canvas.drawLine(fstartX,fstartY,fstopX,fstopY, arrowPaint);

        canvas.drawLine(sstartX, sstartY, sstopX, sstopY, arrowPaint);

        canvas.drawLine(tstartX, tstartY, tstopX, tstopY, arrowPaint);
        canvas.drawLine(tstartX, tstartY, tsstopX, tsstopY, arrowPaint);

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

    private void highlightingWord() {
        try {
            unit[lastSelectedIndexY][lastSelectedIndexX].inFocus = false;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            coloringLine(letterCellPaint, lastSelectedIndexY, lastSelectedIndexX);
            if (unit[selectedCellY][selectedCellX].letter != null) {
                unit[selectedCellY][selectedCellX].inFocus = true;
                //check double tap
                if (selectedCellX == lastSelectedIndexX && selectedCellY == lastSelectedIndexY) {
                    wordOrientation = !wordOrientation;
                    coloringLine(selectedRowPaint, selectedCellY, selectedCellX);
                } else {
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
        boolean selected = false;
        if (paint == selectedRowPaint) {
            selected = true;
        }

        if (wordOrientation) {
            //landscape
            int x = selectedCellX;
            try {
                while (unit[selectedCellY][x].letter != null) {
                    unit[selectedCellY][x].selected = selected;
                    x++;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }

            x = selectedCellX;
            try {
                while (unit[selectedCellY][x].letter != null) {
                    unit[selectedCellY][x].selected = selected;
                    x--;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        } else {
            //portrait
            int y = selectedCellY;
            try {
                while (unit[y][selectedCellX].letter != null) {
                    unit[y][selectedCellX].selected = selected;
                    y++;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }

            y = selectedCellY;
            try {
                while (unit[y][selectedCellX].letter != null) {
                    unit[y][selectedCellX].selected = selected;
                    y--;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
    }

    public void setLetter(String letter) {
        try {
            if (unit[selectedCellY][selectedCellX].letter != null) {
                unit[selectedCellY][selectedCellX].userLetter = letter.toUpperCase();
                unit[selectedCellY][selectedCellX].inFocus = false;

                if (wordOrientation) {
                    if (unit[selectedCellY][selectedCellX + 1].background != questionBackgroundPaint) {
                        lastSelectedIndexX = selectedCellX;
                        selectedCellX++;
                        if (topX + selectedCellX * CELL_SIZE > WIDTH_SCREEN * .7) {
                            topX -= CELL_SIZE;
                        }
                    }
                } else {
                    if (unit[selectedCellY + 1][selectedCellX].background != questionBackgroundPaint) {
                        lastSelectedIndexY = selectedCellY;
                        selectedCellY++;
                        if (topY + selectedCellY * CELL_SIZE > HEIGHT_SCREEN * .7) {
                            topY -= CELL_SIZE;
                        }
                    }
                }
            }

        } catch (ArrayIndexOutOfBoundsException ignored) {
            ignored.printStackTrace();
        }
        unit[selectedCellY][selectedCellX].inFocus = true;

        invalidate();
    }

    public void deleteLetter() {
        try {
            if (unit[selectedCellY][selectedCellX].letter != null) {
                unit[selectedCellY][selectedCellX].userLetter = "";
                unit[selectedCellY][selectedCellX].selected = true;
                unit[selectedCellY][selectedCellX].inFocus = false;

                if (wordOrientation) {
                    if (unit[selectedCellY][selectedCellX - 1].background != questionBackgroundPaint) {
                        lastSelectedIndexX = selectedCellX;
                        selectedCellX--;
                        topX += CELL_SIZE;
                    }
                } else {
                    if (unit[selectedCellY - 1][selectedCellX].background != questionBackgroundPaint) {
                        lastSelectedIndexY = selectedCellY;
                        selectedCellY--;
                        topY += CELL_SIZE;
                    }
                }
            }

        } catch (ArrayIndexOutOfBoundsException ignored) {
            ignored.printStackTrace();
        }
        unit[selectedCellY][selectedCellX].inFocus = true;

        invalidate();
    }


    private class Unit {
        boolean right = false;
        boolean inFocus = false;
        boolean selected = false;

        int arrowPosition = 0;

        String letter;
        String question;
        Paint background;

        String userLetter = "";

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
