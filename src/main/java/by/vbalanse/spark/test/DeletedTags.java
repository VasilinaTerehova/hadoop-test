package by.vbalanse.spark.test;

/**
 * Created by Aliaksandr_Zhuk on 7/18/2017.
 */
public enum DeletedTags {

    GROUPID("groupId"),
    BUILD("build"),
    URL("url"),
    DEVELOPERS("developers"),
    LICENSES("licenses"),
    SCM("scm"),
    DISTRIBUTIONMANAGEMENT("distributionManagement");

    private final String deletedTagName;

    DeletedTags(String deletedTagName) {
        this.deletedTagName = deletedTagName;
    }

    public String getDeletedTagName() {
        return this.deletedTagName;
    }
}
