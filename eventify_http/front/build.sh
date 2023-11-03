#!/bin/bash

# Check if two arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <IP> <PORT>"
    exit 1
fi

# Assign the IP and PORT values from the arguments
new_ip="$1"
new_port="$2"

# Define the path to the TypeScript file
typescript_file="./src/app/services/path.ts"
newTypescript_file="./src/app/services/ApiPath.ts"

# Use awk to replace IP and PORT values in the TypeScript file
awk -v new_ip="$new_ip" -v new_port="$new_port" '{ gsub(/<IP>:<PORT>/, new_ip ":" new_port) } 1' "$typescript_file" > "$newTypescript_file"
rm -rf node_modules package-lock.json
npm i
ng build
cp -r cert.pem key.pem dockerfile dockerInit.sh ./dist/Eventify
mkdir ./dist/Eventify/server/
cp index.js ./dist/Eventify/server/
rm -rf node_modules package-lock.json
