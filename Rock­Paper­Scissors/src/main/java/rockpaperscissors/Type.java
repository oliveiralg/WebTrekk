package rockpaperscissors;

public enum Type {

  ROCK {
    @Override
    public boolean beats(Type other) {
      return other == SCISSOR;
    }
  },
  PAPER {
    @Override
    public boolean beats(Type other) {
      return other == ROCK;
    }
  },
  SCISSOR {
    @Override
    public boolean beats(Type other) {
      return other == PAPER;
    }
  };

  public static Type parseType(Integer value) {
    if (value == 1)
      return ROCK;
    else if (value == 2)
      return PAPER;
    else if (value == 3)
      return SCISSOR;
    else
      return null;
  }

  public abstract boolean beats(Type other);
}
