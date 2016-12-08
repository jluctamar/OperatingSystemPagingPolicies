package project4;

	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.IOException;
	import java.io.InputStreamReader;
import java.util.Random;

public class OperatingSystem {
	
		
		Page[] disk;
		Page[] physicalMemory;   //subset of disk ---> will be a fraction of the size of disk
		int[] accessMap;
		int accessCounter;
		int replaceMethod;       //0 == LRU         1 == Random
		int miss;                //records number of page misses
		int hit;                 //records number of page hits 
		int[] pageTable;
		int numPages;
		int numBytes;
		
	//constructor
	public OperatingSystem(int method, String fileName)
		{
			replaceMethod = method;
			miss = 0;
			hit = 0;
			//read in  the information from the file into a mutable data structure 
			File file = new File(fileName);
			String firstLine;
			String[] lineOne = new String[2];
			
			
			
			//create an input stream and buffered reader to read in the data within the file
			FileInputStream inputStream;
			try {
				inputStream = new FileInputStream(file);
			
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				//extract information from the first line into relevant variables
				firstLine = reader.readLine();
				lineOne = firstLine.split(" ");
				numPages = Integer.parseInt(lineOne[0]);
				numBytes = Integer.parseInt(lineOne[1]);
				
				
				
				//extract information relevant to page tables 
				 // note the proceeding line (lines 2 -> numPages) of the file contain the information that will populate 
					//page table array
				pageTable = new int[numPages];
				disk = new Page[numPages];
				physicalMemory = new Page[(numPages)/100];         //original size
				//physicalMemory = new Page[10];                     //test purposes 
				accessMap = new int[physicalMemory.length];   
				accessCounter = -1;
				
				
				
				String nextLine; // temp variable for the loop(serves the same purpose as firstLine)
			
				
				for(int i = 0; i < numPages ; i++)
				{
					
						// temp variables
						nextLine = reader.readLine();
						String[] temp = new String[2];
						
						//populating the page table
						temp = nextLine.split("->");
						pageTable[i] = Integer.parseInt(temp[1]);
				}
				
				for(int p =0 ; p< numPages; p++)
				{
						// next line is read
						nextLine = reader.readLine(); 
						
						disk[p] = new Page(p,numBytes);              // initializing a new page within "disk"
						disk[p].pageData = nextLine.getBytes();		//assign byte data to its proper location	
						
						if(p<physicalMemory.length)
						{
							physicalMemory[p] = new Page(p,numBytes);//initialize physical memory without including the byte data
						}
								
			
				}
				
				inputStream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}	
					
		}
		
	//returns the physical page number that corresponds with a given virtual page number
	public int getPPN(int vpn)
		{		
			return pageTable[vpn];
		}

	//returns the page instance that corresponds with a given Physical page number (location in disk)
	public Page getPage(int ppn)
	{	
		accessCounter++;
		boolean flag = findPage(ppn, accessCounter);
		
		if(flag)//if the page is found in physical memory return that page(AKA HIT)
		{	
			hit++;
			return disk[ppn];                         //TODO: have the page return from physical memory structure						
		}
		else//AKA MISS
		{
			
			miss++;
			
			//"retrieve" from disk, place in main memory	
			Page temp = disk[ppn];                           
			//*******TODO: simulate delay************************************************************
			
			//place/replace in physical memory according to replacement policy
			if(replaceMethod == 0)
			{
				LRUreplace(temp, accessCounter);
			}
			else if(replaceMethod == 1)
			{
				randomReplace(temp, accessCounter);
			}
			return temp;
		}		
	}
	
	
	//searches physical memory for a page
	public boolean findPage(int ppn, int accessCount)
	{		
		//searches physical memory until desired page is found
		for(int i = 0; i<physicalMemory.length; i++)
		{
			
			if(physicalMemory[i].num == ppn)
			{
				accessMap[i] = accessCount;           //update the access map to account for memory access
				//System.out.println("Page found in Physical Memory");
				return true;
			}
		}
		
		//otherwise page is not in physical memory
		//System.out.println("Page not found in main, must retrieve from disk. One moment...");
		return false;
	}
	
	//replacement mechanism (LRU)
	public void LRUreplace(Page page,int accessCount)
	{
		int lowestCount = accessCount;
		int arrayLocation = 0;                                //links accessMap and physicalMemory tables 
		
		//first fill physical memory slots
		if(accessCount<accessMap.length)
		{
			accessMap[accessCount] = accessCount;	             //update the access map with new value
			physicalMemory[accessCount] = page;                 //replace the least recently used page with the newest reference
		}
		else
		{
			//then, once full, replace corresponding page from Physical memory with new one 
			for(int i = 0; i<physicalMemory.length; i++)
			{
				if(accessMap[i]< lowestCount)
				{
					lowestCount = accessMap[i];						//updates the lowest access number
					arrayLocation = i;                             //keeps record of the offset within the array the lowest number is held
				}	
			}

			accessMap[arrayLocation] = accessCount;	              //update the access map with new value
			physicalMemory[arrayLocation] = page;                 //replace the least recently used page with the newest reference
		}
		
	}
	//replacement mechanism (Random)
	public void randomReplace(Page page, int accessCount)
	{
		//generate a random number representing page to replace
		Random num = new Random();
		int randNum = num.nextInt(physicalMemory.length);
		
		accessMap[randNum] = accessCount;
		physicalMemory[randNum]= page;
	}
	
	//simulation of memory accesses
	public void runSimulation(int numAccess)
	{
		for(int i = 0; i<numAccess; i++)
		{
			Random num = new Random();
			int randNum = num.nextInt(physicalMemory.length *2);                     //ranges the entire list of possible pages found in disk
			Page test = this.getPage(randNum);
			
			/*displays access map and pages in physical memory as well as hits and misses 
			for(int l = 0; l<this.accessMap.length; l++)
			{
				System.out.print(this.accessMap[l]+" " );
				
			}
			System.out.println();
			for(int j = 0; j<this.physicalMemory.length; j++)
			{
				System.out.print(this.physicalMemory[j].num +" ");	
			}
			System.out.println("--------"+"\n" +" Page:"+ randNum +"\n"+" Hit: " +this.hit+ "\n" + " Miss:" + this.miss);
		*/
		}
		
		if(this.replaceMethod == 0)
		{
			System.out.print("Least Recently Used | ");
		}
		else
		{
			System.out.print("Random | ");
		}
		System.out.println("Final Results \n--------------------------");
		System.out.println("Hit percentage: " + ((double)this.hit/numAccess));
		System.out.println("Miss percentage: " +((double)this.miss/numAccess));
		//100nanoseconds = .0001ms 
		System.out.println("Physical Access Time(100ns/Access): " + ((this.hit)*.0001) + " milliseconds" ); 
		System.out.println("Disk Access Time(10ms/Access): " +((this.miss)*10)+ " milliseconds");
		
		//AMAT = (percent of hits * physical mem access time) + (percent of miss * disk access time)
		System.out.println("Average Memory Access Time : "+
				((((double)this.hit/numAccess)*this.hit*.0001) + (((double)this.miss/numAccess)*(this.miss *10)))+" milliseconds \n\n\n");
	
		
	}
	
	// returns byte data stored at the given virtual address
	public byte getDataAtVirtAddress(int virtAddress) throws NumberFormatException, Exception
		{
			//extract bit information from given int
			String binaryInt = Integer.toBinaryString(virtAddress);
			
			
			//divide the integer into its offset and virtual page number 
				//note: offset will be determined by the number of bytes (numBytes)
					//16 = 2^4 (4 bits offset)// 32 = 2^5 (5 bits offset) // bits for offset = log base 2 of (numBytes)
					//bits of offset will be the size of bit array representing the offset
			int VPNbits = (int)(Math.log(numPages)/ Math.log(2));		
				//note: the virtual page number will be determined by numPages
					//the number of bits used must be enough to represent the all pages 
					//bits for VPN = log base 2 (numPages)
					/*log identity: logb(x) = log (x) / log(base)*/
			int offsetBits= (int)(Math.log(numBytes)/ Math.log(2));
			int addressSpace = VPNbits+offsetBits;
			
			
			//create a string that is the same size as address space
				//adding zeros to the binary int should not changes its value
			while(addressSpace != binaryInt.length())
			{
				binaryInt = "0"+ binaryInt;
			}
			
			
			String VPN = binaryInt.substring(0, VPNbits);							//bits representing virtual page number
			String offset = binaryInt.substring(VPNbits, binaryInt.length());		//bits representing offset
			
			//retrieve Physical page number
			int PPN = getPPN(Integer.parseInt(VPN, 2));
			
			//retrieve proper page
			Page page = getPage(PPN);
			
			// returns byte data stored at the given virtual address
			return page.getData(Integer.parseInt(offset, 2));

		}
	
	
	public static void main(String[] args) throws NumberFormatException, Exception {
			
		OperatingSystem osLRU = new OperatingSystem(0,"C:\\Users\\Luctamar\\workspace\\CMP 426 Operating Systems\\src\\pagingProject\\bigTest.txt");
		OperatingSystem osRandom = new OperatingSystem(1,"C:\\Users\\Luctamar\\workspace\\CMP 426 Operating Systems\\src\\pagingProject\\bigTest.txt");
		
		osRandom.runSimulation(100);
		osLRU.runSimulation(100);


	}


}
