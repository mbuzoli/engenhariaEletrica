
import asyncio
import random
from asyncua import ua, Server, Client

async def dict_format(keys, values):
    return dict(zip(keys, values))

async def server_task():
    # setup our server
    server = Server()
    await server.init()
    server.set_endpoint('opc.tcp://192.168.0.104:4840/opcua/')  # Replace <IP_AQUI> with Raspberry Pi 1 IP
    server.set_server_name("Smar OPC-UA Test Server")

    # setup our own namespace, not really necessary but should as spec
    idx = await server.register_namespace("mynamespace")

    # populating our address space
    obj_vplc = await server.nodes.objects.add_object(idx, 'vPLC')
    var_temperature = await obj_vplc.add_variable(idx, 'temperature', 0)
    var_pressure = await obj_vplc.add_variable(idx, 'pressure', 0)
    var_pumpsetting = await obj_vplc.add_variable(idx, 'pumpsetting', 0)

    async with server:
        while True:
            # Writing Variables
            await var_temperature.write_value(random.randint(25, 35))
            await var_pressure.write_value(random.randint(55, 75))
            await var_pumpsetting.write_value(random.randint(0, 1))
            await asyncio.sleep(5)

async def client_task():
    connected = False
    url = "opc.tcp://192.168.0.102:4840/opcua/"  # Replace IP  with Raspberry Pi 1 IP
    while not connected:
        try:
            async with Client(url=url) as client:
                connected = True
                while True:
                    data_variables = ["temperature", "pressure", "pumpsetting"]
                    data_list = []
                    namespace = "mynamespace"
                    idx = await client.get_namespace_index(namespace)
                    for var_name in data_variables:
                        myvar = await client.nodes.root.get_child(["0:Objects", "{}:vPLC".format(idx), "{}:{}".format(idx, var_name)])
                        val = await myvar.get_value()
                        data_list.append(val)
                    print(await dict_format(data_variables, data_list))
                    await asyncio.sleep(5)
        except Exception as e:
            print("Error connecting to the server. Retrying...")
            await asyncio.sleep(5)

async def main():
    # Run both server and client tasks concurrently
    server_task_handler = asyncio.create_task(server_task())
    client_task_handler = asyncio.create_task(client_task())
    
    # Wait for both tasks to complete (this will never happen due to the infinite loops)
    await asyncio.gather(server_task_handler, client_task_handler)

if __name__ == '__main__':
    asyncio.run(main())

