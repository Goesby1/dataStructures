package avlg;

import avlg.exceptions.UnimplementedMethodException;

import java.time.chrono.ThaiBuddhistEra;

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
 * @author Yosefe Eshete
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
    private int imbalance;
    private int heightTree; 
    private int elements; 
    Node root; 

    private class Node {
        T key;
        int height; 
        Node left;
        Node right; 

        Node(T val) {
            this.key = val;  
            this.left = null;
            this.right = null;
        }

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
        //throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        this.imbalance = maxImbalance; 
        if (this.imbalance < 1 ) {
            throw new InvalidBalanceException(""); 
        }
        this.root = null;
        this.elements = 0;
        this.heightTree = -1; 
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
       // throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        if (this.root == null) {
            this.root = new Node(key); 
            this.heightTree = 0;
            this.elements = 1;
        }else {
            insertHelper(key);
            this.elements++;
        }
    }
    /** 
     * Find node to enter the new node. Insert the node in proper location. 
     *  @param key The key to insert in the tree. 
     */
    private void insertHelper(T key) {

        Node temp = this.root; 
        Node temp2 = null;
        

        // Find the correct place to place the new node, we know this when temp is null.
        while(temp != null) {

            if (temp.key == null) {
                break;
            }

            if (temp.key.compareTo(key) > 0 ) {
                temp2 = temp; 
                temp = temp.left;
            } else if (temp.key.compareTo(key) < 0 ) {
                temp2 = temp;
                temp = temp.right; 
            }
        }

        // Update the tree height and insert into the tree.
        this.insertUpdateHeight(temp2, key );
        this.balanceTree();

        
    }
    /**
     * Balance the tree.
     * 
     */
    private void balanceTree() {

        int balance;
        int balanceLeft;
        int balanceRight; 

        balance = this.subBalanceNum(this.root);

         // Check to see if tree is balanced
        if (balance > this.imbalance) {

            balanceLeft = this.subBalanceNum(this.root.left);

            if (balanceLeft > this.imbalance - 1) {

                this .root = this.rotateRight(this.root);
            } else if (balanceLeft <  this.imbalance - 1) {

                this.root = this.rotateLeftRight(this.root);
            } else if (balanceLeft == 0) {
                this.root = this.rotateRight(this.root);

            }


        } else if (balance < (-1)*this.imbalance) {


            balanceRight = this.subBalanceNum(this.root.right);
           

            if (balanceRight >  (-1)*this.imbalance + 1) {


                this.root = this.rotateRightLeft(this.root);
            } else if (balanceRight <  (-1)*this.imbalance + 1) {
                

                this.root = this.rotateLeft(this.root);
            } else if (balanceRight == 0) {
                this.root = this.rotateLeft(this.root);

            }

        }
        this.updateHeight(this.root, 0);
        
    }


    /**
     * This method updates the height of the node and also the height of the tree.
     * @param node
     *
     */
    private void insertUpdateHeight(Node node, T key) {

        Node newNode = new Node(key);
        if (node != null) {
            if (node.key.compareTo(key) > 0) {

                if (node.left != null) {
                    node.left.key = key;
                }else {
                    node.left = newNode;
                }
                
                if (node.right == null && this.heightTree <= node.height) {
                    this.heightTree++;
                }
                node.left.height = node.height + 1;

            } else if(node.key.compareTo(key) < 0) {
                
                if (node.right != null) {
                    node.right.key = key;
                } else {
                    node.right = newNode;

                }
                if (node.left == null && this.heightTree <= node.height) {
                    this.heightTree++;
                }
                node.right.height = node.height + 1;
            }
        }
    }
    
    /**
     * Update the height of the tree.
     * @param n, node 
     * @param height, height of current Node
     * 
     */
    private void updateHeight(Node n, int height) {

        if (this.root.key == n.key && this.root.height != 0) {
                this.root.height = 0; 
                this.heightTree = height; 
            
        }
       
        if ((n.left != null  && n.right != null) && ((n.left.key != null  && n.right.key != null))) {
            int max;
            n.left.height = height + 1;
            n.right.height = height + 1;
            max = Math.max(n.left.height, n.right.height);
            this.heightTree = Math.max(this.heightTree, max);
            updateHeight(n.left, n.left.height);
            updateHeight(n.right, n.right.height);

        } else if (n.right != null && n.right.key != null) {
            n.right.height = height + 1;
            this.heightTree = Math.max(this.heightTree, n.right.height);
            updateHeight(n.right, n.right.height);

        } else if (n.left != null && n.left.key != null) {
            n.left.height = height + 1;
            this.heightTree = Math.max(this.heightTree, n.left.height);
            updateHeight(n.left, n.left.height);
        }
        
        

    } 
    /**
     * Rotates the tree or subtree left from the given Node.
     * @param n, the given node 
     */

    private Node rotateLeft(Node n) {

        Node temp = n.right;
        n.right = temp.left;
        temp.left = n; 
        return temp;
    }
    /**
     * Rotates the tree or subtree right from the given Node.
     * @param n, the given node 
     */

    private Node rotateRight(Node n) {
        Node temp = n.left; 
        n.left = temp.right;
        temp.right = n;  
        return temp;
    }

    /**
     * Rotates the tree or subtree left then right from the given Node.
     * @param n, the given node 
     */

    private Node rotateLeftRight(Node n) {

        n.left = this.rotateLeft(n.left);
        n = rotateRight(n);
        return n;  
    }
    /**
     * Rotates the tree or subtree right then left from the given Node.
     * @param n, the given node 
     */

    private Node rotateRightLeft(Node n) {

        n.right = rotateRight(n.right);
        n = rotateLeft(n);
        return n;
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
        //throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!

        Node n; 
        Node nxt;
        T ret; 
        
        if (this.isEmpty() ) {
            throw new EmptyTreeException("");
        }

        n = this.nodeSearch(this.root, key);
        if (n == null ) {
            return null; 
        }

        ret = n.key;
        if (n.left != null || n.right != null) { 
            nxt = this.nodeSearchNext(this.root, key);
            if (nxt != null) {
                n.key = nxt.key;
            } else {
                n.key = null;
            }
        }
        n.key = null;
        this.balanceTree();
        this.elements--;

        return ret;
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
        //throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        Node node = null;

        if (this.isEmpty()) {
            throw new EmptyTreeException("");
        }

        node = this.nodeSearch(this.root, key);
        
        if (node != null) {
            return node.key;
        }

        return null;
    }
    /**
     * Returns the node where the key is located. 
     * @param key 
     * @param node
     * @return node of the key
     */
    private Node nodeSearch(Node node, T key) {

        if (node == null)  {
            return null;
        }

        if (node.key == null) {
            return node;
        }

        if (key.compareTo(node.key) < 0) {
            return nodeSearch(node.left, key);
        } else if (key.compareTo(node.key) > 0 ) {
            return nodeSearch(node.right, key);
        }

        return node;
    }   

    /**
     * Find next biggest 
     * @return
     */
    private Node nodeSearchNext(Node node, T key) {

        if (node == null)  {
            return null;
        }

        if (node.key == null) {
            return node;
        }

        if (node.key.compareTo(key) > 0 ) {
            if (node.left != null && node.left.key == key) {
                return node;
            }
            return nodeSearchNext(node.left, key); 
               
        } else if (node.key.compareTo(key) < 0) {
                return nodeSearchNext(node.right, key);
        }

        return node.right;
    }   


    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        //throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        return this.imbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
        //throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        return this.heightTree; 
    }

    /**  
     * Return height of subtree
     * @param n, subtree node 
     * */
    private int subTreeHeight(Node n) {
        
        int left;
        int right;

        if (n == null) { 
            return -1;
        }

        if (n.key == null) {
            return -1;

        }

        if (n.left == null && n.right == null) {
            return 0;
        }

        left = subTreeHeight(n.left);
        right = subTreeHeight(n.right);
        
        return Math.max(left,right) + 1; 
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
       // throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        
        return (emptyHelper(this.root)) ? true: false ; 
    }

    private boolean emptyHelper(Node n) {

        if (n != null && n.key == null) {

            return emptyHelper(n.left) && emptyHelper(n.right) && true;
        } else if (n != null && n.key != null) {
            return false; 
        }
        
        return true;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
        //throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        if (this.isEmpty()) {
            throw new EmptyTreeException("");
        }
        return root.key; 
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
      //  throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
      return this.isBstHelper(this.root);
    }

    private boolean isBstHelper(Node n) {

         
        if (n != null) {

            if (n.left != null && n.left.key != null) {
                if (n.left.key.compareTo(n.key) < 0) {
                    return this.isBstHelper(n.left) ;
                } else {
                   return false; 
                }

            } else if (n.right != null && n.right.key != null) {
                if (n.right.key.compareTo(n.key) > 0) {
                    return this.isBstHelper(n.right);
                } else {
                   return false; 
                }

            }
        }
        return true;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
        // throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        return (this.subBalanceNum(this.root) > this.imbalance || this.subBalanceNum(this.root) < (-1) * this.imbalance) ? false : true;    
    }
    /**
     * Subtree Balance
     * @param n, subtree node 
     */
    private int subBalanceNum(Node n){

        int leftHeight = this.subTreeHeight(n.left);
        int rightHeight = this.subTreeHeight(n.right) ;
        return leftHeight - rightHeight;
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
       // throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        this.clearHelp(this.root);
    }
    /**
    * Clear the tree.
    * @param n
    */
    private void clearHelp(Node n) {

        if (n == null) {

        } else {
            n.key = null;
            clearHelp(n.left);
            clearHelp(n.right);
        }
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
    //  throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        return this.elements;

    }
    /**
     * Count the elements in the tree.
     * @param n
     */
    private void countHelper(Node n) {

        if (n == null) {

        } else {
            if (n.key != null) {
                this.elements = this.elements + 1;
                countHelper(n.left);
                countHelper(n.right);
            } else {
                
                countHelper(n.left);
                countHelper(n.right);
            }

        }

    }
}
