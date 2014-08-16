#######################################################################
# Name: applang.py
# Purpose: Textual DSL for generating mobile applications
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from arpeggio import *
from arpeggio.export import PMDOTExporter, PTDOTExporter
from arpeggio import RegExMatch as _

import pprint

def model():            return ZeroOrMore(entity), EOF
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


#Attribute Descriptions Keywords
def ENTITY_KWD():           return "entity"
def MANY_KWD():             return "many"
def REQUIRED_KWD():         return "required"
def UNIQUE_KWD():           return "unique"
def TRANSIENT_KWD():        return "transient"
def TO_STRING_KWD():        return "toString"
def EXCLUDE_FROM_LIST_KWD():return "excludeFromList"
def LABEL_KWD():            return "label"

DESCR_KEYWORDS = [ENTITY_KWD(), MANY_KWD(), REQUIRED_KWD(), UNIQUE_KWD(),
            TRANSIENT_KWD(), TO_STRING_KWD(), EXCLUDE_FROM_LIST_KWD(), LABEL_KWD()]

#Primitive types
def INTEGER_KWD():          return "int"
def STRING_KWD():           return "string"
def FLOAT_KWD():            return "float"
def BOOL_KWD():             return "bool"
def DATE_KWD():             return "date"

PRIM_TYPES_KEYWORDS = [INTEGER_KWD(), STRING_KWD(), FLOAT_KWD(), BOOL_KWD(), DATE_KWD()]

KEYWORDS = DESCR_KEYWORDS + PRIM_TYPES_KEYWORDS

#Regex
def ID():               return _(r'([a-z]|[A-Z]|_)([a-z]|[A-Z]|_|[0-9])*')
def FirstUpperID():     return _(r'([A-Z])([a-z]|[A-Z]|_|[0-9])*')
def STRING():           return _(r"""("(?:\\.|[^\\"])*")|('(?:\\.|[^\\'])*')""")


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

class Entity(SemanticAction):
    """
    Semantic actions for entity. It checks if there are multiple entities with the same name, or
    if there are multiple attributes with the same name inside a single entity.
    """
    def first_pass(self, parser, node, children):
        #Check for duplicate entity names
        entity_name = children[0]
        if IsKeyword(entity_name):
            raise SemanticError("Entity name cannot be a keyword '{}' at {}"
                                .format(entity_name,parser.pos_to_linecol(node[1].position)))
        if not hasattr(parser, "entities"):
            parser.entities = []
        if entity_name in [x['name'] for x in parser.entities]:
            raise SemanticError("Entity '{}' already exists at {}!"
                                .format(entity_name, parser.pos_to_linecol(node[1].position)))
        #Check for duplicate attribute names in the same entity
        attributes = children[-1]
        attribute_names = [x['name'] for x in attributes]
        if len(attribute_names) != len(set(attribute_names)):
            raise SemanticError("There are duplicate attribute names in the entity '{}' at {}!"
                                .format(entity_name, parser.pos_to_linecol(node[1].position)))
        entity_model = {}
        entity_model['name'] = entity_name
        entity_model['attributes'] = attributes
        #Creating EntityCrossRefs for non-primitive attributes
        for attr in attributes:
            if not IsPrimitiveType(attr['type']):
                if not hasattr(parser, "entity_cross_refs"):
                    parser.entity_cross_refs = []
                parser.entity_cross_refs.append(
                    EntityCrossRef(attr['type'], attr['name'], entity_name, parser.pos_to_linecol(node.position)))

        #Getting the label
        labels = [x[1] for x in children if x[0] == 'entity_label']
        if len(labels) > 0:
            entity_model['label'] = labels[0]
        if len(labels) > 1:
            #This should never happen!
            raise Exception("There are more than one labels in the entity {}".format(entity_name))

        parser.entities.append(entity_model)
        return entity_model


    def second_pass(self, parser, node):
        entity_model = node
        for attr in entity_model['attributes']:
            attr_type = attr['type']
            if not IsPrimitiveType(attr_type):
                cross_refs = [x for x in parser.entity_cross_refs
                                     if x.attr_name == attr['name']
                                        and x.attr_entity_name == entity_model['name']
                                        and x.entity_name == attr_type]
                for cross_ref in cross_refs:
                    if cross_ref.entity_name not in [x['name'] for x in parser.entities]:
                        raise SemanticError("The type of attribute '{}' type must be primitive or reference an existing entity "
                                            "at {}"
                                            .format(attr['name'], cross_ref.position))

        return

entity.sem = Entity()


class EntityLabel(SemanticAction):
    """
    Semantic action for entity label. Returns the tuple "entity_label" and the lable as a string.
    """
    def first_pass(self, parser, node, children):
        return 'entity_label', children[0]

entity_label.sem = EntityLabel()


class AttributeBlock(SemanticAction):
    """
    Semantic actions for attribute. It returns all the attribute names of an entity on the first pass
    """
    def first_pass(self, parser, node, children):
        return children

attr_block.sem = AttributeBlock()


class Attribute(SemanticAction):
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
        attr_model = {}
        attr_model['many'] = attr_many
        attr_model['name'] = attr_name
        attr_model['type'] = attr_type
        #Checking if the attribute has descriptions
        if type(children[-1]) is tuple and children[-1][0] == 'DescrBlock':
            attr_model['descr_block'] = children[-1][1]
        return attr_model

    def second_pass(self, parser, node):

        return

attribute.sem = Attribute()


class DescrBlock(SemanticAction):
    """
    Semantic actions for description blocks. Returns the descriptions as a dictionary, or raises
    an semantic error if there are duplicate descriptions.
    """
    def first_pass(self, parser, node, children):
        retval = dict(children)
        if len(retval) != len(children):
            raise SemanticError("There are duplicate descriptions in the attribute at {}!"
                                .format(parser.pos_to_linecol(node[0].position)))
        return 'DescrBlock', retval

descr_block.sem = DescrBlock()


class Required(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'required', True

required.sem = Required()


class Unique(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'unique', True

unique.sem = Unique()


class Transient(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'transient', True

transient.sem = Transient()


class ToString(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'toString', True

toString.sem = ToString()


class ExcludeFromList(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'excludeFromList', True

excludeFromList.sem = ExcludeFromList()


class Label(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'label', children[0]

label.sem = Label()


def IsKeyword(inputText):
    return inputText in KEYWORDS

def IsPrimitiveType(inputType):
    return inputType in PRIM_TYPES_KEYWORDS


if __name__ == "__main__":

    input = """
    entity FirstEntity {
        //First entity
        name : string(required, unique, excludeFromList);
        age : int(label='Age of "the" entity', toString);

    }

    entity SecondEntity ("Second Entity") {
        //Second entity
        many attribute1 : FirstEntity;
        attr2 : date(toString, label="Attribute quotes number 'two' .");

    }

    """

    pyParser = ParserPython(model, comment)

    PMDOTExporter().exportFile(pyParser.parser_model,
                               "applang_parse_tree_model.dot")

    parse_tree = pyParser.parse(input)

    # Then we export it to a dot file in order to visualise it.
    # This is also optional.
    PTDOTExporter().exportFile(parse_tree, "applang_parse_tree.dot")

    # getASG will start semantic analysis.
    # In this case semantic analysis will evaluate expression and
    # returned value will be the result of the input_expr expression.
    # print("{} = {}".format(input, parser.getASG()))
    pyParser.getASG()

    #Pretty printing the model
    pp = pprint.PrettyPrinter(indent=4)
    pp.pprint(pyParser.entities)
