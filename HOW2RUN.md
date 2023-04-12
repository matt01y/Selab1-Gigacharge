# How 2 Run dis demo:

## Backend setup:
- open de map firebaseadmin
- maak de map env aan
- ga naar de firebase console, settings -> project settings -> service accounts -> Generate new private key
- plak de file die je download in de env map
- maak nu terug in firebaseadmin een .env file aan
- plak daarin: SERVICE_ACCOUNT="./env/gigacharge-961d6-firebase-adminsdk-8jhn6-6970d77010.json"
- pas de filename aan naar de naam van u private key en sla op
- voer in een terminal "node authmanager.js" uit, nu draait de authenticatie server.

## App setup:
- google-services.json staat normaal al in androidapp/app.
- Doe zeker nog eens een Gradle sync.
- Daarna kan je gewoon op play drukken om uit te voeren op emulator/eigen device
- als er problemen zijn, stuur dan eens de logcat output door naar mij
