/* EMORY CS571 Natural Language Processing 2014
 * Project 2 Named Entity Recognition
 */

/**
 * @author Jingzhi "John" Wang
 */

/*
 * task1.java
 * (Baseline: Train a span classifier to classify all NPs 
 * into Per, Loc, Org, or Other (if none). 
 * You may use heuristics as needed.)
 * 
 * Set the Baseline for NER Task
 * 
 * Basic idea : Count occurency for each NER tag for each word
 * 				Use NaiveBayes method return the most probable 
 * 				tag in the tag list.
 */

package cc.assignment.task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class task1{
	
    public static void main(String[] args) throws IOException {
    	long startTime = System.currentTimeMillis();
    	//////////////////////////////////////////////////////////////
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
    	///////////////////////////////////////////////////////////////
        System.out.println("Training Start...");
        
    	myDict = readDirectory(trainingDirectory);
    	
        System.out.println("Training Done!");
        System.out.println();
        ///////////////////////////////////////////////////////////////
        System.out.println("Testing Start...");
    	
    	testDirectory(testingDirectory);

    	System.out.println("Testing Done!");
    	System.out.println();
    	///////////////////////////////////////////////////////////////
    	System.out.println("Evaluation Start...");
    	
    	evaluate();
    	
    	System.out.println("Evaluation  Done!");
    	System.out.println();
    	} catch(IOException e){
    		System.err.println("File Not Found");
    	}
    	///////////////////////////////////////////////////////////////
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time : " + totalTime + "mSec"); 
    }//main()


    //////////////////////////Training Part Start/////////////////////////
    // readIn() allows to read the formatted labeled data in;
    public static HashMap<String, wordFeatures> readDirectory(String address) throws FileNotFoundException {
    	
    	File[] files = new File(address).listFiles();    	

    	//loop thru files
    	for (File file : files){
    		extractFile(file);
    		fileCounter++;
    		
    		if(((fileCounter%300)== 0))
    			System.out.println(fileCounter + " files read, still reading, please wait");
    	}//end loop thru files
        
    	System.out.println("Model not saved");
    	System.out.println("Number of Files Read : " + fileCounter);
    	System.out.println("Number of Lines Read : " + lineCounter);
    	return myDict;
    }//readDirectory()
    
    
    //extractFile() process each file into lines and call featureExtract()
    public static void extractFile(File file) throws FileNotFoundException{
    	
    	Scanner sc = new Scanner(file);
		
		//loop thru lines
		while (sc.hasNext()) {
			String line = sc.nextLine();
			String[] tokens = line.split ("\n");
				
				
			//loop thru tokens
			for (int i = 0; i < tokens.length; i++) {
					
		    	 if (tokens[i].length() != 0) {
		    	 		
		    	 	String[] tokenizedLine = tokens[i].split ("\t");

		    	 	featureExtract(tokenizedLine);
 		
		    	 	lineCounter++;
		    	 }
			}//end loop thru tokens		
		}//end loop thru lines 
    }//extractFile()
    
    
    //featureExtract() extract each line and put them into the dictionary
    public static void featureExtract(String[] tokenizedLine){
    	
    	//String[] labelSet = new String[]{"B-PER", "B-LOC", "B-ORG", "B-MISC",
		//		"I-PER", "I-LOC", "I-ORG", "I-MISC"};
    	
    	
    	//sentenceNum = features[0];
		//wordNum = features[1];
    	if(tokenizedLine.length ==7 ){
    		nextWord = tokenizedLine[2];
    	   	nextLabel = tokenizedLine[6];
    	}else {
    		nextWord = tokenizedLine[2];
    	   	nextLabel = "-<S>-";
    	}
 
		if(myDict.containsKey(word)){
			myDict.get(word).record(word, label, previousWord, nextWord);
		} else {
			myDict.put(word, new wordFeatures(word, label, previousWord, nextWord));
		}
		
		/*
		if(word.equals("Atlanta")){
		System.out.println(previousWord + " - " + word + " - " + nextWord + " has occured " + 
							myDict.get(word).getWordCounts() + " times and as " + label +
							" for "+ myDict.get(word).getLabelCounts(label) + " times.");
		}*/
		
		previousWord = word;
		previousLabel = label;
		word = nextWord;
		label = nextLabel;
	}// featureExtract()
    ////////////////////////Training Part End//////////////////////////
    
    
    //////////////////////////Testing Part Start/////////////////////////
    public static void testDirectory(String address) throws FileNotFoundException{
    	File[] files = new File(address).listFiles();
    	
    	for (File file : files){
    		testFile(file);
    		testfileCounter++;
    	}//end loop thru files
    }
    
    public static void testFile(File file) throws FileNotFoundException{
    	Scanner sc = new Scanner(file);
	
		//loop thru lines
		while (sc.hasNext()) {
			String line = sc.nextLine();
			String[] tokens = line.split ("\n");
			
			
			//loop thru tokens
			for (int i = 0; i < tokens.length; i++) {
				
	    	 	if (tokens[i].length() != 0) {
	    	 		
	    	 		String[] tokenizedLine = tokens[i].split ("\t");
	    	 		
	    	 		featurePrediction(tokenizedLine);
		
	    	 		lineCounter++;
	    	 	}
			}//end loop thru tokens
			
		}//end loop thru lines 
    }
    
    public static void featurePrediction(String[] tokenizedLine){
    	
    	if(tokenizedLine.length ==7 ){
        	//nextWord = tokenizedLine[2];
        	//nextLabel = tokenizedLine[6];
    		
    		word = tokenizedLine[2];
    		label  = tokenizedLine[6];
    		String predictedLabel;
    		
    		if(myDict.containsKey(word)){
    			predictedLabel = myDict.get(word).returnHighestLabel();
    		} else {
    			predictedLabel = "O";
    		}
    	   	
    	   	compareLabels(label, predictedLabel);
    	   	
    	}
    	
    	//previousWord = word;
		//previousLabel = label;
		//word = nextWord;
		//label = nextLabel;

    }
    
    public static void compareLabels(String label, String predictedLabel){
    	
    	if(!labelCounts.containsKey(label)){
    		labelCounts.put(label, 1);
		} else if(labelCounts.containsKey(label)){
			int labelCount = labelCounts.get(label) + 1;
			labelCounts.put(label, labelCount);
		}
    	
    	if(!predictedLabelCounts.containsKey(predictedLabel)){
    		predictedLabelCounts.put(predictedLabel, 1);
		} else if(predictedLabelCounts.containsKey(predictedLabel)){
			int predictedLabelCount = predictedLabelCounts.get(predictedLabel) + 1;
			predictedLabelCounts.put(predictedLabel, predictedLabelCount);
		}
    	
    	if(label.equals(predictedLabel)){
    		if(!correctLabelCounts.containsKey(label)){
    			correctLabelCounts.put(label, 1);
    		} else if(correctLabelCounts.containsKey(label)){
    			int correctLabelCount = correctLabelCounts.get(label) + 1;
    			correctLabelCounts.put(label, correctLabelCount);
    		}
    	}
    	
    }
    ////////////////////////Testing Part End//////////////////////////////////
    
    
    ///////////////////////Evaluation Part Start//////////////////////////////
    
    public static void evaluate(){
    	
    	System.out.printf("%-7s \t\t %s \t %s \t %s \t %s \t %s \t %s \n", 
    						"Label", "Precision", "Recall", "F1", 
    						"correctLabelCounts", "predictedLabelCounts", "labelCounts");
    	double totalCorrectCount = 0;
    	double totalpredictedCount = 0;
    	double totalLabelCount = 0;
    	
    	for(String label : labelCounts.keySet()){
    		
    		if(label.equals("O"))
    			continue;
    		
    		double Precision;
    		double Recall;
    		double F1;
    		double correctCount;
    		double predictedCount;
    		double labelCount;
    		
    		if(correctLabelCounts.containsKey(label) && predictedLabelCounts.containsKey(label)){
    			correctCount = correctLabelCounts.get(label).doubleValue();
    			predictedCount = predictedLabelCounts.get(label).doubleValue();
    			Precision =  correctCount / predictedCount;
    		} else {
    			correctCount = 0;
    			predictedCount = 0;
    			Precision = -1;
    		}
    		
    		if(correctLabelCounts.containsKey(label) && labelCounts.containsKey(label)){
    			correctCount = correctLabelCounts.get(label).doubleValue();
    			labelCount = labelCounts.get(label).doubleValue();
    			Recall = correctCount / labelCount;
    		} else {
    			correctCount = 0;
    			labelCount = 0;
    			Recall = -1;
    		}
    		
    		if(Precision > 0 && Recall > 0){
    			F1 = 2/(1/Precision + 1/Recall);
    		} else {
    			F1 = -1;
    		}
    		
    		totalCorrectCount += correctCount;
    		totalpredictedCount += predictedCount;
    		totalLabelCount += labelCount;
    		
    		System.out.printf("%-7s \t\t %f \t %f \t %f \t %f \t %f \t %f \n", 
    							label, Precision, Recall, F1, correctCount, predictedCount,
    							labelCount);    		
    	}
    	
    	double totalPrecision = totalCorrectCount/totalpredictedCount;
    	double totalRecall = totalCorrectCount/totalLabelCount;
    	double totalF1 = 2/(1/totalPrecision + 1/totalRecall);
    	
    	System.out.printf("%-7s \t\t %f \t %f \t %f \t %f \t %f \t %f \n", 
							"Total", totalPrecision, totalRecall, totalF1, 
							totalCorrectCount, totalpredictedCount, totalLabelCount);
    	
    	//System.out.println(labelCounts.keySet().toString());
    	//System.out.println(predictedLabelCounts.keySet().toString());
    	//System.out.println(correctLabelCounts.keySet().toString());
    }
    
    public static int lineCounter = 0;
	public static int fileCounter = 0;
	public static int testfileCounter = 0;

	public static String previousWord = "-<S>-";
	public static String previousLabel = "-<S>-";
	public static String word = "-<S>-";
	public static String label = "-<S>-";
	public static String nextWord, nextLabel;
	
	public static HashMap<String, Integer> labelCounts  = new HashMap<String, Integer>();
	public static HashMap<String, Integer> predictedLabelCounts  = new HashMap<String, Integer>();
	public static HashMap<String, Integer> correctLabelCounts  = new HashMap<String, Integer>();
	
	public static String trainingDirectory;
	public static String testingDirectory;
	public static String defaultModelAddress = "F:/cs571data/project2/data/baselineClassifier";
		
	public static ArrayList<String> labelSet = new ArrayList<String>();
	
	public static HashMap<String, wordFeatures> myDict = new HashMap<String, wordFeatures>();
	

}