
CC = icc
CXX = g++
CC_FLAGS = -std=c99   

ifeq ($(DEBUG), 1)
	CC_FLAGS += -g
else
	CC_FLAGS += -O2
endif

all: othello-serial othello-parallel

clean:
	rm -f othello-serial othello-parallel

othello-serial: othello.c hwtimer.h othello.h othello-human.c othello-simple-ai.c othello-good-ai.c
	 $(CC) $(CC_FLAGS) othello.c othello-human.c othello-simple-ai.c othello-good-ai.c -o othello-serial # -D  SERIALIZE . If you want to serialize. 

othello-parallel: hwtimer.h othello.c hwtimer.h othello.h othello-human.c othello-simple-ai.c othello-good-parallel-ai.c
	 $(CC) $(CC_FLAGS) othello.c othello-human.c othello-simple-ai.c othello-good-parallel-ai.c -o othello-parallel
