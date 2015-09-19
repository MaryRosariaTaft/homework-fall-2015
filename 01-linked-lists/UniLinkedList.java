import java.io.*;
import java.util.*;

public class UniLinkedList<E>{

    //note: 'head' is a dummy node
    private Node<E> head = new Node(null);
    private int size = 0;

    //this is much more involved than anticipated
    //
    //'cursor' here is used basically as an Iterator class which
    //acts upon the argument 'obj'
    //
    //NOTE: after reading about nested static classes, I realize (I believe...)
    //that I could've used *separate* cursors for EACH 'this' and 'that'
    //and then had code with parallel structure (meaning I could've iterated
    //through both LinkedLists the *same way,* rather than iterating through
    //Nodes directly for 'this' while using 'next()' for 'that')
    //
    //However, this works, so I'm gonna let it be
    public boolean equals(Object obj){
	//check if this and obj are the same reference
	if(this == obj)
	    return true;
	//check if obj is of type UniLinkedList
	if(obj instanceof UniLinkedList){
	    //cast 'that' to be a UniLinkedList
	    UniLinkedList that = (UniLinkedList)obj;
	    //avoid issues with empty lists
	    if(this.isEmpty() || that.isEmpty()){
		//both are empty:
		if(this.isEmpty() && that.isEmpty())
		    return true;
		//only one is empty:
		return false;
	    }
	    //check list equality for non-empty lists
	    //'cursor' will be used to track elements in 'that'
	    Cursor cursor = new Cursor(that);
	    cursor.next();
	    //'current' will be used to track elements in this
	    Node current = head.getNext();
	    while(current.hasNext() && cursor.hasNext()){
		Object tmp1 = current.getData();
		current = current.getNext();
		Object tmp2 = cursor.next(); //automatically increments cursor's position
		//if there's an inequality of data:
		if(!tmp1.equals(tmp2)){
		    return false;
		}
	    }
	    if(current.hasNext() || cursor.hasNext()){
		//if one list still has remaining elements
		return false;
	    }
	    //neither list has remaining elements and all
	    //elements contained are equivalent, in order
	    return true;
	}
	//if obj is not even of type UniLinkedList
	return false;
    }

    public boolean isEmpty(){
	return head.getNext() == null;
	//or return size == 0;
    }

    public void clear(){
	head.setNext(null);
	size = 0;
    }

    public int size(){
	return size;
    }

    public int indexOf(E element){
	Node<E> current = head;
	int index = -1;
	while(current.hasNext()){
	    current = current.getNext();
	    index++;
	    if(current.getData().equals(element)){
		return index;
	    }
	}
	return -1;
    }

    public boolean contains(E element){
	//would be more efficient without the extra method call, but...
	return indexOf(element) != -1;
    }

    //in what case would this return false?
    public boolean add(E element){
	Node<E> current = head;
	while(current.hasNext()){
	    current = current.getNext();
	}
	current.setNext(new Node(element));
	size++;
    	return true;
    }

    public boolean addAfter(E mark, E elementToAdd){
	Node<E> current = head;
	while(current.hasNext()){
	    current = current.getNext();
	    if(current.getData().equals(mark)){
		Node<E> nodeToAdd = new Node<E>(elementToAdd);
		nodeToAdd.setNext(current.getNext());
		current.setNext(nodeToAdd);
		size++;
		return true;
	    }
	}
    	return false;
    }

    //in what case would this return false?
    public boolean addFirst(E element){
	Node<E> nodeToAdd = new Node<E>(element);
	nodeToAdd.setNext(head.getNext());
	head.setNext(nodeToAdd);
	size++;
    	return true;
    }

    public E head(){
    	return head.getData();
    }

    public boolean remove(E element){
	if(size == 0)
	    return false;
	Node<E> current = head;
	Node<E> preceding;
	while(current.hasNext()){	
	    preceding = current;
	    current = current.getNext();
	    if(current.getData().equals(element)){
		preceding.setNext(current.getNext());
		size--;
		return true;
	    }
	}
    	return false;
    }

    //would be more useful if return type were
    //an int indicating the num times element was removed
    public boolean removeAll(E element){
	if(!contains(element))
	    return false;
	while(contains(element)){
	    remove(element);
	}
	return true;
    }

    //*maintains* the FIRST instance of a Node with any given element
    //thus I didn't factor out code by using the remove() method, which
    //*removes* the first instance of aforementioned Node
    //(except in the final case, because if the two Nodes of equivalent data
    //are *adjacent,* it effectively doesn't matter which of the two is removed)
    public void deduplicate(){
	if(size < 2)
	    return;
	Node<E> current = head.getNext();
	Node<E> preceding = head;
	while(current.hasNext()){
	    if(alreadyHas(current)){
		preceding.setNext(current.getNext());
		current = current.getNext();
		size--;
	    }else{
		preceding = current;
		current = current.getNext();
	    }
	}
	if(current.getData().equals(preceding.getData()))
	    remove(current.getData());
	return;
    }

    //helper to deduplicate()
    private boolean alreadyHas(Node<E> node){
	Node<E> checker = head.getNext();
	while(checker != node){
	    if(checker.getData().equals(node.getData()))
		return true;
	    checker = checker.getNext();
	}
	return false;
    }

    @Override
    public int hashCode(){
	return head != null ? head.hashCode() : 0;
    }

    //should this also have the @Override annotation?
    public String toString(){
	String str = "";
	if(size == 0) return "[[empty]]";
	Node<E> current = head.getNext();
	while(current.hasNext()){
	    str += current.getData() + " -> ";
	    current = current.getNext();
	}
	str += current.getData();
	return str;
    }
    
    //NOTE ABOUT STATIC NESTED CLASSES (from http://docs.oracle.com/javase/tutorial/java/javaOO/nested.html):
    //"A static nested class interacts with the instance members of its outer class (and other classes) just like any other top-level class. In effect, a static nested class is behaviorally a top-level class that has been nested in another top-level class for packaging convenience."

    private static class Node<E>{	
	private E data;
	private Node<E> next;
	private Node(E data){this.data = data; next = null;}
	private E getData(){return data;}
	private void setData(E data){this.data = data;}
	private boolean hasNext(){return next != null;}
	private Node<E> getNext(){return next;}
	private void setNext(Node<E> next){this.next = next;}
	@Override
	public int hashCode(){
	    int result = data != null ? data.hashCode() : 0;
	    return 31 * result + (next != null ? next.hashCode() : 0);
	}
	//could've made life easier by putting a toString() in here, but
	//it's not in the API, so I hesistated to add a public method
    }
    
    public static class Cursor<E>{
	private Node<E> position;
	private Cursor(UniLinkedList<E> list){position = list.head;}
	public boolean hasNext(){return position.hasNext();}
	//next() changes 'position' to the next Node *but* returns data of the *original*
	//(based on the way next() works in the Iterator class)
	public E next(){
	    if(hasNext()){
		E tmp = position.getData();
		position = position.getNext();
		return tmp;
	    }
	    System.out.println("in next() in class Cursor: already at last element; returning data of last element without changing position");
	    return position.getData();
	    //probably should've thrown an exception here instead of returning--whoops
	    //but I chose to just return the last element because it makes iteration
	    //very easy, as implemented in my UniLinkedList's equals() method
	}
    }

}
