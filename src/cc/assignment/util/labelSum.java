package cc.assignment.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class labelSum{
	int labelCounter = 0;
	public static ArrayList<String> labelSet = new ArrayList<String>();
	
	public static void main (String[] args) throws IOException {
		
		String address = "F:/cs571data/project2/data/train";
		File[] files = new File(address).listFiles();
		for (File file : files){
			Scanner sc = new Scanner(file);
			
			//loop thru lines
			while (sc.hasNext()) {
				String line = sc.nextLine();
				String[] tokens = line.split ("\n");

				//loop thru tokens
				for (int i = 0; i < tokens.length; i++) {	
			    	 if (tokens[i].length() != 0) {
			    	 	String[] tokenizedLine = tokens[i].split ("\t");
			    	 	if(tokenizedLine.length == 7){
			    	 		String Label = tokenizedLine[6];
			    	 		if(labelSet.contains(Label)){
			    	 			continue;
			    	 		}else{
			    	 			labelSet.add(Label);
			    	 		}
			    	 	} else {
			    	 		continue;
			    	 	}
			    	 }
				}//end loop thru tokens		
			}//end loop thru lines 
    	}//end loop thru files
		
		System.out.println(labelSet.toString());
	}
}