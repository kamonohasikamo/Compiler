package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class TermMult extends CParseRule {
    // termDiv ::= MULT term
    private CToken mult;
    private CParseRule left, right;

    public TermMult(CParseContext pcx, CParseRule left) {
        this.left = left;
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_MUL;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        mult = ct.getCurrentToken(pcx);
        CToken tk = ct.getNextToken(pcx);

        if(Factor.isFirst(tk)){
            right = new Factor(pcx);
            right.parse(pcx);
        }else{
            pcx.fatalError("*の後ろはfactor");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        // 掛け算の型計算規則
        final int s[][] = {
                //		T_err				T_int				T_pint			T_intArray		T_pintArray
                {	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err},	// T_err
                {	CType.T_err,	CType.T_int,	CType.T_err,	CType.T_err,	CType.T_err},	// T_int
                {	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err},	// T_pint
                {	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err},	//T_intArray
                {	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err,	CType.T_err},	//T_pintArray
        };
        int lt = 0, rt = 0;
        boolean lc = false, rc = false;
        if (left != null) {
            left.semanticCheck(pcx);
            lt = left.getCType().getType();		// /の左辺の型
            lc = left.isConstant();
        } else {
            pcx.fatalError(mult.toExplainString() + "左辺がありません");
        }
        if (right != null) {
            right.semanticCheck(pcx);
            rt = right.getCType().getType();	// /の右辺の型
            rc = right.isConstant();
        } else {
            pcx.fatalError(mult.toExplainString() + "右辺がありません");
        }
        int nt = s[lt][rt];						// 規則による型計算
        if (nt == CType.T_err) {
            pcx.fatalError(mult.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は掛けられません");
        }
        this.setCType(CType.getCType(nt));
        this.setConstant(lc && rc);				// /の左右両方が定数のときだけ定数

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            o.println("\tJSR\tMULT\t ; termMult:サブルーチン呼び出し");
            o.println("\tSUB\t#2, R6 ; termMult:積んであるスタックを捨てる");
            o.println("\tMOV\tR0, (R6)+; termMult:結果をスタックに積む");
        }
    }

}
