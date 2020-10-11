package it.univr.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import it.univr.domain.AbstractDomain;
import it.univr.domain.relational.substring.SubstringAbstractDomain;
import it.univr.main.antlr.MuJsLexer;
import it.univr.main.antlr.MuJsParser;
import it.univr.main.partitioning.PartitioningAnalyzer;
import it.univr.main.partitioning.TracePartitioningAbstractInterpreter;
import it.univr.main.relational.RelationalAbstractInterpreter;
import it.univr.main.relational.RelationalAnalyzer;

public class Main {
	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println(printHelp());
			return; 
		}

		String file = args[0];

		int callStringsSize = 3;

		boolean traces = false;
		boolean printStates = false;
		boolean asserts = false;

		AbstractDomain domain = new SubstringAbstractDomain();

		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-help")) {
				System.out.println(printHelp());
				return;
			}

			else if (args[i].equals("-trace")) {
				traces = true;
			}

			else if (args[i].equals("-assert")) {
				asserts = true;
			}

			else if (args[i].equals("-printState")) {
				printStates = true;
			}
		}



		if (traces) {
			TracePartitioningAbstractInterpreter analysis = PartitioningAnalyzer.analyze(file, domain, callStringsSize);

			if (printStates) {
				
				for (Integer i : analysis.getStoresAtMainCallString().keySet())
					System.out.println("Trace " + i  + "\n" + analysis.getStoresAtMainCallString().get(i) + "\n");
			}

			if (asserts) 
				System.err.println(analysis.getWarnings());
		}

		else  {
			RelationalAbstractInterpreter analysis = RelationalAnalyzer.analyze(file, domain, callStringsSize);

			if (printStates)
				System.out.println(analysis.getStoreAtMainCallString() + "\n\n");

			if (asserts) 
				System.err.println(analysis.getWarnings());
		}
	}



	public static TracePartitioningAbstractInterpreter analyzeWithTracePartitioning(String file, AbstractDomain domain, int callStringsSize) throws IOException {
		TracePartitioningAbstractInterpreter interpreter = new TracePartitioningAbstractInterpreter(domain, false, callStringsSize);

		interpreter.setAbstractDomain(domain);
		InputStream stream = new FileInputStream(file);

		MuJsLexer lexer = new MuJsLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));

		MuJsParser parser = new MuJsParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.program();
		interpreter.visit(tree);

		return interpreter;
	}

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


	private static String printHelp() {
		String result = "";
		result += "RSUBS static analyzer.\n";
		result += "Usage:";
		result +="java -jar rubs.jar <file> (<opt>)*\n\n";
		result +="where <opt> is one of:\n\n";
		result += "\t -printState \t\t prints the invariants for each program point\n";
		result += "\t -trace \t\t enables trace partitioning (default: false)\n";
		result += "\t -assert \t\t prints assertions checks (default: true)\n";
		result += "\t -help \t\t\t print the menu\n";

		return result;
	}
}
