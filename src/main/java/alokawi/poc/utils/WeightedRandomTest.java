/**
 * 
 */
package alokawi.poc.utils;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author alokkumar
 *
 */
public class WeightedRandomTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WeightedRandomTest randomTest = new WeightedRandomTest();
		randomTest.execute();
	}

	private void execute() {
		Map<Integer, Double> hashMap = new HashMap<Integer, Double>();
		hashMap.put(1, 0.7);
		hashMap.put(2, 0.6);
		hashMap.put(3, 0.5);
		hashMap.put(4, 0.45);
		hashMap.put(5, 0.4);
		hashMap.put(6, 0.35);
		hashMap.put(7, 0.3);
		hashMap.put(8, 0.2);
		hashMap.put(9, 0.1);

		Random random = new Random();

		Map<Integer, Integer> resultMap = new HashMap<>();
		for (int i = 0; i < 400; i++) {
			Integer randomInteger = getWeightedRandom(hashMap.entrySet().stream(), random);
			System.out.println(randomInteger);
			resultMap.put(randomInteger, resultMap.getOrDefault(randomInteger, 1) + 1);
		}

		System.out.println(resultMap);
		resultMap.clear();

		WeightedRandom<Integer> weightedRandom = new WeightedRandom<>(hashMap, 3000);
		while (weightedRandom.hasNext()) {
			Integer integer = (Integer) weightedRandom.next();
			resultMap.put(integer, resultMap.getOrDefault(integer, 1) + 1);
		}

		System.out.println(resultMap);

	}

	public <E> E getWeightedRandom(Stream<Entry<E, Double>> weights, Random random) {
		return weights.map(e -> new SimpleEntry<E, Double>(e.getKey(), -Math.log(random.nextDouble()) / e.getValue()))
				.min((e0, e1) -> e0.getValue().compareTo(e1.getValue())).orElseThrow(IllegalArgumentException::new)
				.getKey();
	}

}
