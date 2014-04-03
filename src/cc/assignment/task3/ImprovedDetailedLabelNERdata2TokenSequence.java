/* EMORY CS571 Natural Language Processing 2014
 * Project 2 Named Entity Recognition
 */


/**
 * @author Jingzhi "John" Wang
 */

/*
 * ImprovedDetailedLabelNERData2TokenSequence 
 * Convert Input Data File to Token Sequence with Feature tagged
 * Include the word order into account
 * Preserve the detailed tags
 */



package cc.assignment.task3;

import java.util.ArrayList;
import java.util.regex.*;

import cc.mallet.pipe.*;
import cc.mallet.types.*;

public class ImprovedDetailedLabelNERdata2TokenSequence extends Pipe
{
	static final String[] endings = new String[]
	{"ing", "ed", "ogy", "s", "ly", "ion", "tion", "ity", "ies"};
	static Pattern[] endingPatterns = new Pattern[endings.length];
	// Indexed by {forward,backward} {0,1,2 offset} {ending char ngram index}
	static final String[][][] endingNames = new String[2][3][endings.length];

	{
		for (int i = 0; i < endings.length; i++) {
			endingPatterns[i] = Pattern.compile (".*"+endings[i]+"$");
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 2; k++)
					endingNames[k][j][i] = "W"+(k==1?"-":"")+j+"=<END"+endings[i]+">";
			}
		}
	}

	boolean saveSource = false;
	boolean doConjunctions = false;
	boolean doTags = true;
	boolean doPhrases = true;
	boolean doSpelling = false;
	boolean doDigitCollapses = true;
	boolean doDowncasing = false;
	
	public ImprovedDetailedLabelNERdata2TokenSequence ()
	{
		super (null, new LabelAlphabet());
	}

	public ImprovedDetailedLabelNERdata2TokenSequence (boolean extraFeatures)
	{
		super (null, new LabelAlphabet());
		if (!extraFeatures) {
			doDigitCollapses = doConjunctions = doSpelling = doPhrases = doTags = false;
			doDowncasing = true;
		}
	}
	
	/* Lines look like this:
0	0	1998-09-21	CD	O	O	B-Num
0	1	00	CD	O	O	B-Num
0	2	:	:	O	O	I-Num
0	3	03	CD	O	B-NP	I-Num
0	4	PEREZ	NNP	O	I-NP	O
0	5	SHUTS	NNP	O	I-NP	O
0	6	OUT	IN	O	I-NP	O
0	7	GIANTS	NNP	O	I-NP	O

1	0	SAN	NNP	O	O	O
1	1	FRANCISCO	NNP	O	O	O
1	2	_	NN	O	O	O
1	3	Kevin	NNP	O	B-NP	B-Peop
1	4	Malone	NNP	O	I-NP	I-Peop
	*/

	public Instance pipe (Instance carrier)
	{
		
		String sentenceLines = carrier.getData().toString();
		String[] tokens = sentenceLines.split ("\n");
		TokenSequence data = new TokenSequence (tokens.length);
		LabelSequence target = new LabelSequence ((LabelAlphabet)getTargetAlphabet(), tokens.length);
		boolean [][] ending = new boolean[3][endings.length];
		boolean [][] endingp1 = new boolean[3][endings.length];
		boolean [][] endingp2 = new boolean[3][endings.length];
		StringBuffer source = saveSource ? new StringBuffer() : null;

		
		String prevWord = "<Start>";
		String prevPOStag = "<Start>";
		String prevPhrase = "<Start>";
		String prevLabel = "O";
		
		String word = "<Start>";
		String POStag = "<Start>"; 
		String phrase = "<Start>";
		String label = "O";
		
		String nextWord, nextPOStag, nextPhrase, nextLabel;
		Pattern ipattern = Pattern.compile ("I-.*");
		
		
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].length() != 0) {
				String[] features = tokens[i].split ("\t");
				if (features.length != 7){
					//throw new IllegalStateException ("Line \""+tokens[i]+"\" doesn't have 7 elements");
					continue;
				}
				//sentenceNum = features[0];
				//wordNum = features[1];
				nextWord = features[2]; 
				nextPOStag = features[3];
				nextPhrase = features[5];
				nextLabel = labelPrefixRemover(features[6]);
				//System.out.println(word + " - " + label);
			} else {
				//sentenceNum = "-<S>-";
				//wordNum = "-<S>-";
				nextWord = "<Start>";
				nextPOStag = "<Start>";
				nextPhrase = "<Start>";
				nextLabel = "O";
			}

			// Transformations
			if (doDigitCollapses) {
				if (word.matches ("19\\d\\d"))
					word = "<YEAR>";
				else if (word.matches ("19\\d\\ds"))
					word = "<YEARDECADE>";
				else if (word.matches ("19\\d\\d-\\d+"))
					word = "<YEARSPAN>";
				else if (word.matches ("\\d+\\\\/\\d"))
					word = "<FRACTION>";
				else if (word.matches ("\\d[\\d,\\.]*"))
					word = "<DIGITS>";
				else if (word.matches ("19\\d\\d-\\d\\d-\\d--d"))
					word = "<DATELINEDATE>";
				else if (word.matches ("19\\d\\d-\\d\\d-\\d\\d"))
					word = "<DATELINEDATE>";
				else if (word.matches (".*-led"))
					word = "<LED>";
				else if (word.matches (".*-sponsored"))
					word = "<LED>";
			}

			if (doDowncasing)
				word = word.toLowerCase();
			Token token = new Token (word);
			
			// Word and tag unigram at current time
			if (doSpelling) {
				for (int j = 0; j < endings.length; j++) {
					ending[2][j] = ending[1][j];
					ending[1][j] = ending[0][j];
					ending[0][j] = endingPatterns[j].matcher(word).matches();
					if (ending[0][j]) token.setFeatureValue (endingNames[0][0][j], 1);
				}
			}

			if (doTags) {
				token.setFeatureValue ("T="+POStag, 1);
			}

			if (doPhrases) {
				token.setFeatureValue ("P="+phrase, 1);
			}

			//************//
			boolean addLabelPrefix = false;
			if (addLabelPrefix) {
				// Change so each segment always begins with a "B-",
				// even if previous token did not have this label.
				String oldLabel = label;
				if (ipattern.matcher(label).matches ()
						&& (prevLabel.length() < 3		// prevLabel is "O"
								|| !prevLabel.substring(2).equals (label.substring(2)))) {
					label = "B" + oldLabel.substring(1);
				}
				prevLabel = oldLabel;
			}
			
			boolean addPreviousLabel = true;
			if(addPreviousLabel){
				token.setFeatureValue ("PL="+prevLabel,1);
			}
			
			boolean addNextLabel = true;
			if(addNextLabel){
				token.setFeatureValue ("NL="+ nextLabel,1);
			}
			
			boolean addPrefixWord = true;
			if(addPrefixWord){
				token.setFeatureValue ("PF="+prevWord,1);
			}
			
			boolean addSuffixWord = true;
			if(addSuffixWord){
				token.setFeatureValue ("SF="+ nextWord,1);
			}

			// Append
			data.add (token);
			//target.add (bigramLabel);
			target.add (label);
			//System.out.print (label + ' ');
			if (saveSource) {
				source.append (word); source.append (" ");
				//source.append (bigramLabel); source.append ("\n");
				source.append (label); source.append ("\n");
			}
			
			prevWord = word;
			prevLabel = label;
			prevPOStag = POStag;
			prevPhrase = phrase;
			
			word = nextWord;
			label = nextLabel;
			POStag = nextPOStag;
			phrase = nextPhrase;
		}
		
		//System.out.println ("NERED");
		carrier.setData(data);
		carrier.setTarget(target);
		if (saveSource)
			carrier.setSource(source);
		return carrier;
	}
	
	public static String labelPrefixRemover(String label){
		
		String newLabel = "O";
		
		if(label.contains("B-")) {
			newLabel = label.replace("B-", "");
		} else if(label.contains("I-")) {
			newLabel = label.replace("I-", "");
		}
		
		if(newLabel.contains("Peo")) {
			return "PER";
		} else if(newLabel.contains("Loc")) {
			return "LOC";
		} else if(newLabel.contains("Org")) {
			return newLabel;
		} else if(newLabel.equals("O")){
			return newLabel;
		} else {
			return "MISC";
		}
	}
}
