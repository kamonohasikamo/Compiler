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
	// constItem ::= [ MULT ] IDENT ASSIGN [ AMP ] number

	private CToken mul, assign, amp;
	private CToken ident;
	private CType entry;
	private CSymbolTableEntry symbol;
	private CParseRule number;
	private CToken num;
	private int ctype;
	public ConstItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return CToken.TK_MUL == tk.getType() || CToken.TK_IDENT == tk.getType();
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ctype = CType.T_int;
		if(CToken.TK_MUL == tk.getType()){
			mul = tk;
			ctype = CType.T_pint;
			tk = ct.getNextToken(pcx);
		}

		entry = (mul == null) ? CType.getCType(CType.T_int) : CType.getCType(CType.T_pint);

		if(CToken.TK_IDENT != tk.getType()){
			pcx.fatalError(tk.toExplainString() + "定数を宣言してください");
		} else {
			ident = tk;
			tk = ct.getNextToken(pcx);
		}

		if(CToken.TK_EQ != tk.getType()){
			pcx.fatalError(tk.toExplainString() + "値を入れてください");
		} else {
			assign = tk;
			tk = ct.getNextToken(pcx);
		}

		if(CToken.TK_AMP == tk.getType()){
			if(mul == null) {
				pcx.fatalError(tk.toExplainString() + "整数型定数にアドレス値は代入できません");
			}
			amp = tk;
			tk = ct.getNextToken(pcx);
		} else if(mul != null){
			pcx.fatalError(tk.toExplainString() + "ポインタ型定数に整数値は代入できません");
		}

		if(!Number.isFirst(tk)){
			pcx.fatalError(tk.toExplainString() + "数値を代入してください");
		}
		num = tk;
		tk = ct.getNextToken(pcx);

		CSymbolTableEntry cste = new CSymbolTableEntry(CType.getCType(ctype), 1, true, true, 0);
		if(pcx.getCSymbolTable().getCSymbolTableGlobal().register(ident.getText(), cste) != null) {
			pcx.error(tk.toExplainString()+"多重定義があります");
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
