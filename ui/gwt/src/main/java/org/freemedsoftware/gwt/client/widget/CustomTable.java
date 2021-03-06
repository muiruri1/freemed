/*
 * $Id$
 *
 * Authors:
 *      Jeff Buchbinder <jeff@freemedsoftware.org>
 *
 * FreeMED Electronic Medical Record and Practice Management System
 * Copyright (C) 1999-2012 FreeMED Software Foundation
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
import java.util.Iterator;
import java.util.List;

import org.freemedsoftware.gwt.client.JsonUtil;
import org.freemedsoftware.gwt.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

public class CustomTable extends Composite implements ClickHandler {

	@Override
	public void setSize(String width, String height) {
		// TODO Auto-generated method stub
		super.setSize(width, height);
		flexTable.setSize(width, height);
	}

	@Override
	public void setHeight(String height) {
		// TODO Auto-generated method stub
		super.setHeight(height);
		flexTable.setHeight(height);
	}

	@Override
	public void setWidth(String width) {
		// TODO Auto-generated method stub
		super.setWidth(width);
		flexTable.setWidth("100%");
	}

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

	/**
	 * Interface used by CustomTable to create custom field content.
	 * 
	 * @author jeff@freemedsoftware.org
	 */
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

	/**
	 * Interface for row clicking callback.
	 * 
	 * @author jeff@freemedsoftware.org
	 */
	public interface TableRowClickHandler {
		/**
		 * Callback to handle a click on a particular data row. CustomTable
		 * passes the HashMap of values to perform the processing.
		 * 
		 * @param data
		 */
		public abstract void handleRowClick(HashMap<String, String> data,
				int col);
	}

	protected FlexTable flexTable = null;

	protected List<Column> columns = new ArrayList<Column>();

	protected String indexName = new String("id");

	protected HashMap<String, String> indexMap = new HashMap<String, String>();

	protected Integer maximumRows = new Integer(25);

	protected Integer visibleRows = maximumRows;

	protected Integer defaultRows = maximumRows;

	protected HashMap<String, String>[] data = null;

	protected boolean multipleSelection = false;

	protected boolean allowSelection = false;

	protected List<String> selected = new ArrayList<String>();

	protected TableWidgetColumnSetInterface widgetInterface = null;

	protected TableRowClickHandler tableRowClickHandler = null;

	protected HorizontalPanel buttonContainer = null;

	protected Label bLabel = new Label();
	protected int actionRow = -1;
	protected int curMinRow = 0;
	protected String rowStyle = AppConstants.STYLE_TABLE_ROW;
	protected String TABLE_STYLE_NAME = AppConstants.STYLE_TABLE;
	protected String ALTERNATE_ROW_STYLE = AppConstants.STYLE_TABLE_ROW_ALTERNATE;
	protected String TABLE_HEADER_STYLE = AppConstants.STYLE_TABLE_HEADER;
	protected boolean sortDesc = true;

	protected Image loadingImage = null;
	protected Label noItemFound = null;

	protected String noItemsText = "No items found.";

	CustomButton nextBtn = new CustomButton("Next", AppConstants.ICON_NEXT);
	CustomButton previousBtn = new CustomButton("Previous",
			AppConstants.ICON_PREV);

	protected HTML perPageDefault;

	protected HTML perPage50;

	protected HTML perPage100;

	public CustomTable() {
		VerticalPanel vPanel = new VerticalPanel();

		flexTable = new FlexTable();
		flexTable.setStyleName(TABLE_STYLE_NAME);
		flexTable.addClickHandler(this);
		vPanel.add(flexTable);

		loadingImage = new Image(GWT.getHostPageBaseURL()
				+ "resources/images/custom_table_loading.gif");
		loadingImage.setVisible(false);
		vPanel.add(loadingImage);
		vPanel.setCellHorizontalAlignment(loadingImage,
				HasHorizontalAlignment.ALIGN_CENTER);

		noItemFound = new Label(noItemsText);
		noItemFound.setVisible(false);
		noItemFound.setStyleName(AppConstants.STYLE_LABEL_NORMAL_ITALIC);
		vPanel.add(noItemFound);
		vPanel.setCellHorizontalAlignment(noItemFound,
				HasHorizontalAlignment.ALIGN_CENTER);

		nextBtn.addClickHandler(this);
		previousBtn.addClickHandler(this);

		Label perPageLb = new Label("Per Page:");
		perPageDefault = new HTML("25");
		perPage50 = new HTML("<a href=\"javascript:undefined;\">50</a>");
		perPage100 = new HTML("<a href=\"javascript:undefined;\">100</a>");
		perPageDefault.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				perPageDefault.setHTML("" + defaultRows);
				perPage50.setHTML("<a href=\"javascript:undefined;\">50</a>");
				perPage100.setHTML("<a href=\"javascript:undefined;\">100</a>");
				
				HashMap<String, String>[] tempDate = data;
				
				clearData();
				maximumRows = defaultRows;
				curMinRow = 0;
				loadData(tempDate, 0);
			}

		});
		perPage50.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				perPage50.setHTML("50");
				perPageDefault.setHTML("<a href=\"javascript:undefined;\">"
						+ defaultRows + "</a>");
				perPage100.setHTML("<a href=\"javascript:undefined;\">100</a>");
				
				HashMap<String, String>[] tempDate = data;
				
				clearData();
				maximumRows = 50;
				curMinRow = 0;
				loadData(tempDate, 0);
			}

		});
		perPage100.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				perPage100.setHTML("100");
				perPageDefault.setHTML("<a href=\"javascript:undefined;\">"
						+ defaultRows + "</a>");
				perPage50.setHTML("<a href=\"javascript:undefined;\">50</a>");
				
				HashMap<String, String>[] tempDate = data;
				clearData();
				maximumRows = 100;
				curMinRow = 0;
				loadData(tempDate, 0);
			}

		});
		// Build button container
		buttonContainer = new HorizontalPanel();
		buttonContainer.setSpacing(2);
		buttonContainer.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonContainer
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonContainer.add(new HTML(""));
		buttonContainer.add(previousBtn);
		buttonContainer.add(bLabel);
		// buttonContainer.setCellVerticalAlignment(bLabel,
		// HasVerticalAlignment.ALIGN_MIDDLE);
		buttonContainer.add(nextBtn);
		buttonContainer.add(perPageLb);
		buttonContainer.add(perPageDefault);
		buttonContainer.add(perPage50);
		buttonContainer.add(perPage100);
		// Button container by default is not visible
		buttonContainer.setVisible(false);
		vPanel.add(buttonContainer);

		// Assign handlers to previous and next

		// Set header row information properly
		RowFormatter rowFormatter = flexTable.getRowFormatter();
		rowFormatter.setStyleName(0, TABLE_HEADER_STYLE);

		// Last thing to do, set the widget.
		initWidget(vPanel);
	}

	public void showloading(boolean show) {
		if (show) {
			noItemFound.setVisible(false);
			clearData();// clear data before showing loading image
		} else {
			if (data==null || data.length == 0) {
				clearData();
				noItemFound.setVisible(true);// If Not Record found then show
												// text
			} else
				noItemFound.setVisible(false);// If Record found then hide text
		}
		loadingImage.setVisible(show);
	}

	public void setTableWidgetColumnSetInterface(TableWidgetColumnSetInterface i) {
		widgetInterface = i;
	}

	public void setTableRowClickHandler(TableRowClickHandler t) {
		tableRowClickHandler = t;
	}

	public void setRowStyle(String s) {
		rowStyle = s;
	}

	public void setAlternateRowStyle(String s) {
		ALTERNATE_ROW_STYLE = s;
	}

	public void setTableStyle(String s) {
		flexTable.removeStyleName(TABLE_STYLE_NAME);
		flexTable.setStyleName(TABLE_STYLE_NAME);
	}

	/**
	 * Add an additional column definition.
	 * 
	 * @param col
	 */
	public void addColumn(Column col) {
		// Get current position in stack
		int currentCols = 0;
		try {
			currentCols = columns.size();
		} catch (Exception e) {
		}

		// Set text for header
		flexTable.setText(0, currentCols, col.getHeading());

		// Push new column into array containing columns
		columns.add(col);
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
			CellFormatter cellFormatter = flexTable.getCellFormatter();
			for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
				cellFormatter.setStyleName(0, colIndex, "headerStyle");
				cellFormatter.setAlignment(0, colIndex,
						HasHorizontalAlignment.ALIGN_CENTER,
						HasVerticalAlignment.ALIGN_MIDDLE);
			}
		}

		// Format all the data, if it exists
		try {
			if (rowCount > 0) {
				RowFormatter rowFormatter = flexTable.getRowFormatter();
				CellFormatter cellFormatter = flexTable.getCellFormatter();
				for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
					if (isRowSelected(rowIndex)) {
						rowFormatter.setStyleName(rowIndex, "rowSelected");
					} else {
						// Alternating rows
						if (rowIndex % 2 == 0) {
							rowFormatter.setStyleName(rowIndex,
									ALTERNATE_ROW_STYLE);
						} else {
							rowFormatter.setStyleName(rowIndex, rowStyle);
						}
					}
					// Set column alignments and fonts
					for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
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

	public boolean isRowSelected(int row) {
		try {
			String rowVal = getValueByRow(row);
			Iterator<String> iter = selected.iterator();
			while (iter.hasNext()) {
				if (iter.next().compareTo(rowVal) == 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	public Widget getWidget(int col) {
		return flexTable.getWidget(actionRow, col);
	}

	public String getCellText(int r, int c) {
		return flexTable.getText(r, c);
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
	 * Get an entire data hashmap based on a row index.
	 * 
	 * @param row
	 * @return
	 */
	public HashMap<String, String> getDataByRow(int row) {
		String index = indexMap.get(new Integer(row).toString());
		for (int iter = 0; iter < data.length; iter++) {
			if (data[iter].get(indexName).compareTo(index) == 0) {
				return data[iter];
			}
		}
		return null;
	}

	/**
	 * Get an entire data hashmap based on a indexName i-e id.
	 * 
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getDataById(int id) {
		for (int iter = 0; iter < data.length; iter++) {
			if (data[iter].get(indexName).compareTo(""+id) == 0) {
				return data[iter];
			}
		}
		return null;
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
				+ new Integer(curMinRow + (row - 1)).toString() + ", key = "
				+ key);
		JsonUtil.debug("getValueFromIndex: return = "
				+ data[curMinRow + (row - 1)].get(key));
		return (String) data[curMinRow + (row - 1)].get(key);
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
			Iterator<String> iter = selected.iterator();
			while (iter.hasNext()) {
				if (iter.next().compareTo(index) == 0) {
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
			maxRows = visibleRows;/*maximumRows.intValue();*/
		} catch (Exception ex) {
		}
		if (data != null) {
			int rows = (data.length < maxRows) ? data.length : maxRows;
			RowFormatter rowFormatter = flexTable.getRowFormatter();
			for (int iter = 0; iter < rows; iter++) {
				rowFormatter.removeStyleName(iter + 1, rowStyle);
				rowFormatter.removeStyleName(iter + 1, ALTERNATE_ROW_STYLE);
				for (int jter = 0; jter < columns.size(); jter++) {
					flexTable.clearCell(iter + 1, jter);
				}
			}
			while (flexTable.getRowCount() > 1) {
				flexTable.removeRow(1);
			}
			// Remove all data
			data = null;
			indexMap.clear();
		}
	}

	public void loadData(HashMap<String, String>[] newData) {
		// By default, regular data load with no offset
		clearData();
		if (newData != null) {
			loadData(newData, 0);
		}
	}

	/**
	 * @param newData
	 */
	public void loadData(HashMap<String, String>[] newData, int offset) {
		if (data != newData) {
			JsonUtil
					.debug("loadData: forcing load of new data instead of repositioning");
			data = newData;
		}
		if (data != null) {
			showloading(false);
			int rows = ((data.length - offset) < maximumRows) ? (data.length - offset)
					: maximumRows;
			visibleRows = rows;
//			visibleRows = rows;
			// JsonUtil.debug("rows = " + rows + ", maximumRows = " +
			// maximumRows
			// + ", offset = " + offset);

			// Decide visibility of buttons
			if (data.length > defaultRows) {
				buttonContainer.setVisible(true);
			} else {
				buttonContainer.setVisible(false);
			}

			// Before we go any further, clear indexMap, otherwise bad click
			// values...
			JsonUtil.debug("clearing indexmap for custom table");
			indexMap.clear();
			
			for (int iter = offset; iter < (rows + offset); iter++) {
				
					
					int actualRow = iter - offset;
	
					// JsonUtil.debug("iter = " + iter + ", actualRow = " +
					// actualRow);
					// Set the value in the index map so clicks can be converted
					String indexValue = data[iter].get(indexName);
					String rowValue = String.valueOf(actualRow + 1);
					indexMap.put(rowValue, indexValue);
					actionRow = actualRow + 1;
					for (int jter = 0; jter < columns.size(); jter++) {
						// Populate the column
						if (widgetInterface != null) {
							Widget content = widgetInterface.setColumn(columns.get(
									jter).getHashMapping(), data[iter]);
							if (content != null) {
								flexTable.setWidget(actualRow + 1, jter, content);
							} else {
								flexTable.setText(actualRow + 1, jter, data[iter]
										.get(columns.get(jter).getHashMapping()));
							}
						} else {
							flexTable.setText(actualRow + 1, jter, data[iter]
									.get(columns.get(jter).getHashMapping()));
						}
					}
			}
			
			
			// Set extra data rows to nothing
//			if (visibleRows < maximumRows) {
//				for (int iter = visibleRows; iter <= maximumRows; iter++) {
//					try {
//						indexMap.remove(iter + 1);
////						flexTable.removeRow(iter + 1);
//					} catch (Exception ex) {
//					}
//					for (int jter = 0; jter < columns.size(); jter++) {
//						flexTable.setText(iter + 1, jter, "");
//					}
//				}
//			}

			JsonUtil.debug("formattable");
			formatTable(rows);
			redrawButtons();
		} else {
			JsonUtil.debug("Skipping loadData code due to null data presented");
		}
	}

	public void selectionAdd(String index) {
		selected.add(index);

		// Reformat table if there is data present
/*		if (data != null && visibleRows > 0) {
			formatTable(visibleRows);
		}*/
	}

	public void selectionRemove(String index) {
		try {
			selected.remove(index);
		} catch (Exception e) {
			JsonUtil.debug("selectionRemove exception: " + e);
		}

		// Reformat table if there is data present
/*		if (data != null && visibleRows > 0) {
			formatTable(visibleRows);
		}*/
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
		defaultRows = max;
		perPageDefault.setHTML("" + defaultRows);
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
	 * Set whether or not this table is allowed to keep selection data. If this
	 * is set to true, selected rows will be highlighted, with further behavior
	 * being determined by multipleSelection. By default this is set to false.
	 * 
	 * @param newAllowSelection
	 */
	public void setAllowSelection(boolean newAllowSelection) {
		allowSelection = newAllowSelection;
	}

	/**
	 * Get underlying FlexTable object.
	 * 
	 * @return
	 */
	public FlexTable getFlexTable() {
		return flexTable;
	}

	/**
	 * Toggle a multiple selection for a row by its index.
	 * 
	 * @param index
	 */
	public void toggleSelection(String index) {
		if (isIndexSelected(index)) {
			selectionRemove(index);
		} else {
			selectionAdd(index);
		}
		this.formatTable(data.length < maximumRows ? data.length : maximumRows);
	}

	private void previousPage() {
		JsonUtil.debug("previousPage");
		if((curMinRow - maximumRows) >= 0) {
			curMinRow -= maximumRows;
		}
		else {
			curMinRow = 0;
		}
		if(data == null) {
			System.out.println("previous nulllllllll");
		}
		HashMap<String, String>[] tempDate = data;
		clearData();
		loadData(tempDate, curMinRow);
		redrawButtons();
	}

	private void nextPage() {
		JsonUtil.debug("nextPage");
		curMinRow += maximumRows;
		if(data == null) {
			System.out.println("Next nullllllllll");
		}
		HashMap<String, String>[] tempDate = data;
		clearData();
		loadData(tempDate, curMinRow);
		redrawButtons();
	}

	private void redrawButtons() {
		JsonUtil.debug("redrawButtons");
		if (curMinRow <= 0) {
			previousBtn.setEnabled(false);
		} else {
			previousBtn.setEnabled(true);
		}

		if (curMinRow + maximumRows < data.length) {
			nextBtn.setEnabled(true);
		} else {
			nextBtn.setEnabled(false);
		}

		// Rewrite label
		String min = new Integer(curMinRow + 1).toString();
		String max = new Integer(
				(curMinRow + maximumRows < data.length) ? curMinRow
						+ maximumRows/* + 1 */: data.length).toString();
		bLabel.setText(min + " to " + max + " of " + data.length);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == previousBtn) {
			// only process previous page if possible
			if (curMinRow > 0) {
				previousPage();
			}
		} else if (event.getSource() == nextBtn) {
			// Only process next page if there are possible more values to
			// display
			if (curMinRow + maximumRows < data.length) {
				nextPage();
			}
		} else {
			Cell clickedCell = flexTable.getCellForEvent(event);
			if (clickedCell == null) {
				JsonUtil.debug("Ignoring null cell");
				return;
			}
			int row = clickedCell.getRowIndex();
			actionRow = row;
			int col = clickedCell.getCellIndex();
			if (row == 0) {
				sortData(col);
			} else {
				if (allowSelection) {
					if (!multipleSelection) {
						// Remove past selection
						if (selected.size() > 0) {
							selected.clear();
						}
					}
					// We do the selection thing, then refresh display in the
					// toggleSelection routine
					toggleSelection(getValueByRow(row));
				}

				// Handle single click with handler, regardless
				if (tableRowClickHandler != null) {
					tableRowClickHandler.handleRowClick(getDataByRow(row), col);
				}
			}
		}
	}

	/**
	 * Removes all selected ids from list.
	 */
	public void clearAllSelections() {
		if (selected.size() > 0) {
			selected.clear();
		}
	}

	/**
	 * Returns list of all selected items.
	 */
	public List<String> getSelected() {
		return selected;
	}

	/**
	 * Returns the total countselected ids.
	 */
	public int getSelectedCount() {
		return selected.size();
	}

	public int getActionRow() {
		return actionRow;
	}

	/**
	 * This function will sort the data by column clicked param column.
	 */
	protected void sortData(int col) {
		if (data != null && data.length > 0) {
			String heading = columns.get(col).getHashMapping();
			HashMap<String, String> tempmap;
			for (int i = curMinRow; i < data.length; i++)
				for (int j = curMinRow; j < data.length; j++) {
					if (i != j
							&& needSwap(data[i].get(heading), data[j]
									.get(heading))) {
						tempmap = data[i];
						data[i] = data[j];
						data[j] = tempmap;
					}
				}
			sortDesc = !sortDesc;
			loadData(data, curMinRow);
		}
	}

	protected boolean needSwap(String str1, String str2) {
		boolean success = false;
		if (str1.trim().length() == 0 && str2.trim().length() == 0)
			success = true;
		else if (str1.trim().length() == 0 && str2.trim().length() != 0) {
			if (sortDesc)
				success = true;
		} else if (str1.trim().length() != 0 && str2.trim().length() == 0) {
			if (sortDesc)
				success = true;
		} else {
			str1 = str1.toLowerCase().toString();
			str2 = str2.toLowerCase().toString();
			int str1Length = str1.length();
			if (sortDesc) {
				for (int i = 0; i < str1Length; i++) {
					if (str1.charAt(i) < str2.charAt(i)) {
						success = true;
						break;
					} else if (str1.charAt(i) > str2.charAt(i)) {
						success = false;
						break;
					}
				}
			} else {
				for (int i = 0; i < str1Length; i++) {
					if (str1.charAt(i) > str2.charAt(i)) {
						success = true;
						break;
					} else if (str1.charAt(i) < str2.charAt(i)) {
						success = false;
						break;
					}
				}
			}
		}
		return success;
	}

	public void removeTableStyle() {
		this.flexTable.removeStyleName(TABLE_STYLE_NAME);
	}

	public void setNoItemsText(String noItemsText) {
		this.noItemsText = noItemsText;
	}

}
