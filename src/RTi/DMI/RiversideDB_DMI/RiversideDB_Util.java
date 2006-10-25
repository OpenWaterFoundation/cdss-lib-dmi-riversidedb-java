

//------------------------------------------------------------------------------
// RiversideDB_Util - Utility class containing static methods related to the 
// 		      RiversideDB_DMI library.
//	
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// History:
// 2002-23-05	Morgan Sheedy, RTi	Initial Implementation
//
// 2002-11-06	AMS, RTi		Changed class name from:
//					RiversideDBAdministrator_Util 
//					to: RTiDBAdmin_Util
// 2002-26-09	Morgan Love, RTi	Changed Application name from:
//					RiversideDBAdmin to:
//					RiverTrakAssistant and Class from:
//					RTiDBAdmin_Util to:
//					RiverTrakAssistant_Util.
//
// 2002-26-09	AML , RTi  		code clean up.
// 2003-26-09	AML , RTi  		Removed code to add Icon to JFrame 
//					since now have methods in 
//					JGUIUtil.
// 2004-11-25 Luiz Teixeira, RTi 	Moved RiverTrakAssistant_Util to
//					the RiversideDB_DMI library renaming
//					it to RiversideDB_System_Util.java.
// 2004-12-02 Luiz Teixeira, RTi 	Moved methods canWrite() from 
//						RTAssistant_Main_Frame.java.
// 2004-12-02 Luiz Teixeira, RTi 	Moved methods 
//						createHTMLDataDictionary and 
//						createHTMLTimeseriesList
//					   from	RTAssistant_Main_Frame.java.
// 2004-12-03 Luiz Teixeira, RTi	Renamed this class from
//						RiversideDB_System_Util.java 
//						to RiversideDB_Util.java.
// 2004-12-03 Luiz Teixeira, RTi	Cleanup and some documentation.
// 2004-12-21 Luiz Teixeira, RTi	Upgrade both canWrite() methods.
//					Removed the method 
//					        showPropertiesWorksheet().
//					This method is not used anymore. From
//						now on, the editor for the Props
//						table is instantiated in the
//						same way as the ReferenceTables.
//					Aditional cleanup and documentation.
// 2005-08-29	J. Thomas Sapienza, RTi	Added getProductGroupsChoices().
//------------------------------------------------------------------------------
package RTi.DMI.RiversideDB_DMI;

import java.awt.Dimension;
import java.io.File;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import RTi.DMI.DMIUtil;
import RTi.Util.GUI.JFileChooserFactory;
import RTi.Util.GUI.JGUIUtil;
import RTi.Util.GUI.SimpleFileFilter;
import RTi.Util.Message.Message;
import RTi.TS.TSIdent;

/**
Utility (static) methods related to the RTi.DMI.RiversideDB_DMI package.
*/
public class RiversideDB_Util
{

/**
Class name
*/
private static String __class = "RiversideDB_Util";

/**
Check if the user can write to the given table.
@param dmi Reference to the RiversideDB DMI conection.
@param RiversideDB_Tables Reference to a RiversideDb table.
@return true if user can write to table, false otherwise.
*/
public static boolean canWrite ( RiversideDB_DMI dmi,
				 RiversideDB_Tables dbTable )
{
	String routine = __class + ".canWrite", mssg;

	String  tableName       = dbTable.getTable_name();
	boolean writePermission = false;
	
	// Status
	mssg = "Checking write permission for table " + tableName; 
	Message.printStatus ( 1, routine, mssg );

	// Checking write permission
	try { 
		writePermission = dmi.canWrite ( 
					dbTable.getDBUser_num(),
					dbTable.getDBGroup_num(),
					dbTable.getRecord_DBPermissions() );
		
		// Debug
		if ( Message.isDebugOn ) {
			mssg = "DBUser       is "
				+ dbTable.getDBUser_num();
			Message.printDebug( 1, routine, mssg );
			mssg = "DBGroup      is "
				+ dbTable.getDBGroup_num();
			Message.printDebug( 1, routine, mssg );
			mssg = "DBPermission is "
				+ dbTable.getRecord_DBPermissions();
			Message.printDebug( 1, routine, mssg );
		}
		
		// Status
		mssg = "Write permission for table " + tableName
			+ " is " + writePermission;
		Message.printStatus ( 1, routine, mssg );	
		
	} catch ( Exception e ) {
		mssg = "Error checking write permission for table '"
		     + tableName + "!";
		Message.printWarning ( 1, routine, mssg );
		Message.printWarning ( 2, routine,    e );
	}

	return writePermission;
}

/**
Check if the user can write to the given table.
@param dmi Reference to the RiversideDB DMI conection.
@param tablesVector Reference to the Vector containing references to
the RiversideDb tables.
@param table The table to check write premission.
@return true if user can write to table, false otherwise.
*/
public static boolean canWrite ( RiversideDB_DMI dmi, String table )
{
	String routine = __class + ".canWrite", mssg;
	
	mssg = "Checking write permission for table " + table; 
	Message.printStatus ( 1, routine, mssg );

	boolean writePermission = false;

	// Get the vector of RiversideDB_Tables table objects.
	// If null return false.
	Vector tablesVector = dmi.getRiversideDB_Tables();
	if ( tablesVector == null ) return writePermission;

	// Or loop through the vector looking for a match for 'table'
	RiversideDB_Tables dbTable = null;
	String tableName           = null;
	
	for ( int i = 0; i < tablesVector.size(); i++ ) {
		
		// RiversideDB_Tables table object at 'i'
		dbTable = ( RiversideDB_Tables ) tablesVector.elementAt(i);
		
		if ( dbTable != null ) {
			
			// Table name in the RiversideDB_Tables object at 'i'
			tableName = dbTable.getTable_name();
			
			// Debug
			if ( Message.isDebugOn ) {
				mssg = "tableVector[" + i + "] = " + tableName;
				Message.printDebug( 2, routine, mssg );
			}
			
			// Checking for a match.
			if ( tableName.equalsIgnoreCase( table ) ) {
				// Found the match, check permission and break.
				writePermission = canWrite ( dmi, dbTable );
				break;	
			}
		} else {
			mssg = "Error checking for permission. Null table!";
			Message.printWarning ( 1, routine, mssg ); 
		}
	}

	return writePermission;
}

// REVISIT [LT] 2004-12-21 - This method should be moved to a new class.
//			     RiversideDB_HTML_DataDirectory where perhaps it
//			     could have the capability to generate data
//			     dictionary for individual tables as well as for
//			     the full list of tables.
//			     
/**
Creates a HTML file containing the Data Dictionary.
@param jFrame Reference to the calling application.
@param dmi Reference to the DIM connection.
@return true if user can write to table, false otherwise.
*/
public static void createHTMLDataDictionary (
	JFrame jFrame, RiversideDB_DMI dmi )
{
	String routine = __class + ".createHTMLDataDictionary";

	JGUIUtil.setWaitCursor( jFrame, true );
	
	String[] refTables = new String[12];
	int i = 0;
	
// REVISIT [LT] 2004-12-21 - SAM's comments from the first review.
//				"Split into separated methods?"
// 	The implementation of this spliting goes well with the 'new Class"
//	approach suggested above.	
	refTables[i++] = "DataDimension";
	refTables[i++] = "DataSource";
	refTables[i++] = "DataType";
	refTables[i++] = "DataUnits";
	refTables[i++] = "ImportType";
	refTables[i++] = "MeasCreateMethod";
	refTables[i++] = "MeasQualityFlag";
	refTables[i++] = "MeasReductionType";
	refTables[i++] = "MeasTimeScale";
	refTables[i++] = "MeasTransProtocol";
	refTables[i++] = "SHEFType";
	refTables[i++] = "TableLayout";

	String lastDirectorySelected  = JGUIUtil.getLastFileDialogDirectory();

	JFileChooser fc = null;
 	if (lastDirectorySelected != null) {
  		fc = JFileChooserFactory.createJFileChooser(
  			lastDirectorySelected);
 	} else {
  		fc = JFileChooserFactory.createJFileChooser();
 	}

 	fc.setDialogTitle("Select Data Dictionary HTML file");
 	SimpleFileFilter htmlff = new SimpleFileFilter("html", "HTML Files");
 	fc.addChoosableFileFilter(htmlff);
 	fc.setFileFilter(htmlff);
 	fc.setDialogType(JFileChooser.SAVE_DIALOG);

 	JGUIUtil.setWaitCursor( jFrame, false);
 	
 	// Prompty to user for a file.
 	int retVal = fc.showSaveDialog( jFrame );
 	if (retVal != JFileChooser.APPROVE_OPTION) return;
 	
 	JGUIUtil.setWaitCursor( jFrame, true);

	// Current directory
 	String currDir = ( fc.getCurrentDirectory() ).toString();
 	if ( !currDir.equalsIgnoreCase(lastDirectorySelected) ) {
  		JGUIUtil.setLastFileDialogDirectory(currDir);
 	}
 	
 	// File name from current directory and selected file.
 	String s = File.separator;
 	String fileName = currDir + s + fc.getSelectedFile().getName();

	try {
		Message.printStatus( 1, routine,
			"Creating data dictionary \"" + fileName + "\"." );

		DMIUtil.createHTMLDataDictionary(
			dmi, fileName, refTables, null );
	}
	catch ( Exception e ) {
		Message.printWarning( 2, routine, e);
		Message.printWarning( 1, routine,
			"Error writing data dictionary to " + fileName );
	}
	
 	JGUIUtil.setWaitCursor( jFrame, false );
}

// REVISIT [LT] 2004-12-21 - This method should be moved to a new class.
//			     RiversideDB_HTML_TimeseriesList.
//			     
/**
Creates a HTML file containing the timeseries list.
@param jFrame Reference to the calling object.
@param applicationTitle The title of the calling application. 
@param projectName The project name.
@param dmi Reference to the DIM connection.
*/
public static void createHTMLTimeseriesList ( JFrame jFrame,
	String applicationTitle, String projectName, RiversideDB_DMI dmi )
{
	String routine = __class + ".createHTMLTimeseriesList";

	JGUIUtil.setWaitCursor( jFrame, true );
	
	// Check DMI. 
	if ( dmi == null ) {
		Message.printWarning( 1, routine, 
			"Cannot create list because the DMI is null!" );
		return;
	}
	
	// Application title.
	String appTitle = applicationTitle;
	if ( applicationTitle==null || applicationTitle.length()<=0 )
	{
		appTitle = "RiversideDB";
	}

	// Project title
	String projName = projectName;
	if ( projectName==null || projectName.length()<=0 ) {
		projName = "";
	} 
	
	// Re-read database to get latest list
	Vector allTS_vect = new Vector();
	try {
		allTS_vect = dmi.readMeasTypeListByLocation();
	} catch ( Exception e ) {
		Message.printWarning( 2, routine, e );
		allTS_vect = new Vector();
	}
	int tsCount = allTS_vect.size();

	StringBuffer b = new StringBuffer();
	b.append( "<HTML><HEAD><TITLE>Time Series List</TITLE> "
		+ "</HEAD><BODY><TABLE border=\"1\">" );
	if ( projName == "" ) {
		b.append( "<tr><th colspan=\"5\"><h2 align=\"left\">"
			+ "<A NAME=\"tstitle\"> "
			+ "Time Series </A></h2></th></tr>" );
	} else {		
		b.append( "<tr><th colspan=\"5\"><h2 align=\"left\">"
			+ projName + "<A NAME=\"tstitle\"> "
			+ "- Time Series </A></h2></th></tr>" );
	}
			
	TSIdent tsid            = null;
	RiversideDB_MeasType mt = null;
	for( int i = 0; i < tsCount; i++ ) {
		
		// First time around. Add collumn titles.
		if ( i == 0 ) {
			b.append( "<tr><th>Location</th><th>Source</th>"
				+ "<th>data type</th><th>Interval</th>" );
		}
		
		// RiversideDB_MeasType
		mt = (RiversideDB_MeasType) allTS_vect.elementAt(i);
		if ( mt == null ) continue;
		
		// TSIdent
		try {
			tsid = mt.toTSIdent();
		} catch ( Exception e ) {
			continue;
		}
		if ( tsid == null ) continue;

		// Populate row for this TS 
		b.append( "<tr>" );
		b.append( "<td>" + tsid.getLocation() + "</td>" );
		b.append( "<td>" + tsid.getSource()   + "</td>" );
		b.append( "<td>" + tsid.getType()     + "</td>" );
		b.append( "<td>" + tsid.getInterval() + "</td>" );
		b.append( "</tr>" );

//		b.append( "<TR><TD>" + tsid.toString() + "</TD></TR>" );
	}
	
	b.append( "</TABLE></BODY></HTML>" );

	// Display this is a JEditor Window
	JEditorPane editor = new JEditorPane( "text/html", b.toString() );
	editor.scrollToReference("tstitle");
	editor.setCaretPosition(0);
	JScrollPane editorPane = new JScrollPane( editor);
	editorPane.setVerticalScrollBarPolicy(
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	editorPane.setPreferredSize( new Dimension( 300, 400) );
	JGUIUtil.center( editor);

	JFrame frame = new JFrame();
	if ( projName == "" ) {
		frame.setTitle( appTitle
			      + " - Time Series List" );
	} else {	
		frame.setTitle( appTitle  + " - "
			      + projName
			      + " - Time Series List" );
	}
	
	JGUIUtil.setIcon( frame, JGUIUtil.getIconImage() );
	frame.getContentPane().add( editorPane );
	frame.pack();
	frame.setVisible(true);
	
	JGUIUtil.setWaitCursor( jFrame, false );
}

/**
Returns a Vector of the product groups in the database, in the form:<br>
[ProductGroup_num] - [ProductGroup Identifier] - [ProductGroup Name]
@return a Vector of the product groups in the database.
*/
protected static Vector getProductGroupsChoices(RiversideDB_DMI dmi) 
throws Exception {
/*
JTS - 2005-08-24
See comment below.
	boolean root = false;

	RiversideDB_DBUser dbuser = _dbuser;
	
	if (dbuser.getLogin().trim().equalsIgnoreCase("root")) {
		root = true;
	}

	int userNum = dbuser.getDBUser_num();
	int groupNum = dbuser.getPrimaryDBGroup_num();
*/

	Vector groups = new Vector();

	Vector v = dmi.readProductGroupList();
	
	int size = v.size();
	int pgGroupNum = 0;
	int pgUserNum = 0;
	RiversideDB_ProductGroup pg = null;
	String p = null;
	for (int i = 0; i < size; i++) {
		pg = (RiversideDB_ProductGroup)v.elementAt(i);
		pgGroupNum = pg.getDBGroup_num();
		pgUserNum = pg.getDBUser_num();

		groups.add("" + pg.getProductGroup_num()
			+ " - " + pg.getIdentifier() + " - "
			+ pg.getName());

/*
JTS - 2005-08-24
Originally, I had though that the permissions controls in the ProductGroup
table could be used to control who is allowed to add/delete/etc records 
related to that product group, but on further examination of the permissions
system it's evident that those permissions simply control permissions on the
specific record in the ProductGroup table.  Therefore, there's no way to control
who can add TSProducts to certain groups.  The user and group numbers could be
checked, but at least half the current users in RiversideDB don't have 
corresponding records in the ProductGroup table.  This means they could go 
through the effort of creating a TSProduct and then try to save it and not
have any valid ProductGroups be found.   For now it seems best to simply allow
them to save as a certain ProductGroup -- this won't control their permissions
to edit other TSProducts in the same group.  

So ... leaving this code in here in case I need to come back to it and use it
somehow:

		if (root) {
			groups.add("" + pg.getProductGroup_num()
				+ " - " + pg.getIdentifier() + " - "
				+ pg.getName());
			continue;
		}

		p = pg.getDBPermissions().toUpperCase();

		if (p.indexOf("UI+") > -1 || p.indexOf("UC+") > -1
		    || p.indexOf("UU+") > -1) {
			if (userNum == pgUserNum) {
				groups.add("" + pg.getProductGroup_num()
					+ " - " + pg.getIdentifier() + " - "
					+ pg.getName());
				continue;
			}
		}
		
		if (p.indexOf("GI+") > -1 || p.indexOf("GC+") > -1
		    || p.indexOf("GU+") > -1) {
			if (groupNum == pgGroupNum) {
				groups.add("" + pg.getProductGroup_num()
					+ " - " + pg.getIdentifier() + " - "
					+ pg.getName());
				continue;
			}
		}

		if (p.indexOf("OI+") > -1 || p.indexOf("OC+") > -1
		    || p.indexOf("OU+") > -1) {
			groups.add("" + pg.getProductGroup_num()
				+ " - " + pg.getIdentifier() + " - "
				+ pg.getName());
			continue;
		}
*/
	}

	return groups;
}

}
//------------------------------------------------------------------------------
