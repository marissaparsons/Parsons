#include <stdio.h>
#include <string.h>

#define MAX_EVENTS 10

struct Event {
    int day;
    char description[100];
};

void initializeEvents(struct Event* events, int numEvents) {
    for (int i = 0; i < numEvents; i++) {
        events[i].day = 0;                           // Initialize day to 0
        strcpy(events[i].description, "");           // Initialize description as an empty string
    }
}

void printEvents(struct Event* events, int numEvents) {
    for (int i = 0; i < numEvents; i++) {
        printf("Event %d:\n", i + 1);
        printf("Day: %d\n", events[i].day);
        printf("Description: %s\n", events[i].description);
        printf("\n");
    }
}

void changeDescription(struct Event * event) {
    strcpy(event->description, "She is so smart.");
}

void changeDay(struct Event * event) {
    event->day += 5;
}

void changeEvents(struct Event events[], int numEvents) {
    for (int i = 0; i < numEvents; i++) {
        struct Event * event = &events[i];
        changeDay(event);
        changeDescription(event);
    }
}

int main() {
    struct Event events[MAX_EVENTS];

    int numEvents = 5;
    initializeEvents(events, numEvents);
    printEvents(events, numEvents);

    

    changeEvents(events, numEvents);
    printEvents(events, numEvents);

    return 0;
}