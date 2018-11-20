package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConstDecl extends CParseRule {
	// constDecl ::= CONST INT constItem { COMMA constItem } SEMI
	
	private CParseRule constitem;
	private ArrayList<CParseRule> list;
	public ConstDecl(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		list = new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_CONST) {
			tk = ct.getNextToken(pcx);
			if(tk.getType() == CToken.TK_INT) {
				tk = ct.getNextToken(pcx);
				if(ConstItem.isFirst(tk)) {
					constitem = new ConstItem(pcx);
					constitem.parse(pcx);
					list.add(constitem);
					tk = ct.getCurrentToken(pcx);
					while(tk.getType() == CToken.TK_COM) {
						tk = ct.getNextToken(pcx);
						if(ConstItem.isFirst(tk)) {
							constitem = new ConstItem(pcx);
							constitem.parse(pcx);
							list.add(constitem);
							tk = ct.getCurrentToken(pcx);
						}else {
							pcx.error(tk.toExplainString() + " constItemが来てません。");
						}
					}
					if(tk.getType() == CToken.TK_SEMI) {
						ct.getNextToken(pcx);
					}else {
						pcx.error(tk.toExplainString() + " ';'が来てません。");
					}
				}else {
					pcx.error(tk.toExplainString() + " constItemが来てません。");
				}
			}else {
				pcx.error(tk.toExplainString() + " intが来てません。");
			}
		}else {
			pcx.error(tk.toExplainString() + " constが来てません。");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}
	

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; constdecl starts");
		for(CParseRule item : list) {
			item.codeGen(pcx);
		}
		o.println(";;; constdecl completes");
	}
}