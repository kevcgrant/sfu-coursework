/**
 * CMPT 471 Assignment 3, Part 2
 * <p>
 * Simulates the operations of RIP (Routing Information Protocol) on routers R1, ..., R7.
 * Omits the "triggered update" portion of RIP.

 * @author Kevin Grant - 301192898
 *
 */
public class RIPSimulator {

	private static final String DIVIDER = "======================================================";
	
	private static final int NUMBER_OF_ROUTERS = 7;
	private static final int NUMBER_OF_NETWORKS = 8;
	private static final int NETWORKS_PER_ROUTER = 2;
	
	/**
	 * The global array of routers
	 */
	private Router[] routers = new Router[NUMBER_OF_ROUTERS];
	
	/**
	 * The global array of networks.
	 */
	private Network[] networks = 
		{ 
			new Network(0), new Network(1), new Network(2), new Network(3),
			new Network(4), new Network(5), new Network(6), new Network(7)
		};  
	
	/**
	 * Flag specifying whether or not updates were performed after the round of RIP advertisements.
	 */
	private boolean didPerformUpdate;

	public RIPSimulator() {
		initializeRouters();
	}
	
	private void initializeRouters() {
		routers[0] = new Router(0);
		routers[1] = new Router(1);
		routers[2] = new Router(2);
		routers[3] = new Router(3);
		routers[4] = new Router(4);
		routers[5] = new Router(5);
		routers[6] = new Router(6);
	}
	
	/**
	 * Begin the RIP operations. Triggered updates are omitted from the advertisements.
	 * Advertisements are sent sequentially in order R1, R2, ..., R8. The rounds continue
	 * until the updates are complete.
	 */
	public void startRipSequence() {
		int round = 0;
		do {
			printDividerWithMessage("ROUND " + round);
			printTables();
			sendAdvertisements();
			round++;
		} while (didPerformUpdate);
	}
	
	/**
	 * Print the routing tables for all routers.
	 */
	private void printTables() {
		for (Router r : routers) {
			r.printTable();
		}
	}
	
	/**
	 * Sends an advertisement for each router in order R1, R2, ..., R7.
	 * Sets the didUpdate flag to false before advertising.
	 */
	private void sendAdvertisements() {
		didPerformUpdate = false;
		for (Router r : routers) {
			r.advertiseRoutingTable();
		}
	}
	
	private void printDividerWithMessage(String message) {
		System.out.println("=== " + message + " " + DIVIDER.substring(0, DIVIDER.length() - message.length()));
		System.out.println("");
	}
	
	public static void main(String[] args) {
		RIPSimulator sim = new RIPSimulator();
		sim.startRipSequence();
	}
	
	/**
	 * Networks that routers are connected to.
	 */
	public class Network {
		
		private int index;
		
		public Network(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
		/**
		 * Return the indexes of the routers in the global routers array that are connected
		 * to this network.
		 */
		public int[] getConnectedRouterIndexes() {
			if (index == networks.length - 1) {
				return new int[] {index - 1};
			} else if (index == 0) {
				return new int[] {index};
			} else {
				return new int[] {index - 1, index};
			}
		}
		
		/**
		 * Represents network with index 0 as "N1", index 1 as "N2", and so on.
		 */
		@Override
		public String toString() {
			return "N" + (index + 1);
		}
	}
	
	/**
	 * Routers in the network.
	 */
	public class Router {
		
		private Network[] connectedNetworks;
		private RoutingTable routingTable;
		private int index;
		
		/**
		 * Initializes the router with the networks it is connected to, and populates
		 * its routing table with initial values.
		 */
		public Router(int index) {
			this.index = index;
			connectedNetworks = new Network[NETWORKS_PER_ROUTER];
			connectedNetworks[0] = networks[index];
			connectedNetworks[1] = networks[index + 1];
			initializeRoutingTable();
		}
		
		/**
		 * Initialize the routing table with an entry for each network directly connected
		 * to the router. The distance to each network is set as 1 hop.
		 */
		private void initializeRoutingTable() {
			routingTable = new RoutingTable();
			for (Network network : connectedNetworks) {
				TableEntry entry = new TableEntry();
				entry.setDestinationNetwork(network);
				entry.setNumberOfHops(1);
				routingTable.setEntry(network.getIndex(), entry);
			}
		}
		
		/**
		 * Advertise the routing table to all directly connected networks, sending the
		 * routing table to all neighbouring routers.
		 */
		public void advertiseRoutingTable() {
			for (Network n : connectedNetworks) {
				for (int routerIndex : n.getConnectedRouterIndexes()) {
					maybeSendAdvertisement(routerIndex);
				}
			}
		}
		
		/**
		 * Send the advertisement if the router specified is not the current router.
		 */
		public void maybeSendAdvertisement(int routerIndex) {
			if (!routers[routerIndex].equals(this)) {
				routers[routerIndex].receiveAdvertisement(routingTable, this.index);
			}
		}
		
		/**
		 * Update the router's routing table if necessary. If updates are made, then an advertisement
		 * is triggered.
		 */
		private void receiveAdvertisement(RoutingTable routingTable, int routerIndex) {
			for (int i = 0; i < NUMBER_OF_NETWORKS; i++) {
				TableEntry neighbourEntry = routingTable.getEntry(i);
				TableEntry currentEntry = this.routingTable.getEntry(i);
				if (neighbourEntry != null) {
					if (shouldUpdateTableEntry(currentEntry, neighbourEntry, routerIndex)) {
						updateTableEntry(i, neighbourEntry, routerIndex);
						didPerformUpdate = true;
					}
				}
			}
		}
		
		/**
		 * Determines whether or not it is necessary to update the routing table for the given entry.
		 * @param currentEntry		The entry in this router's table
		 * @param neighbourEntry	The entry in the table received via advertisement
		 * @param routerIndex		The index of the router the advertisement was received from
		 * @return true if an update is required, false otherwise
		 */
		private boolean shouldUpdateTableEntry(TableEntry currentEntry, TableEntry neighbourEntry, int routerIndex) {
			return 
					// There is no entry in the routing table for the network
					(currentEntry == null) || 
					
					// There is a shorter route available
					(neighbourEntry.getNumberOfHops() + 1 < currentEntry.getNumberOfHops()) ||
					
					// The number of hops needs to be updated
					(currentEntry.getNextHopRouter() != null && 
						currentEntry.getNextHopRouter().equals(routers[routerIndex]) && 
						neighbourEntry.getNumberOfHops() != currentEntry.getNumberOfHops() - 1);
		}
		
		/**
		 * Updates the routing table for the given entry.
		 */
		private void updateTableEntry(int networkIndex, TableEntry neighbourEntry, int routerIndex) {
			TableEntry newEntry = new TableEntry();
			newEntry.setDestinationNetwork(neighbourEntry.getDestinationNetwork());
			newEntry.setNextHopRouter(routers[routerIndex]);
			newEntry.setNumberOfHops(neighbourEntry.getNumberOfHops() + 1);
			routingTable.setEntry(networkIndex, newEntry);
		}
		
		/**
		 * Prints the routing table.
		 */
		public void printTable() {
			System.out.println("Table for " + toString());
			System.out.println(routingTable.toString());
		}
		
		/**
		 * Represents router with index 0 as "R1", index 1 as "R2", and so on.
		 */
		@Override
		public String toString() {
			return "R" + (index + 1);
		}
		
		/**
		 * Routers are considered equal if they have the same index.
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof Router) {
				Router r  = (Router) o;
				if (r.index == index) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * The routing table for the router.
	 */
	public class RoutingTable {
		
		private TableEntry[] entries;
		
		public RoutingTable() {
			entries = new TableEntry[NUMBER_OF_NETWORKS];
		}
		
		/**
		 * Retrieve the table entry at the given index.
		 */
		public TableEntry getEntry(int index) {
			return entries[index];
		}
		
		/**
		 * Set the table entry at the given index.
		 */
		public void setEntry(int index, TableEntry entry) {
			entries[index] = entry;
		}
		
		/**
		 * Prints the routing table in table format.
		 */
		@Override 
		public String toString() {
			String result = "Dest\tNext-hop\tHops\n";
			for (TableEntry entry : entries) {
				if (entry != null) {
					result += entry.toString();
				}
			}
			return result;
		}
	}
	
	/**
	 * Represents each entry in the routing table.
	 */
	public static class TableEntry {
		
		private Network destinationNetwork;
		private Router nextHopRouter;
		private int numberOfHops;
		
		public Network getDestinationNetwork() {
			return destinationNetwork;
		}
		
		public void setDestinationNetwork(Network destinationNetwork) {
			this.destinationNetwork = destinationNetwork;
		}
		
		public Router getNextHopRouter() {
			return nextHopRouter;
		}
		
		public void setNextHopRouter(Router nextHopRouter) {
			this.nextHopRouter = nextHopRouter;
		}
		
		public int getNumberOfHops() {
			return numberOfHops;
		}
		
		public void setNumberOfHops(int numberOfHops) {
			this.numberOfHops = numberOfHops;
		}
		
		@Override
		public String toString() {
			String router = (nextHopRouter != null) ? nextHopRouter.toString() : "*";
			return destinationNetwork.toString() + "\t" + router + "\t\t" + numberOfHops + "\n";
		}
	}
}
