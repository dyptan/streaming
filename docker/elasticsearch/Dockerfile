FROM docker.elastic.co/elasticsearch/elasticsearch-oss:6.2.4
ADD template.json /usr/share/elasticsearch/config/templates/
RUN echo "&& sleep 10; curl -H 'Content-Type: application/json' -XPUT -d @/usr/share/elasticsearch/config/templates/template.json 'http://localhost:9200/_template/template_1'" >> /usr/local/bin/docker-entrypoint.sh