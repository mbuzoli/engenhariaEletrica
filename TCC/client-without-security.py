import asyncio
from asyncua import Client

async def dict_format(keys, values):
    return dict(zip(keys, values))

async def client_task():
    connected = False
    url = "opc.tcp://192.168.1.101:4840/opcua/"
    client = Client(url=url)
    try:
        async with client:
            while True:
                objects = client.nodes.objects
                child = await objects.get_child(['0:MyObject', '0:MyVariable'])
                await child.set_value(float(await child.get_value()+0.1))
                value = await child.get_value()
                print("{:.1f}".format(value))
                await asyncio.sleep(0.1)  # Aguarda 100 ms antes de ler novamente
    except asyncio.CancelledError:
        print("Loop de leitura interrompido pelo usuário.")

async def main():

    await asyncio.gather(server_task_handler, client_task_handler)



if __name__ == '__main__':
    asyncio.run(client_task())
