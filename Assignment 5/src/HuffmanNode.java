public class HuffmanNode implements Comparable<HuffmanNode> {

    char letter;
    int frequency;
    HuffmanNode left;
    HuffmanNode right;
    HuffmanNode parent;
    String huffmanCode= "";//for saving HuffmanCode

    public HuffmanNode(char letter, int frequency) {
        super();
        this.letter = letter;
        this.frequency = frequency;
    }
    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        super();
        this.left = left;
        this.right = right;
        this.left.parent = parent;
        this.right.parent = parent;
    }

    public int compareTo(HuffmanNode o) {
        if (this.frequency < o.getFrequency()) {
            return -1;
        } else if (this.frequency > o.getFrequency()) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getFrequency() {
        return frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public String getCoding() {
        return this.huffmanCode;
    }

}