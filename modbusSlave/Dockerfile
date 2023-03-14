FROM alpine:latest
LABEL maintainer= name=value

WORKDIR /
RUN apk update
RUN apk add py-pip git python3

COPY MODBUS_Slave.py .

RUN pip install pyModbusTCP
CMD python3 MODBUS_Slave.py
