/**
 * 
 */
package alokawi.poc.utils;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author alokkumar
 *
 */
public class WeightedRandom<E extends Number> implements Iterator<E> {

	private Map<E, Double> weightedMap;
	private Random random;
	private int size;

	public WeightedRandom(Map<E, Double> weightedMap, int size) {
		this.weightedMap = weightedMap;
		this.size = size;
		this.random = new Random();
	}

	@SuppressWarnings("hiding")
	private <E> E getWeightedRandom(Stream<Entry<E, Double>> weights, Random random) {
		return weights.map(e -> new SimpleEntry<E, Double>(e.getKey(), -Math.log(random.nextDouble()) / e.getValue()))
				.min((e0, e1) -> e0.getValue().compareTo(e1.getValue())).orElseThrow(IllegalArgumentException::new)
				.getKey();
	}

	@Override
	public boolean hasNext() {
		return size-- > 0;
	}

	@Override
	public E next() {
		return getWeightedRandom(this.weightedMap.entrySet().stream(), this.random);
	}

}
