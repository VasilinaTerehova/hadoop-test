package by.vbalanse.spark.test;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * Created by Aliaksandr_Zhuk on 7/18/2017.
 */
public class PomUpdater {

    static {
        PropertyReader.initPropertyReader();
    }

    public static void updateShimPom(String pathToPom, String shimVendor, String edition) throws IOException, JDOMException {

        Path pomPath = Paths.get(pathToPom, "pom.xml");

        addTags(pomPath.toString(), edition);

        Document documentFromFile = XmlUtilsJDom1.getDocumentFromFile(pomPath.toString());
        Element rootElement = documentFromFile.getRootElement();

        updateTags(rootElement, shimVendor, edition);
        deleteTags(rootElement);

        XmlUtilsJDom1.outputDoc(documentFromFile, pomPath.toString());
    }

    private static void deleteTags(Element root) {
        for (DeletedTags tag : DeletedTags.values()) {
            root.removeContent(new ElementFilter(tag.getDeletedTagName()));
        }
    }

    private static void updateTags(Element root, String shimVendor, String edition) {

        String property = "";
        String propertyPrefix = Edition.getEditionShimNameByString(edition);

        for (UpdatedTags tag : UpdatedTags.values()) {
            property = PropertyReader.getProperty(propertyPrefix + "." + tag.getUpdatedTagName());
            property = MessageFormat.format(property, shimVendor.toUpperCase(), shimVendor.toLowerCase());
            ((Element) root.getContent(new ElementFilter(tag.getUpdatedTagName())).get(0)).setText(property);
        }
    }

    private static void addTags(String pomPath, String edition) throws IOException {

        try {
            File file = new File(pomPath.toString());
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileInputStream(file));

            addParentTag(edition, model);

            OutputStream stream = new FileOutputStream(file);
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));

            new MavenXpp3Writer().write(writer, model);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private static void addParentTag(String edition, Model model) {

        String propertyPrefix = Edition.getEditionShimNameByString(edition);

        Parent parent = new Parent();
        parent.setArtifactId(PropertyReader.getProperty(propertyPrefix + ".artifactParentId"));
        parent.setGroupId(PropertyReader.getProperty(propertyPrefix + ".groupId"));
        parent.setVersion(PropertyReader.getProperty(propertyPrefix + ".version"));

        model.setParent(parent);
    }
}
