/**
 * Prechar Xiong
 * 11/05/19
 * ICS 462-01
 * Assignment 5
 * 
 * This program creates a PageFault class that implements the FIFO, LRU, and Optimal
 * page replacement algorithms on a string page reference, with a given page frame.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class PageFault {

	/**
	 * this method generates a random string page reference that contains 
	 * 20 integers between 0 and 9.
	 * @return random string page reference
	 */
	public static String randomString() {
		String reference = "";
		for(int i = 0; i < 20; i++) {
			reference += randomInt();
			if(i != 19) {
				reference += ",";
			}
		}
		return reference;
	}

	/**
	 * this method generates a random integer between 0 and 9 and 
	 * returns it
	 * @return a random number between 0 and 9
	 */
	public static String randomInt() {
		Random random = new Random();
		int randomInt = random.nextInt(10);
		String randomString = Integer.toString(randomInt);
		return randomString;
	}

	/**
	 * This method converts the string page reference into an array
	 * @param reference, String page reference
	 * @return newReferenceArray, An array of the string page reference
	 */
	public static int[] referenceIntArray(String reference) {
		String[] referenceArray = reference.split(",");
		int[] newReferenceArray = new int[referenceArray.length];
		for(int i = 0; i < newReferenceArray.length; i++) {
			newReferenceArray[i] = Integer.parseInt(referenceArray[i]);
		}
		return newReferenceArray;
	}

	/**
	 * This method converts the string page reference into a Queue
	 * @param reference, String page reference
	 * @return referenceQueue, A Queue of the string page reference
	 */
	public static Queue<Integer> referenceIntQueue(String reference){
		String[] referenceArray = reference.split(",");
		Queue<Integer> referenceQueue = new LinkedList<Integer>();
		for(int i = 0; i < referenceArray.length; i++) {
			referenceQueue.offer((Integer.parseInt(referenceArray[i])));
		}
		return referenceQueue;
	}

	/**
	 * This methods creates an output.txt file if it doesn't already exist, to output
	 * the numbers read by the consumer, output when the consumer waits and when the consumer
	 * finishes.
	 */
	public static void createFile() {
		try {
			FileWriter fileWriter;
			File file = new File("Assignment5-Output.txt");
			PrintWriter printWriter;
			if(file.exists()) {
				fileWriter = new FileWriter(file.getAbsoluteFile(), true);
				printWriter = new PrintWriter(fileWriter);
			} else {
				fileWriter = new FileWriter(file);
				printWriter = new PrintWriter(fileWriter);
				printWriter.println("Prechar Xiong \nICS 462 Assignment #5 \n");
			}
			printWriter.close();
		}	catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method takes in a string and prints it to the output file
	 * @param msg is the String to be printed to the file
	 */
	public static void printToFile(String msg) {
		try {
			File file = new File("Assignment5-Output.txt");
			FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(msg);
			printWriter.close();
		}	catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method finds the longest unused integer and returns it. It is a
	 * helper method for the Optimal Page Replacement algorithm
	 * @param framesQue,the Queue containing the page frames
	 * @param referenceQue, the Queue containing the numbers in the reference string
	 * @return returnValue, which is the longest unused integer
	 */
	public static int longestUnusedInt(Queue<Integer> framesQue, Queue<Integer> referenceQue) {
		Queue<Integer> notFound = new LinkedList<Integer>(framesQue); //copy the framesQue to find which value goes unused the longest
		int returnValue = framesQue.peek();

		for(Integer reference : referenceQue) { //for each Integer reference in the referenceQue
			for(Integer frames : framesQue) { //for each Integer frames in the framesQue
				if(frames.equals(reference) && notFound.contains(frames)) { //if the reference is equal to the frames and the notFound Queue contains it
					returnValue = frames; //set the return value to the frames
					notFound.remove(frames); //remove the frames
					if(notFound.isEmpty()) { //if the notFound Queue is empty then break out of the loop
						break;
					}
				}
			}
		}

		if(!notFound.isEmpty()) { //if the notFound Queue still contains Integers then return the first one
			returnValue = notFound.poll();
		}

		return returnValue;
	}

	/**
	 * This fifo method performs the FIFO page replacement on the
	 * given String given the number of frames.
	 * @param frames, the number of page frames
	 * @param reference, the string page reference
	 * The method will print the number of pageFaults to the console and
	 * to the output file.
	 */
	public static void fifo(int frames, String reference){
		int pageFault = 0;
		int[] referenceArray = referenceIntArray(reference);
		Queue<Integer> framesQue = new LinkedList<Integer>(); //Queue is used since it is FIFO
		int count = 0;
		for(int i = 0; i < referenceArray.length; i++) {
			if(count < frames) { //Store numbers that the framesQue does not already have from the referenceArray until its full
				if(!framesQue.contains(referenceArray[i])) {
					framesQue.offer(referenceArray[i]);
					pageFault++;
					count++;
				} 
			} else if(!framesQue.contains(referenceArray[i])) { //Check to see if the Queue has the number from the referenceArray
				pageFault++;
				framesQue.poll(); //if the Queue does not have the number then poll the first value from the framesQue to make space
				framesQue.offer(referenceArray[i]); //insert the number from the referenceArray into the framesQue
			}
			//System.out.println(framesQue); //This was for testing purposes
		}

		System.out.println("     FIFO had " + pageFault + " page faults.\n");
		printToFile("     FIFO had " + pageFault + " page faults.\n");
	}

	/**
	 * This lru method performs the LRU page replacement on the
	 * given String given the number of frames.
	 * @param frames, the number of page frames
	 * @param reference, the string page reference
	 * The method will print the number of pageFaults to the console and
	 * to the output file.
	 */
	public static void lru(int frames, String reference) {
		int pageFault = 0;
		int[] referenceArray = referenceIntArray(reference);
		Queue<Integer> framesQue = new LinkedList<Integer>();
		int count = 0;
		for(int i = 0; i < referenceArray.length; i++) {
			if(count < frames) {
				if(!framesQue.contains(referenceArray[i])) { //Store numbers that the framesQue does not already have from the referenceArray until its full
					framesQue.offer(referenceArray[i]);
					pageFault++;
					count++;
				} else { //If the framesQue contains that number then remove that number from the framesQue and offer it back into the framesQue
					framesQue.remove(referenceArray[i]);
					framesQue.offer(referenceArray[i]);
				}
			} else if(!framesQue.contains(referenceArray[i])) { //Check to see if the Queue has the number from the referenceArray
				pageFault++;
				framesQue.poll(); //if the Queue does not have the number then poll the first value from the framesQue to make space
				framesQue.offer(referenceArray[i]); //insert the number from the referenceArray into the framesQue
			} else { //If the framesQue contains that number then remove that number from the framesQue and offer it back into the framesQue
				framesQue.remove((Object)referenceArray[i]);
				framesQue.offer(referenceArray[i]);
			}
			//System.out.println(framesQue); //This was for testing purposes
		}

		System.out.println("     LRU had " + pageFault + " page faults.\n");
		printToFile("     LRU had " + pageFault + " page faults.\n");
	}

	/**
	 * This optimal method performs the Optimal page replacement on the
	 * given String given the number of frames.
	 * @param frames, the number of page frames
	 * @param reference, the string page reference
	 * The method will print the number of pageFaults to the console and
	 * to the output file.
	 */
	public static void optimal(int frames, String reference) {
		int pageFault = 0;
		Queue<Integer> referenceQue = referenceIntQueue(reference);
		Queue<Integer> framesQue = new LinkedList<Integer>();
		int count = 0;
		int unused;
		int numPoll;
		for(int i = 0; i < 20; i++) {
			numPoll = referenceQue.poll();
			if(count < frames) {
				if(!framesQue.contains(numPoll)) { //Store numbers that the framesQue does not already have from the referenceArray until its full
					framesQue.offer(numPoll);
					pageFault++;
					count++;
				} 
			} else if(!framesQue.contains(numPoll)) { //Check to see if the Queue has the number from the referenceArray
				pageFault++;
				unused = longestUnusedInt(framesQue,referenceQue); //uses the longestUnusedInt method to find the number that will be unused the longest in the future
				framesQue.remove((Integer) unused); //remove from the framesQue the longest unused integer to make space for the new Integer
				framesQue.offer(numPoll); //offer the new Integer to the framesQue
			}
			//System.out.println(framesQue); //This was for testing purposes
		}

		System.out.println("     Optimal had " + pageFault + " page faults.\n");
		printToFile("     Optimal had " + pageFault + " page faults.\n");
	}

	public static void main(String[] args) {
		createFile();
		
		//Generate a random string page reference to perform the algorithms on
		String s = randomString();
		for(int i = 1; i <= 7; i++) {
			System.out.println("For " + i + " page frames, and using string page reference string " + s + "\n");
			printToFile("For " + i + " page frames, and using string page reference string " + s + "\n");
			fifo(i,s);
			lru(i,s);
			optimal(i,s);
		}

		//perform the algorithms on the given string
		String string1 = "0,7,0,1,2,0,8,9,0,3,0,4,5,6,7,0,8,9,1,2";
		for(int i = 1; i <= 7; i++) {
			System.out.println("For " + i + " page frames, and using string page reference string " + string1 + "\n");
			printToFile("For " + i + " page frames, and using string page reference string " + string1 + "\n");
			fifo(i,string1);
			lru(i,string1);
			optimal(i,string1);
			System.out.println();
		}

		//perform the algorithms on the given string
		String string2 = "7,0,1,2,0,3,0,4,2,3,0,3,2,1,2,0,1,7,0,1";
		for(int i = 1; i <= 7; i++) {
			System.out.println("For " + i + " page frames, and using string page reference string " + string2 + "\n");
			printToFile("For " + i + " page frames, and using string page reference string " + string2 + "\n");
			fifo(i,string2);
			lru(i,string2);
			optimal(i,string2);
			System.out.println();
		}
	}
}

