#!/bin/sh

cp -a /spigot-config/. .

java -Xms1G -Xmx1G -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar spigot.jar
