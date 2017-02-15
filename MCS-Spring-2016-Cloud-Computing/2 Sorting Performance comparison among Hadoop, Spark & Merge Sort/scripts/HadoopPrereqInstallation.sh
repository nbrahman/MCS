clear
echo "Installing mdadm"
sudo apt-get update
sudo apt-get install mdadm

echo "changing to raid 0"
lsblk
sudo mdadm --create --verbose /dev/md0 --level=0 --name=Cloud_PA2 --raid-devices=3 /dev/xvdb /dev/xvdc /dev/xvdd
sudo mkfs.ext4 -L Cloud_PA2 /dev/md0
sudo mkdir -p /mnt/raid
sudo mount LABEL=Cloud_PA2 /mnt/raid

echo "Installing Java"
sudo apt-get install default-jdk
java -version

echo "Installing ant"
sudo apt-get install ant

echo "Installing make"
sudo apt-get isntall make

echo "Installing C++"
sudo apt-get install g++

echo "Installing SSH"
sudo apt-get install ssh
which ssh
which sshd

echo "Installing PSSH"
sudo apt-get update
sudo apt-get install pssh

echo "Creating and configuring HDFS user"
sudo addgroup hadoop
sudo adduser --ingroup hadoop hduser
sudo adduser hduser sudo

