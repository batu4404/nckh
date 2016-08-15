package formula;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;

public class IfFormularization extends Formularization {

	public IfFormularization(	CtIf ifs, 
								List<Variable> listVariables,
								List<Variable> flagVariables,
								StringBox lastReturnFlag) 
	{
		this.ifs = ifs;
		thenStatement = ifs.getThenStatement();
		elseStatement = ifs.getElseStatement();
		
		assign(listVariables, flagVariables, lastReturnFlag);
		
		if( !ifFlag.hasInitialized())
			ifFlag.initialize();
		else 
			ifFlag.increase();
		
	}
	
	@Override
	public List<String> getFormula() {
		
		if(formula == null) {
			formula = new ArrayList<>();
			formularize();
		}
		
		return formula;
	}
	
	// assign condition of if statement to ifFlag
	private void assignCondition() {
		CtExpression<Boolean> conditionExp = ifs.getCondition();
		String condition = formularize(conditionExp);
		ifFlagStr = ifFlag.getValue();
		String assignment = wrap(ifFlagStr, "=", condition);
		formula.add(assignment);
	}
	
	// formularize then statement
	private void formularizeThenStatement() {
		
		List<Variable> backup = listVariables;
		thenListVars = Helper.copyList(listVariables);
		listVariables = thenListVars;
		
		StringBox temp = lastReturnFlag;
		lastReturnFlag = lastReturnThenFlag;
		
		fThen = formularize(thenStatement, null);
		
		lastReturnFlag = temp;
		
		listVariables = backup; 
	}
	
	// formularize else statement
	private void formularizeElseStatement() {
		List<Variable> backup = listVariables;
		elseListVars = Helper.copyList(listVariables);
		listVariables = elseListVars;
		
		StringBox temp = lastReturnFlag;
		lastReturnFlag = lastReturnElseFlag;
		
		fElse = formularize(elseStatement, null);
		
		lastReturnFlag = temp;
		
		listVariables = backup;
	}
	
	
	// cap nhat lai index cua danh sach cac bien
	private void updateIndex() {
		Variable v1, v2;
		for(Variable v: listVariables) {
//			System.out.println("v: " + v);
			v1 = Variable.getVariable(v.getName(), thenListVars);
			v2 = Variable.getVariable(v.getName(), elseListVars);
			if( v1.getIndex() > v2.getIndex()) 
				v.setIndex(v1.getIndex());
			else
				v.setIndex(v2.getIndex());
		}
	}
	
	
	// tim phan them vao
	private void findReturnAddition() {
		System.out.println("find return");
		
		if( hasReturn(thenStatement) ) {
			System.out.println("then has return");
			returnFlag.increase();
			returnFlagValue = returnFlag.getValue();
			thenAddition = wrap(returnFlagValue, "=", "true");
			elseAddition = syncVariable(listVariables, elseListVars);
			
			
			if(lastReturnElseFlag.getString() != null)
				elseAddition = wrap(elseAddition, "and", wrap(returnFlagValue, "=", lastReturnElseFlag.getString()));
			else {
				System.out.println("returnflagvalue = false");
				elseAddition = wrap(elseAddition, "and", wrap(returnFlagValue, "=", "false"));
			}
				
		} else if( hasReturn(elseStatement) ) {
			returnFlag.increase();
			returnFlagValue = returnFlag.getValue();
			elseAddition = wrap(returnFlagValue, "=", "true");
			thenAddition = syncVariable(listVariables, thenListVars);
			if(lastReturnThenFlag.getString() != null)
				wrap(thenAddition, "and", wrap(returnFlagValue, "=", lastReturnThenFlag.getString()));
			else
				wrap(thenAddition, "and", wrap(returnFlagValue, "=", "false"));
		} else {
			thenAddition = syncVariable(listVariables, thenListVars);
			elseAddition = syncVariable(listVariables, elseListVars);
			
		}
	}

	
	private void makeFormula() {
		String f;
		
		if(thenAddition != null)
			fThen.add(thenAddition);
		f = wrapAll(fThen, "and");
		if(f != null)
			formula.add( wrap(ifFlagStr, "=>", f) );
		
		if(elseAddition != null)
			fElse.add(elseAddition);
		f = wrapAll(fElse, "and");
		if(f != null)
			formula.add( wrap(wrap("not", ifFlagStr), "=>", f) );
		
		
		if(lastReturnFlag.getString() == null) {
			System.out.println("getString is null");
			if(returnFlagValue != null) {
				lastReturnFlag.setString(returnFlagValue);
			}
		} else {
			String temp1 = wrap(wrap("not", lastReturnFlag.getString()), "=>", wrapAll(formula, "and"));
			System.out.println("temp1: " + temp1);
			String temp2 = null;
			
			if(returnFlagValue != null) {
				temp2 = wrap(lastReturnFlag.getString(), "=>", wrap(returnFlagValue, "=", lastReturnFlag.getString()));
			}
			
			formula.clear();
			formula.add(temp1);
			if(temp2 != null)
				formula.add(temp2);
		}
	}
	
	private void formularize() {
		
		assignCondition();
		
		formularizeThenStatement();
		formularizeElseStatement();
	
		updateIndex();
		
		findReturnAddition();
		
		Variable.addVariable(listVariables, thenListVars);
		Variable.addVariable(listVariables, elseListVars);
		
		makeFormula();
	
	}
	
	
	private CtIf ifs;
	
	String ifFlagStr;
	
	String returnFlagValue;
	
	StringBox ownReturnFlag;
	
	StringBox lastReturnThenFlag = new StringBox();
	StringBox lastReturnElseFlag = new StringBox();
	
	CtStatement thenStatement;
	CtStatement	elseStatement;

	private List<String> fThen;	// formula of then statement
	private List<String> fElse;  	// formula of else statement
	
	private String thenAddition;	// phan them vao bieu thuc then nhu: return, break, continue hay dong bo gia tri cac bien
	private String elseAddition;	// phan them vao bieu thuc else nhu: return, break, continue hay dong bo gia tri cac bien
	
	private List<Variable> thenListVars;	// danh sach cac bien sau khi da cong thuc then
	private List<Variable> elseListVars;	// danh sach cac bien sau khi da cong thuc else
}
