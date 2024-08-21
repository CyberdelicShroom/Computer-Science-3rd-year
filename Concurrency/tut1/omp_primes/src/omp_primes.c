#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <omp.h>

int isPrime(int n);

int main(int argc, char* argv[]) {

    int n = atoi(argv[1]);
    int num_primes = 0;
    int i;

	#pragma omp parallel for
    for(i = 2; i <= n; i++) {
        if( isPrime(i) ) {
            #pragma omp critical
            num_primes++;
            //printf("Number of primes = %d\n", num_primes);
        }  
    }
    printf("Number of primes = %d\n", num_primes);
	return 0;
}

int isPrime(int n)  
{  
    int prime = 1, i;
  
    for(i = 2; i <= sqrt(n); i++) {
         if (n % i == 0) {  
            prime = 0;  
            break;  
         }  
    }  
  
    return prime;
}