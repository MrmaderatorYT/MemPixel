package com.ccs.mempixel.game;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Button {
    private int x, y, width, height;
    private String text;
    private Paint paint;

    public Button(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;

        paint = new Paint();
        paint.setColor(0xFF888888); // Колір кнопки
        paint.setTextSize(30); // Розмір тексту
        paint.setAntiAlias(true); // Згладжування
    }

    // Малювання кнопки
    public void draw(Canvas canvas) {
        // Малюємо прямокутник кнопки
        canvas.drawRect(x, y, x + width, y + height, paint);

        // Малюємо текст на кнопці
        canvas.drawText(text, x + 10, y + height / 2 + 10, paint);
    }

    // Перевірка, чи торкнулися кнопки
    public boolean isTouched(float touchX, float touchY) {
        return touchX >= x && touchX <= x + width && touchY >= y && touchY <= y + height;
    }

    // Геттери для координат
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}