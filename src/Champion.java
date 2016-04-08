public class Champion implements Comparable<Champion> {
	public int CHAMPIONID, CHAMPIONPOINTSTONEXTLEVEL, CHAMPIONLEVEL, CHAMPIONPOINTS;
	
	public Champion (int champID, int champPointsToLevel, int champLevel, int champPoints) {
		CHAMPIONID = champID;
		CHAMPIONLEVEL = champLevel;
		CHAMPIONPOINTSTONEXTLEVEL = champPointsToLevel;
		CHAMPIONPOINTS = champPoints;
		
	}
	
	public int compareTo(Champion otherChamp) {
		if (this.CHAMPIONPOINTSTONEXTLEVEL > otherChamp.CHAMPIONPOINTSTONEXTLEVEL)
			return 1;
		else if (this.CHAMPIONPOINTSTONEXTLEVEL < otherChamp.CHAMPIONPOINTSTONEXTLEVEL)
			return -1;
		else
			return 0;
	}

	
}
