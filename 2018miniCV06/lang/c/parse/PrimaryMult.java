package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class PrimaryMult extends CParseRule {
	// primaryMult ::= MUL variable
	private CToken op;
	private CParseRule child;
	public PrimaryMult(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		op = tk;
		tk = ct.getNextToken(pcx);
		if(Variable.isFirst(tk)) {
			child = new Variable(pcx);
			child.parse(pcx);
		}else {
			pcx.error(tk.toExplainString() + " '*'の次にvariableがありません");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (child != null) {
			child.semanticCheck(pcx);
			if(child.getCType().getType() == CType.T_apint) {
				setCType(CType.getCType(CType.T_pint));	
			}else {
				if(child.getCType().getType() == CType.T_pint) {
					setCType(CType.getCType(CType.T_int));	
				}else if(child.getCType().getType() == CType.T_aint){
					setCType(CType.getCType(CType.T_aint));
				}else{
					pcx.fatalError("*をint型変数につけないでください。");
				}
			}	
			setConstant(child.isConstant());
		}
		
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (child != null) {
			child.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; PrimaryMult: アドレスを取り出して、内容を参照して、積む<"
					+ op.toExplainString() + ">");
			o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
		}
	}
}