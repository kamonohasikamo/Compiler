package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class StatementAssign extends CParseRule {
	// statementAssign ::= primary ASSIGN expression SEMI
	private CParseRule primary;
	private CParseRule expression;
	private CToken eq;

	public StatementAssign(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		primary = new Primary(pcx);
		primary.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_EQ) {
			eq = tk;
			tk = ct.getNextToken(pcx);
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_SEMI) {
				ct.getNextToken(pcx);
			}else {
				pcx.error("セミコロンください");
			}
		}else {
			pcx.error("イコールください");
		}
	}
	
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		final int s[][] = {
		//		T_err			T_int		   T_pint		  T_aint		T_apint
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err },	// T_err
			{	CType.T_err,	CType.T_int , CType.T_err, CType.T_err, CType.T_err},	// T_int
			{	CType.T_err,	CType.T_err, CType.T_pint	, CType.T_err ,  CType.T_err },	// T_pint
			{	CType.T_err,	CType.T_err , CType.T_err, CType.T_err, CType.T_err}, // T_aint
			{	CType.T_err,	CType.T_err , CType.T_err, CType.T_err, CType.T_err}	// T_apint
		};
		int lt = 0,rt = 0;
		if (primary != null) {
			primary.semanticCheck(pcx);
			lt = primary.getCType().getType();
			if(primary.isConstant()) {
				pcx.fatalError("定数に代入しないでください！");
			}
		}
		if (expression != null) {
			expression.semanticCheck(pcx);
			rt = expression.getCType().getType();
		}
		int nt = s[lt][rt];						// 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(eq.toExplainString() + "左辺の型[" + primary.getCType().toString() + "]と右辺の型[" + expression.getCType().toString() + "]は代入できません");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementAssign starts");
		if (expression != null) expression.codeGen(pcx);
		if(primary != null) primary.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; statementAssign: 左辺の取り出し");
		o.println("\tMOV\t-(R6), (R0)\t; statementAssign: 左辺に右辺を代入");
		o.println(";;; statementAssign completes");
	}
}