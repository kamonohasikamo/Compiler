package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class PlusFactor extends CParseRule {
    // plusFactor ::= PLUS unsignedFactor

    private CToken plus;
    private CParseRule factor;
    public PlusFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_PLUS;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        plus = ct.getCurrentToken(pcx);
        // +の次の字句を読む
        CToken tk = ct.getNextToken(pcx);
        if (Term.isFirst(tk)) {
            factor = new UnsignedFactor(pcx);
            factor.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "+の後ろはunsignedFactorです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (factor != null) {
            factor.semanticCheck(pcx);
            if(factor.getCType().getType() == CType.T_int){
                this.setCType(factor.getCType());		// factor の型をそのままコピー
                this.setConstant(factor.isConstant());
            } else {
                pcx.fatalError(plus.toExplainString() + "式には演算型が必要です");
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; plusFactor starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; plusFactor completes");
    }
}
