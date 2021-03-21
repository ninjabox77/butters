import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

import schema.LibDecl;
import visitor.GenerateNativeLib;

/**
 * @author ben
 */
public class Butters {
    
    public static void main(String[] args) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File("/Users/oswaldocisneros/Documents/String.java"));
            LibDecl libDecl = new LibDecl("strings", "JVM", "std");
            VoidVisitor<LibDecl> generateNativeLib = new GenerateNativeLib();
            generateNativeLib.visit(cu, libDecl);
//            System.out.println(libDecl);
            new rewriter.Nativelib(libDecl).writer();
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }
}
