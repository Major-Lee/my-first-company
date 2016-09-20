package com.bhu.vas.msip.plugins.dirmointor;

public enum IndexModuleType {
	MusicIndex("musicindex"),
	PrefixIndex("prefixindex");
	
	String modulename;
	IndexModuleType(String modulename){
		this.modulename = modulename;
	}
	public String getModulename() {
		return modulename;
	}
	public void setModulename(String modulename) {
		this.modulename = modulename;
	}
	
	public static IndexModuleType parserFromPath(String filepath){
		if(filepath.indexOf(MusicIndex.getModulename())>-1){
			return MusicIndex;
		}
		if(filepath.indexOf(PrefixIndex.getModulename())>-1){
			return PrefixIndex;
		}
		return null;
	}
}
