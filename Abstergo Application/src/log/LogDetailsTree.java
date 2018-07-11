package log;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogDetailsTree extends RecursiveTreeObject<LogDetailsTree> {
	private StringProperty eventCategory;
	private StringProperty eventID;
	private StringProperty eventSource;
	private StringProperty eventType;
	private StringProperty recordNumber;
	private StringProperty eventTimeGenerated;
	private StringProperty eventTimeWritten;
	private StringProperty eventData;
	
	public LogDetailsTree(String recordNumber, String eventCategory) {
		this.recordNumber = new SimpleStringProperty(recordNumber);
		this.eventCategory = new SimpleStringProperty(eventCategory);
	}
	
	public LogDetailsTree(String eventCategory, String eventID, String eventSource, String eventType,
			String recordNumber, String eventTimeGenerated, String eventTimeWritten, String eventData) {
		this.eventCategory = new SimpleStringProperty(eventCategory);
		this.eventID = new SimpleStringProperty(eventID);
		this.eventSource = new SimpleStringProperty(eventSource);
		this.eventType = new SimpleStringProperty(eventType);
		this.recordNumber = new SimpleStringProperty(recordNumber);
		this.eventTimeGenerated = new SimpleStringProperty(eventTimeGenerated);
		this.eventTimeWritten = new SimpleStringProperty(eventTimeWritten);
		this.eventData = new SimpleStringProperty(eventData);
	}

	public LogDetailsTree() {
	}

	public StringProperty getEventCategory() {
		return eventCategory;
	}

	public StringProperty getEventID() {
		return eventID;
	}

	public StringProperty getEventSource() {
		return eventSource;
	}

	public StringProperty getEventType() {
		return eventType;
	}

	public StringProperty getRecordNumber() {
		return recordNumber;
	}

	public StringProperty getEventTimeGenerated() {
		return eventTimeGenerated;
	}

	public StringProperty getEventTimeWritten() {
		return eventTimeWritten;
	}

	public StringProperty getEventData() {
		return eventData;
	}

	@Override
	public String toString() {
		return "LogDetailsTree [eventCategory=" + eventCategory + ", eventID=" + eventID + ", eventSource="
				+ eventSource + ", eventType=" + eventType + ", recordNumber=" + recordNumber + ", eventTimeGenerated="
				+ eventTimeGenerated + ", eventTimeWritten=" + eventTimeWritten + ", eventData=" + eventData + "]";
	}
}
