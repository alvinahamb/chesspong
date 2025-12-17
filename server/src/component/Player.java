package component;

import java.util.List;

public class Player {
    int id;
    String nom;
    List<Piece> pieces;
    Paddle paddle;

    public Player(int id, String nom, List<Piece> pieces, Paddle paddle) {
        this.id = id;
        this.nom = nom;
        this.pieces = pieces;
        this.paddle = paddle;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public List<Piece> getPieces() { return pieces; }
    public Paddle getPaddle() { return paddle; }
    
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPieces(List<Piece> pieces) { this.pieces = pieces; }
    public void setPaddle(Paddle paddle) { this.paddle = paddle; }

    // public void addPiece(Piece piece) {
    //     if (piece != null) {
    //         pieces.add(piece);
    //     }
    // }

    // public boolean removePiece(Piece piece) {
    //     if (piece == null) {
    //         return false;
    //     }
    //     return pieces.remove(piece);
    // }

    // public int remainingPieces() {
    //     int count = 0;
    //     for (Piece piece : pieces) {
    //         if (piece.isAlive()) {
    //             count++;
    //         }
    //     }
    //     return count;
    // }
}
