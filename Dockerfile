FROM mcr.microsoft.com/vscode/devcontainers/universal:latest

ARG ADDITIONAL_APT_PACKAGES=" \
# General Utilities
    sudo \
    openssh-server \
    xauth \
    snapd \
    unzip \
    wget \
    curl \
    vim \
    git \
    tmux \
"

RUN apt-get update && apt-get install ADDITIONAL_APT_PACKAGES

RUN curl -fsSL https://pixi.sh/install.sh | sh

RUN echo "PermitRootLogin yes" >> /etc/ssh/sshd_config && \
    echo "X11UseLocalhost no" >> /etc/ssh/sshd_config && \
    echo "PermitUserEnvironment yes" >> /etc/ssh/sshd_config && \
    mkdir -p /root/.ssh && \
    touch /root/.ssh/environment

RUN echo "Port 2201" >> /etc/ssh/sshd_config

RUN mkdir -p /workspaces/ip/

ENTRYPOINT service ssh restart && bash