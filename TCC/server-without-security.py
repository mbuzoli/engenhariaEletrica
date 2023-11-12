import asyncio
import random
from asyncua import ua, Server

async def dict_format(keys, values):
    return dict(zip(keys, values))


async def server_task():
    server = Server()
    await server.init()
    server.set_endpoint('opc.tcp://192.168.1.101:4840/opcua/')
    server.set_server_name("Marcio Buzoli OPC-UA Test Server")

    idx = 0

    myobj = await server.nodes.objects.add_object(idx, "MyObject")
    myvar = await myobj.add_variable(idx, "MyVariable", 0.0)
    await myvar.set_writable()  

    async with server:
        while True:
            await asyncio.sleep(1)
            current_val = await myvar.get_value()
            count = current_val 
            await myvar.write_value(count)

    async with server:
        while True:

            await myvar.set_writable()
            await asyncio.sleep(1)

async def main():
    client_task_handler = asyncio.create_task(client_task())

if __name__ == '__main__':
    asyncio.run(server_task())

