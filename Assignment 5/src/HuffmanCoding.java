import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */
public class HuffmanCoding {
	Queue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>();
	HashMap<Character, Integer> frequencyMap;//store frequency
	HashMap<Character, String> huffmanCodeMap;//store code
	HuffmanNode root;
	/**
	 * This would be a good place to compute and store the tree.
	 */

	public HuffmanCoding(String text) {
		HashMap<Character, Integer> freq = new HashMap<>();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if(!freq.containsKey(c)) {
				freq.put(c, 1);
			}else {
				freq.put(c, freq.get(c) + 1);
			}

			this.frequencyMap= freq;
			this.queue = createTree(frequencyMap);

			HashMap<Character, String> huffmanMap = new HashMap<>();
			Stack<HuffmanNode> stack = new Stack<>();
			stack.push(this.root);

			while (!stack.isEmpty()) {
				HuffmanNode popedNode = stack.pop();
				if (popedNode.left != null ) {
					popedNode.left.huffmanCode = (popedNode.huffmanCode + '0');
					stack.push(popedNode.left);
				}
				if (popedNode.right !=null){
					popedNode.right.huffmanCode=  (popedNode.huffmanCode+ '1');
					stack.push(popedNode.right);
				} else {
					huffmanMap.put(popedNode.letter, popedNode.getCoding());
				}
			}
			this.huffmanCodeMap = huffmanMap;
		}

	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 */
	public String encode(String text) {
		StringBuilder encode = new StringBuilder();
		for(int index = 0; index< text.length(); index++){
			char c = text.charAt(index);
			encode.append(huffmanCodeMap.get(c));
		}
		return encode.toString();
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		HuffmanNode root = this.root;
		StringBuilder decoded = new StringBuilder();
		//String s = "";
		char[] charArray = encoded.toCharArray();
		HuffmanNode pointer = this.root;
		int i=0;
		while(i < charArray.length){
			char Hcode = charArray[i];
			if(Hcode == '0') {
				pointer = pointer.left;
			}else pointer = pointer.right;
			if(pointer.left==null|| pointer.right==null) {
				decoded.append(pointer.letter);
				pointer = root;
			}
			i++;
		}
		return decoded.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		StringBuilder s = new StringBuilder();
		for (Map.Entry<Character, String> character : huffmanCodeMap.entrySet()) {
			s = s.append(character.getKey()).append(":").append(character.getValue()).append("\n");
		}
		return s.toString();
	}


	public PriorityQueue createTree(HashMap<Character, Integer> freqencyMap) {
		PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();
		for(Map.Entry<Character, Integer> c : freqencyMap.entrySet()) {
			HuffmanNode n = new HuffmanNode(c.getKey(), c.getValue());
			queue.add(n);
		}
		while(queue.size()>1) {
			HuffmanNode left = queue.poll();
			HuffmanNode right = queue.poll();
			HuffmanNode parent = new HuffmanNode(left, right);
			int parentFrequency = left.frequency + right.frequency;
			parent.setFrequency(parentFrequency);
			left.parent=parent;
			right.parent=parent;
			queue.add(parent);
		}
		this.root = queue.peek();
		return queue;
	}
}
