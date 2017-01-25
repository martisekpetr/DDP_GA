outputDirectory="digest"

lengths=(5 10 15)
files_2=()


for l in "${lengths[@]}"
do
	cd $outputDirectory
	logFileNames=""
	legendNames=""

	files=`ls $l-cuts*`
	for file in $files
	do
		basename="$(basename ${file%.*})"
	
		legendNames=$legendNames,$basename
		logFileNames=$logFileNames,$outputDirectory/$basename
	done

	legendNames=`echo $legendNames | sed 's/^.//'`
	logFileNames=`echo $logFileNames | sed 's/^.//'`
	
	cd ..
	
	bash createGraphs.sh -logFileNames $logFileNames -legendNames $legendNames -logScale x -output graph-$l-comp.svg

	python make_average.py $outputDirectory $outputDirectory"/"$l-sites.objective_stats $l-cuts
	files_2+=( $outputDirectory"/"$l-sites.objective_stats )
done


logFileNames=""
legendNames=""

for file in "${files_2[@]}"
do
	basename=$(basename "$file" .objective_stats)

	legendNames=$legendNames,$basename
	logFileNames=$logFileNames,$outputDirectory/$basename
done

legendNames=`echo $legendNames | sed 's/^.//'`
logFileNames=`echo $logFileNames | sed 's/^.//'`

bash createGraphs.sh -logFileNames $logFileNames -legendNames $legendNames -logScale x -output graph-big.svg





