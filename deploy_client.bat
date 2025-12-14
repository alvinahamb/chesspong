@echo off
cd client
if not exist out mkdir out
REM Build with Maven to ensure dependencies are available
call mvn clean compile -q
REM Compile Java files with classpath including Jakarta EJB and dependencies
javac -cp "src;target\classes;%USERPROFILE%\.m2\repository\jakarta\platform\jakarta.jakartaee-api\10.0.0\jakarta.jakartaee-api-10.0.0.jar;%USERPROFILE%\.m2\repository\org\wildfly\wildfly-client-all\27.0.0.Final\wildfly-client-all-27.0.0.Final.jar;%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.6.0\postgresql-42.6.0.jar" -d out src\network\*.java src\ui\*.java src\component\*.java com\chesspong\config\ejb\*.java com\chesspong\config\dto\*.java
java -cp "out;%USERPROFILE%\.m2\repository\jakarta\platform\jakarta.jakartaee-api\10.0.0\jakarta.jakartaee-api-10.0.0.jar;%USERPROFILE%\.m2\repository\org\wildfly\wildfly-client-all\27.0.0.Final\wildfly-client-all-27.0.0.Final.jar;%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.6.0\postgresql-42.6.0.jar" ui.Main