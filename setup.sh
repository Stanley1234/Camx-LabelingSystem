!/bin/bash
RED='\033[0;31m'
NC='\033[0m'

printf "${RED}UPDATE${NC}\n"
yes | sudo apt-get update
yes | sudo apt-get upgrade 
sleep 3

printf "${RED}UPDATE JDK${NC}\n"
yes | sudo apt-get install openjdk-8-jdk 
sleep 3

printf "${RED}UPDATE MAVEN${NC}\n"
yes | sudo apt-get install maven 
sleep 3

printf "${RED}UPDATE SQL${NC}\n"
printf "${RED}Please set root password as root. Press key to continue.${NC}\n"
read dummy
yes | sudo apt-get install mysql-server
systemctl enable mysql # enable mysql launch when boot
sleep 3

printf "${RED}SETUP MYSQL DATABASE${NC}\n"
QUERIES="GRANT ALL PRIVILEGES ON *.* TO 'test'@'localhost' IDENTIFIED BY 'test';create database labeling;"
LOGIN="echo \"${QUERIES}\" | mysql -u root -proot"
echo "${LOGIN}"
eval "${LOGIN}"

printf "${RED}INSTALLING BACKEND SERVICE ${NC}\n"
mvn install
sleep 3

printf "${RED}STARTING BACKEND SERVER${NC}\n"
cd target/
java -jar "labeling-1.0-SNAPSHOT.jar" 
