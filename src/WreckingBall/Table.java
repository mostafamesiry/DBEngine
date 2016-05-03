package WreckingBall;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;

import BPlusTree.BPlusTree;
import BPlusTree.LeafNode;

public class Table implements Serializable
{
	String name;
	BPlusTree priInd;
	Hashtable<String,BPlusTree> secInd;
	Hashtable<String,String> columns; 
	Hashtable<String,String> references;
	String primaryKey;
	static boolean once;
	int pageNumbers = 0;
	int totalCount=0;
	int N;



	public void deleteFromTable(Hashtable<String, Object> htblColNameValue,	String strOperator) throws ClassNotFoundException, IOException, DBAppException 
	{		
		ArrayList<Tuple> tobeDeleted = new ArrayList<Tuple>();
		boolean conditionsValid;
		if(strOperator.equals("AND")||strOperator.equals("OR"))
		{
			for(int i=1;i<=pageNumbers;i++) //loop for pages
			{	
				FileInputStream xs = new FileInputStream(name+i+".class");
				ObjectInputStream xa = new ObjectInputStream(xs);
				ArrayList<Tuple> temp = (ArrayList<Tuple>) xa.readObject();
				System.out.println("Size:    "+temp.size());
				System.out.println("Count: "+ totalCount);
				for(int j=0;j<temp.size();j++)  //loop for lines in the pages
				{

					if(temp.get(j)!=null)
					{
						if(strOperator.equals("AND"))
						{

							conditionsValid=true;
							for(String key: htblColNameValue.keySet())
							{//loop for conditions.

								if(!temp.get(j).row.get(key).equals(htblColNameValue.get(key))){
									conditionsValid=false;
									break;
								}
							}	
							if(conditionsValid)
							{
								tobeDeleted.add(temp.get(j));
								temp.remove(j);
								temp.add(j,null);
								//	j--;
							}
						}
						else if(strOperator.equals("OR"))
						{
							conditionsValid=false;
							for(String key: htblColNameValue.keySet()){  //loop for conditions.
								System.out.println("tempget: "+temp.get(j));
								if(temp.get(j).row.get(key).equals(htblColNameValue.get(key))){
									conditionsValid=true;
									break;
								}
							}	
							if(conditionsValid)
							{
								tobeDeleted.add(temp.get(j));
								temp.remove(j);
								temp.add(j, null);
								//			j--;
							}
						}
					}
				}
				////////////////////////
				FileOutputStream file = new FileOutputStream(name+i+".class");
				ObjectOutputStream out = new ObjectOutputStream(file);
				out.writeObject(temp);
				file.flush();
				out.flush();
				file.close();
				out.close();
				///////////////////////
			}

		}


		else
		{
			throw new DBAppException("Invalid Operator");

		}

		for(int i=0;i<tobeDeleted.size();i++)
		{
			Tuple temp=tobeDeleted.get(i);
			this.priInd.deleteBPlusTree((Integer)temp.getRow().get(this.primaryKey));
			for(String key:secInd.keySet())
			{
				secInd.get(key).deleteBPlusTree((Integer)temp.getRow().get(key));
			}

		}



	}
	public String primaryKeyType() throws IOException, DBAppException
	{
		FileReader fileReader= new FileReader("metadata.csv");
		BufferedReader br = new BufferedReader(fileReader);
		String currentLine="";
		String s="";
		while ((currentLine = br.readLine()) != null) 
		{

			String[] str=new String[6];
			str=currentLine.split(",");
			if(str[0].equals(this.name)&&str[1].substring(1).equals(primaryKey))
			{
				System.out.println("IIINNNN");
				s=str[2];
			}
		}
		return s.substring(1);
	}


	public void updateTable(Object strKey, Hashtable<String,Object> htblColNameValue) throws DBAppException, ClassNotFoundException, IOException
	{
		ArrayList<Tuple> temp=new ArrayList<Tuple>();
		String filename=null;
		if(strKey.getClass().getName().equals(this.primaryKeyType()))
		{

			
			LeafNode node=this.priInd.searchBPlusTree((Integer) strKey,false);
			if(node!=null)
			{
				int index=node.getValues().indexOf((Integer) strKey);
				Triplet pageLocation=node.getDataPointer().get(index);
				int pageNumber=pageLocation.getPageNumber();
				filename=this.name+pageNumber+".class";
				int location =pageLocation.getPositon();
				FileInputStream xs = new FileInputStream(filename);
				ObjectInputStream xa = new ObjectInputStream(xs);
				temp = (ArrayList<Tuple>) xa.readObject();
				System.out.println("Size:    "+temp.size());
				System.out.println("Count: "+ totalCount);

				if(temp.get(location)!=null)
				{
					if(temp.get(location).row.get(primaryKey).equals(strKey))
					{
						for(String key: htblColNameValue.keySet())
						{
							/////

							if(key.equals(primaryKey))
							{
								priInd.deleteBPlusTree((Integer)temp.get(location).getRow().get(key));
							}
							else
							{
								for(String skey:secInd.keySet())
								{
									if(skey.equals(key))
									{
										secInd.get(skey).deleteBPlusTree((Integer)temp.get(location).getRow().get(key));
									}
								}
							}

							/////
							temp.get(location).row.put(key, htblColNameValue.get(key));
							temp.get(location).row.put("TouchDate",new Date());
							//B+<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

							if(key.equals(primaryKey))
							{
								priInd.insertBPlusTree((Integer)htblColNameValue.get(key));
							}
							else
							{
								for(String skey:secInd.keySet())
								{
									if(skey.equals(key))
									{
										secInd.get(skey).insertBPlusTree((Integer)htblColNameValue.get(key));
									}
								}
							}

							///B+<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

						}

					}



				}


			}
			if(filename!=null)
			{
				FileOutputStream file = new FileOutputStream(filename);
				ObjectOutputStream out = new ObjectOutputStream(file);
				out.writeObject(temp);
				file.flush();
				out.flush();
				file.close();
				out.close();
			}
			else
			{
				throw new DBAppException("Key Not found");
			}
		}
		else
		{
			throw new DBAppException("Key type incompatible with key type in metadata file");
		}
	}











	public Table(String name,Hashtable<String,String> htblColNameType, Hashtable<String,String> htblColNameRefs, String strKeyColName) throws Exception 
	{
		secInd=new Hashtable<String,BPlusTree>();

		//DBApp.config contains the int 200 but I can't access it since it isn't in the same package	

		FileInputStream nReader = new FileInputStream("DBApp.config");
		ObjectInputStream nStream = new ObjectInputStream(nReader);
		int[] config = ((int[]) nStream.readObject());
		N =config[0];
		//System.out.println("N= "+N);

		if (once==false){
			once=true;
			DBApp.csv.write("Table Name, Column Name, Column Type, Key, Indexed, References"+'\n');
		}
		this.name=name;
		this.columns=htblColNameType;
		columns.put("TouchDate", "Date");
		this.references=htblColNameRefs;
		this.primaryKey=strKeyColName;

		priInd=new BPlusTree(config[1]);

		boolean bolkey;
		for(String key: columns.keySet()){
			if(key.equals(primaryKey))
				bolkey=true;
			else
				bolkey=false;
			if(references.containsKey(key))
				DBApp.csv.write(name+", "+key+", "+javaType(columns.get(key))+", "+bolkey+", "+"false"+" , "+references.get(key)+ '\n');

			else
				DBApp.csv.write(name+", "+key+", "+javaType(columns.get(key))+", "+bolkey+", "+"false"+", null"+ '\n');
			DBApp.csv.flush();

		}
	}
	public boolean  isValidInsertion(Hashtable<String,Object> htblColNameValue) throws IOException, DBAppException{
		for(String key: htblColNameValue.keySet()){
			FileReader fileReader= new FileReader("metadata.csv");
			BufferedReader br = new BufferedReader(fileReader);
			String currentLine="";
			boolean found=false;
			while ((currentLine = br.readLine()) != null) 
			{

				String[] str=new String[6];
				str=currentLine.split(",");
				if(str[0].equals(this.name)){
					if(str[1].substring(1).equals(key)){
						if(str[2].substring(1).equals(htblColNameValue.get(key).getClass().getName())){
							found=true;
							break;
						}
					}
				}
			}
			if(found==false){
				throw new DBAppException("Not Valid "+this.name);
				//return false;
			}
		}
		return true;
	}

	public void insertIntoTable(Hashtable<String,Object> htblColNameValue) throws Exception
	{	

		ArrayList<Tuple> temp =new ArrayList<Tuple>();
		if(needsPage())
		{
			pageNumbers++;
			FileOutputStream file = new FileOutputStream(name+pageNumbers+".class");
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(new ArrayList<>());
			out.flush();
		}
		if(!hasDuplicate(htblColNameValue)&&isValidInsertion(htblColNameValue)){
			//read
			totalCount++;
			FileInputStream xs = new FileInputStream(name+pageNumbers+".class");
			ObjectInputStream xa = new ObjectInputStream(xs);
			temp = (ArrayList<Tuple>) xa.readObject();

			htblColNameValue.put("TouchDate", new Date());
			temp.add(new Tuple(htblColNameValue));

			//B+ <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

			this.priInd.insertBPlusTree((Integer)htblColNameValue.get(primaryKey));
			LeafNode leaf = priInd.searchBPlusTree((Integer)htblColNameValue.get(primaryKey),true);
			//			leaf.getDataPointer().add(pageNumbers);
			//			//error
			//			leaf.getDataPointer().add(totalCount%200-1);
			leaf.getDataPointer().add(leaf.getValues().indexOf((Integer)htblColNameValue.get(primaryKey)),new Triplet(pageNumbers,totalCount%200-1));


			for(String key:secInd.keySet()){
				secInd.get(key).insertBPlusTree((Integer)htblColNameValue.get(key));

				LeafNode leaf1 = secInd.get(key).searchBPlusTree((Integer)htblColNameValue.get(key),true);
				//				leaf1.getDataPointer().add(pageNumbers);
				//				//error
				//				leaf1.getDataPointer().add(totalCount%200-1);
				leaf1.getDataPointer().add(leaf1.getValues().indexOf((Integer)htblColNameValue.get(key)),new Triplet(pageNumbers, totalCount%200-1));

			}




			///B+ <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


			xs.close();
			xa.close();
			//write
			FileOutputStream file = new FileOutputStream(name+pageNumbers+".class");
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(temp);
			file.flush();
			out.flush();
			file.close();
			out.close();

		}
		else
			throw new DBAppException("Duplicate");





	}



	public boolean hasDuplicate(Hashtable<String,Object> htblColNameValue) throws ClassNotFoundException, DBAppException, IOException {

		Hashtable<String,Object> htb=new Hashtable<>();
		htb.put(primaryKey, htblColNameValue.get(primaryKey));
		return PageSelect(htb, "AND").iterator().hasNext();


	}


	public static void main(String[] args) throws Exception {
	}
	public boolean needsPage()
	{
		if(totalCount%N==0)
		{
			return true;
		}
		else
			return false;

	}
	public String toString()
	{
		return name;
	}

	public String javaType(String type) throws Exception
	{
		if(type.equals("Date"))
		{
			return "java.util.Date";
		}
		else if(type.equals("Integer")||type.equals("String")||type.equals("Double"))
		{
			return "java.lang."+type;
		}
		else
			throw new Exception("Invalid datatype");

	}
	public static int realSize(String s) throws ClassNotFoundException, IOException{
		FileInputStream xs = new FileInputStream(s);
		ObjectInputStream xa = new ObjectInputStream(xs);
		ArrayList<Tuple> temp = (ArrayList<Tuple>) xa.readObject();
		int j=0;
		for(int i=0;i<temp.size();i++){
			if(temp.get(i)!=null){
				j++;
			}
		}
		return j;
	}

	public Iterator Treeselect(Hashtable<String, Object> htblColNameValue, String strOperator,ArrayList<String>strings) throws DBAppException, IOException, ClassNotFoundException {
		ArrayList<ArrayList<Tuple>> tuples=new ArrayList<ArrayList<Tuple>>();


		for(String key: htblColNameValue.keySet()){
			ArrayList<Tuple> it=new ArrayList<Tuple>();
			if(strings.contains(key)){
				if(key.equals(primaryKey)){
					System.out.println("this is a primary key");
					LeafNode todo=priInd.searchBPlusTree((Integer)htblColNameValue.get(key),false);

					for(int i = 0;todo!=null&&i<todo.getValues().size();i++)
					{
						if(todo.getValues().get(i).equals(htblColNameValue.get(key)))
						{
							System.out.println("inside the if condition");

							Triplet tri=todo.getDataPointer().get(i);
							FileInputStream xs = new FileInputStream(this.name+tri.getPageNumber()+".class");
							ObjectInputStream xa = new ObjectInputStream(xs);
							ArrayList <Tuple> temp = (ArrayList<Tuple>) xa.readObject();

							it.add(temp.get(tri.getPositon()));
						}
					}



				}
				else{

					LeafNode todo=secInd.get(key).searchBPlusTree((Integer)htblColNameValue.get(key),false);
					for(int i = 0;todo!=null&&i<todo.getValues().size();i++)
					{
						if(todo.getValues().get(i).equals(htblColNameValue.get(key)))
						{


							Triplet tri=todo.getDataPointer().get(i);
							FileInputStream xs = new FileInputStream(this.name+tri.getPageNumber()+".class");
							ObjectInputStream xa = new ObjectInputStream(xs);
							ArrayList <Tuple> temp = (ArrayList<Tuple>) xa.readObject();
							it.add(temp.get(tri.getPositon()));
						}
					}



				}
				tuples.add(it);


			}
			else{

				Hashtable<String, Object> x=new Hashtable<String, Object>();
				x.put(key, htblColNameValue.get(key));
				tuples.add(PageSelect(x, "AND"));
			}
		}
		if(strOperator.equals("AND"))
		{

			return intersectIterator(tuples);
		}
		else
		{
			return unionIterator(tuples);
		}


	}

	public Iterator unionIterator(ArrayList<ArrayList<Tuple>> tuples) {
		ArrayList<Tuple> it=new ArrayList<Tuple>();
		for(int i=0;i<tuples.size();i++)
		{
			for(int j=0;j<tuples.get(i).size();j++)
			{
				boolean emb=true;
				for(int h=0;h<it.size();h++){
					if(it.get(h).toString().equals(tuples.get(i).get(j).toString())){
						emb=false;
						break;
					}
				}
				if(emb){
					it.add(tuples.get(i).get(j));
				}
				//				if(!it.contains((Tuple)tuples.get(i).get(j)))
				//				{
				//					it.add(tuples.get(i).get(j));
				//				}
			}
		}
		return it.iterator();
	}

	public Iterator intersectIterator(ArrayList<ArrayList<Tuple>> tuples) {
		//		ArrayList<Tuple> ans=new ArrayList<Tuple>();
		//
		//
		//		ArrayList<Tuple> it=tuples.get(0);
		//
		//		for(int i=0;i<it.size();i++){
		//			boolean willbeAdded=true;
		//			for(int j=1;j<tuples.size();j++){
		//				boolean foundinArray=false;
		//
		//				for(int  k=0;k<tuples.get(j).size();k++){
		//					if(tuples.get(j).get(k).toString().equals(it.get(i).toString())){
		//						foundinArray=true;
		//						break;
		//					}
		//				}
		//				willbeAdded=willbeAdded&&foundinArray;
		//
		//
		//			}
		//			if(willbeAdded)
		//			{
		//				ans.add(it.get(i));
		//			}
		//		}
		//		
		//		return ans.iterator();

		ArrayList<Tuple> it=new ArrayList<Tuple>();
		ArrayList<Tuple> ans=new ArrayList<Tuple>();

		it=tuples.get(0);

		for(int i=0;i<it.size();i++){
			boolean itWilBeAdded=true;
			for(int j=1;j<tuples.size();j++){
				boolean itwontBeRemoved=false;
				for(int  k=0;k<tuples.get(j).size();k++){
					if(tuples.get(j).get(k).toString().equals(it.get(i).toString())){
						itwontBeRemoved=true;
					}
				}
				if(itwontBeRemoved==false){
					itWilBeAdded=false;
				}

			}
			if(itWilBeAdded==true){
				ans.add(it.get(i));
			}

		}

		return ans.iterator();
		//		

	}


	public Iterator selectFromTable(Hashtable<String, Object> htblColNameValue, String strOperator) throws DBAppException, IOException, ClassNotFoundException {

		String currentLine;
		ArrayList<String> strings=new ArrayList<String>();
		for(String key: htblColNameValue.keySet()){
			FileReader fileReader= new FileReader("metadata.csv");
			BufferedReader br = new BufferedReader(fileReader);
			ArrayList<String> old=new ArrayList<String>();
			while ((currentLine = br.readLine()) != null) 
			{
				String[]line=currentLine.split(",");
				if(line[0].equals(this.name)&&line[1].substring(1).equals(key)&&line[4].equals("true")){
					strings.add(key);
				}

			}
		}
		if(strings.size()==0){
			return PageSelect(htblColNameValue,strOperator).iterator();
		}
		else{

			return Treeselect(htblColNameValue,strOperator,strings);
		}
	}











	public ArrayList<Tuple> PageSelect(Hashtable<String, Object> htblColNameValue, String strOperator) throws DBAppException, IOException, ClassNotFoundException {

		ArrayList<Tuple> output = new ArrayList<Tuple>();
		boolean conditionsValid;
		boolean flag=false;
		if(strOperator.equals("AND")||strOperator.equals("OR"))
		{
			ArrayList<Tuple> temp=null;
			for(int i=1;i<=pageNumbers;i++) //loop for pages
			{	
				FileInputStream xs = new FileInputStream(name+i+".class");
				ObjectInputStream xa = new ObjectInputStream(xs);
				temp = (ArrayList<Tuple>) xa.readObject();



				for(int j=0;j<temp.size();j++)  //loop for lines in the pages
				{

					if(temp.get(j)!=null)
					{
						if(strOperator.equals("AND"))
						{

							conditionsValid=true;
							for(String key: htblColNameValue.keySet()){//loop for conditions.
								if(!temp.get(j).row.get(key).equals(htblColNameValue.get(key))){
									conditionsValid=false;
									break;
								}
							}	
							if(conditionsValid)
							{

								output.add(temp.get(j));
							}
						}
						else if(strOperator.equals("OR"))
						{
							conditionsValid=false;
							for(String key: htblColNameValue.keySet()){  //loop for conditions.
								if(temp.get(j).row.get(key).equals(htblColNameValue.get(key))){
									conditionsValid=true;
									break;
								}
							}	
							if(conditionsValid)
							{
								output.add(temp.get(j));
							}
						}



					}
				}
			}

		}


		else
		{
			throw new DBAppException("Invalid Operator");

		}

		return output;
	}
	public void createIndex(String strColName) throws ClassNotFoundException, IOException {
		BPlusTree index = new BPlusTree(20);

		FileReader fileReader= new FileReader("metadata.csv");
		BufferedReader br = new BufferedReader(fileReader);
		ArrayList<String> old=new ArrayList<String>();
		String currentLine;
		while ((currentLine = br.readLine()) != null) 
		{
			String[] line = currentLine.split(",");
			if(line[0].equals(this.name)&&line[1].substring(1).equals(strColName))
			{

				line[4]="true";
				currentLine=line[0]+","+line[1]+","+line[2]+","+line[3]+","+line[4]+","+line[5]+'\n';


			}
			old.add(currentLine);
		}	

		DBApp.csv=new FileWriter("metadata.csv");
		for(int i=0;i<old.size();i++)
			DBApp.csv.write(old.get(i)+'\n');
		DBApp.csv.flush();

		for(int i=1;i<=pageNumbers;i++) //loop for pages
		{
			FileInputStream xs = new FileInputStream(name+i+".class");
			ObjectInputStream xa = new ObjectInputStream(xs);
			ArrayList<Tuple> temp = (ArrayList<Tuple>) xa.readObject();



			for(int j=0;j<temp.size();j++)  //loop for lines in the pages
			{
				index.insertBPlusTree((Integer)temp.get(j).getRow().get(strColName));	
				LeafNode leaf = index.searchBPlusTree((Integer)temp.get(j).getRow().get(strColName),true);

				System.out.println(leaf.getDataPointer()+"   jjjjjftjkkkkkkkkkkkkkkkkkkkkkkk   ");
				//	leaf.getDataPointer().add(leaf.getValues().indexOf((Integer)temp.get(j).getRow().get(strColName)),);
				leaf.getDataPointer().add(leaf.getValues().indexOf((Integer)temp.get(j).getRow().get(strColName)),new Triplet(i,j));

			}

		}
		if(!strColName.equals(primaryKey)){


			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			secInd.put(strColName,index);
		}
		else
		{
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			priInd=index;
		}


	}


}
