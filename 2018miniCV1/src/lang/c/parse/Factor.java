package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Factor extends CParseRule {
	// factor ::= number | factorAmp
	private CParseRule number, factor, factorAmp;
	public Factor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
			return Number.isFirst(tk) || FactorAmp.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if( Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
			factor = number;
		}else if(FactorAmp.isFirst(tk)) {
			factorAmp = new FactorAmp(pcx);
			factorAmp.parse(pcx);
			factor = factorAmp;
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType());		// number の型をそのままコピー
			setConstant(factor.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (number != null) { factor.codeGen(pcx); }
		o.println(";;; factor completes");
	}
}