package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class addressToValue extends CParseRule {
	// addressToValue ::= primary
	private CParseRule primary;
	public addressToValue(CParseContext pcx){
	}
	public static boolean isFirst(CToken tk){
		return Primary.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Primary.isFirst(tk)){
			primary = new Primary(pcx);
			primary.parse(pcx);
		}else{
			pcx.fatalError(tk.toExplainString() + "addrressToValue error");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(primary != null) {
			primary.semanticCheck(pcx);
			this.setCType(primary.getCType());
			this.setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; addressToValue starts");
		if(primary != null) {
			primary.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6), R0\t; addressToValue: アドレス取り出し");
		o.println("\tMOV\t(R0), (R6)+\t; addressToValue: 内容を積み直す");
		o.println(";;; addressToValue completes");
	}


}
