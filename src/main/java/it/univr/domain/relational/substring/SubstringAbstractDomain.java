package it.univr.domain.relational.substring;

import it.univr.domain.AbstractDomain;
import it.univr.domain.AbstractValue;

public class SubstringAbstractDomain extends AbstractDomain {

	@Override
	public AbstractValue juggleToNumber(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue juggleToString(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue juggleToBool(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue leastUpperBound(AbstractValue v1, AbstractValue v2) {
		
		if (v1 instanceof Bottom && (v2 instanceof Subs))
			return new Subs();
		else if (v2 instanceof Bottom && (v1 instanceof Subs))
			return new Subs();
		
		if (v1 instanceof Bottom)
			return v2.clone();
		else if (v2 instanceof Bottom)
			return v1.clone();
		
		else if (v1.getClass().equals(v2.getClass()))
			return v1.leastUpperBound(v2);

		return new Top();
	}

	@Override
	public AbstractValue widening(AbstractValue v1, AbstractValue v2) {
		if (v1 instanceof Bottom)
			return v2.clone();
		else if (v2 instanceof Bottom)
			return v1.clone();
		else if (v1.getClass().equals(v2.getClass()))
			return v1.widening(v2);

		return new Top();
	}

	@Override
	public AbstractValue greatestLowerBound(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue narrowing(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue greater(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue less(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue sum(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue diff(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue mul(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue substring(AbstractValue v1, AbstractValue v2, AbstractValue v3) {
		// Not tracket by substring relational abstract domain
		return new Bottom();
	}

	@Override
	public AbstractValue charAt(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue indexOf(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue length(AbstractValue v1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue not(AbstractValue v1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue equals(AbstractValue v1, AbstractValue v2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue and(AbstractValue first, AbstractValue second) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue or(AbstractValue first, AbstractValue second) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue div(AbstractValue left, AbstractValue right) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue makeInterval(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue makeFA(AbstractValue v) {
		return null;
	}

	@Override
	public AbstractValue makeNaN(AbstractValue v) {
		// Not tracket by substring relational abstract domain
		return new Bottom();
	}

	@Override
	public AbstractValue makeBool(AbstractValue v) {
		// Not tracket by substring relational abstract domain
		return new Bottom();
	}

	@Override
	public AbstractValue makeBottom() {
		return new Bottom();
	}

	@Override
	public boolean isTrue(AbstractValue v) {
		AbstractValue r = juggleToBool(v);
		return r instanceof Bool && ((Bool) r).isTrue();	
	}

	@Override
	public boolean isFalse(AbstractValue v) {
		AbstractValue r = juggleToBool(v);
		return r instanceof Bool && ((Bool) r).isFalse();	}

	@Override
	public boolean isTopBool(AbstractValue v) {
		AbstractValue r = juggleToBool(v);
		return r instanceof Bool && ((Bool) r).isTopBool();
	}

	@Override
	public AbstractValue makeUnknownInteger() {
		return new Interval("-Inf", "+Inf");
	}

	@Override
	public AbstractValue includes(AbstractValue left, AbstractValue right) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue repeat(AbstractValue left, AbstractValue right) {
		// Not tracket by substring relational abstract domain
				return new Bottom();
	}

	@Override
	public AbstractValue startsWith(AbstractValue left, AbstractValue right) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue endsWith(AbstractValue left, AbstractValue right) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue trim(AbstractValue par) {
		// Not tracket by substring relational abstract domain
		return new Bottom();
	}

	@Override
	public AbstractValue trimLeft(AbstractValue par) {
		// Not tracket by substring relational abstract domain
				return new Bottom();
	}

	@Override
	public AbstractValue trimRight(AbstractValue par) {
		// Not tracket by substring relational abstract domain
				return new Bottom();
	}

	@Override
	public AbstractValue toUpperCase(AbstractValue par) {
		// Not tracket by substring relational abstract domain
				return new Bottom();
	}

	@Override
	public AbstractValue toLowerCase(AbstractValue par) {
		// Not tracket by substring relational abstract domain
				return new Bottom();
	}

	@Override
	public AbstractValue replace(AbstractValue a, AbstractValue b, AbstractValue c) {
		// Not tracket by substring relational abstract domain
				return new Bottom();
	}

	@Override
	public AbstractValue slice(AbstractValue a, AbstractValue b, AbstractValue c) {
		// Not tracket by substring relational abstract domain
		return new Bottom();
	}

}
