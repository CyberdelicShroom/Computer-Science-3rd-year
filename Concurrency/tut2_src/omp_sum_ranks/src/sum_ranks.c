#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

int main(int argc, char* argv[]) {
    int nt = 0;
    int sum = 0;

    if (argc != 2) {
        printf("Usage: ./sum_ranks <num_threads>\n");
    } else {
        nt = strtol(argv[1], NULL, 10);

        #pragma omp parallel num_threads(nt)\
        reduction(+: sum)
        sum += nt;
        //Todo: Calculate the sum of the rank values in parallel

        printf("The sum of the rank values is %d\n", sum);
    }

    return 0;
}
