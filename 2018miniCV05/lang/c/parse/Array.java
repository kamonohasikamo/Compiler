package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Array extends CParseRule {
	// array ::= LBRA expresstion RBRA
	private CParseRule expression;
	//private CToken lBra, rBra;
	public Array(CParseContext pcx) {
		//this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBRA;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		//System.out.println("arrayにきてるよ");
		CTokenizer ct = pcx.getTokenizer();
		//lBra = ct.getCurrentToken(pcx);
		CToken tk = ct.getCurrentToken(pcx);
		//tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_LBRA) {
			tk = ct.getNextToken(pcx);
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() != CToken.TK_RBRA) {
				pcx.fatalError("添字のカッコが閉じられていません");
			}
			tk = ct.getNextToken(pcx);
		}

		/*if(Ident.isFirst(tk)){
			ident = new Expression(pcx);
			ident.parse(pcx);
			//rBra = ct.getCurrentToken(pcx);
			tk = ct.getCurrentToken(pcx);
			if(rBra.getType() != CToken.TK_RBRA) {
				pcx.fatalError(rBra.toExplainString() + "添字のカッコが閉じられていません");
			}
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_LBRA) {
				pcx.fatalError(lBra.toExplainString() + "配列は1次元までです");
			}
		}else{
			pcx.fatalError(lBra.toExplainString() + "[ の後ろはexpressionです");
		}

		tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_RBRA){
			tk = ct.getNextToken(pcx);
		}else{
				pcx.fatalError(tk.toExplainString() + "の前に]がありません");
		}
		tk = ct.getNextToken(pcx);
*/
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(expression != null) {
			expression.semanticCheck(pcx);
			if(expression.getCType() != CType.getCType(CType.T_int)) {
				pcx.fatalError("[]の中はint型です");
			}else {
				this.setCType(expression.getCType());
				this.setConstant(expression.isConstant());
				/*}else if(ident.getCType() != CType.getCType(CType.T_intArray) && ident.getCType() != CType.getCType(CType.T_pint)) {
				pcx.fatalError(lBra.toExplainString() + "配列名の識別子はint[]かint*型です");
			}*/
			/*
			setCType(expression.getCType());
			setConstant(expression.isConstant());
			if(expression.getCType() != CType.getCType(CType.T_int)) {
				pcx.fatalError("配列の[]内はint型");
			}*/
			}

		}
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; array starts");
		if(expression != null) {
			expression.codeGen(pcx);
			/*
			o.println("\tMOV\t-(R6), R0\t; Array: 添え字を取り出し、変数番地に加えてスタックに積む");
			o.println("\tMOV\t-(R6), R1\t; Array: 変数番地の取り出し ");
			o.println("\tADD\tR1, R0\t; Array: ");
			o.println("\tMOV\tR0, (R6)+\t; Array: ");*/
		}
		o.println(";;; array completes");

	}

}