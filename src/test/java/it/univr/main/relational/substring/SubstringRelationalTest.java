package it.univr.main.relational.substring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Test;


import it.univr.domain.relational.substring.Subs;
import it.univr.domain.relational.substring.SubstringAbstractDomain;
import it.univr.main.antlr.MuJsLexer;
import it.univr.main.antlr.MuJsParser;
import it.univr.main.antlr.MuJsParser.ExpressionContext;
import it.univr.main.relational.RelationalAnalyzer;
import it.univr.state.AbstractStore;
import it.univr.state.Variable;

public class SubstringRelationalTest {
	
	private SubstringAbstractDomain domain = new SubstringAbstractDomain();
	private String path = "src/test/resources/vmcai/relational/";
	private int callStringSize = 3;
	
	
	@Test
	public void testSubsRel001() throws Exception {
		String file = path + "rel001.imp";
		AbstractStore state = RelationalAnalyzer.analyze(file, domain, callStringSize).getStoreAtMainCallString();

		// State size
		Assert.assertEquals(state.size(), 1);

		
		Subs x = buildRelationalValue("\"aaa\"", "\"a\"", "\"aa\"");
		// State values
		Assert.assertEquals(state.getValue(new Variable("x")), x);
	}
	
	@Test
	public void testSubsRel002() throws Exception {
		String file = path + "rel002.imp";
		AbstractStore state = RelationalAnalyzer.analyze(file, domain, callStringSize).getStoreAtMainCallString();

		// State size
		Assert.assertEquals(state.size(), 2);

		
		Subs x = buildRelationalValue("\"aaa\"", "y", "\"a\"", "\"aa\"");
		Subs y = buildRelationalValue("\"a\"");
		
		// State values
		Assert.assertEquals(state.getValue(new Variable("x")), x);
		Assert.assertEquals(state.getValue(new Variable("y")), y);
	}
	
	@Test
	public void testSubsRel003() throws Exception {
		String file = path + "rel003.imp";
		AbstractStore state = RelationalAnalyzer.analyze(file, domain, callStringSize).getStoreAtMainCallString();

		// State size
		Assert.assertEquals(state.size(), 3);

		
		Subs x = buildRelationalValue("\"aaa\"", "y", "z", "\"a\"", "\"aa\"");
		Subs y = buildRelationalValue("\"a\"");
		Subs z = buildRelationalValue("\"aa\"", "\"a\"");
		
		// State values
		Assert.assertEquals(state.getValue(new Variable("x")), x);
		Assert.assertEquals(state.getValue(new Variable("y")), y);
		Assert.assertEquals(state.getValue(new Variable("z")), z);
	}
	
	
	@Test
	public void testSubsRel004() throws Exception {
		String file = path + "rel004.imp";
		AbstractStore state = RelationalAnalyzer.analyze(file, domain, callStringSize).getStoreAtMainCallString();

		// State size
		Assert.assertEquals(state.size(), 3);
		
		Subs x = buildRelationalValue("\"1\"");
		Subs y = buildRelationalValue("\"a\"");
		Subs z = buildRelationalValue("y", "x", "y+x", "\"a\"", "\"1\"");
		
		// State values
		Assert.assertEquals(state.getValue(new Variable("x")), x);
		Assert.assertEquals(state.getValue(new Variable("y")), y);
		Assert.assertEquals(state.getValue(new Variable("z")), z);
	}
	
	
	private ExpressionContext parseExpression(String exp) throws IOException {
		InputStream stream = new ByteArrayInputStream(exp.getBytes());
		MuJsLexer lexer = new MuJsLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		MuJsParser parser = new MuJsParser(new CommonTokenStream(lexer));
		ExpressionContext tree = parser.expression();
		return tree;
	}
	
	private Subs buildRelationalValue(String... exp) throws IOException {
		ArrayList<ExpressionContext> r = new ArrayList<MuJsParser.ExpressionContext>();
		
		for (String e : exp)
			r.add(parseExpression(e));
		
		return new Subs(r);
	}
}
