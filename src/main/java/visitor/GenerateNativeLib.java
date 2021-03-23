package visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import schema.FieldDecl;
import schema.LibDecl;
import schema.MethodDecl;
import schema.ParamDecl;

/**
 * @author ben
 */
public class GenerateNativeLib extends VoidVisitorAdapter<LibDecl> {
    
    static final String JAVA_LANG = "java.lang";
    
    /** Access modifiers for methods */
    static final String SYNCHRONIZED = "synchronized";
    static final String[] ACCESS_MODIFIERS = new String[] { "public",
            "static", "final" };
    static Hashtable<String, Integer> ht = new Hashtable<>();
    
    /** Name of the native Java library */
    String className = null;
    
    static {
        int index = 0;
        for (String str : ACCESS_MODIFIERS)
            ht.put(str, index++);
    }
    
    public void visit(FieldDeclaration fd, LibDecl ld) {
        super.visit(fd, ld);
        List<FieldDecl> fields = new ArrayList<>();
        // Only add primitives to the list
        for (VariableDeclarator vd : fd.getVariables()) {
            if ( vd.getType().isPrimitiveType() || vd.getTypeAsString().equals(className) ) {
                FieldDecl f = new FieldDecl(vd.getTypeAsString(), vd.getNameAsString());
                Optional<Expression> expr = vd.getInitializer();
                if ( expr.isPresent() )
                    f.value(expr.get().toString());
                fields.add(f);
            }
        }
        for (Modifier mod : fd.getModifiers())
            for (FieldDecl f : fields)
                f.addModifier(mod.toString().trim());
        for (FieldDecl f : fields) {
            Boolean[] modifiers = new Boolean[] { false, false, false };
            for (String str : f.modifiers())
                if ( ht.get(str)!=null )
                    modifiers[ht.get(str)] = true;
            if ( !Arrays.asList(modifiers).contains(false) )
                ld.addField(f);
        }
    }
    
    public void visit(MethodDeclaration md, LibDecl ld) {
        super.visit(md, ld);
        MethodDecl method = new MethodDecl();
        method.name(md.getNameAsString());
        if ( !md.getType().isPrimitiveType() && !md.getType().isVoidType() && !md.getTypeAsString().equals(className) )
            return;
        method.returnType(md.getTypeAsString());
        for (Modifier mod : md.getModifiers()) {
            if ( mod.toString().trim().equals(SYNCHRONIZED) ) return;
            method.addModifier(mod.toString().trim());
        }
        // Only add primitives to the list
        for (Parameter param : md.getParameters()) {
            if ( !param.getType().isPrimitiveType() && !param.getTypeAsString().equals(className) )
                return;
            method.addParam(new ParamDecl(param.getTypeAsString(),
                    param.getNameAsString()));
        }
        for (String mod : method.modifiers())
            if ( mod.equals(ACCESS_MODIFIERS[0]) )
                ld.addMethod(method);
    }
    
    public void visit(ConstructorDeclaration cd, LibDecl ld) {
        super.visit(cd, ld);
        MethodDecl method = new MethodDecl();
        method.name(cd.getNameAsString());
        for (Modifier mod : cd.getModifiers())
            method.addModifier(mod.toString().trim());
        // Only add primitives to the list
        for (Parameter param : cd.getParameters()) {
            if ( !param.getType().isPrimitiveType() && !param.getTypeAsString().equals(className) )
                return;
            method.addParam(new ParamDecl(param.getTypeAsString(),
                    param.getNameAsString()));
        }
        for (String mod : method.modifiers())
            if ( mod.equals(ACCESS_MODIFIERS[0]) )
                ld.addMethod(method);
    }
    
    public void visit(ClassOrInterfaceDeclaration cd, LibDecl ld) {
        className = cd.getNameAsString();
        ld.className(className);
        // Ignore inner classes and interfaces
        for (Node n : cd.getChildNodes())
            if ( !(n instanceof ClassOrInterfaceDeclaration) )
                n.accept(this, ld);
    }
}