public class Frog implements Comparable<Frog> {

  private String name;
  
  public Frog(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public int compareTo(Frog o) {
    return this.name.compareTo(o.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Frog)) {
      return false;
    }

    Frog other = (Frog) o;
    return this.name.equals(other.getName());
  }

}
