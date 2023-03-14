#include <iostream>
#include <wiringPiSPI.h>
#include <wiringPiI2C.h>

#define SPI_CHANNEL 0
#define SPI_CLOCK_SPEED 1000000
#define DEVICE_ID 0x08

void spi_communication() {
    // Setup SPI communication
    int fd = wiringPiSPISetupMode(SPI_CHANNEL, SPI_CLOCK_SPEED, 0);
    if (fd == -1) {
        std::cout << "Error setting up SPI communication\n";
        return;
    }
    std::cout << "SPI communication successfully setup.\n";

    // Send and receive data in a loop
    for (int i = 0; i < 10; i++) {
        // Send data to device
        unsigned char buf[2] = { 1+i, 0 };
        wiringPiSPIDataRW(SPI_CHANNEL, buf, 2);
        std::cout << "Data returned: " << +buf[1] << "\n";
    }
}

void i2c_communication() {
    // Setup I2C communication
    int fd = wiringPiI2CSetup(DEVICE_ID);
    if (fd == -1) {
        std::cout << "Failed to init I2C communication.\n";
        return;
    }
    std::cout << "I2C communication successfully setup.\n";

    // Send and receive data in a loop
    for (int i = 0; i < 10; i++) {
        // Send data to device
        uint8_t data_to_send = i + 1;
        wiringPiI2CWrite(fd, data_to_send);
        std::cout << "Sent data: " << (int)data_to_send << "\n";
       
        // Read data from device
        int received_data = wiringPiI2CRead(fd);
        std::cout << "Data received: " << received_data << "\n";
        if (received_data == data_to_send + 10) {
            std::cout << "Success!\n";
        }
    }
}

int main(int argc, char** argv) {
    spi_communication();
    i2c_communication();
    return 0;
}
