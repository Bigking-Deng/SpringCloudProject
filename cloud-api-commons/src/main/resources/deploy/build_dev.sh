#!/bin/sh
ARGS="$@"

#######
get_server_type()
{
    LOCAL_IP=`sudo su -c ifconfig | grep "inet" | grep -v "127.0.0.1" | awk -F" " '{print $2}' | awk '{print $1}'`

    echo "local IP = $LOCAL_IP"

    echo "$LOCAL_IP" | grep "10.100.13.170" > /dev/null
    if [ $? -eq 0 ]; then
        echo "10.100.13.170"
    fi

    echo "$LOCAL_IP" | grep "10.100.13.171" > /dev/null
    if [ $? -eq 0 ]; then
        echo "10.100.13.171"
        return 1
    fi

    echo "$LOCAL_IP" | grep "10.100.13.172" > /dev/null
    if [ $? -eq 0 ]; then
        echo "10.100.13.172"
        return 2
    fi

    echo "$LOCAL_IP" | grep "10.100.13.173" > /dev/null
    if [ $? -eq 0 ]; then
        echo "10.100.13.173"
        return 3
    fi

    echo "$LOCAL_IP" | grep "10.100.13.174" > /dev/null
    if [ $? -eq 0 ]; then
        echo "10.100.13.174"
        return 4
    fi

    echo "$LOCAL_IP" | grep "10.100.13.175" > /dev/null
    if [ $? -eq 0 ]; then
        echo "10.100.13.175"
        return 5
    fi

    echo "$LOCAL_IP" | grep "10.224.76.159" > /dev/null
    if [ $? -eq 0 ]; then
        echo "10.224.76.159"
        return 6
    fi

    echo "UNKNOWN"
    return 7
}

auto_config()
{
	echo "auto configuring web server ..."

	get_server_type
	type=$?

	echo "this server type = $type"

	echo "backup config files ..."
  if [ ! -d backup_path ];then
          mkdir -p backup_path
  fi
	/bin/cp -fv src/main/resources/application.properties src/main/resources/autodeploy/backup/application_BACKUP.properties

	echo "auto configuring ..."
	if [ $type -eq 0 ]; then
	    /bin/cp -fv src/main/resources/autodeploy/application_171_${path:11:4}.properties src/main/resources/application.properties
	elif [ $type -eq 1 ]; then
	    /bin/cp -fv src/main/resources/autodeploy/application_171_${path:11:4}.properties src/main/resources/application.properties
	elif [ $type -eq 2 ]; then
	    /bin/cp -fv src/main/resources/autodeploy/application_172_${path:11:4}.properties src/main/resources/application.properties
	elif [ $type -eq 3 ]; then
	    /bin/cp -fv src/main/resources/autodeploy/application_173_${path:11:4}.properties src/main/resources/application.properties
	elif [ $type -eq 4 ]; then
	    /bin/cp -fv src/main/resources/autodeploy/application_174_${path:11:4}.properties src/main/resources/application.properties
	elif [ $type -eq 5 ]; then
	    /bin/cp -fv src/main/resources/autodeploy/application_175_${path:11:4}.properties src/main/resources/application.properties
	elif [ $type -eq 6 ]; then
  	    /bin/cp -fv src/main/resources/autodeploy/application_159_${path:11:4}.properties src/main/resources/application.properties
	else
	    echo "error: unknown server type !"
	fi
}

if [ $# -gt 1 ]; then
    echo "Parameter number incorrect!"
    exit 1
fi

# go to current directory
cd `dirname $0`
path=`pwd`

source /etc/profile

echo "updating WBXmatsfraudbackendservice code ..."

echo "cd $path"
cd $path
git reset --hard
git pull


chmod 755 ${path}/build_dev.sh
chmod 777 *.sh

auto_config

echo "maven building ..."
mvn clean
mvn package -Dmaven.test.skip=true -U

echo "restarting WBXmatsfraudbackendservice ..."
./restart_dev.sh
