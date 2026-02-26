package com.example.sensorquiz;

public class QuizFrage {
    public String frage;
    public String antwort;
    public boolean wussteIch = false; // Speichert, ob die Frage abgehakt ist

    public QuizFrage(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }
}