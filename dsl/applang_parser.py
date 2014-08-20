#######################################################################
# Name: applang_parser.py
# Purpose: Textual DSL for generating mobile applications
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from arpeggio import *
from arpeggio.export import PMDOTExporter, PTDOTExporter
from arpeggio import RegExMatch as _
from jinja2 import Environment, FileSystemLoader
from applang_model import *


def model():            return config, ZeroOrMore([entity]), EOF

#Config parser rules
def config():           return CONFIG_KWD, "{", ZeroOrMore(config_entry), "}"
def config_entry():     return [config_app_name, config_platforms, config_namespace,
                                config_start_screen, config_android_specifics]
def config_app_name():          return CONFIG_APP_NAME_KWD, "=", STRING, ";"
def config_namespace():         return CONFIG_NAMESPACE_KWD, "=", STRING, ";"
def config_start_screen():      return CONFIG_START_SCREEN_KWD, "=", ID, ";"

#Config platforms
def config_platforms():         return CONFIG_PLATFORMS_KWD, "=", platform_available, \
                                        ZeroOrMore(",", platform_available), ";"
def platform_available():       return [platform_android, platform_ios, platform_windows_phone]
def platform_android():         return PLATFORM_ANDROID_KWD
def platform_ios():             return PLATFORM_IOS_KWD
def platform_windows_phone():   return PLATFORM_WINDOWS_PHONE_KWD

#Config android specifics
def config_android_specifics(): return CONFIG_ANDROID_SPECS_KWD, "=", "{", \
                                       ZeroOrMore(android_specifics_entry), "}"
def android_specifics_entry():  return [android_min_version, android_max_version, android_target_version]
def android_min_version():      return ANDROID_MIN_VERSION_KWD, "=", INT, ";"
def android_max_version():      return ANDROID_MAX_VERSION_KWD, "=", INT, ";"
def android_target_version():   return ANDROID_TARGET_VERSION_KWD, "=", INT, ";"

#Entity parser rules
def entity():           return ENTITY_KWD, FirstUpperID, Optional(entity_label), attr_block
def entity_label():     return "(", STRING, ")"
def attr_block():       return "{", ZeroOrMore(attribute), "}"
def attribute():        return Optional(MANY_KWD), ID, ":", attr_type, Optional(descr_block), ";"
def attr_type():        return [PRIM_TYPES_KEYWORDS, FirstUpperID()]


def descr_block():      return "(", descr, ZeroOrMore(",", descr), ")"
def descr():            return [required, unique, transient, toString, excludeFromList, label]
def required():         return REQUIRED_KWD
def unique():           return UNIQUE_KWD
def transient():        return TRANSIENT_KWD
def toString():         return TO_STRING_KWD
def excludeFromList():  return EXCLUDE_FROM_LIST_KWD
def label():            return LABEL_KWD, "=", STRING


#Root Keywords
def CONFIG_KWD():           return "config"
def ENTITY_KWD():           return "entity"

#Config entries keywords
def CONFIG_APP_NAME_KWD():      return "app_name"
def CONFIG_NAMESPACE_KWD():     return "namespace"
def CONFIG_PLATFORMS_KWD():     return "platforms"
def CONFIG_ANDROID_SPECS_KWD(): return "android_specifics"
def CONFIG_START_SCREEN_KWD():  return "start_screen"

#Available platforms keywords
def PLATFORM_ANDROID_KWD():         return "android"
def PLATFORM_IOS_KWD():             return "ios"
def PLATFORM_WINDOWS_PHONE_KWD():   return "windows_phone"

PLATFORMS_KEYWORDS = [PLATFORM_ANDROID_KWD(), PLATFORM_IOS_KWD(), PLATFORM_WINDOWS_PHONE_KWD()]

#Android specifics keywords
def ANDROID_MIN_VERSION_KWD():      return "min_version"
def ANDROID_MAX_VERSION_KWD():      return "max_version"
def ANDROID_TARGET_VERSION_KWD():   return "target_version"

#Attribute descriptions keywords
def MANY_KWD():             return "many"
def REQUIRED_KWD():         return "required"
def UNIQUE_KWD():           return "unique"
def TRANSIENT_KWD():        return "transient"
def TO_STRING_KWD():        return "toString"
def EXCLUDE_FROM_LIST_KWD():return "excludeFromList"
def LABEL_KWD():            return "label"

PARSER_KEYWORDS = [CONFIG_KWD(), CONFIG_APP_NAME_KWD(), CONFIG_NAMESPACE_KWD(), CONFIG_PLATFORMS_KWD(),
                  CONFIG_ANDROID_SPECS_KWD(), CONFIG_START_SCREEN_KWD(),
                  ANDROID_MIN_VERSION_KWD(), ANDROID_MAX_VERSION_KWD(), ANDROID_TARGET_VERSION_KWD(),
                  ENTITY_KWD(), MANY_KWD(), REQUIRED_KWD(), UNIQUE_KWD(),
                  TRANSIENT_KWD(), TO_STRING_KWD(), EXCLUDE_FROM_LIST_KWD(), LABEL_KWD()]

#Primitive types
def INTEGER_KWD():          return "int"
def STRING_KWD():           return "string"
def FLOAT_KWD():            return "float"
def BOOL_KWD():             return "bool"
def DATE_KWD():             return "date"

PRIM_TYPES_KEYWORDS = [INTEGER_KWD(), STRING_KWD(), FLOAT_KWD(), BOOL_KWD(), DATE_KWD()]

ALL_KEYWORDS = PLATFORMS_KEYWORDS + PARSER_KEYWORDS + PRIM_TYPES_KEYWORDS

#Regex
def ID():               return _(r'([a-z]|[A-Z]|_)([a-z]|[A-Z]|_|[0-9])*')
def FirstUpperID():     return _(r'([A-Z])([a-z]|[A-Z]|_|[0-9])*')
def STRING():           return [("'", _(r"((\\')|[^'])*"),"'"),\
                                    ('"', _(r'((\\")|[^"])*'),'"')]
def INT():              return _(r'[-+]?[0-9]+')
def FLOAT():                return _(r'[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?')


def comment():          return [_("//.*"), _("/\*.*\*/")]


# Helper classes
class EntityCrossRef(object):
    """
    Used for entity reference resolving when referenced by attributes in entites.

    Attributes:
        entity_name(str): Name of the target entity.
        attr_name(str): Name of the attribute used for cross-referencing.
        attr_entity_name(str): Name of the entity that contains the attribute used for cross-referencing
        position(int): The position in the input string of this cross-ref.
    """
    def __init__(self, entity_name, attr_name, attr_entity_name, position=0):
        self.entity_name = entity_name
        self.attr_name = attr_name
        self.attr_entity_name = attr_entity_name
        self.position = position



# Semantic actions

class ConfigSem(SemanticAction):

    def first_pass(self, parser, node, children):
        """
        Checks if there are duplicate configuration entries and if this is a duplicate configuration.
        Puts the config model in the parser, and returns the model for the second pass if needed.
        """
        if hasattr(parser, "config") and isinstance(parser.config, Config):
            raise SemanticError("Multiple configs exist at {}!"
                                .format(parser.pos_to_linecol(node.position)))
        children_dict = dict(children)
        if len(children_dict) != len(children):
            raise SemanticError("There are duplicate configuration entries in the configuration at {}!"
                                .format(parser.pos_to_linecol(node.position)))
        config_model = Config()
        #Checking if app_name exists
        if CONFIG_APP_NAME_KWD() not in children_dict:
            raise SemanticError("The field '{}' is required in the configuration at {}!"
                                .format(CONFIG_APP_NAME_KWD(), parser.pos_to_linecol(node.position)))
        config_model.app_name = children_dict.get(CONFIG_APP_NAME_KWD())
        config_model.namespace = children_dict.get(CONFIG_APP_NAME_KWD(), None)
        #Checking if there is at least one platform specified
        if CONFIG_PLATFORMS_KWD() not in children_dict:
            raise SemanticError("At least one platform must be specified in the configuration at {}!"
                                .format(parser.pos_to_linecol(node.position)))
        config_model.platforms = children_dict[CONFIG_PLATFORMS_KWD()]
        #Checking if android specifics exists and the android platform is specified
        if CONFIG_ANDROID_SPECS_KWD() in children_dict:
            if Platforms.ANDROID not in config_model.platforms:
                raise SemanticError("Android specifics cannot be added without adding Android to the platform at {}!"
                                    .format(parser.pos_to_linecol(node.position)))
        config_model.android_specifics = children_dict.get(CONFIG_ANDROID_SPECS_KWD(), AndroidSpecifics())
        config_model.start_screen = children_dict.get(CONFIG_START_SCREEN_KWD(), None)

        parser.config = config_model
        return config_model

config.sem = ConfigSem()


class ConfigAppNameSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return CONFIG_APP_NAME_KWD(), children[0]

config_app_name.sem = ConfigAppNameSem()


class ConfigNamespaceSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return CONFIG_NAMESPACE_KWD(), children[0]

config_namespace.sem = ConfigNamespaceSem()


class ConfigPlatformsSem(SemanticAction):
    """
    Check if there are duplicate platforms specified
    """
    def first_pass(self, parser, node, children):
        if len(children) == 0:
            raise SemanticError("At least one platform must be specified in the configuration at {}!"
                                .format(parser.pos_to_linecol(node.position)))
        if len(children) != len(set(children)):
            raise SemanticError("Duplicate platforms specified at {}!"
                                .format(parser.pos_to_linecol(node.position)))
        return CONFIG_PLATFORMS_KWD(), children

config_platforms.sem = ConfigPlatformsSem()


class PlatformAndroidSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return Platforms.ANDROID

platform_android.sem = PlatformAndroidSem()


class PlatformIOSSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return Platforms.IOS

platform_ios.sem = PlatformIOSSem()


class PlatformWindowsPhoneSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return Platforms.WINDOWS_PHONE

platform_windows_phone.sem = PlatformWindowsPhoneSem()


class ConfigAndroidSpecificsSem(SemanticAction):
    def first_pass(self, parser, node, children):
        """
        Checks if there are duplicate entries in the Android specifics,
        and if version numbers are valid (max >= target => min)
        """
        children_dict = dict(children)
        if len(children_dict) != len(children):
            raise SemanticError("There are duplicate entries in the android specifics at {}!"
                                .format(parser.pos_to_linecol(node.position)))
        android_specs_model = AndroidSpecifics(
                min_version=children_dict.get(ANDROID_MIN_VERSION_KWD(), None),
                max_version=children_dict.get(ANDROID_MAX_VERSION_KWD(), None),
                target_version=children_dict.get(ANDROID_TARGET_VERSION_KWD(), None)
        )
        if android_specs_model.min_version > android_specs_model.max_version:
            raise SemanticError("Android: min version ({}) cannot be greater than the max version ({}) at {}"
                                .format(android_specs_model.min_version, android_specs_model.max_version,
                                        parser.pos_to_linecol(node.position)))
        if android_specs_model.target_version > android_specs_model.max_version:
            raise SemanticError("Android: target version ({}) cannot be greater than the max version ({}) at {}"
                                .format(android_specs_model.target_version, android_specs_model.max_version,
                                        parser.pos_to_linecol(node.position)))
        if android_specs_model.min_version > android_specs_model.target_version:
            raise SemanticError("Android: min version ({}) cannot be greater than the target version ({}) at {}"
                                .format(android_specs_model.min_version, android_specs_model.target_version,
                                        parser.pos_to_linecol(node.position)))
        return CONFIG_ANDROID_SPECS_KWD(), android_specs_model

config_android_specifics.sem = ConfigAndroidSpecificsSem()


class AndroidMinVersionSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return ANDROID_MIN_VERSION_KWD(), int(children[0])

android_min_version.sem = AndroidMinVersionSem()


class AndroidMaxVersionSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return ANDROID_MAX_VERSION_KWD(), int(children[0])

android_max_version.sem = AndroidMaxVersionSem()


class AndroidTargetVersionSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return ANDROID_TARGET_VERSION_KWD(), int(children[0])

android_target_version.sem = AndroidTargetVersionSem()


class ConfigStartScreenSem(SemanticAction):
    def first_pass(self, parser, node, children):
        return CONFIG_START_SCREEN_KWD(), children[0]

config_start_screen.sem = ConfigStartScreenSem();


class EntitySem(SemanticAction):
    """
    Semantic actions for entity. It checks if there are multiple entities with the same name, or
    if there are multiple attributes with the same name inside a single entity.
    Returns the entity model on the first pass
    """
    def first_pass(self, parser, node, children):
        #Check for duplicate entity names
        entity_name = children[0]
        if IsKeyword(entity_name):
            raise SemanticError("Entity name cannot be a keyword '{}' at {}"
                                .format(entity_name,parser.pos_to_linecol(node[1].position)))
        if not hasattr(parser, "entities"):
            parser.entities = []
        if entity_name in [x.name for x in parser.entities]:
            raise SemanticError("Entity '{}' already exists at {}!"
                                .format(entity_name, parser.pos_to_linecol(node[1].position)))
        #Check for duplicate attribute names in the same entity
        attributes = children[-1]
        attribute_names = [x.name for x in attributes]
        if len(attribute_names) != len(set(attribute_names)):
            raise SemanticError("There are duplicate attribute names in the entity '{}' at {}!"
                                .format(entity_name, parser.pos_to_linecol(node[1].position)))

        #Getting the label
        entity_label = None
        labels = [x[1] for x in children if x[0] == 'entity_label']
        if len(labels) > 0:
            entity_label = labels[0]
        if len(labels) > 1:
            #This should never happen!
            raise Exception("There are more than one labels in the entity {}".format(entity_name))

        entity_model = Entity(entity_name, attributes, entity_label)
        #Creating EntityCrossRefs for non-primitive attributes
        for attr in attributes:
            if not IsPrimitiveType(attr.type):
                if not hasattr(parser, "entity_cross_refs"):
                    parser.entity_cross_refs = []
                parser.entity_cross_refs.append(
                    EntityCrossRef(attr.type, attr.name, entity_name, parser.pos_to_linecol(node.position)))


        parser.entities.append(entity_model)
        return entity_model


    def second_pass(self, parser, node):
        entity_model = node
        for attr in entity_model.attributes:
            attr_type = attr.type
            if not IsPrimitiveType(attr_type):
                cross_refs = [x for x in parser.entity_cross_refs
                                     if x.attr_name == attr.name
                                        and x.attr_entity_name == entity_model.name
                                        and x.entity_name == attr_type]
                for cross_ref in cross_refs:
                    if cross_ref.entity_name not in [x.name for x in parser.entities]:
                        raise SemanticError("The type of attribute '{}' type must be primitive or reference an existing entity "
                                            "at {}"
                                            .format(attr.name, cross_ref.position))

        return

entity.sem = EntitySem()


class EntityLabelSem(SemanticAction):
    """
    Semantic action for entity label. Returns the tuple "entity_label" and the lable as a string.
    """
    def first_pass(self, parser, node, children):
        return 'entity_label', children[0]

entity_label.sem = EntityLabelSem()


class AttributeBlockSem(SemanticAction):
    """
    Semantic actions for attribute. It returns all the attribute names of an entity on the first pass
    """
    def first_pass(self, parser, node, children):
        return children

attr_block.sem = AttributeBlockSem()


class AttributeSem(SemanticAction):
    """
    Semantic actions for attribute. It returns the attribute model with the name, type
    and description dictionary
    """
    def first_pass(self, parser, node, children):
        attr_many = children[0] == MANY_KWD()
        if attr_many:
            attr_name = children[1]
            attr_type = children[2]
        else:
            attr_name = children[0]
            attr_type = children[1]
        if IsKeyword(attr_name):
            raise SemanticError("Attribute name cannot be a keyword '{}' at {}"
                                .format(attr_name, parser.pos_to_linecol(node[1].position)))
        attr_model = Attribute(attr_name, attr_type, many=attr_many)
        #Checking if the attribute has descriptions
        if type(children[-1]) is tuple and children[-1][0] == 'DescrBlock':
            attr_model.descriptions = children[-1][1]
        return attr_model

    def second_pass(self, parser, node):
        return

attribute.sem = AttributeSem()


class DescrBlockSem(SemanticAction):
    """
    Semantic actions for description blocks. Returns the descriptions as a dictionary, or raises
    an semantic error if there are duplicate descriptions.
    """
    def first_pass(self, parser, node, children):
        children_dict = dict(children)
        if len(children_dict) != len(children):
            raise SemanticError("There are duplicate descriptions in the attribute at {}!"
                                .format(parser.pos_to_linecol(node[0].position)))
        return 'DescrBlock', children_dict

descr_block.sem = DescrBlockSem()


class RequiredSem(SemanticAction):

    def first_pass(self, parser, node, children):
        return REQUIRED_KWD(), True

required.sem = RequiredSem()


class UniqueSem(SemanticAction):

    def first_pass(self, parser, node, children):
        return UNIQUE_KWD(), True

unique.sem = UniqueSem()


class TransientSem(SemanticAction):

    def first_pass(self, parser, node, children):
        return TRANSIENT_KWD(), True

transient.sem = TransientSem()


class ToStringSem(SemanticAction):

    def first_pass(self, parser, node, children):
        return TO_STRING_KWD(), True

toString.sem = ToStringSem()


class ExcludeFromListSem(SemanticAction):

    def first_pass(self, parser, node, children):
        return EXCLUDE_FROM_LIST_KWD(), True

excludeFromList.sem = ExcludeFromListSem()


class LabelSem(SemanticAction):

    def first_pass(self, parser, node, children):
        return LABEL_KWD(), children[0]

label.sem = LabelSem()


def IsKeyword(inputText):
    return inputText in ALL_KEYWORDS


def IsPrimitiveType(inputType):
    return inputType in PRIM_TYPES_KEYWORDS


def parse_from_str(language_def, debug=False):
    """
    Constructs parser and initializes the model that is put into the parser.

    Args:
        language_def (str): The language in applang.

    Returns:
        The parser with the initialized model.
    """

    if debug:
        print("*** APPLANG PARSER ***")

    # First create parser for TextX descriptions
    parser = ParserPython(model, comment_def=comment, debug=debug)

    PMDOTExporter().exportFile(parser.parser_model,
                               "applang_parse_tree_model.dot")
    # Parse language description with textX parser
    parse_tree = parser.parse(language_def)

    #Invoice semantic analyze
    lang_parser = parser.getASG()

    if debug:
        # Create dot file for debuging purposes
        PTDOTExporter().exportFile(parse_tree, "applang_parse_tree.dot")

    return parser


def parse_from_file(file_name, debug=False):
    """
    Constructs parser and initializes the model that is put into the parser.

    Args:
        language_def (str): The file_name that contains the language in applang.

    Returns:
        The parser with the initialized model.
    """
    with open(file_name, 'r') as f:
        lang_desc = f.read()

    parser = parse_from_str(lang_desc, debug)

    return parser


if __name__ == "__main__":

    parse_from_file("example.alang")
