package game;

import java.awt.*;

public class Ball {
    private Surface surface;
    private int size;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private Color color;

    // Constructor de la bola
    public Ball(Surface surface, double x, double y, int size, double direction, double speed, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.vx = Math.cos(direction) * speed;
        this.vy = Math.sin(direction) * speed;
        this.surface = surface;
        this.color = color;
    }

    // Dibuja la bola
    public void paint(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) x, (int) y, size, size);
    }

    // Mueve la bola y gestiona rebotes
    public void move(long lapse, int width, int height) {
        x += (lapse * vx) / 1000000000L;
        y += (lapse * vy) / 1000000000L;

        if (x + size >= width) {
            x = 2 * width - x - 2 * size;
            vx *= -1;
        } else if (x < 0) {
            x = -x;
            vx *= -1;
        }

        if (y + size >= height) {
            y = 2 * height - y - 2 * size;
            vy *= -1;
        } else if (y < 0) {
            y = -y;
            vy *= -1;
        }
    }

    // Getters y setters
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
}