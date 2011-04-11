To install jars for a new service, for example Buzz:

mvn install:install-file -Dfile=buzz-1.0.0-alpha-SNAPSHOT.jar -Dsources=buzz-1.0.0-alpha-SNAPSHOT-sources.jar -DcreateChecksum=true -DpomFile=buzz-1.0.0-alpha-SNAPSHOT.pom

then copy over files from ~/.m2/repository/com/google/api/client/buzz
