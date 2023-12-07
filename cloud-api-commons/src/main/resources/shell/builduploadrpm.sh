#!/bin/bash  
if [ $# -gt 4 ]; then  
    echo "######error.. Illegal parameters. only need params -v version_str and -b build_number"  
    exit 1  
fi  

p_version=""
p_buildnumber=""
v_project_name=WBXmatsfraudpolicyapiservice
v_rmc_dir=\fraud/centos7_noarch/
FIRST_YEAR_OF_ANTI_FRAUD_SERVICE=2017

## Start to process the params  
while getopts ":v:b:" opt  
do  
   case $opt in  
      v ) p_version=$OPTARG  
         ;;  
      b ) p_buildnumber=$OPTARG
         ;;
      ? ) echo "error unkown param"  
         exit 1
         ;;  
    esac  
done  
shift $(($OPTIND - 1)) 

echo "###### According to script params, Version will be $p_version and Build_number will be $p_buildnumber..."

#cd /opt/webex/dev3/cloudtools-mats/WBXmatsfraudpolicyapiservice
#echo "###### Dir changed to `pwd`"

echo "###### Check out code from git remote repo..."
git reset --hard
git pull
echo "3. maven building ..."
tyear=`date +%y`
tmonth=`date +%m`
month=`expr $tmonth + 0`
tday=`date +%d`
year=`expr $tyear + '2000'`
if [[ ${p_version} == "" ]]; then
    p_version=$[year-FIRST_YEAR_OF_ANTI_FRAUD_SERVICE]'.'$month
fi
echo "###### param version ${p_version} and build ${p_buildnumber}"
if [[ ${p_buildnumber} == "" ]]; then
      now_date=$year$tmonth$tday
      sudo su -c "yum clean all"
      LATEST_P_BUILDNUMBER=`sudo su -c 'yum list WBXmatsfraudpolicyapiservice-'${p_version} | sed -n '$p' | sed 's/\s\+/ /g' | cut -d' ' -f 2 | cut -d'-' -f 2 | cut -d'_' -f 2`
      echo "LATEST_P_BUILDNUMBER is ${LATEST_P_BUILDNUMBER}"
      if [[ ${LATEST_P_BUILDNUMBER} == "*" ]]; then
            p_buildnumber=$now_date'_1'
      else
            p_buildnumber=$now_date'_'$((${LATEST_P_BUILDNUMBER}+1))
      fi
fi

echo "###### Will use $p_version as RPM version and $p_buildnumber as RPM build number..."
echo "###### Start to build RPM package ${v_project_name} with version ${p_version} and build ${p_buildnumber} ..."
mvn clean package -Dmaven.rpm.phase=package -Dmaven.build.number=${p_buildnumber} -Drpm.build.version=${p_version} -Dmaven.test.skip=true 

v_target_rpm_file=target/rpm/${v_project_name}/RPMS/noarch/${v_project_name}-*.rpm

v_target_rpm_filename=`ls ${v_target_rpm_file} |cut -d'/' -f6`

echo "###### Successfully built the RPM package ${v_target_rpm_filename} ..."

v_token=`curl -X POST https://idbroker.webex.com/idb/oauth2/v1/access_token -H 'cache-control: no-cache' -H 'content-type: application/x-www-form-urlencoded' -d 'grant_type=client_credentials&client_id=C60088e8dab3b6d779acee0e3426230b7ac736fec361db9ef4adb4141a0837621&client_secret=ee39266122a87acc5325fc68cc90c47cf7e7818e463d6bc72981f69cb5cc4bf9&scope=Identity:SCIM Identity:Organization prod-rmc:prod_rmc_api'|jq -r '.access_token'`
echo "###### Successfully get CI token"
v_sha256=`sha256sum target/rpm/${v_project_name}/RPMS/noarch/${v_target_rpm_filename}`
echo "###### RPM package hash is ${v_sha256}"
curl https://rmc.webex.com/api/package/${v_rmc_dir} -H "Authorization: ${v_token}" -F "file=@target/rpm/${v_project_name}/RPMS/noarch/${v_target_rpm_filename}" -F "sha256=${v_sha256}"
echo "###### Successfully uploaded the RPM package ${v_target_rpm_filename} to RMC under directory ${v_rmc_dir} "

#echo "sleep 60 seconds for RMC to recognize the package"
#sleep 60
#curl --data "fileName=${v_target_rpm_filename}&newStatus=Prod" https://rmc.webex.com/change-status
#echo "###### Changed RPM package ${v_target_rpm_filename} status to Prod in RMC..."
		
echo "### Result: SUCCESS. ###"