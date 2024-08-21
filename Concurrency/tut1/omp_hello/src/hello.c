#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

int main(int argc, char* argv[]) {
	
	int rank, thread_count;
	thread_count = strtol(argv[1], NULL, 10);

	#pragma omp parallel num_threads(thread_count)
	{
		rank = omp_get_thread_num();
		int thread_count = omp_get_num_threads();
		printf("Hello World from thread %d of %d\n", rank, thread_count);
		
	}
	return 0;
} /* main */
