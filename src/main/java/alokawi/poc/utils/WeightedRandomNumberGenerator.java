/**
 * 
 */
package alokawi.poc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author alokkumar
 *
 */
public class WeightedRandomNumberGenerator {

	private TreeMap<Double, Integer> weightedMap;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Random random = new Random();
		List<Double> weightAsDoubles = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			weightAsDoubles.add(random.nextDouble());
		}
		Collections.sort(weightAsDoubles);
		Collections.reverse(weightAsDoubles);
		System.out.println(weightAsDoubles);

		WeightedRandomNumberGenerator randomNumberGenerator = new WeightedRandomNumberGenerator();
		List<Integer> asList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		System.out.println(asList);
		randomNumberGenerator.generate(asList, weightAsDoubles);

		for (int i = 0; i < 400; i++) {
			Integer next = randomNumberGenerator.getNext();
			System.out.print(next * 5);
			System.out.print(", ");
		}

	}

	private Integer getNext() {

		Random generator = new Random();
		// Generate a random value between 0 and 1
		double value = generator.nextDouble();
		// Get the object that matches with the generated number
		return weightedMap.ceilingEntry(value).getValue();

	}

	private void generate(List<Integer> objects, List<Double> weight) {

		weightedMap = new TreeMap<>();
		double total = 0.0d;
		for (int i = 0; i < objects.size(); i++) {
			weightedMap.put(total += weight.get(i), objects.get(i));
		}
		System.out.printf("The generated is map %s%n", weightedMap);

	}

}
