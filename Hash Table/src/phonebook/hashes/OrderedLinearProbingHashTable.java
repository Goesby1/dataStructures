package phonebook.hashes;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link OrderedLinearProbingHashTable} is an Openly Addressed {@link HashTable} implemented with
 * <b>Ordered Linear Probing</b> as its collision resolution strategy: every key collision is resolved by moving
 * one address over, and the keys in the chain is in order. It suffer from the &quot; clustering &quot; problem:
 * collision resolutions tend to cluster collision chains locally, making it hard for new keys to be
 * inserted without collisions. {@link QuadraticProbingHashTable} is a {@link HashTable} that
 * tries to avoid this problem, albeit sacrificing cache locality.</p>
 *
 * @author Yosefe Eshete
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see QuadraticProbingHashTable
 * @see CollisionResolver
 */
public class OrderedLinearProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/

    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */
    protected KVPair[] OLPresize(KVPair[] table) {

        int loc; 
        int reLoc; 
        KVPair[] newTable = new KVPair[primeGenerator.getNextPrime()]; 
        String keyCopy;
        String valueCopy;
        String key;
        String value;
        resizeProbe = 0;  
        count = 0;       
        for (int i = 0; i < newTable.length; i++) {
            newTable[i] = null; 
        }
     
        for (int i = 0; i < table.length; i++) {
            resizeProbe++;
            if (table[i] != null && !table[i].equals(TOMBSTONE)) {
                loc = (table[i].getKey().hashCode() & 0x7fffffff) % newTable.length;  
                key = table[i].getKey();
                value = table[i].getValue();
                for (int z = 0; z < newTable.length; z++) {
                    resizeProbe++;
                    reLoc = (loc + z) % newTable.length; 
                    if (newTable[reLoc] == null ) {
                        newTable[reLoc] = new KVPair(key, value);
                        count++;
                        break;
                    }
                    if (key.compareTo(newTable[reLoc].getKey()) < 0 ) {
                        keyCopy = newTable[reLoc].getKey();
                        valueCopy = newTable[reLoc].getValue();
                        newTable[reLoc] = new KVPair(key, value );
                        key = keyCopy;
                        value = valueCopy;
                    }
                }

            }
        }
        return newTable;
    }

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *               we want soft deletion, {@code false} otherwise.
     */
    public OrderedLinearProbingHashTable(boolean soft){

       // throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
       primeGenerator = new PrimeGenerator();
       table = new KVPair[primeGenerator.getCurrPrime()];
       softFlag = soft; 

       for (int i = 0; i < table.length; i++) {
           table[i] = null;
       }
       count = 0; 

    }



    /**
     * Inserts the pair &lt;key, value&gt; into this. The container should <b>not</b> allow for {@code null}
     * keys and values, and we <b>will</b> test if you are throwing a {@link IllegalArgumentException} from your code
     * if this method is given {@code null} arguments! It is important that we establish that no {@code null} entries
     * can exist in our database because the semantics of {@link #get(String)} and {@link #remove(String)} are that they
     * return {@code null} if, and only if, their key parameter is {@code null}. This method is expected to run in <em>amortized
     * constant time</em>.
     *
     * Different from {@link LinearProbingHashTable}, the keys in the chain are <b>in order</b>. As a result, we might increase
     * the cost of insertion and reduce the cost on search miss. One thing to notice is that, in soft deletion, we ignore
     * the tombstone during the reordering of the keys in the chain. We will have some example in the writeup.
     *
     * Instances of {@link OrderedLinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity exceeds 50&#37;
     * @param key The record's key.
     * @param value The record's value.
     * @throws IllegalArgumentException if either argument is {@code null}.
     * @return The {@link phonebook.utils.Probes} with the value added and the number of probes it makes.
     */
    @Override
    public Probes put(String key, String value) {
    //   throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        int loc; 
        int reLoc;
        String keyCopy;
        String valueCopy; 
        Probes probe; 
        int probeCount = 0; 

        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }

        if (resizeBool) {            
            table = OLPresize(table);
            probeCount += resizeProbe;
        } 
        resizeBool = false;
        loc = hash(key); 
        
        for (int i = 0; i < table.length; i++) {
            probeCount++;
            reLoc = (loc + i) % table.length;
            if (table[reLoc] == null ) {
                table[reLoc] = new KVPair(key, value);
                count++;
                break;
            }

            if (!table[reLoc].equals(TOMBSTONE) && key.compareTo(table[reLoc].getKey()) < 0) {
                keyCopy = table[reLoc].getKey();
                valueCopy = table[reLoc].getValue();
                table[reLoc] = new KVPair(key, value);
                key = keyCopy;
                value = valueCopy;
            }
        }

        loc = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                loc++;
            }
        }
        if ( (float) loc/this.capacity() > .5 ) {
            resizeBool = true;
        }
        probe = new Probes(value, probeCount);
        return probe;
    }

    @Override
    public Probes get(String key) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        Probes ret = new Probes(null, 0);
        int loc;
        int linProbe; 

        if (key != null) {
            loc = hash(key);    
            for (int i = 0; i < table.length; i++) {
                linProbe = (loc + i) % table.length; 
                if ( table[linProbe] != null && !table[linProbe].equals(TOMBSTONE) && table[linProbe].getKey().equals(key)) {
                    ret = new Probes(table[linProbe].getValue(), i+1);
                    break;
                }

                if ( table[linProbe] != null && !table[linProbe].equals(TOMBSTONE) && table[linProbe].getKey().compareTo(key) > 0) {
                    ret = new Probes(null, i+1);
                    break;
                }

                if (table[linProbe] == null) {
                    ret = new Probes(null, i+1);
                    break;
                }
            }
        }
        return ret;
    }


    /**
     * <b>Return</b> the value associated with key in the {@link HashTable}, and <b>remove</b> the {@link phonebook.utils.KVPair} from the table.
     * If key does not exist in the database
     * or if key = {@code null}, this method returns {@code null}. This method is expected to run in <em>amortized constant time</em>.
     *
     * @param key The key to search for.
     * @return The {@link phonebook.utils.Probes} with associated value and the number of probe used. If the key is {@code null}, return value {@code null}
     * and 0 as number of probes; if the key doesn't exist in the database, return {@code null} and the number of probes used.
     */
    @Override
    public Probes remove(String key) {
      //  throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        Probes ret = new Probes(null, 0);
        int loc;
        int linProbe; 
        int hardCount = 0;
        String val = null;
        String keyCopy;
        String valueCopy; 

        if (key != null) {
            loc = hash(key);  
            if (softFlag) {
                for (int i = 0; i < table.length; i++) {
                    linProbe = (loc + i) % table.length; 
                    if (!table[linProbe].equals(TOMBSTONE) && table[linProbe] != null && table[linProbe].getKey().equals(key)) {
                        ret = new Probes(table[loc].getValue(), i+1);
                        table[linProbe] = TOMBSTONE;
                        count--;
                        break;
                    }

                    if ( !table[linProbe].equals(TOMBSTONE) && table[linProbe] != null && table[linProbe].getKey().compareTo(key) > 0) {
                        ret = new Probes(null, i+1);
                        break;
                    }

                    if (table[linProbe] == null) {
                        ret = new Probes(null, i+1);
                        break;
                    }
                }
            } else {  
                for (int i = 0; i < table.length; i++) {
                    linProbe = (loc + i) % table.length; 

                    if (table[linProbe] == null) {
                        hardCount += i+1;
                        break;
                    }

                    if ( table[linProbe] != null && val != null ) {
                        keyCopy = table[linProbe].getKey();
                        valueCopy = table[linProbe].getValue();
                        table[linProbe] = null;
                        hardCount += this.put(keyCopy, valueCopy).getProbes();
                    }

                    if ( table[linProbe] != null && table[linProbe].getKey().compareTo(key) > 0) {
                        hardCount += i+1;
                        break;
                    }
                
                    

                    if ( table[linProbe] != null && table[linProbe].getKey().equals(key)) {
                        val = table[linProbe].getValue();
                        table[linProbe] = null;
                        count--;
                    }  
                }
                ret = new Probes(val, hardCount);
            }
        }


        return ret;
    }

    @Override
    public boolean containsKey(String key) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        boolean ret = false;
        int loc;
        int linProbe; 

        if (key != null) {
            loc = hash(key);    
            for (int i = 0; i < table.length; i++) {
                linProbe = (loc + i) % table.length; 
                if ( table[linProbe] != null && !table[linProbe].equals(TOMBSTONE) && table[linProbe].getKey().equals(key)) {
                    ret = true;
                    break;
                }

                if ( table[linProbe] != null && !table[linProbe].equals(TOMBSTONE) && table[linProbe].getKey().compareTo(key) > 0) {
                    break;
                }

                if (table[linProbe] == null) {
                    break;
                }
            }
        }
        return ret;

    }

    @Override
    public boolean containsValue(String value) {
        //throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        boolean ret = false; 
        if (value != null) {
            for (int i = 0; i < table.length; i++) {
                if ( table[i] != null && !table[i].equals(TOMBSTONE) && table[i].getValue().equals(value)) {
                    ret = true;
                    break;
                }              
            }
        }
        return ret; 
    }

    @Override
    public int size() {
    //  throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        return count; 
    }   

    @Override
    public int capacity() {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        return table.length;
    }
   

}
