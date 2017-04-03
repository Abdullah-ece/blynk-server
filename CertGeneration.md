sudo apt-get install git
git clone https://github.com/certbot/certbot && cd certbot

sudo iptables -t nat -D PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 9999
export LC_ALL="C"
./certbot-auto certonly --agree-tos --email dmitriy@blynk.cc --standalone --standalone-supported-challenges http-01 --http-01-port 9999 -d knight-qa.blynk.cc
sudo iptables -t nat -D PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 9999
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

server.properties changes :

server.ssl.cert=/etc/letsencrypt/live/knight-qa.blynk.cc/fullchain.pem
server.ssl.key=/etc/letsencrypt/live/knight-qa.blynk.cc/privkey.pem
server.ssl.key.pass=