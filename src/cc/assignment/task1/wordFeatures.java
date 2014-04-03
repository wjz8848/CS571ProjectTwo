/* EMORY CS571 Natural Language Processing 2014
 * Project 2 Named Entity Recognition
 */

/**
 * @author Jingzhi "John" Wang
 */

/*
 * wordFeatures.java
 *  
 * Store features and counts for each word.
 * 
 */

package cc.assignment.task1;

import java.util.ArrayList;
import java.util.HashMap;

public final class wordFeatures{
	
	public String word;
	public int wordCounts = 0;
	
	public ArrayList<String> labels = new ArrayList<String>(); 
	public ArrayList<String> POStags = new ArrayList<String>();
	public ArrayList<String> prefixWords  = new ArrayList<String>(); 
	public ArrayList<String> suffixWords = new ArrayList<String>();
	
	public ArrayList<Integer> labelCounts = new ArrayList<Integer>(); 
	public ArrayList<Integer> 	POStagCounts = new ArrayList<Integer>();  
	public ArrayList<Integer> 	prefixWordCounts = new ArrayList<Integer>();  
	public ArrayList<Integer> 	suffixWordCounts = new ArrayList<Integer>(); 
	
	public HashMap<String, Integer> labelStat = new HashMap<String, Integer>(); 
	public HashMap<String, Integer> POStagStat = new HashMap<String, Integer>();
	public HashMap<String, Integer> prefixWordStat = new HashMap<String, Integer>(); 
	public HashMap<String, Integer> suffixWordStat = new HashMap<String, Integer>();
	
	public wordFeatures(String word, String label, String prefixWord, String suffixWord)

	{
	   this.word = word;
	   this.wordCounts++;
	   
	   if(!this.labels.contains(label)){
		   this.labels.add(label);
		   int labelIndex = this.labels.indexOf(label);
		   this.labelCounts.add(labelIndex, 1);
	   } else if(this.labels.contains(label)){
		   int labelIndex = this.labels.indexOf(label);
		   int labelCount = this.labelCounts.get(labelIndex);
		   labelCount += 1;
		   this.labelCounts.set(labelIndex, labelCount);
	   }
	   
	   if(!this.prefixWords.contains(prefixWord)){
			this.prefixWords.add(prefixWord);
			int prefixWordsIndex = this.prefixWords.indexOf(prefixWord);
			this.prefixWordCounts.add(prefixWordsIndex, 1);
		} else if(this.prefixWords.contains(prefixWord)){
		    int prefixWordsIndex = this.labels.indexOf(prefixWord);
			int prefixWordCount = this.prefixWordCounts.get(prefixWordsIndex);
			prefixWordCount += 1;
			this.prefixWordCounts.set(prefixWordsIndex, prefixWordCount);
		}
		
		if(!this.suffixWords.contains(suffixWord)){
			this.suffixWords.add(suffixWord);
			int suffixWordIndex = this.suffixWords.indexOf(suffixWord);
			this.suffixWordCounts.add(suffixWordIndex, 1);
		} else if(this.suffixWords.contains(suffixWord)){
		    int suffixWordIndex = this.suffixWords.indexOf(suffixWord);
			int suffixWordCount = this.suffixWordCounts.get(suffixWordIndex);
			suffixWordCount += 1;
			this.suffixWordCounts.set(suffixWordIndex, suffixWordCount);
		}
	}

	public void record(String word, String label, String prefixWord, String suffixWord)
	{
		this.wordCounts++;
		
		if(!this.labels.contains(label)){
			this.labels.add(label);
			int labelIndex = this.labels.indexOf(label);
			this.labelCounts.add(labelIndex, 1);
		} else if(this.labels.contains(label)){
		    int labelIndex = this.labels.indexOf(label);
			int labelCount = this.labelCounts.get(labelIndex);
			labelCount += 1;
			this.labelCounts.set(labelIndex, labelCount);
		}
		
		if(!this.prefixWords.contains(prefixWord)){
			this.prefixWords.add(prefixWord);
			int prefixWordsIndex = this.prefixWords.indexOf(prefixWord);
			this.prefixWordCounts.add(prefixWordsIndex, 1);
		} else if(this.prefixWords.contains(prefixWord)){
		    int prefixWordsIndex = this.prefixWords.indexOf(prefixWord);
			int prefixWordCount = this.prefixWordCounts.get(prefixWordsIndex);
			prefixWordCount += 1;
			this.prefixWordCounts.set(prefixWordsIndex, prefixWordCount);
		}
		
		if(!this.suffixWords.contains(suffixWord)){
			this.suffixWords.add(suffixWord);
			int suffixWordIndex = this.suffixWords.indexOf(suffixWord);
			this.suffixWordCounts.add(suffixWordIndex, 1);
		} else if(this.suffixWords.contains(suffixWord)){
		    int suffixWordIndex = this.suffixWords.indexOf(suffixWord);
			int suffixWordCount = this.suffixWordCounts.get(suffixWordIndex);
			suffixWordCount += 1;
			this.suffixWordCounts.set(suffixWordIndex, suffixWordCount);
		}
	}

	public int getLabelOccurance(String label)
	{
		if(labels.contains(label)){
			return 1; //labels.indexOf(label);
		} else{
			return 0;
		}
	}
	
	public int getWordCounts(){
		return wordCounts;
	}
	
	public int getLabelCounts(String label){	
		return this.labelCounts.get(this.labels.indexOf(label));
	}
	
	public int getTotalLabelCounts(){
		int totalLabelCounts = 0;
		for(String label : labels){
			totalLabelCounts += this.labelCounts.get(this.labels.indexOf(label));
		}
		return totalLabelCounts;
	}
	
	public String returnHighestLabel(){
		int highestLabelCounts = 0;
		String highestLabel = "";
		
		for(int i = 0; i < this.labels.size(); i++){
			if (highestLabelCounts < this.labelCounts.get(i)){
				highestLabelCounts = this.labelCounts.get(i);
				highestLabel = this.labels.get(i);
			}
		}
		return highestLabel;
	}
}