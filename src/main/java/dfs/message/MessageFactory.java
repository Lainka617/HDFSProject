package main.java.dfs.message;

import java.math.BigInteger;

import main.java.dfs.ClientConnection;
import main.java.dfs.message.request.AbortMessage;
import main.java.dfs.message.request.CommitMessage;
import main.java.dfs.message.request.ExitMessage;
import main.java.dfs.message.request.NewTransactionMessage;
import main.java.dfs.message.request.ReadMessage;
import main.java.dfs.message.request.WriteMessage;
import main.java.dfs.message.response.AckMessage;
import main.java.dfs.message.response.AskResendMessage;
import main.java.dfs.message.response.ErrorCode;
import main.java.dfs.message.response.ErrorMessage;

public abstract class MessageFactory {
		
	public static Message makeRequestMessage(String method, String transactionID, String sequenceNumber, String data, ClientConnection connection) {
		
		Message message = null;
		// convert the method to upper case for easier comparison.
		method = method.toUpperCase();
		
		BigInteger parsedTransactionID = null;
		BigInteger parsedSequenceNumber = null;
		
		try {
			parsedTransactionID = new BigInteger(transactionID);
		} catch(NumberFormatException e) {
			return new ErrorMessage(transactionID, ErrorCode.INVALID_TRANSACTION_ID, 
					"Unable to parse transaction ID (" + transactionID + ") into a number.", 
					connection);
		}
		
		try {
			parsedSequenceNumber = new BigInteger(sequenceNumber);
		} catch(NumberFormatException e) {
			return new ErrorMessage(parsedTransactionID.toString(), ErrorCode.INVALID_OPERATION,
					"Unable to parse sequence number (" + sequenceNumber + ") into a number.", 
					connection);
		}

		method = method.replaceAll("\\P{Print}","");

		if(method.equals(ReadMessage.METHOD_ID)) {
			message = new ReadMessage(data, connection);
			
		} else if(method.equals(NewTransactionMessage.METHOD_ID)) {
			
			message = new NewTransactionMessage(parsedTransactionID, parsedSequenceNumber, data, connection);
			
		} else if(method.equals(WriteMessage.METHOD_ID)) {
			
			message = new WriteMessage(parsedTransactionID, parsedSequenceNumber, data, connection);
			
		} else if(method.equals(CommitMessage.METHOD_ID)) {
			
			message = new CommitMessage(parsedTransactionID, parsedSequenceNumber, connection);
		
		} else if(method.equals(AbortMessage.METHOD_ID)) {
			
			message = new AbortMessage(parsedTransactionID, connection);
		
		} else if(method.equals(ExitMessage.METHID_ID)) {
			message = new ExitMessage();
		}

		return message;
		
	}
	
	public static Message makeResponseMessage(String method, String transactionID, String sequenceNumber, ErrorCode errorCode, String reason, ClientConnection connection) {
		Message message = null;
		
		if(method.equals(ErrorMessage.METHOD_ID)) {
			message = new ErrorMessage(transactionID, errorCode, reason, connection);
		} else if(method.equals(AckMessage.METHOD_ID)) {
			message = new AckMessage(transactionID, sequenceNumber, connection);
		} else if(method.equals(AskResendMessage.METHOD_ID)) {
			message = new AskResendMessage(transactionID, sequenceNumber, connection);
		}
		
		return message;
	}
	
}
