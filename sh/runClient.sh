#!/bin/sh
clear
java -Dusername=Brian -DclientPort=15000 -DserverHostname=localhost -DserverPort=15672 -Ddebug=true -jar $(pwd)/../target/Messenger-1.0-SNAPSHOT-jar-with-dependencies.jar
