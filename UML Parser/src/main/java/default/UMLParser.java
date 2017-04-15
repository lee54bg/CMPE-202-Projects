/**
 * Author: Brandon Lee Gaerlan
 * Project: UML Parser
 * Date: 
 * Description: Based on the assignment requirements, the Java program is supposed to take in it's native source code and then parse it
 * into code that is usable for Umple (a class diagram generator).  Exact details of requirements can be located on CMPE 202 reqs
 * 
 * */

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
		
		System.out.println(cu.toString());
		for (TypeDeclaration typeDec : cu.getTypes()) {
			
			List<BodyDeclaration> members = typeDec.getMembers();
			
			for (BodyDeclaration member : members) {
				// Check just members that are FieldDeclarations
//				FieldDeclaration field = (FieldDeclaration) member;
				// Print the field's class typr
//				System.out.println(field.getCommonType());
				if (member instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) member;
					method.setName(method.getNameAsString().toUpperCase());
				} else if(member instanceof FieldDeclaration) {
                	FieldDeclaration fieldDec = (FieldDeclaration) member;
                	
                	
                	if(fieldDec.getModifiers().contains(Modifier.PRIVATE)) {
                		System.out.println(fieldDec.getElementType() + " "
                        		+ fieldDec.getVariable(0));
                	} else if(fieldDec.getModifiers().contains(Modifier.PUBLIC)) {
                		
                	} else if(fieldDec.getModifiers().contains(Modifier.PROTECTED)) {
                		
                	}
                	
                }
			}
			
		}

		// new VariableChangerVisitor().visit(cu, null);
		// new FieldChangerVisitor().visit(cu, null);
			
		//changeMethods(cu);
		 
		System.out.println("New output");
		System.out.println(cu.toString());
		 
		// creates the compilation unit CompilationUnit cue = createCU();
		
		// prints the created compilation unit
		//System.out.println(cue.toString());
		
		/*
		cu.getNodesByType(FieldDeclaration.class).stream(). filter(f ->
		f.getModifiers().contains(Modifier.PUBLIC) &&
		!f.getModifiers().contains(Modifier.STATIC)). forEach(f ->
		System.out.println("Check field at line " +
		f.getBegin().get().line)); System.out.println(cu.toString());
		
		cu.getNodesByType(ClassOrInterfaceDeclaration.class).stream().
		filter(c -> !c.isInterface() &&
		c.getModifiers().contains(Modifier.ABSTRACT) &&
		!c.getName().getId().startsWith("Abstract")). forEach(c -> { String
		oldName = c.getName().getIdentifier(); String newName = "Abstract" +
		oldName; System.out.println("Renaming class " + oldName + " into " +
		newName); c.getName().setIdentifier(newName); });
		*/
		 
	}
	
	
	
	private static void changeMethods(CompilationUnit cu) {

		// Go through all the types in the file
		NodeList<TypeDeclaration<?>> types = cu.getTypes();

		for (TypeDeclaration<?> type : types) {
			// Go through all fields, methods, etc. in this type
			NodeList<BodyDeclaration<?>> members = type.getMembers();
			// NodeList<BodyDeclaration<?>> members = type.getMembers();

			for (BodyDeclaration<?> member : members) {
				if (member instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) member;
					changeMethod(method);
				}
			}
			/*
			 * for (BodyDeclaration<?> member : members) { if (member instanceof
			 * MethodDeclaration) { MethodDeclaration method =
			 * (MethodDeclaration) member; changeMethod(method); } }
			 */
		}
	}

	private static void changeMethod(MethodDeclaration n) {
		// change the name of the method to upper case
		n.setName(n.getNameAsString().toUpperCase());

		// create the new parameter
		n.addParameter(intType(), "value");
	}

	private static Type intType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodChangerVisitor extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			// change the name of the method to upper case
			n.setName(n.getNameAsString().toUpperCase());

			// add a new parameter to the method
			n.addParameter("int", "value");
		}
	}

	private static class VariableChangerVisitor extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(VariableDeclarator n, Void arg) {
			// change the name of the method to upper case
			n.setName(": " + n.getNameAsString().toUpperCase());
			System.out.println(n.getType().toString());

		}
	}

	private static class FieldChangerVisitor extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(FieldDeclaration n, Void arg) {
			// change the name of the method to upper case
			System.out.println(n.getVariables().toString());
			System.out.println(n.getModifiers().toString());

			EnumSet<Modifier> mods = n.getModifiers();
			if (mods.contains(Modifier.PUBLIC)) {
				System.out.println("This works");
			}

		}
	}

	private static CompilationUnit createCU() {
		CompilationUnit cu = new CompilationUnit();
		// set the package
		cu.setPackageDeclaration(new PackageDeclaration(Name.parse("java.parser.test")));

		// or a shortcut
		cu.setPackageDeclaration("java.parser.test");

		// create the type declaration
		ClassOrInterfaceDeclaration type = cu.addClass("GeneratedClass");

		// create a method
		EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
		MethodDeclaration method = new MethodDeclaration(modifiers, new VoidType(), "main");
		modifiers.add(Modifier.STATIC);
		method.setModifiers(modifiers);
		type.addMember(method);

		// or a shortcut
		MethodDeclaration main2 = type.addMethod("main2", Modifier.PUBLIC, Modifier.STATIC);

		// add a parameter to the method
		Parameter param = new Parameter(new ClassOrInterfaceType("String"), "args");
		param.setVarArgs(true);
		method.addParameter(param);

		// or a shortcut
		main2.addAndGetParameter(String.class, "args").setVarArgs(true);

		// add a body to the method
		BlockStmt block = new BlockStmt();
		method.setBody(block);

		// add a statement do the method body
		NameExpr clazz = new NameExpr("System");
		FieldAccessExpr field = new FieldAccessExpr(clazz, "out");
		MethodCallExpr call = new MethodCallExpr(field, "println");
		call.addArgument(new StringLiteralExpr("Hello World!"));
		block.addStatement(call);

		return cu;
	}
}
