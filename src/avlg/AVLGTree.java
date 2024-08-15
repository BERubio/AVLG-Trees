package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author BRANDON RUBIO
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	private class Node{
		private T data;
		Node leftChild, rightChild;

		private int balance;
		private int height;
		
		
		public Node(T data){
			this.data = data;
			leftChild = null;
			rightChild = null;
			height = 0;
			balance = 0; 
		}
	
        // Updates the balance factor of a node
		public void updateBalance() {
			this.balance = findBalance(this);
		}
		
        // Updates the height of a node
		public void updateHeight() {
			this.height = getHeight(this);
		}
	}
	
	private Node root;
	private int maxImbalance, count;

	
    // Rotation methods

	/**
     * Rotates the node's right child to its place.
     * @param target The node to rotate.
     * @return The new root of the subtree.
     */
	private Node rotateRight(Node target) {
		Node temp = target.rightChild;
		target.rightChild = temp.leftChild;
		temp.leftChild = target;
		return temp;
	}
	/**
     * Rotates the node's left child to its place.
     * @param target The node to rotate.
     * @return The new root of the subtree.
     */
	private Node rotateLeft(Node target) {
		Node temp = target.leftChild;
		target.leftChild = temp.rightChild;
		temp.rightChild = target;
		return temp;
	}
	
	/**
     * Performs a right rotation on the left child and then a left rotation on the target node.
     * @param target The node to rotate.
     * @return The new root of the subtree.
     */
	private Node rotateRightLeft(Node target) {
		target.leftChild = rotateRight(target.leftChild);
		target = rotateLeft(target);
		return target;
	}
	/**
     * Performs a left rotation on the right child and then a right rotation on the target node.
     * @param target The node to rotate.
     * @return The new root of the subtree.
     */
	private Node rotateLeftRight(Node target) {
		target.rightChild = rotateLeft(target.rightChild);
		target = rotateRight(target);
		return target;
	}
	
    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */
	
	
	
    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
    	if (maxImbalance < 1) {
    		throw new InvalidBalanceException("Height can't be less than 1.");
    		
    	}
    	root = null;
    	this.maxImbalance = maxImbalance;
    	count = 0;
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
    	// With empty tree, set root 
    	if(root == null) {
        	root = new Node(key);
        	root.updateBalance();
        	root.updateHeight();
        	
        } else {
        	root = insert2(root, key);
        	updateBalanceAndHeight(root);
     
        }
        count++;
    }
  
    private Node insert2(Node insertionNode, T elem) {
    	// Base case: reached a null leaf, insert here
    	if(insertionNode == null) {
    		return new Node(elem);
    	} else if(elem.compareTo(insertionNode.data) < 0) {
    		insertionNode.leftChild = insert2(insertionNode.leftChild, elem);
    		
    		updateBalanceAndHeight(insertionNode);
    		
    		// Rebalance if necessary
    		if(this.findBalance(insertionNode) > maxImbalance) {
    			if(elem.compareTo(insertionNode.leftChild.data) < 0) {		
    				insertionNode = rotateLeft(insertionNode);
    	    	}else {   			
    	    		insertionNode = rotateRightLeft(insertionNode);		
    	    	}
    		}
    		updateBalanceAndHeight(insertionNode);
    		
    	} else if(elem.compareTo(insertionNode.data) > 0) {
    		insertionNode.rightChild = insert2(insertionNode.rightChild, elem);
    		
    		updateBalanceAndHeight(insertionNode);
    		
            // Rebalance if necessary

    		if(this.findBalance(insertionNode) < maxImbalance*-1) {
    			if(elem.compareTo(insertionNode.rightChild.data) > 0) {		
    				insertionNode = rotateRight(insertionNode);
    	    	}else {   			
    	    		insertionNode = rotateLeftRight(insertionNode);		
    	    	}
    		}
    		
    		updateBalanceAndHeight(insertionNode);
    	}	
    	
    	return insertionNode;
    }
    
    private int findBalance(Node n) {		
		return getHeight(n.leftChild) - getHeight(n.rightChild);
	}
	
	private int getHeight(Node n) {
		if(n == null) {
			return -1;
		} else if(n.leftChild == null && n.rightChild == null) {
			return 0;
		} else {
			return Math.max(getHeight(n.leftChild), getHeight(n.rightChild)) + 1;
		}
	}
    
    private void updateBalanceAndHeight(Node curr) {
    	
    	if(curr != null) {
    		curr.updateBalance();
    		curr.updateHeight();
    	}
    	if(curr.leftChild != null) {
    		updateBalanceAndHeight(curr.leftChild);
    	}
    	if(curr.rightChild != null) {
    		updateBalanceAndHeight(curr.rightChild);
    	}
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
        // Check if the tree is empty

    	if(isEmpty()) {
    		throw new EmptyTreeException("The tree is empty");
    	} else {
            // If the key exists in the tree

    		if(this.search(key) != null) {
                // Call the deleteHelper method to handle deletion
    			Node delete = delete2(root, key);
    			
        		root = delete;
        		count--;
        	    return key;
    		}
            // If the key was not found, return null

    		return null;
	    }
    	
    }
    
    
    private Node delete2(Node curr, T key) {
    	
    	if(curr == null) {
    		return null;
    	}
    	
    	if(curr.data.compareTo(key) > 0) {	
    		curr.leftChild = delete2(curr.leftChild, key);	
    		
    	} else if(curr.data.compareTo(key) < 0) {
    		curr.rightChild = delete2(curr.rightChild, key);
    		
    	} else if(curr.data.compareTo(key) == 0){
    		
    		if(curr.leftChild == null && curr.rightChild == null) {
    			curr = null;
    			return curr;
    			
    		} else if(curr.rightChild == null) {
    			curr = curr.leftChild;
    			
    		}else if (curr.leftChild == null) {
    			curr = curr.rightChild;
    			
    		} else {
    			Node successor = findInOrderSuccessor(curr.rightChild);
    			curr.data = successor.data;
    			Node temp = successor;
    		
    			curr.rightChild = delete2(curr.rightChild, temp.data);
    			if(successor.leftChild != null || successor.rightChild != null) {
    				
    				Node nextSuccessor = findInOrderSuccessor(successor.rightChild);
    				successor.data = nextSuccessor.data;
    			}	
    		}
    	}
    	
		updateBalanceAndHeight(root);
		if(Math.abs(findBalance(curr)) > maxImbalance) {
			if(findBalance(curr) < 0) { //rightChild side is heavier
				if(curr.rightChild.balance >= 0) {
	    			curr = rotateLeftRight(curr);	
	    		} else {
	    			curr = rotateRight(curr);
	    		}
			} else { // leftChild side is heavier
				if(curr.leftChild.balance <= 0) {	
					curr = rotateRightLeft(curr);	 	
	    		}else {   			
	    			curr = rotateLeft(curr);	
	    		}
			}
		}
		updateBalanceAndHeight(root);
		return curr;
    	
    }
    

    public Node findInOrderSuccessor(Node curr) {
    	
    	Node successor = curr;
    	while(successor.leftChild != null) {
    		successor = successor.leftChild;
    	}
    	return successor;
    		
    }
    
    private Node getParent(Node current, T key){
    	//If both children of node to be deleted are null, return null
    	if(key == this.root.data) {
    		return null;
    	}
    	
    	//If both children of node to be deleted are null, return null
    	if(current.leftChild == null && current.rightChild == null) {
    		return null;
    	}else if(current.leftChild == null && current.rightChild != null){
    		if(current.rightChild.data.compareTo(key) == 0) {
    			return current;
    		}else {
    			return search3(key, current.rightChild);
    		}
    	}else if(current.leftChild != null && current.rightChild == null){
    		if(current.leftChild.data.compareTo(key) == 0) {
    			return current;
    		}else {
    			return search3(key, current.leftChild);
    		}
    	}else {
    		if(key.compareTo(current.data) < 0){
    			return search3(key, current.leftChild);
    		}else if(key.compareTo(current.data) > 0) {
    			return search3(key, current.rightChild);
    		}else {
    			return current;
    		}
    	}
    }
    
    /*private void deletion(Node current) {
    	
    	Node parent = this.getParent(this.root, current.data);
    	
    	if(current.leftChild == null && current.rightChild == null) {
    		if(current == parent.rightChild) {
    			parent.rightChild = null;
    		}else {
    			parent.leftChild = null;
    		}
    		
    		balance(parent);
    	}else if(current.rightChild == null) {
    		Node replace;
    		replace = current.leftChild;
    		
    		if(parent != null) {
    			if(current == parent.leftChild) {
    				parent.leftChild = replace;
    			}else {
    				parent.rightChild = replace;
    			}
    		}else {
    			this.root = replace;
    		}
    		
    		Node replaceParent = getParent(this.root, replace.data);
    		replaceParent = parent;
    		parent = null;
    		
    		balance(replace);
    	}else if(current.leftChild == null) {
    		Node replace;
    		replace = current.rightChild;
    		
    		if(parent == null) {
    			this.root = replace;
    		}else {
    			if(current == parent.leftChild) {
    				parent.leftChild = replace;
    			}else {
    				parent.rightChild = replace;
    			}
    		}
    		
    		Node replaceParent = getParent(this.root, replace.data);
    		replaceParent = parent;
    		parent = null;
    		
    		balance(replace);
    	}else {
    		Node successor = findSuccessor(current);
    		current.data = successor.data;
    		deletion(successor);
    	}
    }
    
    private Node findSuccessor(Node current) {
    	/*if(current != this.root && current.leftChildChild == null && current.rightChildChild == null) {
    		//found leaf node that is inOrder Successor
    		return current;
    	}else if()
    	if(current.rightChild == null) {
    		return null;
    	}else {
    		if(current.rightChild.leftChild == null) {
    			return current.rightChild;
    		}else {
    			Node temp = current.rightChild.leftChild;
    			while(temp != null) {
    				temp = temp.leftChild;
    			}
    			return temp;
    		}
    	}
    		
    }*/
    

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
    	//System.out.println("search("+key+")");
        // Check if the tree is empty

        if(this.isEmpty()) {
        	throw new EmptyTreeException("Cannot search for data in empty tree");
        }
        //Node searchNode = new Node(key);
        //searchNode.data = key;
        // Call the recursive search method

        return search4(this.root, key);
    }
    
    private T search4(Node curr, T key) {
        // Base case: current node is null, key not found
        if(curr == null) {
            return null;
        }
        
        // If the current node contains the key, return it
        if(curr.data.compareTo(key) == 0) {
            return key;
        }
        
        // Recursively search in the left subtree if key is less than current node
        if(key.compareTo(curr.data) > 0 && curr.rightChild != null) {
            return search4(curr.rightChild, key);
        }
        // Recursively search in the right subtree if key is greater than current node
        if(key.compareTo(curr.data) < 0 && curr.leftChild != null) {
            return search4(curr.leftChild, key);
        }
        return null;
 
    }
    
    private T search2(T target, Node current) {
    	if(current == null) {
    		return null;
    	}else{
    		if(target.compareTo(current.data) < 0){
    			return search2(target, current.leftChild);
    		}else if(target.compareTo(current.data) > 0){
            	return search2(target, current.rightChild);
            }else {
            	return current.data;
            }
    	}
    	
    }
    
    private Node search3(T target, Node current) {
    	if(current == null) {
    		return null;
    	}else{
    		if(target.compareTo(current.data) < 0){
    			return search3(target, current.leftChild);
    		}else if(target.compareTo(current.data) > 0){
            	return search3(target, current.rightChild);
            }else {
            	return current;
            }
    	}
    	
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        return maxImbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
    	return getHeight(root);
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
    	if(root == null) {
    		return true;
    	}
    	return false;
  }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
    	if(this.isEmpty()) {
    		throw new EmptyTreeException("No root in an Empty Tree!");
    	}
        return this.root.data;       
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
        return isBST(this.root);
    }

    private boolean isBST(Node current) {
    	if(current == null) {
    		return true;
    	}
    	
    	if(current.leftChild != null) {
    		if(current.leftChild.data.compareTo(current.data) > 0) return false;
    	}
    	if(current.rightChild != null) {
    		if(current.rightChild.data.compareTo(current.data) < 0) return false;
    	}
    	return isBST(current.leftChild) && isBST(current.rightChild);
    }

    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
        if(this.isEmpty()) {
        	return true;
        }
        return isBalanced(this.root);
    }
    
    private boolean isBalanced(Node current) {
    	if(current == null) {
    		return true;
    	}
    	
    	if(Math.abs(findBalance(current)) > maxImbalance) {
    		return false;
    	}
    	
    	if(current.leftChild != null) {
    		if(Math.abs(findBalance(current.leftChild)) > maxImbalance) return false;
    	}
    	
    	if(current.rightChild != null) {
    		if(Math.abs(findBalance(current.rightChild)) > maxImbalance) return false;
    	}
    	return isBalanced(current.leftChild) && isBalanced(current.rightChild);    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
    	  root = null;
          count = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        return this.count;
    }
}