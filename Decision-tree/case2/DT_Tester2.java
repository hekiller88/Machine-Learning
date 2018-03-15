package First;

import java.io.*;

public class DT_Tester2 {

	//main
	public static void main(String[] args) throws IOException
	{
		//construct decision tree
		DecisionTree2 classifier = new DecisionTree2();
		
		//load training data
		classifier.loadTrainingData("DT_trainingFile2");
		
		//set Entropy Rule : Gini, Class, Shannon
		classifier.setEntropyRule("Gini");
		
		//build decision tree	
		classifier.buildTree();
		
		classifier.trainingError("DT_trainingFile2");
		classifier.leaveOneOutValidation("DT_trainingFile2");
		
		//classifier data
		classifier.classifyData("DT_testFile2", "DT_classifiedFile2");
		
	}
}
