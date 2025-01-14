#! /bin/bash

# This script is used to build and run the application in a docker container locally

if [[ "$1" == "build" ]];
 then 
    mvn clean install
 else 
    echo "Skipping build..."
fi
cp -f fred-main-webapp/target/*.jar .
cp -f fred-main-webapp/target/*.war .
docker rm -f fred1

docker build . -f Dockerfile -t fred --no-cache
docker run -d --name fred1 -e PG_PASS=pass -p 9992:8080 fred

echo ""
echo "Process complete. container can be accessed on http://127.0.0.1:9992/fred/"
echo ""

