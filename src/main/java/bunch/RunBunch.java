package bunch;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.File;
import java.util.regex.Pattern;
import java.awt.*;
import javax.swing.*;

import bunch.api.*;
import bunch.engine.*;

public class RunBunch {
    /** Creates parent directories if necessary. Then returns file */
    private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + "/" + filename);
    }

    private static JProgressBar createProgressBar(int MAX) {
        final JFrame frame = new JFrame("JProgress Demo");

        // creates progress bar
        final JProgressBar pb = new JProgressBar();
        pb.setMinimum(0);
        pb.setMaximum(MAX);
        pb.setStringPainted(true);

        // add progress bar
        frame.setLayout(new FlowLayout());
        frame.getContentPane().add(pb);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);
        return pb;
    }

    private static void updateProgressBar(JProgressBar progressBar, int progress) {
        final int updateValue = progress;
        final JProgressBar pb = progressBar;
        try {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pb.setValue(updateValue);
                }
            });
        } catch (Exception e) {
            System.out.println("Error!");
            System.out.println(e.getMessage());
            System.out.println("Last value: " + Integer.toString(updateValue));
        }
    }

    public static void main(String[] args) throws Exception {
        runBunch(5, "PartsUnlimitedMRPClassNameGraph.mdg", "");
    }

    public static void runBunch(int numberOfClusters, String inputFile, String outputFile) throws Exception {
        BunchAPI api = new BunchAPI();
        BunchProperties bp = new BunchProperties();

        bp.setProperty(BunchProperties.MDG_INPUT_FILE_NAME, inputFile);
        bp.setProperty(BunchProperties.OUTPUT_FORMAT, BunchProperties.TEXT_OUTPUT_FORMAT);
        bp.setProperty(BunchProperties.OUTPUT_DIRECTORY, outputFile);
        bp.setProperty(BunchProperties.ECHO_RESULTS_TO_CONSOLE, "True");

        bp.setProperty(BunchProperties.MQ_CALCULATOR_CLASS, "bunch.TurboMQ");
        //bp.setProperty(BunchProperties.CLUSTERING_APPROACH, BunchProperties.AGGLOMERATIVE);
        bp.setProperty(BunchProperties.CLUSTERING_APPROACH, BunchProperties.ONE_LEVEL);

        //added
        bp.setProperty(BunchProperties.MDG_OUTPUT_MODE, BunchProperties.OUTPUT_TREE);
        bp.setProperty(BunchProperties.ALG_HC_POPULATION_SZ, "200");
        bp.setProperty(BunchProperties.ALG_HC_HC_PCT, "75");
        bp.setProperty(BunchProperties.ALG_HC_RND_PCT, "90");
        bp.setProperty(BunchProperties.FIX_NUMBER_OF_CLUSTERS, "True");
        bp.setProperty(BunchProperties.NUMBER_OF_CLUSTERS, String.valueOf(numberOfClusters));

        api.setProperties(bp);
        api.run();

        BunchEngine engine = null;
        Field field = api.getClass().getDeclaredField("engine");
        field.setAccessible(true);
        engine = (BunchEngine) field.get(api);

        GraphOutput graphOutput = new GraphOutputFactory().getOutput("Text");
        graphOutput.setBaseName(inputFile);
        graphOutput.setBasicName(inputFile);
        String outputFileName = graphOutput.getBaseName();
        String outputPath = outputFile;
        if (outputFile != null) {
            File f = new File(graphOutput.getBaseName());
            String filename = f.getName();
            outputFileName = outputPath + filename;
        }
        graphOutput.setCurrentName(outputFileName);
        graphOutput.setOutputTechnique(3);
        graphOutput.setGraph(engine.getBestGraph());
        graphOutput.write();
    }

//    public static void runBunch(String projectName, int numberOfClusters, String outDir) throws Exception {
//        System.out.println(projectName + "==========================");
//
//        BunchAPI api = new BunchAPI();
//        BunchProperties bp = new BunchProperties();
//        String PROJECT_NAME = projectName;
//        String NUMBER_OF_CLUSTERS = Integer.toString(numberOfClusters);
//        int DIVISOR = 20;
//        int MAX_PROGRESS = 1771;
//        //final JProgressBar progressBar = createProgressBar(MAX_PROGRESS);
//
//        String inputGraphsPath = "/staticGraph.mdg";
//        String resultGraphsPath = PROJECT_NAME;
//
//        String inputFile = inputGraphsPath;
//        String outputFile = resultGraphsPath;
//        File output;
//
//        File resultFile = fileWithDirectoryAssurance(resultGraphsPath, "results.csv");
//        FileWriter resultWriter = new FileWriter(resultFile.getAbsolutePath());
//        resultWriter.append("file_name,MQ_Value,static,dynamic,class_names,class_terms,commits,contributors\n");
//
//        int progress = 0;
//        for(int i = 0; i <= DIVISOR; i++)
//        {
//            String currentPath = inputGraphsPath + Integer.toString(i) + '/';
//            File currentInputDir = new File(currentPath);
//            File [] files = currentInputDir.listFiles();
//            if (files != null)
//            {
//                for(File file : files)
//                {
//                    inputFile = file.getAbsolutePath();
//                    String outputFileName = file.getName();
//                    output = fileWithDirectoryAssurance(resultGraphsPath + Integer.toString(i) + '/', outputFileName);
//                    outputFile = output.getAbsolutePath();
//
//                    bp.setProperty(BunchProperties.MDG_INPUT_FILE_NAME, inputFile);
//                    bp.setProperty(BunchProperties.OUTPUT_FORMAT, BunchProperties.TEXT_OUTPUT_FORMAT);
//                    bp.setProperty(BunchProperties.OUTPUT_DIRECTORY, outputFile);
//                    bp.setProperty(BunchProperties.ECHO_RESULTS_TO_CONSOLE, "True");
//
//                    bp.setProperty(BunchProperties.MQ_CALCULATOR_CLASS, "bunch.TurboMQ");
//                    //bp.setProperty(BunchProperties.CLUSTERING_APPROACH, BunchProperties.AGGLOMERATIVE);
//                    bp.setProperty(BunchProperties.CLUSTERING_APPROACH, BunchProperties.ONE_LEVEL);
//
//                    //added
//                    bp.setProperty(BunchProperties.MDG_OUTPUT_MODE, BunchProperties.OUTPUT_TREE);
//                    bp.setProperty(BunchProperties.ALG_HC_POPULATION_SZ, "200");
//                    bp.setProperty(BunchProperties.ALG_HC_HC_PCT, "75");
//                    bp.setProperty(BunchProperties.ALG_HC_RND_PCT, "90");
//                    bp.setProperty(BunchProperties.FIX_NUMBER_OF_CLUSTERS, "True");
//                    bp.setProperty(BunchProperties.NUMBER_OF_CLUSTERS, NUMBER_OF_CLUSTERS);
//
//                    api.setProperties(bp);
//                    api.run();
//
//                    Hashtable results = api.getResults();
//                    int medLvlGraphIndex = Integer.parseInt((String) results.get("MedianLevelGraph"));
//                    Hashtable [] resultLevels = (Hashtable[])results.get(BunchAPI.RESULT_CLUSTER_OBJS);
//
//                    String MQResult = (String) ((Hashtable) resultLevels[medLvlGraphIndex]).get(BunchAPI.MQVALUE);
//
//                    String[] weights = file.getName().replace(".mdg", "").split(Pattern.quote("_"));
//                    String staticWeight = Integer.toString(Integer.parseInt(weights[0]) * 100 / DIVISOR);
//                    String dynamicWeight = Integer.toString(Integer.parseInt(weights[2]) * 100 / DIVISOR);
//                    String classNamesWeight = Integer.toString(Integer.parseInt(weights[4]) * 100 / DIVISOR);
//                    String classTermsWeight = Integer.toString(Integer.parseInt(weights[6]) * 100 / DIVISOR);
//                    String commitsWeight = Integer.toString(Integer.parseInt(weights[8]) * 100 / DIVISOR);
//                    String contributorsWeight = Integer.toString(Integer.parseInt(weights[10]) * 100 / DIVISOR);
//
//                    resultWriter.append(file.getName() + ',' +
//                            MQResult + ',' +
//                            staticWeight + ',' +
//                            dynamicWeight + ',' +
//                            classNamesWeight + ',' +
//                            classTermsWeight + ',' +
//                            commitsWeight + ',' +
//                            contributorsWeight +
//                            '\n'
//                    );
//                    resultWriter.flush();
//
//                    BunchEngine engine = null;
//                    Field field = api.getClass().getDeclaredField("engine");
//                    field.setAccessible(true);
//                    engine = (BunchEngine) field.get(api);
//
//                    GraphOutput graphOutput = new GraphOutputFactory().getOutput("Text");
//                    graphOutput.setBaseName(inputFile);
//                    graphOutput.setBasicName(inputFile);
//                    outputFileName = graphOutput.getBaseName();
//                    String outputPath = outputFile;
//                    if (outputPath != null) {
//                        File f = new File(graphOutput.getBaseName());
//                        String filename = f.getName();
//                        outputFileName = outputPath + filename;
//                    }
//                    graphOutput.setCurrentName(outputFileName);
//                    graphOutput.setOutputTechnique(3);
//                    graphOutput.setGraph(engine.getBestGraph());
//                    graphOutput.write();
//
//                    progress++;
//                    //updateProgressBar(progressBar, progress);
//                }
//            }
//        }
//        resultWriter.flush();
//        resultWriter.close();
//    }
}
