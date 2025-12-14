package com.chesspong.config.dto;

import java.io.Serializable;

public class ConfigDTO implements Serializable {

    private int id;
    private int roi;
    private int dame;
    private int tour;
    private int fou;
    private int cavalier;
    private int pion;
    private int ballDegats;
    private int pieceNumber;

    public ConfigDTO() {
    }

    public ConfigDTO(int id, int roi, int dame, int tour, int fou, int cavalier, int pion, int ballDegats, int pieceNumber) {
        this.id = id;
        this.roi = roi;
        this.dame = dame;
        this.tour = tour;
        this.fou = fou;
        this.cavalier = cavalier;
        this.pion = pion;
        this.ballDegats = ballDegats;
        this.pieceNumber = pieceNumber;
    }

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

    public int getPieceNumber() {
        return pieceNumber;
    }

    public void setPieceNumber(int pieceNumber) {
        this.pieceNumber = pieceNumber;
    }
}