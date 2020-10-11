package it.univr.main.relational.substring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

public class VMCAITest {
	private SubstringAbstractDomain domain = new SubstringAbstractDomain();
	private String path = "src/test/resources/vmcai/";
	private int callStringSize = 3;


	@Test
	public void testVMCAI001() throws Exception {
		String file = path + "paper/notcon.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;


		for (Integer i : stores.keySet())
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));

			// State size
			Assert.assertEquals(store.size(), 1);

			Subs r = buildRelationalValue("\"ed\"", "\"a\"", "\"d\"", "\"e\"", "\" \"", "\"t \"");

			for (String s : getAllSubstrings("not constant"))
				r.addRelation(parseExpression("\"" +  s + "\""));

			Assert.assertEquals(store.getValue(new Variable("r")), r);
	}

	@Test
	public void testVMCAI002() throws Exception {
		String file = path + "paper/rep.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet())
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));

			// State size
			Assert.assertEquals(store.size(), 2);

			Subs r = buildRelationalValue("\"Elem: \\n\"+v", "v", "v+\"\\n\"", "\"Elem: \\n\"+v+\"\\n\"");

			for (String s : getAllSubstrings("Elem: \\n"))
				r.addRelation(parseExpression("\"" +  s + "\""));
			r.addRelation(parseExpression("\"Elem: \\n\""));

			// State values
			Assert.assertEquals(store.getValue(new Variable("r")), r);
			Assert.assertEquals(store.getValue(new Variable("v")), new Subs());
	}

	@Test
	public void testVMCAI003WithoutTracePartitioning() throws Exception {
		String file = path + "codota/secname.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 3);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 4);

		Subs res = buildRelationalValue();
		Subs pr1 = buildRelationalValue();
		Subs pr2 = buildRelationalValue();
		Subs javaName = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("pr1")), pr1);
		Assert.assertEquals(store.getValue(new Variable("pr2")), pr2);
		Assert.assertEquals(store.getValue(new Variable("javaName")), javaName);
	}


	@Test
	public void testVMCAI003WithTracePartitioning() throws Exception {
		String file = path + "codota/secname.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 3);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [1]
		store = stores.get(1);

		Assert.assertEquals(store.size(), 4);

		Subs res = buildRelationalValue("pr2", "pr2+javaName.substring(4,javaName.length)", "javaName.substring(4,javaName.length)");
		Subs pr1 = buildRelationalValue();
		Subs pr2 = buildRelationalValue();
		Subs javaName = buildRelationalValue("pr1");

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("pr1")), pr1);
		Assert.assertEquals(store.getValue(new Variable("pr2")), pr2);
		Assert.assertEquals(store.getValue(new Variable("javaName")), javaName);

		// Check trace [3]

		store = stores.get(3);

		Assert.assertEquals(store.size(), 4);

		res = buildRelationalValue("pr1", "pr1+javaName.substring(4,javaName.length)", "javaName.substring(4,javaName.length)");
		pr1 = buildRelationalValue();
		pr2 = buildRelationalValue();
		javaName = buildRelationalValue("pr2");

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("pr1")), pr1);
		Assert.assertEquals(store.getValue(new Variable("pr2")), pr2);
		Assert.assertEquals(store.getValue(new Variable("javaName")), javaName);

		// Check trace [4]
		store = stores.get(4);

		//State size
		Assert.assertEquals(store.size(), 4);

		res = buildRelationalValue("javaName");
		pr1 = buildRelationalValue();
		pr2 = buildRelationalValue();
		javaName = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("pr1")), pr1);
		Assert.assertEquals(store.getValue(new Variable("pr2")), pr2);
		Assert.assertEquals(store.getValue(new Variable("javaName")), javaName);
	}

	@Test
	public void testVMCAI004WithoutTracePartitioning() throws Exception {
		String file = path + "codota/parseIso8601Date.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 1);

		Subs dateString = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("dateString")), dateString);
	}

	@Test
	public void testVMCAI004WithTracePartitioning() throws Exception {
		String file = path + "codota/parseIso8601Date.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [1]
		store = stores.get(1);

		Assert.assertEquals(store.size(), 1);

		Subs dateString = buildRelationalValue("\"Z\"", "dateString.substring(0, dateString.length+-5)+\"Z\"", "dateString.substring(0, dateString.length+-5)");


		for (String s : getAllSubstrings("+0000"))
			dateString.addRelation(parseExpression("\"" +  s + "\""));
		dateString.addRelation(parseExpression("\"+0000\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("dateString")), dateString);

		// Check trace [2]
		store = stores.get(2);

		Assert.assertEquals(store.size(), 1);

		dateString = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("dateString")), dateString);
	}

	@Test
	public void testVMCAI005WithoutTracePartitioning() throws Exception {
		String file = path + "codota/verifyProtocol.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 1);

		Subs url = buildRelationalValue("\"http://\"");

		for (String s : getAllSubstrings("http://"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("url")), url);
	}

	@Test
	public void testVMCAI005WithTracePartitioning() throws Exception {
		String file = path + "codota/verifyProtocol.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [1]
		store = stores.get(1);

		Assert.assertEquals(store.size(), 1);

		Subs url = buildRelationalValue("\"http://\"");

		for (String s : getAllSubstrings("http://"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("url")), url);

		// Check trace [2]
		store = stores.get(2);

		Assert.assertEquals(store.size(), 1);

		url = buildRelationalValue("\"http://\"", "\"http://\"+url");

		for (String s : getAllSubstrings("http://"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("url")), url);
	}


	@Test
	public void testVMCAI006WithoutTracePartitioning() throws Exception {
		String file = path + "codota/verifyProtocol2.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 3);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 1);

		Subs url = buildRelationalValue("\"http\"");

		for (String s : getAllSubstrings("http"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		for (String s : getAllSubstrings("://"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("url")), url);
	}

	@Test
	public void testVMCAI006WithTracePartitioning() throws Exception {
		String file = path + "codota/verifyProtocol2.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 3);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [1]
		store = stores.get(1);

		Assert.assertEquals(store.size(), 1);

		Subs url = buildRelationalValue("\"http://\"");

		for (String s : getAllSubstrings("http://"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("url")), url);

		// Check trace [3]
		store = stores.get(3);

		Assert.assertEquals(store.size(), 1);

		url = buildRelationalValue("\"https://\"");

		for (String s : getAllSubstrings("https://"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("url")), url);

		// Check trace [4]
		store = stores.get(4);

		Assert.assertEquals(store.size(), 1);

		url = buildRelationalValue("\"http://\"", "\"http://\"+url");

		for (String s : getAllSubstrings("http://"))
			url.addRelation(parseExpression("\"" +  s +"\""));

		// State values
		Assert.assertEquals(store.getValue(new Variable("url")), url);
	}

	@Test
	public void testVMCAI007WithoutTracePartitioning() throws Exception {
		String file = path + "common-lang/appendIfMissing.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue("str", "suffix");
		Subs str = buildRelationalValue();
		Subs suffix = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("suffix")), suffix);
	}

	@Test
	public void testVMCAI007WithTracePartitioning() throws Exception {
		String file = path + "common-lang/appendIfMissing.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [1]
		store = stores.get(1);

		Assert.assertEquals(store.size(), 3);

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue("str", "suffix");
		Subs str = buildRelationalValue("suffix");
		Subs suffix = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("suffix")), suffix);

		// Check trace [2]
		store = stores.get(2);

		//State size
		Assert.assertEquals(store.size(), 3);

		res = buildRelationalValue("str", "str+suffix", "suffix");
		str = buildRelationalValue();
		suffix = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("suffix")), suffix);
	}

	@Test
	public void testVMCAI008WithoutTracePartitioning() throws Exception {
		String file = path + "common-lang/prependIfMissing.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue("str", "prefix");
		Subs str = buildRelationalValue();
		Subs prefix = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("prefix")), prefix);
	}

	@Test
	public void testVMCAI008WithTracePartitioning() throws Exception {
		String file = path + "common-lang/prependIfMissing.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [1]
		store = stores.get(1);

		Assert.assertEquals(store.size(), 3);

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue("str", "prefix");
		Subs str = buildRelationalValue("prefix");
		Subs prefix = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("prefix")), prefix);

		// Check trace [2]
		store = stores.get(2);

		//State size
		Assert.assertEquals(store.size(), 3);

		res = buildRelationalValue("str", "prefix+str", "prefix");
		str = buildRelationalValue();
		prefix = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("prefix")), prefix);
	}


	@Test
	public void testVMCAI009WithoutTracePartitioning() throws Exception {
		String file = path + "common-lang/removeEnd.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue();
		Subs str = buildRelationalValue();
		Subs remove = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("remove")), remove);
	}

	@Test
	public void testVMCAI009WithTracePartitioning() throws Exception {
		String file = path + "common-lang/removeEnd.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [3]
		store = stores.get(3);

		Assert.assertEquals(store.size(), 3);

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue("str.substring(0, str.length -remove.length)");
		Subs str = buildRelationalValue("remove");
		Subs remove = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("remove")), remove);

		// Check trace [4]
		store = stores.get(4);

		//State size
		Assert.assertEquals(store.size(), 3);

		res = buildRelationalValue("str");
		str = buildRelationalValue();
		remove = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("remove")), remove);
	}


	@Test
	public void testVMCAI010WithoutTracePartitioning() throws Exception {
		String file = path + "common-lang/removeStart.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue();
		Subs str = buildRelationalValue();
		Subs remove = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("remove")), remove);
	}

	@Test
	public void testVMCAI010WithTracePartitioning() throws Exception {
		String file = path + "common-lang/removeStart.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 2);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [3]
		store = stores.get(3);

		Assert.assertEquals(store.size(), 3);

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue("str.substring(remove.length, str.length)");
		Subs str = buildRelationalValue("remove");
		Subs remove = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("remove")), remove);

		// Check trace [4]
		store = stores.get(4);

		//State size
		Assert.assertEquals(store.size(), 3);

		res = buildRelationalValue("str");
		str = buildRelationalValue();
		remove = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
		Assert.assertEquals(store.getValue(new Variable("remove")), remove);
	}

	@Test
	public void testVMCAI011WithoutTracePartitioning() throws Exception {
		String file = path + "common-lang/right.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 3);

		// Merge all the trace states
		AbstractStore store = null;

		for (Integer i : stores.keySet()) {
			store = store == null ? stores.get(i) : store.leastUpperBound(stores.get(i));
		}

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue();
		Subs str = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
	}

	@Test
	public void testVMCAI011WithTracePartitioning() throws Exception {
		String file = path + "common-lang/right.imp";
		Map<Integer, AbstractStore> stores = PartitioningAnalyzer.analyze(file, domain, callStringSize).getStoresAtMainCallString();

		// Stores size (traces)
		Assert.assertEquals(stores.size(), 3);

		// Merge all the trace states
		AbstractStore store = null;

		// Check trace [3]
		store = stores.get(3);

		Assert.assertEquals(store.size(), 3);

		//State size
		Assert.assertEquals(store.size(), 3);

		Subs res = buildRelationalValue("str");
		Subs str = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);

		// Check trace [4]
		store = stores.get(4);

		//State size
		Assert.assertEquals(store.size(), 3);

		res = buildRelationalValue("str.substring(0,str.length)");
		str = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);

		// Check trace [1]
		store = stores.get(1);

		Assert.assertEquals(store.size(), 3);

		//State size
		Assert.assertEquals(store.size(), 3);

		res = buildRelationalValue();
		str = buildRelationalValue();

		// State values
		Assert.assertEquals(store.getValue(new Variable("res")), res);
		Assert.assertEquals(store.getValue(new Variable("str")), str);
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

	private Set<String> getAllSubstrings(String str) {
		HashSet<String> res = new HashSet<String>();

		for (int i = 0; i < str.length(); i++) 
			for (int j = i+1; j <= str.length(); j++) 
				res.add(str.substring(i,j));

		return res;
	}
}
