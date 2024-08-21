/* Program to compute Pi using Monte Carlo methods */

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <omp.h>
#include "logger.h"
#define SEED 35791246

// Todo: Parallelise calc_pi
double calc_pi(int num_iterations, int nt){

    int i=0; /* # of points in the 1st quadrant of unit circle */
    int circle = 0;
    int square = 0;
    double x=0, y=0, z=0;
    struct drand48_data randBuffer;
    srand48_r(SEED ^ omp_get_thread_num(), &randBuffer);

    #pragma omp parallel private(x, y, z, i) \
    reduction(+: circle, square) num_threads(nt)
    for ( i=0; i<num_iterations; i++) {
        drand48_r(&randBuffer, &x);
        drand48_r(&randBuffer, &y);
        z = sqrt(x*x+y*y);

        if (z<=1) {
            circle++;
        }
        square++;
    }
    
    return 4 * ((double)circle/(double)square);
}

int main(int argc, char* argv[])
{
    int num_iterations=200000;
    int nt = 1;
    double pi;
    double start, finish;

    if (argc != 3) {
        printf("Usage: ./prime <num_iterations> <num_threads>\n");
        return 0;
    }

    num_iterations = strtol(argv[1], NULL, 10);
    nt = strtol(argv[2], NULL, 10);
    
    start = omp_get_wtime();
    pi = calc_pi(num_iterations, nt);
    finish = omp_get_wtime();
    printf("# of trials= %d , # threads = %d, estimate of pi is %g\n", num_iterations, nt, pi);
    printf("Elapsed time = %f seconds\n", finish - start);
    logger(160, "estimate of pi to 3 decimals: ", pi);

    return 0;
}
