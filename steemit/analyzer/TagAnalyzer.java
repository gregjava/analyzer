package steemit.analyzer;

/**
 *
 * @author GregJava 20/07/18
 */
import java.awt.EventQueue;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.SYNC;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TagDataExtractor { 
    public static TagDataExtractor analyzer;

    TagDataExtractor(){ String fileData = null;
        if (new File("input.saf").exists()) {
            try { fileData = readFile(Paths.get(new File("input.saf").getPath()), "UTF-8"); 
                System.out.println("Searching for Steemit Analyzer data..."); 
            } catch (Exception ex) { System.out.println("Error!\n"+ex.getMessage()); }
            if (fileData!=null) if (!fileData.isEmpty()) { 
                ArrayList<String> tag = new ArrayList<>();
                ArrayList<String> post = new ArrayList<>();
                ArrayList<String> comment = new ArrayList<>();
                ArrayList<String> payout = new ArrayList<>();
                ArrayList<String> postValue = new ArrayList<>();
                ArrayList<String> postPopularity = new ArrayList<>(); 
                for (int i=0; i<fileData.length(); i++){ 
                    String thisTag="", thisPost="", thisComment="", thisPayout="";
                    do { while (fileData.charAt(i)!='\t'&&fileData.charAt(i)!=' ') {
                            thisTag+=fileData.charAt(i); i++; if (i>=fileData.length()) break;
                        } tag.add(thisTag); if (i>=fileData.length()) break;
                        while(!charIsNum(fileData.charAt(i))) i++;
                        while (fileData.charAt(i)!='\t'&&fileData.charAt(i)!=' ') {
                            thisPost+=fileData.charAt(i); i++; if (i>=fileData.length()) break;
                        } post.add(thisPost); if (i>=fileData.length()) break;
                        while(!charIsNum(fileData.charAt(i))) i++;
                        while (fileData.charAt(i)!='\t'&&fileData.charAt(i)!=' ') {
                            thisComment+=fileData.charAt(i); i++; if (i>=fileData.length()) break;
                        } comment.add(thisComment); if (i>=fileData.length()) break;
                        while(!charIsNum(fileData.charAt(i))) i++;
                        while (fileData.charAt(i)!='\t'&&fileData.charAt(i)!=' ') {
                            thisPayout+=fileData.charAt(i); i++; if (i>=fileData.length()) break;
                        } payout.add(thisPayout); if (i>=fileData.length()) break;
                        while (fileData.charAt(i)!='\n' && i<fileData.length()) { i++; }
                    } while (fileData.charAt(i)!='\n' && i<fileData.length());
                } System.out.println("Commencing computations"); String temp, temp1, temp2;
                for (int i=0; i<payout.size(); i++) { temp = ""; temp1 = ""; temp2 = "";
                    for (int j=0; j<payout.get(i).length(); j++) if (payout.get(i).charAt(j)!=',') temp+=payout.get(i).charAt(j);
                    for (int j=0; j<post.get(i).length(); j++) if (post.get(i).charAt(j)!=',') temp1+=post.get(i).charAt(j);
                    for (int j=0; j<comment.get(i).length(); j++) if (comment.get(i).charAt(j)!=',') temp2+=comment.get(i).charAt(j);
                    postValue.add(i, ""+(Double.parseDouble(temp)/Integer.parseInt(temp1)));
                    postPopularity.add(i, ""+(Double.parseDouble(temp2)/Integer.parseInt(temp1)));
                } temp2 = ("Outputing results:\n\n\nTag\t\t\t\t\t"+"Average Payout per Post\tPayout Ranking\t"+"Average Comments per Post\tComment Ranking\tFinal Ratings\n");
                ArrayList<Double> valueOfPostValue = new ArrayList<>(), valueOfPostComments = new ArrayList<>();
                for (int i=0; i<postValue.size(); i++) valueOfPostValue.add(Double.parseDouble(postValue.get(i).trim()));
                for (int i=0; i<postPopularity.size(); i++) valueOfPostComments.add(Double.parseDouble(postPopularity.get(i).trim()));
                Object[] valueRankingArr = valueOfPostValue.toArray(), popularityRankingArr = valueOfPostComments.toArray();
                Arrays.sort(valueRankingArr); Arrays.sort(popularityRankingArr);
                ArrayList<Integer> tagValueRanking = new ArrayList<>(), tagPopularityRanking = new ArrayList<>(), tagRating = new ArrayList<>();
                for (int i=0; i<postValue.size(); i++) { tagValueRanking.add(0); tagPopularityRanking.add(0); tagRating.add(0); }
                for (int i=0; i<postValue.size(); i++) {
                    formatArrayListChars(tag, i, 24); formatArrayListChars(postValue, i, 24); formatArrayListChars(postPopularity, i, 24); 
                    for (int j=0; j<popularityRankingArr.length; j++) if (valueOfPostComments.get(i)==(double)popularityRankingArr[j]) tagPopularityRanking.set(i,j);
                    for (int j=0; j<valueRankingArr.length; j++) if (valueOfPostValue.get(i)==(double)valueRankingArr[j]){ tagValueRanking.set(i, j); }
                    if (i!=0) temp2+=(tag.get(i)+"\t"+postValue.get(i)+"\t"+tagValueRanking.get(i)+"\t\t"+postPopularity.get(i)+"\t\t"+tagPopularityRanking.get(i)+"\t\t\t"+(tagRating.set(i,tagPopularityRanking.get(i)+tagValueRanking.get(i)))+"\n");
                    else temp2+=("accepted\t\t\t\t"+postValue.get(i)+"\t"+tagValueRanking.get(i)+"\t\t"+postPopularity.get(i)+"\t\t"+tagPopularityRanking.get(i)+"\t\t\t"+(tagRating.set(i,tagPopularityRanking.get(i)+tagValueRanking.get(i)))+"\n");
                } temp2+="\nMOST INTERACTIVE POSTS\n";
                for (int i=tagPopularityRanking.size(); i>tagPopularityRanking.size()-11; i--)
                        for (int j=0; j<tagPopularityRanking.size(); j++) if (i==tagPopularityRanking.get(j)) 
                            if (j!=0) temp2+=(tag.get(j)+"\t"+postValue.get(j)+"\t"+(tagValueRanking.get(j))+"\t\t"+postPopularity.get(j)+
                                "\t\t"+(tagPopularityRanking.get(j))+"\t\t\t"+(tagValueRanking.get(j)+tagPopularityRanking.get(j))+"\n");
                            else temp2+=("accepted\t\t\t\t"+postValue.get(j)+"\t"+(tagValueRanking.get(j))+"\t\t"+postPopularity.get(j)+
                                "\t\t"+(tagPopularityRanking.get(j))+"\t\t\t"+(tagValueRanking.get(j)+tagPopularityRanking.get(j))+"\n");
                temp2+="\nMOST VALUABLE POSTS\n";
                for (int i=tagValueRanking.size(); i>tagValueRanking.size()-11; i--)
                        for (int j=0; j<tagValueRanking.size(); j++) if (i==tagValueRanking.get(j)) 
                            if (j!=0) temp2+=(tag.get(j)+"\t"+postValue.get(j)+"\t"+(tagValueRanking.get(j))+"\t\t"+postPopularity.get(j)+
                                "\t\t"+(tagPopularityRanking.get(j))+"\t\t\t"+(tagValueRanking.get(j)+tagPopularityRanking.get(j))+"\n");
                            else temp2+=("accepted\t\t\t\t"+postValue.get(j)+"\t"+(tagValueRanking.get(j))+"\t\t"+postPopularity.get(j)+
                                "\t\t"+(tagPopularityRanking.get(j))+"\t\t\t"+(tagValueRanking.get(j)+tagPopularityRanking.get(j))+"\n");
                temp2+="\nMOST RATED POSTS\n"; 
                int[] highest = new int[10]; Object[] tagRankingArray = tagRating.toArray(); Arrays.sort(tagRankingArray);
                for (int j=tagRankingArray.length-1, tmp=0; j>tagRankingArray.length-11; j--, tmp++) 
                        for (int k=0; k<tagRating.size(); k++)
                            if (tagRating.get(k)==(int)tagRankingArray[j]) { highest[tmp] = tagRating.get(k); break; }
                for (int j=0; j<highest.length; j++)
                        for (int k=0; k<tagRating.size(); k++)  if (tagRating.get(k)==highest[j])
                            if (k!=0) temp2+=(tag.get(k)+"\t"+postValue.get(k)+"\t"+(tagValueRanking.get(k))+"\t\t"+postPopularity.get(k)+
                                "\t"+(tagPopularityRanking.get(k))+"\t\t"+(tagValueRanking.get(k)+tagPopularityRanking.get(k))+"\n");
                            else temp2+=("accepted\t\t\t\t"+postValue.get(k)+"\t"+(tagValueRanking.get(k))+"\t\t"+postPopularity.get(k)+
                                "\t"+(tagPopularityRanking.get(k))+"\t\t"+(tagValueRanking.get(k)+tagPopularityRanking.get(k))+"\n");
                System.out.println("Done!!!"); appendAndSave(temp2, "output.saf", "UTF-8");
            } else System.out.println("No data obtained\n");
        } else { System.out.println("\nTrying to create new data file...");
            if (!new File("input.saf").exists()) try {
                Files.createFile(new File("input.saf").toPath());
                System.out.println("First time installation.\nCreated data file, input.saf");
                EventQueue.invokeLater(() -> { analyzer = new TagDataExtractor(); });
            } catch (IOException ex1) { System.err.println("Data file creation failed. Exiting...\n"+ex1.getMessage()); }
        }
    }
    
    /**
     *
     * @param list The list to be formatted
     * @param currentItemInList The index of the string in the list
     * @param xters The character width of elements in the list
     * 
     * Sets the characters in a list of strings to a particular length. 
     * All members of the list will be defined by the same amount of characters.
     */
    private void formatArrayListChars(ArrayList<String> list, int currentItemInList, int xters) {
        while (list.get(currentItemInList).length()<xters) { String temp3 = list.get(currentItemInList);
            list.set(currentItemInList,temp3+=" ");
        }
    }
    
    /**
     *
     * @param file The path of file to be read
     * @param charsetname The character set of the document, eg. unicode, utf-8, etc
     * @return The content of the read file. If an error occurs, null is returned.
     */
    public final String readFile(Path file, String charsetname) { // Try bufferedReader
        Charset charset = Charset.forName(charsetname);
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line, page = "";
            while ((line = reader.readLine()) != null) { page+=line+"\n"; } 
            return page;
        } catch (IOException x) { // Try inputStream
            System.err.format("IOException: %s%n", x); 
            try (InputStream in = Files.newInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line, page = "";
                while ((line = reader.readLine()) != null) { page+=line+"\n"; } 
                return page;
            } catch (IOException ex) { System.err.println(ex); } return null;
        }
    }
    
    /**
     *
     * @param path The path to which the file is to be written
     * @param data The string content of file to be written
     * @param charsetname The name of the character set to be applied (utf-8, unicode, etc)
     */
    public static void writeFile(Path path, String data, String charsetname) { // try {
            // Files.createTempFile(path.toString(), ".tmp"); // Create a temp file with an extension, .tmp
        // } catch (IOException ex) { System.err.format("create temporary file error: %s%n", ex.getMessage()); }
        if (!path.toFile().exists()) try { Files.createFile(path);
            } catch (FileAlreadyExistsException x) { System.err.format("file named %s already exists%n%s", path.toFile().getName(), x.getMessage());
            } catch (IOException x) { System.err.format("write to file error: %s%n", x);
        } else { System.err.format("Writing to file "+path.toFile().getName()+"\n"); } 
        Charset charset = Charset.forName(charsetname);
        for (int i=0; i<data.length(); i++) {
            while (!charIsLetter(data.charAt(i))) i++; int start = i-1;
            if (start<0) start = 0; data = data.substring(start, data.length()); break;
        } // data = data.trim();
        try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) { writer.write(data, 0, data.length()-1); } // Try bufferedWriter
        catch (IOException e) { // Try outputStream
            System.err.format("IOException: %s%n", e); // Convert the string to a byte array.
            byte dataArr[] = data.getBytes();
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, CREATE, APPEND, SYNC))) { out.write(dataArr, 0, dataArr.length); } 
            catch (IOException ex) { System.err.println(ex); }
        }
    }
    
    /**
     * @var filename The name of file to be created.
     * @var screenSize The dimension of host machine screen.
     * @set For optimal performance, screen resolution must be over 880px X 750px
     */ // Resize for mini computers
    private void createFile(String filename){
         Path file;
         try { file = Paths.get(filename);
         } catch(Exception ex) { System.err.println(ex);
             if (!new File(filename).exists()) try {
             Files.createFile(new File(filename).toPath());
         } catch (IOException ex1) { System.err.println(ex1); }} 
    }
    
    /**
     *
     * @param data The string to be appended
     * @param location The string address of the location to save into
     * @param charsetname The character set (eg. Unicode, UTF-8, ANSI, etc) for saving to the file
     * 
     * Saves the string content additionally, to the file location, using the defined character set.
     */
    public final void appendAndSave(String data, String location, String charsetname){
        Path file = Paths.get(location); // GET APP STORAGE!!!
        if (!file.toFile().exists()) createFile(location);
        String oldData = readFile(file, charsetname); // Retrieve old data & initialize new transaction db
        if (oldData==null) oldData = ""; // This occurs on first installation
        writeFile(file,(oldData+data+"\nComputation timestamp: "+new SimpleDateFormat().format(new Date())+" "),charsetname); // Save to local storage
    }
    
    /**
     *
     * @param data The string to be saved
     * @param location The string address of the location to save into
     * @param charsetname The character set (eg. Unicode, UTF-8, ANSI, etc) for saving to the file
     * 
     * Saves the string content, to the file location, using the defined character set and deletes old content.
     */
    public final void clearAndSave(String data, String location, String charsetname){
        Path file = Paths.get(location); if (!file.toFile().exists()) createFile(location);
        writeFile(file,(data+"\nComputation timestamp: "+new SimpleDateFormat().format(new Date())+" "),charsetname); // Save to local storage
    }
    
    /**
     *
     * @param thisChar The character to be tested.
     * @return The boolean values 'true', if the character is a number, or else it returns 'false'.
     */
    final static boolean charIsNum(char thisChar) {
        return thisChar=='0'||thisChar=='1'||thisChar=='2'||thisChar=='3'||thisChar=='4'||thisChar=='5'||thisChar=='6'||thisChar=='7'||thisChar=='8'||thisChar=='9';
    }
    
    /**
     *
     * @param thisChar The character to be tested.
     * @return The boolean values 'true', if the character is a lowercase letter, or else it returns 'false'.
     */
    final static boolean charIsLowerCase(char thisChar) {
        return thisChar=='a'||thisChar=='b'||thisChar=='c'||thisChar=='d'||thisChar=='e'||thisChar=='f'||thisChar=='g'||thisChar=='h'||thisChar=='i'||thisChar=='j'||thisChar=='k'||thisChar=='l'||thisChar=='m'||
                thisChar=='n'||thisChar=='o'||thisChar=='p'||thisChar=='q'||thisChar=='r'||thisChar=='s'||thisChar=='t'||thisChar=='u'||thisChar=='v'||thisChar=='w'||thisChar=='x'||thisChar=='y'||thisChar=='z';
    }
    
    /**
     *
     * @param thisChar The character to be tested.
     * @return The boolean values 'true', if the character is a uppercase letter, or else it returns 'false'.
     */
    final static boolean charIsUpperCase(char thisChar) {
        return thisChar=='A'||thisChar=='B'||thisChar=='C'||thisChar=='D'||thisChar=='E'||thisChar=='F'||thisChar=='G'||thisChar=='H'||thisChar=='I'||thisChar=='J'||thisChar=='K'||thisChar=='L'||thisChar=='M'||
                thisChar=='N'||thisChar=='O'||thisChar=='P'||thisChar=='Q'||thisChar=='R'||thisChar=='S'||thisChar=='T'||thisChar=='U'||thisChar=='V'||thisChar=='W'||thisChar=='X'||thisChar=='Y'||thisChar=='Z';
    }
    
    /**
     *
     * @param thisChar The character to be tested.
     * @return The boolean values 'true', if the character is a symbol, or else it returns 'false'.
     */
    final static boolean charIsSymbol(char thisChar) {
        return thisChar=='`'||thisChar=='¬'||thisChar=='!'||thisChar=='"'||thisChar=='£'||thisChar=='$'||thisChar=='%'||thisChar=='^'||thisChar=='&'||thisChar=='*'||thisChar=='('||thisChar==')'||thisChar=='-'||
                thisChar=='_'||thisChar=='='||thisChar=='+'||thisChar=='['||thisChar==']'||thisChar=='{'||thisChar=='}'||thisChar=='\''||thisChar=='\"'||thisChar=='@'||thisChar=='#'||thisChar=='~'||thisChar=='|'||
                thisChar=='\\'||thisChar=='/'||thisChar=='?'||thisChar==','||thisChar==','||thisChar=='.'||thisChar=='<'||thisChar=='>';
    }
    
    /**
     *
     * @param thisChar The character to be tested.
     * @return The boolean values 'true', if the character is an alphabet (lower or upper case), or else it returns 'false'.
     */
    final static boolean charIsAlphabet(char thisChar) {
        return charIsLowerCase(thisChar)||charIsUpperCase(thisChar);
    }
    
    /**
     *
     * @param thisChar The character to be tested.
     * @return The boolean values 'true', if the character is a letter (symbol or alphabet), or else it returns 'false'.
     */
    final static boolean charIsLetter(char thisChar) {
        return charIsAlphabet(thisChar)||charIsNum(thisChar)||charIsSymbol(thisChar);
    }
}
