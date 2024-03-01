#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/**
 * @brief The maximum line length.
 *
 */
#define MAX_LINE_LEN 132

/**
 * @brief The number of arguments passed to the program.
 *
 */
#define ARG_AMOUNT 3

/**
 * @brief The maximum line length.
 *
 */
#define MAX_EVENT_LEN 500


/**
    * Structure: Event 
    * --------------
    * using param documentation tag for structure members
    * @param day the day which the event occurs as one number YYYYMMDD
    * @param start_time The start time of the event in HHMMSS format using 24 hour time
    * @param end_time The end time of the event in HHMMSS format using 24 hour time 
    * @param location The location of the event
    * @param summary A brief description of the event
*/

struct Event {
    char day[MAX_LINE_LEN];
    char start_time[MAX_LINE_LEN];
    char end_time[MAX_LINE_LEN];
    char location[MAX_LINE_LEN];
    char summary[MAX_LINE_LEN];
};

void create_null_struct(int event_num, struct Event *events[]) {
    struct Event event;
    events[event_num] = &event;
    strcpy(events[event_num]->day, "\0");
    strcpy(events[event_num]->start_time, "\0");
    strcpy(events[event_num]->end_time, "\0");
    strcpy(events[event_num]->location, "\0");
    strcpy(events[event_num]->summary, "\0");
    
}

void create_event_structure(char line[], int event_num, struct Event *events[]) {
    char * type = strtok(line, ":");
    char * type_content = strtok(NULL, ":");
    
    if (!strcmp(type, "DTSTART")) {
        struct Event event;
        events[event_num] = &event;
        char * day = strtok(type_content, "T");
        char * time = strtok(NULL, "T");
        strcpy(events[event_num]->day, day);
        printf("%s\n", events[event_num]->day);
        strcpy(events[event_num]->start_time, time);
        printf("%s", events[event_num]->start_time);
    } 
    else if (!strcmp(type, "DTEND")) {
        char * time = strtok(type_content, "T");
        time = strtok(NULL, "T");
        strcpy(events[event_num]->end_time, time);
        printf("%s", events[event_num]->end_time);
    }
    else if (!strcmp(type, "LOCATION")) {
        strcpy(events[event_num]->location, type_content);
        printf("%s", events[event_num]->location);
    }
    else if (!strcmp(type, "SUMMARY")) {
        strcpy(events[event_num]->summary, type_content);
        printf("%s", events[event_num]->summary);
    }
}

/**
    * Function: read_file
    * --------------
    * @brief reads the contents of the given file and initializes a structure for each event in the file.
    *
    * @param filename The filename we want to read passed to the program.
    * @return nothing.
    *
*/

void read_file(char *filename, struct Event *events[]) {
    
    printf("%s opened\n", filename);
    
    FILE* input_file = fopen(filename, "r");
    
    if (input_file == NULL) {
        printf("Unable to open the file for reading\n");
    }
    
    char line[MAX_LINE_LEN];
    char target_word[] = "END:"; 
    char end_statement[] = "END:VCALENDAR";                      //target word marks the starting characters which indicate the end of the current event information
    int current_event_number = 0;
    
    fgets(line, MAX_LINE_LEN, input_file);             //read first BEGIN:VCALENDAR... line
    
    while( fgets(line, MAX_LINE_LEN, input_file) != NULL) {
        //initialize structure
        if (strncmp(line, end_statement, strlen(end_statement)) == 0 ) {
                printf("end of file\n");
                create_null_struct(current_event_number, events);
            }
        //append "/0" to array
        while( fgets(line, MAX_LINE_LEN, input_file) != NULL) {
            //printf("The line is [%s]\n", line);
            if (strncmp(line, target_word, strlen(target_word)) == 0) {
                printf("event break\n\n");
                break;
            }
            create_event_structure(line, current_event_number, events);
        }
        printf("%s\n", events[current_event_number]->summary);
        //append structure to array
        current_event_number++;
    }


    fclose(input_file);
    printf("%s\n closed", filename);
}


/**
 * Function: break_args
 * --------------------
 * @brief Takes the list of arguments passed to main and appends the
 *        arguments into args_array.
 *
 * @param args_array The list of arguments stripped of indicators (indicator ex. --start=).
 * @param argv The list of arguments passed to the program.
 * @return void.
 *
 */

void break_args(char *args_array[], char *argv[]){
   char * start = argv[1];
   char * end = argv[2];
   char * file = argv[3];

   char * start_time = strtok(start, "=");
   start_time = strtok(NULL, "="); 

   char * end_time = strtok(end, "=");
   end_time = strtok(NULL, "=");

   char * filename = strtok(file, "=");
   filename = strtok(NULL, "="); 

   args_array[0] = start_time;
   args_array[1] = end_time;
   args_array[2] = filename;

   //printf( " start: %s\n", start_time );
   //printf( " end: %s\n", end_time );
   //printf( " filename: %s\n", filename );
} 

/**
 * Function: main
 * --------------
 * @brief The main function and entry point of the program.
 *
 * @param argc The number of arguments passed to the program.
 * @param argv The list of arguments passed to the program.
 * @return int 0: No errors; 1: Errors produced.
 *
 */

int main(int argc, char *argv[])
{
   char* args_array[ARG_AMOUNT];
   break_args(args_array, argv);   //arguments are loaded into args_array by the order they were entered - start, end, filename respectively

   printf( " start: %s\n", args_array[0] );
   printf( " end: %s\n", args_array[1] );
   printf( " filename: %s\n", args_array[2] );

   struct Event* events[MAX_EVENT_LEN]; 

   read_file(args_array[2], events); 
   
   exit(0);
}