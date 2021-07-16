package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleFactory {

	public static ArrayList<String> createList(String commaString) {
		return new ArrayList<String>(Arrays.asList(commaString.split(", ")));
	}

}
