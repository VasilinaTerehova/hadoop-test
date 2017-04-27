mvn archetype:create-from-project -Darchetype.properties="..\archetype.properties" -Darchetype.preserveCData=true
mvn archetype:generate -B -DarchetypeGroupId=org.pentaho.hadoop.shim.archetype -DarchetypeArtifactId=emr -DarchetypeVersion=1.0-SNAPSHOT -Dversion=8.0-SNAPSHOT -DshimName=emr540 –DshimVersion=5.4.0
