/**
 * Author: Brandon Lee Gaerlan
 * Project: UML Parser
 * Date: 
 * Description: Based on the assignment requirements, the Java program is supposed to take in it's native source code and then parse it
 * into code that is usable for Umple (a class diagram generator).  Exact details of requirements can be located on CMPE 202 reqs
 * 
 * */

/**
 * 2017-02-28
 * This is test code from the java parser website on the manual instructions page
 * Currently experimenting w/ code.  Will come up w/ interface soon. 
 **/ 


package as;

import java.io.FileInputStream;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;

public class UMLParser {
	
	private String[] className;
	private String[] keywordList = {"if","else if", "if"};
	
	public static void main(String[] args) throws Exception {
		
		// creates an input stream for the file to be parsed
	    FileInputStream in = new FileInputStream("C:\\Users\\Tatsuya\\workspace\\as\\src\\main\\java\\as\\test.java");

	    // parse the file
	    CompilationUnit cu = JavaParser.parse(in);

	    // prints the resulting compilation unit to default system output
	    System.out.println(cu.toString());
	    
	}
	
}
