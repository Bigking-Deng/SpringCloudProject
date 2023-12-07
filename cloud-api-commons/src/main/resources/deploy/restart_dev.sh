path=`pwd`
echo 'operate restart WBXmatsfraudbackendservice: '$path
pid=`ps aux | grep $path | grep -v grep | grep -E "WBXmatsfraudbackendservice" | awk '{print $2}'`
echo 'exist pid:'$pid

if [ -n "$pid" ]
then
{
   kill -9 $pid
   sleep 2
}
fi

cd $path
[ -d logs ] || mkdir logs

cd $path/target
rm -rf META-INF org BOOT-INF
unzip -o WBXmatsfraudbackendservice*.jar 1>/dev/null
cd BOOT-INF/classes
#nohup java -cp ".:../lib/*" -jar "../../WBXmatsfraudbackendservice-2.6.0.jar" -Ddspath=$path com.webex.mats.fraud.WBXmatsfraudbackendservice > $path/fb.log &
nohup java -cp ".:../lib/*" -jar "../../WBXmatsfraudbackendservice-2.6.0.jar" --spring.config.additional-location=classpath:conf/secret.properties -Ddspath=$path com.webex.mats.fraud.WBXmatsfraudbackendservice > $path/fb.log 2>&1 &