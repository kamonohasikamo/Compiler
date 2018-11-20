package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class MinusFactor extends CParseRule {
	// minusFactor ::= MINUS unsignedFactor
	
	private CParseRule unsignedfactor;
	private CToken minus;
	public MinusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		minus = tk;
		tk = ct.getNextToken(pcx);
		if(UnsignedFactor.isFirst(tk)) {
			unsignedfactor = new UnsignedFactor(pcx);
			unsignedfactor.parse(pcx);
		}else {
			pcx.error(tk.toExplainString() + " '-'の後ろにunsignedFactorがありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedfactor != null) {
			unsignedfactor.semanticCheck(pcx);
			if(unsignedfactor.getCType().getType() == CType.T_int) {
				setCType(unsignedfactor.getCType());
			}else {
				pcx.fatalError(minus.toExplainString() + " int型以外に-をつけないでください");
			}
			setConstant(unsignedfactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; minusfactor starts");
		if (unsignedfactor != null) { unsignedfactor.codeGen(pcx); }
		o.println("\tMOV\t#0x0000, R0\t; reverse sign");
		o.println("\tSUB\t-(R6), R0\t; reverse sign ");
		o.println("\tMOV\tR0, (R6)+\t; reverse sign ");
		o.println(";;; minusfactor completes");
	}
}