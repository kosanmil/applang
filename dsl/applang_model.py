#######################################################################
# Name: applang_model.py
# Purpose: Model for the applang DSL
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from pprint import pprint


class Config:
    """
    Config model. NOTE: The property names must be the same as the keywords in the parser.
    """
    def __init__(self, app_name=None, namespace=None, platforms=None,
                 android_specifics=None, start_screen=None):
        self.app_name = app_name
        self.namespace = namespace
        self.platforms = platforms
        self.android_specifics = android_specifics
        self.start_screen = start_screen


class Platforms:
    '''
    Enum class for holding the available platforms. N
    Not sure if this is pythonic, will try it out and see.
    '''
    ANDROID = 'android'
    IOS = 'iOS'
    WINDOWS_PHONE = 'windows_phone'


class AndroidSpecifics:

    def __init__(self, min_version=None, max_version=None, target_version=None):
        self.min_version = min_version if min_version is not None else 8
        self.max_version = max_version if max_version is not None else 18
        self.target_version = target_version if target_version is not None else self.max_version



class Entity:

    def __init__(self, name, attributes, label=None):
        self.name = name
        self.attributes = attributes
        self.label = label

    def __repr__(self):
        return str(self.__dict__)


class Attribute:

    def __init__(self, name, type, descriptions=None, many=False):
        self.name = name
        self.type = type
        self.descriptions = descriptions
        self.many = many

    def __repr__(self):
        return str(self.__dict__)


if __name__ == "__main__":
    config = Config()
    print(vars(config).keys())