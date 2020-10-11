package it.univr.main.partitioning;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import it.univr.main.antlr.MuJsParser.ObjectExpressionContext;
import it.univr.main.antlr.MuJsParser.OrContext;
import it.univr.main.antlr.MuJsParser.ParenthesisContext;
import it.univr.main.antlr.MuJsParser.PrimitiveValueContext;
import it.univr.main.antlr.MuJsParser.ProgramContext;
import it.univr.main.antlr.MuJsParser.ProgramExecutionContext;
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
import it.univr.main.relational.RelationalAbstractInterpreter;
import it.univr.state.AbstractState;
import it.univr.state.AbstractStore;
import it.univr.state.CallStringAbstractStore;
import it.univr.state.KeyAbstractState;
import it.univr.state.PartitioningAbstractState;
import it.univr.state.PartitioningCallStringAbstractStore;
import it.univr.state.Variable;
import it.univr.state.functions.CallString;
import it.univr.state.functions.Function;
import it.univr.state.functions.KCallStrings;

public class TracePartitioningAbstractInterpreter extends MuJsBaseVisitor<AbstractValue> {

	private PartitioningAbstractState state;

	private PartitioningCallStringAbstractStore currentEnvironment;
	private AbstractDomain domain;
	private RelationalAbstractInterpreter interpreter = new RelationalAbstractInterpreter(domain, true);
	private HashMap<Integer, KCallStrings> callStrings = new HashMap<Integer, KCallStrings>();

	private Integer tokenGen = 0;

	private String asserts = "";
	
	private HashMap<Integer, Set<Integer>> tokenRelations = new HashMap<Integer, Set<Integer>>();

	public TracePartitioningAbstractInterpreter(AbstractDomain domain, boolean invariants, int callStringSize) {
		this.currentEnvironment = new PartitioningCallStringAbstractStore(domain);
		this.state = new PartitioningAbstractState();

		currentEnvironment = new PartitioningCallStringAbstractStore(domain);
		CallStringAbstractStore env = new CallStringAbstractStore(domain);
		env.put(new KCallStrings(CallString.mainCallString()), new AbstractStore(domain));
		currentEnvironment.put(tokenGen, env);
	}
	
	public String getWarnings() {
		return asserts;
	}

	public TracePartitioningAbstractInterpreter(AbstractDomain domain, boolean invariants) {
		this.currentEnvironment = new PartitioningCallStringAbstractStore(domain);
		this.state = new PartitioningAbstractState();

		currentEnvironment = new PartitioningCallStringAbstractStore(domain);
		CallStringAbstractStore env = new CallStringAbstractStore(domain);
		env.put(new KCallStrings(CallString.mainCallString()), new AbstractStore(domain));
		currentEnvironment.put(tokenGen, env);
	}

	public Map<Integer, AbstractStore> getStoresAtMainCallString() {
		HashMap<Integer, AbstractStore>  stores = new HashMap<Integer, AbstractStore>();

		for (Integer token : currentEnvironment.keySet()) 
			stores.put(token, currentEnvironment.get(token).get(new KCallStrings(CallString.mainCallString())));

		return stores;
	}


	public PartitioningAbstractState getAbstractState() {
		return state;
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
	public AbstractValue visitStartsWith(MuJsParser.StartsWithContext ctx) {
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.startsWith(left, right);
	}

	@Override
	public AbstractValue visitReplace(MuJsParser.ReplaceContext ctx) {
		AbstractValue a = visit(ctx.expression(0));
		AbstractValue b = visit(ctx.expression(1));
		AbstractValue c = visit(ctx.expression(2));

		return domain.replace(a, b, c);
	}

	@Override
	public AbstractValue visitEndsWith(MuJsParser.EndsWithContext ctx) {
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.endsWith(left, right);
	}

	@Override
	public AbstractValue visitToLowerCase(MuJsParser.ToLowerCaseContext ctx) {
		AbstractValue par = visit(ctx.expression());

		return domain.toLowerCase(par);
	}

	@Override
	public AbstractValue visitToUpperCase(MuJsParser.ToUpperCaseContext ctx) {
		AbstractValue par = visit(ctx.expression());

		return domain.toUpperCase(par);
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

		// Get line
		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		state.add(key, currentEnvironment.clone());

		AbstractState oldState = interpreter.getAbstractState().clone();

		for (Integer token : state.getCallStringEnvironment(key).keySet()) {
			interpreter.setCurrentEnvironment(state.getCallStringEnvironment(key).get(token).clone());
			interpreter.visitAssignmentStmt(ctx);			
			state.setCallStringEnvironmentAt(key, token, interpreter.getCurrentEnvironment().clone());		
			callStrings.put(token, interpreter.getCurrentCallString());
			interpreter.setAbstractState(oldState.clone());
		}

		currentEnvironment = state.getCallStringEnvironment(key);
		state.getCallStringEnvironment(key).impera();

		return new Bottom(); 
	}

	@Override
	public AbstractValue visitAssert(MuJsParser.AssertContext ctx) {

		// Get line
		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());

		AbstractStore store = null;

		for (Integer i : currentEnvironment.keySet()) {
			KCallStrings cs = callStrings.get(i);

			store = store == null ? currentEnvironment.get(i).getStore(cs) : store.leastUpperBound(currentEnvironment.get(i).getStore(cs));

		}

		
		if (interpreter.mustBeFalse(ctx.expression(), store))
			asserts += "Assertion at line " + key + " does definitely not holds.\n";
		else if (interpreter.mustBeTrue(ctx.expression(), store))
			asserts += "Assertion at line " + key + " definitely holds.\n";
		else
			asserts += "Assertion at line " + key + " may hold.\n";

		return domain.makeBottom();
	}

	// Merge all the trace states
	AbstractStore store = null;


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

		// Get line
		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		state.add(key, currentEnvironment.clone());

		// Boolean guard
		ExpressionContext b = ctx.expression();

		// Relations from boolean guard
		HashMap<Variable, Subs> relationsFromGuard = interpreter.visitBooleanGuard(ctx.expression());

		// Build true and false branches abstract stores
		PartitioningCallStringAbstractStore trueBranch = new PartitioningCallStringAbstractStore(domain);
		PartitioningCallStringAbstractStore falseBranch = new PartitioningCallStringAbstractStore(domain);

		for (Integer token : currentEnvironment.keySet()) {
			boolean mustBeFalse = interpreter.mustBeFalse(b, currentEnvironment.get(token).get(callStrings.get(token)));


			// if the boolean guard is definitely not false, propagate the store
			if (!mustBeFalse)  {
				Integer tokenTrue = ++tokenGen;

				trueBranch.put(tokenTrue, currentEnvironment.get(token).clone());
				callStrings.put(tokenTrue, callStrings.get(token));

				for (Variable v : relationsFromGuard.keySet()) 
					((Subs) trueBranch.get(tokenTrue).get(callStrings.get(token)).get(v)).addRelationsExcept(relationsFromGuard.get(v), v);

				if (tokenRelations.containsKey(tokenTrue))
					tokenRelations.get(tokenTrue).add(token);
				else {
					tokenRelations.put(tokenTrue, new HashSet<Integer>());
					tokenRelations.get(tokenTrue).add(token);
				}
			}

		}

		AbstractState oldState = (AbstractState) interpreter.getAbstractState().clone();

		for (Integer token : currentEnvironment.keySet()) {
			boolean mustBeTrue = interpreter.mustBeTrue(b, currentEnvironment.get(token).get(callStrings.get(token)));

			// if the boolean guard is definitely not true, propagate the store
			if (!mustBeTrue) {
				Integer tokenFalse = ++tokenGen;
				falseBranch.put(tokenFalse, currentEnvironment.get(token).clone());
				callStrings.put(tokenFalse, callStrings.get(token));

				if (tokenRelations.containsKey(tokenFalse))
					tokenRelations.get(tokenFalse).add(token);
				else {
					tokenRelations.put(tokenFalse, new HashSet<Integer>());
					tokenRelations.get(tokenFalse).add(token);
				}

			}
		}

		currentEnvironment = trueBranch;
		visit(ctx.block(0));

		PartitioningCallStringAbstractStore trueBranchStore = (PartitioningCallStringAbstractStore) currentEnvironment.clone();

		currentEnvironment = falseBranch;
		interpreter.setAbstractState(oldState);

		visit(ctx.block(1));


		currentEnvironment = currentEnvironment.leastUpperBound(trueBranchStore);
		currentEnvironment.impera();

		return new Bottom();
	}

	@Override 
	public AbstractValue visitProgramExecution(MuJsParser.ProgramExecutionContext ctx) {
		visit(ctx.stmt()); 
		return domain.makeBottom();
	}

	@Override 
	public AbstractValue visitWhileStmt(MuJsParser.WhileStmtContext ctx) { 

		int k = 2;
		int count = 0;

		KeyAbstractState key = new KeyAbstractState(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
		state.add(key, currentEnvironment.clone());

		// Relations from boolean guard
		HashMap<Variable, Subs> relationsFromGuard = interpreter.visitBooleanGuard(ctx.expression());

		PartitioningCallStringAbstractStore previous;

		do {
			previous = currentEnvironment.clone();
			AbstractState oldState = (AbstractState) interpreter.getAbstractState().clone();

			// Get the set of the tokens at the entry of the while loop
			Set<Integer> oldTokens = previous.keySet();

			for (Integer token : oldTokens) {

				KCallStrings currentCallStringForToken = callStrings.get(token);
				boolean mustTrue = interpreter.mustBeTrue(ctx.expression(), currentEnvironment.getCallStringStoreAtToken(token).getStore(currentCallStringForToken));
				boolean mustFalse = interpreter.mustBeFalse(ctx.expression(), currentEnvironment.getCallStringStoreAtToken(token).getStore(currentCallStringForToken));


				/**
				 * True - don't need to split
				 */
				if (mustTrue) {
					visit(ctx.block());
				} 

				/**
				 * False
				 */
				else if (mustFalse) {

				} 

				/**
				 * Top - need to split
				 */
				else if (count < k) {

					count++;

					CallStringAbstractStore newTrueState = (CallStringAbstractStore) currentEnvironment.get(token).clone();

					Integer tokenTrue = ++tokenGen;					

					if (tokenRelations.containsKey(tokenTrue))
						tokenRelations.get(tokenTrue).add(token);
					else {
						tokenRelations.put(tokenTrue, new HashSet<Integer>());
						tokenRelations.get(tokenTrue).add(token);
					}

					callStrings.put(tokenTrue, callStrings.get(token));

					for (Variable v : relationsFromGuard.keySet()) 
						((Subs) newTrueState.get(currentCallStringForToken).get(v)).addRelationsExcept(relationsFromGuard.get(v), v);


					PartitioningCallStringAbstractStore before = (PartitioningCallStringAbstractStore) currentEnvironment.clone();
					currentEnvironment.put(tokenTrue, newTrueState);

					currentEnvironment = new PartitioningCallStringAbstractStore(domain);
					currentEnvironment.put(tokenTrue, newTrueState);

					visit(ctx.block());	


					currentEnvironment.putAll(before);

				} else {
					visit(ctx.block());	
					currentEnvironment = previous.leastUpperBound(currentEnvironment);
				}			

				interpreter.setAbstractState(oldState);
			}

			currentEnvironment.impera();
			if (previous.equals(currentEnvironment) || isPartitioningFixPointReached(previous))
				break;
			else {
				previous = currentEnvironment.clone();
			}

		} while (true);


		currentEnvironment.impera();
		state.add(key, currentEnvironment.clone());
		return new Bottom(); 
	}


	private boolean isPartitioningFixPointReached(PartitioningCallStringAbstractStore previous) {

		for (Integer newToken : currentEnvironment.keySet()) {
			CallStringAbstractStore newStore = currentEnvironment.get(newToken);

			CallStringAbstractStore beforeWhile = new CallStringAbstractStore(domain);

			if (tokenRelations.containsKey(newToken)) {
				for (Integer oldToken : tokenRelations.get(newToken)) 
					beforeWhile = beforeWhile.leastUpperBound(previous.get(oldToken));

				if (!beforeWhile.equals(beforeWhile.leastUpperBound(newStore)))
					return false;
			} else {
				if (!previous.get(newToken).equals(currentEnvironment.get(newToken)))
					return false;
			}
		}

		return true;
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


	public AbstractStore visitBooleanGuard(MuJsParser.ExpressionContext ctx) { 


		if (ctx instanceof EqualsContext) {

			ExpressionContext left = ((EqualsContext) ctx).expression(0);
			ExpressionContext right = ((EqualsContext) ctx).expression(1);

			AbstractStore newRelations = new AbstractStore(domain);

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

		return null;
	}

	@Override 
	public AbstractValue visitLess(MuJsParser.LessContext ctx) { 
		AbstractValue left = visit(ctx.expression(0));
		AbstractValue right = visit(ctx.expression(1));

		return domain.less(left, right);
	}

	@Override 
	public AbstractValue visitSubstring(MuJsParser.SubstringContext ctx) { 
		AbstractValue string = visit(ctx.expression(0));
		AbstractValue init = visit(ctx.expression(1));
		AbstractValue end = visit(ctx.expression(2));

		return domain.substring(string, init, end);	
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
			subs.addRelation(nodes.get(i));
			for (int j = i + 1; j < nodes.size(); j++) {
				subs.addRelation((ExpressionContext) nodes.get(j));

				toParse += "+" + nodes.get(j).getText();
				try {
					ExpressionContext n = parseExpression(toParse);
					subs.addRelation(n);
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		subs.addRelation((ExpressionContext) ctx.getParent());
		return subs;
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
		return domain.makeUnknownInteger();		
	}



	@Override 
	public AbstractValue visitCharAt( MuJsParser.CharAtContext ctx) { 
		AbstractValue string = visit(ctx.expression(0));
		AbstractValue index = visit(ctx.expression(1));

		return domain.charAt(string, index);
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
		return new Bottom();
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
		return null;
	}

	private KCallStrings getCurrentCallString() {
		// return callStrings.clone();
		return null;
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

	public ExpressionContext parseExpression(String exp) throws IOException {
		InputStream stream = new ByteArrayInputStream(exp.getBytes());
		MuJsLexer lexer = new MuJsLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
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


	private Set<ExpressionContext> replaceVariableInExpression(ExpressionContext exp, List<Variable> formPars, List<AbstractValue> actPars) {
		Set<ExpressionContext> result = new HashSet<ExpressionContext>();

		for (int i = 0; i < exp.children.size(); i++) {
			if ((exp.children.get(i) instanceof IdentifierContext || exp.children.get(i) instanceof TerminalNodeImpl)
					&& formPars.contains(new Variable(exp.children.get(i).getText()))) {

				AbstractValue toReplace = actPars.get(formPars.indexOf(new Variable(exp.children.get(i).getText())));

				if (toReplace instanceof Subs) {
					for (ExpressionContext expR : ((Subs) toReplace).getRelations()) {
						try {
							ParseTree t = exp.children.set(i, parseExpression(expR.getText().toString()));
							result.add(parseExpression(exp.getText()));
							exp.children.set(i, t);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
					}
				}
			} 
		}

		return result;
	}

}
