package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementAssign extends CParseRule {
	private CParseRule primary;
	private CParseRule expression;
	public StatementAssign(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	@Override
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(Primary.isFirst(tk)) {
        	primary = new Primary(pcx);
        	primary.parse(pcx);
        	tk = ct.getCurrentToken(pcx);
        	if(tk.getType()==CToken.TK_EQUAL){
            	tk=ct.getNextToken(pcx);
            	if(Expression.isFirst(tk)){
            		expression = new Expression(pcx);
            		expression.parse(pcx);
            		tk=ct.getCurrentToken(pcx);
            		if(tk.getType()!=CToken.TK_SEMI){
            			pcx.fatalError("';'がありません。");
            		}else{
            			ct.getNextToken(pcx);
            		}
            	}else{
            		pcx.fatalError("=の後ろはexpression");
            	}
            }else{
            	pcx.fatalError("'='がありません");
            }
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(primary != null && expression != null){
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
			//System.out.println(primary.getCType());
			//System.out.println(expression.getCType());
			if(primary.getCType() != expression.getCType()){
				pcx.fatalError("(" + primary.getCType() + ")=(" + expression.getCType() + ")は左辺と右辺の型が違います");
			}
			if(primary.isConstant()){
				pcx.fatalError("定数には代入できません");
			}
		}

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementAssign starts");
		if( primary != null && expression !=null){
			primary.codeGen(pcx);
			expression.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; StatementAssign:右辺を取り出す");
			o.println("\tMOV\t-(R6), R1\t; StatementAssign:左辺を取り出す");
			o.println("\tMOV\tR0, (R1)\t; StatementAssign:アドレス値に代入");
		}
		o.println(";;; statementAssign completes");
	}

}
