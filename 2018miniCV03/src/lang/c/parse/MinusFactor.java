package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class MinusFactor extends CParseRule {
    // minusFactor ::= Minus unsignedFactor

    private CToken minus;
    private CParseRule factor;
    public MinusFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MINUS;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        minus = ct.getCurrentToken(pcx);
        // -の次の字句を読む
        CToken tk = ct.getNextToken(pcx);
        if (Term.isFirst(tk)) {
            factor = new UnsignedFactor(pcx);
            factor.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "-の後ろはunsignedFactorです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (factor != null) {
            factor.semanticCheck(pcx);
            if(factor.getCType().getType() == 1){
                setCType(factor.getCType());		// 型はfactorと同じ
                setConstant(factor.isConstant());	// factorは常に定数
            }else {
                pcx.fatalError(minus.toExplainString() + "番地の前に-はつけません");
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; minusFactor starts");
        if (factor != null) {
            factor.codeGen(pcx);
            o.println("\tMOV\t#0, (R6)+\t; 符号を反転");  //符号の反転を行うコード
            o.println("\tMOV\t-(R6), R0\t; 符号を反転");
            o.println("\tSUB\t-(R6), R0\t; 符号を反転");
            o.println("\tMOV\tR0, (R6)+\t; 符号を反転");
        }
        o.println(";;; minusFactor completes");
    }
}
