package it.univr.main.relational;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import it.univr.domain.AbstractDomain;
import it.univr.domain.AbstractValue;
import it.univr.domain.relational.substring.Bottom;
import it.univr.domain.relational.substring.Subs;
import it.univr.domain.relational.substring.Top;
import it.univr.main.antlr.MuJsBaseVisitor;
import it.univr.main.antlr.MuJsLexer;
import it.univr.main.antlr.MuJsParser;
import it.univr.main.antlr.MuJsParser.AndContext;
import it.univr.main.antlr.MuJsParser.AssignmentStmtContext;
import it.univr.main.antlr.MuJsParser.BlockContext;
import it.univr.main.antlr.MuJsParser.BlockStmtContext;
import it.univr.main.antlr.MuJsParser.BooleanContext;
import it.univr.main.antlr.MuJsParser.CharAtContext;
import it.univr.main.antlr.MuJsParser.CompositionContext;
import it.univr.main.antlr.MuJsParser.DiffContext;
import it.univr.main.antlr.MuJsParser.DivContext;
import it.univr.main.antlr.MuJsParser.EndsWithContext;
import it.univr.main.antlr.MuJsParser.EqualsContext;
import it.univr.main.antlr.MuJsParser.ExpressionContext;
import it.univr.main.antlr.MuJsParser.FunctionCallContext;
import it.univr.main.antlr.MuJsParser.FunctionDeclarationContext;
import it.univr.main.antlr.MuJsParser.GreaterContext;
import it.univr.main.antlr.MuJsParser.IdentifierContext;
import it.univr.main.antlr.MuJsParser.IfStmtContext;
import it.univr.main.antlr.MuJsParser.ContainsContext;
import it.univr.main.antlr.MuJsParser.IndexOfContext;
import it.univr.main.antlr.MuJsParser.IntegerContext;
import it.univr.main.antlr.MuJsParser.LengthContext;
import it.univr.main.antlr.MuJsParser.LessContext;
import it.univr.main.antlr.MuJsParser.MulContext;
import it.univr.main.antlr.MuJsParser.NaNContext;
import it.univr.main.antlr.MuJsParser.NotContext;
import it.univr.main.antlr.MuJsParser.NotEqualsContext;
import it.univr.main.antlr.MuJsParser.ObjectExpressionContext;
import it.univr.main.antlr.MuJsParser.OrContext;
import it.univr.main.antlr.MuJsParser.ParenthesisContext;
import it.univr.main.antlr.MuJsParser.PrimitiveValueContext;
import it.univr.main.antlr.MuJsParser.ProgramContext;
import it.univr.main.antlr.MuJsParser.ProgramExecutionContext;
import it.univr.main.antlr.MuJsParser.RandomBoolContext;
import it.univr.main.antlr.MuJsParser.RandomIntContext;
import it.univr.main.antlr.MuJsParser.ReplaceContext;
import it.univr.main.antlr.MuJsParser.ReturnStmtContext;
import it.univr.main.antlr.MuJsParser.SliceContext;
import it.univr.main.antlr.MuJsParser.StartsWithContext;
import it.univr.main.antlr.MuJsParser.StmtContext;
import it.univr.main.antlr.MuJsParser.StringContext;
import it.univr.main.antlr.MuJsParser.SubstringContext;
import it.univr.main.antlr.MuJsParser.SumContext;
import it.univr.main.antlr.MuJsParser.ToLowerCaseContext;
import it.univr.main.antlr.MuJsParser.ToUpperCaseContext;
import it.univr.main.antlr.MuJsParser.TrimContext;
import it.univr.main.antlr.MuJsParser.TrimLeftContext;
import it.univr.main.antlr.MuJsParser.TrimRightContext;
import it.univr.main.antlr.MuJsParser.WhileStmtContext;
import it.univr.state.AbstractState;
import it.univr.state.AbstractStore;
import it.univr.state.CallStringAbstractStore;
import it.univr.state.KeyAbstractState;
import it.univr.state.Variable;
import it.univr.state.functions.ActualParameters;
import it.univr.state.functions.CallString;
import it.univr.state.functions.Function;
import it.univr.state.functions.KCallStrings;

public class RelationalAbstractInterpreter extends MuJsBaseVisitor<AbstractValue> {

	private CallStringAbstractStore currentEnvironment;
	private AbstractDomain domain;
	private AbstractState state;
	private int callStringsSize;

	private KCallStrings callStrings = new KCallStrings(new CallString(0,0));

	public CallStringAbstractStore getCurrentEnvironment() {
		return currentEnvironment.clone();
	}

	private String asserts = "";

	public RelationalAbstractInterpreter(AbstractDomain domain, boolean invariants, int callStringSize) {
		this.currentEnvironment = new CallStringAbstractStore(domain);
		this.state = new AbstractState(domain);
		this.callStringsSize = callStringSize;
		currentEnvironment.put(new KCallStrings(new CallString(0,0)), new AbstractStore(domain));
	}

	public RelationalAbstractInterpreter(AbstractDomain domain, boolean invariants) {
		this.currentEnvironment = new CallStringAbstractStore(domain);
		this.state = new AbstractState(domain);
		this.callStringsSize = 3;
		currentEnvironment.put(new KCallStrings(new CallString(0,0)), new AbstractStore(domain));
	}

	public String getWarnings() {
		return asserts;
	}

	public AbstractStore getStoreAtMainCallString() {
		return currentEnvironment.get(new KCallStrings(new CallString(0,0)));
	}

	public void setCurrentEnvironment(CallStringAbstractStore currentEnvironment) {
		this.currentEnvironment = currentEnvironment;
	}

	public CallStringAbstractStore getCallStringStore() {
		return currentEnvironment;
	}


	public AbstractState getAbstractState() {
		return state;
	}


	public void setAbstractState(AbstractState state) {
		this.state = state;
	}

	public AbstractDomain getAbstractDomain() {
		return domain;
	}

	public void setAbstractDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public HashMap<Variable, Function> getFunctions() {
		return state.getFunctions();
	}

	public void printFunctions() {

		if (!state.getFunctions().keySet().isEmpty()) {
			System.out.println("Declared function:");

			for (Function f : state.getFunctions().values())
				System.out.println(f);
		}
	}

	@Override
	public AbstractValue visitContains(MuJsParser.ContainsContext ctx) {
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.includes(left, right);
	}

	@Override
	public AbstractValue visitAssert(MuJsParser.AssertContext ctx) {
		// Get line
		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		KCallStrings currentCallString = getCurrentCallString();

		if (mustBeFalse(ctx.expression(), currentEnvironment.getStore(currentCallString)))
			asserts += "Assertion at line " + key + " does definitely not holds.\n";
		else if (mustBeTrue(ctx.expression(), currentEnvironment.getStore(currentCallString)))
			asserts += "Assertion at line " + key + " definitely holds.\n";
		else
			asserts += "Assertion at line " + key + " may hold.\n";

		return domain.makeBottom();
	}

	@Override
	public AbstractValue visitStartsWith(MuJsParser.StartsWithContext ctx) {
		return visitSingleExpression(ctx);
	}

	@Override
	public AbstractValue visitReplace(MuJsParser.ReplaceContext ctx) {
		return visitSingleExpression(ctx);
	}

	@Override
	public AbstractValue visitEndsWith(MuJsParser.EndsWithContext ctx) {
		return visitSingleExpression(ctx);
	}

	@Override
	public AbstractValue visitToLowerCase(MuJsParser.ToLowerCaseContext ctx) {
		return visitSingleExpression(ctx);
	}

	@Override
	public AbstractValue visitToUpperCase(MuJsParser.ToUpperCaseContext ctx) {
		return visitSingleExpression(ctx);
	}


	@Override 
	public AbstractValue visitObjectExpression(MuJsParser.ObjectExpressionContext ctx) { 
		return visitChildren(ctx);
	}

	/**
	 * 
	 * MuJS Statements
	 * 
	 */
	@Override 
	public AbstractValue visitBlockStmt(MuJsParser.BlockStmtContext ctx) { 
		return visitChildren(ctx); 
	}

	@Override 
	public AbstractValue visitAssignmentStmt(MuJsParser.AssignmentStmtContext ctx) { 

		// Get variable name
		Variable v = new Variable(ctx.getChild(0).getText());

		// Get line
		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		KCallStrings currentCallString = getCurrentCallString();

		AbstractValue oldAbs = null;
		if (currentEnvironment.get(currentCallString).containsKey(v))
			oldAbs = currentEnvironment.get(currentCallString).get(v);

		state.add(key, currentEnvironment.get(currentCallString).clone(), currentCallString);


		removeOldRelations(v, key, currentCallString);

		ExpressionContext lhs = ctx.expression();
		currentEnvironment.put(currentCallString, state.getCallStringEnvironment(key).getStore(currentCallString));
		state.getCallStringEnvironment(key).putVariable(v, visit(lhs), currentCallString);

		// e.g., x = x + "a";
		AbstractValue value = state.getCallStringEnvironment(key).get(currentCallString).get(v).clone();
		Subs sub;

		if (value instanceof Subs) {
			sub = (Subs) value;


			boolean addOldRelations = false;
			if (oldAbs != null && oldAbs instanceof Subs) {
				for (ExpressionContext exp : sub.getRelations()) 
					for (ExpressionContext oldRel : ((Subs) oldAbs).getRelations()) 
						if (replaceVariableInExpression(exp, v, oldRel))
							addOldRelations = true;


				if (addOldRelations) {
					((Subs) state.getCallStringEnvironment(key).get(currentCallString).get(v)).addRelations((Subs) oldAbs);
				}
				//			((Subs) state.getCallStringEnvironment(key).get(currentCallString).get(v)).addRelations(sub);
			}


			if (lhs instanceof PrimitiveValueContext && isPrimiveString((PrimitiveValueContext) lhs)) {

				// check if lhs is substring of other variable relations
				AbstractStore store = currentEnvironment.getStore(currentCallString);
				HashMap<Variable,Subs> toAdd = new HashMap<Variable,Subs>();

				for (Variable var :  store.keySet()) {
					if (!var.equals(v) && store.getValue(var) instanceof Subs) {
						for (ExpressionContext exp : ((Subs) store.getValue(var)).getRelations())
							if (isSubstringOf(lhs, exp)) {
								if (toAdd.get(var) == null)
									toAdd.put(var, new Subs());

								toAdd.get(var).addRelationExcept(parseExpression(v.toString()), var);
							}

					} 

				}

				for (Variable var : toAdd.keySet())
					((Subs) currentEnvironment.getStore(currentCallString).getValue(var)).addRelationsExcept(toAdd.get(var), var);

			} 


			transitiveClosure(state.getCallStringEnvironment(key).getStore(currentCallString));
			//			transitiveClosure(currentEnvironment.getStore(currentCallString));

		}

		return new Bottom(); 
	}

	private boolean isSubstringOf(ExpressionContext first, ExpressionContext second) {
		if (isPrimiveString(first) && isPrimiveString(second)) {
			String lhs = first.getText().substring(1).substring(0, first.getText().substring(1).length() -1);
			String other = second.getText().substring(1).substring(0, second.getText().substring(1).length() -1);
			return other.contains(lhs);
		}
		return false;
	}

	private boolean isPrimiveString(ExpressionContext e) {
		return e instanceof PrimitiveValueContext && e.getText().startsWith("\"") && e.getText().endsWith("\"");
	}

	private void removeOldRelations(Variable vasg, KeyAbstractState key, KCallStrings cs) {
		AbstractStore store = state.getCallStringEnvironment(key).getStore(cs);
		store.remove(vasg);

		for (Variable var : store.keySet())
			if (store.getValue(var) instanceof Subs) {
				Subs toRemove = new Subs();

				for (ExpressionContext rel : ((Subs) store.getValue(var)).getRelations())
					for (ExpressionContext n : getFinalNodes(rel))
						if (n.getText().equals(vasg.toString()))
							toRemove.addRelation(rel);

				((Subs) store.getValue(var)).removeRelations(toRemove);							
			}
	}

	private void transitiveClosure(AbstractStore store) {

		AbstractStore newRelations = new AbstractStore(domain);

		for (Variable v1 : store.keySet()) {

			for (Variable v2 : store.keySet()) {
				if (!v1.equals(v2) && store.get(v1) instanceof Subs && store.get(v2) instanceof Subs) {
					Subs s1 = (Subs) store.get(v1);
					Subs s2 = (Subs) store.get(v2);

					if (!s1.getRelations().isEmpty() && !s2.getRelations().isEmpty()) {
						if (s2.containsAll(s1) && s1.containsAll(s2)) {

							if (newRelations.containsKey(v1)) {
								((Subs) newRelations.get(v1)).addRelationsExcept(new Subs(parseExpression(v2.toString())), v1);
							} else {						
								if (!v2.toString().equals(v1.toString()))
									newRelations.put(v1, new Subs(parseExpression(v2.toString())));
							}

							if (newRelations.containsKey(v2)) {
								((Subs) newRelations.get(v2)).addRelationsExcept(new Subs(parseExpression(v1.toString())), v2);
							} else {			
								if (!v2.toString().equals(v1.toString()))
									newRelations.put(v2, new Subs(parseExpression(v1.toString())));
							}

						} else if (s2.containsAll(s1)) {

							if (newRelations.containsKey(v1)) {
								((Subs) newRelations.get(v1)).addRelationsExcept(new Subs(parseExpression(v2.toString())), v1);
							} else {		
								if (!v2.toString().equals(v1.toString()))
									newRelations.put(v1, new Subs(parseExpression(v2.toString())));
							}

						} else if (s1.containsAll(s2)) {

							if (newRelations.containsKey(v2)) {
								((Subs) newRelations.get(v2)).addRelationsExcept(new Subs(parseExpression(v1.toString())), v2);
							} else {		
								if (!v2.toString().equals(v1.toString()))
									newRelations.put(v2, new Subs(parseExpression(v1.toString())));
							}
						}
					}	
				}
			}
		}

		for (Variable v : newRelations.keySet()) 
			((Subs) store.getValue(v)).addRelationsExcept((Subs) newRelations.getValue(v), v);



		newRelations = new AbstractStore(domain);


		// Transitive closure
		for (Variable v1 : store.keySet()) {
			for (Variable v2 : store.keySet()) {
				for (Variable v3 : store.keySet()) {
					if (!v1.equals(v2) && !v2.equals(v3) 
							&& store.get(v1) instanceof Subs
							&& store.get(v2) instanceof Subs
							&& store.get(v3) instanceof Subs) {

						for (ExpressionContext exp : ((Subs) store.getValue(v1)).getRelations()) {
							Subs s2 = (Subs) store.get(v2);
							Subs s3 = (Subs) store.get(v3);

							if (s2.contains(exp) && s3.contains(parseExpression(v2.toString())))

								if (newRelations.containsKey(v3)) {
									((Subs) newRelations.get(v3)).addRelationsExcept(new Subs(exp), v3);
								} else {			
									if (!exp.getText().equals(v3.toString()))
										newRelations.put(v3, new Subs(exp));
								}
						}
					}
				}
			}
		}


		for (Variable v : newRelations.keySet())
			((Subs) store.getValue(v)).addRelationsExcept((Subs) newRelations.getValue(v), v);


		newRelations = new AbstractStore(domain);


		for (Variable v1 : store.keySet()) {
			for (Variable v2 : store.keySet()) {
				if (!v1.equals(v2) && store.get(v1) instanceof Subs && store.get(v2) instanceof Subs) {
					Subs s1 = ((Subs) store.get(v1)).clone();
					Subs s2 = ((Subs) store.get(v2)).clone();

					if (s2.contains(parseExpression(v1.toString()))) {
						if (newRelations.containsKey(v2)) {
							((Subs) newRelations.get(v2)).addRelationsExcept(s1, v2);
						} else {
							newRelations.put(v2, s1);
						}
					}
				}
			}
		}


		for (Variable v : newRelations.keySet()) 
			((Subs) store.getValue(v)).addRelationsExcept((Subs) newRelations.getValue(v), v);


		// Normalize
		for (Variable v : store.keySet())
			if (store.getValue(v) instanceof Subs) {
				((Subs) store.getValue(v)).removeRelation(parseExpression(v.toString()));
			}
	}

	@Override 
	public AbstractValue visitBlock(MuJsParser.BlockContext ctx) { 
		if (ctx.stmt() != null)
			visit(ctx.stmt());

		return new Bottom();
	}

	@Override 
	public AbstractValue visitComposition(MuJsParser.CompositionContext ctx) { 	
		visit(ctx.stmt(0));		
		return visit(ctx.stmt(1));
	}

	@Override 
	public AbstractValue visitIfStmt(MuJsParser.IfStmtContext ctx) { 
		visitIfStmtWithRelations(ctx, new HashMap<Variable, Subs>());
		return new Bottom();
	}

	public AbstractValue visitIfStmtWithRelations(MuJsParser.IfStmtContext ctx, HashMap<Variable, Subs> rels) { 


		KCallStrings currCallString = getCurrentCallString();

		CallStringAbstractStore previous = (CallStringAbstractStore) currentEnvironment.clone();

		for (Variable v : rels.keySet())
			((Subs) currentEnvironment.get(currCallString).get(v)).addRelationsExcept(rels.get(v), v);

		visit(ctx.block(0));

		CallStringAbstractStore trueBranch = (CallStringAbstractStore) currentEnvironment.clone();
		currentEnvironment = previous;

		visit(ctx.block(1));

		currentEnvironment = currentEnvironment.leastUpperBound(trueBranch);

		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		state.add(key, currentEnvironment.get(getCurrentCallString()).clone(), getCurrentCallString());

		return new Bottom();
	}

	@Override 
	public AbstractValue visitProgramExecution(MuJsParser.ProgramExecutionContext ctx) {
		visit(ctx.stmt()); 
		return domain.makeBottom();
	}

	@Override 
	public AbstractValue visitWhileStmt(MuJsParser.WhileStmtContext ctx) { 

		AbstractValue guard = domain.juggleToBool(visit(ctx.expression()));

		CallStringAbstractStore previous = (CallStringAbstractStore) currentEnvironment.clone();

		do {
			/**
			 * True
			 */
			if (domain.isTrue(guard)) {
				visit(ctx.block());
				currentEnvironment = previous.widening(currentEnvironment);
			} 

			/**
			 * False
			 */
			else if (domain.isFalse(guard)) {
				break;
			} 

			/**
			 * Top
			 */
			else if (domain.isTopBool(guard)) {
				visit(ctx.block());
				currentEnvironment = previous.widening(previous.leastUpperBound(currentEnvironment));
			}

			if (previous.equals(currentEnvironment))
				break;
			else
				previous = currentEnvironment.clone();
		} while (true);

		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		state.add(key, currentEnvironment.get(getCurrentCallString()).clone(), getCurrentCallString());

		return new Bottom(); 
	}


	/**
	 * 
	 * MuJS Expression
	 * 
	 */
	@Override 
	public AbstractValue visitGreater(MuJsParser.GreaterContext ctx) { 
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.greater(left, right);
	}

	@Override 
	public AbstractValue visitSlice(MuJsParser.SliceContext ctx) { 
		AbstractValue str = visit(ctx.expression(0));
		AbstractValue i = visit(ctx.expression(1));
		AbstractValue j = visit(ctx.expression(2));

		return domain.slice(str, i, j);
	}

	@Override 
	public AbstractValue visitTrim(MuJsParser.TrimContext ctx) { 
		AbstractValue par = visit(ctx.expression());

		return domain.trim(par);
	}

	@Override 
	public AbstractValue visitTrimLeft(MuJsParser.TrimLeftContext ctx) { 
		AbstractValue par = visit(ctx.expression());

		return domain.trimLeft(par);
	}

	@Override 
	public AbstractValue visitTrimRight(MuJsParser.TrimRightContext ctx) { 
		AbstractValue par = visit(ctx.expression());

		return domain.trimRight(par);
	}

	@Override 
	public AbstractValue visitEquals(MuJsParser.EqualsContext ctx) { 
		ExpressionContext left = ctx.expression(0);
		ExpressionContext right = ctx.expression(1);

		if (left instanceof IdentifierContext) {

		}

		else if (right instanceof IdentifierContext) {

		} 

		return domain.makeBottom();
	}


	public HashMap<Variable, Subs> visitBooleanGuard(MuJsParser.ExpressionContext ctx) { 

		HashMap<Variable, Subs> newRelations = new HashMap<Variable, Subs>();

		// e1 == e2
		if (ctx instanceof EqualsContext) {

			ExpressionContext left = ((EqualsContext) ctx).expression(0);
			ExpressionContext right = ((EqualsContext) ctx).expression(1);

			if (left instanceof IdentifierContext) {

				Subs news = new Subs();
				for (ExpressionContext exp : getFinalNodes(right)) 
					news.addRelation(exp);

				newRelations.put(new Variable(left.getText()), news);

			}

			if (right instanceof IdentifierContext) {

				Subs news = new Subs();
				for (ExpressionContext exp : getFinalNodes(left)) 
					news.addRelation(exp);

				newRelations.put(new Variable(right.getText()), news);
			} 

			return newRelations;
		}

		// e1.startsWith(e2) 
		else if (ctx instanceof StartsWithContext) {

			ExpressionContext left = ((StartsWithContext) ctx).expression(0);
			ExpressionContext right = ((StartsWithContext) ctx).expression(1);

			// (e1 = x, e2= const str)
			if (left instanceof IdentifierContext && isPrimiveString(right)) {
				Subs subs = new Subs();

				for (String sub : getAllSubstrings(right.getText().substring(1, right.getText().length()-1)))
					subs.addRelation(parseExpression("\"" + sub + "\""));

				newRelations.put(new Variable(left.getText()), subs);
			} 

			// (e1 = x, e2= y)
			if (left instanceof IdentifierContext && right instanceof IdentifierContext) {
				Subs subs = new Subs(right);
				newRelations.put(new Variable(left.getText()), subs);
			} 

			return newRelations;

		}

		// e1.endssWith(e2) 
		else if (ctx instanceof EndsWithContext) {

			ExpressionContext left = ((EndsWithContext) ctx).expression(0);
			ExpressionContext right = ((EndsWithContext) ctx).expression(1);

			// (e1 = x, e2= const str)
			if (left instanceof IdentifierContext && isPrimiveString(right)) {
				Subs subs = new Subs();

				for (String sub : getAllSubstrings(right.getText().substring(1, right.getText().length()-1)))
					subs.addRelation(parseExpression("\"" + sub + "\""));

				newRelations.put(new Variable(left.getText()), subs);
			} 

			// (e1 = x, e2= y)
			if (left instanceof IdentifierContext && right instanceof IdentifierContext) {
				Subs subs = new Subs(right);
				newRelations.put(new Variable(left.getText()), subs);
			} 

			return newRelations;

		}

		// (e)
		else if (ctx instanceof ParenthesisContext) {
			return visitBooleanGuard(((ParenthesisContext) ctx).expression());
		}

		// e1 && e2
		else if (ctx instanceof AndContext) {
			HashMap<Variable, Subs> first = visitBooleanGuard(((AndContext) ctx).expression(0));
			HashMap<Variable, Subs> second = visitBooleanGuard(((AndContext) ctx).expression(1));

			HashMap<Variable, Subs> result = new HashMap<Variable, Subs>();

			for (Variable key : first.keySet())
				if (second.containsKey(key))
					result.put(key, (Subs) first.get(key).leastUpperBound(second.get(key)));

			return result;
		}

		return newRelations;
	}

	@Override 
	public AbstractValue visitLess(MuJsParser.LessContext ctx) { 
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.less(left, right);
	}

	@Override 
	public AbstractValue visitSubstring(MuJsParser.SubstringContext ctx) { 
		return visitSingleExpression(ctx);
	}

	@Override 
	public AbstractValue visitDiff(MuJsParser.DiffContext ctx) { 
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.diff(left, right);
	}

	@Override 
	public AbstractValue visitMul(MuJsParser.MulContext ctx) {
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.mul(left, right);
	}

	@Override 
	public AbstractValue visitIndexOf(MuJsParser.IndexOfContext ctx) {
		AbstractValue string = visit(ctx.expression(0));
		AbstractValue search = visit(ctx.expression(1));

		return domain.indexOf(string, search);
	}

	@Override 
	public AbstractValue visitSum(MuJsParser.SumContext ctx) { 
		Subs subs = new Subs();
		subs.addRelation(ctx);

		ArrayList<ExpressionContext> nodes = getFinalNodes(ctx);

		for (int i = 0; i < nodes.size(); i++) {
			String toParse = nodes.get(i).getText();
			if (nodes.get(i) instanceof PrimitiveValueContext && isPrimiveString(nodes.get(i))) {
				String s = nodes.get(i).getText().substring(1, nodes.get(i).getText().length() -1);

				for (String sub : getAllSubstrings(s))
					subs.addRelation(parseExpression("\"" + sub +"\""));
			} else {
				subs.addRelation((ExpressionContext) nodes.get(i));		
			}
			for (int j = i + 1; j < nodes.size(); j++) {

				if (nodes.get(j) instanceof PrimitiveValueContext && isPrimiveString(nodes.get(j))) {
					String s = nodes.get(j).getText().substring(1, nodes.get(j).getText().length() -1);

					for (String sub : getAllSubstrings(s))
						subs.addRelation(parseExpression("\"" + sub +"\""));
				} else {
					subs.addRelation((ExpressionContext) nodes.get(j));
				}

				toParse += "+" + nodes.get(j).getText();
				ExpressionContext n = parseExpression(toParse);
				subs.addRelation(n);
			}
		}

		return subs;
	}

	private ArrayList<ExpressionContext> getFinalNodes(ExpressionContext exp) {
		ArrayList<ExpressionContext> result = new ArrayList<ExpressionContext>();
		if (exp instanceof ExpressionContext && !(exp instanceof SumContext))
			result.add(exp);
		else if (exp instanceof SumContext) {
			result.addAll(getFinalNodes(((SumContext) exp).expression(0)));
			result.addAll(getFinalNodes(((SumContext) exp).expression(1)));
		}
		return result;
	}

	@Override 
	public AbstractValue visitPrimitiveValue(MuJsParser.PrimitiveValueContext ctx) { 
		return visitChildren(ctx); 
	}

	@Override 
	public AbstractValue visitString(MuJsParser.StringContext ctx) { 
		Subs subs = new Subs();
		String s = ctx.getText().substring(1, ctx.getText().length() -1);

		for (String sub : getAllSubstrings(s))
			subs.addRelation(parseExpression("\"" +  sub +"\""));
		return subs;
	}

	private Set<String> getAllSubstrings(String str) {
		HashSet<String> res = new HashSet<String>();

		for (int i = 0; i < str.length(); i++) 
			for (int j = i+1; j <= str.length(); j++) 
				res.add(str.substring(i,j));

		return res;
	}

	@Override 
	public AbstractValue visitDiv(MuJsParser.DivContext ctx) { 
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.div(left, right);
	}

	@Override 
	public AbstractValue visitNot(MuJsParser.NotContext ctx) { 
		AbstractValue v = visit(ctx.expression());
		return domain.not(v);		
	}

	@Override 
	public AbstractValue visitRandomInt(MuJsParser.RandomIntContext ctx) { 
		return new Top();
	}

	@Override 
	public AbstractValue visitRandomStr(MuJsParser.RandomStrContext ctx) { 
		return new Subs();	
	}

	private AbstractValue visitSingleExpression(MuJsParser.ExpressionContext exp) {
		return new Subs(exp);
	}



	@Override 
	public AbstractValue visitCharAt( MuJsParser.CharAtContext ctx) { 
		return visitSingleExpression(ctx);
	}

	@Override public AbstractValue visitLength(MuJsParser.LengthContext ctx) { 
		AbstractValue string = visit(ctx.expression());

		return domain.length(string);
	}

	@Override 
	public AbstractValue visitAnd(MuJsParser.AndContext ctx) { 
		AbstractValue first = visit(ctx.expression(0));
		AbstractValue second = visit(ctx.expression(1));

		return domain.and(first, second);
	}

	@Override 
	public AbstractValue visitOr(MuJsParser.OrContext ctx) { 
		AbstractValue first = visit(ctx.expression(0));
		AbstractValue second = visit(ctx.expression(1));

		return domain.or(first, second);
	}

	@Override 
	public AbstractValue visitIdentifier(MuJsParser.IdentifierContext ctx) { 

		Variable v = new Variable(ctx.ID().getText());
		KCallStrings currentCallString = getCurrentCallString();

		if (currentEnvironment.getStore(currentCallString).containsKey(v)) {
			Subs subs = new Subs();
			subs.addRelation(ctx);
			return subs;
		} else {
			System.out.println(v + " " + currentCallString + " " + currentEnvironment.getStore(currentCallString));
			return new Bottom();
		}
	}

	@Override 
	public AbstractValue visitParenthesis(MuJsParser.ParenthesisContext ctx) {
		return visit(ctx.expression()); 
	}


	@Override
	public AbstractValue visitReturnStmt(MuJsParser.ReturnStmtContext ctx) {

		FunctionDeclarationContext call = null;

		try {
			call = getFunctionDeclarationContext(ctx);
		} catch (Exception e) {
			System.err.println("Return statement used outside function declaration!");
		}

		Function f = state.getFunction(new Variable(call.ID(0).getText()));
		f.addReturnValueAtCallString(getCurrentCallString(), visit(ctx.expression()));

		//
		//		// Get line
		//		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		//		KCallStrings currentCallString = getCurrentCallString();
		//		state.add(key, currentEnvironment.get(currentCallString).clone(), currentCallString);

		return f.getReturnValueAtCallString(getCurrentCallString()); 
	}

	@Override 
	public AbstractValue visitFunctionDeclaration(MuJsParser.FunctionDeclarationContext ctx) { 

		Variable name = new Variable(ctx.ID(0).getText());
		StmtContext body = (StmtContext) ctx.stmt();
		Vector<Variable> formalParameters = new Vector<Variable>();

		for (int i = 1; i < ctx.ID().size(); i++)
			formalParameters.add(new Variable(ctx.ID(i).getText()));

		Function function = new Function(name, formalParameters, body, domain);

		state.addFunction(name, function);
		return new Bottom();
	}

	@Override 
	public AbstractValue visitFunctionCall(MuJsParser.FunctionCallContext ctx) { 
		KCallStrings currentCallString = getCurrentCallString().clone();

		Function f = state.getFunction(new Variable(ctx.ID().getText()));

		KeyAbstractState key = new KeyAbstractState(f.getBody().getStart().getLine(), f.getBody().getStart().getCharPositionInLine());

		ActualParameters actualParameters = new ActualParameters();

		for (int i = 0; i < f.getFormalParameters().size(); i++) {
			AbstractValue actualPar = visit(ctx.expression(i));
			actualParameters.add(actualPar);
		}

		CallString call = new CallString(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		KCallStrings newCallString = getCurrentCallString().clone();
		newCallString.add(call);

		if (newCallString.size() > callStringsSize)
			newCallString.remove(0);

		if (!state.contains(key)) 
			state.add(key, currentEnvironment.get(currentCallString).clone(), newCallString);
		else if (state.getCallStringEnvironment(key).containsKey(newCallString)) {
			for (int i = 0; i < f.getFormalParameters().size(); i++) {
				AbstractValue oldActualParameter = state.getCallStringEnvironment(key).get(newCallString).getValue(f.getFormalParameters().get(i));
				actualParameters.set(i, oldActualParameter.widening(actualParameters.get(i)));				
			}
		} else 
			state.getCallStringEnvironment(key).put(newCallString, currentEnvironment.get(currentCallString).clone());


		if (f.hasCallString(newCallString) && actualParameters.equals(f.getActualParametersAtCallString(newCallString))) {
			return f.getReturnValueAtCallString(newCallString);
		} else {

			for (int i = 0; i < f.getFormalParameters().size(); i++) 
				state.getCallStringEnvironment(key).putVariable(f.getFormalParameters().get(i), actualParameters.get(i), newCallString);

			f.addReturnValueAtCallString(newCallString, actualParameters, new Bottom());
		}

		currentEnvironment = state.getCallStringEnvironment(key).clone();


		callStrings = newCallString;
		visit(f.getBody());
		callStrings = currentCallString;

		AbstractValue returnValue = f.getReturnValueAtCallString(newCallString);
		if (returnValue instanceof Subs)
			returnValue = resolveRelation((Subs) returnValue, f.getFormalParameters(), actualParameters);

		for (int i = 0; i < f.getFormalParameters().size(); i++) 
			currentEnvironment.removeVariable(f.getFormalParameters().get(i), newCallString);


		return returnValue; 
	}

	public KCallStrings getCurrentCallString() {
		return callStrings.clone();
	}

	private FunctionDeclarationContext getFunctionDeclarationContext(ReturnStmtContext ret) throws Exception {
		ParserRuleContext result = ret;

		while (true) {
			result = result.getParent();

			if (result instanceof ProgramContext)
				throw new Exception();
			else if (result instanceof FunctionDeclarationContext) 
				return (FunctionDeclarationContext) result;
		}
	}

	public ExpressionContext parseExpression(String exp) {
		InputStream stream = new ByteArrayInputStream(exp.getBytes());
		MuJsLexer lexer = null;

		try {
			lexer = new MuJsLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		} catch (IOException e) {
			System.err.println(exp + " not parsable!");
		}

		MuJsParser parser = new MuJsParser(new CommonTokenStream(lexer));
		ExpressionContext tree = parser.expression();
		return tree;
	}

	public Subs resolveRelation(Subs sub, List<Variable> formPars, List<AbstractValue> actualPars) {

		Subs newSubs = new Subs();
		for (ExpressionContext exp : sub.getRelations())
			for (ExpressionContext e : replaceVariableInExpression(exp, formPars, actualPars))
				newSubs.addRelation(e);

		return newSubs;
	}

	public boolean mustBeTrue(MuJsParser.ExpressionContext b, AbstractStore store) {

		// true
		if (b.getText().equals("true"))
			return true;

		// e1 == e2 (we check only x == y)
		else if (b instanceof EqualsContext) {
			EqualsContext eq = (EqualsContext) b;

			if (eq.expression(0) instanceof IdentifierContext && eq.expression(1) instanceof IdentifierContext) {
				AbstractValue v1 =  store.getValue(new Variable(eq.expression(0).getText()));
				AbstractValue v2 =  store.getValue(new Variable(eq.expression(1).getText()));


				if (v1 instanceof Subs && v2 instanceof Subs) {
					if (((Subs) v1).isEmpty() || ((Subs) v2).isEmpty())
						return false;

					return ((Subs) v1).contains(eq.expression(1)) && 
							((Subs) v2).contains(eq.expression(0));
				}

			}
		}

		// e1.includes(e2) (we check only x.contains(e))
		else if (b instanceof ContainsContext) {
			ContainsContext eq = (ContainsContext) b;

			if (eq.expression(0) instanceof IdentifierContext) {
				AbstractValue v1 =  store.getValue(new Variable(eq.expression(0).getText()));
				AbstractValue v2 =  visit(eq.expression(1));



				if (v1 instanceof Subs && v2 instanceof Subs) {

					if (((Subs) v1).isEmpty() || ((Subs) v2).isEmpty())
						return false;

					for (ExpressionContext exp : ((Subs) v2).getRelations())
						if (!((Subs) v1).contains(exp)) {							
							return false;
						}
					return true;
				} else if (v1 instanceof Subs && eq.expression(1) instanceof IdentifierContext) {
					if (((Subs) v1).contains(eq.expression(1)))
						return true;
				}
			}
		}

		// !(e)
		else if (b instanceof NotContext) {
			return mustBeFalse(((NotContext) b).expression(), store);
		}

		// (e)
		else if (b instanceof ParenthesisContext) {
			return mustBeTrue(((ParenthesisContext) b).expression(), store);
		}

		// (e1 != e2)
		else if (b instanceof NotEqualsContext) {
			return mustBeTrue(desugarNotEquals((NotEqualsContext) b), store);
		}

		// (e1 && e2)
		else if (b instanceof AndContext) {
			return mustBeTrue(((AndContext) b).expression(0), store) && mustBeFalse(((AndContext) b).expression(1), store);
		}

		// randomBool
		else if (b instanceof RandomBoolContext) {
			return false;
		}

		// Don't know (i.e., {true, false})
		return false;
	} 

	public boolean mustBeFalse(MuJsParser.ExpressionContext b, AbstractStore store) {

		// false
		if (b.getText().equals("false"))
			return true;

		// e1 == e2 (we check only x == y)
		else if (b instanceof EqualsContext) {
			EqualsContext eq = (EqualsContext) b;

			if (eq.expression(0) instanceof IdentifierContext && eq.expression(1) instanceof IdentifierContext) {
				AbstractValue v1 =  store.getValue(new Variable(eq.expression(0).getText()));
				AbstractValue v2 =  store.getValue(new Variable(eq.expression(1).getText()));




				if (v1 instanceof Subs && v2 instanceof Subs) {
					if (((Subs) v1).isEmpty() || ((Subs) v2).isEmpty())
						return false;
					return ((Subs) v1.leastUpperBound(v2)).isEmpty();
				}

			}
		}

		// e1.includes(e2) (we check only x.contains(e))
		else if (b instanceof ContainsContext) {
			ContainsContext eq = (ContainsContext) b;

			if (eq.expression(0) instanceof IdentifierContext) {
				AbstractValue v1 =  store.getValue(new Variable(eq.expression(0).getText()));
				AbstractValue v2 =  visit(eq.expression(1));

				if (v1 instanceof Subs && v2 instanceof Subs) {
					//
					//					if (((Subs) v1).isEmpty() || ((Subs) v2).isEmpty())
					//						return false;
					//
					//					for (ExpressionContext exp : ((Subs) v2).getRelations())
					//						if (((Subs) v1).contains(exp))
					//							return false;
					return false;
				}
			}
		}

		// !(e)
		else if (b instanceof NotContext) {
			return mustBeTrue(((NotContext) b).expression(), store);
		}

		// (e)
		else if (b instanceof ParenthesisContext) {
			return mustBeFalse(((ParenthesisContext) b).expression(), store);
		}

		// (e1 != e2)
		else if (b instanceof NotEqualsContext) {
			return mustBeFalse(desugarNotEquals((NotEqualsContext) b), store);
		}

		// (e1 && e2)
		else if (b instanceof AndContext) {
			return mustBeFalse(((AndContext) b).expression(0), store) || mustBeFalse(((AndContext) b).expression(1), store);
		}

		// randomBool
		else if (b instanceof RandomBoolContext) {
			return false;
		}

		// Don't know (i.e., {true, false})
		return false;
	} 

	private ExpressionContext desugarNotEquals(NotEqualsContext b) {
		return parseExpression("!(" + ((NotEqualsContext) b).expression(0).getText() + "==" + ((NotEqualsContext) b).expression(1).getText() + ")");
	}

	private Set<ExpressionContext> replaceVariableInExpression(ExpressionContext exp, List<Variable> formPars, List<AbstractValue> actPars) {
		Set<ExpressionContext> result = new HashSet<ExpressionContext>();

		for (int i = 0; i < exp.children.size(); i++) {
			if ((exp.children.get(i) instanceof IdentifierContext || exp.children.get(i) instanceof TerminalNodeImpl)
					&& formPars.contains(new Variable(exp.children.get(i).getText()))) {

				AbstractValue toReplace = actPars.get(formPars.indexOf(new Variable(exp.children.get(i).getText())));

				if (toReplace instanceof Subs) {
					for (ExpressionContext expR : ((Subs) toReplace).getRelations()) {
						ParseTree t = exp.children.set(i, parseExpression(expR.getText().toString()));
						result.add(parseExpression(exp.getText()));
						exp.children.set(i, t);			
					}
				}
			} 
		}

		return result;
	}

	private boolean replaceVariableInExpression(ExpressionContext exp, Variable var, ExpressionContext toReplace) {

		for (int i = 0; i < exp.children.size(); i++) {
			if ((exp.children.get(i) instanceof IdentifierContext || exp.children.get(i) instanceof TerminalNodeImpl)
					&& exp.children.get(i).getText().equals(var.toString())) {
				return true;
				//exp.children.set(i, parseExpression(toReplace.getText().toString()));	
			} 
		}

		return false;

		//		int size = exp.children.size() -2;
		//
		//		for (int i = 0; i < size; i++) {
		//
		//			if ((exp.children.get(i) instanceof PrimitiveValueContext || exp.children.get(i) instanceof TerminalNodeImpl)
		//					&& (exp.children.get(i + 1).getText().equals("+"))
		//					&&  (exp.children.get(i + 2) instanceof PrimitiveValueContext || exp.children.get(i +2) instanceof TerminalNodeImpl)) {
		//
		//				String s1 = exp.children.get(i).getText().substring(1, exp.children.get(i).getText().length()-1);
		//				String s2 = exp.children.get(i+2).getText().substring(1, exp.children.get(i+2).getText().length()-1);
		//
		//				System.out.println(s1 + " " + s2);
		//				exp.children.add(i, parseExpression("\"" +  s1 + s2 + "\""));	
		//
		//			} 
		//		}

	}

}
