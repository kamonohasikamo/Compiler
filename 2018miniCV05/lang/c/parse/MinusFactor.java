package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class MinusFactor extends CParseRule {
	//factor ::=MINUS unsignedFactor
	//private CToken ;
	private CParseRule unsignedFactor;
	private CToken minus;
	public MinusFactor(CParseContext pcx){
	}
	public static boolean isFirst(CToken tk){
		return tk.getType() == CToken.TK_MINUS;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		minus = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_MINUS) {
			tk = ct.getNextToken(pcx);
			if(UnsignedFactor.isFirst(tk)){
				unsignedFactor= new UnsignedFactor(pcx);
				unsignedFactor.parse(pcx);
			}else{
				pcx.fatalError(tk.toExplainString() + "-の後はunsignedfactorです:minusfactor");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			setCType(unsignedFactor.getCType());
			setConstant(unsignedFactor.isConstant());
			if(unsignedFactor.getCType() == CType.getCType(CType.T_pint)){
				pcx.fatalError(minus.toExplainString() + "番地の前に-はつけません");
				/*setCType(unsignedFactor.getCType());		// 型はfactorと同じ
				setConstant(unsignedFactor.isConstant());	// factorは常に定数*/
			}/*else {
				pcx.fatalError(minus.toExplainString() + "番地の前に-はつけません");
			}*/
		}
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; minusfactor starts");
		if (unsignedFactor != null)
			unsignedFactor.codeGen(pcx);
			o.println("\tMOV #0, R0\t; MinusFactor:R0を0に");
			o.println("\tSUB -(R6), R0\t; MinusFactor:R0(=0) - 符号反転したい数 で符号を反転 <"+ minus.toExplainString()+">");
			o.println("\tMOV R0, (R6)+\t; MinusFactor:スタック積み直し");
			o.println(";;; minusfactor completes");
	}


}
