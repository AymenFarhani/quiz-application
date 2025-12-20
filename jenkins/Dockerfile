FROM jenkins/jenkins:lts-jdk21

USER root

# Install needed tools
RUN apt-get update && \
    apt-get install -y \
        docker.io \
        maven \
        git \
        curl && \
    apt-get clean

# Allow Jenkins to access Docker socket
RUN usermod -aG docker jenkins

USER jenkins
