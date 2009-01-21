/*
 * $Id$
 *
 * Authors:
 *      Jeff Buchbinder <jeff@freemedsoftware.org>
 *
 * FreeMED Electronic Medical Record and Practice Management System
 * Copyright (C) 1999-2008 FreeMED Software Foundation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.freemedsoftware.gwt.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.freemedsoftware.gwt.client.JsonUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.thapar.gwt.user.ui.client.widget.SortableTable;

public class CustomSortableTable extends SortableTable {

	public class Column {
		protected String heading;

		protected String hashMapping;

		public Column() {
		}

		public Column(String newHeading, String newHashMapping) {
			setHeading(newHeading);
			setHashMapping(newHashMapping);
		}

		public String getHashMapping() {
			return hashMapping;
		}

		public String getHeading() {
			return heading;
		}

		public void setHashMapping(String newHashMapping) {
			hashMapping = newHashMapping;
		}

		public void setHeading(String newHeading) {
			heading = newHeading;
		}
	}

	public interface TableWidgetColumnSetInterface {
		/**
		 * Set column renderer for a particular column.
		 * 
		 * @param columnName
		 *            Key for column in question.
		 * @param data
		 *            Full hashmap of data.
		 * @return Rendered widget, or null if this renderer is either disabled
		 *         or not used for this particular column.
		 */
		public abstract Widget setColumn(String columnName,
				HashMap<String, String> data);
	}

	protected Column[] columns = new Column[] {};

	protected String indexName = new String("id");

	protected HashMap<String, String> indexMap = new HashMap<String, String>();

	protected Integer maximumRows = new Integer(20);

	protected HashMap<String, String>[] data = null;

	protected boolean multipleSelection = false;

	protected String[] selected;

	protected TableWidgetColumnSetInterface widgetInterface = null;

	public CustomSortableTable() {
		super();
		setStyleName("sortableTable");
		RowFormatter rowFormatter = getRowFormatter();
		rowFormatter.setStyleName(0, "tableHeader");
	}

	public void setTableWidgetColumnSetInterface(TableWidgetColumnSetInterface i) {
		widgetInterface = i;
	}

	/**
	 * Add an additional column definition.
	 * 
	 * @param col
	 */
	public void addColumn(Column col) {
		int currentCols = 0;
		try {
			currentCols = columns.length;
		} catch (Exception e) {

		}
		this.addColumnHeader(col.getHeading(), currentCols);

		List<Column> sA = new ArrayList<Column>();
		for (int iter = 0; iter < currentCols; iter++) {
			sA.add(columns[iter]);
		}
		sA.add(col);
		columns = (Column[]) sA.toArray(new Column[0]);
	}

	/**
	 * Add an additional column definition.
	 * 
	 * @param col
	 */
	public void addColumn(String headerName, String hashMapping) {
		addColumn(new Column(headerName, hashMapping));
	}

	/**
	 * ` Format table with boiler plate.
	 * 
	 * @param columnCount
	 *            Number of columns present.
	 */
	public void formatTable(int rowCount) {
		{
			CellFormatter cellFormatter = getCellFormatter();
			for (int colIndex = 0; colIndex < columns.length; colIndex++) {
				cellFormatter.setStyleName(0, colIndex, "headerStyle");
				cellFormatter.setAlignment(0, colIndex,
						HasHorizontalAlignment.ALIGN_CENTER,
						HasVerticalAlignment.ALIGN_MIDDLE);
			}
		}

		// Format all the data, if it exists
		try {
			if (rowCount > 0) {
				RowFormatter rowFormatter = getRowFormatter();
				CellFormatter cellFormatter = getCellFormatter();
				for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
					// Alternating rows
					if (rowIndex % 2 == 0) {
						rowFormatter.setStyleName(rowIndex, "customRowStyle");
					} else {
						rowFormatter.setStyleName(rowIndex, "tableRow");
					}
					// Set column alignments and fonts
					for (int colIndex = 0; colIndex < columns.length; colIndex++) {
						cellFormatter.setStyleName(rowIndex, colIndex,
								"customFont");
						cellFormatter.setAlignment(rowIndex, colIndex,
								HasHorizontalAlignment.ALIGN_LEFT,
								HasVerticalAlignment.ALIGN_MIDDLE);
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Resolve value of row based on the physical row number on the actual view.
	 * Meant to be used for things like TableListener.
	 * 
	 * @param row
	 * @return
	 */
	public String getValueByRow(int row) {
		return (String) indexMap.get((String) new Integer(row).toString());
	}

	/**
	 * Resolve value of row member based on the actual view.
	 * 
	 * @param row
	 * @param key
	 * @return
	 */
	public String getValueFromIndex(int row, String key) {
		JsonUtil.debug("getValueFromIndex: row = "
				+ new Integer(row - 1).toString() + ", key = " + key);
		JsonUtil.debug("getValueFromIndex: return = " + data[row - 1].get(key));
		return (String) data[row - 1].get(key);
	}

	/**
	 * Determine if index row has been selected. This only affects anything if
	 * using multiple selection mode.
	 * 
	 * @param index
	 * @return
	 */
	public boolean isIndexSelected(String index) {
		try {
			for (int iter = 0; iter < selected.length; iter++) {
				if (selected[iter].compareTo(index) == 0) {
					return true;
				}
			}
		} catch (Exception e) {
			GWT.log("Exception", e);
		}
		return false;
	}

	/**
	 * Remove all active data and styles in the grid, handling errors.
	 */
	public void clearData() {
		int maxRows = 0;
		try {
			maxRows = maximumRows.intValue();
		} catch (Exception ex) {
		}
		if (data != null) {
			int rows = (data.length < maxRows) ? data.length : maxRows;
			RowFormatter rowFormatter = getRowFormatter();
			for (int iter = 0; iter < rows; iter++) {
				rowFormatter.removeStyleName(iter + 1, "tableRow");
				rowFormatter.removeStyleName(iter + 1, "customRowStyle");
				for (int jter = 0; jter < columns.length; jter++) {
					clearCell(iter + 1, jter);
				}
			}
		}
	}

	/**
	 * @param newData
	 */
	public void loadData(HashMap<String, String>[] newData) {
		data = newData;
		if (data != null) {
			int rows = (data.length < maximumRows.intValue()) ? data.length
					: maximumRows.intValue();
			GWT.log("rows = " + new Integer(rows).toString(), null);
			for (int iter = 0; iter < rows; iter++) {
				// Set the value in the index map so clicks can be converted
				String indexValue = (String) data[iter].get(indexName);
				String rowValue = String.valueOf(iter + 1);
				indexMap.put((String) rowValue, (String) indexValue);
				for (int jter = 0; jter < columns.length; jter++) {
					// Populate the column
					if (widgetInterface != null) {
						Widget content = widgetInterface.setColumn(
								columns[jter].getHashMapping(), data[iter]);
						if (content != null) {
							this.setWidget(iter + 1, jter, content);
						} else {
							setText(iter + 1, jter, (String) data[iter]
									.get((String) columns[jter]
											.getHashMapping()));
						}
					} else {
						setText(iter + 1, jter, (String) data[iter]
								.get((String) columns[jter].getHashMapping()));
					}
				}
			}
			formatTable(rows);
		} else {
			JsonUtil.debug("Skipping loadData code due to null data presented");
		}
	}

	protected void selectionAdd(String index) {
		selected[selected.length] = index;
	}

	protected void selectionRemove(String index) {
		try {
			String[] res = new String[] {};
			for (int iter = 0; iter < selected.length; iter++) {
				if (selected[iter].compareTo(index) != 0) {
					res[res.length] = selected[iter];
				}
			}
			selected = res;
		} catch (Exception e) {
			GWT.log("Exception", e);
		}
	}

	/**
	 * Set name of indexing variable, which is used to determine the value of a
	 * row from the data HashMap given.
	 * 
	 * @param newIndexName
	 */
	public void setIndexName(String newIndexName) {
		indexName = newIndexName;
	}

	/**
	 * Set maximum number of rendered rows.
	 * 
	 * @param max
	 */
	public void setMaximumRows(Integer max) {
		maximumRows = max;
	}

	/**
	 * Set multiple selection capability. true indicates that multiple
	 * selections are allowed, false indicates single selection only. By
	 * default, this is set to false.
	 * 
	 * @param newMultipleSelection
	 */
	public void setMultipleSelection(boolean newMultipleSelection) {
		multipleSelection = newMultipleSelection;
	}

	/**
	 * Toggle a multiple selection for a row by its index.
	 * 
	 * @param index
	 */
	public void toggleSelection(String index) {
		// TODO: handle visual aspect of selection
		if (isIndexSelected(index)) {
			selectionRemove(index);
		} else {
			selectionAdd(index);
		}
	}

}
