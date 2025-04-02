# Match and Scoreboard Application

Ovaj projekt simulira upravljanje utakmicama i rezultatskom tablom koristeći Java i konkurentne mehanizme (niti i zaključavanje).

## Struktura Projekta

### Klase

- **Match**:
  - Služi za praćenje pojedinačnih utakmica, uključujući timove, rezultate i status utakmice.
  - Pruža mogućnosti za postavljanje i ažuriranje rezultata, kao i započinjanje i završavanje utakmica.

- **Scoreboard**:
  - Služi kao "tabla" koja prati sve utakmice.
  - Omogućava dodavanje novih utakmica, ažuriranje rezultata i prikazivanje sažetaka svih utakmica.

### Glavne funkcionalnosti

- **Utakmica**:
  - Započnite utakmicu s početnim rezultatima.
  - Ažurirajte rezultate utakmice tokom njenog trajanja.
  - Zatvorite utakmicu i prikažite završni rezultat.

- **Tablica rezultata**:
  - Dodajte utakmice na tablu.
  - Ažurirajte rezultate na temelju ID-a utakmice.
  - Prikazivanje sažetka svih utakmica sa rezultatima i informacijama o timu.

## Korištene tehnologije

- Java 11 ili noviji
- Maven (ili drugi alat po izboru za upravljanje zavisnostima)
- ExecutorService i Lock za konkurentno izvođenje


