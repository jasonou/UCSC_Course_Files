########################################
# Name: Jason Ou
# Class: CMPS012M
# Date: November 7, 2014
# Filename: Makefile
# Description: Makefile used to manage class files
########################################

CC = gcc
CFLAGS = -std=c99 -Wall -Werror -Wfatal-errors -g
OBJS = queue.o
OUTPUT = queue
HEADERS = minunit.h

${OUTPUT}: ${OBJS}
	${CC} $< -o ${OUTPUT}

%.o: %.c ${HEADERS}
	${CC} -c ${CFLAGS} $<

clean:
	rm -f *.o

spotless: clean
	rm -f ${OUTPUT}

test: ${OUTPUT}
	./${OUTPUT}
	
.PHONY: clean spotless test