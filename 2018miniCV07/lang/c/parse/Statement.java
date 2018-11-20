package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Statement extends CParseRule {
	// statement ::= statementAssign | statementIf | statementWhile | statementInput | statementOutput
	private CParseRule stm;
	public Statement(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(StatementAssign.isFirst(tk)) {
			stm = new StatementAssign(pcx);
			stm.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (stm != null) {
			stm.semanticCheck(pcx);
		}
		
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement starts");
		if (stm != null) { stm.codeGen(pcx); }
		o.println(";;; statement completes");
	}
}