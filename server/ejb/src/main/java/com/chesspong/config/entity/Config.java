package com.chesspong.config.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "config")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int roi;

    @Column(nullable = false)
    private int dame;

    @Column(nullable = false)
    private int tour;

    @Column(nullable = false)
    private int fou;

    @Column(nullable = false)
    private int cavalier;

    @Column(nullable = false)
    private int pion;

    @Column(name = "ball_degats", nullable = false)
    private int ballDegats;

    @Column(name = "pouvoir_ball", nullable = false)
    private int pouvoirBall;

    @Column(name = "atteinte_pouvoir", nullable = false)
    private int atteintePouvoir;

    @Column(name = "piece_number", nullable = false)
    private int pieceNumber;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoi() {
        return roi;
    }

    public void setRoi(int roi) {
        this.roi = roi;
    }

    public int getDame() {
        return dame;
    }

    public void setDame(int dame) {
        this.dame = dame;
    }

    public int getTour() {
        return tour;
    }

    public void setTour(int tour) {
        this.tour = tour;
    }

    public int getFou() {
        return fou;
    }

    public void setFou(int fou) {
        this.fou = fou;
    }

    public int getCavalier() {
        return cavalier;
    }

    public void setCavalier(int cavalier) {
        this.cavalier = cavalier;
    }

    public int getPion() {
        return pion;
    }

    public void setPion(int pion) {
        this.pion = pion;
    }

    public int getBallDegats() {
        return ballDegats;
    }

    public void setBallDegats(int ballDegats) {
        this.ballDegats = ballDegats;
    }

    public int getPouvoirBall() {
        return pouvoirBall;
    }

    public void setPouvoirBall(int pouvoirBall) {
        this.pouvoirBall = pouvoirBall;
    }

    public int getAtteintePouvoir() {
        return atteintePouvoir;
    }

    public void setAtteintePouvoir(int atteintePouvoir) {
        this.atteintePouvoir = atteintePouvoir;
    }

    public int getPieceNumber() {
        return pieceNumber;
    }

    public void setPieceNumber(int pieceNumber) {
        this.pieceNumber = pieceNumber;
    }
}