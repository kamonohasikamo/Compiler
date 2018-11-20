package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Term extends CParseRule {
	// term ::= factor { termMult | termDiv }
	private CParseRule term;
	public Term(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = null,list = null;
		factor = new Factor(pcx);
		factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while(TermMult.isFirst(tk) || TermDiv.isFirst(tk)) {
			if(TermMult.isFirst(tk)) {
				list = new TermMult(pcx, factor);
				list.parse(pcx);
				factor = list;
				tk = ct.getCurrentToken(pcx);
			}else {
				list = new TermDiv(pcx, factor);
				list.parse(pcx);
				factor = list;
				tk = ct.getCurrentToken(pcx);
			}
		}
		term = factor;
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
		if (term != null) { term.codeGen(pcx); }
		o.println(";;; term completes");
	}
}

class TermMult extends CParseRule {
	// MULT factor
	private CToken mul;
	private CParseRule left, right;

	public TermMult(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		mul = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "+の後ろはtermです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 足し算の型計算規則
		final int s[][] = {
		//		T_err			T_int		   T_pint
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err} ,	// T_err
			{	CType.T_err,	CType.T_int , CType.T_err , CType.T_err , CType.T_err},	// T_int
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err} ,   // T_pint
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err} ,
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err} 
		};
		int lt = 0, rt = 0;
		boolean lc = false, rc = false;
		if (left != null) {
			left.semanticCheck(pcx);
			lt = left.getCType().getType();		// +の左辺の型
			lc = left.isConstant();
		} else {
			pcx.fatalError(mul.toExplainString() + "左辺がありません");
		}
		if (right != null) {
			right.semanticCheck(pcx);
			rt = right.getCType().getType();	// +の右辺の型
			rc = right.isConstant();
		} else {
			pcx.fatalError(mul.toExplainString() + "右辺がありません");
		}
		int nt = s[lt][rt];						// 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(mul.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は掛けれません");
		}
		this.setCType(CType.getCType(nt));
		this.setConstant(lc && rc);				// +の左右両方が定数のときだけ定数
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			o.println("\tJSR\tMUL; TermMult:");
			o.println("\tSUB\t#0x0002, R6");
			o.println("\tMOV\tR0, (R6)+");
		}
	}
}

class TermDiv extends CParseRule {
	// DIV factor
	private CToken div;
	private CParseRule left, right;

	public TermDiv(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		div = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "+の後ろはtermです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 足し算の型計算規則
		final int s[][] = {
		//		T_err			T_int		   T_pint
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err },	// T_err
			{	CType.T_err,	CType.T_int , CType.T_err , CType.T_err , CType.T_err},	// T_int
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err },   // T_pint
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err} ,
			{	CType.T_err,	CType.T_err , CType.T_err , CType.T_err , CType.T_err} 
		};
		int lt = 0, rt = 0;
		boolean lc = false, rc = false;
		if (left != null) {
			left.semanticCheck(pcx);
			lt = left.getCType().getType();		// +の左辺の型
			lc = left.isConstant();
		} else {
			pcx.fatalError(div.toExplainString() + "左辺がありません");
		}
		if (right != null) {
			right.semanticCheck(pcx);
			rt = right.getCType().getType();	// +の右辺の型
			rc = right.isConstant();
		} else {
			pcx.fatalError(div.toExplainString() + "右辺がありません");
		}
		int nt = s[lt][rt];						// 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(div.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は割れません");
		}
		this.setCType(CType.getCType(nt));
		this.setConstant(lc && rc);				// +の左右両方が定数のときだけ定数
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			right.codeGen(pcx);
			left.codeGen(pcx);
			o.println("\tJSR\tDIV; TermDiv:");
			o.println("\tSUB\t#0x0002, R6");
			o.println("\tMOV\tR0, (R6)+");
		}
	}
}