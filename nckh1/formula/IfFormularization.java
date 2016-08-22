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
		
		ifFlag = Variable.getVariable("if", flagVariables);
		
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
		
		List<Variable> lvBackup = listVariables;
		thenListVars = Helper.copyList(listVariables);
		listVariables = thenListVars;
		
		List<Variable> flagsBackup = flagVariables;
		thenFlags = Helper.copyList(flagVariables);
		flagVariables = thenFlags;
		
		
		fThen = formularize(thenStatement, thenReturnFlagVal, null, null);
		
		listVariables = lvBackup; 
		
		flagVariables = flagsBackup;
	}
	
	// formularize else statement
	private void formularizeElseStatement() {
		List<Variable> lvBackup = listVariables;
		elseListVars = Helper.copyList(listVariables);
		listVariables = elseListVars;
		
		List<Variable> flagsBackup = flagVariables;
		elseFlags = Helper.copyList(flagVariables);
		flagVariables = elseFlags;
		
		
		fElse = formularize(elseStatement, elseReturnFlagVal, null, null);
		
		listVariables = lvBackup;
		
		flagVariables = flagsBackup;
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
		
		for(Variable v: flagVariables) {
//			System.out.println("v: " + v);
			v1 = Variable.getVariable(v.getName(), thenFlags);
			v2 = Variable.getVariable(v.getName(), elseFlags);
			if( v1.getIndex() > v2.getIndex()) 
				v.setIndex(v1.getIndex());
			else
				v.setIndex(v2.getIndex());
		}
	}
	
	
	// tim phan them vao
	private void findAddition() {
		syncReturnFlag();
		
		if( syncThen)
			thenAddition.addAll( syncVariable(listVariables, thenListVars) );
		if( syncElse)
			elseAddition.addAll( syncVariable(listVariables, elseListVars) );
	}
	
	private void syncReturnFlag() {
		if(thenReturnFlagVal.getString() == null) {
			if(elseReturnFlagVal.getString() != null) {
				syncElse = false;
				thenAddition.add( wrap(elseReturnFlagVal.getString(), "=", "false") );
			}
		} else if(elseReturnFlagVal.getString() == null) {
			System.out.println("else is null");
			if(thenReturnFlagVal.getString() != null) {
				syncThen = false;
				elseAddition.add( wrap(thenReturnFlagVal.getString(), "=", "false") );
			}
		} else if(thenReturnFlagVal.getString() != null && elseReturnFlagVal.getString() != null) {
			String temp;
			temp = syncAVar(thenFlags, flagVariables, "return");
			if(temp != null) {
				lastReturnFlag.setString(temp);
			}
				
			
			temp = syncAVar(elseFlags, flagVariables, "return");
			if(temp != null) {
				lastReturnFlag.setString(temp);
			}
		}
		
		lastReturnFlag.setString(returnFlag.getValue());
	}

	
	private void makeFormula() {
		String f;
		
		if(thenAddition != null)
			fThen.addAll(thenAddition);
		f = wrapAll(fThen, "and");
		if(f != null)
			formula.add( wrap(ifFlagStr, "=>", f) );
		
		if(elseAddition != null)
			fElse.addAll(elseAddition);
		f = wrapAll(fElse, "and");
		if(f != null)
			formula.add( wrap(wrap("not", ifFlagStr), "=>", f) );
		
	}
	
	private void formularize() {
		
		assignCondition();
		
		formularizeThenStatement();
		formularizeElseStatement();
	
		updateIndex();
		
		findAddition();
		
		Variable.addVariable(listVariables, thenListVars);
		Variable.addVariable(listVariables, elseListVars);
		
		makeFormula();
	
	}
	
	
	private CtIf ifs;
	
	String ifFlagStr;
	
	String returnFlagValue;
	
	StringBox ownReturnFlag;
	
	StringBox thenReturnFlagVal = new StringBox();
	StringBox elseReturnFlagVal = new StringBox();
	
	CtStatement thenStatement;
	CtStatement	elseStatement;

	private List<String> fThen;	// formula of then statement
	private List<String> fElse;  	// formula of else statement
	
	private List<String> thenAddition = new ArrayList<>();	// phan them vao bieu thuc then nhu: return, break, continue hay dong bo gia tri cac bien
	private List<String> elseAddition = new ArrayList<>();	// phan them vao bieu thuc else nhu: return, break, continue hay dong bo gia tri cac bien
	
	private List<Variable> thenListVars;	// danh sach cac bien sau khi da cong thuc then
	private List<Variable> elseListVars;	// danh sach cac bien sau khi da cong thuc else
	
	private List<Variable> thenFlags;
	private List<Variable> elseFlags;
	
	private boolean syncThen = true;
	private boolean syncElse = true;
}
