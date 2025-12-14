CREATE DATABASE chesspong;
\c chesspong;

CREATE TABLE config (
    id SERIAL PRIMARY KEY,
    roi INT NOT NULL,
    dame INT NOT NULL,
    tour INT NOT NULL,
    fou INT NOT NULL,
    cavalier INT NOT NULL,
    pion INT NOT NULL,
    ball_degats INT NOT NULL,
    piece_number INT NOT NULL
);

INSERT INTO config (roi, dame, tour, fou, cavalier, pion, ball_degats, piece_number) VALUES (10, 9, 5, 3, 3, 1, 1, 16);
