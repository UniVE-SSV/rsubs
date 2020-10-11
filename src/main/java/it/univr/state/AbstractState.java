package it.univr.state;

import java.util.HashMap;

import it.univr.domain.AbstractDomain;
import it.univr.state.functions.Function;
import it.univr.state.functions.KCallStrings;

public class AbstractState {

	private AbstractDomain domain;
	private HashMap<Variable, Function> functions;
	public HashMap<KeyAbstractState, CallStringAbstractStore> state;
	
	public AbstractState(AbstractDomain domain) {
		this.functions = new HashMap<Variable, Function>();
		this.state = new HashMap<KeyAbstractState, CallStringAbstractStore>();
		this.domain = domain;
	}

	public AbstractState clone() {
		AbstractState res = new AbstractState(domain);
		res.state = (HashMap<KeyAbstractState, CallStringAbstractStore>) this.state.clone();
		res.functions = (HashMap<Variable, Function>) this.functions.clone();
		return res;
	}
	
	public void add(KeyAbstractState key, AbstractStore m, KCallStrings cs) {
		if (state.containsKey(key)) {
			
			if (state.get(key).containsKey(cs)) {
				AbstractStore newEnv = state.get(key).get(cs).leastUpperBound(m);				
				state.get(key).put(cs, newEnv);
			} else {
				state.get(key).put(cs, m);
			}
		} else {
			state.put(key, new CallStringAbstractStore(domain, m, cs));

		}
	}

	public HashMap<Variable, Function> getFunctions() {
		return functions;
	}
	
	public void addFunction(Variable name, Function function) {
		functions.put(name, function);
	}

	public CallStringAbstractStore getCallStringEnvironment(KeyAbstractState key) {
		return state.get(key);
	}
	
	@Override
	public String toString() {
		String result = "";
		
		for (KeyAbstractState k : state.keySet()) {
			result += "\n*******************\n";
			result += "Line " + k.getRow() +", Column " + k.getCol() + "\n";
			result += state.get(k).toString();
		}
		
		return result;
	}
	
	public boolean contains(KeyAbstractState key) {
		return state.containsKey(key);
	}
	
	public void createAbstractEnvironment(KeyAbstractState key, KCallStrings cs) {
		state.put(key, new CallStringAbstractStore(domain));
		state.get(key).put(cs, new AbstractStore(domain));
	}
	
		

	public Function getFunction(Variable variable) {
		return functions.get(variable);
	}
}
