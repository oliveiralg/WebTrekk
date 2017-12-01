package rockpaperscissors;

import java.util.Date;

/*
 * Output message sent to client.
 *
 * @Author Leonardo Oliveira
 */
public class OutputMessage {
  private String destination;
  private String matchId;
  private String status;
  private String message;
  private String opponentName;
  private Date time = new Date();

  public OutputMessage() {}

  public OutputMessage(String destination, String matchId, String status, String message,
      String opponentName, Date time) {
    super();
    this.destination = destination;
    this.matchId = matchId;
    this.status = status;
    this.message = message;
    this.opponentName = opponentName;
    this.time = time;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public String getMatchId() {
    return matchId;
  }

  public void setMatchId(String matchId) {
    this.matchId = matchId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getOpponentName() {
    return opponentName;
  }

  public void setOpponentName(String opponentName) {
    this.opponentName = opponentName;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }



}
