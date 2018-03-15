package First;

import java.io.*;

public class DT_Tester1 {

	//main
	public static void main(String[] args) throws IOException
	{
		//construct decision tree
		DecisionTree1 classifier = new DecisionTree1();
		
		//load training data
		classifier.loadTrainingData("DT_trainingFile1");
		
		//set classifier entropy measurement: "Gini", "Class", "Shannon"
		classifier.setEntropyRule("Shannon");
		
		//build decision tree, Entropy Measurement: "Gini", "Class", "..."(Shannon)
		classifier.buildTree();
		
		classifier.trainingError("DT_trainingFile1");
		classifier.leaveOneOutValidation("DT_trainingFile1");
		
		//classifier data
		classifier.classifyData("DT_testFile1", "DT_classifiedFile1");
		
		//classifier.validate("DT_validationFile1");
	}
}
