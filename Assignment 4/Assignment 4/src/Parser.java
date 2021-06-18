import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;
import javax.swing.JFileChooser;

/**
 * The parser and interpreter. The top level parse function, a main method for
 * testing, and several utility methods are provided. You need to implement
 * parseProgram and all the rest of the parser.
 */
public class Parser {

	/**
	 * Top level parse method, called by the World
	 */
	static RobotProgramNode parseFile(File code) {
		Scanner scan = null;
		try {
			scan = new Scanner(code);
			// the only time tokens can be next to each other is
			// when one of them is one of (){},;
			scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

			RobotProgramNode n = parseProgram(scan); // You need to implement this!!!

			scan.close();
			return n;
		} catch (FileNotFoundException e) {
			System.out.println("Robot program source file not found");
		} catch (ParserFailureException e) {
			System.out.println("Parser error:");
			System.out.println(e.getMessage());
			scan.close();
		}
		return null;
	}

	/** For testing the parser without requiring the world */

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File f = new File(arg);
				if (f.exists()) {
					System.out.println("Parsing '" + f + "'");
					RobotProgramNode prog = parseFile(f);
					System.out.println("Parsing completed ");
					if (prog != null) {
						System.out.println("================\nProgram:");
						System.out.println(prog);
					}
					System.out.println("=================");
				} else {
					System.out.println("Can't find file '" + f + "'");
				}
			}
		} else {
			while (true) {
				JFileChooser chooser = new JFileChooser(".");// System.getProperty("user.dir"));
				int res = chooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					break;
				}
				RobotProgramNode prog = parseFile(chooser.getSelectedFile());
				System.out.println("Parsing completed");
				if (prog != null) {
					System.out.println("Program: \n" + prog);
				}
				System.out.println("=================");
			}
		}
		System.out.println("Done");
	}

	// Useful Patterns

	static Pattern NUMPAT = Pattern.compile("-?(0|[1-9][0-9]*)"); // ("-?(0|[1-9][0-9]*)");
	static Pattern OPENPAREN = Pattern.compile("\\(");
	static Pattern CLOSEPAREN = Pattern.compile("\\)");
	static Pattern OPENBRACE = Pattern.compile("\\{");
	static Pattern CLOSEBRACE = Pattern.compile("\\}");
	static Pattern ACT = Pattern.compile("move|turnL|turnR|takeFuel|wait|shieldOn|shieldOff|turnAround");
	static Pattern SEN = Pattern.compile("fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist");
	static Pattern OP = Pattern.compile("add|sub|mul|div");
	static Pattern VAR = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");
	//static Pattern Expr = Pattern.compile(NUMPAT||SEN||VAR||OP);

	/**
	 * PROG ::= STMT+
	 */
	static RobotProgramNode parseProgram(Scanner s) {
		// THE PARSER GOES HERE
		ArrayList<RobotProgramNode> nodes = new ArrayList<>();
		while(s.hasNext()) {
			nodes.add(parseStmt(s));
		}
		RobotProgramNode PROG = new ProgramNode(nodes);
		return PROG;
	}

	static RobotProgramNode parseStmt(Scanner s){
		RobotProgramNode node;
		if(s.hasNext("loop")){
			return parseLoop(s);//if loop then loop
		}
		else if(s.hasNext("if")){
			node = parseIf(s);//if if then do if parse
			return node;
		}
		else if(s.hasNext("while")){
			node = parseWhile(s);
			return node;
		}
		else if(s.hasNext(ACT)){
			node = parseAction(s);
			return node;
		}
		else if (s.hasNext(VAR)){
			node = parseVariable(s);
			return node;
		}
		s.next();
		fail("missing valid satement"+s.next() , s);
		return null;
	}
static HashMap<String, ExprNode> Var = new HashMap<>();
	static RobotProgramNode parseVariable(Scanner s){
		String var = null;
		variableNode variable = null;
		ExprNode assignment = null;
		if(s.hasNext(VAR)){var = s.next();}
		else{fail("missing variable",s);}
		if(s.hasNext("=")){
			s.next();
			if(s.hasNext(NUMPAT)|| s.hasNext(SEN)|| s.hasNext(VAR )|| s.hasNext(OP)){
				assignment = parseExpr(s);
				variable =new variableNode(var,assignment);
				Var.put(var,assignment);
			}
			if(!checkFor(";", s)){ fail("missing ; for move", s); }
		}
		else{
			assignment = new NUM(0);
			variable =new variableNode(var,assignment);
			Var.put(var,assignment);
			if(!checkFor(";", s)){ fail("missing ; for move", s); }
		}
		return variable;
	}

	static RobotProgramNode parseWhile(Scanner s) {
		if(!checkFor("while", s)){ fail("missing while", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		ConditionNode c = (ConditionNode) parseCondition(s);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
		BlockNode b = (BlockNode) parseBlock(s);
		return new While(c,b);
	}

	static RobotProgramNode parseAction(Scanner s) {//parsing actions(move, turnleft turn right take_feul wait)
		RobotProgramNode node = null;

		if (s.hasNext("move")) {//move statement
			node = parseMove(s);
			if(!checkFor(";", s)){ fail("missing ; for move", s); }
			return node;
		}else if(s.hasNext("turnL")){
		node = parseTurnL(s);
		if(!checkFor(";", s)){ fail("missing ; for turnL ", s); }
		return node;
	}else if(s.hasNext("wait")){
		node = parseWait(s);
		if(!checkFor(";", s)){ fail("missing ; for wait", s); }
		return node;
	}else if(s.hasNext("turnR")){
		node = parseTurnR(s);
		if(!checkFor(";", s)){ fail("missing ; for turnR", s); }
		return node;
	}else if(s.hasNext("takeFuel")){
		node = parseTakeFuel(s);
		if(!checkFor(";", s)){ fail("missing ;", s); }
		return node;
	}else if(s.hasNext("shieldOn")){
			node = parseShieldOn(s);
			if(!checkFor(";", s)){ fail("missing ;", s); }
			return node;
		}else if(s.hasNext("shieldOff")){
			node = parseShieldOff(s);
			if(!checkFor(";", s)){ fail("missing ;", s); }
			return node;
		}else if(s.hasNext("turnAround")){
			node = parseTurnAround(s);
			if(!checkFor(";", s)){ fail("missing ;", s); }
			return node;
		}
		fail("no Actions or Loop to parse",s);
		return null;
	}

	static RobotProgramNode parseLoop(Scanner s) {
		if(!checkFor("loop", s)){ fail("missing loop", s); }
		return new LoopNode(parseBlock(s));
	}
	static RobotProgramNode parseBlock(Scanner s) {
		if(!checkFor(OPENBRACE, s)){ fail("missing {" , s); }//System.out.print(s);
		List<RobotProgramNode> nodes = new ArrayList<RobotProgramNode>();
		while(!s.hasNext(CLOSEBRACE)) {
			nodes.add(parseStmt(s));
		}
		if(!checkFor(CLOSEBRACE,s)){ fail("missing }", s); }//check for whether there is a closebrace or not
		return new BlockNode(nodes);
	}

	static RobotProgramNode parseIf(Scanner s) {
		ArrayList<ConditionNode> conditions = new ArrayList<ConditionNode>();//condition list
		ArrayList<RobotProgramNode> blocks = new ArrayList<RobotProgramNode>();//block list
		boolean els = false;
		ArrayList<RobotProgramNode> ebl = new ArrayList<RobotProgramNode>();//block list
		boolean elf = false;
		ArrayList<elif> elif = new ArrayList<elif>();//block list


		if(!checkFor("if", s)){ fail("missing if", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		conditions.add(parseCondition(s));
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }//check ( )
		blocks.add(parseBlock(s));
		while (s.hasNext("elif")){
			elf = true;
			elif.add(parseElif(s));
		}
		if (s.hasNext("else")){
			s.next();
			els = true;
			ebl.add(parseBlock(s));
		}
		If n = new If(conditions, blocks,els,ebl,elf,elif);
		return n;
	}

	static elif parseElif(Scanner s){
		ArrayList<RobotProgramNode> elfb = new ArrayList<RobotProgramNode>();//block list
		ArrayList<ConditionNode> elfconditions = new ArrayList<ConditionNode>();//condition list
		if(!checkFor("elif", s)){ fail("missing elif", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		elfconditions.add(parseCondition(s));
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }//check ( )
		elfb.add(parseBlock(s));
		return new elif(elfconditions,elfb);
	}

	static ConditionNode parseCondition(Scanner s) {//parse Conditions lt gt eq
		if(s.hasNext("lt")){ return parseLT(s); }
		else if(s.hasNext("gt")){ return parseGT(s); }
		else if(s.hasNext("eq")){ return parseEQ(s); }
		else if(s.hasNext("or")){return parseOr(s);}
		else if(s.hasNext("and")){return parseAnd(s);}
		else if(s.hasNext("not")){return parseNot(s);}
		fail("expected a valid COND", s);
		return null;
	}

	static ConditionNode parseOr(Scanner s){
		ConditionNode or = null;
		ConditionNode left = null;
		ConditionNode right = null;
		if(!checkFor("or", s)){ fail("expected or", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		left = parseCondition(s);
		if(!checkFor(",", s)){ fail("missing , ", s); }
		right = parseCondition(s);
		or = new orNode(left,right);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
		return or;
	}

	static ConditionNode parseAnd(Scanner s){
		ConditionNode and = null;
		ConditionNode left = null;
		ConditionNode right = null;
		if(!checkFor("and", s)){ fail("expected and", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		left = parseCondition(s);
		if(!checkFor(",", s)){ fail("missing , ", s); }
		right = parseCondition(s);
		and = new andNode(left,right);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
		return and;
	}

	static ConditionNode parseNot(Scanner s){
		ConditionNode not = null;
		ConditionNode con = null;
		if(!checkFor("not", s)){ fail("expected not", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		con = parseCondition(s);
		not = new notNode(con);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
		return not;
	}

	static ConditionNode parseLT(Scanner s) {//less than
		if(!checkFor("lt", s)){ fail("expected lt", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		ExprNode lhs = parseExpr(s);
		if(!checkFor(",", s)){ fail("missing , ", s); }
		ExprNode rhs = parseExpr(s);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
		return new lt(lhs, rhs);
	}

	static ConditionNode parseGT(Scanner s) {//greater than
		if(!checkFor("gt", s)){ fail("missing gt", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		ExprNode lhs = parseExpr(s);
		if(!checkFor(",", s)){ fail("missing ,", s); }
		ExprNode rhs = parseExpr(s);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
		return new gt(lhs, rhs);
	}

	static ConditionNode parseEQ(Scanner s) {// equals
		if(!checkFor("eq", s)){ fail("missing eq", s); }
		if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
		ExprNode lhs = parseExpr(s);
		if(!checkFor(",", s)){ fail("missing , ", s); }
		ExprNode rhs = parseExpr(s);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
		return new eq(lhs, rhs);
	}



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
static RobotProgramNode parseMove(Scanner s){
	ExprNode n = null;
	moveNode m = new moveNode();
	if(!checkFor("move", s)){ fail("missing move", s); }
	if(s.hasNext(OPENPAREN)){//stage 2 check () but haven't done move with parameter
		if(!checkFor(OPENPAREN, s)){ fail("missing a ( ",s); }
		n = parseExpr(s);
		m = new moveNode(n);
		if(!checkFor(CLOSEPAREN, s)){ fail("missing a ) ",s); }
	}
	return  m;
}

	static RobotProgramNode parseTurnL(Scanner s) {//TurnF
		if(!checkFor("turnL", s)){ fail("missing turnL", s); }
		return new turnLNode();
	}

	static RobotProgramNode parseTurnR(Scanner s) {//TurnR
		if(!checkFor("turnR", s)){ fail("missing turnR", s); }
		return new turnRNode();
	}


	static RobotProgramNode parseTakeFuel(Scanner s) {//TakeFuel
		if(!checkFor("takeFuel", s)){ fail("missing takeFuel", s); }
		return new takeFuelNode();
	}

	static RobotProgramNode parseTurnAround(Scanner s) {//TurnAround
		if(!checkFor("turnAround", s)){ fail("missing turnAround", s); }
		return new turnAroundNode();
	}

	static RobotProgramNode parseWait(Scanner s) {
		ExprNode n = null;
		waitNode w = new waitNode();
		if(!checkFor("wait", s)){ fail("missing wait", s); }
		if(s.hasNext(OPENPAREN)){
			if(!checkFor(OPENPAREN, s)){ fail("missing a ( ",s); }
			n = parseExpr(s);
			w = new waitNode(n);
			if(!checkFor(CLOSEPAREN, s)){ fail("missing a ) ",s); }
		}
		return w;//////no parameter at stage 0 and 1
	}

	private static ExprNode parseExpr(Scanner s) {
		if(s.hasNext(SEN)){ return parseSensor(s); }
		else if(s.hasNext(NUMPAT)){ return parseNum(s); }
		else if(s.hasNext(OP)){return parseOP(s);}
		else if(s.hasNext(VAR)){
			String v = s.next();
			ExprNode V =Var.get(v);
			if (!Var.keySet().contains(v)){return new NUM(0);}//If a variable being accessed which is not in the map should be added and given the value 0
			return V;
		}
		fail("missing valid Expression (Sensor or Number)"+ s.next(), s);
		return null;
	}

	static OP parseOP(Scanner s){
		OP n = null;
		ExprNode left = null;
		ExprNode right = null;
		if(s.hasNext("add")){
			if(!checkFor("add", s)){ fail("missing add", s); }
			if(!checkFor(OPENPAREN, s)){ fail("missing a ( ",s); }
			left = parseExpr(s);
			if(!checkFor(",", s)){ fail("missing a ,",s); }
			right = parseExpr(s);
			if(!checkFor(CLOSEPAREN, s)){ fail("missing a )",s); }
			n = new addNode(left,right);
		}
		else if(s.hasNext("sub")){
			if(!checkFor("sub", s)){ fail("missing add", s); }
			if(!checkFor(OPENPAREN, s)){ fail("missing a ( ",s); }
			left = parseExpr(s);
			if(!checkFor(",", s)){ fail("missing a ,",s); }
			right = parseExpr(s);
			if(!checkFor(CLOSEPAREN, s)){ fail("missing a )",s); }
			n = new subNode(left,right);
		}
		else if(s.hasNext("mul")){
			if(!checkFor("mul", s)){ fail("missing add", s); }
			if(!checkFor(OPENPAREN, s)){ fail("missing a ( ",s); }
			left = parseExpr(s);
			if(!checkFor(",", s)){ fail("missing a ,",s); }
			right = parseExpr(s);
			if(!checkFor(CLOSEPAREN, s)){ fail("missing a )",s); }
			n = new mulNode(left,right);
		}
		else if(s.hasNext("div")){
			if(!checkFor("div", s)){ fail("missing add", s); }
			if(!checkFor(OPENPAREN, s)){ fail("missing a ( ",s); }
			left = parseExpr(s);
			if(!checkFor(",", s)){ fail("missing a ,",s); }
			right = parseExpr(s);
			if(!checkFor(CLOSEPAREN, s)){ fail("missing a )",s); }
			n = new divNode(left,right);
		}
		return n;



	}

	static ExprNode parseNum(Scanner s) {
		if(s.hasNext(NUMPAT)){
			return new NUM(s.nextInt());
		}fail("missing numbers", s);
		return null;
	}
	static Sensor parseSensor(Scanner s) {//parse Sensors : fuelLeft  oppLR  oppFB  numBarrels  barrelLR  barrelFB  wallDist
		if(s.hasNext("fuelLeft")){
			if(!checkFor("fuelLeft", s)){ fail("missing fuelLeft", s); }
			return new FuelLeftNode();
		}
		else if(s.hasNext("oppLR")){
			if(!checkFor("oppLR", s)){ fail("missing oppLR", s); }
			return new oppLRNode();
		}
		else if(s.hasNext("oppFB")){
			if(!checkFor("oppFB", s)){ fail("missing oppFB", s); }
			return new oppFBNode();
		}
		else if(s.hasNext("numBarrels")){
			if(!checkFor("numBarrels", s)){ fail("missing numBarrels", s); }
			return new numBarrelsNode();
		}
		else if(s.hasNext("barrelLR")){
			return parseBarrelLR(s);
		}
		else if(s.hasNext("barrelFB")){
			return parseBarrelFB(s);
		}
		else if(s.hasNext("wallDist")){ //
			if(!checkFor("wallDist", s)){ fail("missing wallDist", s); }
			return new wallDistNode();
		}
		fail("need a valid sensor", s);
		return null;
	}

	static Sensor parseBarrelLR(Scanner s) {
		if(!checkFor("barrelLR", s)){ fail("missing barrelLR", s); }
		ExprNode n = new NUM(0);
		if(s.hasNext(OPENPAREN)){
			if(!checkFor(OPENPAREN, s)){ fail("missing ( ", s); }
			n = parseExpr(s);
			if(!checkFor(CLOSEPAREN, s)){ fail("missing ) ", s); }
			return new barrelLRNode(n);
		}
		return new barrelLRNode();
	}

	static Sensor parseBarrelFB(Scanner s) {
		if(!checkFor("barrelFB", s)){ fail("missing barrelFB", s); }
		ExprNode n = new NUM(0);
		if(s.hasNext(OPENPAREN)){
			if(!checkFor(OPENPAREN, s)){ fail("missing ( s", s); }
			n = parseExpr(s);
			if(!checkFor(CLOSEPAREN, s)){ fail("missing )", s); }
			return new barrelFBNode(n);
		}
		return new barrelFBNode();
	}

	static RobotProgramNode parseShieldOn(Scanner s){//ShieldOn
		if(!checkFor("shieldOn", s)){ fail("missing shieldOn", s); }
		return new shieldOnNode();
	}

	static RobotProgramNode parseShieldOff(Scanner s){//ShieldOff
		if(!checkFor("shieldOff", s)){ fail("missing shieldOff", s); }
		return new shieldOffNode();
	}


	// utility methods for the parser

	/**
	 * Report a failure in the parser.
	 */
	static void fail(String message, Scanner s) {
		String msg = message + "\n   @ ...";
		for (int i = 0; i < 5 && s.hasNext(); i++) {
			msg += " " + s.next();
		}
		throw new ParserFailureException(msg + "...");
	}

	/**
	 * Requires that the next token matches a pattern if it matches, it consumes
	 * and returns the token, if not, it throws an exception with an error
	 * message
	 */
	static String require(String p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	static String require(Pattern p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	/**
	 * Requires that the next token matches a pattern (which should only match a
	 * number) if it matches, it consumes and returns the token as an integer if
	 * not, it throws an exception with an error message
	 */
	static int requireInt(String p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	static int requireInt(Pattern p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	/**
	 * Checks whether the next token in the scanner matches the specified
	 * pattern, if so, consumes the token and return true. Otherwise returns
	 * false without consuming anything.
	 */
	static boolean checkFor(String p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

	static boolean checkFor(Pattern p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

}

// You could add the node classes here, as long as they are not declared public (or private)
