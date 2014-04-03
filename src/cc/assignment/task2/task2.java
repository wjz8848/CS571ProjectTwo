/* EMORY CS571 Natural Language Processing 2014
 * Project 2 Named Entity Recognition
 */

/**
 * @author Jingzhi "John" Wang
 */

/*
 * task2.java
 * (task2 : HMM (or CMM or CRF): 
 * Train an HMM (or CMM or MEMM or CRF) to tag the named entities.)
 * 
 * Basic idea : built a pipe that can convert given data to tokens with features
 * 
 * Two versions of pipes.
 * SimpleLabelNERdata2ToeknSequence pipe was built earlier and did not take conjunction
 * into account.
 * ImprovedSimpleLabelNERdata2ToeknSequence took previous word's feature and next word's
 * feature into account.
 * 
 * Using MALLET CRF Library and Construct to train and test.
 * 
 */

package cc.assignment.task2;

import java.io.IOException;

public class task2{
          
        public static void main (String[] args) throws IOException {   
        	long startTime = System.currentTimeMillis();
        	
        	if(args.length == 2){
        		trainingDirectory = args[0];
        		testingDirectory = args[1];
        	} else if (args.length == 0){
        		//trainingDirectory = "D:/cs571data/data/train";
        		//testingDirectory = "D:/cs571data/data/test";
        		
        		trainingDirectory = "/aut/proj/ir/eugene/Data/CS571/project2/data/train";
        		testingDirectory = "/aut/proj/ir/eugene/Data/CS571/project2/data/test";
        	} else {
        		System.err.println("Argument Wrong, please provide valid "
        				+ "[TrainingDirectory] [TestingDirectory]");
        	}
        	
        	
        	try{
        		TrainCRF trainer = new TrainCRF(trainingDirectory, testingDirectory);
        	} catch (IOException e) {
        		System.err.println("File Not Found");
        	}
        	
        	
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println();
            System.out.println("Total time : " + totalTime + "mSec");      
        }
        
    	public static String trainingDirectory;
    	public static String testingDirectory;
}