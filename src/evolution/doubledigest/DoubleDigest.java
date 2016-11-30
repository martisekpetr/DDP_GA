package evolution.doubledigest;

import evolution.*;
import evolution.individuals.DigestIndividual;
import evolution.individuals.Individual;
import evolution.operators.*;
import evolution.selectors.RouletteWheelSelector;


import java.io.*;
import java.util.*;

public class DoubleDigest {

    static int maxGen;
    static int popSize;
    static String logFilePrefix;
    static int repeats;
    static String bestPrefix;
    static double eliteSize;
    static double xoverProb;
    static double mutProb;
    static double mutProbPerBit;
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

        // load project properties
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
        mutProb = Double.parseDouble(prop.getProperty("ea.mutProb", "0.05"));
        mutProbPerBit = Double.parseDouble(prop.getProperty("ea.mutProbPerBit", "0.04"));
        eliteSize = Double.parseDouble(prop.getProperty("ea.eliteSize", "0.1"));

//        String inputFile = prop.getProperty("prob.inputFile", "resources/digest_easy.txt");

        repeats = Integer.parseInt(prop.getProperty("xset.repeats", "10"));
        enableDetailsLog = prop.getProperty("xlog.detailsLog", "enabled");
        if (!enableDetailsLog.equals("enabled")) {
            DetailsLogger.disableLog();
        }

        outputDirectory = prop.getProperty("xlog.outputDirectory", "tsp");

        File output = new File(outputDirectory);
        output.mkdirs();

        final File folder = new File("resources/digest/");

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()){
                String filepath = fileEntry.getPath();
                String filename = fileEntry.getName();

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
                try {
                    parseInput(filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                List<Individual> bestInds = new ArrayList<Individual>();

                for (int i = 0; i < repeats; i++) {
                    Individual best = run(i);
                    bestInds.add(best);
                }

                for (int i = 0; i < bestInds.size(); i++) {
                    System.out.println("run " + i + ": best objective=" + bestInds.get(i).getObjectiveValue());
                }

                StatsLogger.processResults(fitnessFilePrefix, fitnessStatsFile, repeats, maxGen, popSize);
                StatsLogger.processResults(objectiveFilePrefix, objectiveStatsFile, repeats, maxGen, popSize);

            }
        }
    }

    static Individual run(int number) {

        //Initialize logging of the run

        DetailsLogger.startNewLog(detailsLogPrefix + "." + number + ".xml");
        DetailsLogger.logParams(prop);

        RandomNumberGenerator.getInstance().reseed(number);

        try {

            DigestIndividual sampleIndividual = new DigestIndividual(a.length, b.length);

            Population pop = new Population();
            pop.setSampleIndividual(sampleIndividual);
            pop.setPopulationSize(popSize);

            EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm();

            ea.setFitnessFunction(new DigestFitness(a, b, ab));
            ea.addOperator(new DigestInversionMutation(mutProb));
            ea.addEnvironmentalSelector(new RouletteWheelSelector());
            ea.setElite(eliteSize);

            pop.createRandomInitialPopulation();

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fitnessFilePrefix + "." + number));
            OutputStreamWriter progOut = new OutputStreamWriter(new FileOutputStream(objectiveFilePrefix + "." + number));

            for (int i = 0; i < maxGen; i++) {
                ea.evolve(pop);

                System.out.println(logFilePrefix + " Generation " + i + ": " + pop.getSortedIndividuals().get(0).getObjectiveValue());

                StatsLogger.logFitness(pop, out);
                StatsLogger.logObjective(pop, progOut);
            }

            // pretty print the best individual
            OutputStreamWriter bestOut = new OutputStreamWriter(new FileOutputStream(bestPrefix + "." + number));
            DigestIndividual bestInd = (DigestIndividual) pop.getSortedIndividuals().get(0);
            prettyPrintIndividual(bestInd, bestOut);
            bestOut.close();

            // close the output streams
            out.close();
            progOut.close();

            DetailsLogger.writeLog();

            return bestInd;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    static void parseInput(String inputFile) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String line;
        String[] strArray;
        if ((line = in.readLine()) == null) {
            System.err.println("Missing line.");
            throw new IOException();
        }
        strArray = line.split(" ");
        a = new int[strArray.length];
        for(int i = 0; i < strArray.length; i++) {
            a[i] = Integer.parseInt(strArray[i]);
        }
        if ((line = in.readLine()) == null) {
            System.err.println("Missing line.");
            throw new IOException();
        }
        strArray = line.split(" ");
        b = new int[strArray.length];
        for(int i = 0; i < strArray.length; i++) {
            b[i] = Integer.parseInt(strArray[i]);
        }
        if ((line = in.readLine()) == null) {
            System.err.println("Missing line.");
            throw new IOException();
        }
        strArray = line.split(" ");
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
