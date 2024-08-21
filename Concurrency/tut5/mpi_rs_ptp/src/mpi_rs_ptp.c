/* File:       
 *    mpi_rs_ptp.c
 *
 */
#include <stdio.h>
#include <string.h>
#include <mpi.h>

#include "logger.h"

int main(void) {
   int my_rank = 0; /* My process rank */
   int comm_sz = 0; /* Number of processes */
   /* TODO: Start up MPI */
   MPI_Init(NULL, NULL);
   /* TODO: Get the number of processes */
   MPI_Comm_size(MPI_COMM_WORLD, &comm_sz);
   /* TODO: Get my rank among all the processes */
   MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

   if (my_rank != 0) { 
      /* TODO: Send my_rank to process 0 */
      MPI_Send(&my_rank, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
      /* TODO: Execute a broadcast to receive the rank sum */
      MPI_Bcast(&my_rank, 1, MPI_INT, 0, MPI_COMM_WORLD);

   } else {
      
	   /* TODO: Initialise ranksum with the rank of process 0 */
      int ranksum = my_rank;
	   /* TODO: Receive the rank of each of the other processes and add it to ranksum */
      int q;
      for (q = 1; q < comm_sz; q++) {
         MPI_Recv(&my_rank, 1, MPI_INT, q, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
         ranksum += my_rank;
      }
      /* TODO: Execute a broadcast to send ranksum to all the processes */
      MPI_Bcast(&ranksum, 1, MPI_INT, 0, MPI_COMM_WORLD);
      /* TODO: Uncomment the following to lines to log the result and print it
         - do not change the lines */
      log_result(ranksum);
      printf("The sum of all the process's ranks are: %i\n", ranksum);
   }

   /* Shut down MPI */
   MPI_Finalize();
   return 0;
}
