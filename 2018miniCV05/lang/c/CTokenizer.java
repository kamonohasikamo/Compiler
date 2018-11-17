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
//		System.out.print("'"+ch+"'("+(int)ch+")");
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
//		System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}
	private CToken readToken() {
		CToken tk = null;
		char ch;
		int  startCol = colNo;
		StringBuffer text = new StringBuffer();

		//text.append(ch) : chをtextに追加する

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
				}else if(ch == '0') {
					startCol = colNo - 1;
					text.append(ch);
					state = 13;
				}else if (ch == '+') {
					startCol = colNo - 1;
					text.append(ch);
					state = 5;
				} else if(ch == '-') {
					startCol = colNo - 1;
					text.append(ch);
					state = 6;
				}else if(ch == '*') {
					startCol = colNo - 1;
					text.append(ch);
					state = 17;
				}else if (ch == '/') {
					startCol = colNo - 1;
					state = 7;
				} else if(ch== '&') {
					startCol = colNo - 1;
					text.append(ch);
					state = 12;
				} else if(ch == '(') {
					startCol = colNo - 1;
					state = 18;
				} else if(ch == ')') {
					startCol = colNo - 1;
					state = 19;
				} else if(ch == '[') {
					startCol = colNo - 1;
					state = 20;
				} else if(ch == ']') {
					startCol = colNo - 1;
					state = 21;
				} else if(Character.isLetter(ch)) {
					startCol = colNo - 1;
					text.append(ch);
					state = 22;
				} else if (ch == '=') {
					startCol = colNo - 1;
					text.append(ch);
					state = 24;
				} else if(ch == ';') {
					startCol = colNo - 1;
					text.append(ch);
					state = 25;
				}else if(ch == ','){
					startCol = colNo - 1;
					text.append(ch);
					state = 26;
				} else {			// ヘンな文字を読んだ
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
				if(Integer.decode(text.toString()) > 65535) {
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString() + "(範囲外の数値)");
					state = 2;
				}
				accept = true;
				break;
			case 5:					// +を読んだ
				tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
				accept = true;
				break;
			case 6:					// -を読んだ
				tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
				accept = true;
				break;
			case 7:					//コメント
				ch = readChar();
				if(ch == '/') {
					state = 8;
				} else if (ch == '*'){
					state = 9;
				}else {
					tk = new CToken(CToken.TK_SLASH, lineNo, startCol, "/");
					accept = true;
					backChar(ch);
				}
				break;
			case 8:					// / を読んだ
				ch = readChar();
				if(ch == '\n') {
					state = 0;
				}else if(ch == (char) - 1) {
					state = 11;
				}else { //all comment
					state = 8;
				}
				break;
			case 9:					// * を読んだ
				ch = readChar();
				if(ch == '*') {
					state = 10;
				} else if(ch == (char) - 1) {
					state = 11;
				}else {
					state = 9;
				}
				break;
			case 10:
				ch = readChar();
				if(ch == '*') {
					state = 10;
				}else if(ch == '/') {
					state = 0;
				}else if(ch == (char) - 1) {
					state = 11;
				}else {
					state = 9;
				}
				break;
			case 11:
				tk = new CToken(CToken.TK_ILL, lineNo, startCol, "error");
				accept = true;
				break;
			case 12:
				tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
				accept = true;
				break;
			case 13: //0 or oct or hex
				ch = readChar();
				if(ch == 'x') {
					text.append(ch);
					state = 14;
				}else if(ch >= '0' && ch <= '7') {
					text.append(ch);
					state = 15;
				}else if(ch == (char) - 1) {
					backChar(ch);
					state = 1;
				}else {
					backChar(ch);
					state = 4;
				}
				break;
			case 14:
				ch = readChar();
				if((ch >= '0' && ch <= '9') || (ch >='A' && ch <= 'F') || (ch >= 'a' && ch <= 'f')) {
					text.append(ch);
					state = 16;
				}else if(ch == (char) - 1) {
					backChar(ch);
					state = 1;
				}else {
//					backChar(ch);
					state = 2;
				}
				break;
			case 15:
				ch = readChar();
				if(ch >= '0' && ch <= '7') {
					text.append(ch);
					state = 15;
				}else if(ch == (char) - 1) {
					backChar(ch);
					state = 1;
				}else{
					backChar(ch);
					state = 4;
				}
				break;
			case 16:
				ch = readChar();
				if((ch >= '0' && ch <= '9') || (ch >='A' && ch <= 'F') || (ch >= 'a' && ch <= 'f')) {
					text.append(ch);
					state = 16;
				}else if(ch == (char) - 1) {
					backChar(ch);
					state = 1;
				}else {
					backChar(ch);
					state = 4;
				}
				break;
			case 17:
				tk = new CToken(CToken.TK_ASTE, lineNo, startCol, "*");
				accept = true;
				break;
			case 18:
				tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
				accept = true;
				break;
			case 19:
				tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
				accept = true;
				break;
			case 20:
				tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
				accept = true;
				break;
			case 21:
				tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
				accept = true;
				break;
			case 22:
				ch = readChar();
				if(Character.isLetterOrDigit(ch)) {
					text.append(ch);
				}else {
					backChar(ch);
					state = 23;
				}
				break;
			case 23:	//単なる識別子かキーワードかのチェック
				//tk = new CToken(CToken.TK_IDENT, lineNo, startCol, text.toString());
				String s = text.toString();
				Integer i = (Integer) rule.get(s);
				// 切り出した軸が登録済みキーワードかどうかはiがnullかどうかで判定
				tk = new CToken(((i == null) ? CToken.TK_IDENT : i.intValue()), lineNo, startCol, s);
				accept = true;
				break;
			case 24:	// =を読んだ
				tk = new CToken(CToken.TK_EQUAL, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 25:	// ;を読んだ
				tk = new CToken(CToken.TK_SEMI, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 26:// ,を読んだ
				tk = new CToken(CToken.TK_COMMA, lineNo, startCol, text.toString());
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
