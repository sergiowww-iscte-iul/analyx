package pt.iscteiul.analyx.service;

public class MyComplexClass {
	public int calculateValue(int a, int b) {
		if (a > b && b > 0) { // +2 CC (1 for if, 1 for &&)
			return a + b; // +1 LLOC
		} else if (a == b) { // +1 CC
			return 0; // +1 LLOC
		} else {
			for (int i = 0; i < a; i++) { // +1 CC
				// This is a comment, not LLOC
				// This line is part of the for statement
			}
			return -1; // +1 LLOC
		}
	}

	public int anotherMethod() {
		return 0;
	}
}

