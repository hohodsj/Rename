import java.util.*;
import java.io.*;

/* This program is used when users need to organize their files, 
 * There are 4 options in this program
 * 1. save keywords that the user wants
 * 2. remove keywords that user does not want
 * 3. add keywords in front of the existing name
 * 4. append keywords at the end of the existing name
 */

public class rename
{
    public static void main (String[] args)
    {
        int choice = askForOptions();
        String processWord = askForWords(choice);
        String path = askForPath();
        renameProcess(choice, processWord, path);
        writePrevious(processWord,path);
        quit();
    }


    public static int askForOptions()
    {
        System.out.println("To save keyword(s):         1");
        System.out.println("To remove keyword(s):       2");
        System.out.println("To add words in the front:  3");
        System.out.println("To append words in the end: 4");
        System.out.println("To exit the program:        0");
        Scanner input = new Scanner(System.in);
        int options = input.nextInt();
        while(options < 0 || options > 4)
        {
            System.out.println("Invalid Input, Please Select following options from 0-4");
            System.out.println("To save keyword(s):         1");
            System.out.println("To remove keyword(s):       2");
            System.out.println("To add words in the front:  3");
            System.out.println("To append words in the end: 4");
            System.out.println("To exit the program:        0");
            input = new Scanner(System.in);
            options = input.nextInt();
        }
        return options;
    }
    
    public static String askForWords(int choice)
    {
        switch(choice) {
        case 1 :
            System.out.print("Please enter the word(s) to save (separate by space). ");
            break;
        case 2 :
            System.out.print("Please enter the words to remove (separate by space). ");
            break;
        case 3 :
            System.out.print("Please enter the words you are trying to put in front. ");
            break;
        case 4 :
            System.out.print("Please enter the words you are trying to put at the end. ");
            break;
        case 0:
            quit();
            break;
        default :
            System.err.println("Error in the program in askForWords(), choice" + choice);
            System.exit(1);
                
        }
        System.out.println("If quit type /quit");
        Scanner stringInput = new Scanner(System.in);
        String words = stringInput.nextLine();
        //Quit option
        if(words.equals("/quit")) quit();
        
        
        if(words.equals("/pre"))
        {
            try{
                FileReader inputFile = new FileReader("pre.txt");
                BufferedReader bufferReader = new BufferedReader(inputFile);
                String line;
                if((line = bufferReader.readLine()) != null)
                    words = line;
                else
                {
                    while(words.equals("/pre"))
                    {
                        System.out.println("CANNOT read from pre.txt. Please enter words manually.");
                        stringInput = new Scanner(System.in);
                        words = stringInput.nextLine();
                    }
                }
                bufferReader.close();
            }catch(Exception e){
                System.out.println("Error while reading file: "+e.getMessage());
                while(words.equals("/pre"))
                {
                    System.out.println("CANNOT read from pre.txt. Please enter words manually.");
                    stringInput = new Scanner(System.in);
                    words = stringInput.nextLine();
                }
            }
        }
        
        //Add 0-9 as filter, use /num
        if(words.contains("/num"))
        {
            //System.out.println("Replace all numbers");
            if(words.contains(" /num"))
                words = words.replace(" /num","");
            else
                words = words.replace("/num","");
            words.replaceAll("[^0-9]+","");
            words += " 0 1 2 3 4 5 6 7 8 9";
            System.out.println("Without 0-9: "+words);
        }
        return words;
    }
    
    public static String askForPath()
    {
        System.out.println("Enter path name (eg./Users/username/Desktop/FOLDER or drag FOLDER)");
        System.out.print("Path:");
        Scanner input = new Scanner(System.in);
        String pathName = input.nextLine();
        
        //Quit option
        if(pathName.equals("/quit")) quit();
        
        //Retrive information from last time
        if(pathName.equals("/pre"))
        {
            try{
                FileReader inputFile = new FileReader("pre.txt");
                BufferedReader bufferReader = new BufferedReader(inputFile);
                bufferReader.readLine();
                String line;
                if((line = bufferReader.readLine()) != null)
                    pathName = line;
                else
                {
                    while(pathName.equals("/pre"))
                    {
                        System.out.println("CANNOT read from pre.txt. Please enter path manually.");
                        input = new Scanner(System.in);
                        pathName = input.next();
                    }
                }
                bufferReader.close();
            }catch(Exception e){
                System.out.println("Error while reading file: "+e.getMessage());
                while(pathName.equals("/pre"))
                {
                    System.out.println("CANNOT read from pre.txt. Please enter path manually.");
                    input = new Scanner(System.in);
                    pathName = input.next();
                }
            }
        }
        
        //for MAC with a filename contains space
        if(pathName.contains("\\ "))
            pathName = pathName.replace("\\ "," ");
        if(pathName.contains("\""))
            pathName = pathName.replace("\"","");
        
        //check if new pathName is a valid directory
        File f = new File(pathName);
        if(!f.isDirectory())
        {
            pathName = pathName.substring(0,pathName.length()-1);
            f = new File(pathName);
            if(!f.isDirectory())
            {
                System.out.println("The path you entered is invalid, please check");
                quit();
            }
        }
        System.out.println(); //format
        return pathName;
    }
    
    public static void quit()
    {
        System.out.println("Exiting the program");
        System.exit(0);
    }
    
    public static void renameProcess(int choice, String processWord, String path)
    {
        //step 1 go to the folder and print all files
        File folder = new File(path);
        File[] filelist = folder.listFiles();
        
        //step 2 separate processWord by space for choice 1 and 2 and put them in array
        String[] elements = {};
        Comparator<String> comparator = new StringLengthComparator();
        PriorityQueue<String> pq = new PriorityQueue<String>(10, comparator);
        if(choice == 1 || choice == 2)
        {
            //separate words by spaces
            elements = processWord.split(" ");
            
            for(int i = 0; i < elements.length; i++)
            {
                elements[i] = elements[i].replace('+', ' ');
                pq.add(elements[i]);
            }
        }
        
        //transfer everything in pq back to array with pq order
        for(int i = 0; i<elements.length; i++)
            elements[i] = pq.remove();
        
        //step 3: Create a hashtable and check for existing names.'
        HashSet<String> hashForFileNames = new HashSet<String>();
        for(File file:filelist)
            if(file.isFile() && !file.isHidden())
                hashForFileNames.add(file.getName());
        //check hashForFileNames Testing purpose
        
        //step3 + step4 + step5 rename iterate entired folder, find last period of a file
        for(File file: filelist) {
            if (file.isFile() && !file.isHidden()) {
                char[] filename = file.getName().toCharArray();
                int lastPeriod = findLastPeriod(filename);
                //System.out.println(file.getName() + " last period is at " +lastPeriod);
            
                String newName = "";
                //Note: differences between elements and processWord is element is
                //string array separate by space, processWord is a string.
                if(choice == 1) newName = save(elements, file.getName(), lastPeriod);
                if(choice == 2) newName = remove(elements,file.getName(), lastPeriod);
                if(choice == 3) newName = front(processWord,file.getName(), lastPeriod);
                if(choice == 4) newName = end(processWord, file.getName(), lastPeriod);
                
                //Put back new file extension
                for(int i=lastPeriod; i<filename.length; i++)
                    //newName+=filename[i].toLowerCase();
                    newName+=Character.toLowerCase(filename[i]);
                System.out.println("The new name is "+newName);
                
                
                //Start renaming the file
                // 1 check if newname is same as old name
                if(newName.equals(filename))
                    continue;
                // 2 check if hash contain the newname if so notice user it is failed
                // else rename the file
                else
                {
                    if(hashForFileNames.contains(newName))
                    {
                        System.out.println(newName+" already exist. " + file.getName() + " FAIL TO RENAME!!!\n");
                        continue;
                    }
                    
                    else
                    {
                        hashForFileNames.remove(file.getName());
                        hashForFileNames.add(newName);
                        //rename process
                        File filetemp = new File(path + "/" + newName);
                        if(file.renameTo(filetemp))
                            System.out.println(file.getName() + " successfully renamed to " + newName+ "\n");
                        else
                        {
                            System.out.println("Failed for some reasons");
                            System.out.println("You might be using some reserved characters ");
                            System.out.print("Such as * % $ Please check your system.");
                            System.out.println("For further information, please visit:");
                            System.out.print("http://stackoverflow.com/questions/1325388/how-to-find-out-why-renameto-failed \n");
                        }
                    }
                }
            }
        }

    }
    
    //Finds the last period of a file if file does not have a type returns the end
    public static int findLastPeriod(char[] filename)
    {
        int i=filename.length-1;
        for(;i>0;i--)
        {
            if(filename[i] == '.')
                break;
        }
        //if file has no extendsion return the end of the file
        if(i == 0)
        {
            System.out.println("Warning: This file has no type.");
            return filename.length;
            
        }
        return i;
        
    }
    
    public static class StringLengthComparator implements Comparator<String>
    {
        @Override
        public int compare(String x, String y)
        {
            if(x.length() > y.length())
            {
                return -1;
            }
            if(x.length() < y.length())
            {
                return 1;
            }
            return 0;
        }
    }
    
    public static String save(String[] elements, String filename, int lastPeriod)
    {
        String newName = "";
        //create a char array to indicate if certain character is been replace
        char[] newNameChar = filename.toCharArray();
        //nested loop iterate amounts existing one element then go to the next one
        //then sort them by index
        TreeMap<Integer,String> tm = new TreeMap<Integer,String>();
        int position = -1;
        for(int i=0; i < elements.length; i++)
        {
            //System.out.println("Inside First for loop");
            for(int index = 0; index < filename.length(); )
            {
                position = filename.indexOf(elements[i], index);
                
                if( position != -1)
                {
                    tm.put( position,elements[i]);
                    //once the word is in the tree map we replace the word with
                    //some ascii for this program we use ascii 206
                    for(int j = position; j < position+elements[i].length(); j++)
                        newNameChar[j] = 206;
                    filename = String.valueOf(newNameChar);
                }
                else break;
            }
        }
        
        for(Map.Entry<Integer,String> entry : tm.entrySet()) {
            //Integer key = entry.getKey(); //key = index
            String value = entry.getValue(); // value = filename
            newName+=value;
        }
        //if empty return original string
        if(newName.equals(""))
        {
            for(int i=0; i < lastPeriod; i++)
                newName+=filename.charAt(i);
            System.out.println(newName+" CANNOT renamed: empty filename.");
        }
        return newName;
    }
    
    public static String remove(String[] elements, String filename, int lastPeriod)
    {
        String newName = "";
        //copy filename without file extendsion
        for(int i=0;i<lastPeriod;i++)
            newName+=filename.charAt(i);
        
        //replace unwanted strings with empty character
        for(int i=0; i<elements.length;i++)
            newName = newName.replace(elements[i],"");
        
        //System.out.println("newName is "+newName);
        
        if(newName.equals(""))
        {
            System.out.println("Runing if");
            for(int i=0; i < lastPeriod; i++)
                newName+=filename.charAt(i);
        }
        return newName;
    }
    
    public static String front(String processWord, String filename, int lastPeriod)
    {
        int totalchar = processWord.length() + lastPeriod;
        char[] newchar = new char[totalchar];
        totalchar--;
        
        for(int i = lastPeriod-1; i>=0; i--)
        {
            newchar[totalchar] = filename.charAt(i);
            totalchar--;
        }
        
        for(int i=processWord.length()-1; i>=0; i--)
        {
            newchar[totalchar] = processWord.charAt(i);
            totalchar--;
        }
        String newName = new String(newchar);
        return newName;
    }
    
    public static String end(String processWord, String filename, int lastPeriod)
    {
        int totalchar = processWord.length() + lastPeriod;
        char[] newchar = new char[totalchar];
        totalchar--;
        
        for(int i=processWord.length()-1; i>=0; i--)
        {
            newchar[totalchar] = processWord.charAt(i);
            totalchar--;
        }
        for(int i=lastPeriod-1; i >= 0; i--)
        {
            newchar[totalchar] = filename.charAt(i);
            totalchar--;
        }
        String newName = new String(newchar);
        return newName;
    }
    
    public static void writePrevious(String processWord, String path)
    {
        BufferedWriter output = null;
        try {
            File file = new File("pre.txt");
            output = new BufferedWriter(new FileWriter(file));
            output.write(processWord+"\n"+path);
        } catch( IOException e) {
            e.printStackTrace();
        } finally {
            if( output != null ) {
                try{
                    System.out.println("Done writing /pre information");
                    output.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}