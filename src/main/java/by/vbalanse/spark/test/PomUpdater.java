package by.vbalanse.spark.test;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jdom.Content;
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

        PropertyReader propertyReader = new PropertyReader();
        String property = "";
        String propertyPrefix = (edition.equals(Edition.CE.name().toLowerCase()) ? Edition.CE.name() : Edition.EE.name());

        for (UpdatedTags tag : UpdatedTags.values()) {
            property = PropertyReader.getProperty(propertyPrefix + "." + tag.getUpdatedTagName());
            property = MessageFormat.format(property, shimVendor.toUpperCase(), shimVendor.toLowerCase());
            ((Element) root.getContent(new ElementFilter(tag.getUpdatedTagName())).get(0)).setText(property);
        }
    }

    private static void addTags(String pomPath, String edition) throws IOException {

        PropertyReader propertyReader = new PropertyReader();
        String groupId = "";
        String artifactParentId = "";
        String version = "";
        String propertyPrefix = (edition.equals(Edition.CE.getEditionShimName()) ? Edition.CE.name() : Edition.EE.name());

        groupId = PropertyReader.getProperty(propertyPrefix + ".groupId");
        artifactParentId = PropertyReader.getProperty(propertyPrefix + ".artifactParentId");
        version = PropertyReader.getProperty(propertyPrefix + ".version");

        try {
            File file = new File(pomPath.toString());
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileInputStream(file));

            Parent parent = new Parent();
            parent.setArtifactId(artifactParentId);
            parent.setGroupId(groupId);
            parent.setVersion(version);

            model.setParent(parent);

            OutputStream stream = new FileOutputStream(file);
            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));

            new MavenXpp3Writer().write(writer, model);
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
    }
}
