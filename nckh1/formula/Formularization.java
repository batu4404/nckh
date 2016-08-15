package formula;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.UnaryOperatorKind;

public abstract class Formularization {
	
	public abstract List<String> getFormula();
	
	public List<String> formularize(CtStatement statement,
									String preCondition) {
		List<String> f = new ArrayList<>();
		if(statement == null)
			return f;
		
		String s = null;
		if(statement instanceof CtAssignment) {
			f = formularize((CtAssignment) statement);
		}
		else if(statement instanceof CtUnaryOperator) {
			s = formularize((CtUnaryOperator) statement);
		}
		else if(statement instanceof CtIf) {
			IfFormularization ifF = new IfFormularization((CtIf) statement, listVariables, 
															flagVariables, lastReturnFlag);
			f = ifF.getFormula();
			System.out.println("last return flag: " + lastReturnFlag);
		} 
		else if(statement instanceof CtSwitch) {
//			f = formularize((CtSwitch) statement);
		}
		else if(statement instanceof CtBlock) {
			f = formularize((CtBlock) statement);
		} 
		else if(statement instanceof CtLocalVariable) {
			f = formularize( (CtLocalVariable) statement);
		}
		else if(statement instanceof CtFor) {
			ForFormularization forF = new ForFormularization((CtFor) statement, 
								listVariables, flagVariables, lastReturnFlag);
			f = forF.getFormula();
		}
		else if(statement instanceof CtReturn) {
			s = formularize((CtReturn) statement);
		}	
		
		if(s != null) {
			if(preCondition != null)
				s = wrap(preCondition, "=>", s);
			f.add(s);
		}
		
		return f;
	}
	
	public List<String> formularize(List<CtStatement> list, String preCondition) {
		
		List<String> f = new ArrayList<>();
		
		if(list == null)
			return f;
	
		List<String> temp = new ArrayList<>();
		for(CtStatement s: list) {
			temp = formularize(s, preCondition);
			f.addAll(temp);
		}
	
		return f;
	}
	
	public List<String> formularize(CtBlock block) {
		
		if(block == null)
			return new ArrayList<>();
		
		return formularize(block.getStatements(), null);
	}
	
	public String formularize(CtUnaryOperator unaryOp) {
		
		CtExpression operand = unaryOp.getOperand();
		if(operand instanceof CtLiteral) {
			return unaryOp.toString();
		}
			
		Variable variable = Variable.getVariable(operand.toString(), listVariables);
		UnaryOperatorKind operator = unaryOp.getKind();
		String opStr = Helper.getUnaryOperator(operator);
		String exp = null;
		String f;
		if(opStr.equals("+") || opStr.equals("-")) {
			exp = wrap(variable.getValue(), opStr, "1");
			variable.increase();
			f = wrap(variable.getValue(), "=", exp);
		}
		else {
			exp = formularize(operand);
			f = wrap(opStr, exp);
		}
			
		return f;
	}
	
	
	public List<String> formularize(CtLocalVariable var) 
	{
/*		System.out.println("local variable:");
		for(Variable v: listVariables) {
			System.out.println(v);
		}
*/		
		List<String> f = new ArrayList<>();

		String variableName;
		variableName = var.getSimpleName();
		Variable v = Variable.getVariable(variableName, listVariables);
		
		// nếu biến v chưa có trong list thì thêm vào, ngược lại thì tăng index cũ
		if(v == null) {
			v = new Variable(variableName, var.getType().toString());
			listVariables.add(v);
		}
/*		else {
			v.increase();
		}
*/
		
		String initializer;
		CtExpression initializerExp = var.getAssignment();
		if(initializerExp != null) {
			v.initialize();
			initializer = formularize(initializerExp);
			f.add( wrap(v.getValue(), "=", initializer) ); 
		}
		
		return f;
	}
	
	public String formularize(CtReturn retExp) {
		
		if(retExp.getReturnedExpression() == null)
			return null;
					
		String ret = formularize(retExp.getReturnedExpression());
		if(ret == null) {
			System.out.println("ret is null");
			System.exit(1);
		}
		if(returnVar == null) {
			System.out.println("returnVar is null");
			System.exit(1);
		}
		
		System.out.println("returnvar.getValue: " + returnVar.getValue());
		
		return wrap(returnVar.getValue(), "=", ret); 
	}
	
	public String formularize(CtExpression exp) {
		String f = null;
		
		if(exp instanceof CtBinaryOperator) {			
			f = formularize((CtBinaryOperator) exp);
		}
		else if(exp instanceof CtUnaryOperator) {
			f = formularize((CtUnaryOperator) exp);
		}
		else if(exp instanceof CtVariableAccess) {
			f = formularize((CtVariableAccess) exp);
		}
		else if(exp instanceof CtLiteral) {
			f = formularize((CtLiteral) exp);
		}
		
		return f;
	}
	
	public List<String> formularize(CtAssignment ass) {
		
//		System.out.println("ass: " + ass);
	
		CtExpression left = ass.getAssigned();
		CtExpression right = ass.getAssignment();
		
		Variable v = Variable.getVariable(left.toString(), listVariables);
		
		if(v == null) {
			System.out.println("v is null");
			System.out.println("left: " + left);
			System.exit(1);
		}

		if(v.hasInitialized())
			v.increase();
		else
			v.initialize();
		
		String leftHandSide = v.getValue();
		String rightHandSide = formularize(right);
		
		String s = wrap(leftHandSide, "=", rightHandSide);
		List<String> f = new ArrayList<>();
		f.add(s);
		return f;
	}
	
	public String formularize(CtBinaryOperator binOp) {

		CtExpression left = binOp.getLeftHandOperand();
		CtExpression right = binOp.getRightHandOperand();
		
		String fLeft = formularize(left);
		String fRight = formularize(right);
		String operator = Helper.getBinaryOperator(binOp.getKind());
		
		return wrap(fLeft, operator, fRight);
	}
	
	public String formularize(CtVariableAccess var) {
		Variable v = Variable.getVariable(var.toString(), listVariables);
		if(v == null)
			return "n/a";
		
		return v.getValue();
	}
	
	public String formularize(CtLiteral literal) {
		return literal.toString();
	}
	
	protected boolean hasReturn(CtStatement s) {
		if(s instanceof CtBlock) {
			CtBlock b = (CtBlock) s;
			List<CtStatement> list = b.getStatements();
			for(CtStatement ss: list) {
				if( hasReturn(ss) )
					return true;
			}
		}
		else if(s instanceof CtReturn) {
			return true;
		}
				
		return false;
	}
	
	protected boolean hasBreak(CtStatement s) {
		if(s instanceof CtBlock) {
			CtBlock b = (CtBlock) s;
			List<CtStatement> list = b.getStatements();
			for(CtStatement ss: list) {
				if( hasBreak(ss) )
					return true;
			}
		}
		else if(s instanceof CtBreak) {
			return true;
		}
				
		return false;
	}
	
	protected boolean hasContinue(CtStatement s) {
		if(s instanceof CtBlock) {
			CtBlock b = (CtBlock) s;
			List<CtStatement> list = b.getStatements();
			for(CtStatement ss: list) {
				if( hasReturn(ss) )
					return true;
			}
		}
		else if(s instanceof CtContinue) {
			return true;
		}
				
		return false;
	}
	
	protected String syncVariable(List<Variable> dest, List<Variable> source, List<String> updatedVarsList) {
		if(updatedVarsList == null) 
			return syncVariable(dest, source);
		
		Variable temp;
		String updateExp = null;
		String syncAVar = null;
		String exp;
		
		if (updatedVarsList.size() != 0) {

			for(String v: updatedVarsList) {
				syncAVar = syncAVar(dest, source, v);
				if(syncAVar != null) {
					if(updateExp == null) 
						updateExp = syncAVar;
					else
						updateExp = wrap(syncAVar, "and", updateExp);
				}
			}
		}
		else {
			for(Variable v: dest) {
				temp = Variable.getVariable(v.getName(), source);
				if(temp == null)
					continue;
				if(temp.getIndex() > v.getIndex() ) {
					updatedVarsList.add(v.getName());
					exp = wrap(temp.getValue(), "=", v.getValue());
					if(updateExp == null) 
						updateExp = exp;
					else
						updateExp = wrap(exp, "and", updateExp);
				}
			}
		}
		
		return updateExp;
	}
	
	protected String syncAVar(List<Variable> dest, List<Variable> source, String varName) {
		Variable v1 = Variable.getVariable(varName, dest);
		Variable v2 = Variable.getVariable(varName, source);
		
		if(v1 == null || v2 == null)
			return null;
		
		return wrap(v2.getValue(), "=", v1.getValue());
	}
	
	
	protected String syncVariable(List<Variable> dest, List<Variable> source) {
		Variable temp;
		String updateExp = null;
		String exp;
		for(Variable v: dest) {
			temp = Variable.getVariable(v.getName(), source);
			if(temp == null)
				continue;
			if(v.getIndex() > temp.getIndex() ) {
				exp = wrap(v.getValue(), "=", temp.getValue());
				if(updateExp == null) 
					updateExp = exp;
				else
					updateExp = wrap(exp, "and", updateExp);
			}
		}
		
		return updateExp;
	}
	
	public String wrapAll(List<String> list, String conjunction) {
		if(list == null || list.size() == 0)
			return null;
		
		int size = list.size();
		String str = list.get(size-1);
		if(size == 1)
			return str;
		
		for(int i = size - 2; i >= 0; i--) {
			str = wrap(list.get(i), conjunction, str);
		//	str = list.get(i) + conjuntion + str;
		}
		
		return str;
//		return wrap(str);
	}
	
	protected void init() {
		forFlag = new Variable("for", "bool");
		ifFlag = new Variable("if", "bool");
		returnFlag = new Variable("return", "bool");
		breakFlag = new Variable("break", "bool");
		
		flagVariables = new ArrayList<>();
		flagVariables.add(breakFlag);
		flagVariables.add(returnFlag);
		flagVariables.add(forFlag);
		flagVariables.add(ifFlag);
		
		lastReturnFlag = new StringBox();
	}
	
	protected void assign(	List<Variable> listVariables, 
							List<Variable> flagVariables,
							StringBox lastReturnFlag) 
	{
		this.listVariables = listVariables;
		this.flagVariables = flagVariables;
		this.lastReturnFlag = lastReturnFlag;
		
		forFlag = Variable.getVariable("for", flagVariables);
		ifFlag = Variable.getVariable("if", flagVariables);
		returnFlag = Variable.getVariable("return", flagVariables);
		breakFlag = Variable.getVariable("break", flagVariables);
		
		returnVar = Variable.getVariable("return", listVariables);
		System.out.println("returnvar.getclass: " + returnVar.getClass());
		System.out.println("returnvar: " + returnVar);
	}
	
	
	public String wrap(String exp1, String op, String exp2) {
//		return "(" + exp1 + " " + op + " " + exp2 + ")";
		return "(" + op + " " + exp1 + " " + exp2 + ")";
	}
	
	public String wrap(String op, String exp) {
		return "(" + op + " " + exp + ")";
	}
	
	public String wrap(String exp) {
		return "(" + exp + ")";
	}
	
	void printListVar(List<Variable> list) {
		if(list == null) {
			System.out.println(list + " is null");
			System.exit(1);
		}
		System.out.println("print list variable");
		for(Variable v: list) {
			System.out.println(v);
		}

	}
	
	
	protected List<Variable> listVariables;
	
	protected Variable forFlag;
	protected Variable ifFlag;
	protected Variable returnFlag;
	protected Variable breakFlag;
	protected List<Variable> flagVariables;
	
	protected StringBox lastReturnFlag;
	
	protected Variable returnVar;
	
	List<String> formula;
}
