package com.umlparser.UMLParser;

import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class UMLParser {
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("Initiated UML Parser");
		
		// creates an input stream for the file to be parsed
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		if(args == null) {
			System.out.println("Please print the commands properly");
		}
		File in = new File(args[0]);
		/*FileInputStream in = new FileInputStream(
				"C:\\Users\\Tatsuya\\workspace\\UMLParser\\src\\main\\java\\com\\umlparser\\UMLParser\\test.java");*/
		
		if(!in.exists()) {
			System.out.println("The file(s) do not exist");
		} else {
			System.out.println("The file(s) exists");
		}
		
		
		// creates an input stream for the file to be parsed
		FileInputStream in = new FileInputStream(
			"C:\\Users\\Tatsuya\\workspace\\UMLParser\\src\\main\\java\\com\\umlparser\\UMLParser\\test.java");

		// parse the file
		CompilationUnit cu = JavaParser.parse(in);
		// StringBuilder writeToFile = null;
		
		parseClassOrInt(cu);
		
		/*
		System.out.println("\nExecuting parse methods...\n\n");
		parseMethods(cu);
		System.out.println("\nExecuting parse variables...\n\n");
		parseVariables(cu);*/
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
            
            /*// Section of code will detect inner classes
            for (BodyDeclaration<?> member : members) {
                if (member instanceof ClassOrInterfaceDeclaration) {
                	ClassOrInterfaceDeclaration innerClassOrInt = (ClassOrInterfaceDeclaration) type;
                	System.out.println("Detected inner class " + innerClassOrInt.getName());
                }
            }*/
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
                    if(method.getModifiers().contains(Modifier.PUBLIC))
            			System.out.println(classOrInt.getName() + " : " + method.getNameAsString().toString() + " " + method.getType());
                }
            }
        }
    }
		
	private static void parseMethods(CompilationUnit cu) {
        // Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    parseMethod(method);
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
                    String addMethods = classOrInt.getName().toString()
                    	+ " :  " + fieldDeclaration.getElementType()
                    	+ " " + fieldDeclaration.getVariable(0).getNameAsString();
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
	
	// Parsing Variables
	// private static void parseVariables(CompilationUnit cu, StringBuilder writeToFile) {
	private static void parseVariables(CompilationUnit cu) {
		// Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            
            for (BodyDeclaration<?> member : members) {
                if (member instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
                    // parseVariable(fieldDeclaration, writeToFile);
                    parseVariable(fieldDeclaration);
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
    		System.out.println("+" + addMethods);
    	}
	}
}


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
