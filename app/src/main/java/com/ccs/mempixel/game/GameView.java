package com.ccs.mempixel.game;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.ccs.mempixel.joystick.Joystick;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread gameThread;
    private boolean isRunning;
    private Joystick joystick;
    private TileMap tileMap;
    private Camera camera;
    private int playerX, playerY; // Позиція гравця
    private int playerSpeed = 5; // Швидкість гравця

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Приклад карти
        int[][] map = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        // Ініціалізація карти
        tileMap = new TileMap(getContext(), map, 64); // 64 - розмір тайла

        // Ініціалізація камери
        camera = new Camera(getWidth(), getHeight());

        // Початкова позиція гравця
        playerX = tileMap.getTileSize() * 2;
        playerY = tileMap.getTileSize() * 2;

        // Ініціалізація джойстика
        joystick = new Joystick(100, getHeight() - 100, 80, 40);

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
        // Отримання напрямку від джойстика
        PointF direction = joystick.getDirection();

        // Нова позиція гравця
        int newPlayerX = playerX + (int) (direction.x * playerSpeed);
        int newPlayerY = playerY + (int) (direction.y * playerSpeed);

        // Перевірка колізій
        if (!tileMap.isWall(newPlayerX / tileMap.getTileSize(), newPlayerY / tileMap.getTileSize())) {
            playerX = newPlayerX;
            playerY = newPlayerY;
        }

        // Оновлення камери
        camera.update(playerX, playerY);
    }

    private void draw() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            try {
                // Очистка екрану (білий колір)
                canvas.drawColor(0xFFFFFFFF);

                // Малювання видимої частини карти
                tileMap.draw(canvas, camera.getX(), camera.getY());

                // Малювання гравця (наприклад, червоний квадрат)
                Paint playerPaint = new Paint();
                playerPaint.setColor(0xFFFF0000); // Червоний колір
                canvas.drawRect(
                        playerX - camera.getX(),
                        playerY - camera.getY(),
                        playerX - camera.getX() + tileMap.getTileSize(),
                        playerY - camera.getY() + tileMap.getTileSize(),
                        playerPaint
                );

                // Малювання джойстика
                joystick.draw(canvas);
            } finally {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                joystick.updateHandle(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                joystick.resetHandle();
                break;
        }
        return true;
    }
}