CS571ProjectTwo ReadMe

//////////////////////////////////////////////////////////////////////////

author: Jingzhi (John) Wang
email: jwang67@emory.edu

//////////////////////////////////////////////////////////////////////////

Structure
	task1 : Using Naive Bayes Method to set up the baseline for Name Entity 
			Recognition (NER);
	task2 : Using Conditional Random Field (CRF) model to train NER tagger
			for major NER class (PER/LOC/ORG/O)
	task3 : Using CRF model to train NER tagger for Sub-class of ORG

How-to-run
Same for Runnable Jar files task1.jar, task2.jar, task3.jar

Command Line Input : java –jar task*.jar [trainDir] [testDir]

args[0] [trainDir] where training data is provided
Default is /aut/proj/ir/eugene/Data/CS571/project2/data/train , if you don’t enter anything

args[1] [testDir] where testing data is provided
Default is /aut/proj/ir/eugene/Data/CS571/project2/data/test , if you don’t enter anything



//////////////////////////////////////////////////////////////////////////

Experiment:

	Baseline Evaluation (task1)
	
		Basic Logic:
			1.	Count each label for certain words
			2.	Map label and counts to the word in wordFeatures
			3.	Iterate through training data
			4.	Test the testing data and get the most probable label
			5.	Evaluate and report Precision/Recall/F1 value
		
		Files
			task1.java : execute the heuristic logistics
			wordFeatures. java : collect the simple features presented in the training data

		Result
			Label   	 Precision 	 Recall 	 F1 
			B-Peop  		0.769 	0.743 	0.756 
			I-Peop  		0.551 	0.321 	0.406 
			B-Loc   		0.667 	0.333 	0.444 
			I-Loc			0.250 	0.200 	0.222 
			B-Org   		0.333 	0.500 	0.400 
			I-Org   		0.625 	0.278 	0.385 
			B-OrgCorp 		0.586 	0.773 	0.667 
			I-OrgCorp 		0.625 	0.588 	0.606 
			B-OrgPolBody 	0.714 	0.714 	0.714 
			I-OrgPolBody 	0.667 	0.667 	0.667 
			B-OrgTeam 		0.905 	0.905 	0.905 
			B-OrgUniv 		1.000 	0.600 	0.750 
			I-OrgUniv 		1.000 	0.500 	0.667 
			Overall			0.807 	0.748 	0.777 
			
			The overall performance of the result is reasonable at 77.7% for F1 measure. And 
		it seems that more specific category achieve a higher F1 measure. Meanwhile, “Beginning” 
		label (“B-”) tends to outperform “In” label (“I-”).
 
	CRF Method for Simple NER Tagger Evaluation (task2)
	
		Basic Logic:
			1.	Constructed a pipe called “ImprovedSimpleLabelNERdata2TokenSequence” to extract features 
				such as label/previousWord/nextWord/POStag/phrase in the input data
			2.	Push input data into the list of pipes and convert sub-category labels into O/PER/ORG/LOC
			3.	Use built-in CRF trainer in MALLET package to find most Likelihood label over test data
			4.	Iterate to find most optimal weights for each feature
			5.	Evaluate and report Precision/Recall/F1 value on each iterate
			
		Parameter Tested
			Label Connections (in TrainCRF.java)
			Feature Inclusion (in ImprovedSimpleLabelNERdata2TokenSequence.java)
			
		Files
			task2.java : thin wrapper of the program
			ImprovedSimpleLabelNERdata2TokenSequence. java : implements Pipe class in MALLET package 
				extract features in the training data
			TrainCRF.java : Import Data -> Process Data -> Call MALLET CRFTrainer -> Evaluate

		Result
			1. Label Connections 
							
											Overall F1		O		PER		ORG		LOC
			HalfLabelsConnected				0.7053			0.9837	0.7313	0.5596	0.5466
			ThreeQuarterLabelsConnected		0.7116			0.9828	0.7281	0.52	0.6154
			AllLabelsConnected				0.7976			0.987	0.8199	0.689	0.6947
			FullyConnectedState				0.7976			0.987	0.8199	0.689	0.6947

			
			With only 4 states (O/PER/ORG/LOC), the transition from state to state is a limited number. 
		Thus, removing a portion of label connections will compromise the result greatly. Under CRF 
		training, NER task can achieve upto 79.8% F1 measure for simple labels (taking only the adjacent 
		word into consideration).
		
			2. Feature Inclusion

											Overall F1		O		PER		ORG		LOC
			Self Features					0.5751			0.9664	0.6584	0.4044	0.2105
			Adjacent Word					0.7976			0.987	0.8199	0.689	0.6947
			Adjacent Word + Full Feature	0.9727			0.9984	0.9806	0.9541	0.9576

			In Self Features, the feature extraction step only takes the label/ POStag/ phrase features of the 
		word itself into consideration. In Adjacent Word, the feature extraction step takes adjacent word 
		order into account. In Adjacent Word + Full Feature, the feature extraction step takes all features for 
		the adjacent words including label/ POStag/ phrase features plus the word order.
			The result indicates, well of course, more features will lead to better results. But the efficiency 
		was compromised. Overall F1 measures for each trial are 0.5751, 0.7976, 0.9727 which, in some sense, 
		can match up to the state-of-art methods.

		
	
	CRF Method for Sub-class NER Tagger Evaluation (task3)
	
		Basic Logic:
			1.	Similar to task2.
			2.	Constructed a pipe called “ImprovedDetailedLabelNERdata2TokenSequence” to extract features 
				such as label/previousWord/nextWord/POStag/phrase in the input data and 
			3.	Maintain the subcategory for Org labels while converting all other labels to general categories

			
		Parameter Tested
			Label Connections (in TrainDetailedCRF.java)
			Feature Inclusion (in ImprovedDetailedLabelNERdata2TokenSequence.java)
			
		Files
			task3.java : thin wrapper of the program
			ImprovedDetailedLabelNERdata2TokenSequence. java : implements Pipe class in MALLET package 
				extract detailed features in the training data
			TrainDetailedCRF.java : Import Data -> Process Data -> Call MALLET CRFTrainer -> Evaluate

		Result
		
			Different Amount of Training Data as input
			
			Amount of Input Data	Overal	Org		OrgTeam	OrgCorp	OrgPolBody	OrgUniv	OrgCGroup
			20% Data				0.8486	1		0.9778	0.963	0.8889		0.9412	?
			40% Data				0.8668	1		0.9778	0.9873	0.8889		0.9412	?
			60% Data				0.8603	1		0.9545	0.9873	0.8889		0.9412	?
			80% Data				0.8604	1		0.9565	0.9873	0.8889		0.9412	?
			100% Data				0.8416	0.9825	0.9302	0.9367	0.8			0.9412	?

			Overall, the training is much more expensive to proceed. It seems there is an over-fitting 
		problem for sub-category labels. The reason might be the too much weight is added and CRF trainer 
		algorithm cannot reasonably resolve and optimize likelihood.


//////////////////////////////////////////////////////////////////////////