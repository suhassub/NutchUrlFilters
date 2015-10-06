#Script to fetch the Mime types
import nutchpy
import os
import sys

if len(sys.argv) < 2:
	print "Error! Please run 'python getMime.py <crawldb>'"
	exit(0)

if sys.argv[-1] == "/":
	sys.argv = sys.argv[:-1]
node_path = sys.argv[1] + "/current/part-00000/data"
seq_reader = nutchpy.sequence_reader
dict={}

# traversing the file line-by-line
for each in seq_reader.read(node_path):
	row=each[1]
	if(row.find("Content-Type") == -1):
                continue 
        else:
                content=row[row.index("Content-Type"):]
                mime=content.split("\n")[0].split("=")[1]
                #print mime 
                if mime not in dict:
                        dict[mime]=1
for each in dict:
	print each	


