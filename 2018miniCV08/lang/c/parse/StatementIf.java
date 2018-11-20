package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementIf extends CParseRule {
	// statementIf	::= IF LPAR condition RPAR LCUR {statement} RCUR [statementElse]
	
	private CParseRule condition;
	private CParseRule statement;
	private ArrayList<CParseRule> list;
	private CParseRule statementelse;
	private int seq;
	
	public StatementIf(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IF;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		list = new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_LP) {
			tk = ct.getNextToken(pcx);
			if(Condition.isFirst(tk)) {
				condition = new Condition(pcx);
				condition.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() == CToken.TK_RP) {
					tk = ct.getNextToken(pcx);
					if(tk.getType() == CToken.TK_LC) {
						tk = ct.getNextToken(pcx);
						while(Statement.isFirst(tk)) {
							statement = new Statement(pcx);
							statement.parse(pcx);
							list.add(statement);
							tk = ct.getCurrentToken(pcx);
						}
						if(tk.getType() == CToken.TK_RC) {
							tk = ct.getNextToken(pcx);
							if(StatementElse.isFirst(tk)) {
								statementelse = new StatementElse(pcx);
								statementelse.parse(pcx);
							}
						}else {
							pcx.error(tk.toExplainString() + " '}'が来てません");
						}
					}else {
						pcx.error(tk.toExplainString() + " '{'が来てません");
					}
				}else {
					pcx.error(tk.toExplainString() + " ')'が来てません");
				}
			}else {
				pcx.error(tk.toExplainString() + " Conditionが来てません");
			}
		}else {
			pcx.error(tk.toExplainString() + " '('が来てません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(condition != null) { condition.semanticCheck(pcx); }
		for(CParseRule item : list) {
			if(item != null) item.semanticCheck(pcx);
		}
		if(statementelse != null) { statementelse.semanticCheck(pcx); }
	}
	

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementIf starts");
		if(condition != null) { condition.codeGen(pcx); }
		seq = pcx.getSeqId();
		int tmpseq = seq;
		o.println("\tMOV\t-(R6), R0\t; StatementIf: フラグを取り出す");
		o.println("\tCMP\t#0x0000, R0\t; StatementIf: 0と比較");
		o.println("\tBRZ\tELSE" + seq + "\t; StatementIf: falseならジャンプ");
		for(CParseRule item : list) {
			item.codeGen(pcx);
		}
		seq = pcx.getSeqId();
		o.println("\tJMP\tEND" + seq + "\t; StatementIf: 強制的にジャンプ");
		o.println("ELSE" + tmpseq + ":\t; StatementIf: falseならジャンプ");
		if(statementelse != null) { statementelse.codeGen(pcx); }
		o.println("END" + seq + ":\t; StatementIf:");
		o.println(";;; statementIf completes");
	}
}