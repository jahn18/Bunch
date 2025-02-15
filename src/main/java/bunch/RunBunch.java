package bunch;

import bunch.api.BunchAPI;
import bunch.api.BunchProperties;
import bunch.engine.BunchEngine;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Hashtable;

public class RunBunch {

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println("Usage: java RunBunch <file-name> <consensus-groups> <population size> <dest>");
			System.exit(1);
		}

		System.out.println(args[0]);

		BunchAPI api = new BunchAPI();
		BunchProperties bp = new BunchProperties();

		bp.setProperty(BunchProperties.MDG_INPUT_FILE_NAME, args[0]);
		bp.setProperty(BunchProperties.OUTPUT_FORMAT, BunchProperties.TEXT_OUTPUT_FORMAT);
		bp.setProperty(BunchProperties.OUTPUT_DIRECTORY, args[3] + File.pathSeparator);
		bp.setProperty(BunchProperties.MQ_CALCULATOR_CLASS, "bunch.ITurboMQ");
		bp.setProperty(BunchProperties.ECHO_RESULTS_TO_CONSOLE, "False");
		bp.setProperty(BunchProperties.MDG_OUTPUT_MODE, BunchProperties.OUTPUT_DETAILED);
		bp.setProperty(BunchProperties.ALG_HC_POPULATION_SZ, args[2]);
        bp.setProperty(BunchProperties.ALG_HC_HC_PCT, "100");
        bp.setProperty(BunchProperties.ALG_HC_RND_PCT,"100");
		bp.setProperty(BunchProperties.LOCK_USER_SET_CLUSTERS,"False");
		bp.setProperty(BunchProperties.USER_DIRECTED_CLUSTER_SIL, args[1]);
		System.out.println(bp.getProperty(BunchProperties.ALG_HC_POPULATION_SZ));
		api.setProperties(bp);
		api.run();
        
//        System.out.println("Results: ");
        Hashtable results = api.getResults();
//        printResults(results);

		BunchEngine engine = null;
		Field field = api.getClass().getDeclaredField("engine");
		field.setAccessible(true);
		engine = (BunchEngine) field.get(api);

		GraphOutput graphOutput = new GraphOutputFactory().getOutput("Text");
		graphOutput.setBaseName(args[0]);
		graphOutput.setBasicName(args[0]);
		String outputFileName = graphOutput.getBaseName();
		String outputPath = args[3];
		if (outputPath != null) {
			File f = new File(graphOutput.getBaseName());
			String filename = f.getName();
			outputFileName = outputPath + filename;
		}
		graphOutput.setCurrentName(outputFileName);
		graphOutput.setOutputTechnique(4);
		graphOutput.setGraph(engine.getBestGraph());
		graphOutput.write();
	}

      public static void printResults(Hashtable results)
    {
        String rt = (String)results.get(BunchAPI.RUNTIME);
        String evals = (String)results.get(BunchAPI.MQEVALUATIONS);
        String levels = (String)results.get(BunchAPI.TOTAL_CLUSTER_LEVELS);
        String saMovesTaken = (String)results.get(BunchAPI.SA_NEIGHBORS_TAKEN);

        System.out.println("Runtime = " + rt + " ms.");
        System.out.println("Total MQ Evaluations = " + evals);
        System.out.println("Simulated Annealing Moves Taken = " + saMovesTaken);
        System.out.println();
        Hashtable [] resultLevels = (Hashtable[])results.get(BunchAPI.RESULT_CLUSTER_OBJS);

        for(int i = 0; i < resultLevels.length; i++)
        {
            Hashtable lvlResults = resultLevels[i];
            System.out.println("***** LEVEL "+i+"*****");
            String mq = (String)lvlResults.get(BunchAPI.MQVALUE);
            String depth = (String)lvlResults.get(BunchAPI.CLUSTER_DEPTH);
            String numC = (String)lvlResults.get(BunchAPI.NUMBER_CLUSTERS);

            System.out.println("  MQ Value = " + mq);
            System.out.println("  Best Cluster Depth = " + depth);
            System.out.println("  Number of Clusters in Best Partition = " + numC);
            System.out.println();
        }
    }
}
