/**
 * @file manager.c
 */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "logger.h"
#include "manager.h"

#define QUANTUM 1
#define TRUE 1
#define FALSE 0

void process_request(struct pcb_t *pcb, struct instruction_t *instruction);
void process_release(struct pcb_t *pcb, struct instruction_t *instruction);
int acquire_resource(struct pcb_t *pcb ,char* resource);

void print_available_resources();
void process_to_readyq(struct pcb_t *proc);
void process_to_waitingq(struct pcb_t *proc);
void process_to_terminatedq(struct pcb_t *proc);

void execute_instruction(struct pcb_t *pcb, struct instruction_t *instruction);

struct pcb_t* detect_deadlock();
void resolve_deadlock (struct pcb_t *pcb);

void print_process_resources(struct pcb_t *pcb);
void print_q (struct pcb_t *pcb);

#define PRIORITY 0
#define RR 1
/*Global resource list which I added: */
struct resource_t *global_resource_list = NULL;
/**
 * The following structs are the queues as required by the project spec together
 * with a pointer to the end of the queue to make insertions into the respective
 * queues easy.
 */
struct pcb_t *ready_queue = NULL;
struct pcb_t *waiting_queue = NULL;
struct pcb_t *terminated_queue = NULL;

/**
 * @brief Schedules each instruction of each process in a round-robin fashion.
 * The number of instruction to execute for each process is governed by the
 * QUANTUM variable.
 *
 * @param pcb The process control block which contains the current process as
 * well as a pointer to the next process control block.
 * @param resource The list of resources available to the system.
 * @param algorithm The type of algorithm to be used (0: Priority, 1: RR).
 * @param time_q The time quantum for the RR algorithm.
 */

void schedule_processes(struct pcb_t *ready_pcbs, struct resource_t *resource_list,
        int sched_algo, int time_quantum) {

            ready_queue = ready_pcbs;
            global_resource_list = resource_list;

            struct pcb_t **ptr_to_head_rq = &ready_queue;
            struct pcb_t *first_pcb_in_rq = *ptr_to_head_rq;

            if(sched_algo == PRIORITY){
                // while (ready_queue != NULL) {
                //     printf("%s\n", ready_queue->page->name);
                //     ready_queue = ready_queue->next;
                // }

                int highest_priority = ready_queue->priority;
                while (ready_queue != NULL) {
                    if(ready_queue->priority < highest_priority){
                        highest_priority = ready_queue->priority;
                    } else {
                        ready_queue = ready_queue->next;
                    }
                }
                // printf("highest priority = %d\n", highest_priority);
                //-------------------------------
                ready_queue = first_pcb_in_rq;
                int num_proc_to_exec = 0;
                while(ready_queue != NULL){
                    if(ready_queue->next_instruction != NULL){
                        num_proc_to_exec++;
                    }
                    ready_queue = ready_queue->next;
                }
                // printf("num_proc_to_exec = %d\n", num_proc_to_exec);
                //-------------------------------
                ready_queue = first_pcb_in_rq;
                int lowest_priority = highest_priority;
                char lowest_prior_proc[100];
                while(ready_queue != NULL){
                    if(ready_queue->next_instruction != NULL){
                        if(ready_queue->priority > lowest_priority){
                            lowest_priority = ready_queue->priority;
                        }
                    }
                    ready_queue = ready_queue->next;
                }

                // printf("Lowest priority = %d\n", lowest_priority);
                ready_queue = first_pcb_in_rq;
                while(ready_queue != NULL){
                    if(ready_queue->priority == lowest_priority){
                        strcat(lowest_prior_proc, ready_queue->page->name);
                        // printf("%s\n", lowest_prior_proc);
                    }
                    ready_queue = ready_queue->next;
                }

                
                ready_queue = first_pcb_in_rq;
                int all_proc_complete = FALSE;
                int num_complete_proc = 0;
                // print_available_resources();
                while (ready_queue != NULL) {
                    if (ready_queue->priority == highest_priority && ready_queue->next_instruction != NULL) {
                        // printf("highest priority = %d\n", highest_priority);

                        while(ready_queue->page->first_instruction != NULL){
                            execute_instruction(ready_queue, ready_queue->page->first_instruction);
                            ready_queue->page->first_instruction = ready_queue->page->first_instruction->next; 
                            // print_available_resources();
                        }
                        
                        char *proc_name = ready_queue->page->name;
                        if(strcmp(proc_name, lowest_prior_proc) == 0){
                            ready_queue->next = NULL;
                        }
                        process_to_terminatedq(ready_queue);
                        
                        num_complete_proc++;
                        // printf("num_complete_proc = %d\n",num_complete_proc);
                        if (num_complete_proc == num_proc_to_exec){
                            // printf("DONE\n");
                            all_proc_complete = TRUE;
                            break;
                        }
                        
                        highest_priority++;
                    } else {
                        if(ready_queue->next == NULL && all_proc_complete == FALSE){
                            highest_priority++;
                            ready_queue = first_pcb_in_rq;
                        } else {
                            ready_queue = ready_queue->next;
                        }
                    }
                }

            } else if (sched_algo == RR){
                // while (ready_queue != NULL) {
                //     printf("bleh: %s\n", ready_queue->page->name);
                //     ready_queue = ready_queue->next;
                // }
            }
    //TODO: Add ready_pcbs to the ready_queue
    //      Add resource_list to a globally declared resource list 

    //printf("TODO: Implement two schedulers: a Priority based scheduler and a Round Robin (RR) scheduler \n");
}

/**
 * @brief Interpret a instruction and calls the respective functions
 *
 * @param pcb The current process for which the instruction must me executed.
 * @param instruction The instruction which must be executed.
 */
void execute_instruction(struct pcb_t *pcb, struct instruction_t *instruction) {
    
    switch (instruction->type) {

        case REQ_V: 
            process_request(pcb, instruction);
            break;
        
        case REL_V: 
            process_release(pcb, instruction);
            break;

        default:
            break;
    }

}

/**
 * @brief Handles the request resource instruction.
 *
 * Executes the request instruction for the process. The function loops
 * through the list of resources and acquires the resource if it is available.
 * If the resource is not available the process sits in the waiting queue and
 * tries to acquire the resource on the next cycle.
 *
 * @param current The current process for which the resource must be acquired.
 * @param instruct The instruction which requests the resource.
 */
void process_request(struct pcb_t *pcb, struct instruction_t *instruction) {

    char* resource_name;
    int available;

    resource_name = instruction->resource;
    available = acquire_resource(pcb, resource_name);
    //if available: Resource gets added to pcb->resources and marked as unavailable in global_resource_list
    //if unavailable: Return 0
    if (available) {
        log_request_acquired(pcb->page->name, resource_name);
        print_available_resources();
        pcb->next_instruction = instruction->next;
        pcb->state = RUNNING;
    } else {
        process_to_waitingq(pcb);
        log_request_waiting(pcb->page->name, resource_name);  
    }
}

/**
 * @brief Handles the release resource instruction.
 *
 * Executes the release instruction for the process. If the resource can be
 * released the process is ready for next execution. If the resource can not
 * be released the process sits in the waiting queue.
 *
 * @param current The process which releases the resource.
 * @param instruct The instruction to release the resource.
 */
void process_release(struct pcb_t *pcb, struct instruction_t *instruction) {
    
    char* resource_name;
    resource_name = instruction->resource;
    int res_in_pcb = FALSE;
    int res_in_grl = FALSE;
    struct resource_t **head_rl = &global_resource_list;
    struct resource_t *ptr_head_grl = *head_rl;
    struct resource_t *lastNode = *head_rl;

    while(lastNode != NULL){
        if(strcmp(lastNode->name, resource_name) == 0) {
            res_in_grl = TRUE;
            break;
        }
        lastNode = lastNode->next;
    }
    lastNode = ptr_head_grl;

    //Check if resource_name is in pcb->resources:
    
    while(lastNode != NULL) {
        if(strcmp(lastNode->proc_name, pcb->page->name) == 0) {
            res_in_pcb = TRUE;
            //printf("res in PCB = true\n");
            lastNode->proc_name = "";
            break;
        }
        lastNode = lastNode->next;
    }
    
    if (res_in_pcb == TRUE && res_in_grl == TRUE) {
        
        //Remove resource from pcb->resources:
        
        if(strcmp(pcb->resources->name, resource_name) == 0) {
            pcb->resources = pcb->resources->next;
        } else {
            while(pcb->resources->next != NULL) {
                
                if(strcmp(pcb->resources->next->name, resource_name) == 0) {
                    pcb->resources->next = pcb->resources->next->next;
                    break;
                }
                else {
                    pcb->resources = pcb->resources->next;
                }
            }
        }
        //---------------------------------------
        //mark available in global_resource_list:

        struct resource_t **head_rl = &global_resource_list;
        struct resource_t *lastNode = *head_rl;
        
        while(lastNode != NULL) {
            if(strcmp(lastNode->name, resource_name) == 0){
                lastNode->available = TRUE;
                break;
            }
            lastNode = lastNode->next;
        }
        //---------------------------------------
        //After every release instruction, cycle through the waiting queue and move all processes waiting for that 
        //resource to the ready queue - remember to update their status first:

        if (waiting_queue != NULL){
            struct pcb_t **head_wq = &waiting_queue;

            struct pcb_t *current_proc  = *head_wq;
            while(current_proc != NULL){
                if(strcmp(current_proc->resources->name, resource_name) == 0){
                    process_to_readyq(current_proc);
                } else {
                    while(current_proc->resources->next != NULL){
                        if(strcmp(current_proc->resources->next->name, resource_name) == 0){
                            process_to_readyq(current_proc);
                            break;
                        } else {
                            current_proc->resources = current_proc->resources->next;
                        }
                    }
                }
                current_proc = current_proc->next;
            }
        }
        //---------------------------------------
        pcb->next_instruction = instruction->next;
        pcb->state = RUNNING;
        log_release_released(pcb->page->name, instruction->resource);
    } else {
        process_to_waitingq(pcb);
        log_release_error(pcb->page->name, instruction->resource);
    }
    
    // printf("TODO: Implement a function that can release a resource and mark it available in the resources list");
    // printf("if successful call log_release_released, else call log_release_error \n");

    // successful release
    // log_release_released(pcb->page->name, instruction->resource);
    // resource not assigned to process 
    // log_release_error(pcb->page->name, instruction->resource);
}

/**
 * @brief Acquires the resource specified by resourceName.
 *
 * The function iterates over the list of resources trying to acquire the
 * resource specified by resourceName. If the resources is available, the
 * process acquires the resource. The resource is indicated as not available
 * in the resourceList and 1 is returned indicating that the resource has been
 * acquired successfully.
 *
 * @param resourceName The name of the resource to acquire.
 * @param resources The list of resources.
 * @param p The process which acquires the resource.
 *
 * @return 1 for TRUE if the resource is available. 0 for FALSE if the resource
 * is not available.
 */
int acquire_resource(struct pcb_t *pcb, char* resource_name) {
    //printf("TODO: implement a function that can assign resource_name to pcb if the resource is available and mark it as unavailable in the resources list\n");
    struct resource_t **head_rl = &global_resource_list;
    struct resource_t *lastNode = *head_rl;
    char *proc_name = pcb->page->name;
    while(lastNode != NULL) {
        if(strcmp(lastNode->name, resource_name) == 0) {
            if(lastNode->available) {
                while(pcb->resources != NULL) {
                    pcb->resources = pcb->resources->next;
                }
                pcb->resources = lastNode;
                lastNode->proc_name = proc_name;
                lastNode->available = FALSE;
                return TRUE;
            } else {
                return FALSE;
            }
        }
        lastNode = lastNode->next;
    }
    return -1;
}

/**
 * @brief Add process (with pcb proc) to the readyQueue 
 *
 * @param proc The process which must be set to ready.
 */
void process_to_readyq(struct pcb_t *proc) {
    /*printf("TODO: implement a function that can move proc from the waiting queue to the ready queue\n");*/
    proc->state = READY;
    
    /*Add proc to ready queue: */
    struct pcb_t **head_rq = &ready_queue;
    if(*head_rq == NULL){
        *head_rq = proc;
    } else {
        struct pcb_t *lastNode = *head_rq;

        while(lastNode->next != NULL) {
            lastNode = lastNode->next;
        }

        lastNode->next = proc;
    }

    /*Remove proc from waiting queue: */
    struct pcb_t **head_wq = &waiting_queue;
    char* proc_name = proc->page->name;
    //struct pcb_t *temp;
    
    if(strcmp((*head_wq)->page->name, proc_name) == 0) {
        //temp = *head_wq;    /*backup to free the memory*/
        *head_wq = (*head_wq)->next;
        //free(temp);
    } else {
        struct pcb_t *current = *head_wq;
        
        while(current->next != NULL) {
            /*if yes, we need to delete the current->next node*/
            if(strcmp(current->next->page->name, proc_name) == 0) {
                //temp = current->next;
                /*node will be disconnected from the linked list.*/
                current->next = current->next->next;
                //free(temp);
                break;
            } else {
                /*Otherwise, move the current node and proceed*/
                current = current->next;
            }
        }
    }

    log_ready(proc->page->name, ready_queue);
    
}

/**
 * @brief Add process (with pcb proc) to the waitingQueue 
 *
 * @param proc The process which must be set to waiting.
 */
void process_to_waitingq(struct pcb_t *proc) {
    //printf("TODO: implement a function that can move proc from the ready queue to the waiting queue\n");
    proc->state = WAITING;

    /*Add proc to waiting queue: */
    struct pcb_t **head_wq = &waiting_queue;
    if(*head_wq == NULL){
        *head_wq = proc;
    } else {
        struct pcb_t *lastNode = *head_wq;

        while(lastNode->next != NULL) {
            lastNode = lastNode->next;
        }

        lastNode->next = proc;
    }

    /*Remove proc from ready queue: */
    struct pcb_t **head_rq = &ready_queue;
    char* proc_name = proc->page->name;
    //struct pcb_t *temp;
    
    if(strcmp((*head_rq)->page->name, proc_name) == 0) {
        //temp = *head_rq;    /*backup to free the memory*/
        *head_rq = (*head_rq)->next;
        //free(temp);
    } else {
        struct pcb_t *current = *head_rq;
        
        while(current->next != NULL) {
            /*if yes, we need to delete the current->next node*/
            if(strcmp(current->next->page->name, proc_name) == 0) {
                //temp = current->next;
                /*node will be disconnected from the linked list.*/
                current->next = current->next->next;
                //free(temp);
                break;
            } else {
                /*Otherwise, move the current node and proceed*/
                current = current->next;
            }
        }
    }
    
    log_waiting(proc->page->name, waiting_queue);
}

/**
 * @brief Add process with pcb proc to the terminatedQueue 
 *
 * @param proc The process which has terminated 
 */
void process_to_terminatedq(struct pcb_t *proc) {
    //printf("TODO: implement a function that can move proc from the ready queue to the terminated queue\n");
    proc->state = TERMINATED;

    /*Add proc to terminated queue: */
    struct pcb_t **head_tq = &terminated_queue;
    if(*head_tq == NULL){
        *head_tq = proc;
    } else {
        struct pcb_t *lastNode = *head_tq;

        while(lastNode->next != NULL) {
            lastNode = lastNode->next;
        }

        lastNode->next = proc;
    }

    /*Remove proc from ready queue: */
    struct pcb_t **head_rq = &ready_queue;
    char* proc_name = proc->page->name;
    // struct pcb_t *temp;
    
    if(strcmp((*head_rq)->page->name, proc_name) == 0) {
        // temp = *head_rq;    /*backup to free the memory*/
        *head_rq = (*head_rq)->next;
        // free(temp);
    } else {
        struct pcb_t *current = *head_rq;
        
        while(current->next != NULL) {
            /*if yes, we need to delete the current->next node*/
            if(strcmp(current->next->page->name, proc_name) == 0) {
                //temp = current->next;
                /*node will be disconnected from the linked list.*/
                current->next = current->next->next;
                //free(temp);
                break;
            } else {
                /*Otherwise, move the current node and proceed*/
                current = current->next;
            }
        }
    }

    log_terminated(proc->page->name);
}

/**
 * @brief Takes the waiting queue and detects deadlock
 */
struct pcb_t* detect_deadlock() {
    printf("detect_deadlock not implemented\n");

    // if deadlock detected
    log_deadlock_detected();

    return NULL;
}

/**
 * @brief Releases a processes' resources and sets it to its first instruction.
 *
 * Generates release instructions for each of the processes' resoures and forces
 * it to execute those instructions.
 *
 * @param pcb The process chosen to be reset and release all of its resources.
 *
 */
void resolve_deadlock (struct pcb_t *pcb) {
    printf("resolve_deadlock not implemented\n");
}

/**
 * @brief Prints the global list of available resources.
 */
void print_available_resources() {

    struct resource_t *current_resource;
    current_resource = get_available_resources();

    printf("Available:");
    do {

        if (current_resource->available) {
            printf(" %s", current_resource->name);
        }
        current_resource = current_resource->next;

    } while (current_resource != NULL);

    printf("\n");
}

#ifdef DEBUG
void print_process_resources(struct pcb_t *pcb) {
    struct resource_t *resource;
    resource = pcb->resources;

    printf("Resources for pcb %s: ",pcb->page->name);
    while (resource != NULL) {
        printf("%s->", resource->name);
        resource = resource->assigned_to;
    }
    printf("\n");
}

void print_q (struct pcb_t *pcb) {
    while (pcb != NULL) {
        printf(" %s,", pcb->page->name);
        pcb = pcb->q_next;
    }
    printf("\n");
}
#endif

void dealloc_queues()
{       
    dealloc_pcb_list(*ready_queue);
    dealloc_pcb_list(*waiting_queue);
    dealloc_pcb_list(*terminated_queue);
}
