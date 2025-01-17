package com.ccs.mempixel.game;

public class Camera {
    private int x, y; // Позиція камери
    private int screenWidth, screenHeight; // Розміри екрану

    public Camera(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    // Оновлення позиції камери (слідкування за гравцем)
    public void update(int playerX, int playerY) {
        x = playerX - screenWidth / 2;
        y = playerY - screenHeight / 2;
    }

    // Отримання позиції камери
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}