package bpt;
import org.junit.Test;
import java.util.Iterator;


import static org.junit.Assert.*;

/**
 * A jUnit test suite for {@link BinaryPatriciaTrie}.
 *
 * @author Yosefe Eshete
 */
public class StudentTests {


    @Test public void testEmptyTrie() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        assertTrue("Trie should be empty",trie.isEmpty());
        assertEquals("Trie size should be 0", 0, trie.getSize());

        assertFalse("No string inserted so search should fail", trie.search("0101"));

    }

    @Test public void testFewInsertionsWithSearch() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        assertTrue("String should be inserted successfully",trie.insert("00000"));
        assertTrue("String should be inserted successfully",trie.insert("00011"));
        assertFalse("Search should fail as string does not exist",trie.search("000"));

    }


    //testing isEmpty function
    @Test 
    public void testFewInsertionsWithDeletion() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        //trie.insert("000");
        //trie.insert("001");
       // trie.insert("011");
        trie.insert("1001");
        trie.insert("1");
        //trie.insert("000001");
        trie.insert("111");
        trie.insert("111111");
       // trie.insert("1010101");
        // trie.insert("1010000000");
/*
        assertTrue("String should find successfully",trie.search("000"));
        assertTrue("String should find successfully",trie.search("001"));
        assertTrue("String should find successfully",trie.search("011"));
        assertTrue("String should find successfully",trie.search("1001"));
        assertTrue("String should find successfully",trie.search("1"));
        
        
        assertTrue("String should find successfully",trie.search("000001"));
        assertTrue("String should find successfully",trie.search("111"));
        assertTrue("String should find successfully",trie.search("111111"));
        assertTrue("String should find successfully",trie.search("1010101"));
        assertTrue("String should find successfully",trie.search("1010000000"));

        assertFalse("Insert should fail as string alreadt exists",trie.insert("1"));
        assertFalse("Insert should fail as string alreadt exists",trie.insert("1001"));

*/

        


        assertFalse("After inserting five strings, the trie should not be considered empty!", trie.isEmpty());
        assertEquals("After inserting 10 strings, the trie should report five strings stored.", 10, trie.getSize());

        trie.delete("0"); // Failed deletion; should affect exactly nothing.
        assertEquals("After inserting five strings and requesting the deletion of one not in the trie, the trie " +
              "should report five strings stored.", 10, trie.getSize());
        trie.insert("0");
        assertEquals("After inserting 11 strings, the trie should report five strings stored.", 11, trie.getSize());
        assertTrue("String should find successfully",trie.search("0"));
        assertTrue("After inserting five strings and requesting the deletion of one not in the trie, the trie had some junk in it!",
                trie.isJunkFree());

        trie.delete("0");
        assertEquals("After inserting 11 strings, the trie should report five strings stored.", 10, trie.getSize());
        assertFalse("String should not be find successfully",trie.search("0"));

        trie.delete("111"); // Successful deletion
        assertEquals("After inserting 10 strings and deleting 1 of them, the trie should report 9 strings.", 9, trie.getSize());
        assertTrue("After inserting five strings and deleting one of them, the trie had some junk in it!",
                trie.isJunkFree());
    }



    @Test
    public void test1() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();
        String ret = "";
        Iterator<String> it;
        int chk = 0;

        trie.insert("000");
        trie.insert("001");
       trie.insert("011");
        //trie.insert("1001");
        //trie.insert("1");
        //trie.insert("000001");
       // trie.insert("111");
       // trie.insert("111111");
      /* trie.insert("1010101");
         trie.insert("1010000000");
*/
//assertTrue("String should find successfully",trie.search("000"));
     //   trie.delete("111");
        trie.delete("001");
        it = trie.inorderTraversal();

        while (it.hasNext() ) {
            ret = ret.concat("*"+ it.next()+"*" );
        }

        assertEquals(ret, 30, trie.getSize());
    }

    @Test
    public void test2() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();
        String ret = "";
        Iterator<String> it;
        int chk = 0;

        trie.insert("0000");
        trie.insert("001");
        trie.insert("011");
        trie.insert("1001");
       trie.insert("1");
        trie.insert("000001");
        trie.insert("111");
        trie.insert("111111");
        trie.insert("1010101");
        trie.insert("1010000000");



        ret = ret.concat("*"+ trie.getLongest()+"*" );
        

        assertEquals(ret, 30, trie.getSize());


    }




}