package com.bhu.jorion.zoo;

import java.util.Comparator;

public class ZkUrsidsComparator implements Comparator{
	    public final int compare(Object pFirst, Object pSecond) {
	    	ZkUrsids f = (ZkUrsids)pFirst;
	    	ZkUrsids s = (ZkUrsids)pSecond;
	    	
	    	return f.getFreeConnections() - s.getFreeConnections();
	    }
}
