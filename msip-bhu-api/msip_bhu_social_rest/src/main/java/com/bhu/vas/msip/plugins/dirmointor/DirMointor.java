package com.bhu.vas.msip.plugins.dirmointor;

import com.smartwork.msip.cores.helper.dirmonitor.DirectoryWatcher;
import com.smartwork.msip.cores.helper.dirmonitor.IResourceListener;

public class DirMointor {
	private DirectoryWatcher dw = null;
	private String directoryPath;
	private int intervalSeconds = 5;
	private IResourceListener listener;
	public String getDirectoryPath() {
		return directoryPath;
	}
	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}
	public int getIntervalSeconds() {
		return intervalSeconds;
	}
	public void setIntervalSeconds(int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
	}
	public IResourceListener getListener() {
		return listener;
	}
	public void setListener(IResourceListener listener) {
		this.listener = listener;
	}
	
	public void start(){
		if(dw == null){
			dw = new DirectoryWatcher(this.getDirectoryPath(), this.getIntervalSeconds());
	        dw.addListener(this.getListener());
	        dw.start();
	        System.out.println("_____________________________________________________");
		}
	}
	
	public void stop(){
		dw.stop();
	}
}
