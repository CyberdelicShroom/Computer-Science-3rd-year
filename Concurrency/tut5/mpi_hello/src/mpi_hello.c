/* vim:
 * :se tw=2
 *
 * File: 
 * mpi_hello.c
 *
 * Purpose: 
 * A "hello world" program that uses MPI
 */
#include <stdio.h>
//#include <string.h> 
#include <mpi.h>
#include "logger.h"

#define MAX_STRING 100

int main(void) {
	char greeting[MAX_STRING]; 
	int my_rank = 0, comm_sz = 0;

	/* Start up MPI */
	MPI_Init(NULL, NULL);
	/* Get the number of processes */
    MPI_Comm_size(MPI_COMM_WORLD, &comm_sz);
	/* Get my rank among all the processes */
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

    if (my_rank != 0) {
		/* Create greeting message for process my_rank */
        sprintf(greeting, "Greetings from process %d of %d!", my_rank, comm_sz);
		log_msg(greeting);
        //MPI_Send(greeting, strlen(greeting)+1, MPI_CHAR, 0, 0, MPI_COMM_WORLD);
		/* Send message to process 0 */
		MPI_Send(greeting, MAX_STRING, MPI_CHAR, 0, 0, MPI_COMM_WORLD);
    } else {
		sprintf(greeting, "Greetings from process %d of %d!", my_rank, comm_sz);
		log_msg(greeting);
		printf("%s\n", greeting);
		/* Receive a message from each of the other processes and print it*/
		int q;
        for (q = 1; q < comm_sz; q++) {
            MPI_Recv(greeting, MAX_STRING, MPI_CHAR, q, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            printf("%s\n", greeting);
        }
    }
	/* Shut down MPI */
    MPI_Finalize();

	return 0;
}
