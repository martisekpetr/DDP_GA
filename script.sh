java -cp out/production/DDP_GA/ evolution.doubledigest.DoubleDigest

input=`grep "inputFile" properties/ga-digest.properties | sed "s/prob.inputFile = //"`
outputDirectory=`grep "outputDirectory" properties/ga-digest.properties | sed "s/xlog.outputDirectory = //"`


logFileNames=""
legendNames=""

files=""

if [[ -d $input ]] 
then
	files=$input/*

fi

if [[ -f $input ]] 
then
	files=$input
fi

for file in $files
do
	basename="$(basename ${file%.*})"

	legendNames=$legendNames,$basename
	logFileNames=$logFileNames,$outputDirectory/$basename
done

legendNames=`echo $legendNames | sed 's/^.//'`
echo $legendNames
logFileNames=`echo $logFileNames | sed 's/^.//'`
echo $logFileNames

bash createGraphs.sh -logFileNames $logFileNames -legendNames $legendNames



