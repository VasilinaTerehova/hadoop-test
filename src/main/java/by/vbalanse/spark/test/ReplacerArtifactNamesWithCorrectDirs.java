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
        String shimName = "cdh511";
        String shimVendor = "cdh";
        String shimEdition = Edition.EE.getEditionShimName();
        String pathToPom = "D:\\21\\pentaho-big-data-ee-master\\shims\\" + shimName + "\\target\\generated-sources\\archetype\\";
        String folderWithArchetype = pathToPom + "src\\main\\resources\\META-INF\\maven\\";
        String fileName = folderWithArchetype + "archetype-metadata.xml";
        String newFileName = folderWithArchetype + "archetype-metadata2.xml";
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        //Scanner scanner = new Scanner(new File(fileName));

        PomUpdater.updateShimPom(pathToPom, shimVendor,shimEdition);

        replaceContent(shimName, fileName, shimVendor, shimEdition);

        addArtifactGroupIdDefaultValues(fileName, shimEdition);

        String folderName = pathToPom + "src\\main\\resources\\archetype-resources\\assemblies\\";
        String prefix = shimEdition.equals(Edition.CE.getEditionShimName()) ? "" : "-" + Edition.EE.getEditionShimName();
        String assemblyFolderName = folderName + shimName + prefix + "-shim";
        String newAssemblyFolderName = folderName + "__" + SHIM_NAME + "__" + prefix + "-shim";
        File sourceFolder = new File(assemblyFolderName);
        if (sourceFolder.exists()) {
            FileUtils.moveDirectory(sourceFolder, new File(newAssemblyFolderName));
        }

        //System.out.println(scanner.);
    }

    private static void addArtifactGroupIdDefaultValues(String fileName, String shimEdition) throws JDOMException, IOException {
        Document documentFromFile = XmlUtilsJDom1.getDocumentFromFile(fileName);
        Element requiredElementsElement = (Element) documentFromFile.getRootElement().getContent(new ElementFilter("requiredProperties")).get(0);

        String artifactPrefix = "pentaho-hadoop-shims";
        String groupId = "org.pentaho";
        if (shimEdition.equals(Edition.EE.getEditionShimName())) {
            artifactPrefix = "pentaho-big-data-ee";
            groupId = "com.pentaho";
        }

        createPropertyWithDefaultValue(requiredElementsElement, "artifactId", artifactPrefix + "-${" + SHIM_NAME + "}-reactor");
        createPropertyWithDefaultValue(requiredElementsElement, "groupId", groupId);
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
        Element requiredElement = readElementFromString("<requiredProperty key=\"" + propertyName + "\">\n" +
                "      <defaultValue>" + propertyValue + "</defaultValue>\r\n" +
                "    </requiredProperty>", namespace);
//        Element requiredElement = new Element("requiredProperty", namespace);
//        requiredElement.setAttribute("key", propertyName);
//        Element defaultValue = new Element("defaultValue", namespace);
//        defaultValue.setText(propertyValue);
//        requiredElement.addContent(defaultValue);
        if (propertyName.equals("artifactId")) {
            requiredElementsElement.addContent(new Text("  "));
            ((Element)requiredElement.getContent(new ElementFilter("defaultValue")).get(0)).setText("${" + SHIM_NAME + "}");
            //requiredElement.setText("${" + SHIM_NAME + "}");
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


    public static void replaceContent(String shimName, String fileName, String shimVendor, String shimEdition) throws IOException {

        if (shimEdition.equals(Edition.CE.getEditionShimName())) {
            replaceCEContent(shimName, fileName, shimVendor);
        } else {
            replaceEEContent(shimName, fileName, shimVendor);
        }
    }

    private static void replaceCEContent(String shimName, String fileName, String shimVendor) throws IOException {

        String fileContent = FileUtils.readFileToString(new File(fileName));
        String replacedContent = fileContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-client-assembly\"", "dir=\"client\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-scope-client-assembly\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-scope-client-assembly\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-scope-client-assembly\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-scope-client-assembly\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-pmr-assembly\"", "dir=\"pmr\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-scope-pmr-assembly\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-scope-pmr-assembly\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-scope-pmr-assembly\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-scope-pmr-assembly\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-default-assembly\"", "dir=\"default\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-scope-default-assembly\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-scope-default-assembly\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-scope-default-assembly\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-scope-default-assembly\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-hbase-comparators\"", "dir=\"hbase-comparators\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-hbase-comparators\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-hbase-comparators\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-hbase-comparators\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-hbase-comparators\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "\"", "dir=\"impl\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-assemblies-reactor\"", "dir=\"assemblies\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-assemblies-reactor\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-assemblies-reactor\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-assemblies-reactor\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-assemblies-reactor\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-package\"", "dir=\"__" + SHIM_NAME + "__-shim\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-package\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-package\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-package\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-package\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-scope-", "dir=\"");

        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-scope-client\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-scope-client\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-scope-client\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-scope-client\"");

        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-scope-default\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-scope-default\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-scope-default\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-scope-default\"");

        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-scope-pmr\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-scope-pmr\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-scope-pmr\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-scope-pmr\"");

        FileUtils.write(new File(fileName), replacedContent, "utf-8");
    }

    private static void replaceEEContent(String shimName, String fileName, String shimVendor) throws IOException {

        String fileContent = FileUtils.readFileToString(new File(fileName));
        String replacedContent = fileContent.replace("dir=\"pentaho-hadoop-shims-" + shimName + "-security\"", "dir=\"impl\"");
        replacedContent = replacedContent.replace("id=\"pentaho-hadoop-shims-" + shimName + "-security\"", "id=\"pentaho-hadoop-shims-" + shimVendor + "-security\"");
        replacedContent = replacedContent.replace("name=\"pentaho-hadoop-shims-" + shimName + "-security\"", "name=\"pentaho-hadoop-shims-" + shimVendor + "-security\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-big-data-ee-" + shimName + "-assemblies-reactor\"", "dir=\"assemblies\"");
        replacedContent = replacedContent.replace("id=\"pentaho-big-data-ee-" + shimName + "-assemblies-reactor\"", "id=\"pentaho-big-data-ee-" + shimVendor + "-assemblies-reactor\"");
        replacedContent = replacedContent.replace("name=\"pentaho-big-data-ee-" + shimName + "-assemblies-reactor\"", "name=\"pentaho-big-data-ee-" + shimVendor + "-assemblies-reactor\"");

        replacedContent = replacedContent.replace("dir=\"pentaho-big-data-ee-" + shimName + "-package\"", "dir=\"__" + SHIM_NAME + "__-ee-shim\"");
        replacedContent = replacedContent.replace("id=\"pentaho-big-data-ee-" + shimName + "-package\"", "id=\"pentaho-big-data-ee-" + shimVendor + "-package\"");
        replacedContent = replacedContent.replace("name=\"pentaho-big-data-ee-" + shimName + "-package\"", "name=\"pentaho-big-data-ee-" + shimVendor + "-package\"");

        FileUtils.write(new File(fileName), replacedContent, "utf-8");
    }

}
