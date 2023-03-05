#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

// Biblioteca do Firebase
#include <FirebaseESP8266.h>

// Host do Firebase
#define FIREBASE_HOST "ledrgbesp8266fire-default-rtdb.firebaseio.com"
// Token del Firebase
#define FIREBASE_AUTH "kMQPbulhPX3iG1S53mQjKVPi5UjLDSswAziG7SPr"

// Conexão ao ponto de acceso wifi
#define WIFI_SSID "Nome_da_rede_wifi"
#define WIFI_PASSWORD "Senha_da_rede_wifi"

WiFiClient client;
FirebaseData firebaseData;

// Pin rgb de cada LED
#define pinR 5
#define pinG 4
#define pinB 2
#define pinS 14

void setup() {
  Serial.begin(9600);

  WiFi.begin (WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println ("");
  Serial.println ("Wifi Conectado!");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  pinMode(pinR,OUTPUT);
  pinMode(pinG,OUTPUT);
  pinMode(pinB,OUTPUT);
  pinMode(pinS,OUTPUT);
}

void loop() {

  Firebase.getInt(firebaseData,"/red");
  int red = firebaseData.intData();
  Serial.println(red);
  
   
  Firebase.getInt(firebaseData,"/green");
  int green = firebaseData.intData();

  Firebase.getInt(firebaseData,"/blue");
  int blue = firebaseData.intData();

  Firebase.getInt(firebaseData,"/state");
  int state = firebaseData.intData();

  Firebase.getInt(firebaseData,"/cycle");
  int cycle = firebaseData.intData();

  digitalWrite(pinS, state);

  if (cycle == 0) {
    analogWrite(pinR, red);
    analogWrite(pinG, green);
    analogWrite(pinB, blue);
  } else {
    analogWrite(pinR, random(0,255));
    analogWrite(pinG, random(0,255));
    analogWrite(pinB, random(0,255));
  }

}
