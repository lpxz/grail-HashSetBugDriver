import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.java_cup.internal.lalr_item;

//import benchmarks.dstest.SimpleObject;
//import benchmarks.instrumented.java.util.Collections;
//import benchmarks.instrumented.java.util.HashSet;
//import benchmarks.instrumented.java.util.Set;
//import benchmarks.jpf_test_cases.MyRandom;



public class Bug3 {

	static Set s1 = Collections.synchronizedSet(new HashSet());
    static Set s2 = Collections.synchronizedSet(new HashSet());
    
    static String hundred = "100";
    static String twohundred = "200";
    static String threehundred = "300";
    
    static{
    	s1.add((hundred));
    	s1.add(twohundred);
        s2.add(hundred);
    }
    
    //significant bug!
	public static void main(String[] args) throws Exception{
		Thread t1 = new Thread(){
			public void run()
			{
				boolean containsAll= s1.containsAll(s2);// should be atomic for s2 too! no any updates to s2 are allowed.
				if(!containsAll)
				{
					throw new RuntimeException("interleavings may occur.");
				}
			}
		};
		
		Thread t2 = new Thread(){
			public void run()
			{
				s2.add(threehundred);// yes, any update to S2.
				
			}
		};
		
//		Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF8
//				Exception in thread "Thread-0" 2
//				java.util.ConcurrentModificationException
//					at java.util.HashMap$HashIterator.nextEntry(HashMap.java:841)
//					at java.util.HashMap$KeyIterator.next(HashMap.java:877)
//					at java.util.AbstractCollection.containsAll(AbstractCollection.java:285)
//					at java.util.Collections$SynchronizedCollection.containsAll(Collections.java:1588)
//					at Bug3$1.run(Bug3.java:34)

		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
		System.out.println(s1.size());// may be 1 or 2. depending on whether s2 already removes the item.

	}

}
