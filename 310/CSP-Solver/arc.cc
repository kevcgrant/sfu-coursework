
/*
 * This function determines whether given a specific edge, there exists some
 * instantiation for "y" given the instantiation for "x".  It loops through
 * all the value of the "y" variable.
 */
int NETWORK::value_arc(int k, int x, int y, int i)
{
    int j;

    for (j = 0; j < k; j++)
        if (N[x][y].access(i,j) && N[y][y].access(j,j))
            return(1);
   
    return(0);
}


/*
 * Determine which domain values for variable "x" can be eliminated by
 * considering the edge between "x" and "y".  The number of values deleted
 * from the domain is returned.  Each possible domain value of "x" is
 * considered separately in the loop.
 */
int NETWORK::revise_arc(int k, int x, int y)
{
	
  	int number_deleted = 0;
  
  	for (int i = 0; i < k; i++) {
		if (!value_arc(k, x, y, i)) {
	    	N[x][y].assign(i, i, 0);
	    	number_deleted++;
	    }
  	}

  	return number_deleted;
  	
}


/*
 * Function to perform complete arc consistency on entire constraint network.
 * The process considers each "half" of the constraint network separate as also
 * done by Mackworth.  It processes each edge using a stack to store edges
 * still to be examined.  It first pushes all "edges" to be examined.  Then, it
 * processes the edges one by one, placing variables back on the stack if
 * necessary (i.e. their domain values have changed).  Note that edges simply
 * correspond to the edges leading into the variable stored on the stack (thus
 * only these variables need to be stored instead of all edges).  The function
 * returns whether the network is consistent or not by checking if any
 * variables have their domains empty.  Note that if a variable x has any of
 * its domain values eliminated, this is noted in the constraint C[x][x], by
 * eliminating this value for the original "I" matrix.
 */
int NETWORK::pre_arc()
{
	STACK edge_stack;

	int x;
	int y;

	int k = N[1][1].size();

	// Push all edges onto the stack
    for(x = 1; x <= n; x++) {
    	for(y = 0; y < n; y++) {
    		//edge_stack.push(x, y);
    		edge_stack.push(x);
    	}
    }

    // while stack isn't empty
    while(!edge_stack.stack_empty()) {

    	// Get the edge and pop it from the stack
    	////edge_stack.pop(x, y);
    	edge_stack.pop(y);
    	// If arc is revised, add edge (x, y) back to stack
    	for(x = 1; x <= n; x++) {
    		if((x != y) && revise_arc(k, x, y)) {
    			//edge_stack.push(x, y);
    			edge_stack.push(x);
    		}
    	}
    }

    // Check if variables have domains empty
    bool empty;
    for(x = 1; x <= n; x++) {
    	empty = true;
    	for(y = 0; y < k; y++) {

    		// Domain isn't empty
    		if(N[x][x].access(y, y)) {
    			empty = false;
    		}

    		// Domain empty
    		if((y == k - 1) && empty) {
    			return 0;
    		}
    	}
    }

    return 1;

    
}
 