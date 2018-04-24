/************************
 * CMPT 300 - PROJECT 2 *
 * FILE:   SHELL.C      *
 * AUTHOR: KEVIN GRANT  *
 *		   301192898    *
 ************************/

#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <limits.h>
#include <ctype.h>
#include <signal.h>
#include <errno.h>

#define COMMAND_LENGTH 1024
#define NUM_TOKENS (COMMAND_LENGTH / 2 + 1)
#define HISTORY_DEPTH 10

/* Global _Bool that is true if the program is currently performing a '!n' 
 * command, false otherwise
 */
_Bool historyExclamation = false;

/* Counter for the number of commands given */
int commandsGiven = 0;

/* Array for holding history values */
char history[HISTORY_DEPTH][COMMAND_LENGTH];

/* Returns the number of digits in an integer. 
 * Will be used to count the digits in commandsGiven,
 * So we know how much space to allocate a char when 
 * converting commandsGiven to a string.
 */
int digitCount(int c)
{
	int digits = 0;

	while(c != 0) {
		c = c/10;
		digits++;
	}

	return digits;
}

/* Adds command to history array */
void addToHistory(char *buff)
{	
	// Create a char array with enough space to hold digits of commandsGiven
	int d = digitCount(commandsGiven);
	char num[d];

	// Converts commandsGiven to a string and stores it in num
	sprintf(num, "%d", commandsGiven);

	// HISTORY_DEPTH or more commands have already been added 
	if(commandsGiven > HISTORY_DEPTH) {

		// Shift existing history contents up in the array
		for(int row = 1; row < HISTORY_DEPTH; row++) {
			for(int col = 0; col < COMMAND_LENGTH; col++) {
				history[row-1][col] = history[row][col];
			}
		}	
	}

	// Determine which history row to add command to
	int r;
	if(commandsGiven < HISTORY_DEPTH) {
		r = commandsGiven - 1;
	}
	else {
		r = HISTORY_DEPTH - 1;
	}

	// Add the command number
	for(int c = 0; c < d; c++) {
		history[r][c] = num[c];
	}

	// Add a tab between number and command
	history[r][d] = '\t';
	d++;

	// Add the command to history
	int b = 0;
	while(buff[b] != '\0' && d < COMMAND_LENGTH) {
		history[r][d] = buff[b];
		d++;
		b++;
	}
	history[r][d] = '\0';

}

/* writes the history to the user */
void writeHistory()
{
	// Determine how many rows to write
	int rows;
	if(commandsGiven < HISTORY_DEPTH) {
		rows = commandsGiven;
	}
	else {
		rows = HISTORY_DEPTH;
	}

	// write
	int c = 0;
	for(int r = 0; r < rows; r++) {
		while(history[r][c] != '\0') {
			write(STDOUT_FILENO, &history[r][c], 1);
			c++;
		}
		c = 0;
	}
}

/* Signal handler function 
 * Writes the history to the user.
 */
void handle_SIGINT()
{
	write(STDOUT_FILENO, "\n", strlen("\n"));
	writeHistory();
}

/*
 * The following functions are helpers for the ! command:
 * getNum()
 * checkExclamation()
 * getRowNum()
 * copyCommand()
 * writeCommand()
 */

/* Gets the number from the '!n' command
 * Returns: -1 if not a valid number
 *			n, where n is the valid number
 */
int getNum(char *buff)
{
	char *number = calloc(strlen(buff), 1);

	// Check each char to make sure it is a digit
	int i = 1;
	while(buff[i] != '\0') {
		if(!isdigit(buff[i])) {
			free(number);
			return -1;
		}
		else {
			number[i-1] = buff[i];
		}

		i++;
	}

	int n = atoi(number);

	free(number);

	return n;
}

/* Checks if the command of the form '!n' is valid 
 * Returns: -2 if command is invalid form
 *			-1 if command is valid form, but 'n' is not in scope of history
 *			-3  if command is valid of form '!!'
 *			n  if command is valid of form '!n'
 */
int checkExclamation(char *buff) 
{	
	// User inputed '!!'
	if(buff[1] == '!' && buff[2] == '\0') {
		return -3;
	}

	// Get the number from '!n'. -2 if n is invalid
	int n = getNum(buff);

	if(n == -1) {
		return -2;
	}

	// Check if n is within the scope of the history
	if((n > commandsGiven-10) && (n <= commandsGiven) && n > 0) {
		return n;
	}
	else {
		return -1;
	}
}

/* Gets the row number of history with command number n */
int getRowNum(int number) 
{
	// Create a char array with enough space to hold digits of number
	int d = digitCount(number);
	char num[d];

	// Converts number to a string and stores it in num
	sprintf(num, "%d", number);

	for(int r = 0; r < 10; r++) {

		_Bool equals = true;

		// Check each row for the correct number
		for(int c = 0; c < d; c++)
		{
			if(history[r][c] == num[c]) {
				if((c == d - 1) && equals) {
					return r;
				}
			}
			else {
				equals = false;
			}
		}
	}
	
	return 0;	
}

/* Copies a command from history and stores it into command */
char *copyCommand(int n)
{
	int r;
	char *command = calloc(PATH_MAX +1, 1);

	// User inputed !! - get row number of last row
	if(n == -3) {
		r = getRowNum(commandsGiven);
	}
	// user inputed !n - get row number of n
	else {
		r = getRowNum(n);	
	}

	// Go to the beginning of the command
	int i = 0;
	while(history[r][i] != '\t')
	{
		i++;
	}
	i++;

	// Copy the command from history into command
	int s = 0;
	while(history[r][i] != '\0')
	{
		command[s] = history[r][i];
		i++;
		s++;
	}

	command[i] = '\0';

	return command;
}

/* Writes the command */
void writeCommand(char *command)
{
	int i = 0;
	while(command[i] != '\0') {
		write(STDOUT_FILENO, &command[i], 1);
		i++;
	}
}

/*
 * Create the string that will be used for the prompt.
 *   The prompt consists of the current working directory along with
 *   a '> ' at the end.
 */
char *prompt()
{
	char *arrow = "> ";
	char *prompt = malloc(PATH_MAX + strlen(arrow) + 1);
	getcwd(prompt, PATH_MAX);
	strcat(prompt, arrow);

	return prompt;
}

/**
 * Command Input and Processing
 */

/*
 * Tokenize the string in 'buff' into 'tokens'.
 * buff: Character array containing string to tokenize.
 *       Will be modified: all whitespace replaced with '\0'
 * tokens: array of pointers of size at least COMMAND_LENGTH/2 + 1.
 *       Will be modified so tokens[i] points to the i'th token
 *       in the string buff. All returned tokens will be non-empty.
 *       NOTE: pointers in tokens[] will all point into buff!
 *       Ends with a null pointer.
 * returns: number of tokens.
 */
int tokenize_command(char *buff, char *tokens[])
{
	int token_count = 0;
	_Bool in_token = false;
	int num_chars = strnlen(buff, COMMAND_LENGTH);
	for (int i = 0; i < num_chars; i++) {
		switch (buff[i]) {
		// Handle token delimiters (ends):
		case ' ':
		case '\t':
		case '\n':
			buff[i] = '\0';
			in_token = false;
			break;

		// Handle other characters (may be start)
		default:
			if (!in_token) {
				tokens[token_count] = &buff[i];
				token_count++;
				in_token = true;
			}
		}
	}
	tokens[token_count] = NULL;
	return token_count;
}

/**
 * Read a command from the keyboard into the buffer 'buff' and tokenize it
 * such that 'tokens[i]' points into 'buff' to the i'th token in the command.
 * buff: Buffer allocated by the calling code. Must be at least
 *       COMMAND_LENGTH bytes long.
 * tokens[]: Array of character pointers which point into 'buff'. Must be at
 *       least NUM_TOKENS long. Will strip out up to one final '&' token.
 *       tokens will be NULL terminated (a NULL pointer indicates end of tokens).
 * in_background: pointer to a boolean variable. Set to true if user entered
 *       an & as their last token; otherwise set to false.
 */
void read_command(char *buff, char *tokens[], _Bool *in_background, _Bool *isExcla)
{
	*in_background = false;
	*isExcla = false;
	int length;

	// Read input
	if(!historyExclamation){
		length = read(STDIN_FILENO, buff, COMMAND_LENGTH-1);
	}
	else {
		length = strlen(buff);
	}

	/* Allocate new char array 'input' to add to history
	 * Use calloc to set allocated memory to 0
	 * This is implemented in order to avoid bad pointers from accessing
	 * unitialized values of buff
	 */
	char *input = calloc(length + 1, 1);
	for(int i = 0; i < length; i++) {
		input[i] = buff[i];
	}

	// if not signalling
	if(length > 0) {

		// If the user entered input
		if(input[0] != '\n') {
		
			// Command is of type '!'
			if(input[0] == '!') {
				*isExcla = true;
			}
			
			// Add to history, unless ! command 
			if(input[0] != '!') {
				commandsGiven ++;
				addToHistory(input);
		    }	

			if ((length < 0) && (errno != EINTR)) {
				perror("Unable to read command from keyboard. Terminating.\n");
				exit(-1);
			}

			// Null terminate and strip \n.
			buff[length] = '\0';
			if (buff[strlen(buff) - 1] == '\n') {
				buff[strlen(buff) - 1] = '\0';
			}

			// Tokenize (saving original command string)
			int token_count = tokenize_command(buff, tokens);
			if (token_count == 0) {
				return;
			}

			// Extract if running in background:
			if (token_count > 0 && strcmp(tokens[token_count - 1], "&") == 0) {
				*in_background = true;
				tokens[token_count - 1] = 0;
			}
		}
		// user entered no input, program should output nothing
		else {
			tokens[0] = 0;
		}
	}
	// signalling, program should do nothing more than signal
	else {
		tokens[0] = 0;
	}	

	free(input);
}

/*
 * Main and Execute Commands
 */
int main(int argc, char* argv[])
{
	char input_buffer[COMMAND_LENGTH];

	char *tokens[NUM_TOKENS];

	/* Set up the signal handler */
	struct sigaction handler;
	handler.sa_handler = handle_SIGINT;
	handler.sa_flags = 0;
	sigemptyset(&handler.sa_mask);
	sigaction(SIGINT, &handler, NULL);

	while (true) {
		/*
		 * Values used for helping with correct execution when using the ! command 
		 * cmd = pointer character array where the characters are the command being re-run
		 * freeCmd = boolean determining whether or not to free() cmd at the while loop
		 *			 [ensures we do not attempt to free() if it was never allocated]
		 * isExcla = boolean determining whether or not the command given was of form !
		 *			 so we know to run the ! command
		 * invalidExcla = boolean determining whether the ! command was of valid form
		 */
		char *cmd;
		_Bool freeCmd = false;
		_Bool isExcla = false;
		_Bool invalidExcla = false;

		historyExclamation = false;

		_Bool in_background = false;
		// Get the correct prompt
		char *inputLine = prompt();

		// Get command
		// Use write because we need to use read() to work with
		// signals, and read() is incompatible with printf().
		write(STDOUT_FILENO, inputLine, strlen(inputLine));

		read_command(input_buffer, tokens, &in_background, &isExcla);

		free(inputLine);
	
		/* ! Command
		 * If no commands have previously been given, print an error informing the user.
		 * If invalid characters after the '!', print the apprporiate error msg.
		 *
		 * copyCommand() copies the correct command into cmd and writeCommand() writes it.
		 * cmd is passed through read_command() as the buffer. read_command() utilizes 
		 * global _Bool historyExclamation to know to just use the data in the buffer (cmd)
		 * rather than read from input.
		 * read_command() calls tokenize_command(). This sets the tokens to the command being
		 * referred to by the ! command. The program then carries on as usual.
		 */
		if (isExcla) {

			historyExclamation = true;
			// No previous commands
			if(commandsGiven == 0) {
				invalidExcla = true;
				write(STDOUT_FILENO, "error: no previous commands given\n",
					  strlen("error: no previous commands given\n"));
			}
			else {
				int check = checkExclamation(tokens[0]);
				// invalid input	
				if(check == -2) {
					invalidExcla = true;
					write(STDOUT_FILENO, "error: chars after '!' are not positive integer\n", 
						  strlen("error: chars after '!' are not positive integer\n"));
				}	
				// n out of history scope
				else if(check == -1) {
					invalidExcla = true;
					write(STDOUT_FILENO, "error: int after '!' is not valid\n", 
						  strlen("error: int after '!' is not valid\n"));
				}
				// !! or !n 	
				else {
					// cmd will be allocated, so set freeCmd to true
					freeCmd = true;
					// copy the command to cmd, write it, and read it
					cmd = copyCommand(check);
					writeCommand(cmd);
					read_command(cmd, tokens, &in_background, &isExcla);
				}
			}
		}
		
		// no input entered
		if(tokens[0] == 0) {
			; //Do nothing
		}

		/* Internal Commands
		 * exit:    Exit the program. Does not matter how many arguments the user
		 *		    enters; they are all ignored.
		 * pwd:     Display the current working directory.
		 * cd:      Change the current working directory.
		 * history: displays the 10 most recent commands executed 
		 */

		// exit command
		else if (strcmp(tokens[0],"exit") == 0) {
			exit(0);
		}

		// pwd command
		else if (strcmp(tokens[0],"pwd") == 0) {

			// Create char array and allocate enough memory for any path
			char *directory = malloc(PATH_MAX + 1);

			// Get the working directory
			getcwd(directory, PATH_MAX);

			// Write the directory
			write(STDOUT_FILENO, directory, strlen(directory));
			write(STDOUT_FILENO, "\n", strlen("\n"));

			free(directory);
		}

		// cd command
		else if (strcmp(tokens[0],"cd") == 0) {

			if(chdir(tokens[1]) == -1) {
				perror("error");
			}	

		}

		// history command
		else if (strcmp(tokens[0],"history") == 0) {
			writeHistory();
		}

		else {

			if(!invalidExcla) {
				/* Fork a child process */ 
				pid_t pid = fork();

				/* Error occured */
				if (pid < 0) {			
					write(2, "fork() Failed.\n", strlen("fork() Failed.\n"));
					exit(-1);
				}
				/* Child process invokes execvp() using results in token array. */
				else if (pid == 0) {	
					//print error if error occurred in execvp
					if(execvp(tokens[0], tokens) == -1){
						write(STDOUT_FILENO, tokens[0], strlen(tokens[0]));
						write(STDOUT_FILENO, ": Unknown command.\n", strlen(": Unknown command.\n"));
						exit(0);
					}

				}
				/* Parent process
				 *    If in_background is false, parent waits for
				 *    child to finish. Otherwise, parent loops back to
				 *    read_command() again immediately. 
				 */
				else {

					//Child not running in background, wait
					if (!in_background) {					
						waitpid(pid, NULL, 0);
					}
					else { 
						; //If child running in background, don't wait
					}	
				}
			}	
		}
		/* Cleanup any previously exited background child processes (zombies) */
		while (waitpid(-1, NULL, WNOHANG) > 0) {
			; //do nothing
		}

		// Free cmd, if it has been allocated.
		if(freeCmd) {
			free(cmd);
		}
	}

	return 0;
}

// :)