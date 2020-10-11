package it.univr.main.partitioning;

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

public class PartitioningAnalyzer {
	

	public static TracePartitioningAbstractInterpreter analyze(String file, AbstractDomain domain, int callStringsSize) throws IOException {
		TracePartitioningAbstractInterpreter interpreter = new TracePartitioningAbstractInterpreter(domain, false, callStringsSize);

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
		result += "MuJS static analyzer.\n";
		result += "Usage:";
		result +="java -jar mujs.jar <file> (<opt>)*\n\n";
		result +="where <opt> is one of:\n\n";
		result += "\t -narr \t\t\t enables narrowing operator (default: disabled)\n";
		result += "\t -widening n \t\t set the parametric widening to n\n";
		result += "\t -coalesced \t\t enable the coalesced sum abstract domain (default)\n";
		result += "\t -lifted \t\t enable the lifted union abstract domain\n";
		result += "\t -invarians \t\t prints the invariants for each statement program point.\n";
		result += "\t\t\t\t By default, it prints only the memory holding at the end of the abstract execution\n";
		result += "\t -help \t\t\t print the menu\n";

		return result;
	}

	private static String potd() {
		return "MMMMMMMM               MMMMMMMM                            JJJJJJJJJJJ   SSSSSSSSSSSSSSS \n" +
				"M:::::::M             M:::::::M                            J:::::::::J SS:::::::::::::::S\n"+
				"M::::::::M           M::::::::M                            J:::::::::JS:::::SSSSSS::::::S\n"+
				"M:::::::::M         M:::::::::M                            JJ:::::::JJS:::::S     SSSSSSS\n"+
				"M::::::::::M       M::::::::::Muuuuuu    uuuuuu              J:::::J  S:::::S            \n"+
				"M:::::::::::M     M:::::::::::Mu::::u    u::::u              J:::::J  S:::::S            \n"+
				"M:::::::M::::M   M::::M:::::::Mu::::u    u::::u              J:::::J   S::::SSSS         \n"+
				"M::::::M M::::M M::::M M::::::Mu::::u    u::::u              J:::::j    SS::::::SSSSS    \n"+
				"M::::::M  M::::M::::M  M::::::Mu::::u    u::::u              J:::::J      SSS::::::::SS  \n"+
				"M::::::M   M:::::::M   M::::::Mu::::u    u::::u  JJJJJJJ     J:::::J         SSSSSS::::S \n"+
				"M::::::M    M:::::M    M::::::Mu::::u    u::::u  J:::::J     J:::::J              S:::::S\n"+
				"M::::::M     MMMMM     M::::::Mu:::::uuuu:::::u  J::::::J   J::::::J              S:::::S\n"+
				"M::::::M               M::::::Mu:::::::::::::::uuJ:::::::JJJ:::::::J  SSSSSSS     S:::::S\n"+
				"M::::::M               M::::::M u:::::::::::::::u JJ:::::::::::::JJ   S::::::SSSSSS:::::S\n"+
				"M::::::M               M::::::M  uu::::::::uu:::u   JJ:::::::::JJ     S:::::::::::::::SS \n"+
				"MMMMMMMM               MMMMMMMM    uuuuuuuu  uuuu     JJJJJJJJJ        SSSSSSSSSSSSSSS\n";
	}
}
