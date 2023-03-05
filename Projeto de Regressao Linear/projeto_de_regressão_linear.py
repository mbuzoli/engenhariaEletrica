# -*- coding: utf-8 -*-
"""Projeto de Regressão Linear.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1w6FFz4-Q9Wu8_8v0nvuBrI_rOgAaT_8N

## **Regressão Linear**

*   Marcio Rafae Buzoli - ST3004414 
*   Mineração de Dados
"""

from google.colab import drive
drive.mount('/content/drive')

"""## **Importando bibliotecas e definindo o CSV**"""

import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import numpy as np

dados = pd.read_csv('/content/drive/MyDrive/Faculdade/Data_Mining/FuelEconomy.csv',sep=',')

"""A base de dados consiste na quantidade de cavalos (HP) e a autonomia (no padrão ameicano de milhas por galão "miles per galon")"""

dados.head()

dados.describe().round(2)

ax=sns.distplot(dados['Fuel Economy (MPG)'])
ax.figure.set_size_inches(12,6)
ax.set_title('Fuel Economy (MPG)',fontsize=20)
ax.set_xlabel('Horse Power',fontsize=15)

"""Observamos abaixo os dados dispostos em no plano cartesiano. é percpetivel a tendencia dos dados para o tracejamento de uma reta."""

x = dados ["Horse Power"]
y = dados ["Fuel Economy (MPG)"]
ax = plt.scatter(x,y)
ax.figure.set_size_inches(15,6)

"""São diversos os atributos responsáveis pelo consumo de combustivel de um veículo, contudo o objetivo dessa base de dados é meramente didático ao analisar a potência do veículo de acordo com sua autonomia.

Definindo variáveis e importando bibliotecas de treino, aplicação e teste:
"""

X = dados['Horse Power']
y = dados['Fuel Economy (MPG)']
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression

"""**Treinando** """

X_train, X_test, y_train, y_test = train_test_split(X,y,test_size=0.7, random_state=10)

#Array unico
X_train = X_train[:,np.newaxis] 
X_test = X_test[:,np.newaxis]

lr = LinearRegression()
lr.fit(X_train,y_train)

"""Observando resultados e comparando com a resposta esperada"""

y_pred = lr.predict(X_test) 
y_pred

y_test

"""Linha de tendência:"""

ax = sns.lmplot(x="Horse Power",y="Fuel Economy (MPG)",data=dados)
ax.fig.suptitle('Dispersão - Potência x Consumo',fontsize=18,y=1.1)
ax.set_axis_labels('Horse Power','Fuel Economy (MPG)',fontsize = 14)

from sklearn.metrics import r2_score,mean_squared_error

mse = mean_squared_error(y_test,y_pred)
print('mean square error:',mse)

rsq = r2_score(y_test,y_pred)
print('r2_score:',rsq)

"""O valor acima estará entre 0 e 1, este é a porcentagem de resultados que pode ser explicada pelo método implementado, os demais resultados são falhas."""