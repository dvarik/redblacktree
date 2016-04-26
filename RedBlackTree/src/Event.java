/**
 * Class for an event object
 * 
 * @author dhanusha
 *
 */
public class Event {

	//Unique event id
	int eventId;

	// Count of that event
	int count;

	/**
	 * Constructor to create event object
	 * @param evtId
	 * @param cnt
	 */
	public Event(int evtId, int cnt) {
		this.eventId = evtId;
		this.count = cnt;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Event Id: " + eventId + " Count: " + count;
	}
}
