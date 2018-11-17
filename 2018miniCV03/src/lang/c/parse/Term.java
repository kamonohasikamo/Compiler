package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Term extends CParseRule {
	// term ::= term { TermMult | TermDiv}
	private CParseRule term;
	private ArrayList<CParseRule> factlist = new ArrayList<CParseRule>();

	public Term(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule list = null, left = null;
		term = new Factor(pcx);
		term.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		left = term;
		while(TermMult.isFirst(tk) || TermDiv.isFirst(tk)){
			list = null;
			if(TermMult.isFirst(tk)){
				list = new TermMult(pcx,left);
			}else{
				list = new TermDiv(pcx,left);
			}
			list.parse(pcx);
			factlist.add(list);
			left=list;
			tk=ct.getCurrentToken(pcx);
		}
		term = left;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(term.getCType());		// factor の型をそのままコピー
			this.setConstant(term.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (term!= null) { term.codeGen(pcx); }
		o.println(";;; term completes");
	}
}
