import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: Enayat Rajabi  (university of ALcala de Henares)
 * Date: 2013/09/30
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */

public class DC2LOM {


    // ********************    DATABASE ACTIVITY *************************************************
     static void IEEELOM_Creator (){

     }

    // ------------     OPEN XML FILE  AND READ AN ELEMENT THAT IS UNDER ROOT->ELEMENT            --------------------------
    static void readDublinCore(String fileName, String root) {
        String value = null, attributeName = null, attributeValue = null, element = null;
        String DCElements[] = {"dc:title", "dc:language", "dc:relation", "dc:description", "dc:subject", "dc:rights", "dc:publisher",
                "dc:creator", "dc:type", "dc:contributor", "dc:source", "dc:coverage", "dc:format", "dc:date", "dc:identifier"};
        IEEELOM IEEELOMobj=new IEEELOM();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(fileName);
            if (file.exists()) {

                Document doc = db.parse(file);
                Element docEle = doc.getDocumentElement();
                try {
                    // Root element --- here is general ---  C:\Enayat\ODS_Resources\SecondCycle\RESURSI
                    NodeList ElementList = null;
                    if (root.equals(root))
                        ElementList = doc.getElementsByTagName(root);
                    else
                        ElementList = docEle.getElementsByTagName(root);
                    if (ElementList != null && ElementList.getLength() > 0) {
                        for (int i = 0; i < ElementList.getLength(); i++) {
                                Node node = ElementList.item(i);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    Element e = (Element) node;

                                    try {
                                        NodeList nodeArrayList = e.getElementsByTagName("dcvalue");
                                        for (int j = 0; j < nodeArrayList.getLength(); j++) {
                                            value = nodeArrayList.item(j).getChildNodes().item(0).getNodeValue();
                                            //System.out.println(" ---------------------"+nodeArrayList.item(j).getAttributes().item(0).getNodeValue()+"---------------------------------------");
                                            //System.out.println(" ---------------------"+nodeArrayList.item(j).getAttributes().getNamedItem("element").getNodeValue()+"---------------------------------------");
                                            if(nodeArrayList.item(j).getAttributes().getNamedItem("element").getNodeValue().equals("identifier") &&
                                                    nodeArrayList.item(j).getAttributes().getNamedItem("qualifier").getNodeValue().equals("uri"))
                                                     IEEELOMobj.Set_General_Identifier(nodeArrayList.item(j).getTextContent());
                                            if(nodeArrayList.item(j).getAttributes().getNamedItem("element").getNodeValue().equals("title"))
                                                IEEELOMobj.Set_General_Title(nodeArrayList.item(j).getTextContent());
                                            if(nodeArrayList.item(j).getAttributes().getNamedItem("element").getNodeValue().equals("language"))
                                                IEEELOMobj.Set_General_Language(nodeArrayList.item(j).getTextContent());
                                            if(nodeArrayList.item(j).getAttributes().getNamedItem("element").getNodeValue().equals("description")){
                                                IEEELOMobj.Set_General_Description(nodeArrayList.item(j).getTextContent());
                                            }
                                            if(nodeArrayList.item(j).getAttributes().getNamedItem("element").getNodeValue().equals("subject")){
                                                IEEELOMobj.Set_General_Keyword(nodeArrayList.item(j).getTextContent());
                                            }
                                                //System.out.println(" ----------TEST="+nodeArrayList.item(j).getTextContent());
                                            if(nodeArrayList.item(j).getAttributes().getNamedItem("qualifier").getNodeValue().equals("restricciones") &&
                                                     nodeArrayList.item(j).getAttributes().getNamedItem("element").getNodeValue().equals("subject"))

                                                    attributeName=nodeArrayList.item(j).getAttributes().item(0).getNodeName();
                                            attributeValue=nodeArrayList.item(j).getAttributes().item(0).getNodeValue();
                                            if(attributeName.equals("element") && attributeValue.equals("identifier")){
                                                IEEELOMobj.Set_General_Identifier(nodeArrayList.item(j).getTextContent());
                                                System.out.println(" -----> Hello="+IEEELOMobj.Get_General_Identifier());
                                            }

                                            //System.out.println("Value=" + value);
                                            //System.out.println("Parent="+nodeArrayList.item(0).getParentNode().getNodeName());
                                        }

                                        System.out.println(" ------------------------------------------------------------");

                                    } catch (Exception ee) {
                                        value = "Error";
                                    }

                            }
                        }
                    } else {
                        value = "Error";
                    }
                } catch (Exception eg) {
                    value = "Error";
                }
            }
        } catch (Exception e) {
            value = "Error";
        }
    }


    // ------------     Main Function         --------------------------
    static void parseDublinCore(File folder, String DCRootElement) {
        File[] listOfFiles;
        int fileNumber = 0, fileIndex;
        String fileName = null;
        int recordsNumber = 0;

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml") || name.endsWith(".XML");
            }
        };

        try {
            listOfFiles = folder.listFiles(filter);
            fileNumber = listOfFiles.length;
            if (fileNumber == 0) {
                System.out.println("No XML files found in the selected folder");
                System.exit(1);
            }

            System.out.println(" -----------------------------------------------------------");
            System.out.println("processing " + fileNumber + " files ...");
            System.out.println(" ------------------------------------------------------------");

            for (fileIndex = 0; fileIndex < fileNumber; fileIndex++) {
                System.out.println(" ------------------------------------------------------------");
                System.out.println("processing " + (fileIndex + 1) + "th file ...");
                System.out.println(" ------------------------------------------------------------");
                fileName = listOfFiles[fileIndex].getPath();
                System.out.println("checking FileName= " + fileName);
                readDublinCore(fileName, DCRootElement);

            }
            recordsNumber = fileIndex;

            System.out.println(" --------------------Finsished! -----------------------------");
            System.out.println(" Total number of records=" + recordsNumber);
            System.out.println(" ------------------------------------------------------------");
        } catch (Exception NotFolder) {
            System.out.println("Could not read the files ");
        }
    }

    public static void main(String args[]) {
        DC2LOM DC2LOMObj = new DC2LOM();
        File metadataFolder = new File("C:\\Users\\Rajabi\\IdeaProjects\\DC2LOM\\dc");
        String DCRootElement = "dublin_core";
        if (!metadataFolder.exists()) {
            System.out.println("-------------------------------------");
            System.out.println("Error! the folder does not exist-->" + metadataFolder.getAbsolutePath());
            System.out.println("-------------------------------------");
            System.exit(1);
        }
        DC2LOMObj.parseDublinCore(metadataFolder, DCRootElement);
    }
}
