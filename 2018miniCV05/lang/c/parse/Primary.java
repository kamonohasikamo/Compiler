package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Primary extends CParseRule {
	//primary ::= primaryMult | variable
	private CParseRule primaryMult, variable;
	public Primary(CParseContext pcx){
	}
	public static boolean isFirst(CToken tk){
		return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		//System.out.println("primaryにきてるよ");
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(PrimaryMult.isFirst(tk)){
			primaryMult = new PrimaryMult(pcx);
			primaryMult.parse(pcx);
		}else if (Variable.isFirst(tk)){
			variable = new Variable(pcx);
			variable.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "Primary error");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {

		if(primaryMult != null) {
			primaryMult.semanticCheck(pcx);
			this.setCType(primaryMult.getCType());
			this.setConstant(primaryMult.isConstant());
		}
		if(variable != null) {
			variable.semanticCheck(pcx);
			this.setCType(variable.getCType());
			this.setConstant(variable.isConstant());
		}
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if(primaryMult != null) {primaryMult.codeGen(pcx);}
		if(variable != null) {variable.codeGen(pcx);}
		o.println(";;; primary completes");
	}

	public CParseRule getPrimary() {
		return primaryMult;
	}
}