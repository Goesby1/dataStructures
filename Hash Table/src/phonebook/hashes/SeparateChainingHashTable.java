package phonebook.hashes;

import java.util.Iterator;

//import org.graalvm.compiler.lir.alloc.lsra.LinearScanEliminateSpillMovePhase_OptionDescriptors;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.KVPairList;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**<p>{@link SeparateChainingHashTable} is a {@link HashTable} that implements <b>Separate Chaining</b>
 * as its collision resolution strategy, i.e the collision chains are implemented as actual
 * Linked Lists. These Linked Lists are <b>not assumed ordered</b>. It is the easiest and most &quot; natural &quot; way to
 * implement a hash table and is useful for estimating hash function quality. In practice, it would
 * <b>not</b> be the best way to implement a hash table, because of the wasted space for the heads of the lists.
 * Open Addressing methods, like those implemented in {@link LinearProbingHashTable} and {@link QuadraticProbingHashTable}
 * are more desirable in practice, since they use the original space of the table for the collision chains themselves.</p>
 *
 * @author Yosefe Eshete
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see OrderedLinearProbingHashTable
 * @see CollisionResolver
 */
public class SeparateChainingHashTable implements HashTable{

    /* ****************************************************************** */
    /* ***** PRIVATE FIELDS / METHODS PROVIDED TO YOU: DO NOT EDIT! ***** */
    /* ****************************************************************** */

    private KVPairList[] table;
    private int count;
    private PrimeGenerator primeGenerator;

    // We mask the top bit of the default hashCode() to filter away negative values.
    // Have to copy over the implementation from OpenAddressingHashTable; no biggie.
    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    /* **************************************** */
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  */
    /* **************************************** */
    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     */
    public SeparateChainingHashTable(){
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        primeGenerator = new PrimeGenerator();
        table = new KVPairList[primeGenerator.getCurrPrime()];
        for (int i = 0; i < table.length; i++) {
            table[i] = new KVPairList();
        }
        count = 0; 
    }

    @Override
    public Probes put(String key, String value) {
       // throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
       int loc;
       Probes probe; 
       if (key == null || value == null) {
           throw new IllegalArgumentException();
       }
       /*Find the proper location in the table to insert the value, mod the size of the table. */
       loc = hash(key);
       probe = new Probes(value, 1);
       //if (table[loc].isEmpty()) {
        table[loc].addBack(key, value);
        count++;
       /*} else {
            
            if (table[loc].containsKey(key)) {
                probe = table[loc].getValue(value);
                table[loc].updateValue(key, value);

            } else {
                table[loc].addBack(key, value);
                count++;
            }
       }*/
       return probe; 

    }

    @Override
    public Probes get(String key) {
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        int loc;
        Probes probe; 
        loc = hash(key);
        
        probe = new Probes(null,0);

        if (table[loc].size() != 0) {
            probe = table[loc].getValue(key);
        }
    
        return probe; 
    }

    @Override
    public Probes remove(String key) {
        // throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        int loc; 
        Probes probe; 

        if (key == null) {
            probe = new Probes(null, 0);
            return probe; 
        }

        loc = hash(key);
        probe = table[loc].removeByKey(key);
        if (probe.getValue() != null) {
            count--;
        }

        return probe; 
    }

    @Override
    public boolean containsKey(String key) {
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        int loc = hash(key);

        return table[loc].containsKey(key)? true: false;
    }

    @Override
    public boolean containsValue(String value) {
       // throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
       Probes probe; 
        for (int i = 0; i < table.length; i++) {
            probe = table[i].getValue(value);
            if(probe.getValue() != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        return count; 
    }

    @Override
    public int capacity() {
        return table.length; // Or the value of the current prime.
    }

    /**
     * Enlarges this hash table. At the very minimum, this method should increase the <b>capacity</b> of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the enlargement heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     * @see PrimeGenerator#getNextPrime()
     */
    public void enlarge() {
       // throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        KVPairList [] newTable = new KVPairList[primeGenerator.getNextPrime()];
        int loc;
        Iterator<KVPair> it;
        KVPair kv;

        for (int i = 0; i < newTable.length; i++) {
            newTable[i] = new KVPairList();
        }

        for (int i = 0; i < table.length; i++) {
            
            if (!table[i].isEmpty()) {
                it = table[i].iterator();
                while(it.hasNext()){
                    kv = it.next();
                    loc = (kv.hashCode() & 0x7fffffff) % newTable.length;
                    newTable[loc].addBack(kv.getKey(), kv.getValue());
               }
            }
        }

        this.table = newTable;
    }

    /**
     * Shrinks this hash table. At the very minimum, this method should decrease the size of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the shrinking heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     *
     * @see PrimeGenerator#getPreviousPrime()
     */
    public void shrink(){
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        KVPairList[] newTable = new KVPairList[primeGenerator.getPreviousPrime()];
        int loc; 
        Iterator<KVPair> it; 
        KVPair kv; 

        for (int i = 0; i < newTable.length; i++) {
            newTable[i] = new KVPairList();
        }
        

        for (int i = 0; i < table.length; i ++) {
            
            if(!table[i].isEmpty()) {
                it = table[i].iterator();

                while (it.hasNext()) {
                    kv = it.next();
                    loc = (kv.hashCode() & 0x7fffffff) % newTable.length;
                    newTable[loc].addBack(kv.getKey(), kv.getValue());
                }
            }   

        }
        this.table = newTable;
    }
}
