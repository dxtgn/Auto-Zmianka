# Auto-Zmianka 1.1 — Fabric 1.21.6

Projekt źródłowy moda klientowego dla Minecraft Java 1.21.6.

## Funkcje
- `R` — START / STOP; STOP resetuje stan.
- `Right Shift` — panel ustawień.
- Suwak progu 1–100%.
- Suwak odstępu między kliknięciami 200–2000 ms.
- Konfiguracja zapisywana w `config/autozmianka.properties`.
- Wyszukiwanie `BLUE_DYE` w otwartym GUI i odczyt procentu z nazwy/lore.

## Budowanie
Wymagane: JDK 21 oraz Gradle 8.12 lub nowszy.
W katalogu projektu uruchom:
```bash
gradle build
```
Plik JAR powinien znaleźć się w `build/libs/`.

## Ważne — prototyp do dopasowania
- Zakłada, że przycisk losowania jest przedmiotem `BLUE_DYE`.
- Procent musi znajdować się w nazwie lub komponencie lore przedmiotu przesyłanym klientowi.
- Kliknięcie wykonywane jest standardowym kliknięciem slotu Minecrafta. Niektóre serwery mogą wymagać innej akcji.
- Mod rozpoznaje nowy wynik, gdy tekst barwnika zmieni się po losowaniu. Jeśli serwer nie aktualizuje danych przedmiotu, trzeba dopasować odczyt do konkretnego GUI.
- Sprawdź regulamin serwera — automatyzacja może być niedozwolona.

## Zależności
Minecraft 1.21.6, Fabric Loader 0.16.14, Fabric API 0.128.2+1.21.6,
Yarn 1.21.6+build.1, Java 21.
