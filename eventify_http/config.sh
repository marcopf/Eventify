#!/bin/bash

addgroup --gid 1001 eventify
docker volume rm event-vol
docker volume rm user-vol
docker volume rm log-vol
docker volume create event-vol
docker volume create user-vol
docker volume create log-vol
chown :1001 /var/lib/docker/volumes/event-vol/_data
chmod 775 /var/lib/docker/volumes/event-vol/_data
chmod g+s /var/lib/docker/volumes/event-vol/_data
chown :1001 /var/lib/docker/volumes/user-vol/_data
chmod 775 /var/lib/docker/volumes/user-vol/_data
chmod g+s /var/lib/docker/volumes/user-vol/_data
chown :1001 /var/lib/docker/volumes/log-vol/_data
chmod 775 /var/lib/docker/volumes/log-vol/_data
chmod g+s /var/lib/docker/volumes/log-vol/_data
if [ $# == 1 ] && [ $1 == "demo" ]
then
	cp ./demo/users/* /var/lib/docker/volumes/user-vol/_data/
	cp ./demo/events/* /var/lib/docker/volumes/event-vol/_data/
fi
