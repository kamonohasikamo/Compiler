package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Program extends CParseRule {
	// program ::= expression EOF
	private CParseRule cpr;
	private ArrayList<CParseRule> declist;
	private ArrayList<CParseRule> stmlist;
	

	public Program(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Declaration.isFirst(tk) || Statement.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		declist = new ArrayList<CParseRule>();
		stmlist = new ArrayList<CParseRule>();
		while(Declaration.isFirst(tk)) {
			cpr = new Declaration(pcx);
			cpr.parse(pcx);
			declist.add(cpr);
			tk = ct.getCurrentToken(pcx);
		}
		while(Statement.isFirst(tk)) {
			cpr = new Statement(pcx);
			cpr.parse(pcx);
			stmlist.add(cpr);
			tk = ct.getCurrentToken(pcx);
		}
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		//if (program != null) { program.semanticCheck(pcx); }
		for(CParseRule dec: declist) {
			if(dec != null) { dec.semanticCheck(pcx); }
		}
		for(CParseRule stm: stmlist) {
			if(stm != null) { stm.semanticCheck(pcx); }
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		// ここには将来、宣言に対するコード生成が必要
		/*if (program != null) {
			o.println("__START:");
			o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
			program.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		}*/
		for(CParseRule dec: declist) {
			if(dec != null) { dec.codeGen(pcx); }
		}
		o.println("__START:");
		o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
		for(CParseRule stm: stmlist) {
			if(stm != null) { stm.codeGen(pcx); }
		}
		
		o.println("\tHLT\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode:");
		o.println(";;; program completes");
	}
}
