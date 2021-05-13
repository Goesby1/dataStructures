package phonebook.hashes;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.KVPair;
import phonebook.utils.Probes;

/**
 * <p>{@link QuadraticProbingHashTable} is an Openly Addressed {@link HashTable} which uses <b>Quadratic
 * Probing</b> as its collision resolution strategy. Quadratic Probing differs from <b>Linear</b> Probing
 * in that collisions are resolved by taking &quot; jumps &quot; on the hash table, the length of which
 * determined by an increasing polynomial factor. For example, during a key insertion which generates
 * several collisions, the first collision will be resolved by moving 1^2 + 1 = 2 positions over from
 * the originally hashed address (like Linear Probing), the second one will be resolved by moving
 * 2^2 + 2= 6 positions over from our hashed address, the third one by moving 3^2 + 3 = 12 positions over, etc.
 * </p>
 *
 * <p>By using this collision resolution technique, {@link QuadraticProbingHashTable} aims to get rid of the
 * &quot;key clustering &quot; problem that {@link LinearProbingHashTable} suffers from. Leaving more
 * space in between memory probes allows other keys to be inserted without many collisions. The tradeoff
 * is that, in doing so, {@link QuadraticProbingHashTable} sacrifices <em>cache locality</em>.</p>
 *
 * @author Yosefe Eshete
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class QuadraticProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/

    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */
    private KVPair[] QPresize(KVPair[] table) {

        int loc; 
        int reLoc; 
        KVPair[] newTable = new KVPair[primeGenerator.getNextPrime()]; 
        resizeProbe = 0;  
        count = 0;       
        for (int i = 0; i < newTable.length; i++) {
            newTable[i] = null; 
        }
     
        for (int i = 0; i < table.length; i++) {
            resizeProbe++;
            if (table[i] != null && !table[i].equals(TOMBSTONE)) {
                loc = (table[i].getKey().hashCode() & 0x7fffffff) % newTable.length;
                resizeProbe++;
                if (newTable[loc] == null ) {
                    newTable[loc] = new KVPair(table[i].getKey(), table[i].getValue());
                    count++;
                    
                } else {
                    for (int z = 1; z < newTable.length; z++) {
                        reLoc = (loc + ((z+1) - 1) + (int) Math.pow(( (z+1) - 1), 2) ) % newTable.length; 
                        resizeProbe++;

                        if (newTable[reLoc] == null ) {
                            newTable[reLoc] = new KVPair(table[i].getKey(), table[i].getValue());
                            count++;
                            break;
                        }
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
    public QuadraticProbingHashTable(boolean soft) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        primeGenerator = new PrimeGenerator();
        table = new KVPair[primeGenerator.getCurrPrime()];
        softFlag = soft; 

        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        count = 0; 
    }

    @Override
    public Probes put(String key, String value) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        int loc; 
        int reLoc;
        Probes probe; 
        int probeCount = 0; 

        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }

        if (resizeBool) {
            table = QPresize(table);
            probeCount += resizeProbe;
        } 
        resizeBool = false;
        loc = hash(key); 
        
        for (int i = 0; i < table.length; i++) {
            probeCount++;
            reLoc = (loc + ((i+1) - 1) + (int) Math.pow(((i+1)-1), 2) )% table.length; 
            if (table[reLoc] == null ) {
                table[reLoc] = new KVPair(key, value);
                count++;
                break;
            }
        }
        
        
        loc = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                loc++;
            }
        }
        if ( (float) loc/this.capacity() >= .5 ) {
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
                linProbe = (loc + ((i+1) - 1) + (int) Math.pow(((i+1)-1), 2)) % table.length; 
                if ( table[linProbe] != null && !table[linProbe].equals(TOMBSTONE) && table[linProbe].getKey().equals(key)) {
                    ret = new Probes(table[linProbe].getValue(), i+1);
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

    @Override
    public Probes remove(String key) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
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
                if (table[loc] != null ) {
                    if (table[loc].getKey().equals(key)) {
                        ret = new Probes(table[loc].getValue(), 1);
                        table[loc] = TOMBSTONE;
                        count--;
                    } else {
                        for (int i = 1; i < table.length; i++) {
                            linProbe = (loc + ((i+1) - 1) + (int) Math.pow(((i+1)-1), 2)) % table.length; 
                            if ( table[linProbe] != null && !table[linProbe].equals(TOMBSTONE) && table[linProbe].getKey().equals(key)) {
                                ret = new Probes(table[linProbe].getValue(), i+1);
                                table[linProbe] = TOMBSTONE;
                                count--;
                                break;
                            }
                        }
                    }
                } else {
                    ret = new Probes(null, 1);
                }
            } else {
                for (int i = 0; i < table.length; i++) {
                    linProbe = (loc + ((i+1) - 1) + (int) Math.pow(((i+1)-1), 2)) % table.length; 

                    if (table[linProbe] == null) {
                        hardCount += i+1;
                        break;
                    }

                    if ( table[linProbe] != null && table[linProbe].getKey().equals(key)) {
                        val = table[linProbe].getValue();
                        table[linProbe] = null;
                        count--;
                        hardCount += i+1;
                        break;
                    }  
                }
                if (val != null) {

                    count = 0;
                    for (int i = 0; i < table.length; i++) {
                        hardCount++; 
                        if (table[i] != null) {
                            keyCopy = table[i].getKey();
                            valueCopy = table[i].getValue();
                            table[i] = null;
                            hardCount += this.put(keyCopy, valueCopy).getProbes();
                        }
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
    int loc; 
        boolean ret = false; 
        int linProbe;
        if (key != null) {

            loc = hash(key); 
            if (table[loc] != null ) {
                    if (table[loc].getKey().equals(key)) {
                        ret = true;
                    } else {
                        for (int i = 1; i < table.length; i++) {
                            linProbe =  (loc + ((i+1) - 1) + (int) Math.pow(((i+1)-1), 2)) % table.length; 
                            if ( table[linProbe] != null && !table[linProbe].equals(TOMBSTONE) && table[linProbe].getKey().equals(key)) {
                                ret = true;
                                break;
                            }
                            if (table[linProbe] == null) {
                                break;
                            }
                        }
                    }
            }
        }
        return ret; 
    }

    @Override
    public boolean containsValue(String value) {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        boolean ret = false; 
        if (value != null) {

            for (int i = 0; i < table.length; i++) {
                if ( table[i] != null  && table[i].getValue().equals(value)) {
                    ret = true;
                    break;
                }              
            }
        }
        return ret; 
    }
    @Override
    public int size(){
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        return count; 
    }

    @Override
    public int capacity() {
    //    throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
        return table.length; 
    }/*
    public boolean check() {
        return resizeBool;
    }
   */

}