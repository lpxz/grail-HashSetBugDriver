import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sun.java_cup.internal.lalr_item;

//import benchmarks.dstest.SimpleObject;
//import benchmarks.instrumented.java.util.Collections;
//import benchmarks.instrumented.java.util.HashSet;
//import benchmarks.instrumented.java.util.Set;
//import benchmarks.jpf_test_cases.MyRandom;



public class Bug2 {

	static Set s1 = Collections.synchronizedSet(new HashSet());
    static Set s2 = Collections.synchronizedSet(new HashSet());
    
    static String hundred = "100";
    static String twohundred = "200";
    static String threehundred = "300";
    
    static{
    	s1.add((hundred));
    	s1.add(twohundred);
        s2.add(hundred);
        s2.add(threehundred);
    }
    
	public static void main(String[] args) throws Exception{
		Thread t1 = new Thread(){
			public void run()
			{
				 s1.addAll(s2);// should be atomic for s2 too! no any updates to s2 are allowed.
			}
		};
		
		Thread t2 = new Thread(){
			public void run()
			{
				s2.clear();//any update to S2 causes the AV!
				
			}
		};
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
		System.out.println(s1.size());// may be 1 or 2. depending on whether s2 already removes the item.

	}

}
