package com.mhcl.treasure.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFileUtil {

	public static List<String> readFile(String fileName) {
		List<String> fileArray = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				fileArray.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileArray;
	}

	public static void main(String[] args) {

		 

	}

}
