package it.univr.domain.relational.substring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import it.univr.domain.AbstractValue;
import it.univr.main.antlr.MuJsLexer;
import it.univr.main.antlr.MuJsParser;
import it.univr.main.antlr.MuJsParser.ExpressionContext;
import it.univr.state.Variable;

public class Subs implements AbstractValue  {

	private ArrayList<ExpressionContext> relations;


	public Subs() {
		this.relations = new ArrayList<ExpressionContext>();
	}

	public Subs(ArrayList<ExpressionContext> relations) {
		this.relations = relations;
	}

	public Subs(ExpressionContext exp) {
		this.relations = new ArrayList<ExpressionContext>();
		this.relations.add(exp);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Subs) {
			return containsAll((Subs) other) && ((Subs) other).containsAll(this);
		}
		
		return false;
	}

	public boolean isEmpty() {
		return relations.isEmpty();
	}
	
	public ArrayList<ExpressionContext> getRelations() {
		return relations;
	}

	public void setRelations(ArrayList<ExpressionContext> relations) {
		this.relations = relations;
	}

	@Override
	public AbstractValue juggleToNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue juggleToString() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean contains(ExpressionContext rel) {
		for (ExpressionContext e : getRelations())
			if (e.getText().equals(rel.getText()))
				return true;
		return false;
	}

	
	public boolean containsAll(Subs other) {
		if (getRelations().size() != other.getRelations().size())
			return false;


		for (ExpressionContext e1 : getRelations()) {
			boolean found = false;

			for (ExpressionContext e2 : other.getRelations())
				if (e1.getText().equals(e2.getText())) {
					found = true;
					break;
				}

			if (!found)
				return false;
		}

		
		for (ExpressionContext e2 : other.getRelations()) {
			boolean found = false;

			for (ExpressionContext e1 : getRelations())
				if (e1.getText().equals(e2.getText())) {
					found = true;
					break;
				}

			if (!found)
				return false;
		}

		return true;

	}

	@Override
	public AbstractValue juggleToBool() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractValue leastUpperBound(AbstractValue other) {
		
		if (other instanceof Subs) {
			ArrayList<ExpressionContext> newRelations = new ArrayList<ExpressionContext>();
			
			for (ExpressionContext e1 : getRelations())
				for (ExpressionContext e2 : ((Subs) other).getRelations())
				if (e1.getText().equals(e2.getText())) {
					newRelations.add(e1);
				}			
			return new Subs(newRelations);

		}

		return new Top();
	}

	@Override
	public AbstractValue widening(AbstractValue other) {
		return leastUpperBound(other);
	}

	@Override
	public String toString() {

		if (relations.isEmpty())
			return "T";

		String result = "{";

		for (ParseTree s : relations)
			result += s.getText() + ", ";

		return result.substring(0, result.length() -2) + "}";
	}

	@Override
	public Subs clone() {
		
		Subs subs = new Subs();
		
		for (ExpressionContext exp : relations) {
			try {
				subs.addRelation(parseExpression(exp.getText()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return subs;
	}

	public void addRelation(ExpressionContext rel) {
		for (int i = 0; i < relations.size(); i++)
			if (relations.get(i).getText().equals(rel.getText()))
				return;

		this.relations.add(rel);
	}
	
	public void addRelationExcept(ExpressionContext rel, Variable v) {
		
		for (int i = 0; i < relations.size(); i++)
			if (relations.get(i).getText().equals(rel.getText()) || relations.get(i).getText().equals(v.toString()))
				return;

		this.relations.add(rel);
	}

	public void addRelations(Subs rels) {

		for (ExpressionContext exp : rels.getRelations())
			addRelation(exp);
	}
	
	public void addRelationsExcept(Subs rels, Variable v) {

		for (ExpressionContext exp : rels.getRelations())
			addRelationExcept(exp, v);
	}
	

	public void removeRelation(ExpressionContext rel) {
		for (int i = 0; i < relations.size(); i++)
			if (relations.get(i).getText().equals(rel.getText()))
				relations.remove(i);

	}

	public void removeRelations(Subs rels) {

		for (ExpressionContext exp : rels.getRelations())
			removeRelation(exp);
	}

	@Override
	public AbstractValue greatestLowerBound(AbstractValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ExpressionContext parseExpression(String exp) throws IOException {
		InputStream stream = new ByteArrayInputStream(exp.getBytes());
		MuJsLexer lexer = new MuJsLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		MuJsParser parser = new MuJsParser(new CommonTokenStream(lexer));
		ExpressionContext tree = parser.expression();
		return tree;
	}
	
	@Override
	public AbstractValue narrowing(AbstractValue value) {
		return greatestLowerBound(value);
	}

}
