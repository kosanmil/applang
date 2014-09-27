#######################################################################
# Name: applang_generator.py
# Purpose: Textual DSL for generating mobile applications
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from dsl.applang_exceptions import UnimplementedPlatformException
from dsl.applang_model import *
from dsl.applang_parser import parse_from_file, parse_from_str
from generator_android import generate_android
from generator_ios import generate_ios
from generator_wp import generate_wp


def generate_apps_from_file(file_path, debug=False, output_folder="../gen/", overwrite_all=True, eclipse_gen=True):
    """
    Generates the applications from the applang language contained in a file
    :param file_path(str): The path to the file that contains the applang language
        that describes the model of the application
    :param debug(bool): Debug mode
    :param output_folder(str): Folder to output the generated apps
    :return:
    """
    parser = parse_from_file(file_path, debug)
    generate_apps_from_parser(parser, debug, output_folder, overwrite_all, eclipse_gen)


def generate_apps_from_str(language_def, debug=False, output_folder="../gen/", overwrite_all=True, eclipse_gen=True):
    """
    Generates the applications from the language definition passed as a string
    :param language_def(str): The applang language that describes the model of the application
    :param debug(bool): Debug mode
    :param output_folder(str): Folder to output the generated apps
    :return:
    """
    parser = parse_from_str(language_def, debug)
    generate_apps_from_parser(parser, debug, output_folder, overwrite_all, eclipse_gen)


def generate_apps_from_parser(parser, debug=False, output_folder="../gen/", overwrite_all=True, eclipse_gen=True):
    """
    Generates the applications for the platforms specified in the model that is contained in the parser.
    :param parser: The parser which was used to parse the applang language
    :param debug: Debug mode
    :param output_folder: Folder to output the generated apps
    :return:
    """
    print("Generating apps from parser")
    config = parser.config
    platforms = config.platforms
    print("Platforms specified: {}".format(platforms))
    if Platforms.ANDROID in platforms:
        try:
            generate_android(parser, debug, output_folder, overwrite_all, eclipse_gen)
        except UnimplementedPlatformException as e:
            print("WARNING: Android generation is not implemented!");
    if Platforms.IOS in platforms:
        try:
            generate_ios(parser, debug, output_folder, overwrite_all)
        except UnimplementedPlatformException as e:
            print("WARNING: iOS generation is not implemented!");
    if Platforms.WINDOWS_PHONE in platforms:
        try:
            generate_wp(parser, debug, output_folder, overwrite_all)
        except UnimplementedPlatformException as e:
            print("WARNING: Windows Phone generation is not implemented!");
    print("Finished generating applications for the following platforms: {}".format(platforms))


if __name__ == "__main__":
    generate_apps_from_file("example.alang", overwrite_all=True, eclipse_gen=True)