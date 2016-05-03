package WreckingBall;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class Tuple implements Serializable{

	Hashtable<String,Object> row;

	public void setRow(Hashtable<String,Object> row) {
		this.row = row;
	}

	public Hashtable<String,Object> getRow() {
		return row;
	}

	public String toString()
	{
		String tup="";
		for(String key: row.keySet())
		{
			tup+=this.row.get(key)+", ";
		}
	//	tup+='\n';
		return tup;
	}
	
	
	public Tuple(Hashtable<String,Object> row)
	{
		this.row=row;
	}

}
