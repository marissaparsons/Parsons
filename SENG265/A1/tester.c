/** pipes & filters program that uses conditionals, loops, and string processing tools in C to process iCalendar
 *  events and printing them in a user-friendly format.
 *   *  @author Felipe R.
 *    *  @author Hausi M.
 *     *  @author Jose O.
 *      *  @author Victoria L.
 *       *  @author Marissa P.
 *        *
 *         */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/**
 *  * @brief The maximum line length.
 *   *
 *    */
#define MAX_LINE_LEN 132

/**
 *  * Function: main
 *   * --------------
 *    * @brief The main function and entry point of the program.
 *     *
 *      * @param argc The number of arguments passed to the program.
 *       * @param argv The list of arguments passed to the program.
 *        * @return int 0: No errors; 1: Errors produced.
 *         *
 *          */
void read_file(char *filename) {
    printf("%s opened\n", filename);
    FILE* input_file = fopen(filename, "r");

    if (input_file == NULL) {
        printf("Unable to open the file for reading\n");
    }

    char line[MAX_LINE_LEN];
    char target_word[] = "END:";

    fgets(line, MAX_LINE_LEN, input_file);

    while( fgets(line, MAX_LINE_LEN, input_file) != NULL) {
        while( fgets(line, MAX_LINE_LEN, input_file) != NULL) {
            printf("The line is [%s]\n", line);
            if (strncmp(line, target_word, strlen(target_word)) == 0) {
                printf("event break\n");
                break;
            }
        create_event_structure(line);
	}
    }


    fclose(input_file);
    printf("%s\n closed", filename);
}

void create_event_structure(char* line) {
	printf("%s\n", line);
}


int main(int argc, char *argv[])
{
   char * start = argv[1];
   char * end = argv[2];
   char * file = argv[3];

   char * start_time = strtok(start, "=");
   start_time = strtok(NULL, "="); 

   char * end_time = strtok(end, "=");
   end_time = strtok(NULL, "=");

   char * filename = strtok(file, "=");
   filename = strtok(NULL, "="); 

   read_file(filename);  

   printf( " %s, %s\n", start, start_time );
   printf( " %s, %s\n", end, end_time );
   printf( " %s, %s\n", file, filename );
   exit(0);
	
}
