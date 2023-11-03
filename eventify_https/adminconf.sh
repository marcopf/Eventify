#!/bin/bash

if [ $# -eq 6 ]
then
	rm -rf BOOT-INF
	rm -f admin.JSON
	touch admin.JSON
	echo "{" > admin.JSON
	printf "\t\"username\": \"%s\",\n" $1 >> admin.JSON 
	printf "\t\"firstName\": \"%s\",\n" $2 >> admin.JSON 
	printf "\t\"lastName\": \"%s\",\n" $3 >> admin.JSON 
	printf "\t\"email\": \"%s\",\n" $4 >> admin.JSON 
	printf "\t\"dateOfBirth\": \"%s\",\n" $5 >> admin.JSON 
	printf "\t\"password\": \"%s\"\n" $(echo -n $6 | sha256sum | cut -c -64) >> admin.JSON
	echo "}" >> admin.JSON
	chmod 444 admin.JSON
	mkdir BOOT-INF
	mkdir BOOT-INF/classes
	cp admin.JSON BOOT-INF/classes
	cp ssl/springboot.jks BOOT-INF/classes
	cp ssl/app.key BOOT-INF/classes
	cp ssl/app.pub BOOT-INF/classes
	jar -uf Eventify/eventifyBack-1.0.0.jar BOOT-INF/classes/*
	rm -rf BOOT-INF
	rm -f admin.JSON
else
	echo "Bad usage: bash adminconf.sh <username> <first name> <last name> <email> <date of birth> <password>"
fi
