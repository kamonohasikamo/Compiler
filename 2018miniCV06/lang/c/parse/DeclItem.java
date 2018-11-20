package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class DeclItem extends CParseRule {
	// declItem ::= [ MULT ] IDENT [ LBRA NUMBER RBRA ]
	
	//private CParseRule mul;
	private CToken ident;
	private CToken num;
	private int ctype;
	
	public DeclItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL || tk.getType() == CToken.TK_IDENT ;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		CSymbolTableEntry cste;
		ctype = CType.T_int;
		if(tk.getType() == CToken.TK_MUL) {
			ctype = CType.T_pint;
			tk = ct.getNextToken(pcx);
		}
		if(tk.getType() == CToken.TK_IDENT) {
			ident = tk;
			tk = ct.getNextToken(pcx);
			if(tk.getType() == CToken.TK_LB) {
				ctype = (ctype == CType.T_int) ? CType.T_aint : CType.T_apint;
				tk = ct.getNextToken(pcx);
				if(tk.getType() == CToken.TK_NUM) {
					num = tk;
					tk = ct.getNextToken(pcx);
					if(tk.getType() == CToken.TK_RB) {
						tk = ct.getNextToken(pcx);
					}else {
						pcx.error(tk.toExplainString() + " ']'が来てません");
					}
				}else {
					pcx.error(tk.toExplainString() + " Numberが来てません。");
				}
			}
		}else {
			pcx.error(tk.toExplainString() + " identが来てません。");
		}
		cste = new CSymbolTableEntry(CType.getCType(ctype),
					((num != null) ? num.getIntValue() : 1), false, true, 0);
		if(pcx.getCSymbolTable().getCSymbolTableGlobal().register(ident.getText(), cste) != null) {
			pcx.error(tk.toExplainString() +"多重定義があります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}
	

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(ident.getText() + ":\t; declItem:");
		if(ctype == CType.T_int || ctype == CType.T_pint) {
			o.println("\t.WORD\t0\t; declItem:");
		}else {
			o.println("\t.BLKW\t" + num.getIntValue() +"\t; declItem:");
		}
	}
}