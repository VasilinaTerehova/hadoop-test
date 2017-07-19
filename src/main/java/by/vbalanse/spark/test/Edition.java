package by.vbalanse.spark.test;

/**
 * Created by Aliaksandr_Zhuk on 7/18/2017.
 */
public enum Edition {
    CE("ce"),
    EE("ee");

    private final String editionShimName;

    Edition(String editionShimName) {
        this.editionShimName = editionShimName;
    }

    public String getEditionShimName() {
        return this.editionShimName;
    }

    public static String getEditionShimNameByString(String editionShimName) {
            return Edition.valueOf(editionShimName.toUpperCase()).name();
    }
}
