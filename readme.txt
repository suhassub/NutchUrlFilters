Team Details-

1)Suhas Subramanya
2)Shantanu Bal
3)Mansi Negi
4)Jai Khanna
5)Pranshu Kumar

—————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————


The Nutch Configuration Folder contains 3 files which we modified for our crawl.
1.nutch-default.xml
2.nutch-site.xml
3.regex-urlfilter.txt
—————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————


Q6.a.
Running The Exact Duplicates Algorithm-
—————————————————————————————————————————
1) Run the following commands-
bin/nutch mergesegs <OutputDIR_1> -dir <PATH_TO_SEGMENTS>

bin/nutch readseg -dump <<PATH_TO_SEGMENTS_OF_OutputDIR_1> <OutputDIR_2> -nocontent -nofetch -nogenerate -noparse -noparsedata

2) Copy the “dump” file into the folder where the exactduplicates.java will be copied.

3)run and execute the program.
		
—————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————


Q6.b.
Running The Near Duplicates Algorithm-
—————————————————————————————————————————

1) Run the following commands-
bin/nutch mergesegs <OutputDIR_1> -dir <PATH_TO_SEGMENTS>

bin/nutch readseg -dump <<PATH_TO_SEGMENTS_OF_OutputDIR_1> <OutputDIR_2> -nocontent -nofetch -nogenerate -noparse -noparsedata

2) Copy the “dump” file into the folder where the nearduplicates.java and MurmurHash.java will be copied.

3)run and execute the program.

—————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

**Details about URLFilter - 
Purpose - The URLFilter gets the contents of the URL and checks for near or exact duplicates based on the extracted text and stores the unique hash values into the file.

Q7.a.
Running the URLFilter for exact duplicates-
————————————————————————————————————————————


1)copy and paste the “urlfilter-exactdedup” folder to nutch-trunk/src/plugin folder.

2)add the tags below to “nutch-site.xml” for enabling exactdedup plugin

<property>
  <name>plugin.includes</name>
  <value>protocol-http|urlfilter-regex|parse-(html|tika)|index-(basic|anchor)|scoring-opic|urlnormalizer-(pass|regex|basic)|urlfilter|urlfilter-exactdedup</value>
  <description>Regular expression naming plugin directory names to
  include.  Any plugin not matching this expression is excluded.
  In any case you need at least include the nutch-extensionpoints plugin. By
  default Nutch includes crawling just HTML and plain text via HTTP,
  and basic indexing and search plugins.
  </description>
</property>

3) add the tags below to “build.xml” in nutch-trunk/src/plugin folder.

<ant dir="urlfilter-exactdedup" target="deploy"/> 

4) build and run the program using ant and ant runtime in the trunk folder.

NOTE: For every fresh crawl delete the file exactdup.txt created through the filter.


—————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————-

Q7.b.
Running the URLFilter for near duplicates- 
————————————————————————————————————————————

*NOTE : we are using extracted text instead of extracted metadata. After discussing with Prof Mattmann.


1)copy and paste the “urlfilter-neardedup” folder to nutch-trunk/src/plugin folder.

2)add the tags below to “nutch-site.xml” for enabling neardedup plugin

<property>
  <name>plugin.includes</name>
  <value>protocol-http|urlfilter-regex|parse-(html|tika)|index-(basic|anchor)|scoring-opic|urlnormalizer-(pass|regex|basic)|urlfilter|urlfilter-neardedup</value>
  <description>Regular expression naming plugin directory names to
  include.  Any plugin not matching this expression is excluded.
  In any case you need at least include the nutch-extensionpoints plugin. By
  default Nutch includes crawling just HTML and plain text via HTTP,
  and basic indexing and search plugins.
  </description>
</property>

3) add the tags below to “build.xml” in nutch-trunk/src/plugin folder.

<ant dir="urlfilter-neardedup" target="deploy"/> 

4) build and run the program using ant and ant runtime in the trunk folder.

NOTE: For every fresh crawl delete the file neardup.txt created through the filter.

————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————


