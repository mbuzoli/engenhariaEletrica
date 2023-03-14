import argparse
import logging

from pyModbusTCP.server import DataBank, ModbusServer


class MyDataBank(DataBank):
    """A custom ModbusServerDataBank for override on_xxx_change methods."""

    def on_coils_change(self, address, from_value, to_value, srv_info):
        """Call by server when change occur on coils space."""
        msg = 'change in coil space [{0!r:^5} > {1!r:^5}] at @ 0x{2:04X} from ip: {3:<15}'
        msg = msg.format(from_value, to_value, address, srv_info.client.address)
        logging.info(msg)

    def on_holding_registers_change(self, address, from_value, to_value, srv_info):
        """Call by server when change occur on holding registers space."""
        msg = 'change in hreg space [{0!r:^5} > {1!r:^5}] at @ 0x{2:04X} from ip: {3:<15}'
        msg = msg.format(from_value, to_value, address, srv_info.client.address)
        logging.info(msg)


if __name__ == '__main__':
    # parse args
    parser = argparse.ArgumentParser()
    parser.add_argument('-H', '--host', type=str, default='192.168.56.101', help='Host (default: localhost)')
    parser.add_argument('-p', '--port', type=int, default=1026, help='TCP port (default: 502)')
    args = parser.parse_args()
    # logging setup
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.INFO)
    logging.getLogger('pyModbusTCP.server').setLevel(logging.DEBUG)
    # init modbus server and start it
    server = ModbusServer(host=args.host, port=args.port, data_bank=MyDataBank())
    server.start()
