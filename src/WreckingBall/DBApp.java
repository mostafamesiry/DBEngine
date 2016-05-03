package WreckingBall;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import BPlusTree.BPlusTree;

class DBAppException extends Exception {

	public DBAppException(String s)
	{
		super(s);
	}
}

class DBEngineException extends Exception {

}

public class DBApp {

	ArrayList<Table> tables;
	static FileWriter csv;


	public void init() throws Exception
	{
		String currentLine;
		tables=new ArrayList<Table>();
		FileReader fileReader=null;
		try
		{
			fileReader= new FileReader("metadata.csv");
			BufferedReader br = new BufferedReader(fileReader);
			ArrayList<String> old=new ArrayList<String>();
			while ((currentLine = br.readLine()) != null) 
			{
				old.add(currentLine);
			}	

			csv=new FileWriter("metadata.csv");
			for(int i=0;i<old.size();i++)
				DBApp.csv.write(old.get(i)+'\n');
			csv.flush();
		}

		catch(FileNotFoundException e)
		{
			csv=new FileWriter("metadata.csv");
		}

		try
		{	
			FileInputStream xs = new FileInputStream("TablesArray.class");
			ObjectInputStream xa = new ObjectInputStream(xs);
			tables = (ArrayList<Table>) xa.readObject();
			System.out.println(tables.size());
		}
		catch(EOFException | FileNotFoundException f)
		{
			UpdateTableFile();
		}
	}


	public void deleteFromTable(String strTableName, Hashtable<String,Object> htblColNameValue, String strOperator)
			throws DBEngineException, ClassNotFoundException, IOException, DBAppException
	{	

		for(int i=0;i<tables.size();i++)
		{
			if(tables.get(i).name.equals(strTableName))
			{
				tables.get(i).deleteFromTable(htblColNameValue, strOperator);
				return;

			}
		}
		throw new DBAppException("Table Not Found");
	}
	public void UpdateTableFile() throws Exception
	{
		FileOutputStream file = new FileOutputStream("TablesArray.class");
		ObjectOutputStream out = new ObjectOutputStream(file);
		out.writeObject(tables);
		
	}

	public void createTable(String strTableName,    Hashtable<String,String> htblColNameType, 
			Hashtable<String,String> htblColNameRefs, String strKeyColName)  throws Exception
	{ 
		for(int i=0;i<tables.size();i++)
		{
			if(tables.get(i).name.equals(strTableName))
			{
				return;
			}	
		}
		Table temp=new Table(strTableName,htblColNameType,htblColNameRefs,strKeyColName);
		tables.add(temp);
		temp.createIndex(strKeyColName);
		UpdateTableFile();
	}


	public void createIndex(String strTableName, String strColName)  throws Exception
	{
		for(int i=0;i<tables.size();i++)
		{
			if(tables.get(i).name.equals(strTableName))
			{
				
				tables.get(i).createIndex(strColName);
				UpdateTableFile();
				return;
			}
		}
		System.out.println(tables.get(0).name);

		throw new DBAppException("Table Not Found");

	}

	public void insertIntoTable(String strTableName, Hashtable<String,Object> htblColNameValue)  throws Exception
	{
		for(int i=0;i<tables.size();i++)
		{
			if(tables.get(i).name.equals(strTableName))
			{
				tables.get(i).insertIntoTable(htblColNameValue);
				UpdateTableFile();
				//UpdateIndeces(strTableName);
				break;
			}
		}
	}

//	private void UpdateIndeces(String strTableName) {
//		Table table=tables.get(tables.indexOf(strTableName));
//		try {
//			createIndex(strTableName,table.primaryKey);
//		} catch (Exception e) {
//			
//		}
//		
//		
//	}


	public void updateTable(String strTableName,Object strKey, Hashtable<String,Object> htblColNameValue)  throws DBAppException, ClassNotFoundException, IOException
	{
		for(int i=0;i<tables.size();i++)
		{
			if(tables.get(i).name.equals(strTableName))
			{
				tables.get(i).updateTable(strKey, htblColNameValue);
				return;

			}
		}
		throw new DBAppException("Table Not Found");


	}



	public Iterator selectFromTable(String strTable,  Hashtable<String,Object> htblColNameValue, 
			String strOperator) throws DBEngineException, ClassNotFoundException, DBAppException, IOException
	{
		for(int i=0;i<tables.size();i++)
		{
			if(tables.get(i).name.equals(strTable))
			{
				return tables.get(i).selectFromTable(htblColNameValue, strOperator);

			}
		}
		throw new DBAppException("Table Not Found");

	}


	public static void main(String [] args) throws Exception {

		//Team: 	
		//Mostafa Ahmed El-Messiry 31-1987 T13
		//Mohamed Amr Abouzeid 31-11381 T13	


		// create a new DBApp
		DBApp myDB = new DBApp();

		// initialize it
		myDB.init();

		// creating table "Faculty"

		Hashtable<String, String> fTblColNameType = new Hashtable<String, String>();
		fTblColNameType.put("ID", "Integer");
		fTblColNameType.put("Name", "String");
		fTblColNameType.put("Students", "Integer");

		Hashtable<String, String> fTblColNameRefs = new Hashtable<String, String>();

		myDB.createTable("Faculty", fTblColNameType, fTblColNameRefs, "ID");


		

		// insert in table "Faculty"

						Hashtable<String,Object> ftblColNameValue1 = new Hashtable<String,Object>();
						ftblColNameValue1.put("ID", Integer.valueOf( "1" ) );
						ftblColNameValue1.put("Name", "Media Engineering and Technology");
						ftblColNameValue1.put("Students", Integer.valueOf( "2000" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue1);
				
						Hashtable<String,Object> ftblColNameValue2 = new Hashtable<String,Object>();
						ftblColNameValue2.put("ID", Integer.valueOf( "2" ) );
						ftblColNameValue2.put("Name", "Management Technology");
						ftblColNameValue2.put("Students", Integer.valueOf( "4000" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue2);
						
						Hashtable<String,Object> ftblColNameValue3 = new Hashtable<String,Object>();
						ftblColNameValue3.put("ID", Integer.valueOf( "3" ) );
						ftblColNameValue3.put("Name", "Technology");
						ftblColNameValue3.put("Students", Integer.valueOf( "500" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue3);
					
			
						Hashtable<String,Object> ftblColNameValue4 = new Hashtable<String,Object>();
						ftblColNameValue4.put("ID", Integer.valueOf( "4" ) );
						ftblColNameValue4.put("Name", "Mechatronics");
						ftblColNameValue4.put("Students", Integer.valueOf( "7000" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue4);
					
						Hashtable<String,Object> ftblColNameValue5 = new Hashtable<String,Object>();
						ftblColNameValue5.put("ID", Integer.valueOf( "5" ) );
						ftblColNameValue5.put("Name", "Pharmacy");
						ftblColNameValue5.put("Students", Integer.valueOf( "1890" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue5);
						
		
						Hashtable<String,Object> ftblColNameValue6 = new Hashtable<String,Object>();
						ftblColNameValue6.put("ID", Integer.valueOf( "6" ) );
						ftblColNameValue6.put("Name", "MET");
						ftblColNameValue6.put("Students", Integer.valueOf( "760" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue6);
						
						Hashtable<String,Object> ftblColNameValue7 = new Hashtable<String,Object>();
						ftblColNameValue7.put("ID", Integer.valueOf( "7" ) );
						ftblColNameValue7.put("Name", "IET");
						ftblColNameValue7.put("Students", Integer.valueOf( "430" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue7);
						
						Hashtable<String,Object> ftblColNameValue8 = new Hashtable<String,Object>();
						ftblColNameValue8.put("ID", Integer.valueOf( "8" ) );
						ftblColNameValue8.put("Name", "EMS");
						ftblColNameValue8.put("Students", Integer.valueOf( "90" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue8);
						
						Hashtable<String,Object> ftblColNameValue9 = new Hashtable<String,Object>();
						ftblColNameValue9.put("ID", Integer.valueOf( "9" ) );
						ftblColNameValue9.put("Name", "DMET");
						ftblColNameValue9.put("Students", Integer.valueOf( "50" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue9);
						
						Hashtable<String,Object> ftblColNameValue10 = new Hashtable<String,Object>();
						ftblColNameValue10.put("ID", Integer.valueOf( "10" ) );
						ftblColNameValue10.put("Name", "Technology");
						ftblColNameValue10.put("Students", Integer.valueOf( "5500" ));
						myDB.insertIntoTable("Faculty", ftblColNameValue10);
//////
////
//		
		Hashtable<String,Object> ftblColNameValue11 = new Hashtable<String,Object>();
		ftblColNameValue11.put("ID", Integer.valueOf( "11" ) );
		ftblColNameValue11.put("Name", "Handasa");
		ftblColNameValue11.put("Students", Integer.valueOf( "560" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue11);
		
		
		
		
		
		
		Hashtable<String,Object> ftblColNameValue12 = new Hashtable<String,Object>();
		ftblColNameValue12.put("ID", Integer.valueOf( "12" ) );
		ftblColNameValue12.put("Name", "IMC");
		ftblColNameValue12.put("Students", Integer.valueOf( "93" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue12);
		
		
		
		
		Hashtable<String,Object> ftblColNameValue13 = new Hashtable<String,Object>();
		ftblColNameValue13.put("ID", Integer.valueOf( "13" ) );
		ftblColNameValue13.put("Name", "Dentisry");
		ftblColNameValue13.put("Students", Integer.valueOf( "9900" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue13);
		
		
		
		Hashtable<String,Object> ftblColNameValue14 = new Hashtable<String,Object>();
		ftblColNameValue14.put("ID", Integer.valueOf( "14" ) );
		ftblColNameValue14.put("Name", "Medicine");
		ftblColNameValue14.put("Students", Integer.valueOf( "10" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue14);
		
		
		
		
		Hashtable<String,Object> ftblColNameValue15 = new Hashtable<String,Object>();
		ftblColNameValue15.put("ID", Integer.valueOf( "15" ) );
		ftblColNameValue15.put("Name", "NuclearEnergy");
		ftblColNameValue15.put("Students", Integer.valueOf( "77" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue15);
		
		
		
		
		Hashtable<String,Object> ftblColNameValue16 = new Hashtable<String,Object>();
		ftblColNameValue16.put("ID", Integer.valueOf( "16" ) );
		ftblColNameValue16.put("Name", "MET");
		ftblColNameValue16.put("Students", Integer.valueOf( "23" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue16);
		
		
		
		Hashtable<String,Object> ftblColNameValue17 = new Hashtable<String,Object>();
		ftblColNameValue17.put("ID", Integer.valueOf( "17" ) );
		ftblColNameValue17.put("Name", "Technology");
		ftblColNameValue17.put("Students", Integer.valueOf( "886" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue17);
		
		
		
		
		Hashtable<String,Object> ftblColNameValue18 = new Hashtable<String,Object>();
		ftblColNameValue18.put("ID", Integer.valueOf( "18" ) );
		ftblColNameValue18.put("Name", "Technology");
		ftblColNameValue18.put("Students", Integer.valueOf( "123" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue18);
		
		
		
		Hashtable<String,Object> ftblColNameValue19 = new Hashtable<String,Object>();
		ftblColNameValue19.put("ID", Integer.valueOf( "19" ) );
		ftblColNameValue19.put("Name", "Technology");
		ftblColNameValue19.put("Students", Integer.valueOf( "865" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue19);
		
		
		
		Hashtable<String,Object> ftblColNameValue20 = new Hashtable<String,Object>();
		ftblColNameValue20.put("ID", Integer.valueOf( "20" ) );
		ftblColNameValue20.put("Name", "Technology");
		ftblColNameValue20.put("Students", Integer.valueOf( "343" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue20);
		
		
		Hashtable<String,Object> ftblColNameValue21 = new Hashtable<String,Object>();
		ftblColNameValue20.put("ID", Integer.valueOf( "21" ) );
		ftblColNameValue20.put("Name", "Technology");
		ftblColNameValue20.put("Students", Integer.valueOf( "6757" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue20);
		
		
		
		Hashtable<String,Object> ftblColNameValue22 = new Hashtable<String,Object>();
		ftblColNameValue20.put("ID", Integer.valueOf( "22" ) );
		ftblColNameValue20.put("Name", "Technology");
		ftblColNameValue20.put("Students", Integer.valueOf( "81" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue20);
		
		
		
		Hashtable<String,Object> ftblColNameValue23 = new Hashtable<String,Object>();
		ftblColNameValue20.put("ID", Integer.valueOf( "23" ) );
		ftblColNameValue20.put("Name", "Technology");
		ftblColNameValue20.put("Students", Integer.valueOf( "23445" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue20);
		
		
		Hashtable<String,Object> ftblColNameValue25 = new Hashtable<String,Object>();
		ftblColNameValue20.put("ID", Integer.valueOf( "25" ) );
		ftblColNameValue20.put("Name", "Technology");
		ftblColNameValue20.put("Students", Integer.valueOf( "8" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue20);
		
		Hashtable<String,Object> ftblColNameValue24 = new Hashtable<String,Object>();
		ftblColNameValue20.put("ID", Integer.valueOf( "24" ) );
		ftblColNameValue20.put("Name", "Technology");
		ftblColNameValue20.put("Students", Integer.valueOf( "12" ));
		myDB.insertIntoTable("Faculty", ftblColNameValue20);
		
		
		
		
		
		
		
		
		
		myDB.createIndex("Faculty", "Students");
		
		
		myDB.tables.get(0).secInd.get("Students").getRoot().printTree(2);
		
		
		System.out.println("-----------------------------------------------------------------------------");
		
		myDB.tables.get(0).priInd.getRoot().printTree(2);
		
		Hashtable<String,Object> stblColNameValue = new Hashtable<String,Object>();
	
//
		Iterator myIt = myDB.selectFromTable("Faculty", stblColNameValue,"AND");

		
		while(myIt.hasNext()) {
			System.out.println(myIt.next());
		}
		Hashtable<String,Object> stblColNameValued = new Hashtable<String,Object>();
			stblColNameValued.put("Students",Integer.valueOf( "2000" ));

		myDB.deleteFromTable("Faculty",stblColNameValued , "AND");
		
		System.out.println("~~~~~~~~Deleted~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		Iterator myIt2 = myDB.selectFromTable("Faculty", stblColNameValue,"AND");
		
		while(myIt2.hasNext()) {
			System.out.println(myIt2.next());
		}

		Hashtable<String,Object> htblColNameValue55 = new Hashtable<String,Object>();
		htblColNameValue55.put("Students",Integer.valueOf("77777"));
			htblColNameValue55.put("Name","Yahia");
			
			
		myDB.updateTable("Faculty", new Integer(12),htblColNameValue55 );

		Iterator myIt4 = myDB.selectFromTable("Faculty", stblColNameValue,"AND");
		while(myIt4.hasNext()) {
			System.out.println(myIt4.next());
		}


	}

}
