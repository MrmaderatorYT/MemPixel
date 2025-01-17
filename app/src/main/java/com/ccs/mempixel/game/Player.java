package com.ccs.mempixel.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player extends GameObject {
    private Bitmap image; // Зображення гравця
    private int speed; // Швидкість гравця

    public Player(float x, float y, int width, int height, Bitmap image) {
        super(x, y, width, height);
        this.image = image;
        this.speed = 5; // Базова швидкість
    }

    // Малювання гравця
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, getX(), getY(), null);
    }

    // Оновлення позиції гравця
    public void update(float deltaX, float deltaY) {
        setX(getX() + deltaX * speed);
        setY(getY() + deltaY * speed);
    }

    // Встановлення швидкості (для прискорення)
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // Стрільба
    public void shoot() {
        // Логіка стрільби (наприклад, створення кулі)
        System.out.println("Player is shooting!");
    }
}