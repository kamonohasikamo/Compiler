package lang.c;

import lang.IOContext;
import lang.ParseContext;

public class CParseContext extends ParseContext {
	public CParseContext(IOContext ioCtx,  CTokenizer tknz) {
		super(ioCtx, tknz);
		setCSymbolTable(new CSymbolTable());
	}

	@Override
	public CTokenizer getTokenizer()		{ return (CTokenizer) super.getTokenizer(); }

	private int seqNo = 0;
	public int getSeqId() { return ++seqNo; }
	
	private CSymbolTable csymboltable;
	public CSymbolTable getCSymbolTable() { return csymboltable; }
	public void setCSymbolTable(CSymbolTable cst) { this.csymboltable = cst; }
}
