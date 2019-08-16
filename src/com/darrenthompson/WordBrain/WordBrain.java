package com.darrenthompson.WordBrain;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import java.util.Scanner;

public class WordBrain {
	private static char[][] p;
	static Scanner sc = new Scanner(System.in);
	
	public static void begin() {
		LinkedHashSet<String> dict = new LinkedHashSet<String>();
		
		// Read all words from dictionary file
		try(Stream<String> stream = Files.lines(Paths.get("C:/Users/Darren/Downloads/words.txt"))) {
			// Add each word to dictionary
			stream.forEach(w -> dict.add(w.toUpperCase()));
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
		int rows, numOfWords;
		
		System.out.println("Enter the height of the grid: ");
		rows = sc.nextInt();
		p = new char[rows][rows];
		for(int i = 0; i < rows; i++) {
			System.out.printf("Enter the items for row %d:\n", i+1);
			for(int j = 0; j < rows; j++) {
				System.out.print(">");
				char c = sc.next().toUpperCase().charAt(0);
				p[i][j] = c;
			}
		}
		
		
		System.out.print("\nHow many words do you need to find?: ");
		numOfWords = sc.nextInt();
		
		System.out.println();
		ArrayList<Integer> lengths = new ArrayList<Integer>();
		for(int k = 0; k < numOfWords; k++) {
			System.out.print("Enter the word length: ");
			int wordLength = sc.nextInt();
			System.out.println();
			lengths.add(wordLength);
		}
		
		ForkJoinPool pool = new ForkJoinPool();
		Puzzle puzzle = null;
		TreeSet<String> foundWords = new TreeSet<String>();
		for(Integer i : lengths) {
			puzzle = new Puzzle(dict, p, 0, 0, p.length-1, p.length-1, i, foundWords);
			pool.invoke(puzzle);
		}
		
		
		for(String word : foundWords) {
			System.out.println(word);
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("WordBrain Solution Finder\n\n");
		char ch = 'a';
		do {
			begin();
			System.out.print("New puzzle?: ");
			ch = sc.next().toUpperCase().charAt(0);
			
		} while(ch != 'N');
		
		
	}


}
