package com.bhu.vas.di.op.benchmark;


public class VapHttpApiBenchmarkOp{

	public static void main(String[] args) throws Exception {
		
		/*if(args.length != 4){
			System.out.println(" loss param!");
			return;
		}*/
		/*String mode = args[0];
		//int index = Integer.parseInt(args[1]);
		int thread = Integer.parseInt(args[1]);
		int total = Integer.parseInt(args[2]);*/
		
		for(int i=0;i<VapHttpApiBenchmark.suffix_business_uri.length;i++){
			System.out.println(String.format("Api Url Request[%s]:", VapHttpApiBenchmark.suffix_business_uri[i]));
			//Remote
			VapHttpApiBenchmark benchmark = new VapHttpApiBenchmark(450,10000);
			benchmark.setMode(VapHttpApiBenchmark.ARGUMENT_REMOTE);
			benchmark.setIndex(i);
			benchmark.execute();
			Thread.sleep(10*1000);
			//Local
			for(int j=1;j<5;j++){
				VapHttpApiBenchmark benchmark_local = new VapHttpApiBenchmark(j*200,10000);
				benchmark_local.setMode(VapHttpApiBenchmark.ARGUMENT_LOCAL);
				benchmark_local.setIndex(i);
				benchmark_local.execute();
				Thread.sleep(5*1000);
			}
		}
		
		//System.out.println(String.format("Api Url Request[%s]:", suffix_business_uri[index]));
		
	}
}
