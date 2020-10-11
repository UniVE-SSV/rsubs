package it.univr.domain.relational.substring;

import it.univr.domain.AbstractValue;

public class Top implements AbstractValue {

	@Override
	public AbstractValue leastUpperBound(AbstractValue other) {
		return new Top();
	}

	@Override
	public AbstractValue widening(AbstractValue other) {
		return new Top();
	}

	@Override
	public String toString() {
		return "T";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Top ? true : false;
	}
	
	@Override
	public int hashCode() {
		return "T".hashCode();
	}

	@Override
	public AbstractValue juggleToNumber() {
		return Interval.makeTopInterval();
	}

	@Override
	public AbstractValue juggleToString() {
		return null; //return new FA(Automaton.makeTopLanguage());
	}

	@Override
	public AbstractValue juggleToBool() {
		return new Bool(2);
	}
	
	@Override
	public AbstractValue clone() {
		return new Top();
	}

	@Override
	public AbstractValue greatestLowerBound(AbstractValue value) {
		if (value instanceof Interval) return ((Interval) value).clone();
		if (value instanceof Bottom) return new Bottom();
		if (value instanceof Subs) return ((Subs) value).clone();
		if (value instanceof NaN) return ((NaN) value).clone();
		if (value instanceof Bool) return ((Bool) value).clone();
		
		return new Top();
	}
	
	@Override
	public AbstractValue narrowing(AbstractValue value) {
		return greatestLowerBound(value);
	}
}
