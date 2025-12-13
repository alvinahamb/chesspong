package component;

public class Ball {
    int x;
    int y;
    int radius;
    int speed;
    int dx;
    int dy;

    public Ball(int x, int y, int radius, int speed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
        this.dx = speed;
        this.dy = speed;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRadius() { return radius; }
    public int getSpeed() { return speed; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setRadius(int radius) { this.radius = radius; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setDx(int dx) { this.dx = dx; }
    public void setDy(int dy) { this.dy = dy; }

    public void move() {
        x += dx;
        y += dy;
    }

    public void bounceHorizontally() {
        dx = -dx;
    }

    public void bounceVertically() {
        dy = -dy;
    }

    public void clampWithin(int width, int height) {
        if (x - radius < 0) {
            x = radius;
            bounceHorizontally();
        } else if (x + radius > width) {
            x = width - radius;
            bounceHorizontally();
        }

        if (y - radius < 0) {
            y = radius;
            bounceVertically();
        } else if (y + radius > height) {
            y = height - radius;
            bounceVertically();
        }
    }

    public void reset(int newX, int newY, int direction) {
        x = newX;
        y = newY;
        dx = Math.abs(speed) * (direction >= 0 ? 1 : -1);
        dy = speed;
    }
}