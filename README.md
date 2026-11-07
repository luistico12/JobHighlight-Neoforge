# Job Highlight (NeoForge 1.21.1)

Shift + Rechtsklick auf einen Villager:

- **Villager hat einen Arbeitsblock** -> dieser Block wird durch Wände hindurch markiert (orange, 15 Sekunden).
  In der Aktionsleiste steht Blockname und Position.
- **Villager hat keinen Arbeitsblock** (berufslos, Nitwit, Block verloren) -> alle Blöcke der Sorte, die du in der
  Hand hältst, werden im Umkreis von 32 Blöcken markiert (cyan). So findest du z. B. freie Lecterns oder Composter.

Der normale Handel wird bei Shift + Rechtsklick unterdrückt.

## Wichtig: Server und Client

Den Arbeitsblock kennt nur der Server (er steckt im Villager-"Brain"). Die Mod muss deshalb dort installiert sein,
wo die Welt läuft:

- Einzelspieler / "Im LAN öffnen": nur Client-Installation nötig (integrierter Server).
- Multiplayer: Mod auf Server **und** Client.

## Bauen

Voraussetzung: JDK 21.

    ./gradlew build       # build/libs/jobhighlight-1.0.0.jar
    ./gradlew runClient   # Testen im Entwicklungs-Client

GitHub Actions baut die JAR bei jedem Push (Actions-Tab -> Artifacts). Ein Tag wie `v1.0.0` erzeugt ein Release.

## Lizenz

MIT, siehe `LICENSE`.
