package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Ident extends CParseRule {
	//ident :== IDENT
	private CToken ident;
	private CSymbolTableEntry cste;
	public Ident(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		//System.out.println("identにきてるよ");
		CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        ident = tk;
        cste = pcx.getTable().checkTable(ident.getText());
//        if(cste == null) {
//        	pcx.fatalError(tk.toExplainString() + "この文字は宣言されていません");
//        }

        tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ident != null) {
			this.setCType(CType.getCType(CType.T_int));
			this.setConstant(true);
		}
//		this.setCType(cste.getType());
//		this.setConstant(cste.getConstp());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ident starts");
		if(ident != null) {
			o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
		}
		o.println(";;; ident completes");
	}


}
