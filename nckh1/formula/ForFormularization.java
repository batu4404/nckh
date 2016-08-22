package formula;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;

public class ForFormularization extends Formularization {
	
	public ForFormularization(	CtFor forLoop, 
								List<Variable> listVariables,
								List<Variable> flagVariables,
								StringBox lastReturnFlag) 
	{
		loop = forLoop;
		assign(listVariables, flagVariables, lastReturnFlag);
		
		
		forFlag = Variable.getVariable("for", flagVariables);
		if( !forFlag.hasInitialized())
			forFlag.initialize();
		else
			forFlag.increase();
		
		
	}

	@Override
	public List<String> getFormula() {
		if(formula == null) {
			formula = new ArrayList<>();
			formularize();
		}
		
		return formula;
	}
	
	// cho lap so lan mac dinh 
	public void formularize() {
		List<String> f = new ArrayList<>();
		List<Variable> sync = Helper.copyList(listVariables);
		
		
		List<CtStatement> forInit = loop.getForInit();
		List<String> init = formularize(forInit, null);
		formula.addAll(init);
		
		List<CtStatement> forUpdate = loop.getForUpdate();
		CtStatement body = loop.getBody();

		
		
		String loopBody = null;
		String aLoop = null;
		int nLoop = defaultNumOfLoop;
		
		String condition = null;
		List<String> updateVarsExp = null;
		
		List<String> temp;
		
		List<String> updatedVarsList = new ArrayList<>();
		String conditionAssign = null;
		String forLoopValue = null;
		
		
		for(int i = 0; i < nLoop; i++) {
			
			condition = formularize(loop.getExpression());
			if(condition != null) {
				if(forLoopValue != null)
					condition = wrap(forLoopValue, "and", condition);
				conditionAssign = wrap(forFlag.getValue(), "=", condition);
				formula.add(conditionAssign);
				forLoopValue = forFlag.getValue();
			}
			
			temp = formularize(body, null);
			List<String> update = formularize(forUpdate, null);
			temp.addAll(update);
			
			loopBody = wrapAll(temp, "and");
			if(condition == null) {
				formula.add(loopBody);
			}
			else {
				aLoop = wrap(forLoopValue, "=>", loopBody);
				formula.add(aLoop);
				updateVarsExp = syncVariable(sync, listVariables, updatedVarsList);
				if(updateVarsExp != null)
					formula.add(wrap(wrap("not", forLoopValue), "=>", wrapAll(updateVarsExp, "and") ));
			}
			sync = Helper.copyList(listVariables);
		}
	}
	
	private CtFor loop;
	
	protected int defaultNumOfLoop = 4;

}
