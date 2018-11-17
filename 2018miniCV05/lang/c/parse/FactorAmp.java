package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP (number | primary)
	private CParseRule primary, num;
	//private CToken amp;
	public FactorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		//System.out.println("factorampにきてる");
		// ここにやってくるときは、必ずisFirst()が満たされている

		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		//amp = ct.getCurrentToken(pcx);

		if(Number.isFirst(tk)) {
			num = new Number(pcx);
			num.parse(pcx);
		}else if(Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		}else {
			pcx.fatalError("&の後ろはnumberかprimary");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(num != null) {
			num.semanticCheck(pcx);
			this.setCType(CType.getCType(CType.T_pint));
			this.setConstant(num.isConstant());
		}

		if(primary != null) {
			primary.semanticCheck(pcx);
			setCType(CType.getCType(CType.T_pint));	//*intに変える
			setConstant(primary.isConstant());			//numberは定数
			if(primary instanceof Primary) {
				if((((Primary) primary).getPrimary() instanceof PrimaryMult)) {
					pcx.fatalError("FactorAmp: &の後ろはアドレス");
					return;
				}
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factoramp starts");
		if(num != null) {num.codeGen(pcx);}
		if (primary != null) { primary.codeGen(pcx); }
		o.println(";;; factoramp completes");
	}
}