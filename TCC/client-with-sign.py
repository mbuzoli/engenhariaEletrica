import asyncio
import logging
import sys
import socket
from pathlib import Path
from cryptography.x509.oid import ExtendedKeyUsageOID
sys.path.insert(0, "..")
from asyncua import Client
from asyncua.crypto.security_policies import SecurityPolicyBasic256Sha256
from asyncua.crypto.cert_gen import setup_self_signed_certificate
from asyncua.crypto.validator import CertificateValidator, CertificateValidatorOptions
from asyncua.crypto.truststore import TrustStore
from asyncua import ua

logging.basicConfig(level=logging.INFO)
_logger = logging.getLogger(__name__)

USE_TRUST_STORE = False

cert_idx = 4
cert_base = Path(__file__).parent
cert = Path(cert_base / f"certificates/peer-certificate-example-4.der")
private_key = Path(cert_base / f"certificates/peer-private-key-example-4.pem")

async def task(loop):
    host_name = socket.gethostname()
    client_app_uri = f"urn:{host_name}:foobar:myselfsignedclient"
    url = "opc.tcp://192.168.1.101:4840/freeopcua/server/"
    print('\n\n ', client_app_uri,'\n\n')
    await setup_self_signed_certificate(private_key,
                                        cert,
                                        client_app_uri,
                                        host_name,
                                        [ExtendedKeyUsageOID.CLIENT_AUTH],
                                        {
                                            'countryName': 'CN',
                                            'stateOrProvinceName': 'AState',
                                            'localityName': 'Foo',
                                            'organizationName': "Bar Ltd",
                                        })
    client = Client(url=url)
    client.application_uri = client_app_uri
    await client.set_security(
        SecurityPolicyBasic256Sha256,
        certificate=str(cert),
        private_key=str(private_key),
        mode=ua.MessageSecurityMode.Sign,
        server_certificate="server-certificate-example.der"
        
    )

    if USE_TRUST_STORE:
        trust_store = TrustStore([Path('examples') / 'certificates' / 'trusted' / 'certs'], [])
        await trust_store.load()
        validator =CertificateValidator(CertificateValidatorOptions.TRUSTED_VALIDATION|CertificateValidatorOptions.PEER_SERVER, trust_store)
    else:
        validator =CertificateValidator(CertificateValidatorOptions.EXT_VALIDATION|CertificateValidatorOptions.PEER_SERVER)
    client.certificate_validator = validator

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

def main():
    loop = asyncio.get_event_loop()
    loop.set_debug(True)
    try:
        loop.run_until_complete(task(loop))
    except KeyboardInterrupt:
        pass
    finally:
        loop.close()

if __name__ == "__main__":
    main()
