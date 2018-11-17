package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Factor extends CParseRule {
	// factor ::= number | factorAmp
	private CParseRule factor;
	public Factor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(UnsignedFactor.isFirst(tk) == true) {
			factor = new UnsignedFactor(pcx);
		} else if(PlusFactor.isFirst(tk)) {
			factor = new PlusFactor(pcx);
		} else if(MinusFactor.isFirst(tk)) {
			factor = new MinusFactor(pcx);
		}
		factor.parse(pcx);
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
		if (factor != null) { factor.codeGen(pcx); }
		o.println(";;; factor completes");
	}
}



