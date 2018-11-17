package lang.c.parse;

// import com.sun.org.apache.xpath.internal.operations.Variable;
import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class PrimaryMult extends CParseRule {
	//primaryMult ::= MULT variable
	private CParseRule variable;
	private CToken mult;
	public PrimaryMult(CParseContext pcx){
	}
	public static boolean isFirst(CToken tk){
		return tk.getType() == CToken.TK_ASTE;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		//System.out.println("primarymultにきてるよ");
		CTokenizer ct = pcx.getTokenizer();
		mult = ct.getCurrentToken(pcx);
		//CToken tk = ct.getNextToken(pcx);
		if(mult.getType() == CToken.TK_ASTE) {
			CToken tk = ct.getNextToken(pcx);
			if(Variable.isFirst(tk)){
				variable = new Variable(pcx);
				variable.parse(pcx);
			}
		}
	}
	/*
	 * !pint *のうしろはアドレス値です
	 */


	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(variable != null) {
			variable.semanticCheck(pcx);
			if(variable.getCType() != CType.getCType(CType.T_pint)) {
				pcx.fatalError("*の後ろはアドレス値です");
				//this.setCType(CType.getCType(CType.T_int));
			}
			if(variable.getCType().isCType(CType.T_pint)) {
				this.setCType(CType.getCType(CType.T_int));
				this.setConstant(variable.isConstant());
				//				this.setCType(CType.getCType(CType.T_intArray));
			}else {
				this.setCType(CType.getCType(CType.T_int));
				this.setConstant(variable.isConstant());

			}
			//this.setConstant(primaryMult.isConstant());
		}
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primaryMult completes");
		if(variable != null) {
			variable.codeGen(pcx);
			o.println("\tMOV\t -(R6), R0\t; PrimaryMult: アドレスを取り出し内容を参照して，積む" + mult.toExplainString() + ">");
			o.println("\tMOV\t (R0), (R6)+\t; PrimaryMult: ");
		}
		o.println(";;; primaryMult completes");


	}

}
