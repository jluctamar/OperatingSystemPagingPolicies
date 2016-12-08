package project4;

public class Page {
	
	int    num = 0;                     //page number
	byte[] pageData;
	
	public Page(int number , int arraySize)
	{	
		this.num = number;
		this.pageData = new byte[arraySize];

	}
	
	//returns byte data within pageData at the given offset 
	public byte getData(int offset) throws Exception
	{
		if((offset<= -1) || (offset> pageData.length))
		{
			throw new Exception("invalid offset");
		}
		else
		{
			return pageData[offset];	
		}
		
	}
	

}
