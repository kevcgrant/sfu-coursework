
public class PlayerTypes {
	// Define all the player types that we current support
	public static String[] playerTypeNames = {"Human", "Random", "Smarter", "Learns"};

    /**
     * This method creates returns a new Player object instance
     * based on the string playerTypeStr which specifies the
     * sub-class to create. 
     * 
     * To implement new player types, create a new child class
     * which extends Player and add a case in the select statement
     * below which calls the new child class constructor for
     * a new player of this type which matches playerTypeStr String.
     * 
     * @param playerTypeStr - Specifies the type of Player object to create
     * @return Player - returns the newly instantiated player object instance
     * 
     */
    public static Player createPlayer(String playerTypeStr) {
       Player  playerObj  = null;
       int     playerType = 0;
       boolean foundMatch = false;
       
	   for(int i=0; i<PlayerTypes.playerTypeNames.length; i++) {
	       if (playerTypeStr.equalsIgnoreCase(PlayerTypes.playerTypeNames[i]))
	       {
	    	   playerType  = i;
	    	   foundMatch  = true;
	       }
       }
	   
	   if (!foundMatch) {
		   System.out.print  ("Unsupported player type specified: " + playerTypeStr);
	       System.out.println(", defaulting to player type: " + PlayerTypes.playerTypeNames[playerType]);
	   }

	   switch(playerType) {
	       default:
	   	   case 0: 
		   playerObj = new SmarterPlayer ();
		   break;
		   
	       case 1: 
		   playerObj = new RandomPlayer();
		   break;
		   
	       case 2:
	       playerObj = new HumanPlayer();
	       break;
	       
	   }
	   
	   return playerObj;
    }

}
