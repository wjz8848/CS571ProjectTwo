/* EMORY CS571 Natural Language Processing 2014
 * Project 2 Named Entity Recognition
 */

/**
 * @author Jingzhi "John" Wang
 */

/*
 * task3.java
 * (task3 : Entity Subtypes (+20pts): 
 * Instead of recognising only the main classes (PER, ORG, LOC), predict precise 
 * entity subtype (as specified in the training data). Take advantage of the type 
 * hierarchy/taxonomy (i.e., combine training evidence for general ORG to predict 
 * OrgPol).)
 * 
 * Basic idea : expand the simple version pipe to include more features
 * 
 * ImprovedDetailedLabelNERdata2ToeknSequence recognize all tags
 * 
 * Using MALLET CRF Library and Construct to train and test.
 * 
 */

package cc.assignment.task3;

import java.io.IOException;

public class task3{
          
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
        		TrainDetailedCRF trainer = new TrainDetailedCRF(trainingDirectory, testingDirectory);
        	} catch (IOException e) {
        		System.err.println("File Not Found");
        	}
        	
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println();
            System.out.println("Total time : " + totalTime  + "mSec");      
        }
        
    	public static String trainingDirectory;
    	public static String testingDirectory;
}