public class BoyerMooreSearch {
    private  String text;
    private  String pattern;
    private int[] badtable;
    private int[] goodtable;

    public  BoyerMooreSearch(String pattern, String text){
        this.pattern = pattern;
        this.text = text;
        this.badtable = new int[256];
        this.goodtable = new int[pattern.length()];
    }
    public int search(String parttern, String text){
        long start = System.currentTimeMillis();
        if(parttern.length()==0 || text.length()==0) return -1;
        char[] P = parttern.toCharArray();
        char[] T = text.toCharArray();
        for(int i=0; i < P.length;i++){
            badtable [P[i]] = P.length-1-i;
        }
        this.badtable = badtable;
        int lastPrefixnum = P.length;
        for(int index = P.length; index>0 ; index--){
            if(checkPrefix(P,index)){
                lastPrefixnum = index;
            }
            goodtable[P.length - index] = lastPrefixnum + P.length - index;
        }
        for (int i=0; i <P.length-1; i++){
            int suffixLength = 0;
            while(P[i-suffixLength] == P[P.length-1-suffixLength]){ suffixLength++;}
            goodtable[suffixLength] = P.length-1 + i +suffixLength;
        }
        this.goodtable = goodtable;
        for (int i = P.length - 1, j = i; i < T.length;  i += Math.max(goodtable[P.length - 1 - j], badtable[T[i]])){//max is the maximum index to shift
            for (j = P.length - 1; P[j] == T[i]; i--, j--) {
                if (j == 0) {
                    long end = System.currentTimeMillis();
                    System.out.println("BoyerMooreSearsh : "+Math.abs(end-start));
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean checkPrefix(char[] pattern, int index) {
        for(int i=0;i<pattern.length-index;i++){
            if(pattern[i]!= pattern[i=index]) return false;//not match then return false
        }
        return true;
    }
}