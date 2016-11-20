#!/bin/sh

inputdir=$1
tablename=$2
array=(`hdfs dfs -ls -R $inputdir | awk '{if ($NF ~ /Finished$/) print $NF}'`)
for i in ${array[@]} ; do
  year=`echo $i | awk -F "/" '{print $(NF-3)}'`
  month=`echo $i | awk -F "/" '{print $(NF-2)}'`
  day=`echo $i | awk -F "/" '{print $(NF-1)}'`
  month=`echo $month | sed 's/^0*//'`
  day=`echo $day | sed 's/^0*//'`

  echo "LOAD DATA INPATH '$i' INTO TABLE $tablename PARTITION (year=$year, month=$month, day=$day)"
  hive -e "LOAD DATA INPATH '$i' INTO TABLE $tablename PARTITION (year=$year, month=$month, day=$day)"
done 
