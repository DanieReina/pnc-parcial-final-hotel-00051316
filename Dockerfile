FROM ubuntu:latest
LABEL authors="daniv"

ENTRYPOINT ["top", "-b"]