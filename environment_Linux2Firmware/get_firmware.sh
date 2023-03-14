#!/bin/bash

get_firmware="http://"$env_user$":"$env_pass"@"$env_url

echo  "###DOWNLOAD FIRMWARE### >> "$get_firmware
git clone $get_firmware
cd ..
cd /$env_device
. /usr/local/oecore-x86_64/environment-setup-armv7at2hf-neon-angstrom-linux-gnueabi && make AM335x=ON
./release.sh
rm *.a -f
