#!/bin/bash
file_name=$1
path="/data/luceneInfoDir/auditRecord1/"
path2="/data/luceneInfoDir/auditRecord2/"

echo "filename:$file_name"
if [ ! -f "$file_name" ];then
echo "source file not exit"
exit
fi

count=0

while true;do
        cur_path=$path$file_name$count
        cur_path2=$path2$file_name$count
        cp -f $file_name $cur_path;
        cp -f $file_name $cur_path2;
        echo $cur_path
        echo $cur_path2
    sleep 0.3
        ((count++))
done
