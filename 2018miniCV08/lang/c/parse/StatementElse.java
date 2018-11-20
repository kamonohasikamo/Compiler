package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementElse extends CParseRule {
	// Else	::= ELSE (statementIf | LCUR {statement} RCUR )
	
	private CParseRule statementif;
	private CParseRule statement;
	private ArrayList<CParseRule> list;
	
	public StatementElse(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_ELSE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		list = new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(StatementIf.isFirst(tk)) {
			statementif = new StatementIf(pcx);
			statementif.parse(pcx);
		}else if(tk.getType() == CToken.TK_LC) {
			tk = ct.getNextToken(pcx);
			while(Statement.isFirst(tk)) {
				statement = new Statement(pcx);
				statement.parse(pcx);
				list.add(statement);
				tk = ct.getCurrentToken(pcx);
			}
			if(tk.getType() == CToken.TK_RC) {
				ct.getNextToken(pcx);
			}else {
				pcx.error(tk.toExplainString() + " '}'が来てません");
			}
		}else {
			pcx.error(tk.toExplainString() + " StatementIfまたは'{'が来てません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(statementif != null) { statementif.semanticCheck(pcx); }
		for(CParseRule item : list) {
			if(item != null) item.semanticCheck(pcx);
		}
	}
	

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementElse starts");
		if(statementif != null) { statementif.codeGen(pcx); }
		for(CParseRule item : list) {
			if(item != null)item.codeGen(pcx);
		}
		o.println(";;; statementElse completes");
	}
}
