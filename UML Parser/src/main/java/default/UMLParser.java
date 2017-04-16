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

		// creates an input stream for the file to be parsed
		FileInputStream in = new FileInputStream(
			"C:\\Users\\Tatsuya\\workspace\\UMLParser\\src\\main\\java\\com\\umlparser\\UMLParser\\test.java");

		// parse the file
		CompilationUnit cu = JavaParser.parse(in);
		// StringBuilder writeToFile = null;
		
		// parseClassOrInt(cu);
		parseMethods(cu);
		parseVariables(cu);
		
		// new VariableChangerVisitor().visit(cu, null);
		// new FieldChangerVisitor().visit(cu, null);
		// System.out.println("New output");
		// System.out.println(cu.toString());
	}
	
	// Parsing the class name or interface
	private static void parseClassOrInt(CompilationUnit cu) {
		// ClassOrInterfaceDeclaration classOrInt = new ClassOrInterfaceDeclaration();
		
		NodeList<TypeDeclaration<?>> types = cu.getTypes();
		
		/*if (.isInterface()) {
			System.out.println("");
		} else {
			System.out.println("");
		}*/
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
			/*st.append("+" + n.getNameAsString().toString() + n.getType()
				+ "\n");*/
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
//    		st.append("-" + addMethods);
    		System.out.println("-" + addMethods);
    	} else if(fieldDec.getModifiers().contains(Modifier.PUBLIC)) {
//    		st.append("+" + addMethods);
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
