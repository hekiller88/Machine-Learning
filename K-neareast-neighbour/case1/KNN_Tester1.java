package First;

import java.io.IOException;

public class KNN_Tester1 {

	public static void main(String[] args) throws IOException {

		// test: weighted | k = 3, 10, 20
		System.out.println("Rule - Weighted ===============> Start\n");
		testCase(3, "weighted");
		testCase(5, "weighted");
		testCase(10, "weighted");
		testCase(20, "weighted");
		System.out.println("Rule - Weighted ===============> End\n");

		// test: unweighted | k = 3, 5, 10, 20
		System.out.println("Rule - Uneighted ===============> Start\n");
		testCase(3, "unweighted");
		testCase(5, "unweighted");
		testCase(10, "unweighted");
		testCase(20, "unweighted");
		System.out.println("Rule - Unweighted ===============> End\n");
	}

	public static void testCase(int k, String rule) throws IOException {

		// construct nearest neighbor classifier
		NearestNeighbor1 classifier = new NearestNeighbor1();

		// load training data
		classifier.loadTrainingData("trainingFile_KNN1");

		// if k == 0, do not change the parameter
		if (k != 0) {

			classifier.setNumberNeighbors(k);
			classifier.setMajorityRule(rule);

		}
		
		// validation
		//classifier.validate("validationFile_KNN1");
		
		// training error
		classifier.trainingError("trainingFile_KNN1");

		// leave one out validation error
		classifier.leaveOneOutValidate("trainingFile_KNN1");

		// classify data
		classifier.classifyData("testFile_KNN1", "classifiedFile_KNN1");

	}

}
