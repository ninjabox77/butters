package schema;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ben
 */
public class LibDecl {
    
    List<FieldDecl> fields = new ArrayList<>();
    List<MethodDecl> methods = new ArrayList<>();
    
    String className;
    String file;
    String language;
    String pkg;
    
    public LibDecl(String file, String language, String pkg) {
        this.file = file;
        this.language = language;
        this.pkg = pkg;
    }
    
    public void addField(FieldDecl fd) {
        fields.add(fd);
    }
    
    public void addMethod(MethodDecl md) {
        methods.add(md);
    }
    
    public List<FieldDecl> fields() {
        return fields;
    }
    
    public List<MethodDecl> methods() {
        return methods;
    }
    
    public void className(String name) {
        className = name;
    }
    
    public String className() {
        return className;
    }
    
    public String file() {
        return file;
    }
    
    public String language() {
        return language;
    }
    
    public String pkg() {
        return pkg;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==== FIELDS ====");
        sb.append('\n');
        for (FieldDecl fd : fields) {
            sb.append(fd);
            sb.append('\n');
        }
        sb.append("==== METHODS ====");
        sb.append('\n');
        for (MethodDecl md : methods) {
            sb.append(md);
            sb.append('\n');
        }
        return sb.toString();
    }
}