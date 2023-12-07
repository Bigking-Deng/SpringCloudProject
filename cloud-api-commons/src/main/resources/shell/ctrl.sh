#!/bin/sh  
. /etc/profile

APP_MAIN=WBXmatsmapbackendservice
APP_JAR_NAME=WBXmatsmapbackendservice.jar
APP_PATH=/opt/webex/mats/WBXmatsmapbackendservice
LOG_PATH=$APP_PATH/logs

cd $APP_PATH

tradePortalPID=0

getTradeProtalPID(){
    javaps=`ps -ef | grep $APP_JAR_NAME | grep -v grep`
    if [ -n "$javaps" ]; then
        tradePortalPID=`echo $javaps | awk '{print $2}'`
    else
        tradePortalPID=0
    fi
}

status(){
	getTradeProtalPID
	if [ $tradePortalPID -ne 0 ]; then
        echo "$APP_MAIN already started(PID=$tradePortalPID)"
        echo "================================================================================================================"
    else
        echo "$APP_MAIN is not running"
        echo "================================================================================================================"
    fi
}

start(){
    getTradeProtalPID
    CONFIG_PATH=--spring.config.location=

	for tradePortalConfigFile in config/application.properties;
	do
	   CONFIG_PATH="$CONFIG_PATH""file:$tradePortalConfigFile"
	done

    echo "================================================================================================================"
    if [ $tradePortalPID -ne 0 ]; then
        echo "$APP_MAIN already started(PID=$tradePortalPID)"
        echo "================================================================================================================"
    else
        echo -n "Starting $APP_MAIN"
        sh -c "cd $APP_PATH"
        if [ -f ${APP_PATH}/nohup.out ]; then
          sh -c "echo \" \" > ${APP_PATH}/nohup.out"
        fi
        sh -c "nohup java -Xms4G -Xmx4G -XX:MaxMetaspaceSize=256M -Xss256K -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/tmp/gc.log -XX:+PrintTenuringDistribution -XX:+PrintHeapAtGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/ -Dapp.home=$APP_PATH -Dlog.path=$LOG_PATH -jar $APP_JAR_NAME $CONFIG_PATH >/dev/null 2>&1 & "
        #sh -c "nohup java -Dapp.home=$APP_PATH -Dlog.path=$LOG_PATH -jar $APP_JAR_NAME $CONFIG_PATH >/dev/null 2>&1 & "
        getTradeProtalPID  
        if [ $tradePortalPID -ne 0 ]; then  
            echo "(PID=$tradePortalPID)...[Success]"  
            echo "================================================================================================================"  
        else  
            echo "[Failed]"  
            echo "================================================================================================================"  
        fi  
    fi  
} 

stop(){  
    getTradeProtalPID  
    echo "================================================================================================================"  
    if [ $tradePortalPID -ne 0 ]; then  
        echo -n "Stopping $APP_MAIN(PID=$tradePortalPID)..."  
        kill -15 $tradePortalPID  
        sleep 2
        getTradeProtalPID  
        if [ $tradePortalPID -ne 0 ]; then  
            kill -9 $tradePortalPID  
        fi
        if [ $? -eq 0 ]; then  
            echo "[Success]"  
            echo "================================================================================================================"  
        else  
            echo "[Failed]"  
            echo "================================================================================================================"  
        fi  
        getTradeProtalPID  
    else  
        echo "$APP_MAIN is not running"  
        echo "================================================================================================================"  
    fi  
}  


case "$1" in
	start)
    {
        start
    }
    ;;
    stop)
    {
        stop
    }
    ;;
    restart)
    {
        stop
        start
    }
    ;;
    status)
        status
    ;;
    *)
    {
        echo -e "\nUsage: $0 [ start | stop | restart | status]"
        echo ""
    }
    ;;
esac

