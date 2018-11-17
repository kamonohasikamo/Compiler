package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Variable extends CParseRule {
	//variable ::= ident[array]
	private CParseRule ident, array;
	public Variable(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		//System.out.println("variableにきてるよ");
		CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(tk.getType() == CToken.TK_IDENT){
        	ident = new Ident(pcx);
        	ident.parse(pcx);
        	tk = ct.getCurrentToken(pcx);
		    if(tk.getType() == CToken.TK_LBRA){
		    	array = new Array(pcx);
		    	array.parse(pcx);
		    	//tk = ct.getCurrentToken(pcx);
		    }
       	}else{
       		pcx.fatalError(tk.toExplainString() + "variable error");
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {

		/*ident.semanticCheck(pcx);
		setCType(ident.getCType());
		setConstant(ident.isConstant());*/
		if(ident != null) {
			ident.semanticCheck(pcx);
/*			if(ident.getCType().isCType((CType.T_intArray))){
				this.setCType(CType.getCType(CType.T_pint));
				this.setConstant(ident.isConstant());
			}else if(ident.getCType().isCType((CType.T_pintArray))){
				this.setCType(CType.getCType(CType.T_pint));
				this.setConstant(ident.isConstant());
			}else {
*/			this.setCType(ident.getCType());
			this.setConstant(ident.isConstant());
//			}
		}
		if(array != null) {
			array.semanticCheck(pcx);
			if((ident.getCType() == CType.getCType(CType.T_int))) {
				pcx.fatalError("identの識別子はint[]型の必要があります");
			}else if(ident.getCType().isCType((CType.T_intArray))){
				this.setCType(CType.getCType(CType.T_int));
				this.setConstant(ident.isConstant());
			}else if(ident.getCType().isCType(CType.T_pintArray)) {
				this.setCType(CType.getCType(CType.T_pint));
				this.setConstant(array.isConstant());
			}else if(ident.getCType().isCType(CType.T_pint)) {
				pcx.fatalError("配列でない変数ポインタに添え字はかけない");
				//this.setCType(CType.getCType(CType.T_pint));
				//this.setConstant(array.isConstant());
			}
		}else {
			if(ident.getCType() == CType.getCType(CType.T_intArray)) {
				pcx.fatalError("配列には代入できません");
			}else if(ident.getCType() == CType.getCType(CType.T_pintArray)){
				pcx.fatalError("配列には代入できません");
			}
				//this.setCType(array.getCType());
				//this.setConstant(array.isConstant());
		}


			/*ident
			 * intArray -> pint
			 * pintArray -> pint
			 *
			 * array
			 * int intにして
			 * apint->pint.
			 *
			if(this.isConstant()) {
				this.setConstant(false);
			}else {
				pcx.fatalError("variable: 配列自体には代入できない");
			}
			array.semanticCheck(pcx);
			if(ident.getCType() == CType.getCType(CType.T_pintArray)) {
				setCType(CType.getCType(CType.T_pint));
			}else if(ident.getCType() == CType.getCType(CType.T_intArray)){
				setCType(CType.getCType(CType.T_int));
			}else if(ident.getCType() == CType.getCType(CType.T_err)) {
				pcx.fatalError("identの識別子はintである必要があります");
			}else if(ident.getCType() == CType.getCType(CType.T_int)) {
				pcx.fatalError("int型の変数を配列型として扱っています");
			}*/
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		if(ident != null) {
			ident.codeGen(pcx);
		}
		if(array != null) {
			if(ident.getCType().isCType(CType.T_intArray) /*|| ident.getCType().isCType(CType.T_pint)*/) {
				array.codeGen(pcx);
				o.println("\tMOV\t-(R6), R0\t; array: 何番目の配列か");
				o.println("\tMOV\t-(R6), R1\t; array: 番地から値を取り出す");
				o.println("\tADD\t R1, R0\t\t; array: 先頭アドレス+いくつ先か");
				o.println("\tMOV\t R0, (R6)+\t; array: 番地を積み直す");
				//o.println(";;; array completes");
/*			}else if(ident.getCType().isCType(CType.T_pint)) {
				array.codeGen(pcx);
				o.println("\tMOV\t-(R6), R0\t; array: 何番目の配列か");
				o.println("\tMOV\t-(R6), R1\t; array: 番地から値を取り出す");
				o.println("\tMOV\t (R1), R1\t; array: ポインタ先の番地を格納");
				o.println("\tADD\t R1, R0\t\t; array: 先頭アドレス+いくつ先か");
				o.println("\tMOV\t R0, (R6)+\t; array: 番地を積み直す");
	*/		}else {
				array.codeGen(pcx);
				o.println("\tMOV\t-(R6), R0\t; array: 何番目の配列か");
				o.println("\tMOV\t-(R6), R1\t; array: 番地から値を取り出す");
				if(ident.getCType().isCType(CType.T_pint)) {
					o.println("\tMOV\t (R1), R1\t; array: ポインタ先の番地を格納");
					o.println("\tADD\t R1, R0\t; array: 先頭アドレス+いくつ先か");
					o.println("\tMOV\t R0, (R6)+; array: 番地を積み直す");
				}else {
					o.println("\tADD\t (R1), R0\t; array: 先頭アドレス+いくつ先か");
					o.println("\tMOV\t R0, (R6)+\t; array: 番地を積み直す");
				}
					//o.println(";;; array completes");
			}
		}
		o.println(";;; variable completes");
	}

}