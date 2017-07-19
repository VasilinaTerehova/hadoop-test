package by.vbalanse.spark.test;

/**
 * Created by Aliaksandr_Zhuk on 7/18/2017.
 */
public enum UpdatedTags {

    ARTIFACTID("artifactId"),
    NAME("name"),
    DESCRIPTION("description");

    private final String updatedTagName;

    UpdatedTags(String updatedTagName) {
        this.updatedTagName = updatedTagName;
    }

    public String getUpdatedTagName() {
        return this.updatedTagName;
    }
}
