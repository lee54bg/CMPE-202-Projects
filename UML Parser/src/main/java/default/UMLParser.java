package com.umlparser.UMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.model.declarations.ClassDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class UMLParser {
	
	public static void main(String[] args) throws Exception {
		PrintWriter writer = null;
		String destination = null;
		CompilationUnit cu = null;
		File toParse;
		
		if(args.length == 1) {
			destination = System.getProperty("user.dir");
			println("Default output to current directory : " + destination);
		} else if(args.length == 2) {
			destination = args[1];
			println("Output location set to: " + destination);
		} else if(args.length == 0) {
			println("Insufficient arguments.\nUsage: umlparser inputFileLocation [outputFileLocation]");
			System.exit(0);
		}
		
		// Find file from the first argument of the cmd prmpt
		toParse			= new File(args[0]);
		
		// This will be used to output to a text file
		StringBuilder toText	= new StringBuilder();
		// Scanner used to input the correct path name
		Scanner input			= new Scanner(System.in);
		// Used to verify if file to parse has been found
		boolean foundFile		= false;
		// Used to parse the input stream of the file
		FileInputStream parsing;
		// Output of the file name
		String fileName = "parseroutput.txt";
		// List of files for associations
		ArrayList<File> files = new ArrayList<>(); 
		
		// Do while loop used if user enters an invalid path type
		// Gives users the option to exit the program
		do {
			// if (toParse.exists())
			if (toParse.exists() | toParse.isDirectory())
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
		
		input.close();
		
		// Parse a directory of files
		if(toParse.isDirectory()) {
			File[] listOfFiles = toParse.listFiles();
			
			println("skinparam classAttributeIconSize 0\n@startuml");
			append(toText, "skinparam classAttributeIconSize 0\r\n@startuml\r\n");
			
//			int count = 0;
//			int numOfFiles = toParse.listFiles().length;
			
//			FileInputStream[] parseFiles = new FileInputStream[numOfFiles];
//			CompilationUnit[] cuParse = new CompilationUnit[numOfFiles]; 
			
			for(File file : listOfFiles) {
				if(file.exists() && file.getName().endsWith(".java")) {
					files.add(file);	
				}
			}
			
			for (File file : listOfFiles) {
				if(file.isFile() && file.getName().endsWith(".java")) {
					/*parseFiles[count] = new FileInputStream(file);
					cuParse[count] = JavaParser.parse(parseFiles[count]);*/
					parsing = new FileInputStream(file);
					cu = JavaParser.parse(parsing);
					parseClassOrInt(cu, toText, files);
				}
			}
			
			println("@enduml");
			append(toText, "@enduml");
			outToFile(writer, toText, destination, fileName);
			genDiagram(destination, fileName);
		} else {
			// Parse a single file
			parsing = new FileInputStream(toParse);
			cu = JavaParser.parse(parsing);
			
			println("skinparam classAttributeIconSize 0\n@startuml");
			append(toText, "skinparam classAttributeIconSize 0\r\n@startuml\r\n");
			parseClassOrInt(cu, toText);
			println("@enduml");
			append(toText, "@enduml");
			
			outToFile(writer, toText, destination, fileName);
			genDiagram(destination, fileName);
		}
	}
	
	// Parsing the class name or interface
	private static void parseClassOrInt(CompilationUnit cu, StringBuilder st, ArrayList<File> files) {
		NodeList<TypeDeclaration<?>> types = cu.getTypes();
		
		for (TypeDeclaration<?> type : types) {
			if (type instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classOrInt = (ClassOrInterfaceDeclaration) type;
				
				if(classOrInt.getExtendedTypes() != null) {
					// Empty if statement to keep going if there's no base class
					if(classOrInt.getExtendedTypes().size() == 0) {} else {
						println(classOrInt.getNameAsString() 
							+ " --|> " + classOrInt.getExtendedTypes(0));
						append(st, (classOrInt.getNameAsString() 
							+ " --|> " + classOrInt.getExtendedTypes(0) + "\r\n"));
					}
				}
				
				if(classOrInt.getImplementedTypes() != null) {
					if(classOrInt.getImplementedTypes().size() == 0) {} else {
						println(classOrInt.getNameAsString() 
							+ " ..|> " + classOrInt.getImplementedTypes(0));
						append(st, (classOrInt.getNameAsString() 
							+ " ..|> " + classOrInt.getImplementedTypes(0) + "\r\n"));
						parseMethods(cu, classOrInt, st);
					}
				}
				
				if (classOrInt.isInterface()) {
					println("interface " + classOrInt.getNameAsString());
					append(st, "interface " + classOrInt.getNameAsString() + "\r\n");
					parseMethods(cu, classOrInt, st);
				} else if(classOrInt.getModifiers().contains(Modifier.PUBLIC)) {
						parseVariables(cu, classOrInt, st, files);
						parseMethods(cu, classOrInt, st);
						parseConstructor(cu, classOrInt, st);
				} else {
					parseVariables(cu, classOrInt, st, files);
					parseMethods(cu, classOrInt, st);
					parseConstructor(cu, classOrInt, st);
				}
			}
		}
	} // End of parseClassOrInt method
	
	// Parsing the class name or interface for single files
	private static void parseClassOrInt(CompilationUnit cu, StringBuilder st) {
		NodeList<TypeDeclaration<?>> types = cu.getTypes();
		
		for (TypeDeclaration<?> type : types) {
			if (type instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classOrInt = (ClassOrInterfaceDeclaration) type;

				if (classOrInt.getExtendedTypes() != null) {
					// Empty if statement to keep going if there's no base class
					if (classOrInt.getExtendedTypes().size() == 0) {
					} else {
						println(classOrInt.getNameAsString() + " --|> " + classOrInt.getExtendedTypes(0));
						append(st, (classOrInt.getNameAsString() + " --|> " + classOrInt.getExtendedTypes(0) + "\r\n"));
					}
				}

				if (classOrInt.getImplementedTypes() != null) {
					if (classOrInt.getImplementedTypes().size() == 0) {
					} else {
						println(classOrInt.getNameAsString() + " ..|> " + classOrInt.getImplementedTypes(0));
						append(st,
								(classOrInt.getNameAsString() + " ..|> " + classOrInt.getImplementedTypes(0) + "\r\n"));
						parseMethods(cu, classOrInt, st);
					}
				}

				if (classOrInt.isInterface()) {
					println("interface " + classOrInt.getNameAsString());
					append(st, "interface " + classOrInt.getNameAsString() + "\r\n");
					parseMethods(cu, classOrInt, st);
				} else if (classOrInt.getModifiers().contains(Modifier.PUBLIC)) {
					parseVariables(cu, classOrInt, st);
					parseMethods(cu, classOrInt, st);
					parseConstructor(cu, classOrInt, st);
				} else if(classOrInt.isInterface() && classOrInt.getModifiers().contains(Modifier.PUBLIC)) { 
					println("interface " + classOrInt.getNameAsString());
					append(st, "interface " + classOrInt.getNameAsString() + "\r\n");
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
		String	prmNames,
				prmType,
				className;
		className = classOrInt.getNameAsString();
		
		for (TypeDeclaration<?> type : types) {
			// Go through all fields, methods, etc. in this type
			NodeList<BodyDeclaration<?>> members	= type.getMembers();
			
			for (BodyDeclaration<?> member : members) {
				if (member instanceof ConstructorDeclaration) {
					ConstructorDeclaration constructor = (ConstructorDeclaration) member;
					String cnstrctrName = constructor.getNameAsString();
					
					print(className + " : " + cnstrctrName + "(");
					append(st, className + " : " + cnstrctrName + "(");
					
					List<Node> cnstrFlds = constructor.getChildNodes();
					int prmCnt = cnstrFlds.size() / 2;
					int count = 0;
					
					if(cnstrFlds != null) {
						for(Node node : cnstrFlds) {
							if(node instanceof Parameter) {
								count++;
								prmNames	= ((Parameter) node).getNameAsString();
								prmType		= ((Parameter) node).getType().toString();
								
								if(count == prmCnt) {
									print(prmType + " " + prmNames);
									append(st, (prmType + " " + prmNames));
									break;
								}
								
								print(prmType + " " + prmNames + ", ");
								append(st, (prmType + " " + prmNames + ", "));
							}
						}
						
						append(st, ")");
						print(")");
					}
					append(st, "\r\n");
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
			// Iterate through fields in class
			NodeList<BodyDeclaration<?>> members = type.getMembers();

			for (BodyDeclaration<?> member : members) {
				if (member instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) member;
					member.toString();
					String className	= classOrInt.getNameAsString();
					String methodName	= method.getNameAsString().toString();
					Type elementType	= method.getType();
					
					if (method.getModifiers().contains(Modifier.PUBLIC))
						println(className + " : +" + methodName + "() : "
							+ elementType);
						append(st, (className + " : +" + methodName + "() : "
							+ elementType + "\r\n") );
				}
			}
		}
	} // End of parseMethods
	
	/*
	 * Methods will parse the variables to their respective classes
	 * */
	
	private static void parseVariables(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInt, StringBuilder st, ArrayList<File> files) {
		// Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        
        for (TypeDeclaration<?> type : types) {
            // Iterate through all fields
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            
            int count = 0;
            for (BodyDeclaration<?> member : members) {
            	if (member instanceof FieldDeclaration) {
            		FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
            		
            		// Declare Strings that will be used to create the grammar for PlantUML variables
                    String className		= classOrInt.getNameAsString();
                    Type elementType		= fieldDeclaration.getElementType();
					//String variableName		= fieldDeclaration.getVariable(0).getNameAsString();
                    String variableName		= fieldDeclaration.getVariable(0).getNameAsString();
					// To compare for file names
                    String temp = elementType.toString() + ".java";
                    String oneToMany = null;
                    
					String 				initialvalue;
					VariableDeclarator	variable;
					Expression expr;
					boolean found = false;
					
					for(File file : files) {
						if(temp.toString().contains(file.getName()) ) {
							oneToMany = elementType.toString();
							found = true;
							break;
						}	
					}
					
					if(found == true) {
						if(elementType.toString().contains(oneToMany)) {
							println(classOrInt.getNameAsString() + " \"1\" -- \"1\" " + elementType.toString());
							append(st, classOrInt.getNameAsString() + " \"1\" -- \"1\" " + elementType.toString() + "\r\n");
						} 
					} else if(fieldDeclaration.toString().contains("Collection")) {
						String m2M = null;
						boolean foundCollection = false;
						
						for(File file : files) {
							String manyToMany[] = file.getName().split("\\.");
							//println("File Name: " + manyToMany[0]);
							if(elementType.toString().contains(manyToMany[0])) {
								m2M = manyToMany[0];
								foundCollection = true;
								break;
							}
						}
						
						if(foundCollection == true) {
							println(classOrInt.getNameAsString() + " \"1\" -- \"many\" " + m2M);
							append(st, classOrInt.getNameAsString() + " \"1\" -- \"many\" " + m2M + "\r\n");
						}
					} else if(fieldDeclaration.toString().contains("private")) {
						if (fieldDeclaration.toString().contains("[]")) {
							String addVariable = className + " : -" + elementType + "[] " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						} else {
							String addVariable = className + " : -" + elementType + " " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						}
            		} else if(fieldDeclaration.toString().contains("public")) {
            			if (fieldDeclaration.toString().contains("[]")) {
							String addVariable = className + " : +" + elementType + "[] " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						} else {
							String addVariable = className + " : +" + elementType + " " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						}
            		} else if(fieldDeclaration.toString().contains("=")) {
            			List<Node> fieldNodes = fieldDeclaration.getChildNodes();
    					if(fieldNodes != null) {
    						variable = (VariableDeclarator) fieldNodes.get(0);
    						
    						if (fieldNodes.get(0) instanceof VariableDeclarator && variable.getInitializer().isPresent()) {
    							expr			= variable.getInitializer().get();
    							initialvalue	= expr.toString();
    							
    							String addVariable = className + " : -" + elementType + " " + variableName
    									+ " = " + initialvalue;
    							
    	            			append(st, (addVariable + "\r\n") );
    	            			println(addVariable);
    						}
    					}
            		}
				}
            }
        } // End of for loop TypeDeclaration
	} // End of parseVariables method
	
	// For single file parse Variables
	private static void parseVariables(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInt, StringBuilder st) {
		// Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            
            
            int count = 0;
            for (BodyDeclaration<?> member : members) {
            	if (member instanceof FieldDeclaration) {
            		FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
            		
            		// Declare Strings that will be used to create the grammar for PlantUML variables
                    String className		= classOrInt.getNameAsString();
                    Type elementType		= fieldDeclaration.getElementType();
					//String variableName		= fieldDeclaration.getVariable(0).getNameAsString();
                    String variableName		= fieldDeclaration.getVariable(0).getNameAsString();
					// To compare for file names
                    String temp = elementType.toString() + ".java";
                    String oneToMany;
                    
					String 				initialvalue;
					VariableDeclarator	variable;
					Expression expr;
					
					
					
					if(fieldDeclaration.toString().contains("private")) {
						if (fieldDeclaration.toString().contains("[]")) {
							String addVariable = className + " : -" + elementType + "[] " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						} else {
							String addVariable = className + " : -" + elementType + " " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						}
            		} else if(fieldDeclaration.toString().contains("public")) {
            			if (fieldDeclaration.toString().contains("[]")) {
							String addVariable = className + " : +" + elementType + "[] " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						} else {
							String addVariable = className + " : +" + elementType + " " + variableName;
	            			append(st, addVariable + "\r\n");
	            			println(addVariable);
						}
            		} else if(fieldDeclaration.toString().contains("=")) {
            			List<Node> fieldNodes = fieldDeclaration.getChildNodes();
    					if(fieldNodes != null) {
    						variable = (VariableDeclarator) fieldNodes.get(0);
    						
    						if (fieldNodes.get(0) instanceof VariableDeclarator && variable.getInitializer().isPresent()) {
    							expr			= variable.getInitializer().get();
    							initialvalue	= expr.toString();
    							
    							String addVariable = className + " : -" + elementType + " " + variableName
    									+ " = " + initialvalue;
    							
    	            			append(st, (addVariable + "\r\n") );
    	            			println(addVariable);
    						}
    					}
            		}
				}
            }
        } // End of for loop TypeDeclaration
	} // End of single file parseVariables method
	
	// Method used to output code to file
	private static void outToFile(PrintWriter writer, StringBuilder st, String destination, String fileName) {
		println("Generating diagram...");
		
		try {
			/*String output = System.getProperty("user.dir");
			println(output);
			*/
			writer = new PrintWriter(destination + "\\" + fileName, "UTF-8");
		    writer.println(st.toString());
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // End of outToFile method
	
	public static void genDiagram(String destination, String fileName) {
		try {
			String cmd = "java -jar plantuml.jar " + destination + "\\" + fileName;
			println(cmd);
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Methods used to simplify code for readability
	 * */
	
	public static void append(StringBuilder st, String string) { st.append(string);	}
	public static void print(String string) { System.out.print(string);	}
	public static void println(String string) { System.out.println(string);	}
}
