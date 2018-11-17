package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp|number|LPAR expresstion RPAR|addressToValue
	private CParseRule factor, expression, addresstovalue;
	public UnsignedFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) || FactorAmp.isFirst(tk) || tk.getType() == CToken.TK_LPAR || addressToValue.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		//System.out.println("unsignedfactor");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if( FactorAmp.isFirst(tk) ){
			factor = new FactorAmp(pcx);
			factor.parse(pcx);
		}else if(tk.getType() == CToken.TK_LPAR){
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)){
				expression = new Expression(pcx);
				expression.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() == CToken.TK_RPAR){
					tk = ct.getNextToken(pcx);
				}else{
					pcx.warning(" ) がない");
				}
			}else{
				pcx.fatalError("(の後ろはterm");
			}
		}else if(Number.isFirst(tk)){
			factor = new Number(pcx);
			factor.parse(pcx);
		} else if(addressToValue.isFirst(tk)) {
			addresstovalue = new addressToValue(pcx);
			addresstovalue.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {

		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType());		// factor の型をそのままコピー
			setConstant(factor.isConstant());	// factor は常に定数
		}
		if(expression != null){
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
		if(addresstovalue != null){
			addresstovalue.semanticCheck(pcx);
			setCType(addresstovalue.getCType());
			setConstant(addresstovalue.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsignedfactor starts");
		if (factor != null) {	factor.codeGen(pcx);}
		if (expression != null) {expression.codeGen(pcx);}
		if(addresstovalue != null) {addresstovalue.codeGen(pcx);}
		o.println(";;; unsignedfactor completes");
	}
}