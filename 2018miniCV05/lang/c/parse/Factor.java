package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Factor extends CParseRule {
	// factor ::= number | factorAmp
	private CParseRule plusFactor;
	private CParseRule minusFactor;
	private CParseRule unsignedFactor;
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
			unsignedFactor = new UnsignedFactor(pcx);
			unsignedFactor.parse(pcx);
		} else if(PlusFactor.isFirst(tk)) {
			plusFactor = new PlusFactor(pcx);
			plusFactor.parse(pcx);
		} else if(MinusFactor.isFirst(tk)) {
			minusFactor = new MinusFactor(pcx);
			minusFactor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (plusFactor != null) {
			plusFactor.semanticCheck(pcx);
			setCType(plusFactor.getCType());		// number の型をそのままコピー
			setConstant(plusFactor.isConstant());	// number は常に定数
		}else if(minusFactor != null) {
			minusFactor.semanticCheck(pcx);
			setCType(minusFactor.getCType());		// number の型をそのままコピー
			setConstant(minusFactor.isConstant());	// number は常に定数
		}else if(unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			setCType(unsignedFactor.getCType());		// number の型をそのままコピー
			setConstant(unsignedFactor.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (plusFactor != null) { plusFactor.codeGen(pcx); }
		if (minusFactor != null) { minusFactor.codeGen(pcx); }
		if (unsignedFactor != null) { unsignedFactor.codeGen(pcx); }

		o.println(";;; factor completes");
	}
}



