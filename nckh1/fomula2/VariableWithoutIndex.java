package formula2;

public class VariableWithoutIndex extends Variable {

	public VariableWithoutIndex(String name, String type) {
		super(name, type);
	}
	
	public VariableWithoutIndex(VariableWithoutIndex other) {
		super(other);
	}
	
	public String getValue() {
		return getName();
	}
	
	public VariableWithoutIndex clone() {
		return new VariableWithoutIndex(this);
	}
	
	public String toString() {
		return "name: " + super.getName() + ", type: " + super.getType();
	}
	
}
