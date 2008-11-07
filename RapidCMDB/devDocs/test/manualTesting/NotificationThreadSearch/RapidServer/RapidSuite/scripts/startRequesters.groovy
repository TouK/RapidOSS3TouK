println "startRequesters START";
20.times{
	def requester = new RequesterThread();	
	requester.setName("Searcher "+it);
	requester.start();

}
println "startRequesters STOP";