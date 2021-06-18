import java.util.ArrayList;
import java.util.Map;

/**
 * A new instance of LempelZiv is created for every run.
 */
public class LempelZiv {
	private int WINDOW_SIZE = 100;
	ArrayList<Tuples> tuples = new ArrayList<>();
	/**
	 * Take uncompressed input as a text string, compress it, and return it as a
	 * text string.
	 */
	public String compress(String input) {
		StringBuilder output = new StringBuilder();
		int cursor=0;
		while(cursor<input.length()) {
			int length=0;
			int prevMatch=-1;
			int start;
			while(true) {
				start = (cursor < WINDOW_SIZE) ? 0 : cursor - WINDOW_SIZE;
				String pattern = input.substring(cursor, cursor + length);
				String s = input.substring(start, cursor);
				int match = s.indexOf(pattern);
				if (cursor + length >= input.length()) {
					match = -1;
				}
				if (match > -1) {
					prevMatch = match;
					length++;
				}
				else {
					int offset = (prevMatch > -1) ? s.length() - prevMatch : 0;
					char nextChar = input.charAt(cursor + length - 1);
					Tuples tuple = new Tuples(offset, length - 1, nextChar);//new a tuple
					tuples.add(tuple);
					output.append(tuple.toString());
					cursor = cursor + length;
					break;
				}
			}
		}
		return output.toString();
	}

	/**
	 * Take compressed input as a text string, decompress it, and return it as a
	 * text string.
	 */
	public String decompress(String compressed) {
		StringBuilder output = new StringBuilder();
		int cursor =0;
		for(Tuples tuple:tuples){
			if(tuple.length ==0 && tuple.offset ==0){
				cursor++;
				output.append(tuple.nextCharacter);
			}else{
				output.append(output.substring(cursor - tuple.offset,cursor - tuple.offset + tuple.length));
				cursor = cursor + tuple.length;
				if (tuple.nextCharacter!=null) {
					output.append(tuple.nextCharacter);
				}
				cursor++;
			}
		}
		return output.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't want to. It is called on every run and its return
	 * value is displayed on-screen. You can use this to print out any relevant
	 * information from your compression.
	 */
	public String getInformation() {
		StringBuilder s = new StringBuilder();
		for (Tuples t : tuples) {
			s.append(t).append("\n");
		}
		return s.toString();
	}
}
