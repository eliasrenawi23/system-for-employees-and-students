package Entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * This class contains all the needed information about the message as represented in the database.
 * 
 * @author Bshara
 * */
public class Message extends SqlObject implements Serializable {

	private static Message emptyInstance = new Message(0, null, null, null, null, false, null, false, false, false);

	public static Message getEmptyInstance() {
		return emptyInstance;
	}

	public long messageID;
	public String subject, from, to, messageContentLT;
	public boolean hasBeenViewed;
	public Timestamp sentAt;
	public boolean isStarred, isRead, isArchived;
	public long requestId, phaseId;

	public Message(long messageID, String subject, String from, String to, String messageContentLT,
			boolean hasBeenViewed, Timestamp sentAt, boolean isStarred, boolean isRead, boolean isArchived) {
		super();
		this.messageID = messageID;
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.messageContentLT = messageContentLT;
		this.hasBeenViewed = hasBeenViewed;
		this.sentAt = sentAt;
		this.isStarred = isStarred;
		this.isRead = isRead;
		this.isArchived = isArchived;
		this.requestId = -1;
		this.phaseId = -1;
	}

	public Message(long messageID, String subject, String from, String to, String messageContentLT,
			boolean hasBeenViewed, Timestamp sentAt, boolean isStarred, boolean isRead, boolean isArchived,
			long requestId, long phaseId) {
		super();
		this.messageID = messageID;
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.messageContentLT = messageContentLT;
		this.hasBeenViewed = hasBeenViewed;
		this.sentAt = sentAt;
		this.isStarred = isStarred;
		this.isRead = isRead;
		this.isArchived = isArchived;
		this.requestId = requestId;
		this.phaseId = phaseId;
	}
	
	

	public long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(long phaseId) {
		this.phaseId = phaseId;
	}

	public static void setEmptyInstance(Message emptyInstance) {
		Message.emptyInstance = emptyInstance;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public boolean isStarred() {
		return isStarred;
	}

	public void setStarred(boolean isStarred) {
		this.isStarred = isStarred;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isArchived() {
		return isArchived;
	}

	public void setArchived(boolean isArchived) {
		this.isArchived = isArchived;
	}

	public void setHasBeenViewed(boolean hasBeenViewed) {
		this.hasBeenViewed = hasBeenViewed;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Timestamp getSentAt() {
		return sentAt;
	}

	public void setSentAt(Timestamp sentAt) {
		this.sentAt = sentAt;
	}

	public long getMessageID() {
		return messageID;
	}

	public void setMessageID(long messageID) {
		this.messageID = messageID;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessageContentLT() {
		return messageContentLT;
	}

	public void setMessageContentLT(String messageContentLT) {
		this.messageContentLT = messageContentLT;
	}

	public boolean isHasBeenViewed() {
		return hasBeenViewed;
	}

	@Override
	public int getPrimaryKeyIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getForeignKeyIndex() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public String getReferenceTableName() {
		// TODO Auto-generated method stub
		return "SystemUser";
	}

	@Override
	public boolean hasForeignKey() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getReferenceTableForeignKeyName() {
		// TODO Auto-generated method stub
		return "userName";
	}

	@Override
	public int fieldsLastIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "Message [messageID=" + messageID + ", subject=" + subject + ", from=" + from + ", to=" + to
				+ ", messageContentLT=" + messageContentLT + ", hasBeenViewed=" + hasBeenViewed + ", sentAt=" + sentAt
				+ ", isStarred=" + isStarred + ", isRead=" + isRead + ", isArchived=" + isArchived + ", requestId="
				+ requestId + ", phaseId=" + phaseId + "]";
	}



}
