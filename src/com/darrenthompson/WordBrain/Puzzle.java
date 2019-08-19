package com.darrenthompson.WordBrain;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;



@SuppressWarnings("serial")
public class Puzzle extends RecursiveTask<Set<String>> {
	public LinkedHashSet<String> dict;
	private char[][] puzzle;
	private Set<String> foundWords;
	private LinkedList<Point2D.Double> usedLocations;
	private int startRow, startCol, endRow, endCol, wordLength;
	
	enum Direction {
		N,
		S,
		E,
		W,
		NE,
		NW,
		SE,
		SW
	}
	
	
	/***
	 * Constructs a puzzle
	 * @param dict The dictionary to match words against
	 * @param puzzle The puzzle to permute
	 * @param startRow The starting row
	 * @param startCol The starting column
	 * @param endRow The final row
	 * @param endCol The final column
	 * @param wordLength The length of the words to match
	 * @param foundWords Collection of found words
	 */
	public Puzzle(LinkedHashSet<String> dict, char[][] puzzle, int startRow, int startCol, int endRow, int endCol, int wordLength, Set<String> foundWords) {
		this.dict = dict;
		this.puzzle = puzzle;
		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;
		this.wordLength = wordLength;
		this.foundWords = foundWords;
		usedLocations = new LinkedList<Point2D.Double>();
	}

	@Override
	protected Set<String> compute() {
		int square = (int)Math.pow(puzzle.length, 2);
		if(wordLength < 0 || wordLength > square) {
			System.out.println("Invalid word length");
			return new TreeSet<String>();
		}
		int area = (endRow + 1 - startRow) * (endCol + 1 - startCol);
		// Split the work in half if the total area is > 16 squares
		if( area <= 16 && ((1.0 * area) / wordLength) <= 1.5) {
			return computeDirectly(startRow, startCol, endRow, endCol, wordLength, usedLocations);
		}
		
		
		ArrayList<Puzzle> tasks = new ArrayList<>();
		tasks.add(new Puzzle(dict, puzzle, startRow, startCol, endRow/2, endCol, wordLength, foundWords)); // left
		tasks.add(new Puzzle(dict, puzzle, (endRow/2)+1, startCol, (endRow/2)+1, endCol, wordLength, foundWords)); // right
		return ForkJoinTask.invokeAll(tasks).stream().flatMap(t -> t.join().stream()).collect(Collectors.toSet());
	}
	
	/***
	 * Directly computes the permutations for pieces of a puzzle in a given range
	 * @param startRow The row to start from
	 * @param startCol The column to start from
	 * @param endRow The final row
	 * @param endCol The final column
	 * @param wordLength The length of the words to match
	 * @param usedLocations A collection of the previously used locations
	 */
	public Set<String> computeDirectly(int startRow, int startCol, int endRow, int endCol, int wordLength, LinkedList<Point2D.Double> usedLocations) {
		for(int row = startRow; row <= endRow; row++) {
			for(int col = startCol; col <= endCol; col++) {
				if(!isValid(row, col, new LinkedList<Point2D.Double>())) {
					continue;
				}
				String word = String.format("%c", puzzle[row][col]);
				// call getSolutions with the first character each time
				getSolutions(row, col, word, wordLength, usedLocations);
				if(usedLocations.size() > 0) {
					usedLocations.pop();
				}
			}
		}
		return foundWords;
	}
	
	
	/***
	 * Gets all of the permutations of a puzzle piece of a specified length
	 * @param row The piece's row
	 * @param col The piece's column
	 * @param word The current word
	 * @param wordLength The length of the words to match
	 * @param usedLocations A collection of the previously used locations
	 */
	public void getSolutions(int row, int col, String word, int wordLength, LinkedList<Point2D.Double> usedLocations) {
		usedLocations.push(new Point2D.Double(row, col));
		// Stop when we have found a word the size of wordLength
		if(word.length() == wordLength) {
			// Add the word to the set if it is in the dictionary
			if(dict.contains(word)) {
				foundWords.add(word);
			}
			return;
		}
		else {
			// Look for a neighbor in each possible direction
			for(Direction d : Direction.values()) {
				int neighborRow, neighborCol;
				
				Point2D.Double point = findNeighbor(row, col, d);
				neighborRow = (int)point.x;
				neighborCol = (int)point.y;
				
				if(isValid(neighborRow, neighborCol, usedLocations)) {
					word += puzzle[neighborRow][neighborCol]; // choose
					getSolutions(neighborRow, neighborCol, word, wordLength, usedLocations); // explore
					word = word.substring(0, word.length() - 1); // unchoose
					usedLocations.pop();
				}
				else {
					continue;
				}
			}
		}
	}
	
	/***
	 * Finds the neighbor at the specified position
	 * @param row The piece's row
	 * @param col The piece's column
	 * @param d The direction to find the neighbor
	 * @return A {@link Point2D} with the neighbor row and neighbor column
	 */
	private Point2D.Double findNeighbor(int row, int col, Direction d) {
		int neighborRow = -1, neighborCol = -1;
		switch(d) {
			case N:
				neighborRow = row - 1;
				neighborCol = col;
				break;
			case S:
				neighborRow = row + 1;
				neighborCol = col;
				break;
			case E:
				neighborRow = row;
				neighborCol = col + 1;
				break;
			case W:
				neighborRow = row;
				neighborCol = col - 1;
				break;
			case NE:
				neighborRow = row - 1;
				neighborCol = col + 1;
				break;
			case NW:
				neighborRow = row - 1;
				neighborCol = col - 1;
				break;
			case SE:
				neighborRow = row + 1;
				neighborCol = col + 1;
				break;
			case SW:
				neighborRow = row + 1;
				neighborCol = col - 1;
				break;
			default:
				break;
		}
		return new Point2D.Double(neighborRow, neighborCol);
	}

	
	/***
	 * Check whether or not a puzzle piece is valid
	 * @param row The piece's row
	 * @param col The piece's column
	 * @param usedLocations A collection of the previously used locations
	 * @return true if the puzzle piece is valid
	 */
	public boolean isValid(int row, int col, LinkedList<Point2D.Double> usedLocations) {
		// A piece is valid if it is not out of the bounds of the puzzle and it has not already been used
		if(row < 0 || row >= puzzle.length || col < 0 || col >= puzzle.length || puzzle[row][col] == '-' || usedLocations.contains(new Point2D.Double(row, col))) {
			return false;
		}
		return true;
	}
	
}
