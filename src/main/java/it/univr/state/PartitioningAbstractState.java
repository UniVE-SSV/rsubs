package it.univr.state;

import java.util.HashMap;
import it.univr.state.functions.Function;

public class PartitioningAbstractState  {

	private HashMap<Variable, Function> functions;
	private HashMap<KeyAbstractState, PartitioningCallStringAbstractStore> state;
	
	public PartitioningAbstractState() {
		this.functions = new HashMap<Variable, Function>();
		this.state = new HashMap<KeyAbstractState, PartitioningCallStringAbstractStore>();
	}


	public HashMap<Variable, Function> getFunctions() {
		return functions;
	}
	
	public void addFunction(Variable name, Function function) {
		functions.put(name, function);
	}

	public PartitioningCallStringAbstractStore getCallStringEnvironment(KeyAbstractState key) {
		return state.get(key);
	}
	
	public void add(KeyAbstractState key, PartitioningCallStringAbstractStore store) {
		state.put(key, store);
	}
	
	public void setCallStringEnvironmentAt(KeyAbstractState key, Integer token, CallStringAbstractStore csStore) {
		state.get(key).setCallStringAbstractStoreAt(token, csStore);
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
	
	public Function getFunction(Variable variable) {
		return functions.get(variable);
	}
	
}
