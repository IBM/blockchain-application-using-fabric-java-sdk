#!/bin/bash
#
# Exit on first error, print all commands.
set -e

# Shut down the Docker containers for the system tests.
docker-compose -f docker-compose.yml kill && docker-compose -f docker-compose.yml down

# remove chaincode docker images
docker rmi $(docker images dev-* -q)

# Your system is now clean
