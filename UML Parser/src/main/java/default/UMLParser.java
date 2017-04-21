package com.umlparser.UMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.Type;

public class UMLParser {
	
	public static void main(String[] args) throws Exception {
		println("Initiating program...");
		
		// Output will go to writer once which has been defined below
		PrintWriter writer = null;
		
		// Find file from the first argument of the cmd prmpt
		File toParse			= new File("C:\\Users\\Tatsuya\\workspace\\UMLParser\\src\\main\\java\\com\\umlparser\\UMLParser\\test.java");
		CompilationUnit cu		= null;
		FileInputStream parsing;
		StringBuilder toText	= new StringBuilder();
		
		/*if(args.length == 1) {
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
		Scanner input			= new Scanner(System.in);
		// Used to verify if file to parse has been found
		boolean foundFile		= false;
		// Used to parse the input stream of the file
		FileInputStream parsing;
		
		// Do while loop used if user enters an invalid path type
		// Gives users the option to exit the program
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
		} while (!foundFile);*/
		
		parsing = new FileInputStream(toParse);
		cu = JavaParser.parse(parsing);
		
		println("skinparam classAttributeIconSize 0");
		parseClassOrInt(cu, toText);
		outToFile(writer, toText);
	}
	
	// Parsing the class name or interface
	private static void parseClassOrInt(CompilationUnit cu, StringBuilder st) {
		// ClassOrInterfaceDeclaration classOrInt = new ClassOrInterfaceDeclaration();
		
		// NodeList<TypeDeclaration<?>> types = cu.get();
		NodeList<TypeDeclaration<?>> types = cu.getTypes();
		for (TypeDeclaration<?> type : types) {
			if (type instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classOrInt = (ClassOrInterfaceDeclaration) type;
				
				if(classOrInt.getExtendedTypes() != null) {
					println(classOrInt.getNameAsString() 
						+ " --|> " + classOrInt.getExtendedTypes(0));
					append(st, classOrInt.getNameAsString() 
						+ " --|> " + classOrInt.getExtendedTypes(0) + "\n");
					
				}
				
				if (classOrInt.isInterface()) {
					parseMethods(cu, classOrInt, st);
				} else {
					parseVariables(cu, classOrInt, st);
					parseMethods(cu, classOrInt, st);
					parseConstructor(cu, classOrInt, st);
				}
			}
		}
	} // End of parseClassOrInt method
	
	/*
	 * Method to parse constructor
	 * */
	
	private static void parseConstructor(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInt, StringBuilder st) {
		// Go through all the types in the file
		NodeList<TypeDeclaration<?>> types	= cu.getTypes();
		String	varNames, 
				prmNames,
				prmType,
				className;
		className							= classOrInt.getNameAsString();
		
		for (TypeDeclaration<?> type : types) {
			// Go through all fields, methods, etc. in this type
			NodeList<BodyDeclaration<?>> members	= type.getMembers();
			
			for (BodyDeclaration<?> member : members) {
				if (member instanceof ConstructorDeclaration) {
					ConstructorDeclaration constructor = (ConstructorDeclaration) member;
					String cnstrctrName = constructor.getNameAsString();
					
					print(className + " : " + cnstrctrName + "(");
					
					List<Node> cnstrFlds = constructor.getChildNodes();
					if(cnstrFlds != null) {
						for(Node node : cnstrFlds) {
							if(node instanceof Parameter) {
								prmNames	= ((Parameter) node).getNameAsString();
								prmType		= ((Parameter) node).getType().toString();
								
								print(prmType + " " + prmNames + ", ");
							}
						}
						print(")");
					} 
					println("");
				} // End if ConstructorDeclaration
			}
		}
	} // End of parseMethods
	
	/*
	 * Methods used to parse java methods 
	 * */
	
	// Parse methods inside a a class
	private static void parseMethods(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInt, StringBuilder st) {
		// Go through all the types in the file
		NodeList<TypeDeclaration<?>> types = cu.getTypes();

		for (TypeDeclaration<?> type : types) {
			// Go through all fields, methods, etc. in this type
			NodeList<BodyDeclaration<?>> members = type.getMembers();

			for (BodyDeclaration<?> member : members) {
				if (member instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) member;
					String className	= classOrInt.getNameAsString();
					String methodName	= method.getNameAsString().toString();
					Type elementType	= method.getType();
					
					if (method.getModifiers().contains(Modifier.PUBLIC))
						println(className + " : +" + methodName + "() : "
							+ elementType);
						append(st, (className + " : +" + methodName + "() : "
								+ elementType + "\n") );
				}
			}
		}
	} // End of parseMethods
	
	/*
	 * Methods will parse the variables to their respective classes
	 * */
	
	private static void parseVariables(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInt, StringBuilder st) {
		// Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            
            println("Getting children of the field classes...");
            for (BodyDeclaration<?> member : members) {
            	if (member instanceof FieldDeclaration) {
            		FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
            		
            		// Declare Strings that will be used to create the grammar for PlantUML variables
                    String className		= classOrInt.getNameAsString();
                    Type elementType		= fieldDeclaration.getElementType();
					String variableName		= fieldDeclaration.getVariable(0).getNameAsString();
					
					String 				initialvalue;
					VariableDeclarator	variable;
					Expression expr;
					
					List<Node> fieldNodes = fieldDeclaration.getChildNodes();
					if(fieldNodes != null) {
						variable = (VariableDeclarator) fieldNodes.get(0);
						
						if (fieldNodes.get(0) instanceof VariableDeclarator && variable.getInitializer().isPresent()) {
							expr			= variable.getInitializer().get();
							initialvalue	= expr.toString();
							
							String addVariable = className + " : -" + elementType + " " + variableName
									+ " = " + initialvalue;
							
	            			append(st, (addVariable + "\n") );
	            			println(addVariable);
						}
					} else {
						if(fieldDeclaration.getModifiers().contains(Modifier.PRIVATE)) {
	            			String addVariable = className + " : -" + elementType + " " + variableName;
	            			append(st, addVariable);
	            			println(addVariable);
	            		} else if(fieldDeclaration.getModifiers().contains(Modifier.PUBLIC)) {
	                		String addVariable = className + " : +" + elementType + " " + variableName;
	                		append(st, addVariable);
	                		println(className + " : +" + elementType + " " + variableName);
	                	}
					}
				}
            }
        } // End of for loop TypeDeclaration
	} // End of parseVariables method
	
	// Method used to output code to file
	private static void outToFile(PrintWriter writer, StringBuilder st) {
		try {
			writer = new PrintWriter("the-file-name.txt", "UTF-8");
		    writer.println(st.toString());
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // End of outToFile method
	
	public static void genDiagram() {
		String url = "http://www.plantuml.com/plantuml/uml/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000";
		URL newUrl;
		HttpURLConnection con;
		
		/*try {
			newUrl = new URL(url);
			con = (HttpURLConnection) newUrl.openConnection();
			con.setRequestMethod("GET");
			//add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			<div id="B1">
			<textarea name="text" id="inflated" spellcheck="false">@startuml
			Bob -&gt; Alice : hello
			@enduml</textarea>
			</div>
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}*/
	}
	
	/*
	 * Methods used to simplify code for readability purposes
	 * */
	
	public static void append(StringBuilder st, String string) {
		st.append(string);
	}
	
	public static void print(String string) {
		System.out.print(string);
	}
	public static void println(String string) {
		System.out.println(string);
	}
}
