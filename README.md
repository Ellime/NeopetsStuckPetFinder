# Neopets Stuck Pet Finder

A script that finds Neopets in the Pound. Written in Java SE 8 and uses jsoup 1.13.1.

A fanmade guide to Neopets and stuck pets is at www.neopets.com//~Lorrenn.

## Setup

1. Download or clone the repository.
1. Install jsoup: https://jsoup.org/download
1. Include the jsoup .jar into your build path.
1. In the root directory create a `settings.txt` file 

## User guide

Running the script allows you to input a few options:

```
0: Search in the pound
1: View settings
2: Toggle iteration/random
3: Set qty. strings
4: Set start (iteration only)
5: Set string length
6: Set core
7: Toggle letters
8: Toggle numbers
9: Toggle underscore
10: Save settings
11: Load settings
```

You can save your settings in by creating a `settings.txt` at the root level. The settings are read in the order shown in the menu.

You can provide login credentials by creating a `credentials.txt` at the root level. The script, on start, will first attempt to log you in. This isn't necessary if you are already logged into your computer's default browser.

## How it works

The script takes your settings as what rules to follow to generate names. It then sends requests to the pound search engine one at a time.

A pet is identified by parsing the response. The name, color, and species is stored in `results.txt`.

There is no distinction between pets that are stuck vs. in the pound normally but the intent is you can search for desirable names which would normally be quickly adopted unless if they are stuck.

## Disclaimer

This was designed in 2020 when Neopets was owned by Jumpstart and was still using the HTTP protocol. 
As of 2024 Neopets has started to modernize and has better protections against DoS so this script no longer works which is why I am comfortable sharing it.

This is a fanmade project and not officially acknowledged by Neopets.com.

This project is NOT under any open-source license.