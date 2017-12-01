package rockpaperscissors;

/*
 * Message received from client.
 *
 * @Author Leonardo Oliveira
 */
public class Message {
  private String from;
  private Integer option;

  public Message() {}

  public Message(String from, Integer option) {
    super();
    this.from = from;
    this.option = option;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public Integer getOption() {
    return option;
  }

  public void setOption(Integer option) {
    this.option = option;
  }

}
