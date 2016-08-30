package formula2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

public class MethodFormularization extends Formularization {
	
	
	
	public MethodFormularization(CtMethod method) {
		this.method = method;
		
		List<CtParameter> parameters = method.getParameters();
		listVariables = new ArrayList<Variable>();
		
		Variable varTemp;
		for(CtParameter p: parameters) {
			varTemp = new Variable(p.getSimpleName(), p.getType().toString());
			varTemp.initialize();
			listVariables.add(varTemp);
		}

		returnType = method.getType().toString();
		
		init();
		
		formula = formularize(method.getBody(), lastReturnFlag, null, null);
	}
	
	public void printFormula() {
		for(String s: formula) {
			System.out.println(s);
		}
	}

	@Override
	public List<String> getFormula() {
		return formula;
	}
	
	
	public static void main(String[] args) {

		LauncherSpoon launcher = new LauncherSpoon();
		String pathFile = "TestSpoon.java";
//		pathFile = "test.java";
		File resource = new File(pathFile);
		if(!resource.exists()) {
			System.err.println("cannot open file");
			System.exit(1);
		}
		launcher.addInputResource(pathFile);
		launcher.buildModel();
		launcher.foo2();
	}

	private CtMethod method;
	
	private String returnType;
}
