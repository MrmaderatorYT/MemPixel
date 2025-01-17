package com.ccs.mempixel.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.ccs.mempixel.R;

public class TileMap {
    private int[][] map; // Масив, що описує карту
    private Bitmap[] tileImages; // Зображення для кожного типу тайла
    private int tileSize; // Розмір одного тайла (наприклад, 32x32 пікселів)

    public TileMap(Context context, int[][] map, int tileSize) {
        this.map = map;
        this.tileSize = tileSize;

        // Завантаження зображень для тайлів
        tileImages = new Bitmap[2]; // 0 - підлога, 1 - стіна
        tileImages[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor1); // Підлога
        tileImages[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall1); // Стіна

        // Масштабування зображень до розміру тайла
        for (int i = 0; i < tileImages.length; i++) {
            tileImages[i] = Bitmap.createScaledBitmap(tileImages[i], tileSize, tileSize, false);
        }
    }

    // Малювання видимої частини карти
    public void draw(Canvas canvas, int cameraX, int cameraY) {
        int screenWidth = canvas.getWidth();
        int screenHeight = canvas.getHeight();

        // Перевірка, чи масив map ініціалізований
        if (map == null || map.length == 0 || map[0].length == 0) {
            return; // Якщо масив порожній, нічого не малюємо
        }

        // Визначення початкових та кінцевих координат для малювання
        int startX = Math.max(0, cameraX / tileSize);
        int startY = Math.max(0, cameraY / tileSize);
        int endX = Math.min(map[0].length, (cameraX + screenWidth) / tileSize + 1);
        int endY = Math.min(map.length, (cameraY + screenHeight) / tileSize + 1);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int tileType = map[y][x];
                if (tileType >= 0 && tileType < tileImages.length) {
                    // Малюємо тайл на основі його типу
                    canvas.drawBitmap(
                            tileImages[tileType],
                            x * tileSize - cameraX,
                            y * tileSize - cameraY,
                            null
                    );
                }
            }
        }
    }

    // Перевірка, чи є тайл стіною
    public boolean isWall(int x, int y) {
        if (y >= 0 && y < map.length && x >= 0 && x < map[y].length) {
            return map[y][x] == 1; // 1 - стіна
        }
        return true; // Якщо координати поза межами карти, вважаємо це стіною
    }

    // Отримання розмірів карти в пікселях
    public int getMapWidth() {
        return map[0].length * tileSize;
    }

    public int getMapHeight() {
        return map.length * tileSize;
    }

    // Отримання розміру тайла
    public int getTileSize() {
        return tileSize;
    }
}