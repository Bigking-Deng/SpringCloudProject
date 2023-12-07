path=`pwd`
echo 'operate restart WBXmatsfraudpolicyapiservice: '$path
pid=`ps aux | grep $path | grep -v grep | grep -E "WBXmatsfraudpolicyapiservice" | awk '{print $2}'`
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
unzip -o WBXmatsfraudpolicyapiservice*.jar 1>/dev/null
cd BOOT-INF/classes
nohup java -cp ".:../lib/*" -Ddspath=$path com.webex.mats.fraud.policy.WbXmatsfraudpolicyapiserviceApplication > $path/fpo.log &
