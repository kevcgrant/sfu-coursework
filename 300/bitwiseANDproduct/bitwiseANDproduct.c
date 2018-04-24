#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

/*
 * Converts an integer to binary, represented as a string
 */
char* int_to_bin_string(int k)
{
	char* binary = malloc(sizeof(char) * 32);
	int rem;

	for (int i = 32; i >= 0; i--) {

		if (k == 0) {
			binary[i] = '0';
		}
		else {
			rem = k % 2;
			if (rem == 0) {
				binary[i] = '0';
			}	
			else {
				binary[ i] = '1';
			}
			k = k/2;
		}

	}

	return binary;
}

/*
 * Converts a binary string to an integer
 */
int bin_string_to_int(char * s) 
{
	double num = 0;

	for (double i = 32; i >= 0; i--) {
		if (s[(int)i] == '1') {
			num = num + pow(2, 32-i);
		}
	}

	return (int)num;
} 

int main(int argc, char **argv)
{
	// Check input
	if (argc != 3) {
		printf("Invalid number of arguments.\n");
		exit(-1);
	}
	if (atoi(argv[1]) <= 0 || atoi(argv[2]) <= 0) {
		printf("Arguments are of invalid format.\n");
		exit(-1);
	}
	
	int n = atoi(argv[1]);
	int m = atoi(argv[2]);

	if (n >= m) {
		printf("The second argument must be larger than the first.\n");
		exit(-1);
	}

	char* string_1 = int_to_bin_string(n);

	// Bitwise AND product computation
	for (int i = n; i < m; i++) {
		char* string_2 = int_to_bin_string(i+1);

		// Bitwise AND of current binary string and next
		// Store result in string_1
		for (int s = 0; s <= 32; s++) {
			if (string_1[s] == '1' && string_2[s] == '1') {
				string_1[s] = '1';
			}
			else {
				string_1[s] = '0';
			}
		}

		free(string_2);
	}

	printf("Bitwise AND Product: %d\n", bin_string_to_int(string_1));

	free(string_1);	
}
