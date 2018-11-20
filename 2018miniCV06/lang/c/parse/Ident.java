package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Ident extends CParseRule {
	// ident ::= IDENT
	private CToken ident;
	private CSymbolTableEntry cste;
	public Ident(CParseContext pcx) {
	}
	
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		cste = pcx.getCSymbolTable().getCSymbolTableGlobal().search(ident.getText());
		if(cste == null) {
			pcx.error(ident.toExplainString() + " は宣言されていません");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(cste.getType());
		this.setConstant(cste.isConstp());
	}

	public void codeGen(CParseContext pcx) {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ident starts");
		if (ident != null) {
			o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
					+ ident.toExplainString() + ">");
		}
		o.println(";;; ident completes");
	}
}
