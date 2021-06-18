public class Tuples {

        int offset;
        int length;
        Character nextCharacter;

        public Tuples(int offset, int length, Character nextCharacter){
                this.offset = offset;
                this.length = length;
                this.nextCharacter = nextCharacter;
        }

        public String toString() {
                return "[" + offset + ", " + length + ", " + nextCharacter + "]";
        }
}
