/* EMORY CS571 Natural Language Processing 2014
 * Project 2 Named Entity Recognition
 */

/**
 * @author Jingzhi "John" Wang
 */

/*
 * TrainCRF.java
 * 
 * Import Data -> Create Trainer -> Set Trainer Parameter -> Train/Evaluate
 * 
 */

package cc.assignment.task2;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import cc.assignment.task2.ImprovedSimpleLabelNERdata2TokenSequence;
import cc.mallet.fst.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.types.*;
import cc.mallet.util.*;

public class TrainCRF {
	
	public TrainCRF(String trainingFileFolder, String testingFileFolder) throws IOException {
		
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		
		// Construct pipes
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new ImprovedSimpleLabelNERdata2TokenSequence(true)); // true to extract feature
        pipeList.add(new TokenSequence2FeatureVectorSequence());
        //////////////////////////////////////////////////
        
		Pipe pipe = new SerialPipes(pipeList);

		InstanceList trainingInstances = new InstanceList(pipe);
		InstanceList testingInstances = new InstanceList(pipe);
		
		File[] trainingFiles = new File(trainingFileFolder).listFiles();
		for(File trainingfile : trainingFiles){
			LineGroupIterator it = new LineGroupIterator(new BufferedReader(new InputStreamReader(
									new FileInputStream(trainingfile))), Pattern.compile("^\\s*$"), true);
			trainingInstances.addThruPipe(it);	
		}
		
		System.out.println("Training loaded");
		
		File[] testingFiles = new File(testingFileFolder).listFiles();
		for(File testingfile : testingFiles){
			testingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(
											new FileInputStream(testingfile))), Pattern.compile("^\\s*$"), true));
		}
		System.out.println("Testing loaded");
		System.out.println();
		
        // Taking 20%, 40%, 60%, 80%, 100% of Training Data as input
        InstanceList[] ilists = trainingInstances.split(new Randoms(), new double[] {1.0, 0.0, 0.0});
		
		CRF crf = new CRF(pipe, null);
		
		// Better result use All Label Connections
		// Higher efficiency use less Label Connections
		// Choose one :
//		crf.addFullyConnectedStatesForLabels();
		// or
		crf.addStatesForLabelsConnectedAsIn(ilists[0]);
		// or 
//		crf.addStatesForThreeQuarterLabelsConnectedAsIn(ilists[0]);	
		// or
//		crf.addStatesForBiLabelsConnectedAsIn (ilists[0]);
		// or
//		crf.addStatesForHalfLabelsConnectedAsIn (ilists[0]);
		
//		crf.addStartState(); // Use Start State

		
		CRFTrainerByThreadedLabelLikelihood trainer = 
			      new CRFTrainerByThreadedLabelLikelihood(crf, 6);
		trainer.setGaussianPriorVariance (100.0);
		
//		CRFTrainerByStochasticGradient trainer = 
//				new CRFTrainerByStochasticGradient(crf, 1.0);

//		CRFTrainerByL1LabelLikelihood trainer = 
//				new CRFTrainerByL1LabelLikelihood(crf, 0.75);
		
//		CRFTrainerByValueGradients trainer = 
//				new CRFTrainerByValueGradients(crf, null);  
		// DON'T UNDERSTAND, need to check the source

		trainer.addEvaluator(new PerClassAccuracyEvaluator(testingInstances, "testing"));
		trainer.train(ilists[0]);
		trainer.shutdown();
	}
}