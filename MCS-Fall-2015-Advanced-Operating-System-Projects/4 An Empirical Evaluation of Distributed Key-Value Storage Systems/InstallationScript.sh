clear
echo "Installing Java"
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
echo "Installing MongoDb"
sudo apt-get install oracle-java8-installer
sudo apt-get install -y mongodb-org-server mongodb-org-shell mongodb-org-tools
sudo rm /var/lib/dpkg/lock
sudo apt-get install mongodb
sudo service mongod start
sudo apt-get install mongodb-clients
echo "Installing CouchDB"
sudo apt-get install couchdb
echo "Installing FileZilla"
sudo apt-get update
sudo apt-get install filezilla
echo "Installing Redis"
sudo apt-get upgrade
sudo apt-get update
sudo apt-get install redis-server
echo "Installing PSSH"
sudo apt-get update
sudo apt-get install pssh
exit 1


