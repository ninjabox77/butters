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
            ///Users/oswaldocisneros/Documents/String.java
            CompilationUnit cu = StaticJavaParser.parse(new File("../butters/src/main/java/Foo.java"));
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