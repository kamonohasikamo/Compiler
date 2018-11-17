package lang.c;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry{
	private CType type;
	private int size;
	private boolean constp;
	private boolean isGlobal;
	private int address;

	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isGlobal, int addr) {
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isGlobal = isGlobal;
		this.address = addr;
	}
	@Override
	public String toExplainString() {
		// TODO Auto-generated method stub
		//return null;
		return "[ type =" + type + ", size = " + size + ", constp = " + constp + ", isGlobal = " + isGlobal + ", address = " + address;
	}

	public CType getType() {
		return type;
	}
	public int getSize() {
		return size;
	}
	public boolean getConstp() {
		return constp;
	}
	public boolean getIsGlobal() {
		return isGlobal;
	}
	public int getAddress() {
		return address;
	}

	public void setType(CType type) {
		this.type = type;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setConstp(boolean constp) {
		this.constp = constp;
	}
	public void setIsGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}
	public void setAddress(int address) {
		this.address = address;
	}


}