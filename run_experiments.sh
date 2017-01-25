#input=`grep "inputFile" properties/ga-digest.properties | sed "s/prob.inputFile = //"`
outputDirectory="digest"
files=""
logFileNames=""
legendNames=""


lengths=(5 10 15)
muts=("inv" "swp")
xovers=("pmx" "ox")
selects=("roul" "tour")


for l in "${lengths[@]}"
do
	input="resources/digest/real/"$l

	for m in "${muts[@]}"
	do
		for x in "${xovers[@]}"
		do
			for s in "${selects[@]}"
			do
				echo $l$m$x$s
				echo -e "prob.inputFile = $input\nxlog.outputDirectory = $outputDirectory/$l\nprob.mutOp = $m\nprob.xoverOp = $x\nprob.selectOp = $s" | cat properties/ga-digest.properties.template - > properties/ga-digest.properties

				java -cp out/production/DDP_GA/ evolution.doubledigest.DoubleDigest

				python make_average.py $outputDirectory"/"$l $outputDirectory"/"$l"-cuts-$x-$m-$s.objective_stats"

			done
		done
	done

done

