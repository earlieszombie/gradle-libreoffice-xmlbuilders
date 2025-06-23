#!/bin/sh
Color_Off='\033[0m'
Yellow='\033[0;33m'
Blue='\033[0;33m'

printf "\n${Yellow}Publishing changes to local maven repo... ${Blue}(~/.m2/repository/)${Color_Off}\n"
./gradlew publishToMavenLocal
printf "\n${Yellow}Launching unit tests...${Color_Off}\n"
./gradlew test
printf "\n${Yellow}Launching functional tests...${Color_Off}\n"
./gradlew functionalTest
