package log;

public class LogDetailsModel {
	private String eventCategory;
	private String eventID;
	private String eventSource;
	private String eventType;
	private String recordNumber;
	private String eventTimeGenerated;
	private String eventTimeWritten;
	private String eventData;
	
	public LogDetailsModel() {
		super();
	}
	
	public LogDetailsModel(String eventCategory, String eventID, String eventSource, String eventType,
			String recordNumber, String eventTimeGenerated, String eventTimeWritten, String eventData) {
		super();
		this.eventCategory = eventCategory;
		this.eventID = eventID;
		this.eventSource = eventSource;
		this.eventType = eventType;
		this.recordNumber = recordNumber;
		this.eventTimeGenerated = eventTimeGenerated;
		this.eventTimeWritten = eventTimeWritten;
		this.eventData = eventData;
	}



	public String getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(String recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getEventTimeGenerated() {
		return eventTimeGenerated;
	}

	public void setEventTimeGenerated(String eventTimeGenerated) {
		this.eventTimeGenerated = eventTimeGenerated;
	}

	public String getEventTimeWritten() {
		return eventTimeWritten;
	}

	public void setEventTimeWritten(String eventTimeWritten) {
		this.eventTimeWritten = eventTimeWritten;
	}

	public String getEventData() {
		return eventData;
	}

	public void setEventData(String eventData) {
		this.eventData = eventData;
	}
}
