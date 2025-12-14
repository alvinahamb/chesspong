@echo off
cd server
cd ejb
call mvn compile -q
cd ..
if not exist out mkdir out
@REM Compile with classpath including EJB classes and Jakarta EE
set EJB_CLASSES=%~dp0server\ejb\target\classes
javac -cp "%EJB_CLASSES%;%USERPROFILE%\.m2\repository\jakarta\platform\jakarta.jakartaee-api\10.0.0\jakarta.jakartaee-api-10.0.0.jar;%USERPROFILE%\.m2\repository\org\wildfly\wildfly-client-all\27.0.0.Final\wildfly-client-all-27.0.0.Final.jar" -d out src\network\*.java src\component\*.java
java -cp "out;%EJB_CLASSES%;%USERPROFILE%\.m2\repository\jakarta\platform\jakarta.jakartaee-api\10.0.0\jakarta.jakartaee-api-10.0.0.jar;%USERPROFILE%\.m2\repository\org\wildfly\wildfly-client-all\27.0.0.Final\wildfly-client-all-27.0.0.Final.jar" network.App