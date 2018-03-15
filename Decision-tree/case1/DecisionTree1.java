package First;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DecisionTree1 {

	/***************************************************************************************************/

	// Training record class
	private class Record {

		private int[] attributes; // attributes of record
		private int className; // class of record

		// constructor of record
		private Record(int[] attributes, int className) {
			this.attributes = attributes;
			this.className = className;
		}
	}

	/***************************************************************************************************/

	// Decision tree node class
	private class Node {

		private String nodeType; // node type - internal | leaf
		private int condition; // condition if node is internal
		private int className; // class name if node is leaf
		private Node left; // left branch
		private Node right; // right branch
		private double confidenceMaj; // # majority class records in leaf / #
										// records in leaf
		private double confidenceRec; // # records in leaf / # total number of
										// records

		// Constructor of node
		private Node(String nodeType, int value, Node left, Node right) {
			this.nodeType = nodeType; // assign node type
			this.left = left; // assign left branch
			this.right = right; // assign right branch

			this.confidenceMaj = -1;
			this.confidenceRec = -1;

			if (nodeType.equals("internal")) { // if node is internal
				condition = value; // assign condition to node
				className = -1; // node has no condition

			} else {
				condition = -1;
				className = value; // vice-versa
			}
		}

		// another constructor to compute confidence level
		private Node(String nodeType, int value, Node left, Node right, double confidenceMaj, double confidenceRec) {

			this.nodeType = nodeType; // assign node type
			this.left = left; // assign left branch
			this.right = right; // assign right branch

			if (nodeType.equals("internal")) { // if node is internal
				condition = value; // assign condition to node
				className = -1; // node has no condition

				this.confidenceMaj = -1;
				this.confidenceRec = -1;

			} else {
				condition = -1;
				className = value; // vice-versa

				this.confidenceMaj = confidenceMaj;
				this.confidenceRec = confidenceRec;
			}

		}
	}

	/***************************************************************************************************/

	private Node root; // root of decision tree
	private ArrayList<Record> records; // list of training records
	private ArrayList<Integer> attributes; // list of attributes
	private int numberRecords; // number of training records
	private int numberAttributes; // number of attributes
	private int numberClasses; // number of classes
	private String entropyRule;	//measurement of entropy: Gini, Class, Shannon

	/***************************************************************************************************/

	// Constructor of decision tree
	public DecisionTree1() {
		root = null; // initialize root, records,
		records = null; // attributes to empty
		attributes = null;
		numberRecords = 0;
		numberAttributes = 0;
		numberClasses = 0;
		entropyRule = "Gini";
	}

	/***************************************************************************************************/
	
	public void setEntropyRule(String entropyRule)
	{
		this.entropyRule = entropyRule;
	}
	
	/***************************************************************************************************/
	
	

	// Method for leave on out validation,
	// cut the specific record
	public void cutRecord(int index) {

		// error check
		if (records == null) {
			System.out.println("Error: the tree is empty.");
		}
		
		//in case the index record is the only class in all Records
		boolean isIndexTheOnlyClass = true;
		for(int i = 0; i < numberRecords; i++)
		{
			if(records.get(i).className == records.get(index).className){
				isIndexTheOnlyClass = false;
				break;
			}
		}
		if(isIndexTheOnlyClass)
			numberClasses--;

		records.remove(index);
		numberRecords--;
	}

	/***************************************************************************************************/

	// Method builds decision tree for the whole training data
	public void buildTree() {
		
		root = build(records, attributes); // initial call to build method
		
	}

	/***************************************************************************************************/

	// Method builds decision tree from given records and attributes, returns
	// root of tree that is build
	private Node build(ArrayList<Record> records, ArrayList<Integer> attributes) {

		// root node is empty initially
		Node node = null;

		// if all records have same class
		if (sameClass(records)) {
			
			//debug
			//System.out.println("Leaf: same class");
			
			// find class name
			int className = records.get(0).className;

			double confidenceRec = 100.0 * records.size() / numberRecords;
			double confidenceMaj = 100.0; // all with same class

			// node is leaf with that class
			node = new Node("leaf", className, null, null, confidenceMaj, confidenceRec);
		}
		// if there are no attributes
		else if (attributes.isEmpty()) {
			
			//debug
			//System.out.println("Leaf: empty attribute");

			int[] tmp = majorityClass(records);
			
			// find majority class of records
			int className = tmp[0];
			
			//find majority class amount
			int numberMaj = tmp[1];
			double confidenceMaj = 100.0 * numberMaj / records.size();
			double confidenceRec = 100.0 * records.size() / numberRecords; 
			
			// node is leaf with that class
			node = new Node("leaf", className, null, null, confidenceMaj, confidenceRec);

		} else {
			// find best condition for current records and attributes
			int condition = bestCondition(records, attributes);

			// collect all records which have 0 for condition
			ArrayList<Record> leftRecords = collect(records, condition, 0);

			// collect all records which have 1 for condition
			ArrayList<Record> rightRecords = collect(records, condition, 1);

			// if either left records or right is empty
			if (leftRecords.isEmpty() || rightRecords.isEmpty()) {
				
				//debug
				//System.out.println("Leaf: empty left || right");
				
				int[] tmp = majorityClass(records);
				
				// find majority class of records
				int className = tmp[0];
				
				//find majority class amount
				int numberMaj = tmp[1];
				double confidenceMaj = 100.0 * numberMaj / records.size();
				double confidenceRec = 100.0 * records.size() / numberRecords; 
				
				// node is leaf with that class
				node = new Node("leaf", className, null, null, confidenceMaj, confidenceRec);
			} else {
				// create copies of current attributes
				ArrayList<Integer> leftAttributes = new ArrayList<>(attributes);
				ArrayList<Integer> rightAttributes = new ArrayList<>(attributes);

				// remove best condition from current attributes
				leftAttributes.remove(new Integer(condition));
				rightAttributes.remove(new Integer(condition));

				// create internal node with the best condition
				node = new Node("internal", condition, null, null);

				// create left subtree recursively
				node.left = build(leftRecords, leftAttributes);

				// create right subtree recursively
				node.right = build(rightRecords, rightAttributes);

			}
		}

		// return root node of tree that is built
		return node;

	}

	/***************************************************************************************************/

	// Method decides whether all records have the same class
	private boolean sameClass(ArrayList<Record> records) {
		// compare class of each record with class of first record
		for (int i = 0; i < records.size(); i++)
			if (records.get(i).className != records.get(0).className)
				return false;

		return true;
	}

	/***************************************************************************************************/

	// Method finds the majority class of records
	/*
	 * @output: [0] majority class name
	 * 			[1] majority class amount
	 */
	private int[] majorityClass(ArrayList<Record> records) {
		int[] frequency = new int[numberClasses]; // frequency array

		for (int i = 0; i < numberClasses; i++) // ini frequency array
			frequency[i] = 0;

		for (int i = 0; i < records.size(); i++) // find frequency of classes
			frequency[records.get(i).className - 1] += 1;

		int maxIndex = 0; // find class with max frequency
		for (int i = 0; i < numberClasses; i++)
			if (frequency[i] > frequency[maxIndex])
				maxIndex = i;

		// # of majority class => frequency[maxIndex]
		return new int[] { maxIndex + 1, frequency[maxIndex] };
	}

	/***************************************************************************************************/

	// Method collects records that have a given value for a given attribute
	private ArrayList<Record> collect(ArrayList<Record> records, int condition, int value) {
		// initialize collection
		ArrayList<Record> result = new ArrayList<>();

		// go thru records and collect those that that have given value
		// for given attribute
		for (int i = 0; i < records.size(); i++)
			if (records.get(i).attributes[condition - 1] == value)
				result.add(records.get(i));

		// return collection
		return result;
	}

	/***************************************************************************************************/

	// Method finds best condition for given records and attributes
	private int bestCondition(ArrayList<Record> records, ArrayList<Integer> attributes) {
		// evaluate first attribute
		double minValue = evaluate(records, attributes.get(0));
		int minIndex = 0;

		// go thru all attributes
		for (int i = 0; i < attributes.size(); i++) {
			double value = evaluate(records, attributes.get(i));

			if (value < minValue) {
				minValue = value; // if value is less then
				minIndex = i; // current min then update min
			}
		}

		return attributes.get(minIndex);
	}

	/***************************************************************************************************/

	// Method evaluates an attribute using weighted average entropy
	private double evaluate(ArrayList<Record> records, int attribute) {
		// collect records that have attribute value 0
		ArrayList<Record> leftRecords = collect(records, attribute, 0);

		// collect records that have attribute value 1
		ArrayList<Record> rightRecords = collect(records, attribute, 1);

		// find entropy of left records
		double entropyLeft = entropy(leftRecords);

		// find entropy of right records
		double entropyRight = entropy(rightRecords);

		// find weighted average entropy
		double average = entropyLeft * leftRecords.size() / records.size()
				+ entropyRight * rightRecords.size() / records.size();

		// return weighted average entropy
		return average;
	}

	/***************************************************************************************************/

	// Method finds entropy of records using gini measure
	// option = gini entropy
	//			class error entropy
	//			Shannon's entropy
	private double entropy(ArrayList<Record> records) {
		
		//System.out.println("Enter: " + entropyRule);
		
		if(entropyRule.equals("Gini"))	//gini(s) = 1 - (p1^2 + p2^2 + p3^3 + ...)
		{
			double[] frequency = new double[numberClasses]; // frequency array

			for (int i = 0; i < numberClasses; i++)
				frequency[i] = 0;

			for (int i = 0; i < records.size(); i++) // find class freq
				frequency[records.get(i).className - 1] += 1;

			for (int i = 0; i < numberClasses; i++) // normalize freq
				frequency[i] = frequency[i] / records.size() * 1.0;

			double sum = 0;
			for (int i = 0; i < numberClasses; i++)
				sum = sum + frequency[i] * frequency[i];

			return 1 - sum;
		}
		else if(entropyRule.equals("Class"))	//class(s) = 1 - max(p1, p2, p3, ...)
		{
			double[] frequency = new double[numberClasses]; // frequency array

			for (int i = 0; i < numberClasses; i++)
				frequency[i] = 0;

			for (int i = 0; i < records.size(); i++) // find class freq
				frequency[records.get(i).className - 1] += 1;
			
			int maxIndex = 0;
			for (int i = 0; i < numberClasses; i++) // normalize freq
				if(frequency[i] > frequency[maxIndex])
					maxIndex = i;

			return 1 - frequency[maxIndex];
		}
		else	//shannon(s) = -(p1logp1 + p2logp2 + ...)
		{
			double[] frequency = new double[numberClasses]; // frequency array

			for (int i = 0; i < numberClasses; i++)
				frequency[i] = 0;

			for (int i = 0; i < records.size(); i++) // find class freq
				frequency[records.get(i).className - 1] += 1;
			
			double sum = 0;
			
			for(int i = 0; i < numberClasses; i++)
				sum -= frequency[i] * (Math.log((double)frequency[i] /Math.log(2.0)));
			
			return sum;
		}
		
	}

	/***************************************************************************************************/

	// Method finds class of given attributes
	private int classify(int[] attributes) {

		// start at root node
		Node current = root;

		// go down the tree
		while (current.nodeType.equals("internal")) {
			// 0 - go left, 1 - go right
			if (attributes[current.condition - 1] == 0)
				current = current.left;
			else
				current = current.right;
		}

		return current.className;
	}
	
	/***************************************************************************************************/
	
	// Method finds tree leaf of given attributes
		private Node classifyGetNode(int[] attributes) {

			// start at root node
			Node current = root;

			// go down the tree
			while (current.nodeType.equals("internal")) {
				// 0 - go left, 1 - go right
				if (attributes[current.condition - 1] == 0)
					current = current.left;
				else
					current = current.right;
			}

			return current;
		}

	/***************************************************************************************************/

	// Method loads training records from training file
	// in file3 attributes are [0,1]
	// class are [1,2,3,4]
	public void loadTrainingData(String trainingFile) throws IOException {
		Scanner inFile = new Scanner(new File(trainingFile));

		// read number of records, attributes, classes
		numberRecords = inFile.nextInt();
		numberAttributes = inFile.nextInt();
		numberClasses = inFile.nextInt();

		// empty list of records
		records = new ArrayList<Record>();

		// for each record
		for (int i = 0; i < numberRecords; i++) {

			// create attribute array
			int[] attributeArray = new int[numberAttributes];

			// for each attribute
			for (int j = 0; j < numberAttributes; j++) {

				attributeArray[j] = inFile.nextInt();

			}

			// read class
			int className = inFile.nextInt();

			// create record using attributes and class
			Record record = new Record(attributeArray, className);

			// add record to list
			records.add(record);

		}

		// create list of attributes
		attributes = new ArrayList<Integer>();
		for (int i = 0; i < numberAttributes; i++)
			attributes.add(i + 1);

		inFile.close();
	}

	/***************************************************************************************************/

	// Method reads test records from test file and writes classified records
	// to classified file
	// in file3 attributes are [0,1]
	// class are [1,2,3,4]
	public void classifyData(String testFile, String classifiedFile) throws IOException {
		Scanner inFile = new Scanner(new File(testFile));
		PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile));

		// read number of records
		int numberRecords = inFile.nextInt();

		// for each record
		for (int i = 0; i < numberRecords; i++) {
			// create attribute array
			int[] attributeArray = new int[numberAttributes];

			// read attributes
			for (int j = 0; j < numberAttributes; j++) {
				attributeArray[j] = inFile.nextInt();
			}

			// find class of attributes
//			int className = classify(attributeArray);
			Node found = classifyGetNode(attributeArray);

			outFile.println("Class: " + found.className 
							+ "\tConfidence Fraction of Records in Leaf:  " + String.format("%.2f", found.confidenceRec)
							+ "\n\t\tConfidence Fraction of Majority in Leaf: " + String.format("%.2f", found.confidenceMaj));
			
			System.out.println("Class: " + found.className 
					+ "\tConfidence Fraction of Records in Leaf:  " + String.format("%.2f", found.confidenceRec)
					+ "\n\t\tConfidence Fraction of Majority in Leaf: " + String.format("%.2f", found.confidenceMaj));
		}

		inFile.close();
		outFile.close();
	}

	/***************************************************************************************************/

	// Method that computes the training error
	public double trainingError(String trainingFile) throws IOException {

		// in case didn't load training file
		if (records == null || attributes == null) {
			loadTrainingData(trainingFile);
			buildTree();
		}

		// initialize number of errors
		int numberErrors = 0;

		for (int i = 0; i < numberRecords; i++) {
			// find class of attribute
			int predictedClass = classify(records.get(i).attributes);

			// find class label and write to output file
			if (predictedClass != records.get(i).className)
				numberErrors++;
		}

		double errorRate = 100.0 * numberErrors / numberRecords;
		System.out.println("Training Error:\t\t" + String.format("%.2f", errorRate) + " percent error");
		
		return errorRate;
	}

	/***************************************************************************************************/
	// Method to get leave one out validation error rate
	public double leaveOneOutValidation(String trainingFile) throws IOException {

		// in case didn't load training file
		if (records == null || attributes == null) {
			loadTrainingData(trainingFile);
			buildTree();
		}

		// initialize number of errors
		int numberErrors = 0;

		for (int i = 0; i < numberRecords; i++) {
			
			Record theOne = records.get(i);

			DecisionTree1 cutOutTree = new DecisionTree1();
			cutOutTree.loadTrainingData(trainingFile);
			cutOutTree.setEntropyRule(entropyRule);
			cutOutTree.cutRecord(i);
			cutOutTree.buildTree();

			int predictedClass = cutOutTree.classify(theOne.attributes);

			if (predictedClass != theOne.className)
				numberErrors++;
		}

		// find and print error rate
		double errorRate = 100.0 * numberErrors / numberRecords;
		System.out.printf("LOO Validation Error:\t%.2f percent error\n", errorRate);
		
		return errorRate;

	}

	/***************************************************************************************************/

	// Method validates decision tree using validation file and displays
	// error rate
	// in file3 attributes are [0,1]
	// class are [1,2,3,4]
	public void validate(String validationFile) throws IOException {
		Scanner inFile = new Scanner(new File(validationFile));

		// read number of records
		int numberRecords = inFile.nextInt();

		// initialize number of errors
		int numberErrors = 0;

		// for each record
		for (int i = 0; i < numberRecords; i++) {
			// create attribute array
			int[] attributeArray = new int[numberAttributes];

			// read attributes and convert to binary
			for (int j = 0; j < numberAttributes; j++) {
				String label = inFile.next();
				attributeArray[j] = convert(label, j + 1);
			}

			// read actual class from validation file
			String label = inFile.next();
			int actualClass = convert(label);

			// find class predicted by decision tree
			int predictedClass = classify(attributeArray);

			// error if predicted and actual classes do not match
			if (predictedClass != actualClass)
				numberErrors++;
		}

		// find and print error rate
		double errorRate = 100.0 * numberErrors / numberRecords;
		System.out.println(errorRate + " percent error\n");

		inFile.close();
	}

	/***************************************************************************************************/

	// Method converts attributes labels to binary values, hard coded for
	// specific application
	private int convert(String label, int column) {
		int value;

		// convert attribute labels to binary values
		if (column == 1)
			if (label.equals("highschool"))
				value = 0;
			else
				value = 1;
		else if (column == 2)
			if (label.equals("smoker"))
				value = 0;
			else
				value = 1;
		else if (column == 3)
			if (label.equals("married"))
				value = 0;
			else
				value = 1;
		else if (column == 4)
			if (label.equals("male"))
				value = 0;
			else
				value = 1;
		else if (label.equals("works"))
			value = 0;
		else
			value = 1;

		return value;
	}

	/***************************************************************************************************/

	// Method converts class labels to integer values, hard coded for
	// specific application
	private int convert(String label) {
		int value;

		// convert class labels to integer values
		if (label.equals("highrisk"))
			value = 1;
		else if (label.equals("mediumrisk"))
			value = 2;
		else if (label.equals("lowrisk"))
			value = 3;
		else
			value = 4;

		return value;
	}

	/***************************************************************************************************/

	// Method converts integer values to class labels, hard coded for
	// specific application
	private String convert(int value) {
		String label;

		// convert integer values to class labels
		if (value == 1)
			label = "highrisk";
		else if (value == 2)
			label = "mediumrisk";
		else if (value == 3)
			label = "lowrisk";
		else
			label = "undetermined";

		return label;
	}

}
