#######################################################################
# Name: applang_model.py
# Purpose: Model for the applang DSL
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from applang.oldDSL.consts import DEF_ANDROID_TARGET_VER, DEF_ANDROID_MIN_VER


class Config:
    """
    Config model.
    Properties:
        app_name(str): Name of the application
        namespace(str): Namespace of the application
        platforms(collection(Platform)): Collection of platforms for which to develop
        android_specifics(AndroidSpecifics): Specific settings for the Android platform
        start_screen(str or something): Start_screen of the application.
            Can be either an entity or a menu or a splash screen (not yet implemented)
    """
    def __init__(self, app_name=None, namespace=None, platforms=None,
                 android_specifics=None, start_screen=None):
        self.app_name = app_name
        self.namespace = namespace
        self.platforms = platforms
        self.android_specifics = android_specifics
        self.start_screen = start_screen

    @property
    def full_qname(self):
        """Fully qualified name (namespace.app_name)"""
        return self.namespace + "." + self.app_name


class Platforms:
    ANDROID = 'android'
    IOS = 'iOS'
    WINDOWS_PHONE = 'windows_phone'


class AndroidSpecifics:
    """
    Model for specific settings for the Android platform.
    Properties:
        sdk_path(str): Path to the Android SDK. Used for getting the required libraries from the SDK.
        min_version(int): Minimum API version to be supported.
        max_version(int): Maximum API version to be supported.
        target_version(int): Target API version.
    """
    def __init__(self, sdk_path=None, min_version=None, max_version=None, target_version=None):
        self.min_version = min_version if min_version is not None else DEF_ANDROID_MIN_VER
        self.target_version = target_version if target_version is not None else DEF_ANDROID_TARGET_VER
        self.max_version = max_version
        self.sdk_path = sdk_path
        if type(self.sdk_path) is str:
            if len(self.sdk_path) > 0 and self.sdk_path[-1] != '/':
                self.sdk_path += '/'


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