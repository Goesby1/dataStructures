package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;

import java.util.Collection;

import org.junit.internal.builders.NullBuilder;

/**
 * <p>{@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used extensively by
 * {@link spatial.trees.KDTree} to implement its functionality.</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  Yosefe Eshete
 *
 * @see spatial.trees.KDTree
 */

public class KDTreeNode {


    /* *************************************************************************** */
    /* ************* WE PROVIDE THESE FIELDS TO GET YOU STARTED.  **************** */
    /* ************************************************************************** */
    private KDPoint p;
    private int height;
    private KDTreeNode left, right;
    private static KDTreeNode ret = null;

    /* *************************************************************************************** */
    /* *************  PLACE ANY OTHER PRIVATE FIELDS AND YOUR PRIVATE METHODS HERE: ************ */
    /* ************************************************************************************* */


    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */


    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly created node.
     * @param p The {@link KDPoint} to store inside this. Just a reminder: {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p){
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        this.p = p; 
        this.height = 0;
        this.left = this.right = null;
        
    }

    /**
     * <p>Inserts the provided {@link KDPoint} in the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.</p>
     * @param currDim The current dimension to consider
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    public  void insert(KDPoint pIn, int currDim, int dims){
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        KDTreeNode fd = finder(this, pIn, currDim, dims);
        System.out.println("Current dim: " + fd.height % dims);
        if ( pIn.coords[fd.height % dims] >= fd.p.coords[fd.height % dims]) {
            fd.right = new KDTreeNode(pIn);
            fd.right.height = fd.height + 1;
        } else {
            fd.left = new KDTreeNode(pIn);
            fd.left.height = fd.height + 1;
        }
    }
    
    

    /**
     * <p>Deletes the provided {@link KDPoint} from the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who either:</p>
     *
     * <ul>
     *      <li>Has a NON-null subtree as a right child.</li>
     *      <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>You should consult the class slides, your notes, and the textbook about what you need to do in those two
     * special cases.</p>
     * @param currDim The current dimension to consider.
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims){
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        KDTreeNode kd = finderDelete(this, pIn, currDim, dims);
        KDTreeNode temp;
        temp = new KDTreeNode(kd.p);
        temp.height = kd.height;
       /* System.out.println("(temp.height - 1) % dims: " +((temp.height - 1) % dims) 
        +"| kd.p.coords[(temp.height - 1) % dims]: " + kd.p.coords[(temp.height - 1) % dims]
        +"| pIn.coords[(temp.height - 1) % dims]: " +  pIn.coords[(temp.height - 1) % dims]);*/
        if (kd.p.coords[temp.height % dims] < pIn.coords[temp.height % dims]) {
            System.out.println("Commit change");
            if (kd.right.left == null && kd.right.right == null) {
                kd.right = null; 
            } else if (kd.right.right != null) {
                rightNotNull(kd.right, kd.right.right, kd.right.p,  (kd.right.height % dims) , dims);
            } else if (kd.right.left != null && kd.right.right == null ) {
                leftNotNull(kd.right, kd.right.left, ret, kd.right.p, currDim, dims);
                if (ret != null) {
                    kd.right.p.coords = ret.p.coords.clone();
                    this.delete(ret.p, ret.height % dims, dims);
                }
            
            }
        } else {
            if (kd.left.left == null && kd.left.right == null) {
                kd.left = null;
            } else if (kd.left.right != null) {
                rightNotNull(kd.left, kd.left.right, kd.left.p,  (kd.left.height % dims) , dims);
            } else if (kd.left.left != null && kd.left.right == null ) {
                leftNotNull(kd.left, kd.left.left, ret, kd.left.p, currDim, dims);
                if (ret != null) {
                    kd.left.p.coords = ret.p.coords.clone();
                    this.delete(ret.p, ret.height % dims, dims);
                }
            }
        }
        ret = null;
        return this;
    }

    /**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     * @param pIn The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false otherwise.
     */
    public  boolean search(KDPoint pIn, int currDim, int dims){
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        KDTreeNode kd = finder(this, pIn, currDim, dims);
        if (kd != null) {
            return true;
        }
        return false;
    }   

    /**
     * <p>Executes a range query in the given {@link KDTreeNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#euclideanDistance(KDPoint) euclideanDistance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     *
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims The total number of dimensions of our {@link KDPoint}s.
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
     *              {@link KDPoint}s that satisfy our query will fall. The euclideanDistance metric used} is defined by
     *              {@link KDPoint#euclideanDistance(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range, int currDim , int dims){
        throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }


    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the euclideanDistance reported by
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public  NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,
                                            NNData<KDPoint> n, int dims){
        throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the euclideanDistance reported by* {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by euclideanDistance to the point.
     *
     * @see BoundedPriorityQueue
     */
    public  void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims){
        throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    /**
     * Returns the height of the subtree rooted at the current node. Recall our definition of height for binary trees:
     * <ol>
     *     <li>A null tree has a height of -1.</li>
     *     <li>A non-null tree has a height equal to max(height(left_subtree), height(right_subtree))+1</li>
     * </ol>
     * @return the height of the subtree rooted at the current node.
     */
    public int height(){
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        if (this.left == null && this.right == null) {
            return this.height;
        } else if (this.left != null && this.right == null) {
            return this.left.height();
        } else if (this.left == null && this.right != null) {
            return this.right.height();
        }

        return Math.max(this.left.height(), this.right.height());
    }

    /**
     * A simple getter for the {@link KDPoint} held by the current node. Remember: {@link KDPoint}s ARE
     * MUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint(){
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        return this.p;
    }

    public KDTreeNode getLeft(){
       // throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
       return this.left;
    }

    public KDTreeNode getRight(){
     //   throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        return this.right;
    }

    /**
    * This helper recurses to the proper location for the insertion/Deletion. 
    */
    private KDTreeNode finder(KDTreeNode org, KDPoint val, int currDim, int dims) {
        
        int compDim = currDim % dims; 
        if (org == null || val == null) {
            return null; 
        }

        if (val.coords[compDim] >= org.p.coords[compDim]) {
            if (org.right == null) {
                //System.out.println("Right is null");
                return org;
            } else {
                //System.out.println("Right is not null, go right");

                currDim++;
                return finder(org.right, val, currDim, dims);
            }
           

        } else if (val.coords[compDim] < org.p.coords[compDim]) {
            if (org.left == null) {
                //System.out.println("Left is null");

                return org;
            } else {
                //System.out.println("Left is not null, go Left");

                currDim++;
                return finder(org.left, val, currDim, dims);
            }
            
        } 
        return null;

    }

    private KDTreeNode finderDelete(KDTreeNode org, KDPoint val, int currDim, int dims) {
        
        int compDim = currDim % dims; 

        /*System.out.println("compDim: "+ compDim +", val.coords[compDim]: "
        + val.coords[compDim]+ ", org.p.coords[compDim]: " +org.p.coords[compDim]);
        */
        if (org == null || val == null) {
            return null; 
        }
        if (org.p.equals(val)) {
            System.out.println("Found the proper value");
            return org;
        }
       
        if (val.coords[compDim] >= org.p.coords[compDim]) {
           
            System.out.println("Right is not null, go Right");
            currDim++;
            System.out.println("currDim: " + currDim);
            if (org.right != null && org.right.p.equals(val)) {
                System.out.println("Found the proper value");
                return org;
            }
            return finderDelete(org.right, val, currDim, dims);
            

        } else if (val.coords[compDim] < org.p.coords[compDim]) {
            
            System.out.println("Left is not null, go Left");
            currDim++;
            return finderDelete(org.left, val, currDim, dims);
            
            
        } 
        return null;

    }
    /**
     * Enter the right subtree so it can be properly sorted. 
     * @param kd is the right subtree of the node to be deleted.
     * @param currDim the dimension we are looking at for proper deletion.
     * @param org the node to be replaced with in order value that is greater.
     */
    private void rightNotNull(KDTreeNode org, KDTreeNode kd, KDPoint del ,int currDim, int dims) {

        if (kd.left == null) {
            if (kd.p.coords[currDim] >= del.coords[currDim]) {
                org.p.coords = del.coords.clone();
                this.delete(kd.p, kd.height % dims, dims);
            } else {
                rightNotNull(org, kd.right, del, currDim, dims);
            }
        } else if (kd.left != null) {
                rightNotNull(org, kd.left, del, currDim, dims);
        }
        
    }
    /**
     * Handle case where left side is non null. 
     * @param org what we want to delete 
     * @param kd subtree we are analyzing 
     * @param ret return node 
     * @param del Point to delete in org 
     * @param currDim current dimension
     * @param dims dimension space
     */
    private void leftNotNull(KDTreeNode org, KDTreeNode kd, KDTreeNode ret ,KDPoint del ,int currDim, int dims) {
        if ( org == null || kd == null) {
            return;
        }

        if (ret == null) {
            ret = new KDTreeNode(kd.p);
            ret.height = kd.height; 
            leftNotNull(org, kd.left, ret, del, currDim, dims);
            leftNotNull(org, kd.right, ret, del, currDim, dims);
        } else {
            if (kd.p.coords[currDim]  <  ret.p.coords[currDim] ) {
                ret.p.coords = kd.p.coords.clone();
                ret.height = kd.height;
            }

            leftNotNull(org, kd.left, ret, del, currDim, dims);
            leftNotNull(org, kd.right, ret, del, currDim, dims);
            
        }
    
    }



}
