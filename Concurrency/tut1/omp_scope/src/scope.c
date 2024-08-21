#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

int main(int argc, char* argv[]) {
	
	int rank, thread_count, value;
	thread_count = strtol(argv[1], NULL, 10);
    value = atoi(argv[2]);

    printf("Before: value = %d\n", value);

	#pragma omp parallel num_threads(thread_count) private(value)
    {
        rank = omp_get_thread_num();
        //int thread_count = omp_get_num_threads();
        //value++;
        value = 0;
        printf("Inside parallel region on thread %d: value = %d\n", rank, value);
    }

    printf("After: value = %d\n", value);
	return 0;
} /* main */
