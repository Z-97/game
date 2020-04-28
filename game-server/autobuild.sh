#!/bin/bash

cd /Users/lsl/workprojects/game/
gradle clean
gradle build -x test

ssh -p 3322 root@172.16.10.120 "cd /root/deploy/game && rm -rf /root/deploy/game/*" 

cd game-server

#拷贝到外网测试服
#scp -P 235 build/distributions/game-server-1.0.0.tar root@172.16.10.172:/data/gameserver/
#拷贝到内网测试服
#scp build/distributions/game-server-1.0.0.tar root@172.16.10.122:/data/gameserver/
#拷贝到内网运维服
scp -P 3322 build/distributions/game-server-1.0.0.tar root@172.16.10.120:/root/deploy/game/
scp -P 3322 /Users/lsl/workprojects/game/game-server/readme.txt root@172.16.10.120:/root/deploy/game/
scp -P 3322 -r /Users/lsl/workprojects/game/game-server/sql/* root@172.16.10.120:/root/deploy/game/
scp -P 3322 ddzai_deploy.zip root@172.16.10.120:/root/deploy/game/

cd ../game-robot

#拷贝到外网测试服
#scp -P 235 build/distributions/game-robot-1.0.0.tar root@172.16.10.172:/data/gameserver/
#拷贝到内网测试服/Users/lsl/workprojects/game/game-server/autobuild.sh
#scp build/distributions/game-robot-1.0.0.tar root@172.16.10.122:/data/gameserver/
#拷贝到内网运维服
scp -P 3322 build/distributions/game-robot-1.0.0.tar root@172.16.10.120:/root/deploy/game/

