SmartsObject.list()*.remove();
def perfFigureFile = new File("perfTestFigures.txt");
perfFigureFile.delete();
def mb = Math.pow(2, 20);
perfFigureFile.append("TotalTime,Search_10%_Time,Search_10%_With_Field_Name_Time,Search_5%_Time,Search_10Specific_Results_Time,Search_With_No_Results_Time,Search_Resulting_All_Time,Search_With_AND_Operation_Time,TotalMemory,UsedMemory\n")
for(j in 0..10000) {

	def current = System.currentTimeMillis();
	for(i in 0..89){
		Device.add(name:"device" + (100 * j + i).toString(), creationClassName:"Device", smartsDs:"smartsDs", ipAddress:"342354"  + (100* j + i).toString(),
				location:"pARIS", model:"CISCO", snmpReadCommunity:"communtiy", vendor:"vendor" );

	}
	if (j == 0) {
		for(i in 0..9){
			Device.add(name:"device" + (100 * j + i + 90).toString(), creationClassName:"Device", smartsDs:"smartsDs", ipAddress:"342354"  + (100 * j + i + 90).toString(),
					location:"pARIS", model:"CISCO1", snmpReadCommunity:"communtiy", vendor:"vdor2" );

		}
	}
	else {
		for(i in 0..9){
            if (i < 5) {
                Device.add(name:"device" + (100 * j + i + 90).toString(), creationClassName:"Device", smartsDs:"smartsDs", ipAddress:"342354"  + (100 * j + i + 90).toString(),
                    location:"pARIS", model:"CISCO1", snmpReadCommunity:"communty", vendor:"vdor1" );

            }
            else {
			Device.add(name:"device" + (100 * j + i + 90).toString(), creationClassName:"Device", smartsDs:"smartsDs", ipAddress:"342354"  + (100 * j + i + 90).toString(),
					location:"pARIS", model:"CISCO1", snmpReadCommunity:"communtiy2", vendor:"vdor1" );

            }
		}
	}

	def current2  = System.currentTimeMillis()
	def additionTimeOf100 = current2 - current;

	100.downto(0) {
		def searchResult = Device.search("communtiy2")

	}
	def search1 = System.currentTimeMillis() - current2;
	def current3  = System.currentTimeMillis()

	100.downto(0) {
		def searchResult = Device.search("vdor2")

	}
	def search2 = System.currentTimeMillis() - current3;
	def current4  = System.currentTimeMillis()

	100.downto(0) {
		def searchResult = Device.search("vdor2")
	}
	def search3 = System.currentTimeMillis() - current4;
	def current5  = System.currentTimeMillis()

	100.downto(0) {
		def searchResult = Device.search("CISCO1")

	}
	def search4 = System.currentTimeMillis() - current5;
	def current6  = System.currentTimeMillis()

	100.downto(0) {
		def searchResult = Device.search("model:CISCO1")

	}
	def search5 = System.currentTimeMillis() - current6;
	def current7  = System.currentTimeMillis()

	100.downto(0) {
		def searchResult = Device.search("ibrahim")

	}
	def search6 = System.currentTimeMillis() - current7;
	def current8  = System.currentTimeMillis()

	100.downto(0) {
		def searchResult = Device.search("pARIS")

	}
	def search7 = System.currentTimeMillis() - current8;

	def current9  = System.currentTimeMillis()

	100.downto(0) {
		def searchResult = Device.search("model:CISCO1 AND snmpReadCommunity:communtiy2")
		
	}
	def search8 = System.currentTimeMillis() - current9;

	search1 /= 100;
	search2 /= 100;
	search3 /= 100;
	search4 /= 100;
	search5 /= 100;
	search6 /= 100;
	search7 /= 100;
	search8 /= 100;

    def totalMem = Runtime.getRuntime().totalMemory() / mb;
    def freeMem = Runtime.getRuntime().freeMemory() / mb;
    def usedMem = totalMem - freeMem;
	def response = "${additionTimeOf100},${search4},${search5},${search1},${search3},${search6},${search7},${search8},${totalMem},${usedMem}\n";
	perfFigureFile.append(response)
}

return "done"