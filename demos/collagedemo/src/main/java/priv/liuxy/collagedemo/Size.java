package com.liuxy.tct;

/**
 * Created by xuyang.liu on 17-6-29.
 */

public class Size {
    int width, height;

    public Size() {
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isSquare() {
        return width == height;
    }

    public boolean isEmpty() {
        return width == 0 || height == 0;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
