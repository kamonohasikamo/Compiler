package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Declaration extends CParseRule {
	//	declaration ::= intDecl | constDecl
	
	private CParseRule intdecl,constdecl;
	public Declaration(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(IntDecl.isFirst(tk)) {
			intdecl = new IntDecl(pcx);
			intdecl.parse(pcx);
		}else{
			constdecl = new ConstDecl(pcx);
			constdecl.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}
	

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; declaration starts");
		if (intdecl != null) { intdecl.codeGen(pcx); }
		if (constdecl != null) { constdecl.codeGen(pcx); }
		o.println(";;; declaration completes");
	}
}