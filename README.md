# chesspong
chess pong java

## Déploiement rapide

### Client
Lancez `deploy_client.bat` depuis la racine du projet pour compiler et exécuter le client.

### Serveur
Lancez `deploy_server.bat` depuis la racine du projet pour compiler et exécuter le serveur.

avant pour le server
@echo off
cd server
if not exist out mkdir out
@REM mvn compile
javac -d out src\network\*.java src\component\*.java
java -cp out network.App
