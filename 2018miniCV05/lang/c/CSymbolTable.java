package lang.c;

import lang.SymbolTable;


public class CSymbolTable {
	private class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {
		@Override
		public CSymbolTableEntry register(String name, CSymbolTableEntry e) { return put(name, e); }
		@Override
		public CSymbolTableEntry search(String name) { return get(name); }
	}
	private OneSymbolTable global = new OneSymbolTable(); // 大域変数用
	private OneSymbolTable local; // 局所変数用

	public void addToTable(String text, CSymbolTableEntry info) {
		global.register(text, info);
    }

	public CSymbolTableEntry checkTable(String text) {
		return global.search(text);
	}

	public void showTable() {
		global.show();
	}
}