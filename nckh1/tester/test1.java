package tester;

public class Test1 {
	public static boolean checkBracket(String exp) {
		int length = exp.length();
		int open = 0;
		for(int i = 0; i < length; i++) {
			if(exp.charAt(i) == '(')
				open++;
			else if(exp.charAt(i) == ')')
				open--;
			if(open < 0)
				return false;
		}
			
		if(open == 0)
			return true;
		else
			return false;
	}
}
