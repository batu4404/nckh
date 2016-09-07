package formula2;

import java.util.List;

public class SMTInput {
	public SMTInput() {
		
	}
	
	public SMTInput(List<String> formula, List<Variable> listVariables) {
		this.formula = formula;
		this.listVariables = listVariables;
	}
	
	public List<Variable> getConstraints() {
		return constraints;
	}
	
	public List<Variable> getListVariables() {
		return listVariables;
	}
	
	public List<String> getFormula() {
		return formula;
	}
	
	public void setConstraints(List<Variable> constraints) {
		this.constraints = constraints;
	}
	
	public void setListVariables(List<Variable> listVariables) {
		this.listVariables = listVariables;
	}
	
	public void setFormula(List<String> formula) {
		this.formula = formula;
	}
	
	public void printInput() {
		for (Variable v: listVariables) {
			if (v.hasInitialized()) {
				String smtType = getSMTType(v.getType());
				if ( v.getIndex() < 0)
					System.out.println("(declare-fun " + v.getValue() + " () " + smtType + ")");
				else {
					for (int i = 0; i <= v.getIndex(); i++)
						System.out.println(declare(v.getName(), i, smtType));
				}
			}
			
		}
		for (String s: formula) {
			System.out.println("(assert " + s + ")");
		}
	}
	
	private String getSMTType(String type) {
		String smtType = null;
		if (type.equals("bool"))
			smtType = "Bool";
		else if (type.equals("int") || type.equals("short"))
			smtType = "Int";
		else if (type.equals("float") || type.equals("double"))
			smtType = "Real";
		
		return smtType;
	}

	private String declare(String variableName, int index, String type) {
		String value = variableName + "_" + index;
		String declaration = "(declare-fun " + value + " () " + type + ")";
		return declaration;
	}
	
	public List<String> formula;
	public List<Variable> listVariables;
	public List<Variable> constraints;
}