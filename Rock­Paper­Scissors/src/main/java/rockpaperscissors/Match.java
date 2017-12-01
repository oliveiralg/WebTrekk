package rockpaperscissors;

import java.util.List;

public class Match {

  private String id;
  private String player1;
  private Type player1Play;
  private String player2;
  private Type player2Play;
  private List<Message> messages;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPlayer1() {
    return player1;
  }

  public void setPlayer1(String player1) {
    this.player1 = player1;
  }

  public String getPlayer2() {
    return player2;
  }

  public void setPlayer2(String player2) {
    this.player2 = player2;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public Type getPlayer1Play() {
    return player1Play;
  }

  public void setPlayer1Play(Type player1Play) {
    this.player1Play = player1Play;
  }

  public Type getPlayer2Play() {
    return player2Play;
  }

  public void setPlayer2Play(Type player2Play) {
    this.player2Play = player2Play;
  }
}
