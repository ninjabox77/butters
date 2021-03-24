package rewriter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import schema.FieldDecl;
import schema.LibDecl;
import schema.MethodDecl;
import schema.ParamDecl;

/**
 * @author ben
 */
public class Nativelib {
    
    // Name of primitive data types in ProcessJ that can be
    // represented by wrapper classes in Java
    static final String[] WRAPPERS = new String[] { "String", 
            "Character", "Byte", "Short", "Integer", "Long", 
            "Float", "Double", "Boolean" };
    
    // Name of primitives data types in ProcessJ
    static final String[] PRIMITIVES = new String[] { "string", 
            "char", "byte", "short", "int", "long", 
            "float", "double", "boolean" };
    
    // Conversion table from Java to ProcessJ types: wrapper -> primitive
    static final Hashtable<String, String> ht = new Hashtable<>();
    
    // String template file locator
    final String STG_NATIVELIB = "../butters/src/main/java/template/nativelib.stg";
    
    // Collection of templates
    STGroup stGroup;
    
    // This instance holds information with respect to the
    // native ProcessJ library
    LibDecl lib;
    
    static {
        for (int i = 0; i<WRAPPERS.length; ++i)
            ht.put(WRAPPERS[i], PRIMITIVES[i]);
    }
    
    public Nativelib(LibDecl lib) {
        this.lib = lib;
        stGroup = new STGroupFile(STG_NATIVELIB);
    }
    
    private String convertClassname(String type) {
        if ( ht.containsKey(type) )
            return ht.get(type);
        return type;
    }
    
    public void writePJ() {
        ST stNativelib = stGroup.getInstanceOf("Nativelib");
        ST stPragmas = stGroup.getInstanceOf("Pragmas");
        
        stPragmas.add("file", lib.file());
        stPragmas.add("language", lib.language());
        stPragmas.add("package", lib.pkg());
        
        List<String> fields = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        
        if ( !lib.fields().isEmpty() ) {
            // Grab formal fields
            ST stFieldDecl = stGroup.getInstanceOf("FieldDecl");
            for (FieldDecl fd : lib.fields()) {
                stFieldDecl.add("type", fd.type());
                stFieldDecl.add("name", fd.name());
                stFieldDecl.add("value", fd.value());
                fields.add(stFieldDecl.render());
            }
        }
        
        if ( !lib.methods().isEmpty() ) {
            // Grab formal parameters
            for (MethodDecl md : lib.methods()) {
                ST stParams = stGroup.getInstanceOf("ParamDecls");
                List<String> types = new ArrayList<>();
                List<String> names = new ArrayList<>();
                for (ParamDecl param : md.params()) {
                    types.add(convertClassname(param.type()));
                    names.add(param.name());
                }
                stParams.add("types", types);
                stParams.add("names", names);
                // Grab method signature
                ST stMethodDecls = stGroup.getInstanceOf("MethodDecls");
                String type = md.returnType()==null? md.name() : md.returnType();
                stMethodDecls.add("type", convertClassname(type));
                stMethodDecls.add("name", convertClassname(md.name()));
                stMethodDecls.add("ref", convertClassname(lib.className()));
                if ( !md.params().isEmpty() )
                    stMethodDecls.add("params", stParams.render());
                methods.add(stMethodDecls.render());
            }
        }
        
        stNativelib.add("pragmas", stPragmas.render());
        stNativelib.add("fields", fields);
        stNativelib.add("methods", methods);
        System.out.println(stNativelib.render());
    }
    
    public void writeJava() {
        ST stClasslib = stGroup.getInstanceOf("Classlib");
        
        List<String> fields = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        
        if ( !lib.fields().isEmpty() ) {
            // Grab formal fields
            ST stFieldDef = stGroup.getInstanceOf("FieldDef");
            for (FieldDecl fd : lib.fields()) {
                stFieldDef.add("type", fd.type());
                stFieldDef.add("name", fd.name());
                stFieldDef.add("value", fd.value());
                fields.add(stFieldDef.render());
            }
        }
        
        if ( !lib.methods().isEmpty() ) {
            // Grab formal parameters
            for (MethodDecl md : lib.methods()) {
                ST stParams = stGroup.getInstanceOf("ParamDecls");
                ST stParamDef = stGroup.getInstanceOf("ParamDef");
                List<String> types = new ArrayList<>();
                List<String> names = new ArrayList<>();
                for (ParamDecl param : md.params()) {
                    types.add(convertClassname(param.type()));
                    names.add(param.name());
                }
                stParams.add("types", types);
                stParams.add("names", names);
                stParamDef.add("names", names);
                // Grab method signature
                ST stMethodDef = stGroup.getInstanceOf("MethodDef");
                String type = md.returnType()==null? md.name() : md.returnType();
                stMethodDef.add("type", type);
                stMethodDef.add("name", md.name());
                stMethodDef.add("ref", lib.className());
                if ( !type.equals("void") )
                    stMethodDef.add("return", true);
                if ( !md.params().isEmpty() ) {
                    stMethodDef.add("actuals", stParams.render());
                    stMethodDef.add("formals", stParamDef.render());
                }
                methods.add(stMethodDef.render());
            }
        }
        
        stClasslib.add("package", lib.pkg());
        stClasslib.add("name", lib.file());
        stClasslib.add("fields", fields);
        stClasslib.add("methods", methods);
        System.out.println(stClasslib.render());
    }
}