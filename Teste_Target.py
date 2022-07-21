#!/usr/bin/env python
# coding: utf-8

# In[ ]:


#Exercicio 1

indice = 13
soma = 0
k = 0

while k < indice:
    k = k+1
    soma = soma+k
print(soma)    


# In[ ]:


#Exercicio 2
valorDigitado = int(input("Digite um numero inteiro: "))
v1 = 0
v2 = 1
v3 = 0

while v3 < valorDigitado:
    v3 = v1 + v2
    v1 = v2
    v2 = v3
if v3 == valorDigitado:
    print("Esse número pertence a sequencia de Fibonacci")
else:
    print("Esse número não pertence a sequencia de Fibonacci")


# In[ ]:


#Exercicio 3

#a) 1, 3, 5, 7, 9 (numeros ímpares)

#b) 2, 4, 8, 16, 32, 64, 128 (próximo elemente é o dobro do antecessor)

#c) 0, 1, 4, 9, 16, 25, 36, 49 (próximo elemento é somado a um elemento de uma sequencia de numeros ímpares(1,3,5,7,...,13), no caso 36+13.

#d) 4, 16, 36, 64, 100 (Os numeros são resultados de uma sequencia dos numeros pares elevado ao quadrado, no caso o próximo numero é 10^2 = 100)

#e) 1, 1, 2, 3, 5, 8, 13 (Sequencia de Fibonacci onde o próximo elemento é a soma dos ultimos 2 elementos)

#f) 2,10, 12, 16, 17, 18, 19, 200(os elementos que iniciam com a letra "D", o próximo elemento é o Duzentos)

#Exercicio 4

#No momento exato em que se estáo se cruzando no pista, ambos estarão a mesma distãncia de Ribeirão Preto, a partir desse momento o Caminhão estará cada vez mais próximo de Ribeirão Preto do que o carro.


# In[ ]:


#Exercicio 5

stringAleatoria = str(input("Digite uma frase qualquer:"))
stringInvertida = stringAleatoria [::-1]
print(stringInvertida)

