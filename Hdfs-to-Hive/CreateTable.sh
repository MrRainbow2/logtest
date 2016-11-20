#!/bin/sh

tablename=$1

hive -e "CREATE TABLE $tablename(time varchar(8),userid varchar(30),query string,pagerank int,clickrank int, site string) PARTITIONED BY (year  int,month int,day int) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'"
