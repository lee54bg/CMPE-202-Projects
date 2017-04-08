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

import java.io.FileInputStream;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;

public class UMLParser {
	
	private String[] className;
	private String[] keywordList = {"if","else if", "if"};
	
	public static void main(String[] args) throws Exception {
		EnumSet<Modifier> pubAccMod = EnumSet.of(Modifier.PUBLIC);
		EnumSet<Modifier> privAccMod = EnumSet.of(Modifier.PRIVATE);
		EnumSet<Modifier> staticMod = EnumSet.of(Modifier.STATIC);		
		
		// creates an input stream for the file to be parsed
    	FileInputStream in = new FileInputStream("C:\\Users\\Tatsuya\\workspace\\as\\src\\main\\java\\as\\test.java");

        // parse the file
        CompilationUnit cu = JavaParser.parse(in);

        // change the methods names and parameters
        changeMethods(cu);

        // prints the changed compilation unit
        System.out.println(cu.toString());
        
        ClassOrInterfaceDeclaration myClass = new ClassOrInterfaceDeclaration();
        myClass.setName("Hi");
        myClass.setModifiers(pubAccMod);
//        myClass.setModifiers(staticMod);
        String code = myClass.toString();
        
        System.out.print(code);
        
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        MethodDeclaration method = new MethodDeclaration(modifiers, new VoidType(), "main");
        modifiers.add(Modifier.STATIC);
        method.setModifiers(modifiers);
        method.toString();
        
        System.out.println(method);
        
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
	}
	
}
