package schema;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ben
 */
public class MethodDecl {
    
    List<String> modifiers = new ArrayList<>();
    List<ParamDecl> params = new ArrayList<>();
    String returnType;
    String name;
    
    public MethodDecl() { }
    
    public void addModifier(String modifier) {
        modifiers.add(modifier);
    }
    
    public void addParam(ParamDecl param) {
        params.add(param);
    }
    
    public void returnType(String returnType) {
        this.returnType = returnType;
    }
    
    public void name(String name) {
        this.name = name;
    }
    
    public List<String> modifiers() {
        return modifiers;
    }
    
    public String returnType() {
        return returnType;
    }
    
    public String name() {
        return name;
    }
    
    public List<ParamDecl> params() {
        return params;
    }
    
    public boolean isConstructorDecl() {
        return returnType == null;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("- MethodDecl:");
        sb.append('\n');
        sb.append(" + modifiers:{");
        int count = modifiers.size();
        for (int i = 0; i < count; ++i) {
            sb.append(modifiers.get(i));
            if (i != count - 1)
                sb.append(", ");
        }
        sb.append('}');
        sb.append('\n');
        sb.append(" + returnType: ");
        sb.append(returnType);
        sb.append('\n');
        sb.append(" + name: ");
        sb.append(name);
        sb.append('\n');
        sb.append(" + params:{");
        count = params.size();
        for (int i = 0; i < count; ++i) {
            sb.append(params.get(i));
            if (i != count - 1)
                sb.append(", ");
        }
        sb.append('}');
        return sb.toString();
    }
}
