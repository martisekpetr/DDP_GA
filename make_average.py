import numpy as np
import sys
from os import listdir
from os.path import isfile, join

mypath = sys.argv[1]
prefix = ""
if len(sys.argv) > 3:
    prefix = sys.argv[3]

print("prefix "+prefix)

onlyfiles = [mypath+"/"+f for f in listdir(mypath) if isfile(join(mypath, f)) and f.endswith(".objective_stats") and f.startswith(prefix)]

a = []
for f in onlyfiles:
    n = np.loadtxt(f)
    a.append(n)

aaa = np.mean(np.array(a), axis=0)

np.savetxt(sys.argv[2], aaa)

