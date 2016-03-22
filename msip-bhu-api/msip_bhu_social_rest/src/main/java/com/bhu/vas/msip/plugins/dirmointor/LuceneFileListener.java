package com.bhu.vas.msip.plugins.dirmointor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.cores.helper.dirmonitor.BaseListener;
import com.smartwork.msip.cores.helper.dirmonitor.IFileListener;

public class LuceneFileListener extends BaseListener implements IFileListener {
	
	private Map<String,IndexModuleDTO> indexModulesUpdates = new HashMap<String,IndexModuleDTO>();

	private int intervalSeconds = 5;
	/**
     * Connstructor
     */
    public LuceneFileListener() {
        super();
    }

    public void onStart(Object monitoredResource) {
        // On startup
        if (monitoredResource instanceof File) {
            File resource = (File) monitoredResource;
            if (resource.isDirectory()) {
                System.out.println("Start to monitor " + resource.getAbsolutePath());
                /*File[] files = resource.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = (File) files[i];
                    onAdd(f);
                }*/
            }
        }
    }

    public void onStop(Object notMonitoredResource) {

    }

    public void onAdd(Object newResource) {
        if (newResource instanceof File) {
            File file = (File) newResource;
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath() + " is added");
                indexModuleReaderAction(file.getAbsolutePath());
            }
        }
    }

    public void onChange(Object changedResource) {
        if (changedResource instanceof File) {
            File file = (File) changedResource;
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath() + " is changed");
                indexModuleReaderAction(file.getAbsolutePath());
            }
        }
    }

    public void onDelete(Object deletedResource) {
        if (deletedResource instanceof String) {
            String deletedFile = (String) deletedResource;
            System.out.println(deletedFile + " is deleted");
            indexModuleReaderAction(deletedFile);
        }
    }
    
    
    public int getIntervalSeconds() {
		return intervalSeconds;
	}

	public void setIntervalSeconds(int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
	}

	private void indexModuleReaderAction(String filepath){
    	IndexModuleType type =  IndexModuleType.parserFromPath(filepath);
    	if(type == null) return;
    	IndexModuleDTO lastDto = indexModulesUpdates.get(type.getModulename());
    	long t0 = System.currentTimeMillis();
    	boolean reopen = false;
    	if(lastDto != null){
    		reopen = false;
    		System.out.println(type.getModulename()+" leaved "+(t0-lastDto.getLastUpdated_at()));
        	if((t0-lastDto.getLastUpdated_at()) > (this.getIntervalSeconds()*1000)){
        		reopen = true;
        		lastDto.setLastUpdated_at(t0);
        		lastDto.setLastAlerted_at(t0);
        		System.out.println(type.getModulename()+" now reopen!");
        	}else{
        		reopen = false;
        		lastDto.setLastAlerted_at(t0);
        		System.out.println(type.getModulename()+" waiting reopen!");
        	}
    	}else{
    		reopen = true;
    		lastDto = new IndexModuleDTO();
    		lastDto.setLastUpdated_at(t0);
    		lastDto.setLastAlerted_at(t0);
    		System.out.println(type.getModulename()+" now reopen!");
    	}
    	indexModulesUpdates.put(type.getModulename(), lastDto);
    	if(reopen){
	    	switch(type){
	    		case MusicIndex:
	    			break;
	    		case PrefixIndex:
	    			break;
	    	}
    	}
    }
}

