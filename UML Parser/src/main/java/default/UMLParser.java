package com.umlparser.UMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class UMLParser {
	
	public static void main(String[] args) throws Exception {
		println("Initiating program...");
		
		// Output will go to writer once which has been defined below
		PrintWriter writer;
		
		if(args.length == 1) {
			println("Default output to the current directory");
		} else if(args.length != 2) {
			println("Not enough arguments.  Terminating program");
			System.exit(0);
		}
		
		// Find file from the first argument of the cmd prmpt
		File toParse			= new File(args[0]);
		CompilationUnit cu		= null;
		// This will be used to output to a text file
		StringBuilder toText	= new StringBuilder();
		// Scanner used to input the correct path name
		Scanner input = new Scanner(System.in);
		// Used to verify if file to parse has been found
		boolean foundFile = false;
		// Used to parse the input stream of the file
		FileInputStream parsing;
		
		do {
			if (toParse.exists())
				foundFile = true;
			else {
				println("Please enter valid path name or exit to close program: ");
				String pathName = input.nextLine();
				
				if(pathName.equals("exit"))
					System.exit(0);
				else
					toParse = new File(pathName);
			}
		} while (!foundFile);
		
		// println(toParse.getName());
		
		parsing = new FileInputStream(toParse);
		cu = JavaParser.parse(parsing);
		
		println("skinparam classAttributeIconSize 0");
		parseClassOrInt(cu);
		// File f = new File(fileArg);	
		// FileInputStream parsing = new FileInputStream(f);
		// cu = JavaParser.parse(parsing);
	}
	
	// Parsing the class name or interface
	private static void parseClassOrInt(CompilationUnit cu) {
		// ClassOrInterfaceDeclaration classOrInt = new ClassOrInterfaceDeclaration();
		
		//NodeList<TypeDeclaration<?>> types = cu.get();
		NodeList<TypeDeclaration<?>> types = cu.getTypes();
		for (TypeDeclaration<?> type : types) {
			if (type instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classOrInt = (ClassOrInterfaceDeclaration) type;
				
				if(classOrInt.getExtendedTypes() != null) {
					System.out.println(classOrInt.getNameAsString() 
						+ " --|> " + classOrInt.getExtendedTypes(0));
				}
				
				if (classOrInt.isInterface()) {
					parseMethods(cu, classOrInt);
					// System.out.println("It's an interface yay");
				} else {
					parseVariables(cu, classOrInt);
					parseMethods(cu, classOrInt);
					// System.out.println("It's a class yay");
				}
				System.out.println("Detected class " + classOrInt.getName());
			}
			// Go through all fields, methods, etc. in this type
			NodeList<BodyDeclaration<?>> members = type.getMembers();
		}
	}
	
	/*
	 * Methods will parse the methods to their respective classes
	 * */
	
	// Parse methods inside a a class
	private static void parseMethods(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInt) {
        // Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    // parseMethod(method);
                    String methodToPrint = method.getNameAsString().toString() + " : " + method.getType();
                    if(method.getModifiers().contains(Modifier.PUBLIC))
            			System.out.println(methodToPrint);
                }
            }
        }
    }
		
	// Parse method
	private static void parseMethod(MethodDeclaration n) {
		if(n.getModifiers().contains(Modifier.PUBLIC))
			System.out.println("+" + n.getNameAsString().toString() + " " + n.getType());
			/*st.append("+" + n.getNameAsString().toString() + n.getType() + "\n");*/
	}
	
	/*
	 * Methods will parse the variables to their respective classes
	 * */
	
	private static void parseVariables(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInt) {
		// Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            
            for (BodyDeclaration<?> member : members) {
                if (member instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
                    // parseVariable(fieldDeclaration, writeToFile);
                    String addMethods = fieldDeclaration.getVariable(0).getNameAsString()
                    	+ " : " + fieldDeclaration.getElementType();
            		if(fieldDeclaration.getModifiers().contains(Modifier.PRIVATE)) {
            			// st.append("-" + addMethods);
                		System.out.println("-" + addMethods);
                	} else if(fieldDeclaration.getModifiers().contains(Modifier.PUBLIC)) {
                		// st.append("+" + addMethods);
                		System.out.println("+" + addMethods);
                	}
                }
            }
        }
	}
	
	private static void parseVariable(FieldDeclaration fieldDec) {
		String addMethods = fieldDec.getElementType() + " : " + fieldDec.getVariable(0);
		
		if(fieldDec.getModifiers().contains(Modifier.PRIVATE)) {
			// st.append("-" + addMethods);
    		System.out.println("-" + addMethods);
    	} else if(fieldDec.getModifiers().contains(Modifier.PUBLIC)) {
    		// st.append("+" + addMethods);
    		println("+" + addMethods);
    	}
	}
	
	
	private static void outToFile() {
		try{
		    PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
		    writer.println("The first line");
		    writer.println("The second line");
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void print(String string) {
		System.out.print(string);
	}
	public static void println(String string) {
		System.out.println(string);
	}
}

/*
 * The following syntax works for PlantUML:
ArrayList --|> List
ArrayList : -Object[] elementData
ArrayList : +size()
ArrayList : +size(coin : int)
ArrayList : +size(coisn : String) : void
class Dummy {
  String data
  void methods()
}
class Flight {
   -flightNumber : Integer
   departureTime : Date
}
Flight --|> Dummy
 * */

/*
 * 
@startuml
Class01 "1" *-- "many" Class02 : contains
Class01 : equals()

Class03 o-- "1" Class04 : aggregation

Class05 --> "1" Class06

Object : equals()
ArrayList : Object[] elementData
ArrayList : size()
@enduml
 * 
 * */
