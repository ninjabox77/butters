package schema;

import java.util.ArrayList;
import java.util.List;

public class FieldDecl {
    
    List<String> modifiers = new ArrayList<>();
    String type;
    String name;
    String value;
    
    public FieldDecl(String type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public void addModifier(String modifier) {
        modifiers.add(modifier);
    }
    
    public void type(String type) {
        this.type = type;
    }
    
    public void name(String name) {
        this.name = name;
    }
    
    public List<String> modifiers() {
        return modifiers;
    }
    
    public String type() {
        return type;
    }
    
    public String name() {
        return name;
    }
    
    public void value(String value) {
        this.value = value;
    }
    
    public String value() {
        return value;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("- FieldDecl:");
        sb.append('\n');
        sb.append(" + modifiers:{");
        int count = modifiers.size();
        for (int i = 0; i < count; ++i) {
            sb.append(modifiers.get(i));
            if ( i!=count - 1 )
                sb.append(", ");
        }
        sb.append('}');
        sb.append('\n');
        sb.append(" + type: ");
        sb.append(type);
        sb.append('\n');
        sb.append(" + name: ");
        sb.append(name);
        sb.append('\n');
        sb.append(" + value: ");
        sb.append(value);
        return sb.toString();
    }
}