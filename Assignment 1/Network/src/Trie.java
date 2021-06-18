import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Trie {
        public List<Stops> stops = new ArrayList<>();
        public Map<Character, Trie> children = new HashMap<>();

        public void add(char[] word, Stops s) {
                // Set	node to	the	root of	the	trie;
                for (char c: word) {
                        if(!stops.contains(c)) {
                                Trie trienode = new Trie();
                                children.put(c, trienode);//create	a	new	child	of	node,	connecting	to	node via	c
                        }
                }
                stops.add(s);
        }

        public List<Stops> get(char[] word) {
               for(char c : word){
                       if(!stops.contains(c)) {
                              return null;
                       }
                  //     children.put(c, );
               }
               return stops;//return stops
        }

        public List<Stops> getAll(char[] prefix) {
                List<Stops> results = new ArrayList<Stops>();
                for (char c : prefix) {
                        if(!stops.contains(c)) {
                                return null;
                        }
                }
                getAllFrom(children.get(prefix),results);
                return stops;
        }

        public void getAllFrom(Trie node, List<Stops> results) {
                for(Stops stop: stops) {
                        results.add(stop);
                }
                for (Trie child: children.values()){
                        getAllFrom(child,results);
                }

        }


}