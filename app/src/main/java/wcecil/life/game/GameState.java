package wcecil.life.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

import java.util.Timer;

import GameOfLife.Board;
import GameOfLife.IBoardVisualizer;

public class GameState extends Board implements IBoardVisualizer {
    private static final long PERIOD = 333l;
    private static final long INITIAL_DELAY = 1000l;
    private static final float BORDER_X = 2f;
    private static final float BORDER_Y = 2f;
    static GameState instance = new GameState();
    private final Paint paintEmpty;
    private final Paint paintAlive;
    float dw, dh, aspectRatio, xStep, yStep;
    Timer timer;
    ImageView imageView;
    Canvas canvas;

    public boolean running = false;
    private int areaSize = 16;

    private GameState() {
        timer = new Timer("GameOfLifeTimer", true);
        timer.scheduleAtFixedRate(GameTimerTask.getInstance(), INITIAL_DELAY, PERIOD);
        paintAlive = new Paint();
        paintAlive.setColor(0xFFD99AB5);
        paintEmpty = new Paint();
        paintEmpty.setColor(0xFFF0F0F0);
    }

    public static GameState getInstance() {
        return instance;
    }

    @Override
    public void displayCurrentStateOfBoard() {
        canvas.drawColor(Color.BLUE);

        float x0 = 0f, y0, x1, y1;
        for (int i = 0; i < areaSize * aspectRatio; i++) {
            y0 = 0f;
            x1 = x0 + xStep;
            x0 += BORDER_X;
            for (int j = 0; j < areaSize; j++) {

                y1 = y0 + yStep;
                y0 += BORDER_Y;

                Paint paint;
                if (isCellExist(j, i)) {
                    paint = paintAlive;
                } else {
                    paint = paintEmpty;
                }

                canvas.drawRect(x0, y0, x1, y1, paint);

                y0 = y1;
            }
            x0 = x1;
        }

        //force redraw at next chance
        imageView.postInvalidate();
    }

    @Override
    @Deprecated
    public void playGame() {
        nextState();
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;

        dw = canvas.getWidth();
        dh = canvas.getHeight();

        aspectRatio = (dw) / (dh);

        xStep = (dw) / (areaSize * aspectRatio);
        yStep = (dh) / areaSize;


    }

    public void handleClick(float x, float y) {

        int myX = (int) (x / xStep);
        int myY = (int) (y / xStep);

        addCell(myY,myX);

        System.out.println("cell added @"+myX+","+myY);

        displayCurrentStateOfBoard();
    }
}
