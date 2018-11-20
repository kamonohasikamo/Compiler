package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class FactorAmp extends CParseRule {
	// factorAmp ::= Amp ( number | primary )
	private CParseRule number;
	private CParseRule primary;
	private CToken amp;
	public FactorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		amp = tk;
		tk = ct.getNextToken(pcx);
		if(Number.isFirst(tk)){
			number = new Number(pcx);
			number.parse(pcx);
		}else if(Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		}else {
			pcx.error(tk.toExplainString() + " '&'の次にnumberまたはprimaryがありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			setCType(CType.getCType(CType.T_pint));		
			setConstant(number.isConstant());	// number は常に定数
		}
		if (primary != null) {
			primary.semanticCheck(pcx);
			if(((Primary)(primary)).getCPR() instanceof PrimaryMult) {
				pcx.error("factorAmp の子節点にprimary がつながっているとき、その下にはprimaryMult クラスのオブジェクトが来てはいけません");
			}
			setCType(CType.getCType(CType.T_apint));		
			setConstant(primary.isConstant());	
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		if (number != null) { number.codeGen(pcx); }
		if( primary != null) { primary.codeGen(pcx); }
		o.println(";;; factorAmp completes");
	}
}