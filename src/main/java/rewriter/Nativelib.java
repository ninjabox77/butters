package rewriter;

import java.util.ArrayList;
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
    
    /** Name of primitive data types in ProcessJ that can be
     * represented by wrapper classes in Java */
    static final String[] PRIMITIVES = new String[] { "String", "Byte",
            "Short", "Integer", "Long", "Float", "Double", "Boolean" };
    /** Especial case for when a library holds this type */
    static final String CHARACTER = "Character";
    
    /** String template file locator */
    final String STG_NATIVELIB = "../butters/src/main/java/template/nativelib.stg";
    
    /** Collection of templates */
    STGroup stGroup;
    
    LibDecl lib;
    
    public Nativelib(LibDecl lib) {
        this.lib = lib;
        this.lib.className(convertClassname(lib.className()));
        stGroup = new STGroupFile(STG_NATIVELIB);
    }
    
    private String convertClassname(String type) {
        if ( type.equals(CHARACTER) ) return "char";
        for (String name : PRIMITIVES)
            if ( type.equals(name) )
                type = type.toLowerCase();
        return type;
    }
    
    public void writer() {
        ST stCompilation = stGroup.getInstanceOf("Compilation");
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
            for (MethodDecl md : lib.methods()) {
                // Grab formal parameters
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
                stMethodDecls.add("name", md.name());
                stMethodDecls.add("refname", lib.className());
                if ( !md.params().isEmpty() )
                    stMethodDecls.add("params", stParams.render());
                methods.add(stMethodDecls.render());
            }
        }
        
        stCompilation.add("pragmas", stPragmas.render());
        stCompilation.add("fields", fields);
        stCompilation.add("methods", methods);
        System.out.println(stCompilation.render());
    }
}