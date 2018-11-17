package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class FactorAMP extends CParseRule {
    // factorAMP ::= AMP ( number | primary )

    private CToken amp;
    private CParseRule number;
    public FactorAMP(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AMP;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        amp = tk;
        tk = ct.getCurrentToken(pcx);
        // &の次の字句を読む
        if (Number.isFirst(tk)) {
            number = new Number(pcx);
            number.parse(pcx);
        } else if(Primary.isFirst(tk)){
            number = new Primary(pcx);
            number.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "&の後ろはnumberかprimaryです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (number != null) {
            number.semanticCheck(pcx);
            if(number instanceof Primary){
                if(((Primary) number).getChildClass() instanceof PrimaryMult){
                    pcx.fatalError(amp.toExplainString() + "&*は許されていません");
                }
            }

            //if((number.getCType() == CType.getCType(CType.T_int) || number.getCType() == CType.getCType(CType.T_aint)) && !number.isConstant()){
                setCType(CType.getCType(CType.T_pint));	// アドレス演算子なのでポインタになる
                setConstant(number.isConstant());	// number は常に変数
            //} else {
              //  pcx.fatalError(amp.toExplainString() + "&の後ろはint型変数にしてください");
            //}
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factorAMP starts");
        if (number != null) { number.codeGen(pcx); }
        o.println(";;; factorAMP completes");
    }
}