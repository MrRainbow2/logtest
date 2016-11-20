#!/bin/bash

flumedir=$1
bin=`dirname "$0"`
export dir=$bin
echo $dir
export flumebin=$flumedir/bin

sh  $flumedir/bin/flume-ng agent -n agent -c conf -f $dir/flume-kafka.con -Dflume.root.logger=INFO,console
