package WreckingBall;

import java.io.Serializable;

public class Triplet implements Serializable{
	Integer pageNumber;
	Integer positon;
	boolean deleted;
	public Triplet(Integer x,Integer y)
	{
		this.pageNumber=x;
		this.positon=y;
		deleted=false;
	}
	
public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPositon() {
		return positon;
	}

	public void setPositon(Integer positon) {
		this.positon = positon;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

public String toString()
{
	return "( "+pageNumber+", "+positon+" )";
}
}
