import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates weighted undirected graphs.
 * <p>
 * The following command line options are available:
 * <pre>
 *  -d : density of the graph. Must be a float between [0.0, 1.0]. Default is 0.5
 *  -D : number of destinations. Must be an int between [1, nodes). Default is 2
 *  -n : number of nodes. Must be an int between [4, 20]. Default is 10
 * </pre>
 * 
 * @author Kevin Grant / 301192898
 *
 */
public class GraphGenerator {

	private static final String DENSITY_OPTION = "-d";
	private static final String NODES_OPTION = "-n";
	private static final String DESTINATIONS_OPTION = "-D";
	
	private static final float DEFAULT_DENSITY = 0.5f; 
	private static final int DEFAULT_NODES = 10;
	private static final int DEFAULT_NUM_DESTINATIONS = 2;
	
	private static final int MIN_NODES = 4;
	private static final int MAX_NODES = 20;
	private static final int MAX_EDGE_WEIGHT = 100;
	
	private final float density;
	private final int nodes;
	private final int numDestinations;
	
	private int[][] edgeMatrix;
	private float edgeCount = 0.0f;
	
	public GraphGenerator(float density, int nodes, int numDestinations) {
		ensureValidDensity(density, nodes);
		ensureValidNodes(nodes);
		ensureValidDestinations(numDestinations, nodes);
		
		this.density = density;
		this.nodes = nodes;
		this.numDestinations = numDestinations;
		this.edgeMatrix = new int[nodes][nodes];
	}
	
	private void ensureValidDensity(float density, float nodes) {
		boolean canGraphBeConnected = (density >= (2 / nodes));
		boolean isDensityLessEqualMax = (density <= 1.0f); 
		if (!canGraphBeConnected) {
			throw new IllegalArgumentException("Graph with " + nodes + " nodes must have density at least " + 2 / nodes);
		} else if (!isDensityLessEqualMax) {
			throw new IllegalArgumentException("Density cannot exceed 1.0");
		} else {
			return;
		}
	}
	
	private void ensureValidNodes(int nodes) {
		if (nodes < MIN_NODES || nodes > MAX_NODES) {
			throw new IllegalArgumentException("Number of nodes must be in range [" + MIN_NODES + ", " + MAX_NODES + "].");
		}
	}
	
	private void ensureValidDestinations(int destinations, int nodes) {
		if (destinations < 1 || destinations >= nodes) {
			throw new IllegalArgumentException("Number of destinations must be in range [1, N)");
		}
	}
	
	public void generateGraph() {
		minimallyConnectGraph();
		addEdgesUntilDensityReached();
		printEdgeRelations();
		printNodeRelations();
	}
	
	private void minimallyConnectGraph() {
		List<Integer> nodeList = getShuffledNodeList();
		for (int i = 0; i < nodes - 1; i++) {
			giveEdgeToNodes(nodeList.get(i), nodeList.get(i + 1), getRandomEdgeWeight());
		}
	}
	
	private List<Integer> getShuffledNodeList() {
		List<Integer> nodeList = new ArrayList<>();
		for (int i = 0; i < nodes; i++) {
			nodeList.add(i);
		}
		Collections.shuffle(nodeList);
		return nodeList;
	}
	
	private void giveEdgeToNodes(int u, int v, int weight) {
		edgeMatrix[u][v] = weight;
		edgeMatrix[v][u] = weight;
		edgeCount++;
	}
	
	private int getRandomEdgeWeight() {
		Random rand = new Random();
		return rand.nextInt(40) + 5;
	}
	
	private void addEdgesUntilDensityReached() {
		while (getCurrentDensity() < density) {
			addRandomEdge();
		}
	}
	
	private float getCurrentDensity() {
		return (2 * edgeCount) / (nodes * (nodes - 1));
	}
	
	private void addRandomEdge() {
		int u = getRandomNodeWithoutMaxDegree();
		int v = getRandomNonSiblingNode(u);
		giveEdgeToNodes(u, v, getRandomEdgeWeight());
	}
	
	private int getRandomNodeWithoutMaxDegree() {
		int node = getRandomNode();
		while (getNodeDegree(node) >= nodes - 1) {
			node = getRandomNode();
		}
		return node;
	}
	
	private int getNodeDegree(int node) {
		int degree = 0;
		for (int i = 0; i < nodes; i++) {
			if (edgeMatrix[node][i] != 0) {
				degree++;
			}
		}
		return degree;
	}
	
	private int getRandomNode() {
		Random rand = new Random();
		return rand.nextInt(nodes);
	}
	
	private int getRandomNonSiblingNode(int currentNode) {
		int candidate;
		Random rand = new Random();
		do {
			candidate = rand.nextInt(nodes);
		} while (candidate == currentNode || doNodesHaveEdge(currentNode, candidate));
		
		return candidate;
	}
	
	private boolean doNodesHaveEdge(int u, int v) {
		if (u == v) {
			return false;
		} else {
			return edgeMatrix[u][v] != 0 || edgeMatrix[v][u] != 0;
		}
	}
	
	private void printEdgeRelations() {
		String output = "edge={";
		for (int u = 0; u < nodes; u++) {
			for (int v = 0; v < u; v++) {
				if (edgeMatrix[u][v] != 0) {
					output += ((u + 1) + "," + (v + 1) + "," + edgeMatrix[u][v] + "; ");
				}
			}
		}
		
		output = output.substring(0, output.length() - 2);
		System.out.println(output + "}");
	}
	
	private void printNodeRelations() {
		Random rand = new Random();
		int start = rand.nextInt(nodes);
		System.out.println("start=" + (start + 1));
		
		String dest="dest={";
		List<Integer> nodeList = getShuffledNodeList();
		int destCount = 0;
		for (int i = 0; i < nodes; i++) {
			if (nodeList.get(i) != start) {
				dest += ((nodeList.get(i) + 1) + ";");
				destCount++;
			}
			if (destCount >= numDestinations) break;
		}
		dest = dest.substring(0, dest.length() - 1);
		System.out.println(dest + "}");
	}
	
	
	private static float tryToParseValueFromArg(String arg, String option) {
		try {
			return Float.parseFloat(arg);
		} catch (NumberFormatException e) {
			if (option.equals(NODES_OPTION) || option.equals(DESTINATIONS_OPTION)) {
				System.out.println("Value for " + option + " must be a number > 1.");
			} else {
				System.out.println("Value for " + option + " must be in range [0, 1]");
			}
			System.exit(1);
			return 1f;
		}
	}
	
	public static void main(String[] args) {
		int nodes = DEFAULT_NODES;
		float density = DEFAULT_DENSITY;
		int destinations = DEFAULT_NUM_DESTINATIONS;
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals(NODES_OPTION)) {
				nodes = (int) tryToParseValueFromArg(args[i + 1], NODES_OPTION);
			} else if (args[i].equals(DENSITY_OPTION)) {
				density = tryToParseValueFromArg(args[i + 1], DENSITY_OPTION);
			} else if (args[i].equals(DESTINATIONS_OPTION)) {
				destinations = (int) tryToParseValueFromArg(args[i + 1], DESTINATIONS_OPTION);
			}
		}
		
		GraphGenerator generator = new GraphGenerator(density, nodes, destinations);
		generator.generateGraph();
	}

}
