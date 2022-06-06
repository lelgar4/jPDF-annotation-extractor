import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.bouncycastle.crypto.signers.DSTU4145Signer;



public class NotesExtractorCMD
{
    static String filePath;
    static PDDocument document;
    static List<PDMarkedContent> listMarkedContents;
    static int maxPage;
    private static boolean isMultiplePages = false;


    public static void main(String[] args) throws IOException
    {
        test_getNotes();


//      -----------------------------------------------------------
//      filePath = getInputFilePath();
//      document = Loader.loadPDF(new File("C:\\Users\\elgar\\Documents\\test.pdf"));
//      maxPage = document.getNumberOfPages();
//      getInputIsMultiplePages();

/*      int pageStart = getInputPage(maxPage);

        if (isMultiplePages) {
            int pageEnd = getInputPage(maxPage,pageStart);
            getNotes(pageStart,pageEnd);

        } else {
            getNotes(pageStart);
        }
*/

        //PDFTextStripper textStripper = new PDFTextStripper();



    }


    public static void test_getNotes() throws IOException
    {
        COSDocument cosDoc = null;
        PDFTextStripper textStripper = null;
        PDFParser parser;
        PDDocument doc = null;

        String parsedText;
        String fileName = "C:\\Users\\elgar\\Documents\\test.pdf";
        File file = new File(fileName);

        try{
            String content;
            PDDocument pdDocument = new PDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            pdDocument.close();
            pdDocument = Loader.loadPDF(new File(fileName));
            //content = stripper.getText(pdDocument);
            //System.out.println(content);

            ArrayList<List<PDAnnotation>> annotations = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                List<PDAnnotation> pageAnnotations = pdDocument.getPage(i).getAnnotations();
                annotations.add(pageAnnotations);
            }

            for (List<PDAnnotation> a: annotations) {
                for (PDAnnotation pd: a) {
                    String text = pd.toString();
                    System.out.println(text);

                    System.out.println(pd);
                    System.out.println(pd.getContents());
                    System.out.println(pd.getAnnotationName());
                    System.out.println("\n");
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

/*      try {
            //parser = new PDFParser((RandomAccessRead) new FileInputStream(new File("C:\\Users\\elgar\\Documents\\test.pdf")));
            parser = new PDFParser((RandomAccessRead) new FileInputStream(file));
            parser.parse();
            cosDoc = parser.parse().getDocument();
            textStripper = new PDFTextStripper();
            doc = new PDDocument(cosDoc);
            //parsedText = textStripper.getAppearance().



        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }


    public static void getNotes(int page) throws IOException
    {
        try {
            PDFMarkedContentExtractor contentExtractor = new PDFMarkedContentExtractor();
            contentExtractor.processPage(document.getPage(page - 1));
            List<PDMarkedContent> pageContents = contentExtractor.getMarkedContents();

            List<PDAnnotation> annotations = document.getPage(page - 1).getAnnotations();

            System.out.println("pageContents: " + pageContents);
            System.out.println("annotations: " + annotations);
            System.out.println();
            System.out.println("Start for loop");           // -- delete

            for (Iterator<PDMarkedContent> i = pageContents.iterator(); i.hasNext(); ) {

                System.out.println("loop..");               // -- delete

                PDMarkedContent content = (PDMarkedContent) i.next();
                System.out.println("tags: " + content.getTag()
                        +"\nContents: "+content.getContents()
                        + "\nProperties"+content.getProperties());
            }

            System.out.println("END: getNotes()");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void getNotes(int pageStart, int pageEnd) throws IOException
    {
        PDFMarkedContentExtractor contentExtractor = new PDFMarkedContentExtractor();

        for(int pageIterator = pageStart; pageIterator <= pageEnd; pageIterator++){
            contentExtractor.processPage(document.getPage(pageIterator));

        }

    }


    //  TODO: method documentation
//  ======================================================================
//                              getInputFilePath()
//  ======================================================================
    public static String getInputFilePath()
    {
        String filePath = "";

        try
        {
            OUTER: while (true)
            {
                Scanner scanFilePath = new Scanner(System.in);
                System.out.println("""
                        =================================================
                        Input full/absolute file path."
                        "EX: c:/user/you/fileName.type"
                        "File path: 
                        """);

                String inputFilePath = scanFilePath.next();
                //scanFilePath.close();

                if (validateFilePath(inputFilePath)) {
                    filePath = inputFilePath;
                    break;
                } else {
                    printErrorMessage(          "Invalid File Path",
                                                "No such file found at the path entered.");

                    INNER: while (true) {
                        Scanner scan = new Scanner(System.in);
                        System.out.println("""
                                Select one of the following options:
                                [1] -- Continue w/ invalid file path
                                [2] -- Input new file path
                                """);
                        int inputContinue = scan.nextInt();

                        if (inputContinue == 1){
                            filePath = inputFilePath;
                            break;
                        }

                        else if(inputContinue == 2){
                            continue OUTER;
                        }

                        else{
                            printErrorMessage("Invalid Input","An integer other than '1' or '2' was entered.\n\tTry again.");
                            continue INNER;
                        }
                    }
                }
            }
        } catch (InputMismatchException ime) {
            printErrorMessage(          "Invalid input.",
                                        "A non-integer value was input. Input must be '1' or '2'.\n\tTry again.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }



/** ======================================================================
 *                              ValidateFilePath()
 *  ======================================================================
 *      -- Validates file and path.
 *      -- Returns true if file/file path is valid; otherwise returns false
 */

    public static boolean validateFilePath(String filePath)
    {
        try {
            Paths.get(filePath);

//  if an InvalidPathException is thrown by Paths.get() then the file path is not valid; function returns false
        } catch (InvalidPathException ipe) {
            printErrorMessage(      "Invalid File/Path",
                                    "InvalidPathException thrown. No such file found at path entered."
            );
            return false;
        }
//  if try-catch executes w/o throwing InvalidPathException, file path is valid; return true
        return true;
    }



    //  TODO: method documentation
/** ======================================================================
 *                          getInputPage()
 *  ======================================================================
 *      --  Get single page number from user via scanner input.
 *      --  Param 'maxPage' = the last page of the pdf/file
**/
    public static int getInputPage(int maxPage)
    {
//      while loop used to prevent invalid input. Loop continues until valid input is entered
        while (true)
        {
            try {
                Scanner input = new Scanner(System.in);
                System.out.println("Enter page number: ");
                int pageNumber = input.nextInt();
                input.close();

                if (pageNumber <= maxPage) {
                    return pageNumber;

                } else {
                    printErrorMessage(  "Invalid Page Number",
                                        "Page number entered is greater than the last page in the document.\n\tTry again.");
                }

            } catch (InputMismatchException ime) {
                printErrorMessage(  "InputMismatchException thrown",
                                    "Input for page number must be an integer, e.g. '1'.\n\tTry again.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    //  TODO: method documentation
/** ======================================================================
 *                              getInputPage()
 *  ======================================================================
 *      --  Get single page number from user via scanner input.
 *      --  Overridden method; used to get input for end page number
**/
    public static int getInputPage(int maxPage, int pageStart)
    {
        while (true)
        {
            int inputLastPage;
            try {
                Scanner input = new Scanner(System.in);
                System.out.println("Enter page number: ");
                inputLastPage = input.nextInt();
                input.close();

//  input validation; page number input by user cannot exceed document max page
                if (inputLastPage >= maxPage) {
                    printErrorMessage(      "Invalid page number.",
                                            "Page number entered is greater than the last page in the document.\n\t Try again.");
                }

//  input validation; end of range page number cannot preceed start of range page number (pageStart)
                else if (inputLastPage <= pageStart) {
                    printErrorMessage(          "Invalid page number.",
                                                "End page is less than start page.\n\tTry again.");
                }

                // else if()    TODO:[?]

                else {
                    return inputLastPage;
                }

            } catch (InputMismatchException ime) {
                printErrorMessage(      "InputMismatchException thrown.",
                                        "Input for page number must be an integer, e.g. '1'.\n\tTry again.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



/** ======================================================================
 *                          printErrorMessage()
 *  ======================================================================
 *      --  Creates an error message using a template, then outputs the error message to console
 *      --  If function is passed a null/invalid String for errorHeader/errorBody, a generic
 *          default error message is printed instead
 *
 * @param errorMessageHeader -- error message body; a more detailed description of error and how/why it occurred
 * @param errorMessageBody  -- error message header; a title/brief summarization of the error or type of error
 */
    public static void printErrorMessage(String errorMessageHeader,String errorMessageBody)
    {
//  TODO: def defaultHeader/defaultBody
        String defaultHeader;
        String defaultBody;

/**  TODO::
 *      if (errorMessageBody.isEmpty()) {errorMessageBody = defaultBody;}
 *      if (errorMessageHeader.isEmpty()) {errorMessageHeader = defaultHeader;}
 **/

        try {
            int messageBorderLen = Math.max(errorMessageHeader.length(),errorMessageBody.length());
            String messageBorder = "=".repeat(messageBorderLen + 5);

            String errorMessage = messageBorder + "\n" +
                    "ERROR: " + errorMessageHeader + "\n\n" +
                    "\t" + errorMessageBody + "\n" +
                    messageBorder;

            System.out.println(errorMessage);

//  TODO:   NPE catch block/exception handling; printErrorMessage() in catch block
        } catch (NullPointerException npe) {
            System.out.println("""
                    =========================================
                    String errorHeader/errorBody null
                    =========================================
                    """);
            npe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



/** ======================================================================
 *                          getInputIsMultiplePages()
 *  ======================================================================
 *      --
 **/
    public static void getInputIsMultiplePages()
    {
//  TODO: Input validation; throw exception -> error message if optionSelected:input != 1 || 2
//  TODO:       -- non-numeric input
//  TODO:       -- ints > 2 && ints < 1

        while (true)
        {
            try
            {
                Scanner scanOption = new Scanner(System.in);
                System.out.println("""
                        ================================================================================
                        Select one of the following options:\s
                        [1] Enter '1' to extract notes from a single page.
                        [2] Enter '2' to extract notes from multiple, sequential pages (e.g. 1-10).
                        Option:\s""");
                int optionSelected = scanOption.nextInt();

                if (optionSelected == 1) {
                    isMultiplePages = false;
                    break;

                } else if (optionSelected == 2) {
                    isMultiplePages = true;
                    break;

                } else {
                    printErrorMessage(  "Invalid Input",
                                        "Must enter either '1' or '2'.\n\tTry again.");

                }

            } catch (InputMismatchException ime) {
                printErrorMessage(  "Invalid Input",
                                    "A non-integer value was input. Input must be '1' or '2'.\n\tTry again.");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



/** ======================================================================
 *                        GETTER/SETTER METHODS
 *  ======================================================================
 */

    public static boolean getIsMultiplePages()                      {return isMultiplePages;}

}
