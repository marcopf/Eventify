-you can configure server options in ./Eventify/.env file
-you can configure database options in ./mysql/.env file
-you can configure client options in docker-compose.yml file, the only option available
	 is setting a different port for the client
-don't delete any variables, change only the values in order to fit your needings

To launch the application:
cd ./front
bash build.sh "http://<your ip adress>" "<server port>"
cd ..
sudo bash adminconf.sh <username> <first_name> <last_name> <email> <yyyy-MM-dd> <password>
sudo bash config.sh
sudo docker compose up
-after set up of mysql
sudo docker exec -it <container id> mysql -u<MYSQL_USER> -p

To launch the application in demo mode set the DEMO_APPLICATION to 'true' in ./Eventify/.env
then:
cd ./front
bash build.sh "http://<your ip adress>" "<server port>"
cd ..
sudo bash adminconf.sh <username> <first_name> <last_name> <email> <yyyy-MM-dd> <password>
sudo bash config.sh demo
sudo docker compose up
-after set up of mysql
sudo docker exec -it <container id> mysql -u<MYSQL_USER> -p
