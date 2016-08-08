package Abstraction;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;

public class Helper {
	public static List<Variable> copyList(List<Variable> list) {
		List<Variable> copiedList = new ArrayList<>();
		
		for(Variable v: list) {
			copiedList.add(new Variable(v));
		}
		
		return copiedList;
	}
	
	public static String getBinaryOperator(BinaryOperatorKind binOperator) {
		String binOperatorStr = "";
		if(binOperator == BinaryOperatorKind.PLUS)
			binOperatorStr = "+";
		else if(binOperator == BinaryOperatorKind.MINUS)
			binOperatorStr = "-";
		else if(binOperator == BinaryOperatorKind.DIV)
			binOperatorStr = "/";
		else if(binOperator == BinaryOperatorKind.MUL)
			binOperatorStr = "*";
		else if(binOperator == BinaryOperatorKind.LT)
			binOperatorStr = "<";
		else if(binOperator == BinaryOperatorKind.LE)
			binOperatorStr = "<=";
		else if(binOperator == BinaryOperatorKind.GT)
			binOperatorStr = ">";
		else if(binOperator == BinaryOperatorKind.GE)
			binOperatorStr = ">=";
		else if(binOperator == BinaryOperatorKind.EQ)
			binOperatorStr = "==";
		else if(binOperator == BinaryOperatorKind.AND)
			binOperatorStr = "&&";
		else if(binOperator == BinaryOperatorKind.OR)
			binOperatorStr = "||";
		
		return binOperatorStr;
	}

}
