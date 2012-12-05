1) CODE STRUCTURE:
iiit/cloud/concepts/Seed.java
iiit/cloud/concepts/Lookup.java
iiit/cloud/concepts/Map.java
iiit/cloud/concepts/Catalog.java
iiit/cloud/concepts/Logger.java

2) STEPS TO RUN CODE:
$ sudo apt-get install mongodb-10gen
$ java -cp Code.jar iiit.cloud.concepts.Seed init.conf
$ # Place the documents under the directory (/smadhapp/docs) and then run hadoop
$ hadoop jar Code.jar iiit.cloud.concepts.Lookup

3) METRICS DOC:
Please refer the attached folder 'PerfMetrics' for performance metrics collected.

4) ASSUMPTION ABOUT THE PROJECT ENVIRONMENT/CONFIGURATION:
Please refer the attached poster for 
-> Hardware/Software Configuration
-> Dataset Used
-> Future Work