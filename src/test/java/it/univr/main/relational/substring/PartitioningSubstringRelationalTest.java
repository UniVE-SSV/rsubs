package it.univr.main.relational.substring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Test;

import it.univr.domain.relational.substring.Subs;
import it.univr.domain.relational.substring.SubstringAbstractDomain;
import it.univr.main.antlr.MuJsLexer;
import it.univr.main.antlr.MuJsParser;
import it.univr.main.antlr.MuJsParser.ExpressionContext;
import it.univr.main.partitioning.PartitioningAnalyzer;
import it.univr.state.AbstractStore;
import it.univr.state.Variable;

public class PartitioningSubstringRelationalTest {
	private SubstringAbstractDomain domain = new SubstringAbstractDomain();
	private String path = "src/test/resources/vmcai/relational/";
	private int callStringSize = 3;
	
	
	@Test
	public void testSubsRel001() throws Exception {
		String file = path + "rel001.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(0);
		// State size
		Assert.assertEquals(store.size(), 1);

		
		Subs x = buildRelationalValue("\"aaa\"", "\"aa\"", "\"a\"");
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
	}
	
	@Test
	public void testSubsRel002() throws Exception {
		String file = path + "rel002.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(0);
		
		// State size
		Assert.assertEquals(store.size(), 2);

		
		Subs x = buildRelationalValue("\"aaa\"", "y", "\"a\"", "\"aa\"");
		Subs y = buildRelationalValue("\"a\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
	}
	
	@Test
	public void testSubsRel003() throws Exception {
		String file = path + "rel003.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(0);
		// State size
		Assert.assertEquals(store.size(), 3);

		
		Subs x = buildRelationalValue("\"aaa\"", "y", "z", "\"a\"", "\"aa\"");
		Subs y = buildRelationalValue("\"a\"");
		Subs z = buildRelationalValue("\"aa\"", "\"a\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	
	@Test
	public void testSubsRel004() throws Exception {
		String file = path + "rel004.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(0);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"1\"");
		Subs y = buildRelationalValue("\"a\"");
		Subs z = buildRelationalValue("y", "x", "y+x", "\"a\"", "\"1\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	
	@Test
	public void testSubsRel005() throws Exception {
		String file = path + "rel005.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(1);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"a\"");
		Subs y = buildRelationalValue("\"b\"");
		Subs z = buildRelationalValue("\"d\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	@Test
	public void testSubsRel006() throws Exception {
		String file = path + "rel006.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(1);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"abc\"", "\"a\"", "\"bc\"", "\"b\"", "\"ab\"", "\"c\"");
		Subs y = buildRelationalValue("\"ok\"", "\"o\"", "\"k\"");
		Subs z = buildRelationalValue("\"1\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("c")), z);
	}
	
	@Test
	public void testSubsRel007() throws Exception {
		String file = path + "rel007.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(1);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"abc\"", "\"a\"", "\"bc\"", "\"b\"", "\"ab\"", "\"c\"");
		Subs y = buildRelationalValue("\"ok\"", "\"o\"", "\"k\"");
		Subs z = buildRelationalValue("\"1\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("c")), z);
	}
	
	@Test
	public void testSubsRel008() throws Exception {
		String file = path + "rel008.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(0);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"a\"");
		Subs y = buildRelationalValue("\"b\"");
		Subs z = buildRelationalValue("\"1\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	@Test
	public void testSubsRel009() throws Exception {
		String file = path + "rel009.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(1);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"a\"");
		Subs y = buildRelationalValue("\"c\"");
		Subs z = buildRelationalValue("\"ok\"", "\"k\"", "\"o\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	@Test
	public void testSubsRel010() throws Exception {
		String file = path + "rel010.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(1);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"a\"");
		Subs y = buildRelationalValue("\"c\"");
		Subs z = buildRelationalValue("\"ok\"", "\"k\"", "\"o\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	@Test
	public void testSubsRel011() throws Exception {
		String file = path + "rel011.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(2);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"a\"");
		Subs y = buildRelationalValue("\"c\"");
		Subs z = buildRelationalValue("\"ok\"", "\"k\"", "\"o\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	@Test
	public void testSubsRel012() throws Exception {
		String file = path + "rel012.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(2);
		// State size
		Assert.assertEquals(store.size(), 3);
		
		Subs x = buildRelationalValue("\"ok\"", "\"k\"", "\"o\"");
		Subs y = buildRelationalValue("\"c\"");
		Subs z = buildRelationalValue("\"ok\"", "x", "\"k\"", "\"o\"");		
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
	}
	
	@Test
	public void testSubsRel013() throws Exception {
		String file = path + "rel013.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(2);
		
		// State size
		Assert.assertEquals(store.size(), 5);
		
		Subs x = buildRelationalValue("\"a\"");
		Subs y = buildRelationalValue("\"b\"");
		Subs z = buildRelationalValue("x+y+\"a\"", "x", "y", "x+y", "\"a\"", "y+\"a\"", "\"b\"");
		Subs w = buildRelationalValue("z+\"b\"", "z", "x", "y", "x+y", "y+\"a\"", "x+y+\"a\"",  "\"b\"", "\"a\"");
		Subs h = buildRelationalValue("\"c\"");
		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
		Assert.assertEquals(store.getValue(new Variable("y")), y);
		Assert.assertEquals(store.getValue(new Variable("h")), h);
		Assert.assertEquals(store.getValue(new Variable("z")), z);
		Assert.assertEquals(store.getValue(new Variable("w")), w);
	}
	
	@Test
	public void testSubsRel015() throws Exception {
		String file = path + "rel015.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size
		Assert.assertEquals(stores.size(), 1);

		AbstractStore store = stores.get(0);
		
		// State size
		Assert.assertEquals(store.size(), 1);
		
		Subs x = buildRelationalValue("\"abc\"", "\"ab\"", "\"bc\"", "\"a\"", "\"b\"", "\"c\"");

		
		// State values
		Assert.assertEquals(store.getValue(new Variable("x")), x);
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
