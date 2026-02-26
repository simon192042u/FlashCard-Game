package com.example.sensorquiz;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView questionText, cardTitle;
    private Button btnRichtig, btnFalsch, btnRepeat;
    private View layoutButtons;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private ArrayList<QuizFrage> hauptListe;
    private ArrayList<QuizFrage> aktuelleListe;
    private int index = 0;
    private boolean zeigtAntwort = false;
    private boolean kannUeberspringen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionText = findViewById(R.id.questionText);
        cardTitle = findViewById(R.id.cardTitle);
        btnRichtig = findViewById(R.id.btnRichtig);
        btnFalsch = findViewById(R.id.btnFalsch);
        btnRepeat = findViewById(R.id.btnRepeat);
        layoutButtons = findViewById(R.id.layoutButtons);

        // Die 20 Fragen für dein Quizlet
        hauptListe = new ArrayList<>();
        hauptListe.add(new QuizFrage("Hauptstadt von Frankreich?", "Paris"));
        hauptListe.add(new QuizFrage("Was ist 12 mal 12?", "144"));
        hauptListe.add(new QuizFrage("Größter Ozean der Erde?", "Pazifik"));
        hauptListe.add(new QuizFrage("Chemische Formel für Wasser?", "H2O"));
        hauptListe.add(new QuizFrage("Wie viele Zähne hat ein Erwachsener?", "32"));
        hauptListe.add(new QuizFrage("Wer malte die Mona Lisa?", "Leonardo da Vinci"));
        hauptListe.add(new QuizFrage("Währung in Japan?", "Yen"));
        hauptListe.add(new QuizFrage("Planet am nächsten zur Sonne?", "Merkur"));
        hauptListe.add(new QuizFrage("Startjahr des 1. Weltkriegs?", "1914"));
        hauptListe.add(new QuizFrage("Wie viele Kontinente gibt es?", "7"));
        hauptListe.add(new QuizFrage("Härtestes natürliches Material?", "Diamant"));
        hauptListe.add(new QuizFrage("Organ, das Blut pumpt?", "Herz"));
        hauptListe.add(new QuizFrage("Wie viele Bundesländer hat DE?", "16"));
        hauptListe.add(new QuizFrage("Wer erfand das Telefon?", "Alexander Graham Bell"));
        hauptListe.add(new QuizFrage("Dreieck mit 3 gleichen Seiten?", "Gleichseitig"));
        hauptListe.add(new QuizFrage("Metall, das bei Raumtemperatur flüssig ist?", "Quecksilber"));
        hauptListe.add(new QuizFrage("In welcher Stadt steht der Eiffelturm?", "Paris"));
        hauptListe.add(new QuizFrage("Sekunden in einer Stunde?", "3600"));
        hauptListe.add(new QuizFrage("Farbe eines Smaragds?", "Grün"));
        hauptListe.add(new QuizFrage("Welches Land hat die meisten Einwohner?", "Indien"));

        aktuelleListe = new ArrayList<>(hauptListe);
        updateAnzeige();

        // Karte tippen = Umdrehen
        findViewById(R.id.quizCard).setOnClickListener(v -> {
            zeigtAntwort = !zeigtAntwort;
            updateAnzeige();
        });

        // "Gewusst" markiert die Frage als erledigt
        btnRichtig.setOnClickListener(v -> {
            aktuelleListe.get(index).wussteIch = true;
            naechsteKarte();
        });

        // "Nicht gewusst" lässt sie für die Wiederholung offen
        btnFalsch.setOnClickListener(v -> {
            aktuelleListe.get(index).wussteIch = false;
            naechsteKarte();
        });

        btnRepeat.setOnClickListener(v -> starteWiederholung());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private void updateAnzeige() {
        if (aktuelleListe.isEmpty()) {
            zeigeAbschluss();
            return;
        }

        QuizFrage q = aktuelleListe.get(index);
        if (zeigtAntwort) {
            cardTitle.setText("ANTWORT");
            cardTitle.setTextColor(Color.parseColor("#E91E63"));
            questionText.setText(q.antwort);
        } else {
            cardTitle.setText("FRAGE (" + (index + 1) + "/" + aktuelleListe.size() + ")");
            cardTitle.setTextColor(Color.parseColor("#3F51B5"));
            questionText.setText(q.frage);
        }
    }

    private void naechsteKarte() {
        if (index < aktuelleListe.size() - 1) {
            index++;
            zeigtAntwort = false;
            updateAnzeige();
        } else {
            zeigeAbschluss();
        }
    }

    private void zeigeAbschluss() {
        questionText.setText("Runde beendet!\nWas möchtest du tun?");
        layoutButtons.setVisibility(View.GONE);
        btnRepeat.setVisibility(View.VISIBLE);
        cardTitle.setText("FERTIG");
    }

    private void starteWiederholung() {
        ArrayList<QuizFrage> falsche = new ArrayList<>();
        for (QuizFrage q : hauptListe) {
            if (!q.wussteIch) falsche.add(q);
        }

        if (falsche.isEmpty()) {
            Toast.makeText(this, "Alles gewusst! Starte komplett neu.", Toast.LENGTH_SHORT).show();
            for(QuizFrage q : hauptListe) q.wussteIch = false;
            aktuelleListe = new ArrayList<>(hauptListe);
        } else {
            aktuelleListe = falsche;
            Toast.makeText(this, falsche.size() + " Fragen übrig.", Toast.LENGTH_SHORT).show();
        }

        index = 0;
        zeigtAntwort = false;
        layoutButtons.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.GONE);
        updateAnzeige();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        // Links kippen = Überspringen (ohne Wertung)
        if (x > 7.0 && kannUeberspringen && !aktuelleListe.isEmpty()) {
            naechsteKarte();
            kannUeberspringen = false;
        }
        if (Math.abs(x) < 2.0) kannUeberspringen = true;
    }

    @Override protected void onResume() { super.onResume(); sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI); }
    @Override protected void onPause() { super.onPause(); sensorManager.unregisterListener(this); }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}