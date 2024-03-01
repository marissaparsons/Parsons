/** @file event_manager.c
 *  @brief A pipes & filters program that uses conditionals, loops, and string processing tools in C to process iCalendar
 *  events and printing them in a user-friendly format.
 *  @author Felipe R.
 *  @author Hausi M.
 *  @author Jose O.
 *  @author Victoria L.
 *  @author Marissa P.
 *
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/**
 * @brief The maximum line length.
 *
 */
#define MAX_LINE_LEN 132

/**
 * @brief The maximum amount of events.
 *
 */
#define MAX_EVENT_LEN 500


/**
    * Structure: Event 
    * --------------
    * using param documentation tag for structure members
    * @param date the date which the event occurs as one number YYYYMMDD
    * @param year
    * @param month
    * @param day
    * @param start_time The start time of the event in HHMMSS format using 24 hour time
    * @param end_time The end time of the event in HHMMSS format using 24 hour time 
    * @param location The location of the event
    * @param summary A brief description of the event
*/

struct Event {
    char date[MAX_LINE_LEN];
    int year;
    int month;
    int day;
    char start_time[MAX_LINE_LEN];
    char end_time[MAX_LINE_LEN];
    char location[MAX_LINE_LEN];
    char summary[MAX_LINE_LEN];
};


// Prototypes
int main(int argc, char *argv[]);
void initialize_events(struct Event* events);
void create_repeating_event(char line[], struct Event events[], int * cur_event_pointer);
void create_event_structure(char line[], struct Event * event);
void read_file(char *filename, struct Event events[]);
void convert_date(char * merged_pointer, char * date);
void reduce_array(char * start_date, char * end_date, struct Event print_events[], struct Event events[]);
int print_month(int month, int date_print_len);
int print_date(int year, int day, int date_print_len);
void print_dashes(int amount);
void print_time(char * time);
void print_listed_events(struct Event print_events[]);
void break_args(char *args_array[], char *argv[]);


/**
 * Function: initialize_events
 * --------------------
 * @brief initializes and creates null structures for every structure in the given array
 *
 * @param events The array of Event structures.
 * @return void.
 *
 */

void initialize_events(struct Event* events) {
    for (int event_num = 0; event_num < MAX_EVENT_LEN; event_num++) {
        strcpy(events[event_num].date, "\0");
        events[event_num].year = 0;
        events[event_num].month = 0;
        events[event_num].day = 0;
        strcpy(events[event_num].start_time, "\0");
        strcpy(events[event_num].end_time, "\0");
        strcpy(events[event_num].location, "\0");
        strcpy(events[event_num].summary, "\0");            
    }
}


/**
 * Function: create_repeating_event
 * --------------------
 * @brief creates the structures for recurring events
 *
 * @param line the line that contains the information about a repeating event - RRULE line.
 * @param events The array of Event structures.
 * @param cur_event_pointer pointer to the current event index
 * @return void.
 *
 */

void create_repeating_event(char line[], struct Event events[], int * cur_event_pointer) {
    //breaks up string to sets freq to the str indicator for frequency of event
    line[strcspn(line, "\n")] = '\0';
    char * freq = strtok(line, "=");
    freq = strtok(NULL, ";");

    // checks if frequency is WEEKLY as that is the only condition we will work with
    if (strncmp(freq, "WEEKLY", strlen("WEEKLY")) == 0) {
        //extract date when frequency stops
        char * until = strtok(NULL, ";");
        until = strtok(NULL, "=");
        until = strtok(NULL, "T");
        
        //converts start time and end time of repeats to integers 
        //we know the repeats happen in the same year and month so we can work with the entire string as on large integer
        //the number will only ever change in the 10's and 1's place
        int repeat_start = atoi(events[*cur_event_pointer].date);
        int repeat_end = atoi(until);
        char date[MAX_LINE_LEN];

        //extract repeating data from existing first occurrence
        char * start_time = events[*cur_event_pointer].start_time;
        char * end_time = events[*cur_event_pointer].end_time;
        char * location = events[*cur_event_pointer].location;
        char * summary = events[*cur_event_pointer].summary;
        
        //create and set structures for following repeating occurrences
        for (int index = repeat_start + 7; index <= repeat_end; index += 7) {
            *cur_event_pointer += 1;

            sprintf(date, "%d", index);
            
            strcpy(events[*cur_event_pointer].date, date);


            char year[5];  // Array for first 4 characters
            char month[3];  // Array for next 2 characters
            char day[3];  // Array for last 2 characters
    
            strncpy(year, date, 4);
            year[4] = '\0';
            strncpy(month, date + 4, 2);
            month[2] = '\0';
            strncpy(day, date + 6, 2);
            day[2] = '\0';

            events[*cur_event_pointer].year = atoi(year);
            events[*cur_event_pointer].month = atoi(month);
            events[*cur_event_pointer].day = atoi(day);

            strcpy(events[*cur_event_pointer].start_time, start_time);
            strcpy(events[*cur_event_pointer].end_time, end_time);
            strcpy(events[*cur_event_pointer].location, location);
            strcpy(events[*cur_event_pointer].summary, summary);
        }
    }    
}


/**
 * Function: create_event_structure
 * --------------------
 * @brief creates the structures for events
 *
 * @param line the line that contains the information about an event.
 * @param events The array of Event structures.
 * @return void.
 *
 */

void create_event_structure(char line[], struct Event * event) {
    line[strcspn(line, "\n")] = '\0';

    //seperates the type of information and the information into strings
    char * type = strtok(line, ":");
    char * type_content = strtok(NULL, ":");
    
    //if info type is DTSTART we create 4 members - start_time, year, month, day  
    if (!strcmp(type, "DTSTART")) {
        char * date = strtok(type_content, "T");
        char * time = strtok(NULL, "T");
        strcpy(event->date, date);
        strcpy(event->start_time, time);
    
        char year[5];  // Array for first 4 characters
        char month[3];  // Array for next 2 characters
        char day[3];  // Array for last 2 characters
    
        strncpy(year, date, 4);
        year[4] = '\0';
        strncpy(month, date + 4, 2);
        month[2] = '\0';
        strncpy(day, date + 6, 2);
        day[2] = '\0';

        event->year = atoi(year);
        event->month = atoi(month);
        event->day = atoi(day);
    } 
    else if (!strcmp(type, "DTEND")) {
        char * time = strtok(type_content, "T");
        time = strtok(NULL, "T");
        strcpy(event->end_time, time);
    }
    else if (!strcmp(type, "LOCATION")) {
        strcpy(event->location, type_content);
    }
    else if (!strcmp(type, "SUMMARY")) {
        strcpy(event->summary, type_content);
    } 
}


/**
 * Function: read_file
 * --------------------
 * @brief opens file, reads each line of a file and creates structures based on info in file, closes file
 *
 * @param filename the files name.
 * @param events The array of Event structures.
 * @return void.
 *
 */

void read_file(char *filename, struct Event events[]) {
    
    //printf("%s opened\n", filename);

    //opens file 
    FILE* input_file = fopen(filename, "r");

    //prints statement if file does not exist
    if (input_file == NULL) {
        printf("Unable to open the file for reading\n");
        return;
    }

    char line[MAX_LINE_LEN];                      
    int current_event_number = 0;
    int* cur_event_pointer = &current_event_number;
    
    //variable to determine if we have found a RRULE line
    int r_flag = 0;
    int* r_flag_pointer = &r_flag;
    //string to store the RRULE line if found
    char repeat_line[MAX_LINE_LEN];
    
    fgets(line, MAX_LINE_LEN, input_file);  //read first BEGIN:VCALENDAR... line
    
    //read lines and create structures 
    while( fgets(line, MAX_LINE_LEN, input_file) != NULL && strncmp(line,  "END:VCALENDAR", strlen( "END:VCALENDAR")) != 0) {
        struct Event * event = &events[current_event_number];

        //second while loop to set paramaters for event structure at current events array index
        while( fgets(line, MAX_LINE_LEN, input_file) != NULL) {
            
            //if r_flag was not set and we've reached the end of the events info, break out of while loop 
            if (strncmp(line, "END:", strlen("END:")) == 0 && *r_flag_pointer == 0) {
                break;
            } 

            //if r_flag was set and we've reached the end of the events info, create repeating events and then break out of loop 
            else if (strncmp(line, "END:", strlen("END:")) == 0 && *r_flag_pointer == 1) {
                create_repeating_event(repeat_line, events, cur_event_pointer);
                *r_flag_pointer = 0;
                break;
            }
            
            //if RRULE line is found set r_flag and store line in repeat_line variable
            else if (strncmp(line, "RRULE:", strlen("RRULE:")) == 0) {
                strcpy(repeat_line, line);
                *r_flag_pointer = 1;
            }
           
            // set parameters of event based on info of current line
            create_event_structure(line, event);
        }
        current_event_number++;
    }
    

    fclose(input_file);
    //printf("%s closed \n\n", filename);
}


/**
 * Function: convert_date
 * --------------------
 * @brief converts date in YYYY/MM/DD format into YYYYMMDD string
 *
 * @param merged_pointer The string of the date in YYYYMMDD format.
 * @param date The string of the date in YYYY/MM/DD format.
 * @return void.
 *
 */

void convert_date(char * merged_pointer, char * date) {
    //seperate date string into year, month, day strings
    char * year = strtok(date, "/");
    char * month = strtok(NULL, "/");
    char * day = strtok(NULL, "/");

    //copy year into merged_pointer
    strcpy(merged_pointer, year);
    
    //add month using two numbers into string (ex. month = "1", "01" added, month = "12", "12" added)
    if (strlen(month) == 1) {
        strcat(merged_pointer, "0");
        strcat(merged_pointer, month);
    } else {
        strcat(merged_pointer, month);
    }

    //add day using two numbers into string (ex. day = "1", "01" added, day = "12", "12" added)
    if (strlen(day) == 1) {
        strcat(merged_pointer, "0");
        strcat(merged_pointer, day);
    } else {
        strcat(merged_pointer, day);
    }
}


/**
 * Function: reduce_array
 * --------------------
 * @brief places Event structure from one array that occur between the given start and end times into a secondary array
 *        (the array we will later print)
 *
 * @param start_date The staring date of wanted time period.
 * @param end_date The ending date of wanted time period.
 * @param print_events The array of events which occur between the two given dates.
 * @param events The array of all of our events
 * @return void.
 *
 */

void reduce_array(char * start_date, char * end_date, struct Event print_events[], struct Event events[]) {
    int event_array_index = 0;
    int print_array_index = 0;

    //convert start date into an interger representation in form YYYYMMDD
    char start[strlen(start_date)];
    char * start_pointer = start;
    convert_date(start_pointer, start_date);
    int start_num = atoi(start);

    //convert end date into an interger representation in form YYYYMMDD
    char end[strlen(end_date)];
    char * end_pointer = end;
    convert_date(end_pointer, end_date);
    int end_num = atoi(end);

    //create variable to store the integer representation (YYYYMMDD) of current event dates
    //assign initial value as the date of the first event in events array
    int cur_event_num = atoi(events[event_array_index].date);

    //find first event within the given time period
    while ( cur_event_num < start_num && cur_event_num != 0 ) {
        event_array_index++;
        cur_event_num = atoi(events[event_array_index].date);
    }

    //copy Event structure of events within given time period to print_events array
    while (cur_event_num <= end_num && cur_event_num != 0) {
        print_events[print_array_index] = events[event_array_index];
        print_array_index++;
        event_array_index++;
        cur_event_num = atoi(events[event_array_index].date);
    }

} 


/**
 * Function: print_month
 * --------------------
 * @brief prints integer representation of a month in written english
 *
 * @param month The integer representation of the month.
 * @param date_print_len The integer variable counting the length of the print line.
 * @return int: the length of printed string
 *
 */

int print_month(int month, int date_print_len) {
    if (month == 1) {
        printf("January ");
        date_print_len += 8;
    } else if (month == 2) {
        printf("February ");
        date_print_len += 9;
    } else if (month == 3) {
        printf("March ");
        date_print_len += 6;
    } else if (month == 4) {
        printf("April ");
        date_print_len += 6;
    } else if (month == 5) {
        printf("May ");
        date_print_len += 4;
    } else if (month == 6) {
        printf("June ");
        date_print_len += 5;
    } else if (month == 7) {
        printf("July ");
        date_print_len += 5;
    } else if (month == 8) {
        printf("August ");
        date_print_len += 7;
    } else if (month == 9) {
        printf("September ");
        date_print_len += 10;
    } else if (month == 10) {
        printf("October ");
        date_print_len += 8;
    } else if (month == 11) {
        printf("November ");
        date_print_len += 9;
    } else if (month == 12) {
        printf("December ");
        date_print_len += 9;
    }
    return date_print_len;
}


/**
 * Function: print_date
 * --------------------
 * @brief print the days and then the year in DD, YYYY format
 *
 * @param year The year we want to print.
 * @param day The day we want to print.
 * @param date_print_len The integer variable counting the length of the print line.
 * @return int: the length of printed string
 *
 */

int print_date(int year, int day, int date_print_len) {
    //if day is an integer less than 10 print a 0 in front to keep DD, YYYY print format
    if (day < 10) {
        printf("0%i, %i\n", day, year);
    } else {
        printf("%i, %i\n", day, year);
    }
    return date_print_len += 8;
}


/**
 * Function: print_dashes
 * --------------------
 * @brief print the given amount of dashes in one line
 *
 * @param amount The amount of dashes we want in the line.
 * @return void
 *
 */

void print_dashes(int amount){
    for (int count = 0; count < amount; count++) {
        printf("-");
    }
    printf("\n");
}


/**
 * Function: print_time
 * --------------
 * @brief converts 24 hour time to 12 hour and prints the time in hh:mm AM/PM format 
 *
 * @param time 24 hour string representation of time - needs to have at least 4 characters
 *             first 4 characters will be hhmm 
 * @return void
 *
 */

void print_time(char * time) {
    char hour[3];  // Array for first 2 characters of time which is the hour
    char minute[3];  // Array for next 2 characters of time which is the minutes

    char * time_stamp = "AM";
    
    strncpy(hour, time, 3);
    hour[2] = '\0';
    strncpy(minute, time + 2, 3);
    minute[2] = '\0';

    int hr = 0; //initializing int for hour
    int min = 0;    //initializing int for minutes
    hr = atoi(hour);    //assigning proper value to hr by converting str to int
    min = atoi(minute); //assigning proper value to min by converting str to int

    if (hr > 12) {
        hr = hr - 12;
        if (hr == 24) {
            time_stamp = "AM";  //special condition time stamp as 2400 is 12am
        } else {
            time_stamp = "PM";  //if hour is greater than twelve we change the time stamp to pm
        }
    } else if (hr == 12) {
        time_stamp = "PM";  //special condition time stamp as 1200 is 12pm
    }

    if (hr < 10) {
        printf(" %i:", hr);
    } else {
        printf("%i:", hr);
    }

    if (min < 10) {
        printf("0%i ", min);
    } else {
        printf("%i ", min);
    }
    printf("%s", time_stamp);
}


/**
 * Function: print_listed_events
 * --------------------
 * @brief Takes the print_events array and prints a formatted output of every event
 *        under the date which the event happens underneath.
 *
 * @param print_events The array of Event structures we wish to print.
 * @return void.
 *
 */

void print_listed_events(struct Event print_events[]) {
    int cur_event_index = 0;                                                                                                        
    char * cur_date = "\0";                                                                                                         

    while (strcmp(print_events[cur_event_index].date, "\0") != 0) {
        
        //if the event has a different date than the current date, we print a new date
        if (strcmp(print_events[cur_event_index].date, cur_date) != 0) {                                                            
            int date_print_len = 0;                                                                                                 //holds the count of how many characters are printed when the date is printed (determines how many dashes are needed)
            date_print_len = print_month(print_events[cur_event_index].month, date_print_len);                                      //prints the month and updates date_print_len counter
            date_print_len = print_date(print_events[cur_event_index].year, print_events[cur_event_index].day, date_print_len);     //prints the date and updates date_print_len counter
            print_dashes(date_print_len); 
            cur_date = print_events[cur_event_index].date;
            
        } 

        //print event time, summary and location
        print_time(print_events[cur_event_index].start_time);
        printf(" to ");
        print_time(print_events[cur_event_index].end_time);
        printf(": %s {{%s}}", print_events[cur_event_index].summary, print_events[cur_event_index].location);
        cur_event_index++;
        
        //print statements for formatting
        if (strcmp(print_events[cur_event_index].date, cur_date) != 0) {                                                            
            printf("\n");
        }
        if (strcmp(print_events[cur_event_index].date, "\0") != 0) {
            printf("\n");
        }
    }
}


/**
 * Function: break_args
 * --------------------
 * @brief Takes arguments passed to main and appends the
 *        arguments into args_array.
 *
 * @param args_array The string of arguments stripped of indicators (indicator ex. --start=).
 * @param argv The string of arguments passed to the program.
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
   args_array[3] = "\0";
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
   char* args_array[argc];
   break_args(args_array, argv);                                            //arguments are loaded into args_array by the order they were entered - start, end, filename respectively

   struct Event events[MAX_EVENT_LEN]; 
   initialize_events(events);

   struct Event print_events[MAX_EVENT_LEN];
   initialize_events(print_events);

   read_file(args_array[2], events);                                        //reads file and puts all events into events array as Event structures
   
   reduce_array(args_array[0], args_array[1], print_events, events);        //puts events found in events array between the given start and end in print_events array

   print_listed_events(print_events);                                       //prints formated events between given time period

   
   //code to print all Events structures created of the events in given file 

    /* for (int i = 0; i < 14; i++) {
        printf("%i) %s: ", i, events[i].date);
        printf("%i: %i: %i: ", events[i].year, events[i].month, events[i].day);
        printf("%s: %s: ", events[i].start_time, events[i].end_time);
        printf("%s: %s\n", events[i].location, events[i].summary);  
    }

    printf("\n");

    for (int i = 0; i < 14; i++) {
        printf("%i) %s: ", i, print_events[i].date);
        printf("%i: %i: %i: ", print_events[i].year, print_events[i].month, print_events[i].day);
        printf("%s: %s: ", print_events[i].start_time, print_events[i].end_time);
        printf("%s: %s\n", print_events[i].location, print_events[i].summary); 
    } */
   
   
   exit(0);
}