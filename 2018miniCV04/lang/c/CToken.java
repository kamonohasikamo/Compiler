package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {
	public static final int TK_PLUS			= 2;				// +
	public static final int TK_MINUS		= 3;
	public static final int TK_MUL			= 4;
	public static final int TK_DIV			= 5;
	public static final int TK_AMP 			= 6;
	public static final int TK_LP			= 7;
	public static final int TK_RP			= 8;
	public static final int TK_LB			= 9;
	public static final int TK_RB			= 10;
	public static final int TK_EQ			= 11;
	public static final int TK_SEMI			= 12;
	public static final int TK_COM			= 13;	
	public static final int TK_INT 			= 14;
	public static final int TK_CONST		= 15;
	public static final int TK_LT			= 16;
	public static final int TK_LE			= 17;
	public static final int TK_GT			= 18;
	public static final int TK_GE			= 19;
	public static final int TK_EQEQ			= 20;
	public static final int TK_NOTEQ		= 21;
	public static final int TK_TRUE			= 22;
	public static final int TK_FALSE		= 23;
	public static final int TK_LC			= 24;
	public static final int TK_RC			= 25;
	public static final int TK_IF			= 26;
	public static final int TK_ELSE			= 27;
	public static final int TK_WHILE		= 28;
	public static final int TK_INPUT		= 29;
	public static final int TK_OUTPUT		= 30;
	
	
	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}