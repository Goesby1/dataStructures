package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.PRQuadTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/** <p>A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants: </p>
 * <ul>
 *      <li>Its children pointer buffer is non-null and has a length of 4.</li>
 *      <li>If there is at least one black node child, the total number of {@link KDPoint}s stored
 *      by <b>all</b> of the children is greater than the bucketing parameter (because if it is equal to it
 *      or smaller, we can prune the node.</li>
 * </ul>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 *  @author Yosefe Eshete
 */
public class PRQuadGrayNode extends PRQuadNode{


    /* ******************************************************************** */
    /* *************  PLACE ANY  PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
    public int gryHeight;
    private PRQuadNode [] prQuadArray;
    private static boolean found = false;
    private final int NW = 0;
    private final int NE = 1;
    private final int SW = 2;
    private final int SE = 3;
    
    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */

    /**
     * Creates a {@link PRQuadGrayNode}  with the provided {@link KDPoint} as a centroid;
     * @param centroid A {@link KDPoint} that will act as the centroid of the space spanned by the current
     *                 node.
     * @param k The See {@link PRQuadTree#PRQuadTree(int, int)} for more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object!
        this.prQuadArray = new PRQuadNode[4];
        this.prQuadArray[0] = null;
        this.prQuadArray[1] = null;
        this.prQuadArray[2] = null;
        this.prQuadArray[3] = null;
        

    }


    /**
     * <p>Insertion into a {@link PRQuadGrayNode} consists of navigating to the appropriate child
     * and recursively inserting elements into it. If the child is a white node, memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for the appropriate insert to be called
     * based on the child object's runtime object.</p>
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint}  to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        int space = this.spaceLocation(p);
        KDPoint kd; 

        if (this.prQuadArray[space] == null) {

            if (space == NW) {
                kd = new KDPoint((int) (this.centroid.coords[0] - Math.pow(2, k-2)) , (int) (this.centroid.coords[1] +  Math.pow(2, k-2)) ) ;
                this.prQuadArray[0] = new PRQuadBlackNode(kd, k-1, bucketingParam, p);
                ((PRQuadBlackNode) this.prQuadArray[0]).blkHeight = this.gryHeight + 1;
            } else if (space == NE) { 
                kd = new KDPoint((int) (this.centroid.coords[0] + Math.pow(2, k-2)), (int) (this.centroid.coords[1] + Math.pow(2, k-2)));
                this.prQuadArray[1] = new PRQuadBlackNode(kd, k-1, bucketingParam, p);
                ((PRQuadBlackNode) this.prQuadArray[1]).blkHeight = this.gryHeight + 1;

            } else if (space == SW) {
                kd = new KDPoint((int) (this.centroid.coords[0] - Math.pow(2, k-2)) , (int) (this.centroid.coords[1] - Math.pow(2, k-2)));
                this.prQuadArray[2] = new PRQuadBlackNode(kd , k-1, bucketingParam, p);
                ((PRQuadBlackNode) this.prQuadArray[2]).blkHeight = this.gryHeight + 1;

            } else if (space == SE) {
                kd = new KDPoint((int) (this.centroid.coords[0] + Math.pow(2, k-2)) , (int) (this.centroid.coords[1] - Math.pow(2, k-2)));
                this.prQuadArray[3] =  new PRQuadBlackNode(kd , k-1, bucketingParam, p);
                ((PRQuadBlackNode) this.prQuadArray[3]).blkHeight = this.gryHeight + 1;

            }
        } else {
            this.prQuadArray[space] = this.prQuadArray[space].insert(p, k-1);
        }
        return this;
    }

    /**
     * <p>Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree rooted at the current node!</b></p>
     *
     * <p>Polymorphism will allow for the recursive call to be made into the appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if it has no gray children, and one of the
     * following two conditions are satisfied:</p>
     *
     * <ol>
     *     <li>The deletion left it with a single black child. Then, there is no reason to further subdivide the quadrant,
     *     and we can replace this with a {@link PRQuadBlackNode} that contains the {@link KDPoint}s that the single
     *     black child contains.</li>
     *     <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained by <b>all</b> the black children
     *     is <b>equal to or smaller than</b> the bucketing parameter. We can then similarly replace this with a
     *     {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black children.</li>
     *  </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
        int space = this.spaceLocation(p);
        int count = 0; 
        if (this.prQuadArray[space] != null) {
            if (this.prQuadArray[space] instanceof PRQuadGrayNode) {
                this.prQuadArray[space] = this.prQuadArray[space].delete(p);
            } else {
                this.prQuadArray[space].delete(p);
                if ((this.prQuadArray[NW] == null ||  this.prQuadArray[NW] instanceof PRQuadBlackNode) ||
                    (this.prQuadArray[NE] == null ||  this.prQuadArray[NE] instanceof PRQuadBlackNode) ||
                    (this.prQuadArray[SW] == null ||  this.prQuadArray[SW] instanceof PRQuadBlackNode) ||
                    (this.prQuadArray[SE] == null ||  this.prQuadArray[SE] instanceof PRQuadBlackNode) ) {
                        
                        if (this.prQuadArray[NW] != null ) {
                            count+=this.prQuadArray[NW].count();
                        }
                        if (this.prQuadArray[NE] != null ) {
                            count+=this.prQuadArray[NE].count();
                        }
                        if (this.prQuadArray[SW] != null ) {
                            count+=this.prQuadArray[SW].count();
                        }
                        if (this.prQuadArray[SE] != null ) {
                            count+=this.prQuadArray[SE].count();
                        }
                        if (count <= this.bucketingParam) {
                            
                            PRQuadBlackNode blk = new PRQuadBlackNode(this.centroid, k, bucketingParam);
                            blk.blkHeight = this.gryHeight;
                            if (this.prQuadArray[NW] != null ) {
                               for(KDPoint i: ((PRQuadBlackNode) this.prQuadArray[NW]).getPoints()) {
                                    blk.insert(i, k);
                                }
                            }
                            if (this.prQuadArray[NE] != null ) {
                                for(KDPoint i: ((PRQuadBlackNode) this.prQuadArray[NE]).getPoints()) {
                                    blk.insert(i, k);
                                }
                            }
                            if (this.prQuadArray[SW] != null ) {
                                for(KDPoint i: ((PRQuadBlackNode) this.prQuadArray[SW]).getPoints()) {
                                    blk.insert(i, k);
                                }
                            }
                            if (this.prQuadArray[SE] != null ) {
                                for(KDPoint i: ((PRQuadBlackNode) this.prQuadArray[SE]).getPoints()) {
                                    blk.insert(i, k);
                                }
                            }

                            return blk;
                        }
                }
            }
        }
        return this;
    }

    @Override
    public boolean search(KDPoint p){
        int space = this.spaceLocation(p);

        if (this.prQuadArray[space] == null) {
            return false;
        } else {
            if (this.prQuadArray[space] instanceof PRQuadGrayNode) {
                return this.prQuadArray[space].search(p);
            } else {
                return this.prQuadArray[space].search(p);
            }
        }

    }

    @Override
    public int height(){
        int nwHeight = 0;
        int neHeight = 0;
        int swHeight = 0;
        int seHeight = 0;


        if (this.prQuadArray[NW] != null ) {
            nwHeight = this.prQuadArray[NW].height();
        }
        if (this.prQuadArray[NE] != null ) {
            neHeight = this.prQuadArray[NE].height();

        }
        if (this.prQuadArray[SW] != null ) {
            swHeight = this.prQuadArray[SW].height();

        }
        if (this.prQuadArray[SE] != null ) {
            seHeight = this.prQuadArray[SE].height();
        }
        //System.out.println("Height: " +nwHeight +" " +neHeight + " "+swHeight + " " + seHeight );
        return  Math.max(Math.max( nwHeight, neHeight), Math.max(swHeight, seHeight) );
    }

    @Override
    public int count(){

        int nwHeight = 0;
        int neHeight = 0;
        int swHeight = 0;
        int seHeight = 0;
        int count = 0;

        if (this.prQuadArray[NW] != null ) {
            count+= this.prQuadArray[NW].count();
        }
        if (this.prQuadArray[NE] != null ) {
            count+=this.prQuadArray[NE].count();

        }
        if (this.prQuadArray[SW] != null ) {
            count+=this.prQuadArray[SW].count();

        }
        if (this.prQuadArray[SE] != null ) {
            count+=this.prQuadArray[SE].count();
        }
        //System.out.println("Height: " +nwHeight +" " +neHeight + " "+swHeight + " " + seHeight );
        return  count;

    }

    /**
     * Returns the children of the current node in the form of a Z-ordered 1-D array.
     * @return An array of references to the children of {@code this}. The order is Z (Morton), like so:
     * <ol>
     *     <li>0 is NW</li>
     *     <li>1 is NE</li>
     *     <li>2 is SW</li>
     *     <li>3 is SE</li>
     * </ol>
     */
    public PRQuadNode[] getChildren(){

        return this.prQuadArray;
        
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results, double range) {

        int space = this.spaceLocation(anchor); 
        if (this.prQuadArray[space] instanceof PRQuadBlackNode) {
            this.prQuadArray[space].range(anchor, results, range);

            if (space != NW && this.prQuadArray[NW] != null && this.prQuadArray[NW].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[NW].range(anchor, results, range);
            }
    
            if (space != NE && this.prQuadArray[NE] != null && this.prQuadArray[NE].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[NE].range(anchor, results, range);
    
            }
    
            if (space != SW && this.prQuadArray[SW] != null && this.prQuadArray[SW].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[SW].range(anchor, results, range);
    
            }
    
            if (space != SE && this.prQuadArray[SE] != null && this.prQuadArray[SE].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[SE].range(anchor, results, range);
    
            }
            
        } else if (this.prQuadArray[space] instanceof PRQuadGrayNode ) {
            this.prQuadArray[space].range(anchor, results, range);
            if (space != NW && this.prQuadArray[NW] != null && this.prQuadArray[NW].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[NW].range(anchor, results, range);
            }
    
            if (space != NE && this.prQuadArray[NE] != null && this.prQuadArray[NE].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[NE].range(anchor, results, range);
    
            }
    
            if (space != SW && this.prQuadArray[SW] != null && this.prQuadArray[SW].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[SW].range(anchor, results, range);
    
            }
    
            if (space != SE && this.prQuadArray[SE] != null && this.prQuadArray[SE].doesQuadIntersectAnchorRange(anchor, range)) {
                this.prQuadArray[SE].range(anchor, results, range);
    
            }

        }
        
        
    }


    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n)  {
    //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
        int space = this.spaceLocation(anchor); 
        if (this.prQuadArray[space] instanceof PRQuadBlackNode) {
            n.update(this.prQuadArray[space].nearestNeighbor(anchor, n).getBestGuess(), this.prQuadArray[space].nearestNeighbor(anchor, n).getBestDist());

            if (space != NW && this.prQuadArray[NW] != null && this.prQuadArray[NW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess()) )) {
                this.prQuadArray[NW].nearestNeighbor(anchor, n);
            }
    
            if (space != NE && this.prQuadArray[NE] != null && this.prQuadArray[NE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess())  )) {
                this.prQuadArray[NE].nearestNeighbor(anchor, n);
    
            }
    
            if (space != SW && this.prQuadArray[SW] != null && this.prQuadArray[SW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess()))) {
                this.prQuadArray[SW].nearestNeighbor(anchor, n);
    
            }
    
            if (space != SE && this.prQuadArray[SE] != null && this.prQuadArray[SE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess()))) {
                this.prQuadArray[SE].nearestNeighbor(anchor, n);
    
            }
            
        } else if (this.prQuadArray[space] instanceof PRQuadGrayNode ) {
            n.update(this.prQuadArray[space].nearestNeighbor(anchor, n).getBestGuess(), this.prQuadArray[space].nearestNeighbor(anchor, n).getBestDist());

            if (space != NW && this.prQuadArray[NW] != null && this.prQuadArray[NW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess()))) {
                this.prQuadArray[NW].nearestNeighbor(anchor, n);
            }
    
            if (space != NE && this.prQuadArray[NE] != null && this.prQuadArray[NE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess()))) {
                this.prQuadArray[NE].nearestNeighbor(anchor, n);
    
            }
    
            if (space != SW && this.prQuadArray[SW] != null && this.prQuadArray[SW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess()))) {
                this.prQuadArray[SW].nearestNeighbor(anchor, n);
    
            }
    
            if (space != SE && this.prQuadArray[SE] != null && this.prQuadArray[SE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, n.getBestGuess()))) {
                this.prQuadArray[SE].nearestNeighbor(anchor, n);
    
            }

        }

        return n;

    }

    @Override
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue) {
        
        int space = this.spaceLocation(anchor); 
        if (this.prQuadArray[space] instanceof PRQuadBlackNode) {
            this.prQuadArray[space].kNearestNeighbors(k, anchor, queue);

            if (space != NW && this.prQuadArray[NW] != null && this.prQuadArray[NW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()) )) {
                this.prQuadArray[NW].kNearestNeighbors(k, anchor, queue);
            }
    
            if (space != NE && this.prQuadArray[NE] != null && this.prQuadArray[NE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()))) {
                this.prQuadArray[NE].kNearestNeighbors(k, anchor, queue);
    
            }
    
            if (space != SW && this.prQuadArray[SW] != null && this.prQuadArray[SW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()))) {
                this.prQuadArray[SW].kNearestNeighbors(k, anchor, queue);
    
            }
    
            if (space != SE && this.prQuadArray[SE] != null && this.prQuadArray[SE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()))) {
                this.prQuadArray[SE].kNearestNeighbors(k, anchor, queue);
    
            }
            
        } else if (this.prQuadArray[space] instanceof PRQuadGrayNode ) {
            this.prQuadArray[space].kNearestNeighbors(k, anchor, queue);
            if (space != NW && this.prQuadArray[NW] != null && this.prQuadArray[NW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()))) {
                this.prQuadArray[NW].kNearestNeighbors(k, anchor, queue);
            }
    
            if (space != NE && this.prQuadArray[NE] != null && this.prQuadArray[NE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()))) {
                this.prQuadArray[NE].kNearestNeighbors(k, anchor, queue);
    
            }
    
            if (space != SW && this.prQuadArray[SW] != null && this.prQuadArray[SW].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()))) {
                this.prQuadArray[SW].kNearestNeighbors(k, anchor, queue);
    
            }
    
            if (space != SE && this.prQuadArray[SE] != null && this.prQuadArray[SE].doesQuadIntersectAnchorRange(anchor, KDPoint.euclideanDistance(anchor, queue.last()))) {
                this.prQuadArray[SE].kNearestNeighbors(k, anchor, queue);
    
            }

        }

    }

    private int spaceLocation(KDPoint p) {
       
        if (p.coords[0] >= this.centroid.coords[0]) {
            if (p.coords[1] >= this.centroid.coords[1]) {
                return NE;
            }
            return SE;
        } else {
            if (p.coords[1] >= this.centroid.coords[1]) {
                return NW;
            }
            return SW;
        }
    }

}

