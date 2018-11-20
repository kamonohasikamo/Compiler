package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.Tokenizer;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	private int			lineNo, colNo;
	private char		backCh;
	private boolean		backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.setRule(rule);
		lineNo = 1; colNo = 1;
	}

	private CTokenRule						rule;
	public void setRule(CTokenRule rule)	{ this.rule = rule; }
	public CTokenRule getRule()				{ return rule; }

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n')  { colNo = 1; ++lineNo; }
		//System.out.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}
	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') { --lineNo; }
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;
	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}
	// 新しく次のトークンを読み込んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
		if(currentTk.getType() == CToken.TK_NUM ) {
			try {
				if(currentTk.getIntValue() > 0xFFFF) {
					pctx.error(currentTk.toExplainString() + "16bitでは表現できない値です。");
				}
			} catch (NumberFormatException e) {
				pctx.error(currentTk.toExplainString() + " 16bitでは表現できない値です。");
			}
		}
//		System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}
	private CToken readToken() {
		CToken tk = null;
		char ch;
		int  startCol = colNo;
		StringBuffer text = new StringBuffer();

		int state = 0;
		boolean accept = false;
		while (!accept) {
			switch (state) {
			case 0:					// 初期状態
				ch = readChar();
				if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
				} else if (ch == (char) -1) {	// EOF
					startCol = colNo - 1;
					state = 1;
				} else if (ch >= '1' && ch <= '9') {
					startCol = colNo - 1;
					text.append(ch);
					state = 3;
				} else if (ch == '+') {
					startCol = colNo - 1;
					text.append(ch);
					state = 5;
				} else if(ch == '-'){
					startCol = colNo - 1;
					text.append(ch);
					state = 6;
				} else if(ch == '/'){
					startCol = colNo - 1;
					text.append(ch);
					state = 7;
				} else if(ch == '0'){
					startCol = colNo - 1;
					text.append(ch);
					state = 11;
				} else if(ch == '&'){
					startCol = colNo - 1;
					text.append(ch);
					state = 15;
				} else if(ch == '*'){
					startCol = colNo - 1;
					text.append(ch);
					state = 16;
				} else if(ch == '('){
					startCol = colNo - 1;
					text.append(ch);
					state = 18;
				} else if(ch == ')'){
					startCol = colNo - 1;
					text.append(ch);
					state = 19;
				} else if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')){
					startCol = colNo - 1;
					text.append(ch);
					state = 20;
				} else if(ch == '['){
					startCol = colNo - 1;
					text.append(ch);
					state = 21;
				} else if(ch == ']'){
					startCol = colNo - 1;
					text.append(ch);
					state = 22;
				} else if(ch == '='){
					startCol = colNo - 1;
					text.append(ch);
					state = 24;
				} else if(ch == ';'){
					startCol = colNo - 1;
					text.append(ch);
					state = 25;
				} else if(ch == ','){
					startCol = colNo - 1;
					text.append(ch);
					state = 26;
				} else if(ch == '<'){
					startCol = colNo - 1;
					text.append(ch);
					state = 27;
				} else if(ch == '>'){
					startCol = colNo - 1;
					text.append(ch);
					state = 30;
				} else if(ch == '!'){
					startCol = colNo - 1;
					text.append(ch);
					state = 35;
				} else if(ch == '{'){
					startCol = colNo - 1;
					text.append(ch);
					state = 37;
				} else if(ch == '}'){
					startCol = colNo - 1;
					text.append(ch);
					state = 38;
				}else {			// ヘンな文字を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 2;
				}
				break;
			case 1:					// EOFを読んだ
				tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
				accept = true;
				break;
			case 2:					// ヘンな文字を読んだ
				tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 3:					// 数（10進数）の開始
				ch = readChar();
				if (ch >= '0' && ch <= '9') {
					text.append(ch);
				} else {
					backChar(ch);
					state = 4;
				}
				break;
			case 4:					// 数の終わり
				tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 5:					// +を読んだ
				tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
				accept = true;
				break;
			case 6:
				tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
				accept = true;
				break;
			case 7:
				ch = readChar();
				if(ch == '/') {
					text.append(ch);
					state = 8;
				}else if(ch == '*') {
					text.append(ch);
					state = 9;
				}else {
					backChar(ch);
					text.append(ch);
					state = 17;
				}
				break;
			case 8:
				ch = readChar();
				if(ch == '\n') {
					text = new StringBuffer();
					state = 0;
				}else if(ch == (char) -1) {
					state = 1;
				}else {
					text.append(ch);
					state = 8;
				}
				break;
			case 9:
				ch = readChar();
				if(ch == '*') {
					text.append(ch);
					state = 10;
				}else {
					text.append(ch);
					state = 9;
				}
				break;
			case 10:
				ch = readChar();
				if(ch == '/') {
					text = new StringBuffer();
					state = 0;
				}else if(ch == (char) -1){
					state = 2;
				}else {
					text.append(ch);
					state = 9;
				}
				break;
			case 11:
				ch = readChar();
				if(ch == 'x' || ch == 'X') {
					text.append(ch);
					state = 12;
				}else if(ch >= '0' && ch <= '7'){
					text.append(ch);
					state = 13;
				}else {
					backChar(ch);
					state = 4;
				}
				break;
			case 12:
				ch = readChar();
				if((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')){
					text.append(ch); 
					state = 14;
				}else {
					text.append(ch);
					state = 2;
				}
				break;
			case 13:
				ch = readChar();
				if(ch >= '0' && ch <= '7'){
					text.append(ch); 
					state = 13;
				}else {
					backChar(ch);
					state = 4;
				}
				break;
			case 14:
				ch = readChar();
				if((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')){
					text.append(ch); 
					state = 14;
				}else {
					backChar(ch);
					state = 4;
				}
				break;
			case 15:
				tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
				accept = true;
				break;
			case 16:
				tk = new CToken(CToken.TK_MUL, lineNo, startCol, "*");
				accept = true;
				break;
			case 17:
				tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
				accept = true;
				break;
			case 18:
				tk = new CToken(CToken.TK_LP, lineNo, startCol, "(");
				accept = true;
				break;
			case 19:
				tk = new CToken(CToken.TK_RP, lineNo, startCol, ")");
				accept = true;
				break;
			case 20:
				ch = readChar();
				if((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')){
					text.append(ch); 
					state = 20;
				}else {
					backChar(ch);
					state = 23;
				}
				break;
			case 21:
				tk = new CToken(CToken.TK_LB, lineNo, startCol, "[");
				accept = true;
				break;
			case 22:
				tk = new CToken(CToken.TK_RB, lineNo, startCol, "]");
				accept = true;
				break;
			case 23:
				String s = text.toString();
				Integer i = (Integer) rule.get(s);
				tk = new CToken((i == null ? CToken.TK_IDENT : i.intValue()), lineNo, startCol, text.toString());
				accept = true;
				break;
			case 24:
				ch = readChar();
				if(ch == '='){
					text.append(ch); 
					state = 33;
				}else {
					backChar(ch);
					state = 34;
				}
				break;
			case 25:
				tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
				accept = true;
				break;
			case 26:
				tk = new CToken(CToken.TK_COM, lineNo, startCol, ",");
				accept = true;
				break;
			case 27:
				ch = readChar();
				if(ch == '='){
					text.append(ch); 
					state = 28;
				}else {
					backChar(ch);
					state = 29;
				}
				break;
			case 28:
				tk = new CToken(CToken.TK_LE, lineNo, startCol, "<=");
				accept = true;
				break;
			case 29:
				tk = new CToken(CToken.TK_LT, lineNo, startCol, "<");
				accept = true;
				break;
			case 30:
				ch = readChar();
				if(ch == '='){
					text.append(ch); 
					state = 31;
				}else {
					backChar(ch);
					state = 32;
				}
				break;
			case 31:
				tk = new CToken(CToken.TK_GE, lineNo, startCol, ">=");
				accept = true;
				break;
			case 32:
				tk = new CToken(CToken.TK_GT, lineNo, startCol, ">");
				accept = true;
				break;
			case 33:
				tk = new CToken(CToken.TK_EQEQ, lineNo, startCol, "==");
				accept = true;
				break;
			case 34:
				tk = new CToken(CToken.TK_EQ, lineNo, startCol, "=");
				accept = true;
				break;
			case 35:
				ch = readChar();
				if(ch == '='){
					text.append(ch); 
					state = 36;
				}else {
					backChar(ch);
					state = 2;
				}
				break;
			case 36:
				tk = new CToken(CToken.TK_NOTEQ, lineNo, startCol, "!=");
				accept = true;
				break;
			case 37:
				tk = new CToken(CToken.TK_LC, lineNo, startCol, "{");
				accept = true;
				break;
			case 38:
				tk = new CToken(CToken.TK_RC, lineNo, startCol, "}");
				accept = true;
				break;
			}
		}
		return tk;
	}

	public void skipTo(CParseContext pctx, int t) {
		int i = getCurrentToken(pctx).getType();
		while (i != t && i != CToken.TK_EOF) {
			i = getNextToken(pctx).getType();
		}
		pctx.warning(getCurrentToken(pctx).toExplainString() + "まで読み飛ばしました");
	}
	public void skipTo(CParseContext pctx, int t1, int t2) {
		int i = getCurrentToken(pctx).getType();
		while (i != t1 && i != t2 && i != CToken.TK_EOF) {
			i = getNextToken(pctx).getType();
		}
		pctx.warning(getCurrentToken(pctx).toExplainString() + "まで読み飛ばしました");
	}
	public void skipTo(CParseContext pctx, int t1, int t2, int t3, int t4, int t5, int t6) {
		int i = getCurrentToken(pctx).getType();
		while (i != t1 && i != t2 && i != t3 && i != t4 && i != t5 && i != t6 && i != CToken.TK_EOF) {
			i = getNextToken(pctx).getType();
		}
		pctx.warning(getCurrentToken(pctx).toExplainString() + "まで読み飛ばしました");
	}
}
