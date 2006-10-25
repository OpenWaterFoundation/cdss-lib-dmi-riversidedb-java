// ----------------------------------------------------------------------------
// RiversideDB_State_CellRenderer - Class for rendering cells for a table of
//	state objects.
// ----------------------------------------------------------------------------
// Copyright:   See the COPYRIGHT file
// ----------------------------------------------------------------------------
// History:
// 2003-10-28		J. Thomas Sapienza, RTi	Initial versoin.
// ----------------------------------------------------------------------------

package RTi.DMI.RiversideDB_DMI;

import RTi.Util.GUI.JWorksheet_AbstractExcelCellRenderer;

/**
This class is used to render cells for state tables.
*/
public class RiversideDB_State_CellRenderer
extends JWorksheet_AbstractExcelCellRenderer {

/**
The table model for which this class will render cells.
*/
private RiversideDB_State_TableModel __tableModel;

/**
Constructor.  
@param tableModel the table model for which to render cells
*/
public RiversideDB_State_CellRenderer(
	RiversideDB_State_TableModel tableModel) {
	__tableModel = tableModel;
}

/**
Returns the format for a given column.  Formats are in a form that will
be understood by StringUtil.format().
@param column the colum for which to return the format.
@return the format (as used by StringUtil.format) for a column.
*/
public String getFormat(int column) {
	return __tableModel.getFormat(column);
}

/**
Returns the widths of the columns in the table.  Widths are specified in 
number of characters, not number of pixels.
@return an integer array of the widths of the columns in the table.
*/
public int[] getColumnWidths() {
	return __tableModel.getColumnWidths();
}

}
