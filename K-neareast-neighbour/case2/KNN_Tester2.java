package First;

import java.io.IOException;

public class KNN_Tester2 {

	public static void main(String[] args) throws IOException {

		String bestRule = "";
		int bestK = 0;
		double bestErrorRate = 999.9;
		double trainingErrorRate = 999.9;
		for (int i = 1; i < 50; i++) {

			double errorRate;

			errorRate = testCase(i, "weighted")[0];
			if (errorRate < bestErrorRate) {
				bestErrorRate = errorRate;
				bestRule = "weighted";
				bestK = i;
				trainingErrorRate = testCase(i, "weighted")[1];
			}

			errorRate = testCase(i, "unweighted")[0];
			if (errorRate < bestErrorRate) {
				bestErrorRate = errorRate;
				bestRule = "unweighted";
				bestK = i;
				trainingErrorRate = testCase(i, "unweighted")[1];
			}
		}

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("Training Error:\t\t\t" + trainingErrorRate);
		System.out.println("Best Validation Error Rate:\t" + bestErrorRate);
		System.out.println("Best Rule:\t\t\t" + bestRule);
		System.out.println("Best K value:\t\t\t" + bestK);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
		
		//make the classified File for best situation
		testCase(bestK, bestRule);

	}

	//return validation Error Rate
	public static double[] testCase(int k, String rule) throws IOException {

		double validationErrorRate = 0.0;
		double trainingErrorRate = 0.0;

		// construct nearest neighbor classifier
		NearestNeighbor2 classifier = new NearestNeighbor2();

		// load training data
		classifier.loadTrainingData("trainingFile_KNN2");

		// if k == 0, do not change the parameter
		if (k != 0) {

			classifier.setNumberNeighbors(k);
			classifier.setMajorityRule(rule);

		}

		// training error
		trainingErrorRate = classifier.trainingError("trainingFile_KNN2");

		// leave one out validation error
		validationErrorRate = classifier.leaveOneOutValidate("trainingFile_KNN2");

		// classify data
		classifier.classifyData("testFile_KNN2", "classifiedFile_KNN2");

		return new double[]{validationErrorRate, trainingErrorRate} ;

	}

}
