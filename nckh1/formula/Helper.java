package formula;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.UnaryOperatorKind;

public class Helper {
	public static List<Variable> copyList(List<Variable> list) {
		List<Variable> copiedList = new ArrayList<>();
		
		for(Variable v: list) {
			copiedList.add(v.clone());
		}
		
		return copiedList;
	}
	
	public static String getBinaryOperator(BinaryOperatorKind operator) {
		String opStr = "";
		if(operator == BinaryOperatorKind.PLUS)
			opStr = "+";
		else if(operator == BinaryOperatorKind.MINUS)
			opStr = "-";
		else if(operator == BinaryOperatorKind.DIV)
			opStr = "/";
		else if(operator == BinaryOperatorKind.MUL)
			opStr = "*";
		else if(operator == BinaryOperatorKind.LT)
			opStr = "<";
		else if(operator == BinaryOperatorKind.LE)
			opStr = "<=";
		else if(operator == BinaryOperatorKind.GT)
			opStr = ">";
		else if(operator == BinaryOperatorKind.GE)
			opStr = ">=";
		else if(operator == BinaryOperatorKind.EQ)
			opStr = "==";
		else if(operator == BinaryOperatorKind.AND)
			opStr = "&&";
		else if(operator == BinaryOperatorKind.OR)
			opStr = "||";
		
		return opStr;
	}
	
	public static String getUnaryOperator(UnaryOperatorKind operator) {
		String opStr = "n/a";
		if(operator == UnaryOperatorKind.POSTDEC)
			opStr = "-";
		else if(operator == UnaryOperatorKind.PREDEC)
			opStr = "-";
		else if(operator == UnaryOperatorKind.POSTINC)
			opStr = "+";
		else if(operator == UnaryOperatorKind.PREINC)
			opStr = "+";
		else if(operator == UnaryOperatorKind.NOT)
			opStr = "not";
		
		return opStr;
	}

}
