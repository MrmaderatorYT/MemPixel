package com.ccs.mempixel.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.ccs.mempixel.R;
import com.ccs.mempixel.joystick.Joystick;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread gameThread;
    private boolean isRunning;
    private Joystick joystick;
    private TileMap tileMap;
    private Camera camera;
    private int playerX, playerY;
    private int playerSpeed = 5;
    private Bitmap playerBitmap;
    private Bitmap bulletBitmap; // Зображення кулі
    private Bitmap speedButtonBitmap;
    private Bitmap shootButtonBitmap;
    private boolean isAccelerated = false; // Чи активовано прискорення
    private long accelerationEndTime = 0; // Час закінчення прискорення
    private boolean isShooting = false; // Чи відбувається стрілянина
    private int bulletX, bulletY; // Позиція кулі
    private int bulletSpeed = 20; // Швидкість кулі
    // Стани кнопок
    private boolean isSpeedButtonPressed = false;
    private boolean isShootButtonPressed = false;
    // Позиції кнопок
    private float speedButtonX, speedButtonY;
    private float shootButtonX, shootButtonY;

    // Кнопки
    private Button accelerateButton;
    private Button shootButton;
    private Player player;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        playerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        bulletBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);

        speedButtonBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.run_btn);
        shootButtonBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shoot_btn);


        bulletBitmap = Bitmap.createScaledBitmap(bulletBitmap, 1, 1, false); // Зменшуємо кулю до 1x1 пікселя
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        final int[][] map = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        tileMap = new TileMap(getContext(), map, 256);
        playerBitmap = Bitmap.createScaledBitmap(playerBitmap, 96, 96, false);
        camera = new Camera(getWidth(), getHeight());

        playerX = tileMap.getTileSize() * 2;
        playerY = tileMap.getTileSize() * 2;

        joystick = new Joystick(100, getHeight() - 100, 80, 40);

        // Ініціалізація кнопок
        shootButton = new Button(getWidth() - 200, getHeight() - 200, 100, 50, "Shoot");

        // Кнопка прискорення (ліворуч внизу)
        accelerateButton = new Button(50, 50, 100, 50, "Accelerate");


        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void run() {
        while (isRunning) {
            update();
            draw();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // Оновлення прискорення
        if (isAccelerated && System.currentTimeMillis() > accelerationEndTime) {
            isAccelerated = false;
            playerSpeed = 5;
        }

        // Оновлення позиції гравця
        PointF direction = joystick.getDirection();
        int newPlayerX = playerX + (int) (direction.x * playerSpeed);
        int newPlayerY = playerY + (int) (direction.y * playerSpeed);

        if (!tileMap.isWall(newPlayerX / tileMap.getTileSize(), newPlayerY / tileMap.getTileSize())) {
            playerX = newPlayerX;
            playerY = newPlayerY;
        }

        // Оновлення позиції кулі
        if (isShooting) {
            player.shoot(); // Виклик методу стрільби

        }

        camera.update(playerX, playerY);
    }

    private void draw() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            try {
                canvas.drawColor(0xFFFFFFFF);

                tileMap.draw(canvas, camera.getX(), camera.getY());

                canvas.drawBitmap(
                        playerBitmap,
                        playerX - camera.getX(),
                        playerY - camera.getY(),
                        null
                );

                // Малювання кулі
                if (isShooting) {
                    canvas.drawBitmap(
                            bulletBitmap,
                            bulletX - camera.getX(),
                            bulletY - camera.getY(),
                            null
                    );
                }

                joystick.draw(canvas);

                // Малювання кнопок
                canvas.drawBitmap(shootButtonBitmap, shootButton.getX(), shootButton.getY(), null); // Кнопка стрільби
                canvas.drawBitmap(speedButtonBitmap, accelerateButton.getX(), accelerateButton.getY(), null); // Кнопка прискорення
            } finally {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Перевірка натискання на кнопку прискорення
                if (accelerateButton.isTouched(touchX, touchY)) {
                    isSpeedButtonPressed = true;
                    isShootButtonPressed = false; // Інша кнопка не активна
                    return true; // Подія оброблена, джойстик не реагує
                }

                // Перевірка натискання на кнопку стрільби
                if (shootButton.isTouched(touchX, touchY)) {
                    isShootButtonPressed = true;
                    isSpeedButtonPressed = false; // Інша кнопка не активна
                    return true; // Подія оброблена, джойстик не реагує
                }

                // Якщо дотик не на кнопках, обробляємо джойстик
                isSpeedButtonPressed = false;
                isShootButtonPressed = false;
                joystick.updateHandle(touchX, touchY);
                break;

            case MotionEvent.ACTION_UP:
                // Скидання станів кнопок
                isSpeedButtonPressed = false;
                isShootButtonPressed = false;
                joystick.resetHandle();
                break;
        }
        return true;
    }
}