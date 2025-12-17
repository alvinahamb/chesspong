package component;

public class Piece {
    int x, y;
    String type;
    String color;
    int lives;
    int actualLives;

    public Piece(int x, int y, String type, String color, int lives) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.color = color;
        this.lives = lives;
        this.actualLives = lives;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getType() { return type; }
    public String getColor() { return color; }
    public int getLives() { return lives; }
    public int getActualLives() { return actualLives; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setType(String type) { this.type = type; }
    public void setColor(String color) { this.color = color; }
    public void setLives(int lives) { this.lives = lives; }
    public void setActualLives(int actualLives) { this.actualLives = actualLives; }

    // public boolean loseLife() {
    //     if (actualLives <= 0) {
    //         return false;
    //     }
    //     actualLives -= 1;
    //     return actualLives > 0;
    // }

    // public boolean isAlive() {
    //     return actualLives > 0;
    // }

    // public void resetLives() {
    //     actualLives = lives;
    // }
}
