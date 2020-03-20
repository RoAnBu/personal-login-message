#!/bin/bash

if [[ $(sudo docker ps -a | grep spigot-server) ]]
then
  sudo docker start -ia spigot-server
else
  sudo docker run -it -p 5005:5005 -p 25565:25565 --name spigot-server --user 1000:1000 -v "$(pwd)/plugins":/spigot/plugins spigot-server
fi