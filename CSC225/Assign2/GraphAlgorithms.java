import java.awt.Color;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.String;

public class GraphAlgorithms{

  /* 
   * To draw a list of integers int_list (of type List<Integer)
   * to the canvas, call drawSequence(int_list, writer).
   *
   * The index of each integer in the list will be
   * plotted along the x-axis; the integer value itself
   * is plotted on the y-axis.
   *                                                      */

  public static List<Integer> MergeSort(List<Integer> s, PixelWriter writer) {
  	if (s.size() < 2) {
  		return s;
  	}
  	List<Integer> s1 = new ArrayList<>();
  	List<Integer> s2 = new ArrayList<>();
  	List<Integer> merged = new ArrayList<>();

  	divide(s1, s2, s);
  	s1 = MergeSort(s1, writer);
  	s2 = MergeSort(s2, writer);
  	merged = merge(s1, s2);

  	drawSequence(merged, writer);
  	return merged;
  }

  public static void divide(List<Integer> s1, List<Integer> s2, List<Integer> s) {
  	int i = 0;
  	for (i = 0; i < s.size()/2; i++){
  		s1.add(s.get(i));
  	}
  	for (i = s.size()/2; i < s.size(); i++) {
  		s2.add(s.get(i));
  	}
  }

  public static List<Integer> merge(List<Integer> s1, List<Integer> s2) {
  	List<Integer> merged = new ArrayList<>();
  	int i = 0;
  	int j = 0;
  	while (i < s1.size() && j < s2.size()) {
  		if (s1.get(i) <= s2.get(j)) {
  			merged.add(s1.get(i));
  			i++;
  		}
  		else {
  			merged.add(s2.get(j));
  			j++;
  		}
  	}
  	while (i < s1.size()) {
  			merged.add(s1.get(i));
  			i++;
  	}
  	while (j < s2.size()) {
  			merged.add(s2.get(j));
  			j++;
  	}
  	return merged;
  }


  public static List<Integer> QuickSort(List<Integer> s, PixelWriter writer) {
  	int size = s.size();
 	if (size < 2) {
 		return s;
 	}

 	List<Integer> less = new ArrayList<>();
 	List<Integer> equal = new ArrayList<>();
  	List<Integer> greater = new ArrayList<>();

	int pivot = s.get(size/2);

	split(less, equal, greater, s, pivot);
	less = QuickSort(less, writer);
	greater = QuickSort(greater, writer);

	List<Integer> merged = concatenate(less, equal, greater);

	drawSequence(merged, writer);
	return merged;
	}


  public static void split(List<Integer> less, List<Integer> equal, List<Integer> greater, List<Integer> s, int pivot) {
  	for (int index = 0; index < s.size(); index++) {
  		if (s.get(index) < pivot) {
  			less.add(s.get(index));
  		} 
  		else if (s.get(index) == pivot) {
  			equal.add(s.get(index));
  		}
  		else {
  			greater.add(s.get(index));
  		}
  	}
  }

  public static List<Integer> concatenate(List<Integer> less, List<Integer> equal, List<Integer> greater) {
  	List<Integer> merged = new ArrayList<>(less.size() + equal.size() + greater.size());
    merged.addAll(less);
    merged.addAll(equal);
    merged.addAll(greater);
    return merged;
  }


  public static List<Integer> InsertionSort(List<Integer> s, PixelWriter writer) {
  	for (int index = 1; index < s.size(); index++) {
  		int val = s.get(index);
  		int index2 = index - 1;
  		while (index2 >= 0 && s.get(index2) > val) {
  			s.set(index2 + 1, s.get(index2));
  			index2--;
  		}
  		s.set(index2 + 1, val);
  		drawSequence(s, writer);
  	}
  	return s;
  }

  public static List<Integer> RadixSort(List<Integer> s, PixelWriter writer) {
  	int max = getMax(s);
  	int width = Integer.toString(max).length();

  	List<String> s_str = new ArrayList<>();
  	convertToString(s, s_str, width);

  	List<List<String>> buckets = new ArrayList<>();
  	createBuckets(buckets);

  	for (int i = width-1 ; i >= 0; i--) {
  		bucketSort(s_str, i, buckets);
  		//for visualization of sort the next 2 lines need to be in the for loop
  		s = convertToInteger(s_str, s);
  		drawSequence(s, writer);
  	}

  	//for shorter runtime the next 2 lines that are currently in the for loop need to be outside the for loop
  	//s = convertToInteger(s_str, s);
  	//drawSequence(s, writer);
  	return s;
  }


  public static void createBuckets(List<List<String>> buckets_list) {
  	for (int i = 0; i < 10; i++) {
  		List<String> bucket = new ArrayList<>();
  		buckets_list.add(bucket);
  	}
  }


  public static void convertToString(List<Integer> s, List<String> s_str, int width) {
  	for (int i = 0; i < s.size(); i++) {
  		String format = String.format("%0"+width+"d", s.get(i));
  		s_str.add(format);
  	}
  }


  public static List<Integer> convertToInteger(List<String> s_str, List<Integer> s) {
  	s.clear();
  	for (int i = 0; i < s_str.size(); i++) {
  		s.add(Integer.valueOf(s_str.get(i)));
  	}
  	return s;
  }


  public static int getMax(List<Integer> s) {
  	if (s.size() < 1) {
  		return 0;
  	}
  	int max = s.get(0);
  	for (int i = 0; i < s.size(); i++) {
  		if (s.get(i) > max) {
  			max = s.get(i);
  		}
  	}
  	return max;
  }


  public static void bucketSort(List<String> s_str, int position, List<List<String>> buckets) {
  	for (int i = 0; i < s_str.size(); i++) {
  		String num = s_str.get(i);
  		char key = num.charAt(position);
  		int digit = key - '0';
  		buckets.get(digit).add(num);
  	}
  	s_str.clear();
  	for (int j = 0; j < buckets.size(); j++) {
  		List<String> bckt = buckets.get(j);
  		for (int k = 0; k < bckt.size(); k++) {
  			s_str.add(bckt.get(k));
  		}
  		bckt.clear();
  	}
  }




  /* DO NOT CHANGE THIS METHOD */
  private static void drawSequence(List<Integer> sequence, PixelWriter writer) {
    for (Integer curr : sequence) {
      for (int j=0; j<sequence.size(); j++) {
        Color c = writer.getColor(j, curr);
        if (c.equals(Color.BLACK))
          writer.setPixel(j, curr, Color.WHITE);
      }
      int x = sequence.indexOf(curr);
      if (!writer.getColor(x, curr).equals(Color.BLACK)) {
        writer.setPixel(sequence.indexOf(curr), curr, Color.BLACK);
      }
    }
  } 


  /* THE FOLLOWING METHODS WILL NOT BE MARKED;
   * YOU MAY IMPLEMENT THEM OPTIONALLY
   */

	/* FloodFillDFS(v, writer, fillColour)
	   Traverse the component the vertex v using DFS and set the colour 
	   of the pixels corresponding to all vertices encountered during the 
	   traversal to fillColour.
	   
	   To change the colour of a pixel at position (x,y) in the image to a 
	   colour c, use
			writer.setPixel(x,y,c);
	*/
	public static void FloodFillDFS(PixelVertex v, PixelWriter writer, Color fillColour){
	}
	
	/* FloodFillBFS(v, writer, fillColour)
	   Traverse the component the vertex v using BFS and set the colour 
	   of the pixels corresponding to all vertices encountered during the 
	   traversal to fillColour.
	   
	   To change the colour of a pixel at position (x,y) in the image to a 
	   colour c, use
			writer.setPixel(x,y,c);
	*/
	public static void FloodFillBFS(PixelVertex v, PixelWriter writer, Color fillColour){
	}
	
}
