import random
from Bio.Restriction import *
from Bio.Alphabet import generic_dna
from Bio.Seq import Seq

# choose restriction enzymes
# TODO randomize?

enzyme1 = BsiSI
enzyme2 = PspPI

NUM_TAKES = 5

rb = RestrictionBatch([BsiSI, PspPI])

# make random artificial DNA sequences of different lengths
for size in (10**i for i in range(3, 6)):
    # multiple samples of the same length
    for take in range(NUM_TAKES):
        dna = ''.join([random.choice('AGTC') for x in range(size)])
        seq = Seq(dna, generic_dna)
        Ana = Analysis(rb, seq, linear=True)
        # print(Ana.full())

        dic = Ana.full()

        with open("digest_rnd_{}_{}_{}_{}".format(size, take, enzyme1, enzyme2), 'w') as f:
            for enzyme in dic:
                prev = 0
                for loc in dic[enzyme]:
                    f.write("{} ".format(loc - prev))
                    prev = loc
                f.write("\n")
            prev = 0
            for loc in sorted(item for sublist in dic.values() for item in sublist):
                f.write("{} ".format(loc - prev))
                prev = loc

# TODO make tests with real DNA sequences?
# TODO refine the range of the artificial sequences 