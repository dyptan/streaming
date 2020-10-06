FROM ubuntu

# Install packages
RUN apt-get update && \
apt-get install -y curl

COPY docker-entrypoint.sh /

ENTRYPOINT ["/docker-entrypoint.sh"]