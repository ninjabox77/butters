package schema;

/**
 * @author ben
 */
public class ParamDecl {
    
    String type;
    String name;
    
    public ParamDecl(String type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public void type(String type) {
        this.type = type;
    }
    
    public void name(String name) {
        this.name = name;
    }
    
    public String type() {
        return type;
    }
    
    public String name() {
        return name;
    }
    
    public String toString() {
        return "ParamDecl:[type: " + type + ", name: " + name + "]";
    }
}