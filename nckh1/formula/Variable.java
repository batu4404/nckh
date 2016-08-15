package formula;

import java.util.ArrayList;
import java.util.List;

public class Variable {
	
	public Variable(String name, String type) {
		this.name = name;
		this.type = type;
		index = 0;
	}
	
	public Variable(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	public Variable(Variable other) {
		name = other.name;
		index = other.index;
		type = other.type;
		hasInitialized = other.hasInitialized;
	}
	
	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean hasInitialized() {
		return hasInitialized;
	}
	
	public String getValue() {
		return name + "_" + index;
	} 
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void setIndex(int newIndex) {
		index = newIndex;
	}
	
	public void initialize() {
		hasInitialized = true;
		if(index < 0)
			index = 0;
	}
	
	public void increase() {
		index++;
	}
	
	public void delete() {
		hasInitialized = false;
	}
	
	public static void addVariable(List<Variable> des, List<Variable> source) {
		for(Variable v: source) {
			if( getVariable(v.name, des) == null) {
				des.add(v);
			}
		}
	}
	
	// get first variable has name 
	public static Variable getVariable(String name, List<Variable> listVariables) {
//		if(name == null || listVariables == null) {
//			return null;
//		}
		
		if(name == null) {
			System.out.println("name is null");
			System.exit(1);
		}
		
		if(listVariables == null) {
			System.out.println("listvariables is null");
			System.exit(1);
		}
			
		
		for(Variable v: listVariables) {
			if(name.equals(v.getName()))
				return v;
		}
		
		return null;
	}
	
	public String toString() {
		return "name: " + name + ", index: " + index + ", type: " + type;
	}
	
	public Variable clone() {
		return new Variable(this);
	}
	
	private String name;
	private int index;
	private String type;
	private boolean hasInitialized = false;
}