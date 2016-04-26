import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * UI Console class for event counter operations
 * 
 * @author dhanusha
 *
 */
public class bbst {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Please enter file name as argument");
			System.exit(0);
		}

		RedBlackEventTree eventCounter = new RedBlackEventTree();
		long start = System.currentTimeMillis();
		eventCounter.buildTreeFromFile(args[0]);
		System.out.println("Time(sec):" + (System.currentTimeMillis() - start));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			String commandString = null;
			ArrayList<String> commandArray = new ArrayList<String>();
			commandArray.add(0, "increase");
			commandArray.add(1, "reduce");
			commandArray.add(2, "count");
			commandArray.add(3, "inrange");
			commandArray.add(4, "next");
			commandArray.add(5, "previous");

			while (!(commandString = reader.readLine()).equals("quit")) {

				String[] cArgs = commandString.split(" ");

				switch (commandArray.indexOf(cArgs[0])) {

				case 0:
					System.out.println(eventCounter.increase(Integer.parseInt(cArgs[1]), Integer.parseInt(cArgs[2])));
					break;
				case 1:
					System.out.println(eventCounter.reduce(Integer.parseInt(cArgs[1]), Integer.parseInt(cArgs[2])));
					break;
				case 2:
					System.out.println(eventCounter.count(Integer.parseInt(cArgs[1])));
					break;
				case 3:
					System.out.println(eventCounter.inRange(Integer.parseInt(cArgs[1]), Integer.parseInt(cArgs[2])));
					break;
				case 4:
					Event e = eventCounter.next(Integer.parseInt(cArgs[1]));
					System.out.println(e.getEventId() + " " + e.getCount());
					break;
				case 5:
					Event p = eventCounter.prev(Integer.parseInt(cArgs[1]));
					System.out.println(p.getEventId() + " " + p.getCount());
					break;
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
