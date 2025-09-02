FROM mcr.microsoft.com/devcontainers/universal:latest

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
    # Make Tools
    gcc \
    g++ \
    cmake \
    build-essential \
    # ML Dependencies
    nvidia-cuda-toolkit \
    clinfo \
    tf \
    # Python Tools
    python3-colcon-common-extensions \
    python3-pip \
    "

RUN apt update && apt install -y --no-install-recommends ADDITIONAL_APT_PACKAGES

RUN echo root:robotics | chpasswd

RUN echo "PermitRootLogin yes" >> /etc/ssh/sshd_config && \
    echo "X11UseLocalhost no" >> /etc/ssh/sshd_config && \
    echo "PermitUserEnvironment yes" >> /etc/ssh/sshd_config && \
    mkdir -p /root/.ssh && \
    touch /root/.ssh/environment

RUN echo "Port 2201" >> /etc/ssh/sshd_config

RUN curl -LsSf https://astral.sh/uv/install.sh | sh

ENTRYPOINT service ssh restart && bash
