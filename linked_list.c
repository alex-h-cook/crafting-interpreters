#include<stdio.h>
#include<stdlib.h>


// Doubly linked list of heap allocated strings


typedef struct Node {
        int data;
        struct Node* prev;
        struct Node* next;
    } Node;

Node* createNode(int data)
{
    Node* newNode = (Node*)malloc(sizeof(Node));
    newNode->data = data;
    newNode->next = NULL;
    newNode->prev = NULL;
    return newNode;
}

void insertAtBeginning(Node** head, int data) //Double pointer is used here since head is passed by value, and we need to change where the pointer is pointing, we can't do this without passing the pointer by reference
{
    Node* newNode = createNode(data);
    if (*head == NULL) {
        *head = newNode;
        return;
    }
    newNode->next = *head;
    (*head)->prev = newNode;
    *head = newNode;
}

void insertAtEnd(Node** head, int data)
{
    Node* newNode = createNode(data);
    if (*head == NULL) {
        *head = newNode;
        return;
    }
    Node* temp = *head;
    while (temp->next != NULL) {
        temp = temp->next;
    }
    temp->next = newNode;
    newNode->prev = temp;
}

void insertAtPosition(Node** head, int data, int position)
{
    Node* newNode = createNode(data);
    if (position < 0) {
        printf("Position should be >= 1.\n");
        return;
    }
    if (position == 1) {
        insertAtBeginning(head, data);
        return;
    }
    Node* temp = *head;
    for(int i = 1; temp!=NULL && i < position - 1; i++) {
        temp = temp->next;
    }
    if (temp == NULL) {
        printf("Out of list range");
        return;
    }
    newNode->next = temp->next;
    newNode->prev = temp;
    if (temp->next != NULL) {
        temp->next->prev = newNode;
    }
    temp->next = newNode;
    return;
}

void deleteAtBeginning(Node** head) {
    if (*head == NULL){
        printf("List is already empty.\n");
        return;
    }
    Node* temp = *head;
    *head = (*head)->next;
    if (*head != NULL) {
        (*head)->prev = NULL;
    }
    free(temp);
}

void deleteAtEnd(Node** head) {
    if (*head == NULL) {
        printf("List is already empty.\n");
        return;
    }
    Node* temp = *head;
    if(temp->next == NULL) {
        *head = NULL; //Better to avoid having temp point to freed memory (dangling pointer)
        free(temp);
        return;
    }
    while(temp->next != NULL) {
        temp = temp->next;
    }
    temp->prev->next = NULL;
    free(temp);
}

void deleteAtPosition(Node** head, int position) {
    if (position < 1) {
        printf("Position must be >= 1.\n");
    }
    if (*head == NULL) {
        printf("List is already empty.\n");
        return;
    }
    if (position == 1) {
        deleteAtBeginning(head);
        return;
    }
    Node* temp = *head;
    for(int i=1; i<position && temp!=NULL; i++) { 
       temp = temp->next;
    }
    if(temp == NULL) {
        printf("POsition outside loop range.\n");
        return;
    }
    if(temp->next == NULL) {
        deleteAtEnd(head);
        return;
    }
    temp->prev->next = temp->next;
    temp->next->prev = temp->prev;
    free(temp);
}

int findAtPosition(Node** head, int position) {
    if (position < 1) {
        printf("Position must be >=1");
        return -1;
    }
    Node* temp = *head;
    for(int i=1; temp!=NULL && i<position; i++) {
        temp = temp->next;
    }
    if (temp == NULL) {
        printf("Position outside list range.\n");
        return -1;
    }
    return temp->data;
}

int main() {
    return 0;    
}