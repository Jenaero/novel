#spider-shell
#!/bin/bash
#export BUILD_ID=dontKillMe这一句很重要，项目启动之后才不会被jenkins杀掉。
export BUILD_ID=dontKillMe
# copy war到编译好的文件到tomcat目录的webaaps
TOMCAT_HOME="/web/novel-spider/tomcat"
# 获取pid
GETPID="ps -ef | grep novel-spider | grep -v grep | awk '{print \$2}'"
pid=$(sh -c "$GETPID")
echo "need to be killed pid is : $pid"
kill -9 $pid
echo '进入webapps下删除war包'
cd $TOMCAT_HOME/webapps/ && rm -rf spider*
# copy war到编译好的文件到tomcat目录的webaaps
cp -rf /var/lib/jenkins/workspace/novel-spider/novel-spider/target/novel-spider-0.0.1-SNAPSHOT.war /web/novel-spider/tomcat/webapps
cp -rf /var/lib/jenkins/workspace/novel-spider/novel-service/src/main/resources/rule/Spider-Rule.xml /web/novel-spider/tomcat/webapps

mv novel-spider-0.0.1-SNAPSHOT.war spider.war
cd ../bin
./startup.sh
echo 'sleep 3s'
sleep 3s
newpid=$(sh -c "$GETPID")
echo "newpid $newpid"
if test "$newpid"
then
     echo "hsae tomcat restart success, pid is : $newpid"
     exit
else
     echo 'failed to restart'
     exit 1
fi


#web-shell
#!/bin/bash
#export BUILD_ID=dontKillMe这一句很重要，项目启动之后才不会被jenkins杀掉。
export BUILD_ID=dontKillMe
# copy war到编译好的文件到tomcat目录的webaaps
TOMCAT_HOME="/web/novel-web/tomcat"
# 获取pid
GETPID="ps -ef | grep novel-web | grep -v grep | awk '{print \$2}'"
pid=$(sh -c "$GETPID")
echo "need to be killed pid is : $pid"
kill -9 $pid
echo '进入webapps下删除war包'
cd $TOMCAT_HOME/webapps/ && rm -rf novel*
# copy war到编译好的文件到tomcat目录的webaaps
cp -rf /var/lib/jenkins/workspace/novel-web/novel-web/target/novel-web-0.0.1-SNAPSHOT.war /web/novel-web/tomcat/webapps
cp -rf /var/lib/jenkins/workspace/novel-web/novel-service/src/main/resources/rule/Spider-Rule.xml /web/novel-spider/tomcat/webapps
mv novel-web-0.0.1-SNAPSHOT.war novel.war
cd ../bin
./startup.sh
echo 'sleep 3s'
sleep 3s
newpid=$(sh -c "$GETPID")
echo "newpid $newpid"
if test "$newpid"
then
     echo "hsae tomcat restart success, pid is : $newpid"
     exit
else
     echo 'failed to restart'
     exit 1
fi