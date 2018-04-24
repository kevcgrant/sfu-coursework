
// Simple chronological backtracking

// **********************************************************************
// Check if the current instantiation of the variables is consistent
// by looking at the edge between the current variable and all previours
// variables.
// **********************************************************************
 

int NETWORK::consistent_bt( SOLUTION solution, int current )
{
  int i;

  for( i=1; i<current; i++ )
    {
      checks++;
      if ( N[current][i].access(solution[current], solution[i]) == 0)
	return(0);
    }
  return(1);
}


// ************************************************************************
// Solve the constraint network using the simple chronological backtracking
// method (BT).
// ************************************************************************

int NETWORK::BT( SOLUTION solution, int current, int number, int *found )
{
  int i;
  int k = N[1][1].size();

  if (current == 1) 
    {
      clear_setup(n);
      *found = 0;
    } 
  else 
    if (current > n) 
    {
      process_solution( solution );
      *found = 1;
      if ( count == 0 )
        // save first solution into SOL_1
        sfs( solution ); 
      count++;
      return(number == 1 ? 1 : 0);
    }
  if (time_expired())
    return(1);
  for (i = 0; i < k; i++) 
    {
      if (N[current][current].access(i,i) == 0)
        continue;
      solution[current] = i;
      if (consistent_bt(solution, current))
        if (BT(solution, current + 1, number, found))
          return(1);
    }
  return(0);
}

