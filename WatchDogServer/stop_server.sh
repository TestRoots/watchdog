#!/bin/sh

ps aux | grep 'unicorn' | awk '{print $2}' | xargs kill -9
