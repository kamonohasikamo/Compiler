package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class UnsignedFactor extends CParseRule {
    // unsignedFactor ::= factorAMP | number | LPAR expression RPAR | addressToValue

    private CParseRule number;
    public UnsignedFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return FactorAMP.isFirst(tk) || Number.isFirst(tk) || tk.getType() == CToken.TK_LPAR || AddressToValue.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(FactorAMP.isFirst(tk)){
            number = new FactorAMP(pcx);
            number.parse(pcx);
        } else if(Number.isFirst(tk)){
            number = new Number(pcx);
            number.parse(pcx);
        } else if(tk.getType() == CToken.TK_LPAR) {
            tk = ct.getNextToken(pcx);
            if(Expression.isFirst(tk)){
                number = new Expression(pcx);
                number.parse(pcx);
                tk = ct.getCurrentToken(pcx);
                if(CToken.TK_RPAR == tk.getType()){
                    tk = ct.getNextToken(pcx);
                } else {
                    pcx.fatalError(tk.toExplainString() + "expressionの後ろは)です");
                }
            } else {
                pcx.fatalError(tk.toExplainString() + "(の後ろはexpressionです");
            }
        } else if(AddressToValue.isFirst(tk)){
            number = new AddressToValue(pcx);
            number.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (number != null) {
            number.semanticCheck(pcx);
            setCType(number.getCType());		// number の型をそのままコピー
            setConstant(number.isConstant());	// number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; unsignedFactor starts");
        if (number != null) { number.codeGen(pcx); }
        o.println(";;; unsignedFactor completes");
    }
}
