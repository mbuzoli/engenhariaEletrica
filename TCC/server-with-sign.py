import asyncio
import sys
from pathlib import Path
import socket

import logging
sys.path.insert(0, "..")
from asyncua import Server
from asyncua import ua
from asyncua.crypto.permission_rules import SimpleRoleRuleset
from asyncua.server.user_managers import CertificateUserManager
from asyncua.crypto.cert_gen import setup_self_signed_certificate
from asyncua.crypto.validator import CertificateValidator, CertificateValidatorOptions
from cryptography.x509.oid import ExtendedKeyUsageOID
from asyncua.crypto.truststore import TrustStore

logging.basicConfig(level=logging.INFO)

USE_TRUST_STORE = False

async def main():
    cert_base = Path(__file__).parent
    server_cert = Path(cert_base / "certificates/server-certificate-example.der")
    server_private_key = Path(cert_base / "certificates/server-private-key-example.pem")
    host_name = socket.gethostname()
    server_app_uri =   f"myselfsignedserver@{host_name}"


    cert_user_manager = CertificateUserManager()
    await cert_user_manager.add_user("certificates/peer-certificate-example-4.der", name='test_user')
    await cert_user_manager.add_user("certificates/uaexpert.der", name='test_user1')
    server = Server(user_manager=cert_user_manager)
    server.set_server_name("Marcio Buzoli OPC-UA Test Server - Mode: Sign")
    await server.init()

    await server.set_application_uri(server_app_uri)
    server.set_endpoint("opc.tcp://192.168.1.101:4840/freeopcua/server/")
    server.set_security_policy([ua.SecurityPolicyType.Basic256Sha256_Sign],
                               permission_ruleset=SimpleRoleRuleset())

    await setup_self_signed_certificate(server_private_key,
                                        server_cert,
                                        server_app_uri,
                                        host_name,
                                        [ExtendedKeyUsageOID.CLIENT_AUTH, ExtendedKeyUsageOID.SERVER_AUTH],
                                        {
                                            'countryName': 'CN',
                                            'stateOrProvinceName': 'AState',
                                            'localityName': 'Foo',
                                            'organizationName': "Bar Ltd",
                                        })

    await server.load_certificate(str(server_cert))
    await server.load_private_key(str(server_private_key))

    if USE_TRUST_STORE:
        trust_store = TrustStore([Path('examples') / 'certificates' / 'trusted' / 'certs'], [])
        await trust_store.load()
        validator = CertificateValidator(options=CertificateValidatorOptions.TRUSTED_VALIDATION | CertificateValidatorOptions.PEER_CLIENT,
                                         trust_store = trust_store)
    else:
        validator = CertificateValidator(options=CertificateValidatorOptions.EXT_VALIDATION | CertificateValidatorOptions.PEER_CLIENT)
    server.set_certificate_validator(validator)

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


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    asyncio.run(main())

