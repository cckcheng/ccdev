
import com.itextpdf.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *
 * @author Colin Cheng
 */
public class GenPDF {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException,
			DocumentException, FileNotFoundException, IOException {
		if(args.length < 1) {
			System.out.println("Usage: java GenPDF gedcomFile -p params");
			System.exit(1);
		}

		String gedcomName = args[0];
		int idx = gedcomName.lastIndexOf(".");
		if(idx<0) {
			System.out.println("Invalid gedcomFile");
			System.exit(2);
		}
		if(!gedcomName.substring(idx).equalsIgnoreCase(".ged")) {
			System.out.println("Invalid gedcomFile");
			System.exit(2);
		}
		FamilyTree famTree = new FamilyTree();
		if(args.length > 2) {
			String param = URLDecoder.decode(args[2], "UTF-8");
			int x0 = param.indexOf("gedkey=") + 7;
			int x1 = param.indexOf("&", x0);
			if(x1 < x0) param = param.substring(x0);
			else param = param.substring(x0, x1);
			String[] pp = param.split(",");
			for(String p : pp) {
				famTree.addGedKey(p);
			}
		}
		famTree.buildFamilyTree(gedcomName);
		if(famTree.hasError()) {
			System.out.println(famTree.getErrorMessage());
			if(famTree.isFatal()) System.exit(-1);
		}
//		famTree.printTree();	// for test

		TreeToPDF toPDF = new TreeToPDF(famTree.getRoot(), famTree.getIndividualList());

		if(args.length > 1) {
			if(args[1].equalsIgnoreCase("-p")) {
				if(args.length > 2) {
//					System.out.println(URLEncoder.encode(args[2], "UTF-8"));
					String param = URLDecoder.decode(args[2], "UTF-8");
//					System.out.println(param);
					toPDF.parseConfigStr(param);
				}
			} else if(!toPDF.parseConfig(args[1])) {
				System.out.println(toPDF.getErrorMessage());
				System.exit(3);
			}
		}

		String fname = gedcomName.substring(0, idx);
		toPDF.generatePDF(fname + ".pdf");
//		toPDF.testPDF();
		if(toPDF.hasError()) {
			System.out.println(toPDF.getErrorMessage());
			System.exit(-1);
		}
    }
}
