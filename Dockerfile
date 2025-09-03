FROM mcr.microsoft.com/devcontainers/universal:linux

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

RUN apt-get update && apt-get install -y ${ADDITIONAL_APT_PACKAGES}

RUN curl -fsSL https://pixi.sh/install.sh | sh \
    && . ~/.bashrc \
    && pixi init && pixi add openjdk=17

RUN echo "Port 2201" >> /etc/ssh/sshd_config

RUN mkdir -p /workspaces/ip/