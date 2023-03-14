FROM ubuntu:latest
LABEL maintainer= name=value

WORKDIR /

RUN apt-get update && apt-get upgrade -y
RUN apt-get install git python3 make unzip gettext libevent-dev libsnmp-dev gcc g++ -y
ARG USER_git
ARG PASS_git
ARG COMP_url
ARG get_firmware

ENV env_user $USER_git
ENV env_pass $PASS_git
ENV env_url null
ENV env_device null


RUN git clone  http://$USER_git:$PASS_git@$COMP_url
WORKDIR /oecore-x86_64
RUN mv oecore-x86_64.zip /usr/local
WORKDIR /oecore-x86_64
RUN rm -rf .git
WORKDIR /
RUN rmdir /oecore-x86_64
WORKDIR /usr/local
RUN unzip oecore-x86_64.zip
RUN rm oecore-x86_64.zip
WORKDIR /

RUN git clone  http://$USER_git:$PASS_git@$get_firmware
WORKDIR /get_firmware
RUN chmod +x get_firmware.sh
RUN mv get_firmware.sh /
WORKDIR /

CMD ./get_firmware.sh $env_user $env_pass $env_url $env_device


#CMD git clone  http://$USER_git:$PASS_git@$FIRMWARE_url
#WORKDIR /$FIRMWARE_name
#CMD . /usr/local/oecore-x86_64/environment-setup-armv7at2hf-neon-angstrom-linux-gnueabi && make AM335x=ON
#CMD ./release.sh
#WORKDIR /$FIRMWARE_name/output

