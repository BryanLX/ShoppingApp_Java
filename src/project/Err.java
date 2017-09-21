package project;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Err {
	
	public static void err() {
		try {
			File file = new File("/Users/Shan/Documents/C/csc209/A1/A1/testinput1.csv"
);
			File file2 =new File("/Users/Shan/Documents/C/csc209/A1/A1/testinput4.csv"
					);
			Scanner s = new Scanner(file);
			Scanner s1 = new Scanner(file2);
			while (s.hasNext()) {
				String line = s.nextLine();
				String line1 = s1.nextLine();
				System.out.println("Are they equal ?" + line1.equals(line));
				String[] para = line.split(",");
				//System.out.println("Fiest seg: " + para[2].length());
				//System.out.println(line);
			}
			s.close();
		} 
		catch (IOException e) {
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		err();
	}

}
