#!/bin/sh
ST_DIR=studentdirs

for f in `ls $ST_DIR` 
do
	./test.sh $ST_DIR/$f
done

