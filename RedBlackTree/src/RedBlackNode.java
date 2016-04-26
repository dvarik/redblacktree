/**
 * Class for Red Black Nodes
 * @author dhanusha
 *
 */
public class RedBlackNode {

	
	RedBlackNode leftChild;
	
	RedBlackNode rightChild;
	
	RedBlackNode parent;
	
	Color color;
	
	int eventId;
	
	int count;
	
	public RedBlackNode(){
		
		this(0,0);
	}
	
	public RedBlackNode(int evid, int count){
		
		this.eventId = evid;
    	this.count = count;
    	this.leftChild = null;
        this.rightChild = null;
        this.parent = null;
        color = Color.BLACK;
	}
	
	
    /**
     * Enum to save color of a RedBlackNode
     * @author dhanusha
     *
     */
    public static enum Color {
    	RED,
    	BLACK;
    }
    
	@Override
	public String toString() {

		return ((this.color == Color.RED) ? "Color: Red " : "Color: Black ") + eventId + " " + count + "\n";
	}
	
}
