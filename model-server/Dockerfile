FROM registry.access.redhat.com/ubi8/openjdk-11:1.15-1.1682059493
WORKDIR /usr/modelix-model
EXPOSE 28101
COPY run-model-server.sh /usr/modelix-model/
COPY build/dependency-libs/ /usr/modelix-model/model-server/build/libs/
COPY build/libs/ /usr/modelix-model/model-server/build/libs/
CMD ["./run-model-server.sh"]
