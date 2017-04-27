package by.vbalanse.spark.test;

import org.apache.commons.io.FileUtils;
import org.jdom.*;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.List;

/**
 * Created by Vasilina_Terehova on 4/18/2017.
 */
public class ReplacerArtifactNamesWithCorrectDirs {
    public static final String SHIM_NAME = "shimName";
    public static final String SHIM_VERSION = "shimVersion";

    public static void main(String[] args) throws IOException, JDOMException {
        String shimName = "emr531";
        String folderWithArchetype = "D:\\projects\\pentaho\\pentaho-hadoop-shims\\shims\\" + shimName + "\\target\\generated-sources\\archetype\\src\\main\\resources\\META-INF\\maven\\";
        String fileName = folderWithArchetype + "archetype-metadata.xml";
        String newFileName = folderWithArchetype + "archetype-metadata2.xml";
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        //Scanner scanner = new Scanner(new File(fileName));
        String fileContent = FileUtils.readFileToString(new File(fileName));
        String replacedContent = fileContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-client-assembly\"", "dir=\"client\"");
        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-pmr-assembly\"", "dir=\"pmr\"");
        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-default-assembly\"", "dir=\"default\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-hbase-comparators\"", "dir=\"hbase-comparators\"");
        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "\"", "dir=\"impl\"");
        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-assemblies-reactor\"", "dir=\"assemblies\"");
        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-package\"", "dir=\"__" + SHIM_NAME + "__-shim\"");


        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-", "dir=\"");
        FileUtils.write(new File(fileName), replacedContent, "utf-8");

        //get tag requiredProperty, add artifactId, groupId

        addArtifactGroupIdDefaultValues(shimName, fileName, newFileName);


        //rename folder now
        //String folderName = "D:\\projects\\pentaho\\pentaho-hadoop-shims\\shims\\emr52\\target\\generated-sources\\archetype\\target\\classes\\archetype-resources\\assemblies\\";
        String folderName = "D:\\projects\\pentaho\\pentaho-hadoop-shims\\shims\\"+shimName+"\\target\\generated-sources\\archetype\\src\\main\\resources\\archetype-resources\\assemblies\\";
        String assemblyFolderName = folderName + shimName + "-shim";
        String newAssemblyFolderName = folderName + "__" + SHIM_NAME + "__-shim";
        File sourceFolder = new File(assemblyFolderName);
        if (sourceFolder.exists()) {
            FileUtils.moveDirectory(sourceFolder, new File(newAssemblyFolderName));
        }

        //System.out.println(scanner.);
    }

    private static void addArtifactGroupIdDefaultValues(String shimName, String fileName, String newFileName) throws JDOMException, IOException {
        Document documentFromFile = XmlUtilsJDom1.getDocumentFromFile(fileName);
        Element requiredElementsElement = (Element) documentFromFile.getRootElement().getContent(new ElementFilter("requiredProperties")).get(0);
        createPropertyWithDefaultValue(requiredElementsElement,"artifactId", "pentaho-hadoop-shims-${"+SHIM_NAME+"}-reactor");
        createPropertyWithDefaultValue(requiredElementsElement,"groupId", "org.pentaho");
        removeDefaultValue(requiredElementsElement, SHIM_VERSION);
        XmlUtilsJDom1.outputDoc(documentFromFile, fileName);
    }

    private static void removeDefaultValue(Element requiredElementsElement, String moduleFullNumber) {
        List content = requiredElementsElement.getContent(new ElementFilter("requiredProperty"));
        for (Object contentItem : content) {
            Element requiredProperty = (Element) contentItem;
            if (requiredProperty.getAttribute("key").getValue().equals(moduleFullNumber)) {
                List defaultValues = requiredProperty.getContent(new ElementFilter("defaultValue"));
                if (defaultValues.size() > 0) {
                    Element defaultValue = (Element) defaultValues.get(0);
                    defaultValue.detach();
                }
            }
        }
    }

    private static Element createPropertyWithDefaultValue(Element requiredElementsElement, String propertyName, String propertyValue) throws JDOMException, IOException {
        Namespace namespace = requiredElementsElement.getNamespace();
        List content = requiredElementsElement.getContent(new ElementFilter("requiredProperty", namespace));
        for (Object contentItem : content) {
            Element requiredProperty = (Element) contentItem;
            if (requiredProperty.getAttribute("key").getName().equals(propertyName)) {
                return null;
            }
        }
        Element requiredElement = readElementFromString("<requiredProperty key=\""+propertyName+"\">\n" +
                "      <defaultValue>"+propertyValue+"</defaultValue>\r\n" +
                "    </requiredProperty>", namespace);
//        Element requiredElement = new Element("requiredProperty", namespace);
//        requiredElement.setAttribute("key", propertyName);
//        Element defaultValue = new Element("defaultValue", namespace);
//        defaultValue.setText(propertyValue);
//        requiredElement.addContent(defaultValue);
        if (propertyName.equals("artifactId")) {
            requiredElementsElement.addContent(new Text("  "));
        } else {
            requiredElementsElement.addContent(new Text("    "));
        }
        requiredElementsElement.addContent(requiredElement);
        requiredElementsElement.addContent(new Text("\n"));
        return requiredElement;
    }

    public static Element readElementFromString(String toAdd, Namespace namespace) throws JDOMException, IOException {
        SAXBuilder jdomBuilder2 = new SAXBuilder(false);
        Document doc = jdomBuilder2.build(new StringReader("<just_wrapper_now xmlns=\"" + namespace.getURI() + "\">" +
                toAdd +
                "</just_wrapper_now>"));
        Element targetElement = (Element) doc.getRootElement().getChildren().stream().findFirst().get();
        targetElement.detach();
        return targetElement;
    }
}
