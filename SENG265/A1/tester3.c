#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void change_x(int x){
    x=2;
}

void change_y(int* yp) {
    *yp+= 2;
}

void printArray(int arr[], int size) {
    for (int i = 0; i < size; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");
}

void change_array(int array[]) {
    for (int i = 0; i < 5; i++) {
        array[i] += 1;
    }

}

int main() {
    int x = 1;
    int y = 5;
    int * yp = &y;

    printf("x before: %i\n", x);
    printf("y before: %i\n", y);
    printf("*yp before: %i\n", *yp);

    change_x(x);
    change_y(yp);

    printf("x after: %i\n", x);
    printf("y after: %i\n", y);
    printf("*yp after: %i\n", *yp);
    

    int array[5] = {1,2,3,4,5};
    printArray(array, 5);
    change_array(array);
    printArray(array, 5);

    
    
    return 0;
}