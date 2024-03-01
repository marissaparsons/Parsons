import java.util.ArrayList;
public class Group {

  private ArrayList<Frog> frogList;
  private int size;

  public Group() {
        frogList = new ArrayList<Frog>();
        size = 0;
  }


  public void addFrog(Frog f) {
    int index = indexBinarySearch(frogList, f);
    frogList.add(index, f);
    size++;
  }

  public static int indexBinarySearch(ArrayList<Frog> list, Frog frog) {
    int low = 0;
    int high = list.size() - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        Frog currentFrog = list.get(mid);

        int comparison = currentFrog.compareTo(frog);
        if (comparison == 0) {
            return mid;
        } else if (comparison < 0) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }

    return low;
  }

  public int size() {
    return size;
  }


  public Frog get(int i) {
    return frogList.get(i);
  }

  public Group[] halfGroups() {
    Group g1 = new Group();
    Group g2 = new Group();

    for (int i=0; i<(size/2); i++) {
      g1.addFrog(frogList.get(i));
      g2.addFrog(frogList.get(i+(size/2)));
    }
    if ((size % 2) == 1) {
      g2.addFrog(frogList.get(size-1));
    }

    Group[] groups = {g1, g2};
    return groups; 
  }

  @Override
  public String toString() {
    return frogList.toString();
  }

  public static boolean FrogEquals(Group g1, Group g2) {
    if (g1.size() == g2.size()) {
      int i = 0;
        for (i = 0; i < g1.size(); i++) {
          if (g1.frogList.get(i).compareTo(g2.frogList.get(i)) != 0) {
            break;
          }
        }
        if (i == g1.size) {
          return true;
        }

        Group[] g1_group = g1.halfGroups();
        Group[] g2_group = g2.halfGroups();

        //System.out.println("g1 top: " + g1_group[0].toString());
        //System.out.println("g2 bot: " + g2_group[1].toString());

        return FrogEquals(g1_group[0], g2_group[1]) || FrogEquals(g2_group[0], g1_group[1]);
      }
       
    return false;
  }


}
