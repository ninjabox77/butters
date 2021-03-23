import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

import schema.LibDecl;
import visitor.GenerateNativeLib;

/**
 * @author ben
 */
public class Butters {
    
    static enum OptionType {
        STRING,
        BOOLEAN;
    }
    
    static class Option {
        protected String fieldName;
        protected String optionName;
        protected OptionType optionType;
        protected String description;
        
        public Option(String fieldName, String optionName, OptionType optionType, String description) {
            this.fieldName = fieldName;
            this.optionName = optionName;
            this.optionType = optionType;
            this.description = description;
        }
        
        public Option(String fieldName, String optionName, String description) {
            this(fieldName, optionName, OptionType.BOOLEAN, description);
        }
    }
    
    static final Option[] OPTIONS = new Option[] {
            new Option("nativelib", "-nativelib", OptionType.STRING, "library that maps directly to a different language's library"),
            new Option("native_", "-native", "library written in the language set by the '-lang' command"),
            new Option("file", "-file", OptionType.STRING, "name of the pj library"),
            new Option("lang", "-lang", OptionType.STRING, "name of the native language"),
            new Option("show", "show", "display usage and exits")
    };
    
    // Parse arguments as follows:
    // butters (-nativelib <name> | -native) -file <name> -lang <name>
    public void parseArgs(String[] args) {
        int pos = 0;
        while ( pos<args.length ) {
            String arg = args[pos++];
            if ( arg.charAt(0)!='-' ) {
                if ( arg.equals("show") ) showUsage();
            } else {
                boolean foundOption = false;
                for (Option o : OPTIONS) {
                    if (arg.equals(o.optionName)) {
                        foundOption = true;
                        String value = null;
                        if ( o.optionType!=OptionType.BOOLEAN )
                            value = args[pos++];
                        Class<? extends Butters> c = getClass();
                        try {
                            Field f = c.getField(o.fieldName);
                            if ( value!=null )
                                f.set(this, value);
                            else
                                f.set(this, true);
                        } catch (Exception e) {
                            System.err.println("Failde to access field '" + o.fieldName + "'");
                            System.exit(1);
                        }
                    }
                }
                if ( !foundOption ) {
                    System.err.println("Invalid option '" + arg + "' found!");
                    System.exit(1);
                }
            }
        }
    }
    
    public void showUsage() {
        for (Option o : OPTIONS)
            System.out.printf("%-15s %s\n", o.optionName, o.description);
        System.exit(0);
    }
    
    // Fields accessed by Butters
    public String file;
    public String nativelib;
    public String lang;
    public String native_;
    public boolean show;
    

    // There are two types of libraries:
    // - Libraries written in ProcessJ
    //   ProcessJ libraries must set the #LIBRARY flag
    // - Libraries written in some native language
    //   Native libraries should set the #LIBRARY flag
    //   and also the #NATIVE "language" string.
    //   It may use #INCLUDE "module" to include modules in the
    //   native language.
    //

    // There are three different kinds of libraries:
    // NATIVELIB libraries that map directly to a different language's library like e.g., math.h
    //   NATIVELIB libraries require the following pragmas set:
    //     LIBRARY
    //     NATIVELIB "name of the native library" (e.g. "math.h")
    //     LANGUAGE "name of the native language" (e.g. "C")
    //     FILE "name of the pj library" (e.g. "math")
    // NATIVE libraries are libraries written in the language set by the "LANGUAGE" pragma.
    //   NATIVE libraries require the following pragmas set:
    //     LIBRARY
    //     NATIVE
    //     LANGUAGE
    //     FILE
    // ProcessJ libraries are 100% written in ProcessJ.
    //   ProcessJ libraries require the following pragmas set:
    //     LIBRARY
    //     FILE
    public static void main(String[] args) {
        Butters butt = new Butters();
        butt.parseArgs(new String[] { "show" });
        try {
            //../butters/src/main/java/Foo.java
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