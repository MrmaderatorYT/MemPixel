package com.ccs.mempixel.joystick;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class Joystick {
    private PointF center; // Центр джойстика
    private PointF handle; // Позиція ручки джойстика
    private float radius;  // Радіус джойстика
    private float handleRadius; // Радіус ручки джойстика
    private Paint paint;   // Об'єкт для малювання

    public Joystick(float centerX, float centerY, float radius, float handleRadius) {
        this.center = new PointF(centerX, centerY);
        this.handle = new PointF(centerX, centerY);
        this.radius = radius;
        this.handleRadius = handleRadius;

        paint = new Paint();
        paint.setColor(0xFF888888); // Колір джойстика
        paint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        // Малюємо основу джойстика
        canvas.drawCircle(center.x, center.y, radius, paint);

        // Малюємо ручку джойстика
        canvas.drawCircle(handle.x, handle.y, handleRadius, paint);
    }

    public void updateHandle(float touchX, float touchY) {
        // Обмежуємо ручку джойстика в межах основи
        float dx = touchX - center.x;
        float dy = touchY - center.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > radius) {
            dx = dx * radius / distance;
            dy = dy * radius / distance;
        }

        handle.set(center.x + dx, center.y + dy);
    }

    public void resetHandle() {
        handle.set(center.x, center.y);
    }

    public PointF getDirection() {
        float dx = handle.x - center.x;
        float dy = handle.y - center.y;
        return new PointF(dx / radius, dy / radius);
    }
}