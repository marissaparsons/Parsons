/** @file music_manager.c
 *  @brief A small program to analyze songs data.
 *  @author Mike Z.
 *  @author Felipe R.
 *  @author Hausi M.
 *  @author Jose O.
 *  @author Victoria L.
 *  @author Marissa P.
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"

/**
 * @brief The maximum line length.
 *
 */
#define MAX_LINE_LEN 180

// Prototypes
int main(int argc, char *argv[]);
void inccounter(node_t *p, void *arg);
void print_node(node_t *p, void *arg);
void analysis(node_t *l);
void output(char * display, char * sort_by, node_t * song_list);
void process_line(char* line, char* sort_by, Song * new_song);
node_t * read_file(char *filename, char *sort_by, node_t * song_list);
void break_args(char *args_array[], char *argv[]);
void free_memory(node_t * list);


/**
 * @brief Serves as an incremental counter for navigating the list.
 *
 * @param p The pointer of the node to print.
 * @param arg The pointer of the index.
 *
 */
void inccounter(node_t *p, void *arg)
{
    int *ip = (int *)arg;
    (*ip)++;
}

/**
 * @brief Allows to print out the content of a node.
 *
 * @param p The pointer of the node to print.
 * @param arg The format of the string.
 *
 */
void print_node(node_t *p, void *arg)
{
    char *fmt = (char *)arg;
    printf(fmt, p->song);
}

/**
 * @brief Allows to print each node in the list.
 *
 * @param l The first node in the list
 *
 */
void analysis(node_t *l)
{
    int len = 0;

    apply(l, inccounter, &len);
    printf("Number of words: %d\n", len);

    apply(l, print_node, "%s\n");
}


/**
 * Function: output
 * --------------------
 * @brief Creates an output.csv file of the top x amount of songs (amount passed to main)
 *        in artist,song,year,comparator(popularity, danceability, or energy) format
 *        (comparator as defined by the sort_by passed to main)
 *
 * @param display The string of the number of top songs wanted.
 * @param sort_by The comparator (popularity, danceability, or energy) we want to sort by.
 * @param song_list The pointer to the first element in our linked list of song structures.
 * @return void.
 *
 */
void output(char * display, char * sort_by, node_t * song_list) {
    //convert display amount to an interger
    int display_amount = atoi(display);
    
    node_t* current = song_list;
    
    //create output file
    FILE* output_file = fopen("output.csv", "w");

    //songs to output file
    fprintf(output_file, "artist,song,year,%s\n", sort_by);
    for (int i = 0; i < display_amount; i++) {
        fprintf(output_file, "%s,%s,%d,", current->song->artist, current->song->song, current->song->year);
        
        //alter amount of decimal points depending on comparator
        if (strcmp(sort_by, "popularity") == 0) {
            fprintf(output_file, "%.0f\n", current->song->comparator);
        } else {
            fprintf(output_file, "%.3g\n", current->song->comparator);
        }
        current = current->next;
    } 

    fclose(output_file);
}

/**
 * Function: process_line
 * --------------------
 * @brief creates structure based on information in given line
 *
 * @param line The line of information to process and create a song structure from.
 * @param sort_by The sorting condition passed to main.
 * @param new_song The pointer to a new song structure.
 * @return void.
 *
 */
void process_line(char* line, char* sort_by, Song * new_song) {
    char * artist = strtok(line, ",");
    strcpy(new_song->artist, artist);
        
    char * song = strtok(NULL, ",");
    strcpy(new_song->song, song);
        
    char * year_str;
    //loops through to find the information in the year position
    for (int i = 0; i < 3; i++) {
        year_str = strtok(NULL, ",");
    }
    int year = atoi(year_str);
    new_song->year = year;

    char * pop_str = strtok(NULL, ",");
    float pop = atof(pop_str);
    char * dance_str = strtok(NULL, ",");
    float dance = atof(dance_str);
    char * energy_str = strtok(NULL, ",");
    float energy = atof(energy_str);

    //sets comparator to the information we want as defined in sort_by
    if (strcmp(sort_by, "popularity") == 0) {
        new_song->comparator = pop;
    } else if (strcmp(sort_by, "danceability") == 0) {
        new_song->comparator = dance;
    } else if (strcmp(sort_by, "energy") == 0) {
        new_song->comparator = energy;
    }
}

/**
 * Function: read_file
 * --------------------
 * @brief reads the given file and creates a song structure from each line
 *
 * @param filename The name of the given file passed to main.
 * @param sort_by The sorting condition passed to main.
 * @param song_list The pointer to the first element in the linked list of song structures
 * @return node_t The pointer to the first element in the linked list of song structures.
 *
 */
node_t * read_file(char *filename, char *sort_by, node_t * song_list) {
    
    //printf("%s opened\n", filename);

    //opens file 
    FILE* input_file = fopen(filename, "r");

    //prints statement if file does not exist
    if (input_file == NULL) {
        printf("Unable to open the file for reading\n");
        return NULL;
    }

    char line[MAX_LINE_LEN];                      
    
    fgets(line, MAX_LINE_LEN, input_file);  //read first line
     
    while( fgets(line, MAX_LINE_LEN, input_file) != NULL){
        struct Song * new_song = malloc(sizeof(struct Song));
        
        //read line and create structure
        process_line(line, sort_by, new_song);

        //printf("%s, %s, %d, %f \n", new_song->artist, new_song->song, new_song->year, new_song->comparator);
        song_list = add_inorder(song_list, new_node(new_song));
    }

    fclose(input_file);
    //printf("%s closed \n\n", filename);
    return song_list;
}


/**
 * Function: break_args
 * --------------------
 * @brief Takes arguments passed to main and appends the
 *        arguments into args_array.
 *
 * @param args_array The string of arguments stripped of indicators (indicator ex. --sort_by=).
 * @param argv The string of arguments passed to the program.
 * @return void.
 *
 */
void break_args(char *args_array[], char *argv[]) {
   char * sort = argv[1];
   char * display = argv[2];
   char * file = argv[3];

   char * sort_by = strtok(sort, "=");
   sort_by = strtok(NULL, "="); 

   char * display_amount = strtok(display, "=");
   display_amount = strtok(NULL, "=");

   char * filename = strtok(file, "=");
   filename = strtok(NULL, "="); 

   args_array[0] = sort_by;
   args_array[1] = display_amount;
   args_array[2] = filename;
   args_array[3] = "\0";
}


/**
 * Function: free_memory
 * --------------------
 * @brief Takes the list passed to the function and frees all of the memory in the list
 *
 * @param node_t the pointer to the first element in the list of song structures.
 * @return void.
 *
 */
void free_memory(node_t * list) {
    node_t *temp_n = NULL;
    for (; list != NULL; list = temp_n)
    {
        temp_n = list->next;
        free(list->song);
        free(list);
    }
}


/**
 * @brief The main function and entry point of the program.
 *
 * @param argc The number of arguments passed to the program.
 * @param argv The list of arguments passed to the program.
 * @return int 0: No errors; 1: Errors produced.
 *
 */
int main(int argc, char *argv[])
{
    //args_array[0] = sort by, args_array[1] = top amount, args_array[2] = filename
    char* args_array[argc];
    break_args(args_array, argv);

    node_t * song_list = NULL;

    //read file and create structures
    song_list = read_file(args_array[2], args_array[0], song_list);

    //create and write to output file
    output(args_array[1], args_array[0], song_list);

    free_memory(song_list);

    exit(0);
}
