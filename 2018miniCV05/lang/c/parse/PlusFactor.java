package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class PlusFactor extends CParseRule {
	//factor ::= PLUS unsignedFactor
	private CParseRule unsignedFactor;
	private CToken plus;
	public PlusFactor(CParseContext pcx){
	}
	public static boolean isFirst(CToken tk){
		return tk.getType() == CToken.TK_PLUS;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		plus = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_PLUS) {
			if(UnsignedFactor.isFirst(tk)){
				unsignedFactor = new UnsignedFactor(pcx);
				unsignedFactor.parse(pcx);

			}else{
				pcx.fatalError(tk.toExplainString() + "+の後ろはFactorです:plusFactor");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			setCType(unsignedFactor.getCType());		// 型はfactorと同じ
			setConstant(unsignedFactor.isConstant());	// factorは常に定数
		}
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; plusfactor starts");
		if (unsignedFactor != null)
			unsignedFactor.codeGen(pcx);
		o.println(";;; plusfactor completes");
	}



}
