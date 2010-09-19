package org.eclipse.xtext.graph.trafo;

/**
 * Data structure to track current positions in a grid.
 * 
 * @author koehnlein
 */
public class GridPointer {
	private int track;
	private int row;
	private int column;
	private int maxRow;
	private int maxColumn;

	public GridPointer() {
		this(0, 0, 0, 0, 0);
	}

	public GridPointer(int track, int row, int column, int maxRow, int maxColumn) {
		this.track = track;
		this.row = row;
		this.column = column;
		this.maxRow = maxRow;
		this.maxColumn = maxColumn;
	}

	public GridPointer clone() {
		return new GridPointer(track, row, column, maxRow, maxColumn);
	}

	public int getTrack() {
		return track;
	}

	public void incTrack() {
		++track;
	}

	public int getRow() {
		return row;
	}

	public void incRow() {
		++row;
		maxRow = Math.max(row, maxRow);
	}

	public void setRow(int row) {
		this.row = row;
		maxRow = Math.max(row, maxRow);
	}

	public int getColumn() {
		return column;
	}

	public void incColumn() {
		++column;
		maxColumn = Math.max(column, maxColumn);
	}

	public void setColumn(int column) {
		this.column = column;
		maxColumn = Math.max(column, maxColumn);
	}

	public void resetColumn() {
		this.column = 0;
		this.maxColumn = 0;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public int getMaxColumn() {
		return maxColumn;
	}

	public void incMaxColumn() {
		++maxColumn;
	}

	public void incMaxRow() {
		++maxRow;
	}

	public void aggregate(GridPointer other) {
		maxRow = Math.max(other.maxRow, maxRow);
		maxColumn = Math.max(other.maxColumn, maxColumn);
		setColumn(other.maxColumn);
	}

	public void resetMax() {
		maxRow = row;
		maxColumn = column;
	}
}