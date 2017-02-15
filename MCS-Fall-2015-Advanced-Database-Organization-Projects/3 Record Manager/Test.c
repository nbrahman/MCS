#include <stdio.h>
#include <stdlib.h>

int main()
{
	int i=0,j=0,num_elements=3,num_elements_sub=2;
	char **arr = (char**) malloc(num_elements*sizeof(char*));

	for ( i = 0; i < num_elements; i++ )
	{
	    arr[i] = (char*) malloc(num_elements_sub* sizeof(char));
	    arr[i] = "a";  // OR *(*(arr+i)+j) = ++count
	    	         printf("%s ",arr[i]);
	}
	/*for (i = 0; i <  num_elements; i++)
	      for (j = 0; j < num_elements_sub; j++){
	         arr[i] = "a";  // OR *(*(arr+i)+j) = ++count
	         printf("%s ",arr[i]);
	      }*/
	printf("The value is updated\n");
	/*for (i = 0; i <  num_elements; i++){
		      for (j = 0; j < num_elements_sub; j++){
		         printf("%s ",arr[i][j]);//arr[i][j] = '1';  // OR *(*(arr+i)+j) = ++count
		      }
	}*/

	/*for ( i = 0; i < num_elements_sub; i++ )
	{
	    free(arr[i]);
	}

	free(arr);*/
	/*int r = 3, c = 4, i, j, count;

    char **arr = (char **)malloc(r * sizeof(char *));
    for (i=0; i<r; i++)
         arr[i] = (char *)malloc(c * sizeof(char));

    // Note that arr[i][j] is same as *(*(arr+i)+j)
    count = 0;
    for (i = 0; i <  r; i++)
      for (j = 0; j < c; j++)
         arr[i][j] = ++count;  // OR *(*(arr+i)+j) = ++count

    for (i = 0; i <  r; i++)
      for (j = 0; j < c; j++)
         printf("%s ", arr[i][j]);

    Code for further processing and free the
      dynamically allocated memory*/

   return 0;
}/*
#include <stdio.h>
#include <stdlib.h>

void *alloc_array(int x, int y) {
    char *(*a)[y] = calloc(x, sizeof *a);
    return a;
}

int main() {
    char *(*a)[2] = alloc_array(3, 2);
    a[2][1] = "foo";
    printf("%s\n", a[2][1]);
}

#include <stdio.h>
#include <stdlib.h>
int main(){
    char str[100] = "hello\nworld\nstack\noverflow";

    int numWords = 4; // you can handle this part
    char **words = malloc(numWords * sizeof(char *));

    char *tok = strtok(str, "\n");
    int counter = 0;
    while(tok != NULL){
        words[counter] = tok;
        counter++;
        tok = strtok(NULL, "\n");
    }

    printf("%s\n", words[0]); // hello
    printf("%s\n", words[1]); // world
    printf("%s\n", words[2]); // stack
    printf("%s\n", words[3]); // overflow

}
*/
