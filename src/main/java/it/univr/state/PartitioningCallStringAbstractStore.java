package it.univr.state;

import java.util.HashMap;
import java.util.HashSet;

import dnl.utils.text.table.TextTable;
import it.univr.domain.AbstractDomain;
import it.univr.state.functions.KCallStrings;

public class PartitioningCallStringAbstractStore extends HashMap<Integer, CallStringAbstractStore>{

	private AbstractDomain domain;

	public PartitioningCallStringAbstractStore(AbstractDomain domain) {
		super();
		this.domain = domain;
	}


	@Override
	public CallStringAbstractStore get(Object token) {
		if (token instanceof Integer) {
			if (containsKey(token))
				return super.get(token);
			else
				return new CallStringAbstractStore(domain);
		}

		throw new NullPointerException();
	}

	public PartitioningCallStringAbstractStore(AbstractDomain domain, CallStringAbstractStore cStore, Integer token) {
		super();
		this.setAbstractDomain(domain);
		put(token, cStore);
	}

	public void setCallStringAbstractStoreAt(Integer token, CallStringAbstractStore csStore) {
		put(token, csStore);
	}

	public void printTable() {
		String[] columns = {"Token", "Call String", "Store"};

		int i = 0;
		int n = keySet().size();
		String[][] t = new String[n][3];

		for (Integer tok : keySet()) {
			for (KCallStrings cs : get(tok).keySet()) {

				t[i][0] = tok.toString();
				t[i][1] = cs.toString();
				t[i][2] = get(tok).get(cs).toString();
				i++;
			}
		}

		TextTable table = new TextTable(columns, t);
		table.printTable();
	}

	public PartitioningCallStringAbstractStore(AbstractDomain domain, Integer token) {
		super();
		this.setAbstractDomain(domain);
		put(token, new CallStringAbstractStore(domain));
	}

	public AbstractDomain getAbstractDomain() {
		return domain;
	}

	public void setAbstractDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public CallStringAbstractStore getCallStringStoreAtToken(Integer token) {
		return get(token);
	}

	@Override
	public PartitioningCallStringAbstractStore clone() {
		PartitioningCallStringAbstractStore clone = new PartitioningCallStringAbstractStore(domain);

		for (Integer cs : keySet())
			clone.put(cs, get(cs).clone());

		return clone;
	}

	public  PartitioningCallStringAbstractStore leastUpperBound(PartitioningCallStringAbstractStore other) {
		PartitioningCallStringAbstractStore lub = new PartitioningCallStringAbstractStore(domain);


		for (Integer token : keySet())
			if (other.containsKey(token)) 
				lub.put(token, get(token).leastUpperBound(other.get(token)));
			else {
				lub.put(token, get(token).clone());
			}
		
		for (Integer cs : other.keySet()) 
			if (!containsKey(cs))
				lub.put(cs, other.get(cs).clone());

		return lub;
	}


	public void joinIfEquals() {

		HashSet<Integer> toRemove = new HashSet<Integer>();
		HashSet<Integer> toMantain = new HashSet<Integer>();

		for (Integer i1 : keySet())
			for (Integer i2 : keySet())
				if (!i1.equals(i2) && get(i1).equals(get(i2)) && !toMantain.contains(i1)) {
					toRemove.add(i1);
					toMantain.add(i2);
				}
		
		for (Integer i : toRemove)
			remove(i);
	}
	
	public void impera() {

		PartitioningCallStringAbstractStore previous;
		
		do {
			previous = clone();
			joinIfEquals();
		} while (!equals(previous));
		
	}

	public  PartitioningCallStringAbstractStore widening(PartitioningCallStringAbstractStore other) {
		PartitioningCallStringAbstractStore lub = new PartitioningCallStringAbstractStore(domain);

		for (Integer cs : keySet()) 
			lub.put(cs, get(cs).widening(other.get(cs)));

		for (Integer cs : other.keySet()) 
			if (!containsKey(cs))
				lub.put(cs, other.get(cs).clone());

		return lub;
	}
}
