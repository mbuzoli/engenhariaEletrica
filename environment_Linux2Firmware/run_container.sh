#!/bin/bash

sudo docker run -e env_url=$1 -e env_device=$2 -ti --name Container_MRB MRB_comp
sudo docker cp Container_MRB:/$2/output .
sudo docker system prune
cd output
sudo rm *.a -f





