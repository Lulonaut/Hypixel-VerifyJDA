import argparse
import os

# Functions


def clear_file():
    choice = input(
        "The existing file contains data, do you want to override it? (Y/n) ")
    if(choice == "n" or choice == "no"):
        print("Please clean the file and run the config again.")
        exit()

    if(choice == "Y" or choice == "y" or choice == ""):
        print("OK, overiding it.")
        return
    else:
        print("Invalid Input, try again.")
        clear_file()


def is_correct():
    choice = input("Is this correct? (Y/n) ")
    if(choice == "n" or choice == "no"):
        print("Please run the config again")
        exit()

    if(choice == "Y" or choice == "y" or choice == ""):
        print("OK, saving config to file.")
        return
    else:
        print("Invalid Input, try again.")
        is_correct()


def endpoint_choice():
    api_endpoint = ENDPOINT_HYPIXEL
    print("No API Endpoint passed, please choose one of the following (type the number or press ENTER to choose the default):\n"
          + "[1] Slothpixel\n"
          + "[2] Hypixel (default)\n"
          + "[3] Hypixel 2")
    choice = input()
    if(choice == ""):
        api_endpoint = ENDPOINT_HYPIXEL
    elif(choice == "1"):
        api_endpoint = ENDPOINT_SLOTHPIXEL
    elif(choice == "2"):
        api_endpoint = ENDPOINT_HYPIXEL
    elif(choice == "3"):
        api_endpoint = ENDPOINT_HYPIXEL2
    else:
        print("Invalid Input, please input a valid Number and try again.")
        endpoint_choice()

    return api_endpoint


parser = argparse.ArgumentParser(
    description="Creates the config file required to run the bot.")

parser.add_argument(
    '-t', '--token',
    type=str,
    help='The Discord Token'
)
parser.add_argument(
    '-p', '--prefix',
    type=str,
    help='The Prefix used by default',
)
parser.add_argument(
    '-a', '--api-endpoint',
    type=str,
    help='The API Endpoint used by the bot',
    choices=["slothpixel", "hypixel", "hypixel2"]
)
parser.add_argument(
    '-k', '--hypixel-key',
    type=str,
    help='The Hypixel API Key, only required when the above is set to hypixel(2)',
)

print("WARNING: Please be sure to run the config in the same folder the .jar is. Otherwise you will have to move the jar or the config")

ENDPOINT_SLOTHPIXEL = "slothpixel"
ENDPOINT_HYPIXEL = "hypixel"
ENDPOINT_HYPIXEL2 = "hypixel2"

args = parser.parse_args()

token = args.token
prefix = args.prefix
api_endpoint = args.api_endpoint
if(api_endpoint == "slothpixel"):
    api_endpoint = ENDPOINT_SLOTHPIXEL
elif(api_endpoint == "hypixel"):
    api_endpoint = ENDPOINT_HYPIXEL
elif(api_endpoint == "hypixel2"):
    api_endpoint = ENDPOINT_HYPIXEL2

hypixel_key = args.hypixel_key

# Token
if(token is None):
    print("No token passed. Please get one from the Discord Developer Portal and paste it here:")
    token = input()
    print("OK")

# Prefix
if(prefix is None):
    print("No prefix passed, to choose the default of \"+\" press ENTER, otherwise type one here:")
    prefix = input()
    if(prefix == ""):
        prefix = "+"
    print("OK")

# Endpoint
if(api_endpoint is None):
    api_endpoint = endpoint_choice()
    print("OK")

# Hypixel Key
if(hypixel_key is not None):
    if(api_endpoint is ENDPOINT_SLOTHPIXEL):
        print("You dont need an API key for the Slothpixel Endpoint")
        hypixel_key = None


if(api_endpoint is not ENDPOINT_SLOTHPIXEL and hypixel_key is None):
    print("No Hypixel API Key passed. Please generate one with /api new in game and paste it here:")
    hypixel_key = input()
    print("OK")

# Remove whitespaces
token = token.replace(" ", "")
prefix = prefix.replace(" ", "")

# ask if choices are correct
print("Here are your choices:\n"
      + "Your Discord Token is the following:\n"
      + token
      + "\nYour Prefix is the following:\n"
      + prefix
      + "\nYour API Endpoint is the following:\n"
      + api_endpoint)
if(api_endpoint is not ENDPOINT_SLOTHPIXEL):
    print("Your Hypixel API Key is the following:\n"
          + hypixel_key)


is_correct()
# Save to file
config_path = "./config/"
config_file_path = config_path + "config.properties"

# Config folder doesn't exist
if(not os.path.isdir(config_path)):
    print("Creating config Folder...")
    os.mkdir(config_path)

# Config folder does exist, check if File exists and create it if not
if(not os.path.isfile(config_file_path)):
    print("creating config file...")
    f = open(config_file_path, "w")
    f.close()

# Config file does exist, check if it contains something
with open(config_file_path) as f:
    config_content_old = f.readlines()

if(len(config_content_old) >= 1 and config_content_old[0] is not None):
    clear_file()

# Write contents to file
config_content_new = (f"token={token}\n"
                      + f"prefix={prefix}\n"
                      + f"apiendpoint={api_endpoint}\n")
if(api_endpoint is not ENDPOINT_SLOTHPIXEL):
    config_content_new = config_content_new + f"hypixelapikey={hypixel_key}"

with open(config_file_path, "r+") as f:
    f.seek(0)
    f.write(config_content_new)
    f.truncate()

print("\nDone.")
