package org.eclipse.xtext.graph.util;

import java.util.HashMap;

import com.google.inject.internal.Maps;

public class MaxSizeAggregator {

	private HashMap<Integer, Integer> sizes = Maps.newHashMap();

	private int maxIndex = -1;

	public void aggregate(int index, int newSize) {
		int max = Math.max(newSize, get(index));
		sizes.put(index, max);
		maxIndex = Math.max(index, maxIndex);
	}

	public int[] getPositions() {
		int[] positions = new int[maxIndex + 1];
		int currentPosition = 0;
		for (int index = 0; index <= maxIndex; ++index) {
			positions[index] = currentPosition;
			currentPosition += get(index);
		}
		return positions;
	}

	public int get(int index) {
		return sizes.containsKey(index) ? sizes.get(index) : 0;
	}
}
