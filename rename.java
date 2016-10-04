import java.io.*;
import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.*;

public class rename
{
	public static void main (String[] args)
	{
		System.out.println("save:  0");
		System.out.println("remove:1");
		System.out.println("front: 2");
		System.out.println("append:3");
		Scanner input = new Scanner(System.in);
		int choice = input.nextInt();
		//System.out.println("input is " + choice);
		if(choice < 0 ||choice >3)
		{
			System.out.println("invalid input");
			System.exit(1);
		}
		if(choice == 0) System.out.println("word to save?");
		if(choice == 1) System.out.println("words to remove?");
		if(choice == 2) System.out.println("words to put infront?");
		if(choice == 3) System.out.println("words to put after?");
		//can add file option here
		input = new Scanner(System.in);
		String process = input.next();
		char[] processChar = process.toCharArray();
		//System.out.println("Word to process is "+process);
        
        HashSet<Character> hsetProcess = new HashSet<Character>();//save option put char in hash
        if(choice == 0||choice == 1)
        {
          for(int i=0; i<processChar.length;i++)
          {
              if(!process.equals("/null"))
                  hsetProcess.add(processChar[i]);
          }
        }
        System.out.println("hset "+hsetProcess);
        String sequence = "";
        if(choice == 1)
        {
            System.out.println("Sequence removal: ");
            sequence = input.next();
        }
    //System.out.println("processChar[0]: "+processChar[0]+" length: "+processChar.length);
        
        
        
		System.out.print("Path:");
		input = new Scanner(System.in);
		String infile = input.next();
		//System.out.println("Path is "+infile);
		File folder = new File(infile);
		File[] filelist = folder.listFiles();
        //put all file names in a hash table
        HashSet<String> hfile = new HashSet<String>();
        System.out.println("File length: "+filelist.length);
        for(File file:filelist)
            if(file.isFile() && !file.isHidden())
                hfile.add(file.getName());
        
		for(File file: filelist)
		{
			if(file.isFile() && !file.isHidden())
			{
				char[] filename = file.getName().toCharArray();
				//System.out.println(filename);
				//find last position of '.'
			    int lastPeriod = findLastPeriod(filename);
                
                //test
                //System.out.println("index: "+file.getName().indexOf(sequence));

                String newName = "";
				if(choice == 0) newName = save(hsetProcess,filename,lastPeriod);
                if(choice == 1) newName = remove(hsetProcess,filename,lastPeriod,sequence);
                if(choice == 2) newName = front(processChar, filename, lastPeriod);
                if(choice == 3) newName = end(processChar, filename, lastPeriod);
                if(newName.length() == 0)
                {
                    System.out.println(file.getName()+"cannot be renamed. Rename length = 0.");
                    continue;
                }
                for(int i=lastPeriod; i<filename.length; i++)
                    newName+=filename[i];
                
                //System.out.println("newName: "+ newName);
                //System.out.println(infile+"/"+newName);
                if(newName.equals(filename))
                    continue;
                else
                {
                    if(hfile.contains(newName))
                    {
                        System.out.println(newName+" already exist. FAIL TO RENAME!!!!.");
                        continue;
                    }
                    else
                    {
                        hfile.remove(file.getName());
                        hfile.add(newName);
                        //rename process
                        File filetemp = new File(infile+"/"+newName);
                        if(file.renameTo(filetemp))
                            System.out.println(file.getName()+" successfully renamed to "+newName);
                        else
                            System.out.println("Failed for some mystery reasons");
                    }
                }
			}
		}
        
    }
    
    
    public static HashSet hashfile(File[] filelist)
    {
        HashSet<String> hash = new HashSet<String>();
        for(File file: filelist)
            hash.add(file.getName());
        return hash;
    }
    
	public static int findLastPeriod(char[] filename)
	{
		int i=filename.length-1;
		for(;i>0;i--)
		{
			if(filename[i] == '.')
				break;
		}
		return i;
	}
    
	public static String save(HashSet<Character> hset, char[] filename,int lastPeriod)
    {
       List<Character> newChar = new ArrayList<Character>();
       for(int i=0; i<lastPeriod;i++)
       {
           //System.out.println(filename[i]);
           if(hset.contains(filename[i]))
               newChar.add(filename[i]);
       }
       //newChar.add('\0');
       
       char[] newCharArray = new char[newChar.size()];
       for(int i=0; i<newChar.size();i++)
       {
           newCharArray[i] = newChar.get(i);
       }
       String name = new String(newCharArray);
       return name;
   }
    
    public static String remove(HashSet<Character> hset, char[] filename, int lastPeriod, String sequence)
    {
        
        int index = new String(filename).indexOf(sequence);
        int rmsequencetot = sequence.equals("/null")? 0 : lastPeriod - sequence.length();
        //System.out.println("rmsequencetot: " + rmsequencetot);
        char[] rmsequence = new char[rmsequencetot];
        rmsequencetot--;
        //System.out.println("index: "+index + " sequence: "+sequence+" rmsqtot: "+rmsequencetot);
        //System.out.println("index+sequence.length(): " + index+sequence.length());
        
        String output1 = new String(rmsequence);
        for(int i=lastPeriod-1; i>=0; i--)
        {
            if(i < index || i >= index+sequence.length())
            {
                if(!sequence.equals("/null") && rmsequencetot>=0)
                {
                  rmsequence[rmsequencetot] = filename[i];
                  rmsequencetot--;
                  
                }
                //output1 = new String(rmsequence);
                //System.out.println("output1: "+output1);
            }
        }
        output1 = new String(rmsequence);
        //System.out.println("rmsequence: "+output1+ " rmseeqtot: "+rmsequencetot);
        
        int actualLength = (sequence.equals("/null")? lastPeriod:rmsequence.length);
        
        //System.out.println("actualyLength: "+actualLength+" rmsequence.length: "+rmsequence.length);
        //System.out.println("lastPeriod: "+lastPeriod);
        List<Character> newChar = new ArrayList<Character>();
        for(int i=0; i<actualLength; i++)
        {
            if(!hset.contains(sequence.equals("/null")? filename[i] : rmsequence[i]) )
                newChar.add(sequence.equals("/null")? filename[i] : rmsequence[i]);
        }
        
        char[] newCharArray = new char[newChar.size()];
        for(int i=0; i<newChar.size(); i++)
        {
            newCharArray[i] = newChar.get(i);
        }
        String output = new String(newCharArray);
        return output;
    }
    
    public static String front(char[] processChar, char[] filename, int lastPeriod)
    {
        int totalchar = processChar.length+lastPeriod;
        //System.out.println("new total: "+ totalchar);
        char [] newchar = new char[totalchar];
        totalchar--; //adjust position: 1 less than the totalchar
        
        for(int i = lastPeriod-1; i>=0; i--)
        {
            //System.out.println("totalchar: "+totalchar+" i: "+i);
            newchar[totalchar] = filename[i];
            totalchar--;
        }
        
        //System.out.println(newchar);
        
        for(int i=processChar.length-1; i>=0; i--)
        {
            newchar[totalchar] = processChar[i];
            totalchar--;
        }
        
        String output = new String(newchar);
        return output;
    }
    
    public static String end(char[] processChar, char[] filename, int lastPeriod)
    {
        int totalchar = lastPeriod+processChar.length;
        char[] newchar = new char[totalchar];
        totalchar--; //adjust position
        for(int i=processChar.length-1; i>=0; i--)
        {
            newchar[totalchar] = processChar[i];
            totalchar--;
        }
        for(int i=lastPeriod-1; i>=0; i--)
        {
            newchar[totalchar] = filename[i];
            totalchar--;
        }
        String output = new String(newchar);
        return output;
    }
    
    
    
}











