# Dokumentation: Entwicklung der „Sensor-Quiz“ App

## 1. Projektübersicht

Ziel des heutigen Tages war die Entwicklung einer interaktiven Lern-App (ähnlich wie Quizlet), die moderne Smartphone-Hardware nutzt.  
Die App ermöglicht es, digitale Karteikarten durch Antippen umzudrehen und durch physische Gesten (Kippen des Geräts) zu navigieren.

---

## 2. Implementierte Funktionen

- **Karteikarten-System:** Anzeige von 20 vordefinierten Fragen aus verschiedenen Themenbereichen  
- **Flip-Logik:** Durch Klicken auf die Karte wird zwischen Frage und Antwort gewechselt (inklusive visueller Rotation)  
- **Gestensteuerung (Sensorik):** Integration des Beschleunigungssensors, um die nächste Karte aufzurufen, wenn das Handy nach links gekippt wird  
- **Selbstbewertung:** Buttons für „Gewusst“ und „Nicht gewusst“ zur Verfolgung des Lernfortschritts  
- **Spaced Repetition (Wiederholung):** Ein Algorithmus, der am Ende einer Runde alle falsch beantworteten Fragen filtert und eine gezielte Wiederholungssitzung ermöglicht  

---

## 3. Technische Umsetzung

### A. Die Datenstruktur (`QuizFrage.java`)

Wir haben eine eigene Klasse erstellt, um die Daten einer Karte zu kapseln.  
Dies ist ein Beispiel für Objektorientierte Programmierung (OOP).

Sie speichert:

- `String frage`
- `String antwort`
- `boolean wussteIch` → Ein Status-Flag, das entscheidet, ob die Frage in der Wiederholungsrunde erscheint

---

### B. Die Sensor-Logik (`MainActivity.java`)

Die App implementiert das `SensorEventListener`-Interface.

- **Sensor:** `TYPE_ACCELEROMETER` (Beschleunigungsmesser)
- **Logik:** Wir überwachen die X-Achse. Ein Wert von `X > 7.0` signalisiert eine starke Neigung nach links.
- **Debouncing:** Eine boolesche Variable (`kannUeberspringen`) verhindert, dass bei einer einzigen Bewegung mehrere Karten gleichzeitig übersprungen werden

---

### C. Benutzeroberfläche (`activity_main.xml`)

Das Design wurde mit einem `LinearLayout` und einer `CardView` erstellt.

- **CardView:** Sorgt für den typischen „Karten-Look“ mit abgerundeten Ecken und Schatten
- **Sichtbarkeitssteuerung:** Der Wiederholungs-Button wird dynamisch ein- und ausgeblendet (`View.GONE` vs. `View.VISIBLE`), je nachdem, ob die Runde beendet ist

---

## 4. Durchgeführte Tests

- **Emulator-Tests:** Verwendung der „Virtual Sensors“ in Android Studio, um die Neigung der Y-Achse zu simulieren und die Reaktion der X-Achse zu prüfen  
- **Logik-Tests:** Überprüfung, ob der Filter-Mechanismus korrekt arbeitet und nach der Runde tatsächlich nur die falsch markierten Fragen in der `aktuelleListe` landen  

---

## 5. Gelöste Probleme (Troubleshooting)

- **Sprachwechsel:** Korrektur von einem irrtümlich erstellten Kotlin-Projekt zu einem Java-Projekt, damit der Code kompatibel mit der klassischen Android-Entwicklung ist  
- **Ressourcen-Fehler:** Behebung von *resource not found* Fehlern durch korrektes Anlegen von Layout-IDs und Menü-Strukturen  

---

## Ergänzung: Technischer Tiefgang & Lebenszyklus

### 6. Management der System-Ressourcen

Ein kritischer Punkt bei der Nutzung von Hardware-Sensoren ist der Android Activity Lifecycle.  
Wir haben die Sensor-Registrierung bewusst nicht nur in `onCreate` geschrieben, sondern über die Lebenszyklus-Methoden gesteuert.

- **onResume():** Hier wird der SensorListener registriert. Das stellt sicher, dass die App sofort auf Bewegungen reagiert, sobald sie für den Nutzer sichtbar wird.
- **onPause():** Hier wird `unregisterListener()` aufgerufen. Dies ist essenziell, um den Akku zu schonen. Würden wir den Sensor hier nicht abmelden, würde das Handy auch bei ausgeschaltetem Bildschirm ständig die Bewegung berechnen.
- **onAccuracyChanged():** Diese Methode wurde implementiert (als Interface-Pflicht), blieb aber leer, da für ein Quiz die absolute Präzision des Beschleunigungsmessers vernachlässigbar ist.

---

### 7. Verwendete UI-Komponenten & Logik-Bausteine

- **ArrayList<QuizFrage>:** Dient als dynamischer Speicher. Im Gegensatz zu einem einfachen Array können wir hier zur Laufzeit Fragen filtern und neue Listen (wie die Wiederholungsliste) erstellen.
- **View.GONE vs. View.VISIBLE:** Diese Status-Flags haben wir genutzt, um das Interface „sauber“ zu halten. Der Wiederholungs-Button existiert zwar technisch immer im Layout, nimmt aber erst Platz ein und wird sichtbar, wenn die `aktuelleListe` abgearbeitet ist.
- **Animation Framework:** Mit `.animate().rotationY(...)` haben wir einen haptischen Effekt erzeugt. Die Rotation signalisiert dem Gehirn des Nutzers das „Umdrehen“ einer physischen Karte, was die User Experience (UX) massiv verbessert.

---

### 8. Mathematische Logik der Gestensteuerung

Wir haben einen Schwellenwert (Threshold) für die X-Achse festgelegt:

1. **Aktivierung:** `X > 7.0` (Starke Neigung nach links)  
2. **Reset:** `|X| < 2.0` (Gerätehaltung nahezu vertikal)  

Diese **Hysterese** (der Abstand zwischen 7.0 und 2.0) verhindert das sogenannte „Bouncing“.  
Ohne diesen Puffer würde eine einzige zittrige Bewegung dazu führen, dass die App 5 Fragen gleichzeitig überspringt.
