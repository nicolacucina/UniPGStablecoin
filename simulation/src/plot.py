import sys
import csv
import os
import matplotlib.pyplot as plt
from os.path import exists

os.chdir("..")
config_properties = os.path.join(os.getcwd(), 'bin', 'data', 'simulation.properties')
#print(exists(config_properties))
#print(config_properties)
with open(config_properties, 'r') as reader:
    line = reader.readline()
    #print(line)

logname = line.split('=')[1][:-1];
#print(logname)

csv_path = os.path.join(os.getcwd(), 'bin', 'data', logname+ '.csv')
#print(csv_path)

prices = []
tokens = []
days = []

with open(csv_path, 'r', encoding='utf-8') as csvfile:
    reader = csv.DictReader(csvfile, delimiter=',')
    i = 0
    for row in reader:
        prices.append(float(row['Price']))
        tokens.append(float(row['Token']))
        days.append(i)
        i += 1

fig, (ax1, ax2) = plt.subplots(2, sharex=True)
fig.suptitle('Prices and token supply evolution')
ax1.plot(days, prices, 'o-')
ax1.set_title('Prices evolution')
ax1.set_ylabel('Prices')
ax1.grid(True)
ax2.plot(days, tokens, 'o-')
ax2.set_title('Token supply evolution')
ax2.set_ylabel('Token amount')
ax2.set_xlabel('Days')
ax2.grid(True)
plt.show()