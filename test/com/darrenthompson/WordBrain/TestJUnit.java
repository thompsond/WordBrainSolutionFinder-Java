package com.darrenthompson.WordBrain;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;
import java.util.LinkedList;

public class TestJUnit {

	@Test
	public void testIsValid() {
		char[][] p = {
				{'E', 'D', 'H', 'N'},
				{'L', 'E', 'R', 'O'},
				{'B', 'A', 'R', 'R'},
				{'C', 'O', 'T', 'T'}
		};
		
		LinkedList<Point2D.Double> usedLocations = new LinkedList<Point2D.Double>();
		usedLocations.add(new Point2D.Double(1, 2));
		assertEquals(false, isValid(p, 1, 2, usedLocations));
	}
	
	public boolean isValid(char[][] puzzle, int row, int col, LinkedList<Point2D.Double> usedLocations) {
		// A piece is valid if it is not out of the bounds of the puzzle and it has not already been used
		if(row < 0 || row >= puzzle.length || col < 0 || col >= puzzle.length || puzzle[row][col] == '-' || usedLocations.contains(new Point2D.Double(row, col))) {
			return false;
		}
		return true;
	}
}
