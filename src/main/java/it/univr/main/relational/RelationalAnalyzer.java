package it.univr.main.relational;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import it.univr.domain.AbstractDomain;
import it.univr.main.antlr.MuJsLexer;
import it.univr.main.antlr.MuJsParser;

public class RelationalAnalyzer {
	
	public static RelationalAbstractInterpreter analyze(String file, AbstractDomain domain, int callStringsSize) throws IOException {
		RelationalAbstractInterpreter interpreter = new RelationalAbstractInterpreter(domain, false, callStringsSize);

		interpreter.setAbstractDomain(domain);
		InputStream stream = new FileInputStream(file);

		MuJsLexer lexer = new MuJsLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));

		MuJsParser parser = new MuJsParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.program();
		interpreter.visit(tree);

		return interpreter;
	}
}
