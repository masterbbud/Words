package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

	public FileManager() {
		
	}
	public String readJSON(String file) throws IOException {
		// takes input of a file url representing a json file, returns the file as a single string.
		BufferedReader br = new BufferedReader(new FileReader(file));
		//System.out.println("ASD");
		String line = br.readLine();
		br.close();
		return line;
	}
	public ArrayList<String> readNormal(String file) throws IOException {
		// takes input of a file url representing a txt file, returns a list of lines as Strings.
		// IMPORTANT: First line should be an integer representing the total number of lines in the file - 1
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		int n = Integer.parseInt(br.readLine());
		for (int i = 0; i < n; i++) {
			list.add(br.readLine());
		}
		br.close();
		return list;
	}
}
