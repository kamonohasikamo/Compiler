package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class UnsignedFactor extends CParseRule {
	// unsignedfactor ::= factorAMP || number || LP expression RP
	private CParseRule number;
	private CParseRule factorAmp;
	private CParseRule a2v;
	private CParseRule expression;
	public UnsignedFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) || FactorAmp.isFirst(tk) || tk.getType() == CToken.TK_LP || AddressToValue.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(FactorAmp.isFirst(tk)) {
			factorAmp = new FactorAmp(pcx);
			factorAmp.parse(pcx);
		}else if(Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
		}else if(tk.getType() == CToken.TK_LP) {
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)){
				expression = new Expression(pcx);
				expression.parse(pcx);
			}else {
				pcx.error(tk.toExplainString() + " '('の次にexpressionがありません。");
			}
			tk = ct.getNextToken(pcx);
			if(tk.getType() == CToken.TK_RP){
				ct.getNextToken(pcx);
			}else{
				pcx.error(tk.toExplainString() + " '(' expression の次に ')' がありません。");
			}
		}else if(AddressToValue.isFirst(tk)) {
			a2v = new AddressToValue(pcx);
			a2v.parse(pcx);
			
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			setCType(number.getCType());		// number の型をそのままコピー
			setConstant(number.isConstant());	// number は常に定数
		}
		if(factorAmp != null) {
			factorAmp.semanticCheck(pcx);
			setCType(factorAmp.getCType());		
			setConstant(factorAmp.isConstant());
		}
		if(expression != null){
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
		if(a2v != null) {
			a2v.semanticCheck(pcx);
			setCType(a2v.getCType());
			setConstant(a2v.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsignedFactor starts");
		if (number != null) { number.codeGen(pcx); }
		if (factorAmp != null) { factorAmp.codeGen(pcx); }
		if (expression != null){ expression.codeGen(pcx); }
		if (a2v != null) { a2v.codeGen(pcx);}
		o.println(";;; unsignedFactor completes");
	}
}