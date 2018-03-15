package First;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

//Application Scenario: bank wants to classify and make predictions about its loan applicants
//Attributes: credit score, income, sex, age, and marital status
//Classes: high risk, medium risk, low risk,and undermined

public class NearestNeighbor2 {

	/***************************************************************************************************/

	// training record class
	private class Record {
		private double[] attributes; // attributes of record
		private int className; // class of record

		// Constructor of record
		private Record(double[] attributes, int className) {
			this.attributes = attributes; // assign attributes
			this.className = className; // assign class
		}
	}

	/***************************************************************************************************/

	private ArrayList<Record> records; // list of training records
	private int numberRecords; // numbers of training records
	private int numberAttributes; // numbers of attributes
	private int numberClasses; // number of classes
	private int numberNeighbors; // number of nearest neighbors
	private String majorityRule; // majority rule used

	/***************************************************************************************************/

	// Constructor of classifier
	public NearestNeighbor2() {
		records = null; // initialize records to empty
		numberRecords = 0; // number of records, attributes
		numberAttributes = 0; // classes are zero
		numberClasses = 0;
		numberNeighbors = 0; // nearest neighbors, majority rule
		majorityRule = null;
	}

	/***************************************************************************************************/

	// Method to reset K value
	public void setNumberNeighbors(int k) {
		numberNeighbors = k;
	}

	/***************************************************************************************************/

	// Method to reset majorityRule value
	public void setMajorityRule(String rule) {

		if (rule.equals("weighted"))
			majorityRule = "weighted";
		// in case of mis-input
		else
			majorityRule = "unweighted";
	}

	/***************************************************************************************************/

	// Method loads training records from training file
	public void loadTrainingData(String trainingFile) throws IOException {
		Scanner inFile = new Scanner(new File(trainingFile));

		// read number of records, attributes, classes
		numberRecords = inFile.nextInt();
		numberAttributes = inFile.nextInt();
		numberClasses = inFile.nextInt();

		// read neighbors, majority rule
		numberNeighbors = inFile.nextInt();
		majorityRule = inFile.next();

		// empty list of records
		records = new ArrayList<Record>();

		// for each record
		for (int i = 0; i < numberRecords; i++) {
			// create attribute array
			double[] attributeArray = new double[numberAttributes];

			// read attributes and convert them to numerical form
			for (int j = 0; j < numberAttributes; j++) {
				String label = inFile.next();
				attributeArray[j] = convert(label, j + 1);
			}

			// read class and convert it to numerical form
			String label = inFile.next();
			int className = convert(label);

			// create record
			Record record = new Record(attributeArray, className);

			// add record to list of records
			records.add(record);
		}

		inFile.close();
	}

	/***************************************************************************************************/

	// Method reads test records from test file and writes classes
	// to classified file
	public void classifyData(String testFile, String classifiedFile) throws IOException {
		Scanner inFile = new Scanner(new File(testFile));
		PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile));

		// read number of records
		int numberRecords = inFile.nextInt();

		// for each record
		for (int i = 0; i < numberRecords; i++) {
			// create attribute array
			double[] attributeArray = new double[numberAttributes];

			// read attributes and convert them to numerical form
			for (int j = 0; j < numberAttributes; j++) {
				String label = inFile.next();
				attributeArray[j] = convert(label, j + 1);
			}

			// find class of attribute
			int className = classify(attributeArray);

			// find class label and write to output file
			String label = convert(className);
			System.out.println(label);
			outFile.println(label);

		}

		System.out.println();

		inFile.close();
		outFile.close();
	}

	/***************************************************************************************************/

	// Method finds class of given attributes
	private int classify(double[] attributes) {

		double[] distance = new double[numberRecords];
		int[] id = new int[numberRecords];

		// find distance between attributes and all records
		for (int i = 0; i < numberRecords; i++) {
			distance[i] = distance(attributes, records.get(i).attributes);
			id[i] = i;
		}

		// find the nearest neighbors
		nearestNeighbor(distance, id);

		// find majority class of neighbors
		int className = majority(id, attributes);

		// return class
		return className;
	}

	/***************************************************************************************************/

	// Overload Method for leave on out Validation
	// to finds class of given attributes
	private int classify(double[] attributes, ArrayList<Record> cutOutRecords) {

		int numberRecords = cutOutRecords.size();
		// System.out.println("#records " + numberRecords );

		double[] distance = new double[numberRecords];
		int[] id = new int[numberRecords];

		// find distance between attributes and all records
		for (int i = 0; i < numberRecords; i++) {
			distance[i] = distance(attributes, cutOutRecords.get(i).attributes);
			id[i] = i;
		}

		// find the nearest neighbors
		nearestNeighbor(distance, id, numberRecords);

		// find majority class of neighbors
		int className = majority(id, attributes, cutOutRecords);

		// return class
		return className;
	}

	/***************************************************************************************************/

	// Method finds the nearest neighbors
	private void nearestNeighbor(double[] distance, int[] id) {
		// sort the records by their distances and choose
		// the closest neighbor
		for (int i = 0; i < numberNeighbors; i++)
			for (int j = i; j < numberRecords; j++)
				if (distance[i] > distance[j]) {
					double tempDistance = distance[i];
					distance[i] = distance[j];
					distance[j] = tempDistance;

					int tempId = id[i];
					id[i] = id[j];
					id[j] = tempId;
				}
	}

	/***************************************************************************************************/

	// Overload Method for leave one out validation to
	// finds the nearest neighbors
	private void nearestNeighbor(double[] distance, int[] id, int newNumberReocrds) {
		// sort the records by their distances and choose
		// the closest neighbor
		for (int i = 0; i < numberNeighbors; i++)
			for (int j = i; j < newNumberReocrds; j++)
				if (distance[i] > distance[j]) {
					double tempDistance = distance[i];
					distance[i] = distance[j];
					distance[j] = tempDistance;

					int tempId = id[i];
					id[i] = id[j];
					id[j] = tempId;
				}
	}

	/***************************************************************************************************/

	// Method finds the majority class of nearest neighbors
	private int majority(int[] id, double[] attributes) {
		double[] frequency = new double[numberClasses];

		// class frequencies are zero initially
		for (int i = 0; i < numberClasses; i++)
			frequency[i] = 0;

		// if unweighed majority rule is used
		if (majorityRule.equals("unweighted")) {
			// each neighbor contributes 1 to its class
			for (int i = 0; i < numberNeighbors; i++)
				frequency[records.get(id[i]).className - 1] += 1;
		}
		// if weighted majority rule is used
		else {
			// each neighbor contributes 1/distance to its class
			for (int i = 0; i < numberNeighbors; i++) {
				double d = distance(records.get(id[i]).attributes, attributes);
				if (d == 0)
					frequency[records.get(id[i]).className - 1] += 1 / (d + 0.001);
				else
					frequency[records.get(id[i]).className - 1] += 1 / d;
			}
		}

		// find majority class
		int maxIndex = 0;
		for (int i = 0; i < numberClasses; i++)
			if (frequency[i] > frequency[maxIndex])
				maxIndex = i;

		return maxIndex + 1;
	}

	/***************************************************************************************************/

	// Overload Method for leave on out validation
	// to finds the majority class of nearest neighbors
	private int majority(int[] id, double[] attributes, ArrayList<Record> cutOutRecords) {
		double[] frequency = new double[numberClasses];

		// class frequencies are zero initially
		for (int i = 0; i < numberClasses; i++)
			frequency[i] = 0;

		// if unweighed majority rule is used
		if (majorityRule.equals("unweighted")) {
			// each neighbor contributes 1 to its class
			for (int i = 0; i < numberNeighbors; i++)
				frequency[cutOutRecords.get(id[i]).className - 1] += 1;
		}
		// if weighted majority rule is used
		else {
			// each neighbor contributes 1/distance to its class
			for (int i = 0; i < numberNeighbors; i++) {
				double d = distance(cutOutRecords.get(id[i]).attributes, attributes);
				if (d == 0)
					frequency[cutOutRecords.get(id[i]).className - 1] += 1 / (d + 0.001);
				else
					frequency[cutOutRecords.get(id[i]).className - 1] += 1 / d;
			}
		}

		// find majority class
		int maxIndex = 0;
		for (int i = 0; i < numberClasses; i++)
			if (frequency[i] > frequency[maxIndex])
				maxIndex = i;

		return maxIndex + 1;
	}

	/***************************************************************************************************/

	// Method that compute the training error
	public double trainingError(String trainingFile) throws IOException {

		// shallow copy the training file records
		if (records == null)
			loadTrainingData(trainingFile);

		// count the training error frequency
		int numberErrors = 0;
		for (int i = 0; i < numberRecords; i++) {
			// find class of attribute
			int predictedClass = classify(records.get(i).attributes);

			// find class label and write to output file
			if (predictedClass != records.get(i).className)
				numberErrors++;
		}

		double errorRate = 100.0 * numberErrors / numberRecords;
		System.out.println("k = " + numberNeighbors + "\t" + "Training Error: \t\t" + errorRate + " percent error");
		
		return errorRate;

	}

	/***************************************************************************************************/
	// Method Leave One Out validates classifier using validation file and
	// displays error rate
	public double leaveOneOutValidate(String trainingFile) throws IOException {

		if (records == null)
			loadTrainingData(trainingFile);

		// initially zero errors
		int numberErrors = 0;

		// for each records
		for (int i = 0; i < numberRecords; i++) {

			Record theOne = records.get(i);

			// shallow copy the training data
			ArrayList<Record> cutOutRecords = new ArrayList<>(records);
			cutOutRecords.remove(i);

			int predictedClass = classify(theOne.attributes, cutOutRecords);

			if (predictedClass != theOne.className)
				numberErrors++;

		}

		// find and print error rate
		double errorRate = 100.0 * numberErrors / numberRecords;
		System.out.println(
				"k = " + numberNeighbors + "\t" + "Leave One Out Validation Error: " + errorRate + " percent error");
		
		return errorRate;

	}

	/***************************************************************************************************/

	// Method validates classifier using validation file and displays error rate
	public void validate(String validationFile) throws IOException {
		Scanner inFile = new Scanner(new File(validationFile));

		// read number of records
		int numberRecords = inFile.nextInt();

		// initially zero errors
		int numberErrors = 0;

		// for each records
		for (int i = 0; i < numberRecords; i++) {
			double[] attributeArray = new double[numberAttributes];

			// read attributes
			for (int j = 0; j < numberAttributes; j++) {
				String label = inFile.next();
				attributeArray[j] = convert(label, j + 1);
			}

			// read actual class
			String label = inFile.next();
			int actualClass = convert(label);

			// find class predicted by classifier
			int predictedClass = classify(attributeArray);

			// error if predicted and actual classes do not match
			if (predictedClass != actualClass)
				numberErrors += 1;
		}

		// find and print error rate
		double errorRate = 100.0 * numberErrors / numberRecords;
		System.out.println(errorRate + " percent error");

		inFile.close();
	}

	/***************************************************************************************************/

	// Method converts attribute value to numerical values. Hard coded for
	// specific application
	// example: 720 87 77 male single low
	// credit score: 500-900
	// income: 30k-90k
	// age: 30-80
	// sex: male, female
	// marital status: single, divorced, married
	// class: low risk, medium risk, high risk, undetermined
	private double convert(String label, int column) {

		double value;

		// convert credit score(500-900) attribute to 0/1
		if (column == 1) {
			value = Double.valueOf(label);
			value = (value - 500.0) / (900.0 - 500.0);
		}
		// convert income(30k-90k) attribute to [0, 1] range
		else if (column == 2) {
			value = Double.valueOf(label);
			value = (value - 30.0) / (90.0 - 30.0);
		}
		//convert age(30-80) to [0, 1] range
		else if(column == 3){
			value = Double.valueOf(label);
			value = (value - 30.0)/(80.0 - 30.0);
		}
		// convert sex(male, female) to 1, 0
		else if(column == 4){
			if (label.equals("male"))
				value = 1.0;
			else 
				value = 0.0;
		}
		// convert marital(single, divorced, married) to 0, 1, 2
		else{
			if(label.equals("single"))
				value = 0.0;
			else if(label.equals("divorced"))
				value = 1.0;
			else
				value = 2.0;
		}

		return value;
	}

	/***************************************************************************************************/

	// Method converts class labels to integer values. Hard coded for specific
	// application
	private int convert(String label) {
		int value;

		if (label.equals("low"))
			value = 1;
		else if (label.equals("medium"))
			value = 2;
		else if (label.equals("high"))
			value = 3;
		else
			value = 4;

		return value;
	}

	/***************************************************************************************************/

	// Method converts integer values to class labels. Hard coded for specific
	// application
	private String convert(int value) {
		String label;

		if (value == 1)
			label = "low";
		else if (value == 2)
			label = "medium";
		else if (value == 3)
			label = "high";
		else
			label = "undetermined";

		return label;
	}

	/***************************************************************************************************/

	// Method finds distance between two records. Hard coded for specific
	// application
	private double distance(double[] u, double[] v) {
		
		double distance = 0;
		
		//heterogeneous attributes in similarity
		for (int i = 0; i < u.length; i++)
			// attributes col == 1, 2, 3 => euclidean distance
			if( i == 0 || i == 1 || i == 2)
				distance = distance + (u[i] - v[i]) * (u[i] - v[i]);
			// attributes col == 4, 5 => matching coefficent distance (0, 1)
			else{
				if(u[i] != v[i])
					distance += 1.0;
			}

		distance = Math.sqrt(distance);
		
		
		return distance;
		
	}

}
