@echo off
cd client
mvn compile
mvn exec:java -Dexec.mainClass="ui.Main" -q

cd c:\Users\erant\Documents\S5\Archlog\chesspong-java\client ; mvn clean install -DskipTests
cd c:\Users\erant\Documents\S5\Archlog\chesspong-java ; .\deploy_client.bat