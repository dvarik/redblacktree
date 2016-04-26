import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Event counter using Red Black Tree
 * @author dhanusha
 *
 */
public class RedBlackEventTree {

	private RedBlackNode root;

	//Nil node used to store the external nodes
	private RedBlackNode nil = new RedBlackNode();

	/**
	 * Constructor : initializes root node to nil for an empty tree
	 */
	public RedBlackEventTree() {
		root = nil;
		root.leftChild = nil;
		root.rightChild = nil;
		root.parent = nil;
	}

	/**
	 * Wrapper method to parse input file with sorted event data
	 * and build RBT
	 * @param filename
	 */
	public void buildTreeFromFile(String filename) {

		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
			int count = readInt(bis);
			
			// Insert first node and set root
			RedBlackNode next = new RedBlackNode(readInt(bis), readInt(bis));
			root = next;
			next.leftChild = nil;
			next.rightChild = nil;
			next.parent = nil;
			
			RedBlackNode tempPtr = root;
			count--;

			//Insert remaining nodes
			for (int i = count; i > 0; i--) {

				tempPtr = buildTree(tempPtr, readInt(bis), readInt(bis));
			}

			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to build RBT in linear time from sorted data
	 * 
	 * @param insertPtr
	 * @param evid
	 * @param count
	 * @return
	 */
	public RedBlackNode buildTree(RedBlackNode insertPtr, int evid, int count) {

		// Keep pointer to rightmost node at every step
		// and insert next event from sorted array as right child using this
		// pointer
		RedBlackNode next = new RedBlackNode(evid, count);
		insertPtr.rightChild = next;
		next.leftChild = nil;
		next.rightChild = nil;
		next.parent = insertPtr;
		next.color = RedBlackNode.Color.RED;

		// Adjust colors will take an amortized cost of O(n) for the
		// sequence of operations
		adjustAfterInsert(next);
		// Update the insert pointer to rightmost child
		insertPtr = next;

		return insertPtr;
	}
	
	/**
	 * Helper method to read in integer data from file
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static int readInt(InputStream in) throws IOException {
	    int ret = 0;
	    boolean digit = false;

	    for (int c = 0; (c = in.read()) != -1; ) {
	        if (c >= '0' && c <= '9') {
	            digit = true;
	            ret = ret * 10 + c - '0';
	        } else if (digit) break;
	    }
	    return ret;
	}

	/**
	 * Insert a new node into Red Black Tree
	 * @param nodeToInsert
	 */
	private void insertNode(Event newEvent) {
		
		RedBlackNode nodeToInsert = new RedBlackNode(newEvent.eventId,newEvent.count);

		RedBlackNode insertPtr = root;
		RedBlackNode tempParent = nil;

		// Find the proper place to insert new node
		// by comparing event ids and going into left/right subtree accordingly
		while (!isNil(insertPtr)) {
			tempParent = insertPtr;
			if (nodeToInsert.eventId - insertPtr.eventId < 0) {
				insertPtr = insertPtr.leftChild;
			} else {
				insertPtr = insertPtr.rightChild;
			}
		}

		nodeToInsert.parent = tempParent;

		if (isNil(tempParent)) {
			root = nodeToInsert;
		} else if (nodeToInsert.eventId - tempParent.eventId < 0) {
			tempParent.leftChild = nodeToInsert;
		} else {
			tempParent.rightChild = nodeToInsert;
		}
		// Insert the new node as red node
		nodeToInsert.leftChild = nil;
		nodeToInsert.rightChild = nil;
		nodeToInsert.color = RedBlackNode.Color.RED;
		
		// Adjust the colors of Red Black Tree 
		// if insertion caused red-red RBT property violations
		adjustAfterInsert(nodeToInsert);
		
	}

	/**
	 * Correct RedBlackTree property violations that have occurred
	 * after new node insert
	 * 
	 * @param newNode
	 */
	private void adjustAfterInsert(RedBlackNode newNode) {

		RedBlackNode uncle = nil;
		boolean parentIsLeftChild = true;

		// There is a red-red violation between new node and parent
		while (newNode.parent.color == RedBlackNode.Color.RED) {

			// parent of inserted node is left child of its parent
			if (newNode.parent == newNode.parent.parent.leftChild) {
				uncle = newNode.parent.parent.rightChild;
				parentIsLeftChild = true;
			}
			// parent of inserted node is right child of its parent
			else {
				uncle = newNode.parent.parent.leftChild;
				parentIsLeftChild = false;
			}

			switch (uncle.color) {

			case RED:
				// Recolor parent and uncle to black and grandparent to red.
				// Problem gets pushed up to level of grandparent, assign that
				// as new node and continue
				newNode.parent.color = RedBlackNode.Color.BLACK;
				uncle.color = RedBlackNode.Color.BLACK;
				newNode.parent.parent.color = RedBlackNode.Color.RED;
				newNode = newNode.parent.parent;
			break;

			case BLACK:

				if (parentIsLeftChild) {

					if (newNode == newNode.parent.leftChild) {
						// LL case: new node is left child of left child
						// recolor and right rotate around the grandparent -> DONE
						newNode.parent.color = RedBlackNode.Color.BLACK;
						newNode.parent.parent.color = RedBlackNode.Color.RED;
						rightRotate(newNode.parent.parent);

					} else {
						// LR case: new node is right child of left child
						// left rotate around parent to convert to above LL case
						newNode = newNode.parent;
						leftRotate(newNode);
					}
				} else {

					if (newNode == newNode.parent.rightChild) {
						// RR case: new node is right child of right child
						// recolor and left rotate around grandparent -> DONE
						newNode.parent.color = RedBlackNode.Color.BLACK;
						newNode.parent.parent.color = RedBlackNode.Color.RED;
						leftRotate(newNode.parent.parent);
					} else {
						// RL case: new node is left child of right child
						// right rotate around parent to convert to above case
						newNode = newNode.parent;
						rightRotate(newNode);
					}
				}
			break;
			}
		}
		
		// Color root black - safety check
		root.color = RedBlackNode.Color.BLACK;
	}

	/**
	 * Performs a left rotate around node 'node'
	 * @param node
	 */
	private void leftRotate(RedBlackNode node) {
		
		RedBlackNode rightChild = node.rightChild;
		node.rightChild = rightChild.leftChild;
		
		if(!isNil(rightChild.leftChild)){
			rightChild.leftChild.parent = node;
		}
		
		rightChild.parent = node.parent;
		
		if(isNil(node.parent)){
			root = rightChild;
		}
		else if(node == node.parent.leftChild){
			node.parent.leftChild = rightChild;
		}
		else{
			node.parent.rightChild = rightChild;
		}
		
		rightChild.leftChild = node;
		node.parent = rightChild;

	}
	
	/**
	 * Performs right rotate around node 'node'
	 * @param node
	 */
	private void rightRotate(RedBlackNode node) {

		RedBlackNode left = node.leftChild;
		node.leftChild = left.rightChild;

		if (!isNil(left.rightChild)) {
			left.rightChild.parent = node;
		}

		left.parent = node.parent;
		left.rightChild = node;
		
		if (isNil(node.parent)) {
			root = left;
		}
		else if (node == node.parent.leftChild) {
			node.parent.leftChild = left;
		} else {
			node.parent.rightChild = left;
		}
		node.parent = left;
	}

	/**
	 * Searches RedBlackTree for node by event id
	 * @param event
	 * @return - desired node, or null if not found
	 */
	private RedBlackNode findNode(Event event) {
		
		RedBlackNode eventNode = new RedBlackNode(event.eventId,event.count);
		
		//Empty tree
		if (isNil(root)) {
            return null;
        }
		
		// Initialize search pointer to the root
		RedBlackNode searchPtr = root;
		
		while(!isNil(searchPtr)){
			
			if(searchPtr.eventId - eventNode.eventId == 0)
				return searchPtr;
			
			//Enter right subtree if current node's event is less than desired event value
			if(searchPtr.eventId - eventNode.eventId < 0){
				searchPtr = searchPtr.rightChild;
			}
			//Enter left subtree if current node's event is greater
			else {
				searchPtr = searchPtr.leftChild;
			}
		}
		// Event node not found
		return null;
	}

	/**
	 * Delete specified RedBlackNode from tree
	 * @param nodeToDel
	 */
	private void deleteNode(RedBlackNode nodeToDel){
		
		RedBlackNode childOfDeletedNode = nil;
		RedBlackNode delPtr = nil;
		
		if(isNil(nodeToDel.leftChild) || isNil(nodeToDel.rightChild)){
			// The node to be deleted has only one child
			// Remove nodeToDel node
			delPtr = nodeToDel;
		}
		else {
			// The node to be deleted has 2 children
			// Remove successor (min in right subtree) node of nodeToDel
			delPtr = nextMinNodeInSubtree(nodeToDel);
		}
		
		// Node selected to be physically deleted from above will have only 1 child
		if(!isNil(delPtr.leftChild)){
			childOfDeletedNode = delPtr.leftChild;
		}
		else {
			childOfDeletedNode = delPtr.rightChild;
		}
		childOfDeletedNode.parent = delPtr.parent;
		
		if(isNil(delPtr.parent)){
			// If deleted node has no parent, its child is the new root
			root = childOfDeletedNode;
		}
		else if (delPtr == delPtr.parent.leftChild){
			delPtr.parent.leftChild = childOfDeletedNode;
		}
		else if (delPtr == delPtr.parent.rightChild){
			delPtr.parent.rightChild = childOfDeletedNode;
		}
		
		// If physically deleted node was successor node, 
		// transplant value of successor into the node we wanted to delete
		if(delPtr != nodeToDel){
			nodeToDel.eventId = delPtr.eventId;
			nodeToDel.count = delPtr.count;
		}
		
		//If deleted node was black, readjust RBT to maintain properties
		if(delPtr.color == RedBlackNode.Color.BLACK){
			adjustAfterDelete(childOfDeletedNode);
		}
		
	}
	
	/**
	 * If deleted node is black, adjust tree 
	 * to have equal number of black nodes on all paths
	 * @param node - child of deleted node
	 */
	private void adjustAfterDelete(RedBlackNode node) {
		
		RedBlackNode sibling;

		boolean siblingIsLeftChild;

		while (node != root && node.color == RedBlackNode.Color.BLACK) {

			// Check if node is left or right child of its parent
			// Set sibling node accordingly
			if (node == node.parent.leftChild) {
				siblingIsLeftChild = false;
				sibling = node.parent.rightChild;
			} else {
				siblingIsLeftChild = true;
				sibling = node.parent.leftChild;
			}

			// case 1: sibling is RED.
			if (sibling.color == RedBlackNode.Color.RED) {

				// recolor and rotate around parent --> converts to case 2 or 3
				sibling.color = RedBlackNode.Color.BLACK;
				node.parent.color = RedBlackNode.Color.RED;

				if (siblingIsLeftChild) {
					rightRotate(node.parent);
					sibling = node.parent.leftChild;
				} else {
					leftRotate(node.parent);
					sibling = node.parent.rightChild;
				}
			}

			// case 2: sibling is BLACK and both its children are BLACK
			if ((sibling.leftChild.color == RedBlackNode.Color.BLACK) && 
					(sibling.rightChild.color == RedBlackNode.Color.BLACK)) {
				// recolor and push up the problem to parent node.
				sibling.color = RedBlackNode.Color.RED;
				node = node.parent;
			}

			// case 3: sibling is BLACK and has atleast one RED child
			else {
				if (siblingIsLeftChild) {
					// LR case: sibling is left child and its only red child is a right child
					// recolor, left rotate around sibling --> converts to LL case
					if (sibling.leftChild.color == RedBlackNode.Color.BLACK) {
						sibling.color = RedBlackNode.Color.RED;
						sibling.rightChild.color = RedBlackNode.Color.BLACK;
						leftRotate(sibling);
						sibling = node.parent.leftChild;
					}
					// LL case: sibling is left child and its red child is a left child
					// recolor, right rotate around parent, DONE.
					sibling.leftChild.color = RedBlackNode.Color.BLACK;
					sibling.color = node.parent.color;
					node.parent.color = RedBlackNode.Color.BLACK;
					rightRotate(node.parent);
					node = root;
				} else {
					// RL case: sibling is right child and its only red child is a left child
					// recolor, right rotate around sibling --> converts to RR case
					if (sibling.rightChild.color == RedBlackNode.Color.BLACK) {
						sibling.color = RedBlackNode.Color.RED;
						sibling.leftChild.color = RedBlackNode.Color.BLACK;
						rightRotate(sibling);
						sibling = node.parent.rightChild;
					}
					// RR case: sibling is right child and its red child is a right child
					// recolor, left rotate around parent, DONE.
					sibling.rightChild.color = RedBlackNode.Color.BLACK;
					sibling.color = node.parent.color;
					node.parent.color = RedBlackNode.Color.BLACK;
					leftRotate(node.parent);
					node = root;
				}
			}
		}

		/*
		 *  If deleted node's child was red it would not have entered while loop 
		 *  and RBT could be fixed by simply setting it to black.
		 *  Also in case 2 in while loop, if parent was red, it would exit the loop,
		 *  so we set it to black here to ensure rbt properties are held in all cases
		 */
		node.color = RedBlackNode.Color.BLACK;
	}

	/**
	 * Finds the next minimum node after 'node' in subtree
	 * that is, retrieves successor of this 'node'
	 * @param node
	 * @return
	 */
	private RedBlackNode nextMinNodeInSubtree(RedBlackNode node) {

		// Traverse left nodes of right subtree of 'node'
		// Stop when you reach leftmost node
		RedBlackNode rightTree = node.rightChild;
		while (!isNil(rightTree.leftChild)) {
			rightTree = rightTree.leftChild;
		}
		return rightTree;
	}
	
	/**
	 * Checks RedBlackNode node to see whether it is nil node
	 * 
	 * @param node
	 * @return true if node is nil else false
	 */
	private boolean isNil(RedBlackNode node) {
		return node == nil;
	}
	
	/**
	 * Finds the number of nodes whose event Id's are in the range
	 * (id1,id2) both inclusive by calling recursive function countInRange
	 * 
	 * @param id1
	 * @param id2
	 * @return
	 */
	public int inRange(int id1, int id2){
		
		RedBlackNode rootPtr = root;
		return countInRange(rootPtr, new Event(id1, 0), new Event(id2, 0));
		
	}
	
	/**
	 * Returns count of number of nodes between start and end, both inclusive
	 * by comparing their event ids
	 * 
	 * @param rootPtr - initially points to root of tree
	 * @param start - start node
	 * @param end - end node
	 * @return
	 */
	private int countInRange(RedBlackNode rootPtr, Event start, Event end){
		
		// Stopping condition
	    if (isNil(rootPtr)){
	    	return 0;
	    }
	 
		// If current node is in range, then increment count by 1 and
		// call countInRange for its children
		if ((rootPtr.eventId - end.getEventId() <= 0) && (rootPtr.eventId - start.eventId >= 0)) {
			return rootPtr.count + countInRange(rootPtr.leftChild, start, end) + countInRange(rootPtr.rightChild, start, end);
		}
	 
	    // If current node is smaller than start, enter its right subtree and count
	    else if (rootPtr.eventId - start.eventId < 0){
	    	 return countInRange(rootPtr.rightChild, start, end);
	    }
	 
	    // Else enter left subtree and count
	    else return countInRange(rootPtr.leftChild, start, end);
		
	}
	
	/**
	 * Increase count of node with event id 'evId' by 'count'
	 * If node not present, insert it
	 * 
	 * @param evId
	 * @param count
	 * @return - the new count of node
	 */
	public int increase(int evId, int count){
		
		Event event = new Event(evId, count);
		RedBlackNode node = findNode(event);
		if(node == null){
			//node does not exist, insert it
			insertNode(event);
			return count;
		}
		else {
			//node exists, increment count
			node.count = node.count + count;
			return node.count;
		}
	}
	
	/**
	 * Reduce count of node with event Id 'evId' by 'count'
	 * Delete node if count drops to 0
	 * 
	 * @param evId
	 * @param count
	 * @return - the new count of node, or 0 if node doesn't exist/was removed
	 */
	public int reduce(int evId, int count){
		
		Event event = new Event(evId, count);
		RedBlackNode node = findNode(event);
		if(node == null){
			//node does not exist
			return 0;
		}
		else {
			//node exists, reduce count and delete if count drops to 0
			int currCount = node.count;
			if(currCount<= count){
				deleteNode(node);
				return 0;
			}
			else{
				node.count = node.count - count;	
				return node.count;
			}
		}
		
	}
	
	/**
	 * Gets node with next greater event Id after 'eventId'
	 * @param eventId
	 * @return next node or (0,0) if doesn't exist
	 */
	public Event next(int eventId){
		
		Event event = new Event(eventId, 0);
		RedBlackNode rootPtr = root;
		RedBlackNode tempLargest = null;
		
		while (!isNil(rootPtr)) {
			//If this node's event id is <= 'eventId', go into its right subtree
			if (rootPtr.eventId - event.eventId <= 0) {
				rootPtr = rootPtr.rightChild;
			} else {
				//This node's event id is greater than 'eventId', save it in 'tempLargest'
				//and go into its left subtree to check if further
				//there is an event id smaller than this one but larger than 'eventId'
				tempLargest = rootPtr;
				rootPtr = rootPtr.leftChild;
			}
		}
		if(tempLargest == null){
			return new Event(0, 0);
		}
		return new Event(tempLargest.eventId, tempLargest.count);
	}
	

	/**
	 * Gets node with next smaller event id after 'eventId'
	 * @param eventId
	 * @return previous node or (0,0) if doesn't exist
	 */
	public Event prev(int eventId) {
		
		Event event = new Event(eventId, 0);
		RedBlackNode rootPtr = root;
		RedBlackNode tempSmallest = null;
		
		while (!isNil(rootPtr)) {

			if (rootPtr.eventId - event.getEventId() < 0) {
				//If this node's event id is less than 'eventId', save it in 'tempSmallest'
				//and go into its right subtree to check if further
				//there is an event id larger than this one but smaller than 'eventId'
				tempSmallest = rootPtr;
				rootPtr = rootPtr.rightChild;
			} else {
				// This node's event id is larger, go into its left subtree
				rootPtr = rootPtr.leftChild;
			}
		}
		if(tempSmallest == null){
			return new Event(0, 0);
		}
		return new Event(tempSmallest.eventId, tempSmallest.count);
	}


	/**
	 * Find node with event id 'evId' and return its count
	 * @param evId
	 */
	public int count(int evId){
		
		Event node = new Event(evId, 0);
		RedBlackNode foundNode = findNode(node);
		if(foundNode == null){
			return 0;
		}
		else{
			return foundNode.count;
		}
	}
	
}
