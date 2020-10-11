package it.univr.state;

import java.util.HashMap;

import dnl.utils.text.table.TextTable;
import it.univr.domain.AbstractDomain;
import it.univr.domain.AbstractValue;
import it.univr.state.functions.KCallStrings;

public class CallStringAbstractStore extends HashMap<KCallStrings, AbstractStore> {

	private AbstractDomain domain;

	public CallStringAbstractStore(AbstractDomain domain) {
		super();
		this.domain = domain;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public AbstractStore get(Object cs) {
		if (cs instanceof KCallStrings) {
			if (containsKey(cs))
				return super.get(cs);
			else
				return new AbstractStore(domain);
		}

		throw new NullPointerException();
	}

	public CallStringAbstractStore(AbstractDomain domain, AbstractStore store, KCallStrings cs) {
		super();
		this.setAbstractDomain(domain);
		put(cs, store);
	}

	public CallStringAbstractStore(AbstractDomain domain, KCallStrings cs) {
		super();
		this.setAbstractDomain(domain);
		put(cs, new AbstractStore(domain));
	}


	public void printTable() {
		String[] columns = {"Call String", "Abstract environment", "Abstract Value"};

		int i = 0;
		int n = keySet().size();
		String[][] t = new String[n][3];

		for (KCallStrings cs : keySet()) {

			AbstractStore store = getStore(cs);

			t[i][0] = cs.toString();

			for (Variable v : store.keySet()) {
				t[i][1] = v.toString();
				t[i][2] = store.getValue(v).toString();
			}

			i++;
		}

		TextTable table = new TextTable(columns, t);
		table.printTable();
	}

	public AbstractDomain getAbstractDomain() {
		return domain;
	}

	public void setAbstractDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public AbstractStore getStore(KCallStrings cs) {
		return get(cs);
	}


	public void putVariable(Variable var, AbstractValue v, KCallStrings cs) {
		getStore(cs).put(var, v);
	}

	public void removeVariable(Variable var, KCallStrings cs) {
		getStore(cs).remove(var);
	}

	@Override
	public CallStringAbstractStore clone() {
		CallStringAbstractStore clone = new CallStringAbstractStore(domain);

		for (KCallStrings cs : keySet())
			clone.put(cs, get(cs).clone());

		return clone;
	}

	public  CallStringAbstractStore leastUpperBound(CallStringAbstractStore other) {
		CallStringAbstractStore lub = new CallStringAbstractStore(domain);

		for (KCallStrings cs : keySet()) 
			lub.put(cs, get(cs).leastUpperBound(other.get(cs)));

		for (KCallStrings cs : other.keySet()) 
			if (!containsKey(cs))
				lub.put(cs, other.get(cs).clone());

		return lub;
	}
	

	public  CallStringAbstractStore widening(CallStringAbstractStore other) {
		CallStringAbstractStore lub = new CallStringAbstractStore(domain);

		for (KCallStrings cs : keySet()) 
			lub.put(cs, get(cs).widening(other.get(cs)));

		for (KCallStrings cs : other.keySet()) 
			if (!containsKey(cs))
				lub.put(cs, other.get(cs).clone());

		return lub;
	}
}
