package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementInput extends CParseRule {
	// statementInput ::= INPUT (primary | factorAMP) SEMI
	
	private CParseRule primary;
	private CParseRule factoramp;
	
	public StatementInput(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		}else if(FactorAmp.isFirst(tk)) {
			factoramp = new FactorAmp(pcx);
			factoramp.parse(pcx);
		}else {
			pcx.error(tk.toExplainString() + "PrimaryかFactorAmpです");
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_SEMI) {
			ct.getNextToken(pcx);		
		}else {
			pcx.error(tk.toExplainString() + " ；が抜けています");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(primary != null) { primary.semanticCheck(pcx); }
		if(factoramp != null) { factoramp.semanticCheck(pcx); }
	}
	

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementInput starts");
		if(primary != null) { primary.codeGen(pcx); }
		if(factoramp != null) { factoramp.codeGen(pcx); }
		o.println("\tMOV\t#0xFFE0, R1\t; StatementInput: 0xFFE0をR1に");
		o.println("\tMOV\t-(R6), R0\t; StatementInput:");
		o.println("\tMOV\t(R1), (R0)\t; StatementInput: 入力値を番地中に");
		o.println(";;; statementInput completes");
	}
}
