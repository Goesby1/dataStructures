package bpt;

import bpt.UnimplementedMethodException;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.internal.builders.NullBuilder;

/**
 * <p>{@code BinaryPatriciaTrie} is a Patricia Trie over the binary alphabet &#123;	 0, 1 &#125;. By restricting themselves
 * to this small but terrifically useful alphabet, Binary Patricia Tries combine all the positive
 * aspects of Patricia Tries while shedding the storage cost typically associated with tries that
 * deal with huge alphabets.</p>
 *
 * @author Yosefe Eshete
 */
public class BinaryPatriciaTrie {

    /* We are giving you this class as an example of what your inner node might look like.
     * If you would prefer to use a size-2 array or hold other things in your nodes, please feel free
     * to do so. We can *guarantee* that a *correct* implementation exists with *exactly* this data
     * stored in the nodes.
     */
    private static class TrieNode {
        private TrieNode left, right, parent;
        private String str;
        private boolean isKey;

        // Default constructor for your inner nodes.
        TrieNode() {
            this("", false);
        }

        // Non-default constructor.
        TrieNode(String str, boolean isKey) {
            
            parent = left = right = null;
            this.str = str;
            this.isKey = isKey;
        }
    }

    private TrieNode root;
    private TrieNode nxt;
    private ArrayList<TrieNode> stck;

    private int size;

    /**
     * Simple constructor that will initialize the internals of {@code this}.
     */
    public BinaryPatriciaTrie() {
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
        root = new TrieNode();
        size = 0; 
    }

    /**
     * Searches the trie for a given key.
     *
     * @param key The input {@link String} key.
     * @return {@code true} if and only if key is in the trie, {@code false} otherwise.
     */
    public boolean search(String key) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
        char firstChar; 
        int plc;
        TrieNode curr = root; 
        String temp = ""; 
    
        while (true) {
            
            if (key.isEmpty() && curr.isKey && temp.length() == curr.str.length()) {
                return true;
            } else if (key.isEmpty()) {
                break;
            }

            firstChar = key.charAt(0);
            plc = 0; 

            if (firstChar == '0') {
                if (curr.left != null) { 
                    for (int i = 0; i < Math.min(curr.left.str.length(),key.length()); i++) {
                        if ( curr.left.str.charAt(i) == key.charAt(i) ) {
                            plc++;
                        } else {
                            break;
                        }
                    }
                }
                temp = key.substring(0, plc);
                key = key.substring(plc);

                if (plc == 0) {
                    break;
                } else {
                    curr = curr.left;
                }
            } 
            
            else {
                if (curr.right != null) { 
                    for (int i = 0; i < Math.min(curr.right.str.length(),key.length()); i++) {
                        if (curr.right != null && curr.right.str.charAt(i) == key.charAt(i) ) {
                            plc++;
                        } else {
                            break;
                        }
                    }
                }
                temp = key.substring(0, plc);
                key = key.substring(plc);

                if (plc == 0) {
                    break;
                }else {
                    curr = curr.right;
                }
            }
        }
        return false;
    }   

    /**
     * Inserts key into the trie.
     *
     * @param key The input {@link String}  key.
     * @return {@code true} if and only if the key was not already in the trie, {@code false} otherwise.
     */
    public boolean insert(String key) {
    //   throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
        char firstChar; 
        int plc = 0;
        TrieNode curr = root;
        TrieNode tmp;
        String temp = ""; 
        String key2 = "";

        while (true) {
            
            /** If the key is empty and we stop at a KEY node that has the same previous length as our current,
            *   then the item is already in the trie. (Return false)
            */ 
            if (key.isEmpty() && curr.isKey  && temp.length() == curr.str.length()) {
                return false;
            
            }
            /** If the key is empty and we stop on a none KEY node and it has the same previous length as our current,
            *   then set the node to key(make isKey = true), then break
            */ 
             else if (key.isEmpty() && !curr.isKey &&  temp.length() == curr.str.length()) {
                curr.isKey = true;
                break;
            } 
            
            firstChar = key.charAt(0);
            plc = 0; 

            // Find which node to examine next. Go to the left 
            if (firstChar == '0') {
                if (curr.left != null) { 
                    for (int i = 0; i < Math.min(curr.left.str.length(),key.length()); i++) {
                        if (curr.left.str.charAt(i) == key.charAt(i) ) {
                            plc++;
                        } else {
                            break;
                        }
                    }
                }
                
                if (plc == 0) {
                    key = key.substring(plc);
                    
                    curr.left = new TrieNode(key,true);
                    curr.left.parent = curr;
                    break;
                }
                else if (curr.left != null && plc != 0 && plc < curr.left.str.length()) {
                  
                    temp = curr.left.str.substring(plc);
                    key = key.substring(plc);
                    key2 = curr.left.str.substring(0,plc);
                    curr.left.str = temp; 
                    // Redefine 
                    tmp = curr.left;
                    if (temp.charAt(0) == '0') {
                        curr.left = new TrieNode(key2,false);
                        curr.left.parent = curr;
                        curr.left.left = tmp;
                        tmp.parent = curr.left;
                        if (!key.isEmpty()) {
                            curr.left.right = new TrieNode(key,true); 
                            curr.left.right.parent = curr.left;
 
                        } else {
                            curr.left.isKey = true;
                        }
                    } else {
                        curr.left = new TrieNode(key2,false);
                        curr.left.parent = curr;
                        curr.left.right = tmp;
                        tmp.parent = curr.left;
                        if (!key.isEmpty()) {
                            curr.left.left = new TrieNode(key,true);  
                            curr.left.left.parent = curr.left;

                        } else {
                            curr.left.isKey = true;
                        }
                    }


                    break; 
                } else {

                 
                    temp = key.substring(0, plc);
                    key = key.substring(plc);
                    curr = curr.left; 
                }
            }
            
            //Go to the right 
            else {
                if (curr.right != null) { 
                    for (int i = 0; i < Math.min(curr.right.str.length(),key.length()); i++) {
                        if (curr.right.str.charAt(i) == key.charAt(i) ) {
                            plc++;
                        } else {
                            break;
                        }
                    }
                }

                if (plc == 0) {
                    key = key.substring(plc);
                    curr.right = new TrieNode(key, true);
                    curr.right.parent = curr;

                    break;
                }
                else if (curr.right != null && plc != 0 && plc < curr.right.str.length()) {
                    temp = curr.right.str.substring(plc);
                    key = key.substring(plc);
                    key2 = curr.right.str.substring(0, plc);
                    curr.right.str = temp; 
                    // Redefine 
                    tmp = curr.right;
                    if (temp.charAt(0) == '0') {

                        curr.right = new TrieNode(key2,false);
                        curr.right.parent = curr;
                        curr.right.left = tmp;
                        tmp.parent = curr.right;
                        if(!key.isEmpty()){

                            curr.right.right = new TrieNode(key,true);  
                            curr.right.right.parent = curr.right;

                        } else {
                            curr.right.isKey = true; 
                        }
                    } else {
                        curr.right = new TrieNode(key2,false);
                        curr.right.parent = curr;
                        curr.right.right = tmp;
                        tmp.parent = curr.right;
                        if (!key.isEmpty()) {
                            curr.right.left = new TrieNode(key,true);  
                            curr.right.left.parent = curr.right;

                        }else {
                            curr.right.isKey = true; 
                        }

                    }


                    break; 
                } 
                 else {
                 
                    temp = key.substring(0, plc);   
                    key = key.substring(plc);
                    curr = curr.right;
                }

            }
        }

        this.size++;
        return true;


    }


    /**
     * Deletes key from the trie.
     *
     * @param key The {@link String}  key to be deleted.
     * @return {@code true} if and only if key was contained by the trie before we attempted deletion, {@code false} otherwise.
     */
    public boolean delete(String key) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
        char firstChar; 
        int plc = 0;
        TrieNode curr = root;
        String temp = ""; 

        while (true) {
            
            /** If the key is empty and we stop at a KEY node that has the same previous length as our current,
            *   then the item is already in the trie. (Return false)
            */ 
            if (key.isEmpty() && curr.isKey  && temp.length() == curr.str.length()) {
                if (curr.left == null && curr.right == null) {

                    if (curr.parent.left != null &&  curr.parent.left.equals(curr)) {
                        curr.parent.left = null;

                    } else if (curr.parent.right != null && curr.parent.right.equals(curr)) {
                        curr.parent.right = null;

                    }
                } else if (curr.right != null && curr.left != null){
                    curr.isKey = false;
                }
                else if ( curr.left == null || curr.right == null ) {

                    if (curr.right == null ) {
                        if (curr.parent.right.equals(curr)) {
                            curr.left.str = curr.str.concat(curr.left.str);
                            curr.parent.right = curr.left; 
                            curr.left.parent = curr.parent;
                        } else {
                            curr.right.str = curr.str.concat(curr.right.str);
                            curr.parent.left = curr.right; 
                            curr.right.parent = curr.parent;
                        }
                        
                    } else if (curr.left == null) {
                        if (curr.parent.right.equals(curr)) {
                            curr.right.str = curr.str.concat(curr.right.str);
                            curr.parent.right = curr.right; 
                            curr.right.parent = curr.parent;
                        } else {
                            curr.right.str = curr.str.concat(curr.right.str);
                            curr.parent.right = curr.right; 
                            curr.right.parent = curr.parent;
                        }
                    }
                } else if (curr.parent.parent == null) {
                    if (curr.parent.left.equals(curr)) {
                        if (curr.left != null) {
                            curr.left.str = curr.str.concat(curr.left.str);
                            curr.left.parent = curr.parent;
                            curr.parent.left = curr.left;
                            
    
                        }else {
                            curr.right.str = curr.str.concat(curr.right.str);
                            curr.right.parent = curr.parent;
                            curr.parent.left = curr.right;
                        }
                    }

                }
                this.size--;
                return true;
            }
            /** If the key is empty and we stop on a none KEY node and it has the same previous length as our current,
            *   then set the node to key(make isKey = true), then break
            */ 
            else if (key.isEmpty() && !curr.isKey &&  temp.length() == curr.str.length()) {
                break;
            } 
            

            firstChar = key.charAt(0);
            plc = 0; 

            // Find which node to examine next. Go to the left 
            if (firstChar == '0') {
                if (curr.left != null) { 
                    for (int i = 0; i < Math.min(curr.left.str.length(),key.length()); i++) {
                        if (curr.left.str.charAt(i) == key.charAt(i) ) {
                            plc++;
                        } else {
                            break;
                        }
                    }
                }
                
                if (plc == 0) {
                    break;
                }
                else if (curr.left != null && plc != 0 && plc < curr.left.str.length()) {
                   
                    break; 
                } else {

                    temp = key.substring(0, plc);
                    key = key.substring(plc);
                    curr = curr.left; 
                }
            }
            
            //Go to the right 
            else {
                if (curr.right != null) { 
                    for (int i = 0; i < Math.min(curr.right.str.length(),key.length()); i++) {
                        if (curr.right.str.charAt(i) == key.charAt(i) ) {
                            plc++;
                        } else {
                            break;
                        }
                    }
                }
                

                if (plc == 0) {
                    break;
                }
                else if (curr.right != null && plc != 0 && plc < curr.right.str.length()) {
                  

                    break; 
                } 
                else {
                    
                    temp = key.substring(0, plc);
                    key = key.substring(plc);
                    curr = curr.right;
                }

            }
        }

        return false;




    }

    /**
     * Queries the trie for emptiness.
     *
     * @return {@code true} if and only if {@link #getSize()} == 0, {@code false} otherwise.
     */
    public boolean isEmpty() {
     //   throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
        return (this.getSize() == 0)? true: false;
    }

    /**
     * Returns the number of keys in the tree.
     *
     * @return The number of keys in the tree.
     */
    public int getSize() {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
        return this.size; 
    }

    /**
     * <p>Performs an <i>inorder (symmetric) traversal</i> of the Binary Patricia Trie. Remember from lecture that inorder
     * traversal in tries is NOT sorted traversal, unless all the stored keys have the same length. This
     * is of course not required by your implementation, so you should make sure that in your tests you
     * are not expecting this method to return keys in lexicographic order. We put this method in the
     * interface because it helps us test your submission thoroughly and it helps you debug your code! </p>
     *
     * <p>We <b>neither require nor test </b> whether the {@link Iterator} returned by this method is fail-safe or fail-fast.
     * This means that you  do <b>not</b> need to test for thrown {@link java.util.ConcurrentModificationException}s and we do
     * <b>not</b> test your code for the possible occurrence of concurrent modifications.</p>
     *
     * <p>We also assume that the {@link Iterator} is <em>immutable</em>, i,e we do <b>not</b> test for the behavior
     * of {@link Iterator#remove()}. You can handle it any way you want for your own application, yet <b>we</b> will
     * <b>not</b> test for it.</p>
     *
     * @return An {@link Iterator} over the {@link String} keys stored in the trie, exposing the elements in <i>symmetric
     * order</i>.
     */

    public Iterator<String> inorderTraversal() {
       // throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
    
       nxt = root; 
       stck = new ArrayList<>();

       
        while (nxt.left != null) {
            nxt = nxt.left;   
            stck.add(nxt);
 
        }
        
        return  new Iterator<String>() {

            @Override
            public boolean hasNext() {
                return nxt != null;
            }

            @Override
            public String next() {
                if (!hasNext()) throw new NoSuchElementException();
                String ret = "";
                int par = 0; 
                TrieNode n = nxt;

                if (nxt.right != null) {

                    if (nxt.isKey) {
                        for (int i = 0; i < stck.size(); i++) {
                            ret = ret.concat(stck.get(i).str);
                        }
                        
                        nxt = nxt.right; 
                        stck.add(nxt);


                        while (nxt.left != null) {
                            nxt = nxt.left;
                            stck.add(nxt);
                        }

                    } else {

                        nxt = nxt.right; 
                        stck.add(nxt);


                        while (nxt.left != null) {
                            nxt = nxt.left;
                            stck.add(nxt);
                        }
                        if (n.isKey) {
                            for (int i = 0; i < stck.size(); i++) {
                                ret = ret.concat(stck.get(i).str);
                            }
                            if (!stck.isEmpty()) {
                                stck.remove(stck.size() - 1);
                            }
                        }  
                    }

                    if (ret.equals("") ) {
                        return next();
                    }
                    return ret;

                }

                while(true) {
                    
                    if (nxt.parent == null) {
                        nxt = null;
                        if(n.isKey){
                            for (int i = 0; i < stck.size(); i++) {
                                ret = ret.concat(stck.get(i).str);
                            }
                            if (!stck.isEmpty()){
                                stck.remove(stck.size() - 1);
                            }
                        }
                      //  if (ret.equals("") ) {
                       //     return next();
                       // }
                        return ret;

                    }
                    if (nxt.parent.left != null && nxt.parent.left.equals(nxt)) {
                        nxt = nxt.parent;
                        if (n.isKey){
                            for (int i = 0; i < stck.size(); i++) {
                                ret = ret.concat(stck.get(i).str);
                            }
                            while (!stck.isEmpty() && par >= 0) {
                                stck.remove(stck.size() - 1);
                                par--;
                            }
                            
                        }
                        if (ret.equals("") ) {
                            return next();
                        }
                        return ret;
                    }
                    nxt = nxt.parent;
                    par++;
                }
            
            }

            @Override
            public void remove(){
                throw new UnsupportedOperationException("Iterator does not implement remove().");
            }

        };
    }


    

    /**
     * Finds the longest {@link String} stored in the Binary Patricia Trie.
     * @return <p>The longest {@link String} stored in this. If the trie is empty, the empty string &quot;&quot; should be
     * returned. Careful: the empty string &quot;&quot;is <b>not</b> the same string as &quot; &quot;; the latter is a string
     * consisting of a single <b>space character</b>! It is also <b>not the same as the</b> null <b>reference</b>!</p>
     *
     * <p>Ties should be broken in terms of <b>value</b> of the bit string. For example, if our trie contained
     * only the binary strings 01 and 11, <b>11</b> would be the longest string. If our trie contained
     * only 001 and 010, <b>010</b> would be the longest string.</p>
     */
    public String getLongest() {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THE METHOD!
        Iterator<String> it;
        ArrayList<String> st;
        int length = -1;
        String ret = "";
        if (this.getSize() > 0) {
            it = this.inorderTraversal();
            st = new ArrayList<>();

            while (it.hasNext()) {
                st.add(it.next());
                
            }

            for (int i = 0; i < st.size(); i++) {
                if (st.get(i).length() >= length) {
                    length = st.get(i).length();
                }
            }
            
            for (int i = 0; i < st.size(); i++) {
                if (st.get(i).length() < length) {
                    st.remove(i);
                    st.trimToSize();
                    i = 0;
                }
            }
           


            if (st.size() == 1) {
                st.trimToSize();
                return st.get(0);
            } else {

                for (int i = 0; i < st.size(); i++) {
                    if (ret.compareTo(st.get(i)) <= 0) {
                        ret = st.get(i);
                    }
                }
            }
            
        }

        return ret;
    }

    /**
     * Makes sure that your trie doesn't have splitter nodes with a single child. In a Patricia trie, those nodes should
     * be pruned.
     * @return {@code true} iff all nodes in the trie either denote stored strings or split into two subtrees, {@code false} otherwise.
     */
    public boolean isJunkFree(){
        return isEmpty() || (isJunkFree(root.left) && isJunkFree(root.right));
    }

    private boolean isJunkFree(TrieNode n){
        if(n == null){   // Null subtrees trivially junk-free
            return true;
        }
        if(!n.isKey){   // Non-key nodes need to be strict splitter nodes
            return ( (n.left != null) && (n.right != null) && isJunkFree(n.left) && isJunkFree(n.right) );
        } else {
            return ( isJunkFree(n.left) && isJunkFree(n.right) ); // But key-containing nodes need not.
        }
    }
}
