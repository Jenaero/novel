#!/bin/bash
# 重启tomcat 并且 删除 webapps 的war包, 作为TOMCAT_HOME环境变量无效  author : xizhi.ding date :2018.05.18
export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.232.b09-0.el7_7.x86_64"
# 设置tomcat 目录
TOMCAT_HOME="/usr/local/software/apache-tomcat-8.5.50"
# 获取pid
GETPID="ps -ef | grep $TOMCAT_HOME | grep -v grep | awk '{print \$2}'"
pid=$(sh -c "$GETPID")
echo "need to be killed pid is : $pid"
kill -9 $pid
echo '进入webapps下删除war包'
cd $TOMCAT_HOME/webapps/ && rm -rf core core.war
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



#!/bin/bash
#export BUILD_ID=dontKillMe这一句很重要，项目启动之后才不会被jenkins杀掉。
export BUILD_ID=dontKillMe

#指定最后编译好的jar存放的位置
final_path=/web/novel-spider


#jenkins中编译好的jar位置
jar_path=/var/lib/jenkins/workspace/demo/novel-spider/target

#jenkins中编译好的名称
jar_name=novel-spider-0.0.1-SNAPSHOT.jar

#获取运行编译好的进程ID，便于我们在重新部署项目的时候先杀掉以前的进程
pid=${cat /web/novel-spider/pid/novel-spider.pid}

#进入指定的编译好的jar的位置
cd ${jar_path}

#将编译好的jar复制到最后指定的位置
cp ${jar_path}/${jar_name} ${final_path}

#进入最后指定存放jar的位置
cd ${final_path}

#杀掉以前可能启动的进程
kill -9 ${pid}

#启动jar,指定SpringBoot的profiles为test,后台启动
java -jar -Dspring.profile.active=test ${jar_name}

#将进程ID存入到novel-spider.pid文件中
echo $! > /web/novel-spider/pid/novel-spider.pid
