package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ConstItem extends CParseRule {
	// constItem ::= [ MULT ] IDENT ASSIGN [ AMP ] NUMBER
	
	private int ctype;
	private CToken ident;
	private CToken num;
	public ConstItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL || tk.getType() == CToken.TK_IDENT ;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ctype = CType.T_int;
		if(tk.getType() == CToken.TK_MUL) {
			ctype = CType.T_pint;
			tk = ct.getNextToken(pcx);
		}
		if(tk.getType() == CToken.TK_IDENT) {
			ident = tk;
			tk = ct.getNextToken(pcx);
			if(tk.getType() == CToken.TK_EQ) {
				tk = ct.getNextToken(pcx);
				if(tk.getType() == CToken.TK_AMP) {
					tk = ct.getNextToken(pcx);
				}
				if(tk.getType() == CToken.TK_NUM) {
					num = tk;
					tk = ct.getNextToken(pcx);
				}else {
					pcx.error(tk.toExplainString() + " NUMBERが来てません");
				}
			}else {
				pcx.error(tk.toExplainString() + " '='が来てません。");
			}
		}else {
			pcx.error(tk.toExplainString() + " identが来てません。");
		}
		CSymbolTableEntry cste = new CSymbolTableEntry(CType.getCType(ctype), 1, true, true, 0);
		if(pcx.getCSymbolTable().getCSymbolTableGlobal().register(ident.getText(), cste) != null) {
			pcx.error("多重定義があります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}
	

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(ident.getText() + ":\t; constItem:");
		o.println("\t.WORD\t"+ num.getIntValue() +"\t; constItem:");
	}
}