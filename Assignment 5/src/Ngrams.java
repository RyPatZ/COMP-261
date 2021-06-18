import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.*;

/**
 * Ngrams predictive probabilities for text
 */
public class Ngrams {
	/**
	 * The constructor would be a good place to compute and store the Ngrams probabilities.
	 * Take uncompressed input as a text string, and store a List of Maps. The n-th such
     * Map has keys that are prefixes of length n. Each value is itself a Map, from 
     * characters to floats (this is the probability of the char, given the prefix).
	 */
    List<Map<String,Map<Character, Float>>> ngram = new ArrayList <Map<String,Map<Character, Float>>>();  /* nb. suggestion only - you don't have to use
                                                     this particular data structure */
    Map <String , String > CharProb = new HashMap<>();
	Map <String , Float > CharProb2 = new HashMap<>();


    public void Ngrams (String input){
    	ngram.add(getNgrams(input,0));
		ngram.add(getNgrams(input,1));
		ngram.add(getNgrams(input,2));
		ngram.add(getNgrams(input,3));
		ngram.add(getNgrams(input,4));
		ngram.add(getNgrams(input,5));
	}

	public String getNgramProb(String text){
    	StringBuilder P = new StringBuilder();
    	if (text.contains("0")) {
			P.append("n = 0 :\n");
			for (String s : ngram.get(0).keySet()) {
				P.append("prefix :").append(s).append(": \n");
				for (Character c : ngram.get(0).get(s).keySet()) {
					P.append(c).append(": ").append(ngram.get(0).get(s).get(c)).append("\n");
				}
			}
		}
    	else if (text.contains("1")) {
			P.append("n = 1 :\n");
			for (String s : ngram.get(1).keySet()) {
				P.append("prefix :").append(s).append(": \n");
				for (Character c : ngram.get(1).get(s).keySet()) {
					P.append(c).append(": ").append(ngram.get(1).get(s).get(c)).append("\n");
				}
			}
		}
		else if (text.contains("2")) {
			P.append("n = 2 :\n");
			for (String s : ngram.get(2).keySet()) {
				P.append("prefix :").append(s).append(": \n");
				for (Character c : ngram.get(2).get(s).keySet()) {
					P.append(c).append(": ").append(ngram.get(2).get(s).get(c)).append("\n");
				}
			}
		}
		else if (text.contains("3")) {
			P.append("n = 3 :\n");
			for (String s : ngram.get(3).keySet()) {
				P.append("prefix :").append(s).append(": \n");
				for (Character c : ngram.get(3).get(s).keySet()) {
					P.append(c).append(": ").append(ngram.get(3).get(s).get(c)).append("\n");
				}
			}
		}
		else if (text.contains("4")) {
			P.append("n = 4 :\n");
			for (String s : ngram.get(4).keySet()) {
				P.append("prefix :").append(s).append(": \n");
				for (Character c : ngram.get(4).get(s).keySet()) {
					P.append(c).append(": ").append(ngram.get(4).get(s).get(c)).append("\n");
				}
			}
		}
		else if (text.contains("5")) {
			P.append("n = 5 :\n");
			for (String s : ngram.get(5).keySet()) {
				P.append("prefix :").append(s).append(": \n");
				for (Character c : ngram.get(5).get(s).keySet()) {
					P.append(c).append(": ").append(ngram.get(5).get(s).get(c)).append("\n");
				}
			}
		}
		else{
			P.append("invalid text ");
		}
    	return P.toString();
	}

	public Map<String, Map<Character, Float>> getNgrams(String input , int n) {
		Map<String,Map<Character, Integer>> counts = new HashMap<String, Map<Character, Integer>> ();
		for (int i = 0 ; i+n < input.length(); i++ ){
			String prefix = input.substring(i , i+ n );
			String s = input.substring(i + n, i + n + 1);
			Character c = s.charAt(0);

			if (counts.keySet().contains(prefix)){
				if (counts.get(prefix).keySet().contains(c)){
					counts.get(prefix).put(c, counts.get(prefix).get(c)+1);
				}
				else{
					counts.get(prefix).put(c,1);
				}
			}
			else {
				Map<Character, Integer> a = new HashMap<>();
				a.put(c,1);
				counts.put(prefix,a);
			}
		}
		Map<String,Map<Character, Float>> probs = findCharProbs(counts);
		//System.out.println(probs.toString());
		return probs;
	}

	public Map<String,Map<Character, Float>> findCharProbs(Map<String,Map<Character, Integer>> counts) {
		Map<String,Map<Character, Float>> a = new HashMap<String,Map<Character, Float>>();
		for (String k : counts.keySet()){
			Map<Character, Integer> c = counts.get(k);
			int total = 0;
			for (Character i : c.keySet()){
				total += c.get(i);
			}
			Map <Character, Float> prob = new HashMap<Character, Float>();
			for (Character i : c.keySet()){
				Float p = (float)c.get(i)/total;
				prob.put(i,p);
			}
			a.put(k,prob);
		}
		return a;
	}

	/**
	 * Take a string, and look up the probability of each character in it, under the Ngrams model.
     * Returns a List of Floats (which are the probabilities).
	 */
	public Float findCharProbs(String mystring) {
		List <Float> probs = new ArrayList<>();

		for (int i =0; i < mystring.length(); i++){
			int n = 5 ;
			if (mystring.length() - i < n){
				n = mystring.length() - i;
			}
			while(n >= 0){
				String s = mystring.substring(i , i+n);
				String c = null;
				if(i + n + 1 <= mystring.length()) {
					c = mystring.substring(i + n, i + n + 1);
				}
				int match = 0;
				for (String prefix :ngram.get(n).keySet()){
					if (s.equals(prefix)){
						for (Character ch : ngram.get(n).get(prefix).keySet()) {
							if (c != null) {
								if (c.equals(ch.toString())) {
									match = 1;
									probs.add(ngram.get(n).get(prefix).get(ch));
									CharProb.put(s,c);
									CharProb2.put(c,ngram.get(n).get(prefix).get(ch));
									n = -1;
								}
							}
						}

					}
				}
				if(match == 0){
					n -=1;
				}
			}
		}
		return calcTotalLogProb(probs);
	}

	/**
	 * Take a list of probabilites (floats), and return the sum of the logs (base 2) in the list.
	 */
	public float calcTotalLogProb(List<Float> charProbs) {
		double LogProb = 0;
		for (Float f : charProbs){
			double d = f;
			LogProb += Math.log10(d);
		}
		float LP = (float)LogProb;
		return LP;
	}
}
