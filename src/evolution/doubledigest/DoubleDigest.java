package evolution.doubledigest;

import evolution.*;
import evolution.individuals.DigestIndividual;
import evolution.individuals.Individual;
import evolution.operators.*;
import evolution.selectors.RouletteWheelSelector;


import java.io.*;
import java.util.*;


/************************************
 *          MAIN PROGRAM
 ************************************/

public class DoubleDigest {

    static int maxGen;
    static int popSize;
    static String logFilePrefix;
    static int repeats;
    static String bestPrefix;
    static double eliteSize;
    static double xoverProb;
    static double mutProb;
    static double mutProbPerEnzyme;
    static double geneChangePercentage;
    static String enableDetailsLog;
    static String outputDirectory;
    static String objectiveFilePrefix;
    static String objectiveStatsFile;
    static String fitnessFilePrefix;
    static String fitnessStatsFile;
    static String detailsLogPrefix;
    static Properties prop;
    static int[] a, b, ab;

    public static void main(String[] args) {

        // load project properties (or use defaults if IO error)
        prop = new Properties();
        try {
            InputStream propIn = new FileInputStream("properties/ga-digest.properties");
            prop.load(propIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        maxGen = Integer.parseInt(prop.getProperty("ea.maxGenerations", "20"));
        popSize = Integer.parseInt(prop.getProperty("ea.popSize", "30"));
        xoverProb = Double.parseDouble(prop.getProperty("ea.xoverProb", "0.8"));
        mutProb = Double.parseDouble(prop.getProperty("ea.mutProb", "0.4"));
        mutProbPerEnzyme = Double.parseDouble(prop.getProperty("ea.mutProbPerEnzyme", "0.5"));
        geneChangePercentage = Double.parseDouble(prop.getProperty("ea.geneChangePercentage", "0.3"));
        eliteSize = Double.parseDouble(prop.getProperty("ea.eliteSize", "0.1"));
        // input file or folder
        // tohle nastavovat v ga-digest.properties, tady je to jen backup default
        String inputFile = prop.getProperty("prob.inputFile", "resources/digest_hard.txt");

        repeats = Integer.parseInt(prop.getProperty("xset.repeats", "10"));
        enableDetailsLog = prop.getProperty("xlog.detailsLog", "enabled");
        if (!enableDetailsLog.equals("enabled")) {
            DetailsLogger.disableLog();
        }
        outputDirectory = prop.getProperty("xlog.outputDirectory", "digest");
        File output = new File(outputDirectory);
        output.mkdirs();

        // Check if the file exists
        File file = new File(inputFile);
        if(!file.exists()){
            System.err.println("Input file not found.");
            System.exit(1);
        }

        // it can be a directory or a single file, create an input file array (possibly with only single item)
        File[] inputFiles;
        if(file.isDirectory()){
            inputFiles = file.listFiles();
        } else {
            inputFiles = new File[]{file};
        }

        // iterate over all input files
        for (final File fileEntry : inputFiles) {
            if (!fileEntry.isDirectory()){   // not recursive, only depth 1
                String filepath = fileEntry.getPath();
                String filename = fileEntry.getName();

                // init loggers
                logFilePrefix = filename;
                if(filename.contains("."))
                    logFilePrefix = filename.substring(0, filename.lastIndexOf('.'));

                String path = outputDirectory + System.getProperty("file.separator") + logFilePrefix;
                objectiveFilePrefix = path + ".objective";
                objectiveStatsFile = path + ".objective_stats";
                bestPrefix = path + ".best";
                fitnessFilePrefix = path + ".fitness";
                fitnessStatsFile = path + ".fitness_stats";
                detailsLogPrefix = path + ".details";


                // read the A, B and AB fragments from the input file
                // a, b and ab are global (fuj, ale co už)
                try {
                    parseInput(filepath);
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                }

                // run the EA, store best individuals from each run
                List<Individual> bestInds = new ArrayList<Individual>();
                for (int i = 0; i < repeats; i++) {
                    Individual best = run(i);
                    bestInds.add(best);
                }
                // print the best individuals
                for (int i = 0; i < bestInds.size(); i++) {
                    System.out.println("run " + i + ": best objective=" + bestInds.get(i).getObjectiveValue());
                }
                // log the best individuals
                StatsLogger.processResults(fitnessFilePrefix, fitnessStatsFile, repeats, maxGen, popSize);
                StatsLogger.processResults(objectiveFilePrefix, objectiveStatsFile, repeats, maxGen, popSize);
            }
        }
    }

    /**
     * Performs one run of the EA.
     * @param number index of the current run
     * @return best individual found in this run
     */
    static Individual run(int number) {

        //Initialize logging of the run
        DetailsLogger.startNewLog(detailsLogPrefix + "." + number + ".xml");
        DetailsLogger.logParams(prop);

        RandomNumberGenerator.getInstance().reseed(number);

        try {
            // set up the initial population
            DigestIndividual sampleIndividual = new DigestIndividual(a.length, b.length);
            Population pop = new Population();
            pop.setSampleIndividual(sampleIndividual);
            pop.setPopulationSize(popSize);
            pop.createRandomInitialPopulation();

            // set up the EA
            EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm();
            // set fitness
            ea.setFitnessFunction(new DigestFitness(a, b, ab));

            // set genetic operators

            ea.addOperator(new Order1XOverDoubleDigest(xoverProb));
            //ea.addOperator(new PMXover(xoverProb));
            //ea.addOperator(new DigestInversionMutation(mutProb, mutProbPerEnzyme));
            ea.addOperator(new DigestSwappingMutation(mutProb, mutProbPerEnzyme, geneChangePercentage));

            ea.addEnvironmentalSelector(new RouletteWheelSelector());
            ea.setElite(eliteSize);

            // set up loggers
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fitnessFilePrefix + "." + number));
            OutputStreamWriter progOut = new OutputStreamWriter(new FileOutputStream(objectiveFilePrefix + "." + number));


            // evolve
            for (int i = 0; i < maxGen; i++) {
                ea.evolve(pop);

                Individual best = pop.getSortedIndividuals().get(0);
                System.out.println(logFilePrefix + " Generation " + i + ": " + best.getObjectiveValue());

                // ukoncovaci podminka - ale musi se vypnout logy, jinak to spadne!! radky 122 a 123
                /*if (best.getObjectiveValue() == 1.0)
                    i = maxGen;*/

                StatsLogger.logFitness(pop, out);
                StatsLogger.logObjective(pop, progOut);
            }

            // pretty print the best individual (asi k nicemu)
            OutputStreamWriter bestOut = new OutputStreamWriter(new FileOutputStream(bestPrefix + "." + number));
            DigestIndividual bestInd = (DigestIndividual) pop.getSortedIndividuals().get(0);
            prettyPrintIndividual(bestInd, bestOut);
            bestOut.close();

            // close the output streams
            out.close();
            progOut.close();

            DetailsLogger.writeLog();

            return bestInd;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Parses the fragment lengths from the input file. Expects three lines, on each of them space-separated sizes of
     * fragments: for enzyme A, enzyme B and both enzymes together
     * @param inputFileName name of the file containing the data
     * @throws IOException
     */
    static void parseInput(String inputFileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(inputFileName));
        String line;
        String[] strArray;
        if ((line = in.readLine()) == null) {
            System.err.println("Missing line.");
            throw new IOException();
        }
        strArray = line.replace(",","").split(" ");
        a = new int[strArray.length];
        for(int i = 0; i < strArray.length; i++) {
            a[i] = Integer.parseInt(strArray[i]);
        }
        if ((line = in.readLine()) == null) {
            System.err.println("Missing line.");
            throw new IOException();
        }
        strArray = line.replace(",","").split(" ");
        b = new int[strArray.length];
        for(int i = 0; i < strArray.length; i++) {
            b[i] = Integer.parseInt(strArray[i]);
        }
        if ((line = in.readLine()) == null) {
            System.err.println("Missing line.");
            throw new IOException();
        }
        strArray = line.replace(",","").split(" ");
        ab = new int[strArray.length];
        for(int i = 0; i < strArray.length; i++) {
            ab[i] = Integer.parseInt(strArray[i]);
        }}


    /**
     * Prints visual fragmentation of the individual by both enzymes given the permutations.
     * @param bestInd the individual to be printed
     * @param bestOut output
     * @throws IOException
     */
    static void prettyPrintIndividual(DigestIndividual bestInd, OutputStreamWriter bestOut) throws IOException {
        bestOut.write("Objective: " + bestInd.getObjectiveValue());
        bestOut.write(System.getProperty("line.separator"));
        // fragment lengths A
        bestOut.write(" ");
        for (int i = 0; i < bestInd.toIntArrayA().length; i++) {
            bestOut.write(a[bestInd.toIntArrayA()[i]]+"");
            for(int j = 0; j < 2*a[bestInd.toIntArrayA()[i]]- String.valueOf(a[bestInd.toIntArrayA()[i]]).length(); j++){
                bestOut.write(" ");
            }
        }
        bestOut.write(System.getProperty("line.separator"));

        // graphic output
        bestOut.write("|");
        for (int i = 0; i < bestInd.toIntArrayA().length; i++) {
            for (int j = 0; j < a[bestInd.toIntArrayA()[i]]; j++){
                bestOut.write("-");
                if(j < a[bestInd.toIntArrayA()[i]]-1){
                    bestOut.write(" ");
                }
            }
            bestOut.write("|");
        }
        bestOut.write(System.getProperty("line.separator"));
        bestOut.write("|");
        for (int i = 0; i < bestInd.toIntArrayB().length; i++) {
            for (int j = 0; j < b[bestInd.toIntArrayB()[i]]; j++){
                bestOut.write("-");
                if(j < b[bestInd.toIntArrayB()[i]]-1){
                    bestOut.write(" ");
                }
            }
            bestOut.write("|");
        }
        bestOut.write(System.getProperty("line.separator"));

        // fragment lengths B
        bestOut.write(" ");
        for (int i = 0; i < bestInd.toIntArrayB().length; i++) {
            bestOut.write(b[bestInd.toIntArrayB()[i]]+"");
            for(int j = 0; j < 2*b[bestInd.toIntArrayB()[i]]- String.valueOf(b[bestInd.toIntArrayB()[i]]).length(); j++){
                bestOut.write(" ");
            }
        }
        bestOut.write(System.getProperty("line.separator"));
    }
}
