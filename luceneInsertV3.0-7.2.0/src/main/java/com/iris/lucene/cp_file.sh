#!/bin/bash
file_name=$1
path="/data/luceneInfoDir/"
#file_name="/data/audit/152326521619720180323_3k.ini"
echo "filename:$file_name"
if [ ! -f "$file_name" ];then
echo "source file not exit"
exit
fi
count=0
while true;do
        cur_path=$path$file_name$count
        cp -f $file_name $cur_path;
        echo $cur_path
    sleep 1.5
        ((count++))
done
